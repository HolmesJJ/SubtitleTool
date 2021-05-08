package com.example.subtitletool;

import android.content.Intent;
import android.os.Bundle;

import androidx.core.content.ContextCompat;

import com.example.subtitletool.base.BaseActivity;
import com.example.subtitletool.databinding.ActivityMainBinding;
import com.example.subtitletool.utils.ContextUtils;

public class MainActivity extends BaseActivity<ActivityMainBinding, MainViewModel> {

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
        startService();
    }

    public void startService() {
        Intent serviceIntent = new Intent(ContextUtils.getContext(), MainService.class);
        ContextCompat.startForegroundService(ContextUtils.getContext(), serviceIntent);
    }
    public void stopService() {
        Intent serviceIntent = new Intent(ContextUtils.getContext(), MainService.class);
        stopService(serviceIntent);
    }

    @Override
    protected void onDestroy() {
        stopService();
        super.onDestroy();
    }
}