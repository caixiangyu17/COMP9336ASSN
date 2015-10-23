package leo.unsw.comp9336assn.service;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.NotificationCompat;

import leo.unsw.comp9336assn.R;
import leo.unsw.comp9336assn.activity.Activity_Main;
import leo.unsw.comp9336assn.methods.AccelerometerRecorder;
import leo.unsw.comp9336assn.methods.Recorder;
import leo.unsw.comp9336assn.methods.ServiceStarter;
import leo.unsw.comp9336assn.utils.LogManagerEx;

/**
 * Created by LeoPC on 2015/10/9.
 */
public class Service_Main extends Service {
    public static final int IDLE = 0x0000;
    public static final int ACCELEROMETER_L_START = 0x0001;
    public static final int ACCELEROMETER_M_START = 0x0002;
    public static final int ACCELEROMETER_H_START = 0x0003;
    public static final int GPS = 0x0004;
    public static final int NFC = 0x0005;
    public static final int WIFI_HOTSPOT = 0x0006;
    public static final int BLUETOOTH = 0x0007;


    Context context;
    AccelerometerRecorder accelerometerRecorder;
    boolean isAlive = false;
    int currentState = IDLE;

    Handler commandHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ServiceStarter.ERROR:
                    break;
                case ServiceStarter.INIT:
                    new Thread() {
                        public void run() {
                            while (isAlive) {
                                switch (currentState) {
                                    case ACCELEROMETER_L_START:
                                        Recorder.appendConsumption(context, "accelerometer_l");
                                        break;
                                    case ACCELEROMETER_M_START:
                                        Recorder.appendConsumption(context, "accelerometer_m");
                                        break;
                                    case ACCELEROMETER_H_START:
                                        Recorder.appendConsumption(context, "accelerometer_h");
                                        break;
                                    case GPS:
                                        Recorder.appendConsumption(context, "gps");
                                        break;
                                    case NFC:
                                        Recorder.appendConsumption(context, "nfc");
                                        break;
                                    case WIFI_HOTSPOT:
                                        Recorder.appendConsumption(context, "wifi_hotspot");
                                        break;
                                    case BLUETOOTH:
                                        Recorder.appendConsumption(context, "bluetooth");
                                        break;
                                }
                                try {
                                    sleep(60000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }.start();
                    break;
                case ServiceStarter.ACCELEROMETER_L_START:
                    if (accelerometerRecorder != null) {
                        accelerometerRecorder.stop();
                    }
                    accelerometerRecorder = new AccelerometerRecorder(context);
                    accelerometerRecorder.start(SensorManager.SENSOR_DELAY_UI);
                    currentState = ACCELEROMETER_L_START;
                    break;
                case ServiceStarter.ACCELEROMETER_M_START:
                    if (accelerometerRecorder != null) {
                        accelerometerRecorder.stop();
                    }
                    accelerometerRecorder = new AccelerometerRecorder(context);
                    accelerometerRecorder.start(SensorManager.SENSOR_DELAY_NORMAL);
                    currentState = ACCELEROMETER_M_START;
                    break;
                case ServiceStarter.ACCELEROMETER_H_START:
                    if (accelerometerRecorder != null) {
                        accelerometerRecorder.stop();
                    }
                    accelerometerRecorder = new AccelerometerRecorder(context);
                    accelerometerRecorder.start(SensorManager.SENSOR_DELAY_FASTEST);
                    currentState = ACCELEROMETER_H_START;
                    break;
                case ServiceStarter.ACCELEROMETER_L_STOP:
                    if (accelerometerRecorder != null) {
                        accelerometerRecorder.stop();
                    }
                    currentState = IDLE;
                    break;
                case ServiceStarter.ACCELEROMETER_M_STOP:
                    if (accelerometerRecorder != null) {
                        accelerometerRecorder.stop();
                    }
                    currentState = IDLE;
                    break;
                case ServiceStarter.ACCELEROMETER_H_STOP:
                    if (accelerometerRecorder != null) {
                        accelerometerRecorder.stop();
                    }
                    currentState = IDLE;
                    break;
                case ServiceStarter.GPS_START:
                    currentState = GPS;
                    break;
                case ServiceStarter.GPS_STOP:
                    currentState = IDLE;
                    break;
                case ServiceStarter.NFC_START:
                    currentState = NFC;
                    break;
                case ServiceStarter.NFC_STOP:
                    currentState = IDLE;
                    break;
                case ServiceStarter.BLUETOOTH_START:
                    currentState = BLUETOOTH;
                    break;
                case ServiceStarter.BLUETOOTH_STOP:
                    currentState = IDLE;
                    break;
                case ServiceStarter.WIFI_HOTSPOT_START:
                    currentState = WIFI_HOTSPOT;
                    break;
                case ServiceStarter.WIFI_HOTSPOT_STOP:
                    currentState = IDLE;
                    break;
                case ServiceStarter.REQUEST_STATE:
                    ServiceStarter.senState(context, currentState);
                    break;
            }
            super.handleMessage(msg);
        }
    };

    public void init() {
        context = this;
        isAlive = true;
        Intent notificationIntent = new Intent(this, Activity_Main.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        NotificationCompat.Builder mBuilder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                        .setColor(Color.BLUE)
                        .setSmallIcon(R.drawable.battery)
                        .setContentTitle("Energy Manager")
                        .setContentText("EM is running on background")
                        .setContentIntent(pendingIntent);
        startForeground(9336, mBuilder.build());
        LogManagerEx.getInstance().show("service initialization complete");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        commandHandler.sendEmptyMessage(ServiceStarter.getState(intent));
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        isAlive = false;
        super.onDestroy();
    }
}
