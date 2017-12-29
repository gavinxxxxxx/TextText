package me.gavin.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import me.gavin.util.L;

public class StreamHelper {

    public static String getText(InputStream is, long offset, int limit) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            char[] buffer = new char[limit];
            reader.skip(offset);
            reader.read(buffer);
            return String.valueOf(buffer);
        } catch (IOException e) {
            e.printStackTrace();
            L.e(e);
        }
        return null;
    }

    public static long getLength(InputStream is) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            return reader.skip(Long.MAX_VALUE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

}
