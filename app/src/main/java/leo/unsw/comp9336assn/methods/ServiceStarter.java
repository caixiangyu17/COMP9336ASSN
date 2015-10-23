package leo.unsw.comp9336assn.methods;

import android.content.Context;
import android.content.Intent;

import leo.unsw.comp9336assn.Constant;
import leo.unsw.comp9336assn.service.Service_Main;

/**
 * Created by LeoPC on 2015/10/9.
 */
public class ServiceStarter {
    public static final String TAG = "SERVICE_TAG";
    public static final int ERROR = 0x0000;
    public static final int INIT = 0x0001;
    public static final int REQUEST_STATE = 0x0002;
    public static final int ACCELEROMETER_L_START = 0x0010;
    public static final int ACCELEROMETER_L_STOP = 0x0011;
    public static final int ACCELEROMETER_M_START = 0x0012;
    public static final int ACCELEROMETER_M_STOP = 0x0013;
    public static final int ACCELEROMETER_H_START = 0x0014;
    public static final int ACCELEROMETER_H_STOP = 0x0015;
    public static final int GPS_START = 0x0016;
    public static final int GPS_STOP = 0x0017;
    public static final int NFC_START = 0x0018;
    public static final int NFC_STOP = 0x0019;
    public static final int BLUETOOTH_START = 0x001A;
    public static final int BLUETOOTH_STOP = 0x001B;
    public static final int WIFI_HOTSPOT_START = 0x001C;
    public static final int WIFI_HOTSPOT_STOP = 0x001D;


    public static void start(Context context, int type) {
        Intent intent = new Intent(context, Service_Main.class);
        intent.putExtra(TAG, type);
        context.startService(intent);
    }

    public static int getState(Intent intent) {
        if (intent != null) {
            return intent.getIntExtra(TAG, ERROR);
        } else {
            return ERROR;
        }
    }

    public static void senState(Context context, int type) {
        Intent intent = new Intent(Constant.BROADCAST_ACTION);
        intent.putExtra(TAG, type);
        context.sendBroadcast(intent);
    }

}
