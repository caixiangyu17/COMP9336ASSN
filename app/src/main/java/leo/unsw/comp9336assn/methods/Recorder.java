package leo.unsw.comp9336assn.methods;

import android.content.Context;
import android.content.Intent;

import java.io.File;

import leo.unsw.comp9336assn.Constant;
import leo.unsw.comp9336assn.utils.BatteryManagerEx;
import leo.unsw.comp9336assn.utils.FileManager;

/**
 * Created by LeoPC on 2015/10/10.
 */
public class Recorder {
    public static void appendConsumption(Context context, String file){

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
        StringBuffer input = new StringBuffer();
        input.append(batteryManagerEx.getBatteryPercent()*100);
        input.append(",");
        FileManager.writeFile(Constant.PATH, file, input.toString());
    }

    public static void appendKinetic(String path, double acc[], int time){
        File tmpFile = new File(Constant.PATH+path);
        if (!tmpFile.exists()) {
            FileManager.writeFile(Constant.PATH, path, "time,x,y,z\n");
        }
        StringBuffer input = new StringBuffer();
        input.append(time/100f);
        input.append(",");
        input.append(acc[0]);
        input.append(",");
        input.append(acc[1]);
        input.append(",");
        input.append(acc[2]);
        input.append("\n");
        FileManager.writeFile(Constant.PATH, path, input.toString());
    }

}
