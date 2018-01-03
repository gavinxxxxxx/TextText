package me.gavin.app;

import java.io.File;

import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * RxTransformer
 *
 * @author gavin.xiong 2018/1/3
 */
public class RxTransformer {

    /**
     * 线程调度
     */
    public static <T> ObservableTransformer<T, T> applySchedulers() {
        return upstream -> upstream
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
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
