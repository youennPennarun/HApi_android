package com.example.nolitsou.hapi.data;

import android.content.Context;
import android.content.SharedPreferences;

public class Settings {
    public final static String PREF_FILE = "hapi.conf";
    public final static String HOST_PREF_STR = "HOST";
    public final static String USERNAME_PREF_STR = "USERNAME";
    public final static String PASSWORD_PREF_STR = "PASSWORD";
    public static String host = "";
    public static String username = "";
    public static String password = "";

    public static void loadSettings(Context context) {
        final SharedPreferences prefs = new Preferences(
                context, context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE));
        host = prefs.getString(HOST_PREF_STR, "");
        username = prefs.getString(USERNAME_PREF_STR, "");
        password = prefs.getString(PASSWORD_PREF_STR, "");
    }
}
