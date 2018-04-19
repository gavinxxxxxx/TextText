package me.gavin.app;

import android.support.annotation.NonNull;

import org.mozilla.universalchardet.UniversalDetector;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.gavin.app.model.Chapter;
import me.gavin.util.L;

public class StreamHelper {

    /**
     * 使用 juniversalchardet 获取文本文件编码
     *
     * @link {https://code.google.com/archive/p/juniversalchardet/}
     */
    public static String getCharsetByJUniversalCharDet(@NonNull File textFile) {
        try (FileInputStream fis = new FileInputStream(textFile)) {
            byte[] buffer = new byte[4096];
            UniversalDetector detector = new UniversalDetector(null);
            int length;
            while ((length = fis.read(buffer)) > 0 && !detector.isDone()) {
                detector.handleData(buffer, 0, length);
            }
            detector.dataEnd();
            String encoding = detector.getDetectedCharset();
            detector.reset();
            return encoding;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

//    /**
//     * 使用 ICU4J 获取文本文件编码
//     *
//     * @link {http://site.icu-project.org/download/60#TOC-ICU4J-Download}
//     * @link {http://www.th7.cn/Program/Android/201704/1146516.shtml}
//     */
//    public static String getCharsetByICU4J(byte[] bytes){
//        CharsetDetector detector = new CharsetDetector();
//        detector.setText(bytes);
//        CharsetMatch match = detector.detect();
//        String encode = match.getName();
//        return encode;
//    }

    /**
     * 通过文件头判断文件编码
     */
    public static String getCharsetByHead(InputStream is) {
        try (BufferedInputStream bis = new BufferedInputStream(is)) {
            bis.mark(4);
            byte[] first3bytes = new byte[3];
            bis.read(first3bytes);
            bis.reset();
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

    public static long getLength(InputStream is, String charset) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, charset))) {
            return reader.skip(Long.MAX_VALUE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 生成章节信息
     */
    public static List<Chapter> getChapters(InputStream is, String charset) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, charset))) {
            List<Chapter> result = new ArrayList<>();
            char[] buffer = new char[1024 * 4];
            long offset = 0;
            while (reader.read(buffer) > 0) {
                String str = String.valueOf(buffer);
                Matcher matcher = Pattern.compile(Config.REGEX_CHAPTER).matcher(str);
                while (matcher.find()) {
                    String title = matcher.group().trim();
                    long index = offset + str.indexOf(title);
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

    /**
     * RandomAccessFile 获取文件的MD5值
     *
     * @param file 文件路径
     * @return md5
     */
    public static String getFileMD5(@NonNull File file) {
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r")) {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[1024 * 10];
            int len;
            while ((len = randomAccessFile.read(buffer)) > 0) {
                messageDigest.update(buffer, 0, len);
            }
            BigInteger bigInt = new BigInteger(1, messageDigest.digest());
            String md5 = bigInt.toString(16);
            while (md5.length() < 32) {
                md5 = "0" + md5;
            }
            return md5;
        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
