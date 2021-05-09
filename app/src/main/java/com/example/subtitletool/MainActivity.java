package com.example.subtitletool;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.view.View;

import com.example.subtitletool.base.BaseActivity;
import com.example.subtitletool.constants.Constants;
import com.example.subtitletool.databinding.ActivityMainBinding;
import com.example.subtitletool.listener.OnMultiClickListener;
import com.example.subtitletool.utils.ContextUtils;
import com.example.subtitletool.utils.ListenerUtils;
import com.example.subtitletool.utils.ToastUtils;

public class MainActivity extends BaseActivity<ActivityMainBinding, MainViewModel> {

    private static final String TAG = MainActivity.class.getSimpleName();

    private MediaProjectionManager mediaProjectionManager;

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_main;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public Class<MainViewModel> getViewModelClazz() {
        return MainViewModel.class;
    }

    @Override
    public void initData() {
        super.initData();
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        mBinding.btnStopCapturing.setEnabled(false);
        setClickListener();
    }

    @Override
    protected void onDestroy() {
        stopMainService();
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.MEDIA_PROJECTION_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                ToastUtils.showShortSafe("MediaProjection permission obtained. Foreground service will be started to capture audio.");
                mBinding.btnStartCapturing.setEnabled(false);
                mBinding.btnStopCapturing.setEnabled(true);
                startMainService(data);
            } else {
                ToastUtils.showShortSafe("Request to obtain MediaProjection denied.");
            }
        }
    }

    private void setClickListener() {
        ListenerUtils.setOnClickListener(mBinding.btnStartCapturing, new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                startMediaProjectionRequest();
            }
        });

        ListenerUtils.setOnClickListener(mBinding.btnStopCapturing, new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                stopMainService();
                mBinding.btnStartCapturing.setEnabled(true);
                mBinding.btnStopCapturing.setEnabled(false);
            }
        });
    }

    public void startMainService(Intent data) {
        Intent serviceIntent = new Intent(ContextUtils.getContext(), MainService.class);
        serviceIntent.setAction(Constants.MAIN_SERVICE_START);
        serviceIntent.putExtra(Constants.MAIN_SERVICE_EXTRA_RESULT_DATA, data);
        startForegroundService(serviceIntent);
    }

    public void stopMainService() {
        Intent serviceIntent = new Intent(ContextUtils.getContext(), MainService.class);
        serviceIntent.setAction(Constants.MAIN_SERVICE_STOP);
        stopService(serviceIntent);
    }

    /**
     * Before a capture session can be started, the capturing app must
     * call MediaProjectionManager.createScreenCaptureIntent().
     * This will display a dialog to the user, who must tap "Start now" in order for a
     * capturing session to be started. This will allow both video and audio to be captured.
     */
    private void startMediaProjectionRequest() {
        // use applicationContext to avoid memory leak on Android 10.
        // see: https://partnerissuetracker.corp.google.com/issues/139732252
        mediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(), Constants.MEDIA_PROJECTION_REQUEST_CODE);
    }
}