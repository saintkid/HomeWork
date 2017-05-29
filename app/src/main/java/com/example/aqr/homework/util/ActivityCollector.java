package com.example.aqr.homework.util;

import android.app.Activity;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aqr on 2017/5/29.
 */

public class ActivityCollector {
    public static List<Activity> activities = new ArrayList<Activity>();
    public static void addActivity(Activity activity){
        activities.add(activity);
        Log.d("Activity","activity: "+activity.getClass().getSimpleName());
    }
    public static Activity getActivity(String name){
        for(Activity activity: activities){
            if(activity.getClass().getSimpleName().equals(name)){
                return activity;
            }
        }
        return  null;
    }
    public static void removeActivity(Activity activity){
        activities.remove(activity);
    }
    public static void finishAll(){
        for(Activity activity:activities){
            if(!activity.isFinishing()){
                activity.finish();
            }

        }
    }
}
