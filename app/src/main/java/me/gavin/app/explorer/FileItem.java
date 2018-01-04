package me.gavin.app.explorer;

import android.support.annotation.NonNull;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import me.gavin.app.RxTransformer;
import me.gavin.text.R;

/**
 * 这里是萌萌哒注释君
 *
 * @author gavin.xiong 2018/1/2
 */
public class FileItem {

    private File file;
    private String name;
    private boolean dir;
    private String sub;
    private String type;
    private int icon;
    private int color = 0xff33b5e5;
    private String time;

    public FileItem(@NonNull File file) {
        this.file = file;
        this.name = file.getName();
        this.dir = file.isDirectory();
        this.time = SimpleDateFormat.getDateTimeInstance().format(file.lastModified());
        this.sub = dir ? "dir" : "file";
        this.icon = dir ? R.drawable.ic_folder_black_24dp : R.drawable.ic_description_black_24dp;
        this.color = dir ? 0xff33b5e5 : 0xff99cc00;
    }

    public static ObservableTransformer<File, FileItem> fromFile() {
        return upstream -> upstream.flatMap(srcFile -> {
            if (!srcFile.isDirectory()) {
                return Observable.just(srcFile)
                        .map(FileItem::new);
            }
            int[] counts = {0, 0};
            return Observable.fromArray(srcFile.listFiles())
                    .compose(RxTransformer.fileFilter())
                    .map(subFile -> {
                        if (subFile.isDirectory()) {
                            counts[0]++;
                        } else {
                            counts[1]++;
                        }
                        return subFile;
                    })
                    .toList()
                    .map(files -> {
                        FileItem fileItem = new FileItem(srcFile);
                        fileItem.setSub(String.format(Locale.getDefault(),
                                "%1$d 个文件夹，%2$d 个文本文件", counts[0], counts[1]));
                        return fileItem;
                    })
                    .toObservable();
        });
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
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

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
