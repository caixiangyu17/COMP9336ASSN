package leo.unsw.comp9336assn.utils;

import android.app.Activity;
import android.view.View;
import java.util.HashMap;

/**
 * Created by LeoPC on 2015/10/8.
 */
public class ViewFactory {
    Activity activity;
    HashMap<Integer, View> views = new HashMap<>();

    public ViewFactory(Activity activity) {
        this.activity = activity;
    }

    public void add(int id) {
        View view = activity.findViewById(id);
        views.put(id, view);
    }

//    public View get(int id) {
//        return views.get(id);
//    }

    public View get(int id){
        return activity.findViewById(id);
    }
}
