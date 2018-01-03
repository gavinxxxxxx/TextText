package me.gavin.app.explorer;

import android.support.annotation.NonNull;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.functions.Function;
import me.gavin.app.RxTransformer;

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
    private String time;

    public FileItem(@NonNull File file) {
        this.file = file;
        this.name = file.getName();
        this.dir = file.isDirectory();
        this.time = SimpleDateFormat.getDateTimeInstance().format(file.lastModified());
        this.sub = dir ? "dir" : "file";
    }


    public static ObservableTransformer<File, FileItem> fromFile() {
        return upstream -> upstream.flatMap(new Function<File, ObservableSource<FileItem>>() {
            private int dc = 0, fc = 0;

            @Override
            public ObservableSource<FileItem> apply(File srcFile) throws Exception {
                if (!srcFile.isDirectory()) {
                    return Observable.just(srcFile)
                            .map(FileItem::new);
                }
                return Observable.fromArray(srcFile.listFiles())
                        .doOnSubscribe(disposable -> {
                            dc = 0;
                            fc = 0;
                        })
                        .compose(RxTransformer.fileFilter())
                        .map(subFile -> {
                            if (subFile.isDirectory()) {
                                dc++;
                            } else {
                                fc++;
                            }
                            return subFile;
                        })
                        .toList()
                        .map(files -> {
                            FileItem fileItem = new FileItem(srcFile);
                            fileItem.setSub(String.format(Locale.getDefault(), "%1$d 个文件夹，%2$d 个文本文件", dc, fc));
                            return fileItem;
                        })
                        .toObservable();
            }
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

}
