package me.gavin.util;

import android.content.Context;
import android.content.SharedPreferences;

import me.gavin.base.App;

/**
 * SharedPreferences 数据存储工具类
 *
 * @author gavin.xiong
 */
public class SPUtil {

    private static SharedPreferences getSharedPreferences() {
        return App.get().getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
    }

    private static SharedPreferences.Editor getEditor() {
        return getSharedPreferences().edit();
    }

    private static boolean contains(String key) {
        return getSharedPreferences().contains(key);
    }

    private static void remove(String key) {
        getEditor().remove(key).apply();
    }

    private static boolean removeIfPutNull(String key, Object value) {
        if (value == null) {
            remove(key);
            return false;
        }
        return true;
    }

    public static Integer getInt(String key) {
        return contains(key) ? getSharedPreferences().getInt(key, 0) : null;
    }

    public static int getInt(String key, int defVal) {
        return getSharedPreferences().getInt(key, defVal);
    }

    public static void putInt(String key, Integer value) {
        if (removeIfPutNull(key, value)) {
            getEditor().putInt(key, value).apply();
        }
    }

    public static Float getFloat(String key) {
        return contains(key) ? getSharedPreferences().getFloat(key, 0f) : null;
    }

    public static float getFloat(String key, float defVal) {
        return getSharedPreferences().getFloat(key, defVal);
    }

    public static void putFloat(String key, Float value) {
        if (removeIfPutNull(key, value)) {
            getEditor().putFloat(key, value).apply();
        }
    }

    public static Boolean getBoolean(String key) {
        return contains(key) ? getSharedPreferences().getBoolean(key, false) : null;
    }

    public static boolean getBoolean(String key, boolean defVal) {
        return getSharedPreferences().getBoolean(key, defVal);
    }

    public static void putBoolean(String key, Boolean value) {
        if (removeIfPutNull(key, value)) {
            getEditor().putBoolean(key, value).apply();
        }
    }

    public static String getString(String key) {
        return getSharedPreferences().getString(key, null);
    }

    public static void putString(String key, String value) {
        getEditor().putString(key, value).apply();
    }

}
