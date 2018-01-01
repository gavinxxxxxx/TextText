package me.gavin.app.model;

import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import me.gavin.app.StreamHelper;
import me.gavin.base.App;

/**
 * Book
 *
 * @author gavin.xiong 2017/12/19
 */
public class Book {

    private String name;
    private String author;
    private String charset;
    private long length;
    private Uri uri;
    private String MD5; // TODO: 2018/1/1 文件一致性校验
    private String SHA1; // TODO: 2018/1/1 文件一致性校验

    private Book() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public String getMD5() {
        return MD5;
    }

    public void setMD5(String MD5) {
        this.MD5 = MD5;
    }

    public static Book fromUri(Uri uri) {
        Book book = new Book();
        book.setUri(uri);
        return book;
    }

    public static Book fromAsset(String path) {
        return fromUri(Uri.parse("file:///android_asset/" + path));
    }

    public static Book fromSDCard(String path) {
        return fromFile(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + path));
    }

    public static Book fromFile(File file) {
        Book book = fromUri(Uri.fromFile(file));
        book.charset = StreamHelper.getCharset(book.open());
        book.length = StreamHelper.getLength(book.open(), book.getCharset());
        StreamHelper.getChapters(book.open(), book.getCharset());
        return book;
    }

    public InputStream open() {
        try {
            if (uri.getScheme().equals("file")) {
                String path = uri.getPath();
                if (path.startsWith("/android_asset")) {
                    return App.get().getAssets().open(path.substring(15));
                }
                return new FileInputStream(path);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
