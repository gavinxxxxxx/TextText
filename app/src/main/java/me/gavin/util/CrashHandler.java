package me.gavin.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Process;
import android.text.TextUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Locale;

import me.gavin.base.App;

/**
 * 全局异常捕获工具
 *
 * @author gavin.xiong 2018/3/29
 */
public final class CrashHandler implements Thread.UncaughtExceptionHandler {

    public static CrashHandler get() {
        return Holder.INSTANCE;
    }

    private static final class Holder {
        private static CrashHandler INSTANCE = new CrashHandler();
    }

    public void init() {
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable t) {
//        t.printStackTrace();
        printException2SDCard(t);
        // TODO: 2018/3/29  上传异常信息到服务器 & 延时杀死进程
        // SystemClock.sleep(2000);
        Process.killProcess(Process.myPid());
        System.exit(1);
    }

    private void printException2SDCard(Throwable t) {
        String parent = getLogDir(App.get());
        if (TextUtils.isEmpty(parent)) {
            return;
        }
        Date curr = new Date();
        String path = String.format(Locale.getDefault(), "%1$s/CRASH_%2$tY.%2$tm.%2$td_%2$tH.%2$tM.%2$tS.trace", parent, curr);
        File file = new File(path);
        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            PackageManager pm = App.get().getPackageManager();
            PackageInfo info = pm.getPackageInfo(App.get().getPackageName(), PackageManager.GET_ACTIVITIES);
            pw.println(String.format(Locale.getDefault(), "异常时间：%1$tF %1$tT", curr));
            pw.println("应用版本：" + info.versionName);
            pw.println("应用版本号：" + info.versionCode);
            pw.println("Android版本：" + Build.VERSION.RELEASE);
            pw.println("Android版本号：" + Build.VERSION.SDK_INT);
            pw.println("手机制造商：" + Build.MANUFACTURER);
            pw.println("手机型号：" + Build.MODEL);
            t.printStackTrace(pw);
            Throwable cause = t.getCause();
            while (cause != null) {
                cause.printStackTrace(pw);
                cause = cause.getCause();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取日志文件夹 - /storage/sdcard0/Android/data/com.gavin.example/files/log
     */
    private String getLogDir(Context context) {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                && context.getExternalFilesDir("log") != null) {
            return context.getExternalFilesDir("log").getPath();
        } else {
            return null;
        }
    }
}