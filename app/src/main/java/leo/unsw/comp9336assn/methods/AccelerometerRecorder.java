package leo.unsw.comp9336assn.methods;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;

import java.util.ArrayList;

import leo.unsw.comp9336assn.utils.SensorManagerEx;

/**
 * Created by LeoPC on 2015/10/10.
 */
public class AccelerometerRecorder {
    Context context;
    private SensorManagerEx sensorManagerEx = null;

    public AccelerometerRecorder(Context context){
        this.context = context;
    }
    public void start(int delay){
        if(sensorManagerEx != null){
            sensorManagerEx.unregister();
        }
        sensorManagerEx = new SensorManagerEx(context) {
            @Override
            public void onSensorChanged(SensorEvent event) {
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }

            @Override
            public void onMagneticFieldChanged(float[] magneticFieldValues, float[] angle) {

            }

            @Override
            public void onAccelerometerChanged(float[] accWithGravity, float[] accWithoutGravity) {

            }

            @Override
            public void onGyroscopeChanged(float[] gyroscopeValues, float[] gyroscopeAngle) {

            }
        };
        ArrayList<Integer> sensorList = new ArrayList<>();
        sensorList.add(Sensor.TYPE_ACCELEROMETER);
        sensorManagerEx.register(sensorList, delay, false);
    }

    public void stop(){
        if(sensorManagerEx != null){
            sensorManagerEx.unregister();
        }
    }
}
