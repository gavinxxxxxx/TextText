package me.gavin.app;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.mozilla.universalchardet.UniversalDetector;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.gavin.app.core.model.Chapter;

public class StreamHelper {

    /**
     * 获取文本文件编码 - juniversalchardet
     *
     * @link {https://code.google.com/archive/p/juniversalchardet/}
     */
    @Nullable
    public static String getCharsetByJUniversalCharDet(@NonNull File textFile) throws IOException {
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
        }
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

    public static long getLength(InputStream is, String charset) throws IOException {
        try (Reader reader = new InputStreamReader(is, charset)) {
            return reader.skip(Long.MAX_VALUE);
        }
    }

    /**
     * 生成章节信息
     * todo 回退防切断
     */
    public static List<Chapter> getChapters(InputStream is, String charset) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, charset))) {
            List<Chapter> result = new ArrayList<>();
            char[] buffer = new char[1024 * 8];
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
        }
    }

    public static String getText(InputStream is, String charset, long offset, int limit) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, charset))) {
            char[] buffer = new char[limit];
            reader.skip(offset);
            reader.read(buffer);
            return String.valueOf(buffer);
        }
    }

    /**
     * RandomAccessFile 获取文件的MD5值
     *
     * @param file 文件路径
     * @return md5
     */
    public static String getFileMD5(@NonNull File file) throws IOException, NoSuchAlgorithmException {
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
        }
    }
}
