package com.example.aqr.homework;

import android.app.Activity;
import android.os.Bundle;

import com.example.aqr.homework.view.FiveInaRowView;

public class MainActivity extends Activity {
    private FiveInaRowView fiveInaRowView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ongame_main);
        fiveInaRowView = (FiveInaRowView) findViewById(R.id.fiveinrow);

    }
}
