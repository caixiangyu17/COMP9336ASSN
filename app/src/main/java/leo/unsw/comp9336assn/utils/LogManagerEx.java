package leo.unsw.comp9336assn.utils;

import android.util.Log;

/**
 * Created by LeoPC on 2015/8/1.
 */
public class LogManagerEx {
    private boolean enable = true;
    private String tag = "test";

    private static LogManagerEx instance = new LogManagerEx();
    public static LogManagerEx getInstance(){
        return instance;
    }
    public static LogManagerEx getInstance(String tag){
        instance.tag = tag;
        return instance;
    }


    public void show(String msg){
        if(enable){
            Log.v(tag, msg);
        }
    }

    public void show(int msg){
        if(enable){
            Log.v(tag, msg+"");
        }
    }

    public void show(boolean msg){
        if(enable){
            Log.v(tag, msg+"");
        }
    }

    public void show(float msg){
        if(enable){
            Log.v(tag, msg+"");
        }
    }

    public void show(double msg){
        if(enable){
            Log.v(tag, msg+"");
        }
    }

    public void showMethodName(){
        Log.v(tag, new Throwable().getStackTrace()[1].getMethodName());
    }
}
