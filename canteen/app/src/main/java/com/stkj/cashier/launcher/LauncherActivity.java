package com.stkj.cashier.launcher;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.stkj.cashier.MainApplication;
import com.stkj.cashier.base.device.DeviceManager;
import com.stkj.cashier.base.model.BaseNetResponse;
import com.stkj.cashier.base.model.StatNetResponse;
import com.stkj.cashier.base.net.ParamsUtils;
import com.stkj.cashier.base.utils.EncryptUtils;
import com.stkj.cashier.base.utils.LauncherUtils;
import com.stkj.cashier.home.ui.activity.MainActivity;
import com.stkj.cashier.home.ui.activity.MainActivity1;
import com.stkj.cashier.setting.model.IntervalCardTypeBean;
import com.stkj.cashier.setting.service.SettingService;
import com.stkj.cashier.stat.model.ConsumeStatBean;
import com.stkj.cashier.stat.service.StatService;
import com.stkj.common.log.LogHelper;
import com.stkj.common.net.retrofit.RetrofitManager;
import com.stkj.common.rx.AutoDisposeUtils;
import com.stkj.common.rx.DefaultObserver;
import com.stkj.common.rx.RxTransformerUtils;
import com.stkj.common.utils.DisplayUtils;

import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

/**
 * 启动屏幕
 */
public class LauncherActivity extends AppCompatActivity {

    public final static String TAG = "LauncherActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (LauncherUtils.needFinishLauncher(this)) {
            finish();
            return;
        }

        getIntervalCardType();
        //跳转首页
        //判断是否支持自定义display
        Display mainDisplay = DisplayUtils.getIndexDisplay(1);
        if (mainDisplay != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                try {
                    Intent mainIntent = getMainActivityIntent();
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                    ActivityOptions options = ActivityOptions.makeBasic();
                    options.setLaunchDisplayId(mainDisplay.getDisplayId());
                    startActivity(mainIntent, options.toBundle());
                    finish();
                    LogHelper.print("--LauncherActivity--setLaunchDisplayId success");
                } catch (Throwable e) {
                    e.printStackTrace();
                    LogHelper.print("--LauncherActivity--setLaunchDisplayId error");
                    startMainActivity();
                }
            } else {
                startMainActivity();
            }
        } else {
            startMainActivity();
        }
    }

    private void getIntervalCardType() {
        TreeMap<String, String> map = new TreeMap<>();
        String deviceId = DeviceManager.INSTANCE.getDeviceInterface().getMachineNumber();
        map.put("mode", "GetIntervalCardType");
        map.put("machine_Number", deviceId);
        String md5 = EncryptUtils.encryptMD5ToString16(deviceId);
        map.put("sign", md5);
        RetrofitManager.INSTANCE.getDefaultRetrofit()
                .create(SettingService.class)
                .getIntervalCardType(ParamsUtils.signSortParamsMap(map))
                .compose(RxTransformerUtils.mainSchedulers())
                .to(AutoDisposeUtils.onDestroyDispose(this))
                .subscribe(new DefaultObserver<BaseNetResponse<List<IntervalCardTypeBean>>>() {
                    @Override
                    protected void onSuccess(BaseNetResponse<List<IntervalCardTypeBean>> statData) {
                        Log.d(TAG, "lime == getIntervalCardType: " + (new Gson()).toJson(statData));

                        if (statData != null && statData.getCode().equals("10000") && statData.getData() != null) {
                            MainApplication.intervalCardType = statData.getData();
                        } else {


                        }


                    }
                });

    }

    private void startMainActivity() {
        startActivity(getMainActivityIntent());
        finish();
        LogHelper.print("--LauncherActivity--startMainActivity 9999999999999999");
    }

    private Intent getMainActivityIntent() {
        int deviceMainStyle = 1;
        Intent mainIntent;
        if (deviceMainStyle == 1) {
            mainIntent = new Intent(this, MainActivity1.class);
        } else {
            mainIntent = new Intent(this, MainActivity.class);
        }
        return mainIntent;
    }

    @Override
    public void finish() {
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        super.finish();
    }
}