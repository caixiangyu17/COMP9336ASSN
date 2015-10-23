package leo.unsw.comp9336assn.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

/**
 * Created by LeoPC on 2015/9/7.
 */
abstract public class BatteryManagerEx {

    private LogManagerEx logManagerEx;
    private BroadcastReceiver receiver;
    Context context;
    private Intent batteryStatus;
    private boolean usbCharge;
    private boolean acCharge;
    private boolean wirelessCharge;


    public static final int BATTERY_PLUGGED_AC = BatteryManager.BATTERY_PLUGGED_AC;
    public static final int BATTERY_PLUGGED_USB = BatteryManager.BATTERY_PLUGGED_USB;
    public static final int BATTERY_PLUGGED_WIRELESS = BatteryManager.BATTERY_PLUGGED_WIRELESS;


    public static final int BATTERY_STATUS_CHARGING = BatteryManager.BATTERY_STATUS_CHARGING;
    public static final int BATTERY_STATUS_DISCHARGING = BatteryManager.BATTERY_STATUS_DISCHARGING;
    public static final int BATTERY_STATUS_NOT_CHARGING = BatteryManager.BATTERY_STATUS_NOT_CHARGING;

    public BatteryManagerEx(Context context) {
        this.context = context;
        logManagerEx = LogManagerEx.getInstance();
    }

    public void register() {
        receiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                batteryStatus = intent;
                switch (intent.getAction()) {
                    case Intent.ACTION_POWER_CONNECTED:
                        onPowerConnected(context, intent);
                        break;
                    case Intent.ACTION_POWER_DISCONNECTED:
                        onPowerDisconnected(context, intent);
                        break;
                    case Intent.ACTION_BATTERY_CHANGED:
                        onBatteryChanged(context, intent);
                        break;
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        filter.addAction(Intent.ACTION_POWER_CONNECTED);
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        batteryStatus = context.registerReceiver(receiver, filter);

    }

    public float getBatteryPercent() {
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        LogManagerEx.getInstance().show("----------------"+level+"/"+scale);
        float batteryPct = level / (float) scale;
        return batteryPct;
    }

    public int getChargePlug() {
        return batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
    }

    public int getStatus() {
        return batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
    }

    public int getHealth() {
        return batteryStatus.getIntExtra(BatteryManager.EXTRA_HEALTH, -1);
    }

    public int getTemperature() {
        return batteryStatus.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
    }

    public int getVoltage() {
        return batteryStatus.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
    }

    public boolean isPresent() {
        return batteryStatus.getBooleanExtra(BatteryManager.EXTRA_PRESENT, false);
    }

    public String getTechnology() {
        return batteryStatus.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY);
    }

    public String getChargePlugString() {
        String result = null;
        int chargePlug = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        switch (chargePlug) {
            case BATTERY_PLUGGED_AC:
                result = "AC";
                break;
            case BATTERY_PLUGGED_USB:
                result = "USB";
                break;
            case BATTERY_PLUGGED_WIRELESS:
                result = "Wireless";
                break;
        }
        return result;
    }

    public void unregister() {
        context.unregisterReceiver(receiver);
    }


    abstract public void onPowerConnected(Context context, Intent intent);

    abstract public void onPowerDisconnected(Context context, Intent intent);

    abstract public void onBatteryChanged(Context context, Intent intent);
}
