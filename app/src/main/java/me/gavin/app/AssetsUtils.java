package me.gavin.app;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


/**
 * 操作安装包中的“assets”目录下的文件
 *
 * @author 李玉江[QQ:1023694760]
 * @version 2013-11-2
 */
public class AssetsUtils {

    /**
     * read file content
     *
     * @param context   the context
     * @param assetPath the asset path
     * @return String string
     */
    public static String readText(Context context, String assetPath, long offset) {
        try {
            return toString(context.getAssets().open(assetPath), offset);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * To string string.
     *
     * @param is the is
     * @return the string
     */
    private static String toString(InputStream is, long offset) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            char[] chars = new char[1000];
            reader.skip(offset);
            reader.read(chars, 0, chars.length);
            return String.valueOf(chars);
        } catch (IOException e) {
            return null;
        }
    }

}
