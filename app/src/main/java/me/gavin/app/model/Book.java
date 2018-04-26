package me.gavin.app.model;

import android.net.Uri;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;

import me.gavin.app.StreamHelper;

/**
 * Book
 *
 * @author gavin.xiong 2017/12/19
 */
@Entity
public class Book implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id(autoincrement = true)
    private Long _id;
    private String name;
    private String author;
    private String cover;
    private String introduction;
    private String uri;

    private String id;
    private String src;

    private String charset;
    private long length;
    private String MD5; // TODO: 2018/1/1 文件一致性校验

    @Transient
    private List<Chapter> chapterList;

    private long offset; // 阅读进度
    private long time; // 阅读时间


    @Generated(hash = 1977289157)
    public Book(Long _id, String name, String author, String cover, String introduction,
                String uri, String id, String src, String charset, long length, String MD5,
                long offset, long time) {
        this._id = _id;
        this.name = name;
        this.author = author;
        this.cover = cover;
        this.introduction = introduction;
        this.uri = uri;
        this.id = id;
        this.src = src;
        this.charset = charset;
        this.length = length;
        this.MD5 = MD5;
        this.offset = offset;
        this.time = time;
    }

    @Generated(hash = 1839243756)
    public Book() {
    }

    public Long get_id() {
        return this._id;
    }

    public void set_id(Long _id) {
        this._id = _id;
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

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
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

    public static Book fromUri(Uri uri) throws IOException {
        if ("file".equals(uri.getScheme())) {
            File file = new File(uri.getPath());
            if (!file.exists()) throw new FileNotFoundException();
            Book book = new Book();
            book.uri = uri.toString();
            book.name = file.getName().substring(0, file.getName().lastIndexOf("."));
            book.charset = StreamHelper.getCharsetByJUniversalCharDet(file);
            book.MD5 = StreamHelper.getFileMD5(file);
            book.length = StreamHelper.getLength(book.open(), book.charset);
            book.time = System.currentTimeMillis();
            return book;
        }
        throw new IOException("UNKNOWN");
    }

    public InputStream open() throws IOException {
        Uri uri = Uri.parse(this.uri);
        if (uri.getScheme().equals("file")) {
            return new FileInputStream(uri.getPath());
        }
        return null;
    }

    @Override
    public String toString() {
        return "Book{" +
                "name='" + name + '\'' +
                ", author='" + author + '\'' +
                ", charset='" + charset + '\'' +
                ", length=" + length +
                ", uri='" + uri + '\'' +
                '}';
    }

    public String getCover() {
        return this.cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIntroduction() {
        return this.introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }
}
