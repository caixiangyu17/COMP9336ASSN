package leo.unsw.comp9336assn.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import leo.unsw.comp9336assn.R;

public class SampleActivity extends Activity {
    LogManagerEx logManager;
    Context context;

    private void init() {
        context = this;
        logManager = LogManagerEx.getInstance();
        initLayout();
    }

    private void initLayout() {
        logManager.showMethodName();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

        logManager.showMethodName();
    }

    //
    @Override
    protected void onStart() {
        super.onStart();
        logManager.showMethodName();
    }

    @Override
    protected void onResume() {
        super.onResume();
        logManager.showMethodName();
    }

    @Override
    protected void onPause() {
        super.onPause();
        logManager.showMethodName();
    }

    @Override
    protected void onStop() {
        super.onStop();
        logManager.showMethodName();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        logManager.showMethodName();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        logManager.showMethodName();
    }
}
