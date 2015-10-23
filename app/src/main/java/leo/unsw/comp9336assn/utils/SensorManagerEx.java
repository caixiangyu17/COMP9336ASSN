package leo.unsw.comp9336assn.utils;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by LeoPC on 2015/9/1.
 */
abstract public class SensorManagerEx {
    private SensorEventListener sensorEventListener;
    private SensorManager mSensorManager;
    private Sensor accelerometerSensor;
    private Sensor magnetometer;
    private List<Sensor> sensorList;
    Context context;
    private float[] magneticFieldValues = new float[3];
    private float[] accelerometerValues = new float[3];
    private float x_gravity;
    private float y_gravity;
    private float z_gravity;
    private LogManagerEx logManagerEx;
    private Sensor gyroscopeSensor;
    private static final float NS2S = 1.0f / 1000000000.0f;
    private long timestamp;
    private float[] gyroscopeAngle = new float[3];


    public SensorManagerEx(Context context) {
        this.context = context;
        init();
    }

    private void init() {
        logManagerEx = LogManagerEx.getInstance();
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        accelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        gyroscopeSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensorList = mSensorManager.getSensorList(Sensor.TYPE_ALL);
        sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                SensorManagerEx.this.onSensorChanged(event);
                if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                    magneticFieldValues = event.values;
                    float[] R = new float[9];
                    float[] radians = new float[3];
                    SensorManager.getRotationMatrix(R, null, accelerometerValues, magneticFieldValues);
                    SensorManager.getOrientation(R, radians);
                    SensorManagerEx.this.onMagneticFieldChanged(magneticFieldValues, radians);
                }
                if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                    accelerometerValues = event.values;
                    NumberFormat formatter = new DecimalFormat("#0.0000000000000000");
                    float alpha = 0.8f;
                    x_gravity = alpha * x_gravity + (1 - alpha) * accelerometerValues[0];
                    y_gravity = alpha * y_gravity + (1 - alpha) * accelerometerValues[1];
                    z_gravity = alpha * z_gravity + (1 - alpha) * accelerometerValues[2];
                    float linearAcceleration[] = new float[3];
                    linearAcceleration[0] = accelerometerValues[0] - x_gravity;
                    linearAcceleration[1] = accelerometerValues[1] - y_gravity;
                    linearAcceleration[2] = accelerometerValues[2] - z_gravity;
                    SensorManagerEx.this.onAccelerometerChanged(accelerometerValues, linearAcceleration);
                }
                if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
                    //从 x、y、z 轴的正向位置观看处于原始方位的设备，如果设备逆时针旋转，将会收到正值；否则，为负值
                    if (timestamp != 0) {
                        // 得到两次检测到手机旋转的时间差（纳秒），并将其转化为秒
                        final float dT = (event.timestamp - timestamp) * NS2S;
                        // 将手机在各个轴上的旋转角度相加，即可得到当前位置相对于初始位置的旋转弧度
                        gyroscopeAngle[0] += event.values[0] * dT;
                        gyroscopeAngle[1] += event.values[1] * dT;
                        gyroscopeAngle[2] += event.values[2] * dT;

                        SensorManagerEx.this.onGyroscopeChanged(event.values, gyroscopeAngle);

                    }
                    //将当前时间赋值给timestamp
                    timestamp = event.timestamp;

                }


            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                SensorManagerEx.this.onAccuracyChanged(sensor, accuracy);
            }
        };
    }


    public void resetGyroscopeAngle(){
        gyroscopeAngle = new float[3];
    }

    public List<Sensor> getSensorList() {
        return mSensorManager.getSensorList(Sensor.TYPE_ALL);
    }

    public List<Sensor> getSensorList(int type) {
        return mSensorManager.getSensorList(type);
    }

    public void register(ArrayList<Integer> sensors, int delay, boolean isAppendSensors) {
        if (!isAppendSensors) {
            mSensorManager.unregisterListener(sensorEventListener);
        }
        if (sensors == null) {
            return;
        }
        for (int sensor : sensors) {
            switch (sensor) {
                case Sensor.TYPE_ACCELEROMETER:
                    mSensorManager.registerListener(sensorEventListener, accelerometerSensor, delay);
                    break;
                case Sensor.TYPE_MAGNETIC_FIELD:
                    mSensorManager.registerListener(sensorEventListener, magnetometer, delay);
                    mSensorManager.registerListener(sensorEventListener, accelerometerSensor, delay);
                    break;
                case Sensor.TYPE_GYROSCOPE:
                    mSensorManager.registerListener(sensorEventListener, gyroscopeSensor, delay);
                    break;
            }
        }
    }

    public void register(ArrayList<Integer> sensors, boolean isAppendSensors) {
        register(sensors, SensorManager.SENSOR_DELAY_NORMAL, isAppendSensors);
    }

    public void unregister() {
        LogManagerEx.getInstance().show("unregister");
        mSensorManager.unregisterListener(sensorEventListener);
    }

    public float[] toDegrees(float[] angles) {
        float[] result = new float[3];
        result[0] = (float) Math.toDegrees(angles[0]);
        result[0] = (float) Math.toDegrees(angles[0]);
        result[0] = (float) Math.toDegrees(angles[0]);
        return result;
    }


    public abstract void onSensorChanged(SensorEvent event);

    public abstract void onAccuracyChanged(Sensor sensor, int accuracy);

    /**
     * @param magneticFieldValues
     * @param angle               radians
     */
    public abstract void onMagneticFieldChanged(float[] magneticFieldValues, float[] angle);

    public abstract void onAccelerometerChanged(float[] accWithGravity, float[] accWithoutGravity);

    public abstract void onGyroscopeChanged(float[] gyroscopeValues, float[] gyroscopeAngle);

}
