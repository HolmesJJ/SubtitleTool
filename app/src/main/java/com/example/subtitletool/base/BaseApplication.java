package com.example.subtitletool.base;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;

import com.example.subtitletool.utils.ContextUtils;

import static com.example.subtitletool.constants.Constants.MAIN_SERVICE_CHANNEL;

public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ContextUtils.init(this.getApplicationContext());
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        NotificationChannel serviceChannel = new NotificationChannel(
                MAIN_SERVICE_CHANNEL,
                MAIN_SERVICE_CHANNEL,
                NotificationManager.IMPORTANCE_DEFAULT
        );
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(serviceChannel);
    }
}
