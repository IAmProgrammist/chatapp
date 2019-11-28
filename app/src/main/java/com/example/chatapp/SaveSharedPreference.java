package com.example.chatapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SaveSharedPreference {
    static final String PREF_USER_NAME = "username";
    static final String PREF_PASSWORD = "password";
    static final String REMEMBER = "remember";

    static SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public static void setUserName(Context ctx, String userName) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_USER_NAME, userName);
        editor.commit();
    }

    public static String getUserName(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_USER_NAME, "");
    }

    public static void setPassword(Context ctx, String password) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_PASSWORD, password);
        editor.commit();
    }

    public static String getPassword(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_PASSWORD, "");
    }

    public static void setRemember(Context ctx, boolean check) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(REMEMBER, String.valueOf(check));
        editor.commit();
    }

    public static boolean getRemember(Context ctx) {
        return Boolean.parseBoolean(getSharedPreferences(ctx).getString(REMEMBER, ""));
    }
}