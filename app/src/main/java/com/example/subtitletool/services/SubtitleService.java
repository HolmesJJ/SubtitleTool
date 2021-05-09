package com.example.subtitletool.services;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.example.subtitletool.constants.Constants;
import com.example.subtitletool.utils.ContextUtils;

public class SubtitleService extends Service {

    private static final String TAG = SubtitleService.class.getSimpleName();

    private WindowManager windowManager;
    private LinearLayout linearLayout;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction().equals(Constants.SUBTITLE_SERVICE_START)) {
            initLayout();
            // 系统被杀死后将尝试重新创建服务
            return START_STICKY;
        }  else {
            windowManager.removeView(linearLayout);
            stopSelf();
            // 系统被终止后将不会尝试重新创建服务
            return START_NOT_STICKY;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        windowManager.removeView(linearLayout);
        stopSelf();
        super.onDestroy();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initLayout() {
        linearLayout = new LinearLayout(ContextUtils.getContext());
        LinearLayout.LayoutParams linearLayoutLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        linearLayout.setBackgroundColor(Color.BLUE);
        linearLayout.setLayoutParams(linearLayoutLayoutParams);

        final WindowManager.LayoutParams windowManagerLayoutParams = new WindowManager.LayoutParams(400, 150,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        windowManagerLayoutParams.x = 0;
        windowManagerLayoutParams.y = 0;
        windowManagerLayoutParams.gravity = Gravity.CENTER;
        windowManager.addView(linearLayout, windowManagerLayoutParams);

        linearLayout.setOnTouchListener(new View.OnTouchListener() {
            private final WindowManager.LayoutParams newWindowManagerLayoutParams = windowManagerLayoutParams;
            int newX, newY;
            float touchedX, touchedY;
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        newX = newWindowManagerLayoutParams.x;
                        newY = newWindowManagerLayoutParams.y;
                        touchedX = motionEvent.getRawX();
                        touchedY = motionEvent.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        newWindowManagerLayoutParams.x = (int) (newX + (motionEvent.getRawX() - touchedX));
                        newWindowManagerLayoutParams.y = (int) (newY + (motionEvent.getRawY() - touchedY));
                        windowManager.updateViewLayout(linearLayout, newWindowManagerLayoutParams);
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
    }
}
