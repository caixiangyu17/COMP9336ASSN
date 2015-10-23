package leo.unsw.comp9336assn.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;

import leo.unsw.comp9336assn.Constant;
import leo.unsw.comp9336assn.R;
import leo.unsw.comp9336assn.methods.ServiceStarter;
import leo.unsw.comp9336assn.utils.LogManagerEx;


public class Activity_A_Sample extends Activity implements View.OnClickListener{
    Context context;
    private BroadcastReceiver broadcastReceiver;

    private void init(int layoutId) {
        context = this;
        LogManagerEx.getInstance("COMP9336");
        initLayout(layoutId);

        ServiceStarter.start(context, ServiceStarter.INIT);
    }

    private void initLayout(int layoutId) {
        setContentView(layoutId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(R.layout.activity_main);
    }

    //
    @Override
    protected void onStart() {
        super.onStart();
        registBroadcast();
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
        unRegistBroadcast();
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

    private void registBroadcast(){
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                LogManagerEx.getInstance().show("oooooooooooooooooooooooooooooooo");
            }
        };
        IntentFilter filter_system = new IntentFilter();
        filter_system.addAction(Constant.BROADCAST_ACTION);
        registerReceiver(broadcastReceiver, filter_system);
    }

    private void unRegistBroadcast(){
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

        }
    }
}
