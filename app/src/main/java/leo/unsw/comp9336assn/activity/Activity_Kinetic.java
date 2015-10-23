package leo.unsw.comp9336assn.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.File;
import java.util.ArrayList;

import leo.unsw.comp9336assn.Constant;
import leo.unsw.comp9336assn.R;
import leo.unsw.comp9336assn.methods.Recorder;
import leo.unsw.comp9336assn.utils.FileManager;
import leo.unsw.comp9336assn.utils.LogManagerEx;
import leo.unsw.comp9336assn.utils.SensorManagerEx;


public class Activity_Kinetic extends Activity implements View.OnClickListener {


    public static final int STOP = 0x0000;
    public static final int START = 0x0001;


    Context context;
    private BroadcastReceiver broadcastReceiver;
    private RadioButton rb_walking;
    private RadioButton rb_jogging;
    private RadioButton rb_stair_up;
    private RadioButton rb_stair_down;
    private GraphView graph;
    private ImageView iv_light;
    private Button btn_start;
    private Button btn_stop;

    private double currentAcc[] = new double[3];
    private boolean isAccStart = false;

    private int state = STOP;
    private SensorManagerEx sensorManagerEx;
    private LineGraphSeries<DataPoint> series;
    private Button btn_show;

    private void init(int layoutId) {
        context = this;
        LogManagerEx.getInstance("COMP9336");
        initLayout(layoutId);
    }

    private void initLayout(int layoutId) {
        setContentView(layoutId);
        rb_walking = (RadioButton) find(R.id.rb_walking);
        rb_jogging = (RadioButton) find(R.id.rb_jogging);
        rb_stair_up = (RadioButton) find(R.id.rb_stair_up);
        rb_stair_down = (RadioButton) find(R.id.rb_stair_down);
        graph = (GraphView) findViewById(R.id.graph);
        iv_light = (ImageView) find(R.id.iv_light);
        btn_start = (Button) find(R.id.btn_start);
        btn_stop = (Button) find(R.id.btn_stop);
        btn_show = (Button) find(R.id.btn_show);
    }


    public void updateLight() {
        switch (state) {
            case STOP:
                iv_light.setImageResource(R.drawable.light_gray);
                break;
            case START:
                iv_light.setImageResource(R.drawable.light_green);
                break;
        }
        final int GREEN = 0x0001;
        final int GRAY = 0x0002;
        final Handler lightHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == GREEN) {
                    iv_light.setImageResource(R.drawable.light_green);
                } else if (msg.what == GRAY) {
                    iv_light.setImageResource(R.drawable.light_gray);
                }
            }
        };
        new Thread() {
            public void run() {
                int i = 0;
                while (state == START) {
                    if (i % 2 == 0) {
                        lightHandler.sendEmptyMessage(GREEN);
                    } else {
                        lightHandler.sendEmptyMessage(GRAY);
                    }
                    try {
                        sleep(700);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    i++;
                }
            }
        }.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(R.layout.activity_kinetic);
    }

    //
    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    private View find(int id) {
        View view = findViewById(id);
        try {
            view.setOnClickListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    private void registerBroadcast() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
            }
        };
        IntentFilter filter_system = new IntentFilter();
        filter_system.addAction(Constant.BROADCAST_ACTION);
        registerReceiver(broadcastReceiver, filter_system);
    }

    private void unRegisterBroadcast() {
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start:
                state = START;
                updateLight();
                startAcc();
                break;
            case R.id.btn_stop:
                state = STOP;
                updateLight();
                stopAcc();
                break;
            case R.id.btn_show:
                showGraph();
        }
    }

    public void startAcc() {
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
                currentAcc[0] = accWithGravity[0];
                currentAcc[1] = accWithGravity[1];
                currentAcc[2] = accWithGravity[2];
            }

            @Override
            public void onGyroscopeChanged(float[] gyroscopeValues, float[] gyroscopeAngle) {

            }
        };
        ArrayList<Integer> sensorList = new ArrayList<>();
        sensorList.add(Sensor.TYPE_ACCELEROMETER);
        sensorManagerEx.register(sensorList, SensorManager.SENSOR_DELAY_UI, false);
        isAccStart = true;
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0:
                        graph.removeAllSeries();
                        graph.addSeries(series);
                        break;
                    default:
                        double value = Math.sqrt(Math.abs(currentAcc[0]) + Math.abs(currentAcc[1]) + Math.abs(currentAcc[2]));
                        save(msg.what);
                        DataPoint point = new DataPoint(msg.what, value);
                        series.appendData(point, false, 500);
                        break;
                }
            }
        };
        new Thread() {
            public void run() {
                series = new LineGraphSeries<DataPoint>();
                handler.sendEmptyMessage(0);
                delete();
                int i = 0;
                while (isAccStart) {
                    try {
                        i++;
                        handler.sendEmptyMessage(i);
                        sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();

    }

    public void stopAcc() {
        if (sensorManagerEx != null) {
            isAccStart = false;
            sensorManagerEx.unregister();
        }
    }

    public void save(int time) {
        if (rb_jogging.isChecked()) {
            Recorder.appendKinetic("jogging", currentAcc, time);
        } else if (rb_walking.isChecked()) {
            Recorder.appendKinetic("walking", currentAcc, time);
        } else if (rb_stair_down.isChecked()) {
            Recorder.appendKinetic("stair_down", currentAcc, time);
        } else if (rb_stair_up.isChecked()) {
            Recorder.appendKinetic("stair_up", currentAcc, time);
        }
    }

    public void delete() {
        File tmpFile = null;
        if (rb_jogging.isChecked()) {
            tmpFile = new File(Constant.PATH + "jogging");
        } else if (rb_walking.isChecked()) {
            tmpFile = new File(Constant.PATH + "walking");
        } else if (rb_stair_down.isChecked()) {
            tmpFile = new File(Constant.PATH + "stair_down");
        } else if (rb_stair_up.isChecked()) {
            tmpFile = new File(Constant.PATH + "stair_up");
        }
        if (tmpFile != null && tmpFile.exists()) {
            tmpFile.delete();
        }
    }

    public void showGraph() {
        graph.removeAllSeries();
        series = new LineGraphSeries<DataPoint>();
        String string = null;
        if (rb_jogging.isChecked()) {
            string = FileManager.readInStream(Constant.PATH + "jogging");
        } else if (rb_walking.isChecked()) {
            string = FileManager.readInStream(Constant.PATH + "walking");
        } else if (rb_stair_down.isChecked()) {
            string = FileManager.readInStream(Constant.PATH + "stair_down");
        } else if (rb_stair_up.isChecked()) {
            string = FileManager.readInStream(Constant.PATH + "stair_up");
        }
        if(string == null){
            return;
        }
        String strs[] = string.split("\n");
        for (int i = 1; i < strs.length; i++) {
            String info[] = strs[i].split(",");
            try{
                float time = Float.parseFloat(info[0]);
                double acc[] = new double[3];
                acc[0] = Double.parseDouble(info[1]);
                acc[1] = Double.parseDouble(info[2]);
                acc[2] = Double.parseDouble(info[3]);
                double value = Math.sqrt(Math.abs(acc[0]) + Math.abs(acc[1]) + Math.abs(acc[2]));
                DataPoint point = new DataPoint(time, value);
                series.appendData(point, false, 5000);
            }catch(Exception e){
                e.printStackTrace();
            }

        }
        graph.addSeries(series);

    }
}

