package com.stkj.cashier.base.net;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.stkj.cashier.base.callback.AppNetCallback;
import com.stkj.cashier.base.model.AppNetInitResponse;
import com.stkj.cashier.base.model.ShopInitInfo;
import com.stkj.cashier.base.service.AppService;
import com.stkj.common.log.LogHelper;
import com.stkj.common.net.retrofit.RetrofitManager;
import com.stkj.common.rx.DefaultObserver;
import com.stkj.common.rx.RxTransformerUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * app 网络管理类
 */
public enum AppNetManager {
    INSTANCE;
    public static final String API_TEST_URL = "http://101.43.252.67:9003";
    public static final String API_OFFICIAL_URL = "http://101.42.54.44:9003";
    private AppOkhttpIntercept appOkhttpIntercept;
    private AppRetrofitJsonConvertListener retrofitJsonConvertListener;
    private ShopInitInfo mShopInitInfo;
    private boolean isRequestingDeviceDomain;
    private Set<AppNetCallback> netCallbackSet = new HashSet<>();

    public AppOkhttpIntercept getAppOkhttpHttpIntercept() {
        if (appOkhttpIntercept == null) {
            appOkhttpIntercept = new AppOkhttpIntercept();
        }
        return appOkhttpIntercept;
    }

    public AppRetrofitJsonConvertListener getRetrofitJsonConvertListener() {
        if (retrofitJsonConvertListener == null) {
            retrofitJsonConvertListener = new AppRetrofitJsonConvertListener();
        }
        return retrofitJsonConvertListener;
    }



    public String getDeviceDomain() {
        return mShopInitInfo == null ? "" : mShopInitInfo.getDomain();
    }

    public String getShopId() {
        return mShopInitInfo == null ? "" : mShopInitInfo.getShopId();
    }

    public String getDeviceId() {
        return mShopInitInfo == null ? "" : mShopInitInfo.getDeviceId();
    }

    public void clearAppNetCache() {
        mShopInitInfo = null;
        isRequestingDeviceDomain = false;
        RetrofitManager.INSTANCE.removeAllRetrofit();
    }

    public boolean isRequestingDeviceDomain() {
        return isRequestingDeviceDomain;
    }

    public void addNetCallback(@NonNull AppNetCallback appNetCallback) {
        netCallbackSet.add(appNetCallback);
    }

    public void removeNetCallback(@NonNull AppNetCallback appNetCallback) {
        netCallbackSet.remove(appNetCallback);
    }
}