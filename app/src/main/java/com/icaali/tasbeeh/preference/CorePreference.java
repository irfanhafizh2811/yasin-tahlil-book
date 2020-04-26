package com.icaali.tasbeeh.preference;

import android.content.Context;
import android.content.SharedPreferences;

public class CorePreference {

    public static final String CORE_PREF = "CorePref";
    private static CorePreference sinPref;
    private SharedPreferences sharedPreference;

    public CorePreference(SharedPreferences sharedPreference) {
        this.sharedPreference = sharedPreference;
    }

    public static CorePreference getInstance(Context context) {
        if (sinPref == null)
            sinPref = new CorePreference(context.getSharedPreferences(CORE_PREF, Context.MODE_PRIVATE));
        return sinPref;
    }

    public String getString(String key, String defValue) {
        return sharedPreference.getString(key, defValue);
    }

    public int getInt(String key, int defValue) {
        return sharedPreference.getInt(key, defValue);
    }

    public void setString(String key, String value) {
        sharedPreference.edit().putString(key, value).apply();
    }

    public void setInt(String key, int value) {
        sharedPreference.edit().putInt(key, value).apply();
    }

    public void setBoolean(String key, boolean value) {
        sharedPreference.edit().putBoolean(key, value).apply();
    }

    public boolean getBoolean(String key, boolean defValue) {
        return sharedPreference.getBoolean(key, defValue);
    }

    public void setLong(String key, long value) {
        sharedPreference.edit().putLong(key, value).apply();
    }

    public long getLong(String key, long defValue) {
        return sharedPreference.getLong(key, defValue);
    }

    public void remove(String key) {
        sharedPreference.edit().remove(key).apply();
    }

    public void clear() {
        sharedPreference.edit().clear().commit();
    }

    public boolean hasKey(String key) {
        return sharedPreference.contains(key);
    }
}
