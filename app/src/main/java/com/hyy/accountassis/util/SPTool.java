package com.hyy.accountassis.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;

/**
 *
 */

public class SPTool {

    private SharedPreferences mSharedPreferences;
    private Context mContext;

    private SPTool() {
    }

    private static class Holder {
        private static SPTool singleton = new SPTool();
    }

    public static SPTool getInstance() {
        return Holder.singleton;
    }

    public void init(Context context) {
        if (context == null) {
            throw new IllegalArgumentException();
        }
        this.mContext = context;
        mSharedPreferences = context.getSharedPreferences("account_cache",
                Context.MODE_PRIVATE);
    }


    /**
     * Ways of preserving data, we need to save specific types of data, then according to different types of calls the save method
     *
     * @param key
     * @param object
     */
    public void setParam(String key, Object object) {

        String type = object.getClass().getSimpleName();
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        switch (type) {
            case "String":
                editor.putString(key, (String) object);
                break;
            case "Integer":
                editor.putInt(key, (Integer) object);
                break;
            case "Boolean":
                editor.putBoolean(key, (Boolean) object);
                break;
            case "Float":
                editor.putFloat(key, (Float) object);
                break;
            case "Long":
                editor.putLong(key, (Long) object);
                break;
        }
        editor.apply();
    }


    /**
     * Preserved by the method of data, we according to the default deserves to specific types of stored data, and then call the method that relative to get the value
     *
     * @param key
     * @param defaultObject
     * @return
     */
    public Object getParam(String key, Object defaultObject) {
        String type = defaultObject.getClass().getSimpleName();
        switch (type) {
            case "String":
                return mSharedPreferences.getString(key, (String) defaultObject);
            case "Integer":
                return mSharedPreferences.getInt(key, (Integer) defaultObject);
            case "Boolean":
                return mSharedPreferences.getBoolean(key, (Boolean) defaultObject);
            case "Float":
                return mSharedPreferences.getFloat(key, (Float) defaultObject);
            case "Long":
                return mSharedPreferences.getLong(key, (Long) defaultObject);
        }
        return null;
    }


    /**
     * Clear all data
     */
    public void clear() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.clear().apply();
    }


    /**
     * Remove the specified data
     *
     * @param key
     */
    public void remove(String key) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.remove(key);
        editor.apply();
    }

    /**
     * Queries whether a key already exists
     *
     * @param key
     * @return
     */
    public boolean contains(String key) {
        return mSharedPreferences.contains(key);
    }

    /**
     * Returns all key/value pair
     *
     * @return
     */
    public Map<String, ?> getAllKey() {
        return mSharedPreferences.getAll();
    }

}
