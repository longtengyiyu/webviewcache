package com.tangtang.webviewcache;

import android.app.Application;
import android.content.Context;

import com.liulishuo.filedownloader.FileDownloader;

public class AppApplication extends Application {
    private static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        initDownload();
    }

    private void initDownload(){
        FileDownloader.setupOnApplicationOnCreate(this);
    }

    public static Context getContext(){
        return context;
    }
}
