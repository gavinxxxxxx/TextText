package me.gavin.app.model;

import android.net.Uri;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import me.gavin.app.StreamHelper;
import me.gavin.base.App;

/**
 * Book
 *
 * @author gavin.xiong 2017/12/19
 */
@Entity
public class Book {

    @Id(autoincrement = true)
    private long _Id;
    private String name;
    private String author;
    private String charset;
    private long length;
    private String uri;
    private String MD5; // TODO: 2018/1/1 文件一致性校验

    @Transient
    private List<Chapter> chapterList;

    private long offset; // 阅读进度
    private long time; // 阅读时间


    @Generated(hash = 399209597)
    public Book(long _Id, String name, String author, String charset, long length,
                String uri, String MD5, long offset, long time) {
        this._Id = _Id;
        this.name = name;
        this.author = author;
        this.charset = charset;
        this.length = length;
        this.uri = uri;
        this.MD5 = MD5;
        this.offset = offset;
        this.time = time;
    }

    @Generated(hash = 1839243756)
    public Book() {
    }

    public long get_Id() {
        return this._Id;
    }

    public void set_Id(long _Id) {
        this._Id = _Id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return this.author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCharset() {
        return this.charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public long getLength() {
        return this.length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public String getUri() {
        return this.uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getMD5() {
        return this.MD5;
    }

    public void setMD5(String MD5) {
        this.MD5 = MD5;
    }

    public long getOffset() {
        return this.offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public long getTime() {
        return this.time;
    }

    public void setTime(long time) {
        this.time = time;
    }


    public static Book fromUri(Uri uri) {
        Book book = new Book();
        book.uri = uri.toString();
        return book;
    }

    public static Book fromSDCard(String path) {
        return fromFile(new File(path));
    }

    public static Book fromFile(File file) {
        Book book = fromUri(Uri.fromFile(file));
        book.name = file.getName().substring(0, file.getName().lastIndexOf("."));
        book.charset = StreamHelper.getCharsetByJUniversalCharDet(file);
        book.length = StreamHelper.getLength(book.open(), book.charset);
        StreamHelper.getChapters(book.open(), book.charset);
        return book;
    }

    public InputStream open() {
        try {
            if (Uri.parse(uri).getScheme().equals("file")) {
                String path = Uri.parse(uri).getPath();
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
