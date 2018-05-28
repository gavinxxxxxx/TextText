package me.gavin.app.core.model;

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
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import me.gavin.app.StreamHelper;
import me.gavin.app.core.source.SourceModel;
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
    @Transient
    public SourceModel source;
    public String ext; // 补充字段

    @Transient
    public String category; // 分类
    @Transient
    public String state; // 状态
    @Transient
    public String updateTime; // 更新时间
    @Transient
    public String updateChapter; // 更新章节
    @Transient
    public final List<Chapter> chapters = new ArrayList<>(10);

    public int count; // 章节数
    public int index; // 章节进度

    public int srcCount;
    public String ids;
    public String srcs;
    public String srcNames;

    @Generated(hash = 2109721951)
    public Book(Long _id, String name, String author, String cover, int type, long offset, long time,
            String uri, String charset, long length, String MD5, String id, String src, String srcName,
            String ext, int count, int index, int srcCount, String ids, String srcs, String srcNames) {
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
        this.ext = ext;
        this.count = count;
        this.index = index;
        this.srcCount = srcCount;
        this.ids = ids;
        this.srcs = srcs;
        this.srcNames = srcNames;
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

    public String getCover() {
        return this.cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
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

    public String getUri() {
        return this.uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
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

    public String getMD5() {
        return this.MD5;
    }

    public void setMD5(String MD5) {
        this.MD5 = MD5;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSrc() {
        return this.src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getSrcName() {
        return this.srcName;
    }

    public void setSrcName(String srcName) {
        this.srcName = srcName;
    }

    public String getExt() {
        return this.ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public int getCount() {
        return this.count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getIndex() {
        return this.index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getSrcCount() {
        return this.srcCount;
    }

    public void setSrcCount(int srcCount) {
        this.srcCount = srcCount;
    }

    public String getIds() {
        return this.ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }

    public String getSrcs() {
        return this.srcs;
    }

    public void setSrcs(String srcs) {
        this.srcs = srcs;
    }

    public String getSrcNames() {
        return this.srcNames;
    }

    public void setSrcNames(String srcNames) {
        this.srcNames = srcNames;
    }

    public static Book fromUri(Uri uri) throws IOException, NoSuchAlgorithmException {
        if ("file".equals(uri.getScheme())) {
            File file = new File(uri.getPath());
            if (!file.exists()) throw new FileNotFoundException();
            Book book = new Book();
            book.uri = uri.toString();
            book.name = file.getName().substring(0, file.getName().lastIndexOf("."));
            book.type = TYPE_LOCAL;
            String encoding = StreamHelper.getCharsetByJUniversalCharDet(new FileInputStream(file));
            book.charset = encoding != null ? encoding : "UTF-8";
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
                ", ids='" + ids + '\'' +
                ", srcs='" + srcs + '\'' +
                ", srcNames='" + srcNames + '\'' +
                '}';
    }
}
