package me.gavin.app.explorer;

import android.support.annotation.NonNull;

import java.io.File;
import java.text.SimpleDateFormat;

/**
 * 这里是萌萌哒注释君
 *
 * @author gavin.xiong 2018/1/2
 */
public class FileItem {

    private String name;
    private boolean dir;
    private String sub;
    private String type;
    private String time;

    public FileItem(@NonNull File file) {
        this.name = file.getName();
        this.dir = file.isDirectory();
        this.sub = dir ? "dir" : "file";
        this.time = SimpleDateFormat.getDateTimeInstance().format(file.lastModified());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isDir() {
        return dir;
    }

    public void setDir(boolean dir) {
        this.dir = dir;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
