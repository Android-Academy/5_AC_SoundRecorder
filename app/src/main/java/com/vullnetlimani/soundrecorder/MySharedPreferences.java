package com.vullnetlimani.soundrecorder;

import android.content.Context;
import android.content.SharedPreferences;

public class MySharedPreferences {

    private static final String MY_PREFS = "my_prefs";
    private static final String FIRST_TIME_PERMISSION_ASK = "first_time_permissions_ask";


    public static boolean isFirstTimeAskingPermission(Context context) {
        return context.getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE).getBoolean(FIRST_TIME_PERMISSION_ASK, true);
    }

    public static void firstTimeAskingPermission(Context context, boolean isFirstTime) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(FIRST_TIME_PERMISSION_ASK, isFirstTime).apply();
    }
}
