package me.gavin.app.model;

import android.net.Uri;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.security.NoSuchAlgorithmException;

import me.gavin.app.StreamHelper;
import me.gavin.base.App;

/**
 * Book
 *
 * @author gavin.xiong 2017/12/19
 */
@Entity
public class Book implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final int TYPE_LOCAL = 0;
    public static final int TYPE_ONLINE = 1;

    @Id(autoincrement = true)
    public Long _id;
    public String name;
    public String author;
    public String cover;
    @Transient
    public String intro;
    public int type;
    public long offset; // 阅读进度
    public long time; // 阅读时间

    // 本地
    public String uri; // file:// | content:/
    public String charset;
    public long length;
    public String MD5; // TODO: 2018/1/1 文件一致性校验

    // 在线
    public String id;
    public String src;
    public String srcName;
    public int count; // 章节数
    public int index; // 章节进度

    @Generated(hash = 799521487)
    public Book(Long _id, String name, String author, String cover, int type, long offset,
                long time, String uri, String charset, long length, String MD5, String id,
                String src, String srcName, int count, int index) {
        this._id = _id;
        this.name = name;
        this.author = author;
        this.cover = cover;
        this.type = type;
        this.offset = offset;
        this.time = time;
        this.uri = uri;
        this.charset = charset;
        this.length = length;
        this.MD5 = MD5;
        this.id = id;
        this.src = src;
        this.srcName = srcName;
        this.count = count;
        this.index = index;
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

    public static Book fromUri(Uri uri) throws IOException, NoSuchAlgorithmException {
        if ("file".equals(uri.getScheme())) {
            File file = new File(uri.getPath());
            if (!file.exists()) throw new FileNotFoundException();
            Book book = new Book();
            book.uri = uri.toString();
            book.name = file.getName().substring(0, file.getName().lastIndexOf("."));
            book.type = TYPE_LOCAL;
            String encoding = StreamHelper.getCharsetByJUniversalCharDet(file);
            book.charset = encoding != null ? encoding : "utf-8";
            book.MD5 = StreamHelper.getFileMD5(file);
            book.length = StreamHelper.getLength(book.open(), book.charset);
            book.time = System.currentTimeMillis();
            return book;
        }
        throw new IOException("UNKNOWN");
    }

    public InputStream open() throws IOException {
        return App.get().getContentResolver().openInputStream(Uri.parse(uri));
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

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getIndex() {
        return this.index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getCount() {
        return this.count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getIntro() {
        return this.intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getSrcName() {
        return this.srcName;
    }

    public void setSrcName(String srcName) {
        this.srcName = srcName;
    }
}
