package com.example.subtitletool;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.subtitletool.utils.ContextUtils;

import static com.example.subtitletool.constants.Constants.MAIN_SERVICE_CHANNEL;

public class MainService extends Service {

    private static final String TAG = MainService.class.getSimpleName();
    private static final int MAIN_SERVICE_CHANNEL_ID = 1;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        Intent notificationIntent = new Intent(ContextUtils.getContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(ContextUtils.getContext(), 0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(ContextUtils.getContext(), MAIN_SERVICE_CHANNEL)
                .setContentTitle(MAIN_SERVICE_CHANNEL)
                .setContentText("Service Running...")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(MAIN_SERVICE_CHANNEL_ID, notification);
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        return null;
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
        stopSelf();
        super.onDestroy();
    }
}