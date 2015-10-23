package leo.unsw.comp9336assn.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import leo.unsw.comp9336assn.Constant;
import leo.unsw.comp9336assn.R;
import leo.unsw.comp9336assn.methods.ServiceStarter;
import leo.unsw.comp9336assn.service.Service_Main;
import leo.unsw.comp9336assn.utils.FileManager;
import leo.unsw.comp9336assn.utils.LogManagerEx;


public class Activity_Consumption extends Activity implements View.OnClickListener {

    public static final int STOP = 0x0000;
    public static final int START = 0x0001;


    Context context;
    private ImageView iv_light;
    private Button btn_start;
    private Button btn_stop;

    private int state = STOP;
    private BroadcastReceiver broadcastReceiver;
    private GraphView graph;
    private RadioButton rb_low;
    private RadioButton rb_mid;
    private RadioButton rb_high;
    private RadioButton rb_gps;
    private RadioButton rb_bluetooth;
    private RadioButton rb_nfc;
    private RadioButton rb_hotspot;
    private Spinner sp_show;

    private void init(int layoutId) {
        context = this;
        LogManagerEx.getInstance("COMP9336");
        initLayout(layoutId);
        registerBroadcast();
    }

    private void initLayout(int layoutId) {
        setContentView(layoutId);
        graph = (GraphView) findViewById(R.id.graph);
        iv_light = (ImageView) find(R.id.iv_light);
        btn_start = (Button) find(R.id.btn_start);
        btn_stop = (Button) find(R.id.btn_stop);
        rb_low = (RadioButton) find(R.id.rb_low);
        rb_mid = (RadioButton) find(R.id.rb_mid);
        rb_high = (RadioButton) find(R.id.rb_high);
        rb_gps = (RadioButton) find(R.id.rb_gps);
        rb_bluetooth = (RadioButton) find(R.id.rb_bluetooth);
        rb_nfc = (RadioButton) find(R.id.rb_nfc);
        rb_hotspot = (RadioButton) find(R.id.rb_hotspot);
        sp_show = (Spinner) find(R.id.sp_show);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.planets_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_show.setAdapter(adapter);
        sp_show.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                getGraph(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private void getGraph(int showType) {
        String str_l = FileManager.readInStream(Constant.PATH + "accelerometer_l");
        String str_m = FileManager.readInStream(Constant.PATH + "accelerometer_m");
        String str_h = FileManager.readInStream(Constant.PATH + "accelerometer_h");
        String str_gps_h = FileManager.readInStream(Constant.PATH + "gps_h");
        String str_gps_l = FileManager.readInStream(Constant.PATH + "gps_l");
        String str_wifi = FileManager.readInStream(Constant.PATH + "wifi");
        String str_bluetooth = FileManager.readInStream(Constant.PATH + "bluetooth");
        String str_wifi_hotspot = FileManager.readInStream(Constant.PATH + "wifi_hotspot");
//        str_l = "55,54,53,51,50,47,46";
//        str_m = "65,63,61,57,54,42";
//        str_h = "30,25,22,18,15,13,10";
        LineGraphSeries<DataPoint> series_l = getSeries(str_l);
        LineGraphSeries<DataPoint> series_m = getSeries(str_m);
        LineGraphSeries<DataPoint> series_h = getSeries(str_h);
        LineGraphSeries<DataPoint> series_gps_h = getSeries(str_gps_h);
        LineGraphSeries<DataPoint> series_gps_l = getSeries(str_gps_l);
        LineGraphSeries<DataPoint> series_wifi = getSeries(str_wifi);
        LineGraphSeries<DataPoint> series_bluetooth = getSeries(str_bluetooth);
        LineGraphSeries<DataPoint> series_wifi_hotspot = getSeries(str_wifi_hotspot);


        series_l.setColor(color("#FF0066"));
        series_m.setColor(color("#6600CC"));
        series_h.setColor(color("#6B8E23"));
        series_gps_h.setColor(color("#7EC0EE"));
        series_gps_l.setColor(color("#EEC900"));
        series_wifi.setColor(color("#191970"));
        series_bluetooth.setColor(color("#CD6600"));
        series_wifi_hotspot.setColor(color("#2B2B2B"));
        graph.removeAllSeries();
        if(showType == 0){
            if (!series_l.isEmpty()) {
                graph.addSeries(series_l);
            }
            if (!series_m.isEmpty()) {
                graph.addSeries(series_m);
            }
            if (!series_h.isEmpty()) {
                graph.addSeries(series_h);
            }
        }else if(showType == 1){
            if (!series_gps_l.isEmpty()) {
                graph.addSeries(series_gps_l);
            }
            if (!series_gps_h.isEmpty()) {
                graph.addSeries(series_gps_h);
            }
        }else if(showType == 2){
            if (!series_wifi.isEmpty()) {
                graph.addSeries(series_wifi);
            }
            if (!series_bluetooth.isEmpty()) {
                graph.addSeries(series_bluetooth);
            }
            if (!series_wifi_hotspot.isEmpty()) {
                graph.addSeries(series_wifi_hotspot);
            }
        }

    }

    public int color(String str) {
        str = str.replace("#","");
        StringBuffer red = new StringBuffer();
        red.append(str.charAt(0));
        red.append(str.charAt(1));
        StringBuffer green = new StringBuffer();
        green.append(str.charAt(2));
        green.append(str.charAt(3));
        StringBuffer blue = new StringBuffer();
        blue.append(str.charAt(4));
        blue.append(str.charAt(5));
        return Color.rgb(hexString2Int(red.toString()), hexString2Int(green.toString()), hexString2Int(blue.toString()));
    }
    public int hexString2Int(String str){
        int result = 0;
        for(int i = 0 ; i < str.length(); i++){
            if( str.charAt(i) >= '0' && str.charAt(i) <= '9'){
                result = result*16 + str.charAt(i) - '0';
            }else if(str.charAt(i) == 'A' || str.charAt(i) == 'a'){
                result = result*16 + 10;
            }else if(str.charAt(i) == 'B' || str.charAt(i) == 'b'){
                result = result*16 + 11;
            }else if(str.charAt(i) == 'C' || str.charAt(i) == 'c'){
                result = result*16 + 12;
            }else if(str.charAt(i) == 'D' || str.charAt(i) == 'd'){
                result = result*16 + 13;
            }else if(str.charAt(i) == 'E' || str.charAt(i) == 'e'){
                result = result*16 + 14;
            }else if(str.charAt(i) == 'F' || str.charAt(i) == 'f'){
                result = result*16 + 15;
            }
        }
        return result;
    }

    public LineGraphSeries<DataPoint> getSeries(String str) {
        LineGraphSeries<DataPoint> series = null;
        if (str != null) {
            String[] strs = str.split(",");
            DataPoint points[] = new DataPoint[strs.length];
            for (int i = 0; i < strs.length; i++) {
                points[i] = new DataPoint(i, Float.parseFloat(strs[i]));
            }
            series = new LineGraphSeries<DataPoint>(points);
        } else {
            series = new LineGraphSeries<DataPoint>();
        }
        return series;
    }

    public void updateLight() {
        switch (state) {
            case STOP:
                iv_light.setImageResource(R.drawable.light_gray);
                if (rb_low.isChecked()) {
                    ServiceStarter.start(context, ServiceStarter.ACCELEROMETER_L_STOP);
                } else if (rb_mid.isChecked()) {
                    ServiceStarter.start(context, ServiceStarter.ACCELEROMETER_M_STOP);
                } else if (rb_high.isChecked()) {
                    ServiceStarter.start(context, ServiceStarter.ACCELEROMETER_H_STOP);
                } else if (rb_gps.isChecked()) {
                    ServiceStarter.start(context, ServiceStarter.GPS_STOP);
                } else if (rb_nfc.isChecked()) {
                    ServiceStarter.start(context, ServiceStarter.NFC_STOP);
                } else if (rb_bluetooth.isChecked()) {
                    ServiceStarter.start(context, ServiceStarter.BLUETOOTH_STOP);
                } else if (rb_hotspot.isChecked()) {
                    ServiceStarter.start(context, ServiceStarter.WIFI_HOTSPOT_STOP);
                }
                break;
            case START:
                iv_light.setImageResource(R.drawable.light_green);
                if (rb_low.isChecked()) {
                    ServiceStarter.start(context, ServiceStarter.ACCELEROMETER_L_START);
                } else if (rb_mid.isChecked()) {
                    ServiceStarter.start(context, ServiceStarter.ACCELEROMETER_M_START);
                } else if (rb_high.isChecked()) {
                    ServiceStarter.start(context, ServiceStarter.ACCELEROMETER_H_START);
                } else if (rb_gps.isChecked()) {
                    ServiceStarter.start(context, ServiceStarter.GPS_START);
                } else if (rb_nfc.isChecked()) {
                    ServiceStarter.start(context, ServiceStarter.NFC_START);
                } else if (rb_bluetooth.isChecked()) {
                    ServiceStarter.start(context, ServiceStarter.BLUETOOTH_START);
                } else if (rb_hotspot.isChecked()) {
                    ServiceStarter.start(context, ServiceStarter.WIFI_HOTSPOT_START);
                }
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
        init(R.layout.activity_consumption);
    }

    //
    @Override
    protected void onStart() {
        registerBroadcast();
        ServiceStarter.start(context, ServiceStarter.REQUEST_STATE);
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
        unRegisterBroadcast();
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

        }
        return view;
    }

    private void registerBroadcast() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int currentState = ServiceStarter.getState(intent);
                switch (currentState) {
                    case Service_Main.IDLE:
                        state = STOP;
                        updateLight();
                        break;
                    case Service_Main.ACCELEROMETER_L_START:
                        state = START;
                        rb_low.setChecked(true);
                        updateLight();
                        break;
                    case Service_Main.ACCELEROMETER_M_START:
                        state = START;
                        rb_mid.setChecked(true);
                        updateLight();
                        break;
                    case Service_Main.ACCELEROMETER_H_START:
                        rb_high.setChecked(true);
                        state = START;
                        updateLight();
                        break;
                    case Service_Main.GPS:
                        state = START;
                        rb_gps.setChecked(true);
                        updateLight();
                        break;
                    case Service_Main.NFC:
                        state = START;
                        rb_nfc.setChecked(true);
                        updateLight();
                        break;
                    case Service_Main.BLUETOOTH:
                        rb_bluetooth.setChecked(true);
                        state = START;
                        updateLight();
                        break;
                    case Service_Main.WIFI_HOTSPOT:
                        rb_hotspot.setChecked(true);
                        state = START;
                        updateLight();
                        break;
                }
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
                break;
            case R.id.btn_stop:
                state = STOP;
                updateLight();
                break;
        }
    }
}
