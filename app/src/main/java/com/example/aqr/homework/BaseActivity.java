package com.example.aqr.homework;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.example.aqr.homework.util.ActivityCollector;

/**
 * Created by Aqr on 2017/5/29.
 */

public class BaseActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityCollector.addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }
}
