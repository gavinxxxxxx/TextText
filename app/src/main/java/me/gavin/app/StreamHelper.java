package me.gavin.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StreamHelper {

    public static String getText(InputStream is, long offset, int limit) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            char[] buffer = new char[limit];
            reader.skip(offset);
            reader.read(buffer);
            return String.valueOf(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
