package com.example.subtitletool.init;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.OnLifecycleEvent;

import com.example.subtitletool.MainActivity;
import com.example.subtitletool.base.BaseViewModel;

public class InitViewModel extends BaseViewModel {

    public MutableLiveData<Class> mActivityAction = new MutableLiveData<>();

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    private void onCreate() {

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    protected void onDestroy() {

    }

    public void initData() {
        doInitSuccess();
    }

    private void doInitSuccess() {
        mActivityAction.postValue(MainActivity.class);
    }
}
