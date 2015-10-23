package leo.unsw.comp9336assn.utils;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by LeoPC on 2015/9/8.
 */
public class SampleService extends Service {
    Context context;
    LogManagerEx logManagerEx = LogManagerEx.getInstance();
    int anInt;

    public void init(){
        context = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
