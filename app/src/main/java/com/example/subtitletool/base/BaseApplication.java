package com.example.subtitletool.base;

import android.app.Application;

import com.example.subtitletool.utils.ContextUtils;
import com.example.subtitletool.utils.FileUtils;

public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ContextUtils.init(this);
        FileUtils.init();
    }
}
