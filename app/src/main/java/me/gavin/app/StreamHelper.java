package me.gavin.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.gavin.app.model.Chapter;
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

    public static List<Chapter> getChapters(InputStream is) {
         try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
             List<Chapter> result = new ArrayList<>();
             char[] buffer = new char[1024 * 4];
             long offset = 0;
             while (reader.read(buffer) != -1) {
                 String str = String.valueOf(buffer);
                 Matcher matcher = Pattern.compile(Config.REGEX_CHAPTER).matcher(str);
                 while (matcher.find()) {
                     String title = matcher.group().trim();
                     long index = offset + str.indexOf(title);
                     L.e(title + " - " + index);
                     result.add(new Chapter(index, title));
                 }
                 offset += buffer.length;
             }
             return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
