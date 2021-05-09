package com.example.subtitletool.init;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;

import com.example.subtitletool.BR;
import com.example.subtitletool.R;
import com.example.subtitletool.base.BaseActivity;
import com.example.subtitletool.constants.Constants;
import com.example.subtitletool.databinding.ActivityInitBinding;
import com.example.subtitletool.utils.ContextUtils;
import com.example.subtitletool.utils.PermissionsUtils;

import pub.devrel.easypermissions.EasyPermissions;

public class InitActivity extends BaseActivity<ActivityInitBinding, InitViewModel> {

    private static final String TAG = InitActivity.class.getSimpleName();

    private static final String[] PERMISSIONS = new String[]{
            android.Manifest.permission.FOREGROUND_SERVICE,
            android.Manifest.permission.RECORD_AUDIO
            // 自行去Settings打开SYSTEM_ALERT_WINDOW权限
            // android.Manifest.permission.SYSTEM_ALERT_WINDOW
    };

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_init;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public Class<InitViewModel> getViewModelClazz() {
        return InitViewModel.class;
    }

    @Override
    public void initData() {
        super.initData();
        PermissionsUtils.doSomeThingWithPermission(this, () -> {
            if (mViewModel != null) {
                mViewModel.initData();
            }
        }, PERMISSIONS, Constants.PERMISSION_REQUEST_CODE, R.string.rationale_init);
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        showLoading(false);
        mViewModel.mActivityAction.observe(this, activityAction -> {
            Intent intent = new Intent(ContextUtils.getContext(), activityAction);
            startActivity(intent);
            finish();
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLoading();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected boolean onHasPermissions() {
        return EasyPermissions.hasPermissions(this, PERMISSIONS);
    }

    @Override
    protected void onPermissionSuccessCallbackFromSetting() {
        super.onPermissionSuccessCallbackFromSetting();
        if (mViewModel != null) {
            mViewModel.initData();
        }
    }
}
