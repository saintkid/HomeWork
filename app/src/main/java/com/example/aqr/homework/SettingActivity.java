package com.example.aqr.homework;


import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;




/**
 * Created by Aqr on 2017/5/30.
 */

public class SettingActivity extends Activity {
    public static PreferenceManager preferenceManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
       /// setContentView(R.layout.audio_layout);


    }





    public static class MyPreferenceFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preference);
            preferenceManager = this.getPreferenceManager();


        }




    }

}
