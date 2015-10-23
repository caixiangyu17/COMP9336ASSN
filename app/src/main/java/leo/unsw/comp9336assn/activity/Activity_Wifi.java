package leo.unsw.comp9336assn.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import leo.unsw.comp9336assn.R;
import leo.unsw.comp9336assn.methods.ApsList;
import leo.unsw.comp9336assn.methods.SeriesManager;
import leo.unsw.comp9336assn.methods.ServiceStarter;
import leo.unsw.comp9336assn.methods.ShowAp;
import leo.unsw.comp9336assn.utils.LocationManagerEx;
import leo.unsw.comp9336assn.utils.LogManagerEx;
import leo.unsw.comp9336assn.utils.WifiManagerEx;


public class Activity_Wifi extends Activity implements View.OnClickListener {
    Context context;
    private BroadcastReceiver broadcastReceiver;
    private CheckBox cb_filter;
    private EditText et_input;
    private Button btn_search;
    private ListView lv_wifiList;
    private TextView tv_wifiResult;
    private GraphView graph;
    private TextView tv_position;
    private TextView tv_total;

    private void init(int layoutId) {
        context = this;
        LogManagerEx.getInstance("COMP9336");
        initLayout(layoutId);

        ServiceStarter.start(context, ServiceStarter.INIT);
    }

    private void initLayout(int layoutId) {
        setContentView(layoutId);
        cb_filter = (CheckBox) find(R.id.cb_filter);
        et_input = (EditText) find(R.id.et_input);
        btn_search = (Button) find(R.id.btn_search);
        lv_wifiList = (ListView) find(R.id.lv_wifiList);
        tv_position = (TextView)find(R.id.tv_position);
        tv_total = (TextView) find(R.id.tv_total);
        final LocationManagerEx locationManagerEx = new LocationManagerEx(context) {
            @Override
            public void onLocationChanged(Location location) {

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        locationManagerEx.isGPSEnabled();
        locationManagerEx.isNetworkEnabled();
        locationManagerEx.requestLocationUpdates();
//        tv_wifiResult = (TextView) find(R.id.tv_wifiResult);
        graph = (GraphView) find(R.id.graph);
        et_input.clearFocus();


        cb_filter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    et_input.setEnabled(true);
                    et_input.setBackgroundResource(android.R.drawable.editbox_dropdown_light_frame);
                } else {
                    et_input.setEnabled(false);
                    et_input.setBackgroundResource(android.R.drawable.editbox_dropdown_dark_frame);
                }
            }
        });
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DecimalFormat df = new DecimalFormat("0.000000");
                StringBuffer sb = new StringBuffer();
                sb.append(df.format(locationManagerEx.getLocation().getLatitude()));
                sb.append("\n");
                sb.append(df.format(locationManagerEx.getLocation().getLongitude()));
                tv_position.setText(sb.toString());
                Toast.makeText(context, "Update by " + locationManagerEx.getLocation().getProvider(), Toast.LENGTH_SHORT).show();
                WifiManagerEx wifiManagerEx = new WifiManagerEx(context) {
                    @Override
                    public void onWifiEnabling() {

                    }

                    @Override
                    public void onWifiEnabled() {

                    }

                    @Override
                    public void onWifiDisabling() {

                    }

                    @Override
                    public void onWifiDisabled() {

                    }

                    @Override
                    public void onNetworkConnecting(String ssid) {

                    }

                    @Override
                    public void onNetworkConnected(String ssid) {

                    }

                    @Override
                    public void onNetworkDisconnecting() {

                    }

                    @Override
                    public void onNetworkDisconnected() {

                    }

                    @Override
                    public void onNetworkSuspened() {

                    }

                    @Override
                    public void onNetworkUnknown() {

                    }
                };
                wifiManagerEx.registerReceiver();
                List<ScanResult> aps = wifiManagerEx.getScanResults();
                updateView(aps);
                wifiManagerEx.unRegisterReceiver();
            }
        });
    }

    private void updateView(List<ScanResult> aps) {
        SeriesManager seriesManager = new SeriesManager();
        ArrayList<String> list = new ArrayList<>();
        StringBuffer result = new StringBuffer();
        HashMap<String, Integer> dict = new HashMap<>();
        double totalPower = 0;
        int count = 0;

        HashMap<String, ShowAp> map = new HashMap<>();
        for (ScanResult ap : aps) {
            if (ap.SSID.trim().equals("")) {
                continue;
            }
            if (cb_filter.isChecked()) {
                if (!ap.SSID.equals(et_input.getText().toString())) {
                    continue;
                }
            }
            ShowAp showAp = new ShowAp();
            if (map.containsKey(ap.SSID)) {
                showAp = map.get(ap.SSID);
            } else {
                showAp.ssid = ap.SSID;
            }
            double power = Math.pow(10, ap.level / 10) * 1000000;
            totalPower += power;
            count++;
            showAp.power += power;
            showAp.count++;
            showAp.color = Color.rgb(randomColor(), randomColor(), randomColor());
            map.put(ap.SSID, showAp);
            seriesManager.push(ap.SSID, ap.level, showAp.color);
        }

        result.append("Number of wireless AP : " + count + "\n");
        result.append("Total power : " + totalPower + "\n");
        ApsList apsList = new ApsList(context, map);
        graph.removeAllSeries();
        Iterator itr = seriesManager.getSeriesDict().entrySet().iterator();
        if (seriesManager.getSeriesDict().size() > 1) {
            while (itr.hasNext()) {
                HashMap.Entry entry = (HashMap.Entry) itr.next();
                SeriesManager.MySeries series = (SeriesManager.MySeries) entry.getValue();
                graph.addSeries(series.series);
            }
        } else {
            graph.addSeries(seriesManager.getAllPointsSeries());
        }
        lv_wifiList.setAdapter(apsList.normalModeAdapter);
        DecimalFormat df = new DecimalFormat("0.00");
        tv_total.setText("Total power is "+df.format(totalPower)+"nW");
    }

    public int randomColor() {
        return (int) (Math.random() * 100000 % 256);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(R.layout.activity_wifi);
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
            e.toString();
        }
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }

}
