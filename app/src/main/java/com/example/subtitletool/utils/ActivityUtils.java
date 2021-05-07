package com.example.subtitletool.utils;

import android.app.Activity;
import android.os.Handler;

import com.example.subtitletool.ui.widget.dialog.LoadingDialog;

public class ActivityUtils {

    public static void appExitDelayed(long milliseconds){

        Activity context = AppManagerUtils.getAppManager().popActivity();
        Handler handler = new Handler(ContextUtils.getContext().getMainLooper());
        if(context != null) {
            final LoadingDialog loadingDialog = new LoadingDialog(context);
            loadingDialog.showLoading();


            AppManagerUtils.getAppManager().callbackExitCallbacks();
            // 提交退出信息
            handler.postDelayed(new Runnable() {

                @Override
                public void run() {

                    loadingDialog.dismiss();
                    AppManagerUtils.getAppManager().appExit();
                }
            }, milliseconds);
        }else{
            AppManagerUtils.getAppManager().callbackExitCallbacks();
            handler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    AppManagerUtils.getAppManager().appExit();
                }
            }, milliseconds);
        }
    }
}
