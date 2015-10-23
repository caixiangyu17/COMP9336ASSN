package leo.unsw.comp9336assn.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import leo.unsw.comp9336assn.Constant;
import leo.unsw.comp9336assn.R;
import leo.unsw.comp9336assn.methods.ServiceStarter;
import leo.unsw.comp9336assn.service.Service_Main;
import leo.unsw.comp9336assn.utils.BatteryManagerEx;
import leo.unsw.comp9336assn.utils.LogManagerEx;
import leo.unsw.comp9336assn.utils.ViewFactory;


public class Activity_Main extends Activity implements View.OnClickListener{
    Context context;
    private ViewFactory vf;
    private View iv_battery;
    private View iv_kinetic;
    private View iv_wifi;
    private BroadcastReceiver broadcastReceiver;
    private TextView tv_currentState;

    private void init(int layoutId) {
        context = this;
        LogManagerEx.getInstance("COMP9336");
        initLayout(layoutId);
        ServiceStarter.start(context, ServiceStarter.INIT);
        BatteryManagerEx batteryManagerEx = new BatteryManagerEx(context) {
            @Override
            public void onPowerConnected(Context context, Intent intent) {

            }

            @Override
            public void onPowerDisconnected(Context context, Intent intent) {

            }

            @Override
            public void onBatteryChanged(Context context, Intent intent) {

            }
        };
        batteryManagerEx.register();
        batteryManagerEx.getBatteryPercent();
        batteryManagerEx.unregister();
    }

    private void initLayout(int layoutId) {
        setContentView(layoutId);
        vf = new ViewFactory((Activity)context);
        iv_battery = find(R.id.iv_battery);
        iv_kinetic = find(R.id.iv_kinetic);
        tv_currentState = (TextView)find(R.id.tv_currentState);
        iv_wifi = find(R.id.iv_wifi);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(R.layout.activity_main);
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
        super.onStop();
        unRegisterBroadcast();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    private View find(int id){
        View view = findViewById(id);
        view.setOnClickListener(this);
        return view;
    }

    private void registerBroadcast(){
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int state = ServiceStarter.getState(intent);
                switch (state){
                    case Service_Main.IDLE:
                        tv_currentState.setText("IDLE");
                        break;
                    case Service_Main.ACCELEROMETER_L_START:
                        tv_currentState.setText("LOW RATE ACCELEROMETER IS RUNNING");
                        break;
                    case Service_Main.ACCELEROMETER_M_START:
                        tv_currentState.setText("MID RATE ACCELEROMETER IS RUNNING");
                        break;
                    case Service_Main.ACCELEROMETER_H_START:
                        tv_currentState.setText("HIGH RATE ACCELEROMETER IS RUNNING");
                        break;
                    case Service_Main.GPS:
                        tv_currentState.setText("GPS IS RUNNING");
                        break;
                    case Service_Main.NFC:
                        tv_currentState.setText("NFC IS RUNNING");
                        break;
                    case Service_Main.WIFI_HOTSPOT:
                        tv_currentState.setText("HOTSPOT IS RUNNING");
                        break;
                    case Service_Main.BLUETOOTH:
                        tv_currentState.setText("BLUETOOTH IS RUNNING");
                        break;
                }
            }
        };
        IntentFilter filter_system = new IntentFilter();
        filter_system.addAction(Constant.BROADCAST_ACTION);
        registerReceiver(broadcastReceiver, filter_system);
    }

    private void unRegisterBroadcast(){
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_battery:
                startActivity(new Intent(context, Activity_Consumption.class));
                break;
            case R.id.iv_kinetic:
                startActivity(new Intent(context, Activity_Kinetic.class));
                break;
            case R.id.iv_wifi:
                startActivity(new Intent(context, Activity_Wifi.class));
                break;
        }
    }

}
