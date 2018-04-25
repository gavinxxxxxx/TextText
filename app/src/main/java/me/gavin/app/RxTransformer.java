package me.gavin.app;

import java.io.File;

import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import me.gavin.util.L;

/**
 * RxTransformer
 *
 * @author gavin.xiong 2018/1/3
 */
public class RxTransformer {

    public static <T> ObservableTransformer<T, T> applySchedulers() {
        return upstream -> upstream
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static <T> ObservableTransformer<T, T> log() {
        return upstream -> upstream
                .map(t -> {
                    L.d(t);
                    return t;
                });
    }

    /**
     * 文件过滤
     */
    public static ObservableTransformer<File, File> fileFilter() {
        return upstream -> upstream
                .filter(subFile -> !subFile.getName().startsWith("."))
                .filter(subFile -> subFile.isDirectory() || subFile.getName().endsWith(".txt"));
    }

}
