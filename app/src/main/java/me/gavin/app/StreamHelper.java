package me.gavin.app;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.gavin.app.model.Chapter;
import me.gavin.util.L;

public class StreamHelper {

    public static String getCharset(InputStream is) {
        try(BufferedInputStream bis = new BufferedInputStream(is)) {
            bis.mark(4);
            byte[] first3bytes = new byte[3];
            bis.read(first3bytes);
            L.e(BinaryToHexString(first3bytes));
            if (first3bytes[0] == (byte) 0xEF && first3bytes[1] == (byte) 0xBB && first3bytes[2] == (byte) 0xBF) {
                return "utf-8";
            } else if (first3bytes[0] == (byte) 0xFF && first3bytes[1] == (byte) 0xFE) {
                return "unicode";
            } else if (first3bytes[0] == (byte) 0xFE && first3bytes[1] == (byte) 0xFF) {
                return "utf-16be";
            } else if (first3bytes[0] == (byte) 0xFF && first3bytes[1] == (byte) 0xFF) {
                return "utf-16le";
            } else {
                return "GBK";
            }
        } catch (IOException e) {
            e.printStackTrace();
            L.e(e);
        }
        return null;
    }

    private static String hexStr =  "0123456789ABCDEF";  //全局
    public static String BinaryToHexString(byte[] bytes){
        String result = "";
        String hex = "";
        for(int i=0;i<bytes.length;i++){
            //字节高4位
            hex = String.valueOf(hexStr.charAt((bytes[i]&0xF0)>>4));
            //字节低4位
           hex += String.valueOf(hexStr.charAt(bytes[i]&0x0F));
                    result +=hex;
        }
        return result;
    }

    public static String getText(InputStream is, String charset, long offset, int limit) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, charset))) {
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

    public static long getLength(InputStream is, String charset) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, charset))) {
            return reader.skip(Long.MAX_VALUE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static List<Chapter> getChapters(InputStream is, String charset) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, charset))) {
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
