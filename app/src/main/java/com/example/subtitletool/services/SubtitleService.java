package com.example.subtitletool.services;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.subtitletool.R;
import com.example.subtitletool.constants.Constants;
import com.example.subtitletool.stt.baidu.RecogResult;
import com.example.subtitletool.stt.baidu.STTHelper;
import com.example.subtitletool.stt.baidu.listeners.STTListener;
import com.example.subtitletool.utils.ContextUtils;
import com.example.subtitletool.utils.ToastUtils;

import java.util.Arrays;

public class SubtitleService extends Service implements STTListener {

    private static final String TAG = SubtitleService.class.getSimpleName();

    private WindowManager windowManager;
    private View vSubtitle;
    private TextView tvSubtitle;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        STTHelper.getInstance().initSTT(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction().equals(Constants.SUBTITLE_SERVICE_START)) {
            STTHelper.getInstance().start();
            STTHelper.getInstance().setSpeaking(true);
            initLayout();
            // 系统被杀死后将尝试重新创建服务
            return START_STICKY;
        }  else {
            STTHelper.getInstance().setSpeaking(false);
            STTHelper.getInstance().stop();
            windowManager.removeView(vSubtitle);
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
        windowManager.removeView(vSubtitle);
        stopSelf();
        super.onDestroy();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initLayout() {
        vSubtitle = LayoutInflater.from(this).inflate(R.layout.layout_subtitle, null);
        tvSubtitle = (TextView) vSubtitle.findViewById(R.id.tv_subtitle);

        final WindowManager.LayoutParams windowManagerLayoutParams = new WindowManager.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 150,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        windowManagerLayoutParams.x = 0;
        windowManagerLayoutParams.y = 0;
        windowManagerLayoutParams.gravity = Gravity.CENTER;
        windowManager.addView(vSubtitle, windowManagerLayoutParams);

        vSubtitle.setOnTouchListener(new View.OnTouchListener() {
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
                        windowManager.updateViewLayout(vSubtitle, newWindowManagerLayoutParams);
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
    }

    // STT
    // 引擎准备完毕
    @Override
    public void onSTTAsrReady() {
        Log.i(TAG, "onSTTAsrReady");
    }

    @Override
    public void onSTTAsrBegin() {
        Log.i(TAG, "onSTTAsrBegin");
    }

    @Override
    public void onSTTAsrEnd() {
        Log.i(TAG, "onSTTAsrEnd");
    }

    @Override
    public void onSTTAsrPartialResult(String[] results, RecogResult recogResult) {
        Log.i(TAG, "onSTTAsrPartialResult results: " + Arrays.toString(results) + ", recogResult: " + recogResult.toString());
        tvSubtitle.setText(results[0]);
    }

    @Override
    public void onSTTAsrOnlineNluResult(String nluResult) {
        Log.i(TAG, "onSTTAsrOnlineNluResult nluResult: " + nluResult);
    }

    @Override
    public void onSTTAsrFinalResult(String[] results, RecogResult recogResult) {
        Log.i(TAG, "onSTTAsrFinalResult results: " + Arrays.toString(results) + ", recogResult: " + recogResult.toString());
        tvSubtitle.setText(results[0]);
    }

    @Override
    public void onSTTAsrFinish(RecogResult recogResult) {
        Log.i(TAG, "onSTTAsrFinish recogResult: " + recogResult.toString());
    }

    @Override
    public void onSTTAsrFinishError(int errorCode, int subErrorCode, String descMessage, RecogResult recogResult) {
        Log.i(TAG, "onSTTAsrFinishError errorCode: "+ errorCode + ", subErrorCode: " + subErrorCode + ", " + descMessage +", recogResult: " + recogResult.toString());
    }

    @Override
    public void onSTTAsrLongFinish() {
        Log.i(TAG, "onSTTAsrLongFinish");
    }

    @Override
    public void onSTTAsrVolume(int volumePercent, int volume) {
        Log.i(TAG, "onSTTAsrVolume 音量百分比" + volumePercent + " ; 音量" + volume);
    }

    @Override
    public void onSTTAsrAudio(byte[] data, int offset, int length) {
        Log.i(TAG, "onSTTAsrAudio 音频数据回调, length:" + data.length);
    }

    // 结束识别
    @Override
    public void onSTTAsrExit() {
        Log.i(TAG, "onSTTAsrExit");
        STTHelper.getInstance().setSpeaking(false);
        STTHelper.getInstance().stop();
    }

    @Override
    public void onSTTOfflineLoaded() {
        Log.i(TAG, "onSTTOfflineLoaded");
    }

    @Override
    public void onSTTOfflineUnLoaded() {
        Log.i(TAG, "onSTTOfflineUnLoaded");
    }

}
