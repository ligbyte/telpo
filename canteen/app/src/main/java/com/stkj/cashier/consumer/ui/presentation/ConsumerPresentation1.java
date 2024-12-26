package com.stkj.cashier.consumer.ui.presentation;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.stkj.cashier.R;
import com.stkj.cashier.base.callback.OnConsumerConfirmListener;
import com.stkj.cashier.base.tts.TTSVoiceHelper;
import com.stkj.cashier.base.ui.widget.FacePass1CameraLayout;
import com.stkj.cashier.consumer.callback.ConsumerListener;
import com.stkj.cashier.home.ui.widget.Home1TitleLayout;
import com.stkj.cashier.setting.model.FacePassPeopleInfo;
import com.stkj.common.core.AppManager;
import com.stkj.common.log.LogHelper;
import com.stkj.common.ui.activity.BaseActivity;
import com.stkj.common.ui.toast.AppToast;

/**
 * 食堂收银客户展示页
 */
public class ConsumerPresentation1 extends BasePresentation {

    private Home1TitleLayout htlConsumer;
    private TextView tvFaceTips;
    private FacePass1CameraLayout fp1cFace;
    private TextView tvPayTips;

    public ConsumerPresentation1(Context outerContext, Display display) {
        super(outerContext, display);
        getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
    }

    public ConsumerPresentation1(Context outerContext, Display display, int theme) {
        super(outerContext, display, theme);
        getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
        Window window = getWindow();
        if (window != null) {
            WindowManager.LayoutParams attributes = getWindow().getAttributes();
            //窗口标记属性
            attributes.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            window.setAttributes(attributes);
        }
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        setContentView(R.layout.presentation1_consumer);
        findViews();
        LogHelper.print("-Consumer--getDisplayMetrics--" + getResources().getDisplayMetrics());
    }

    @Override
    public boolean dispatchKeyEvent(@NonNull KeyEvent event) {
        LogHelper.print("-Consumer--dispatchKeyEvent--" + event);
        return super.dispatchKeyEvent(event);
    }

    private void findViews() {
        htlConsumer = (Home1TitleLayout) findViewById(R.id.htl_consumer);
        tvFaceTips = (TextView) findViewById(R.id.tv_face_tips);
        fp1cFace = (FacePass1CameraLayout) findViewById(R.id.fp1c_camera);
        tvPayTips = (TextView) findViewById(R.id.tv_pay_tips);
        if (consumerListener != null) {
            consumerListener.onCreateFacePreviewView(fp1cFace.getFacePreviewFace(), fp1cFace.getIrPreviewFace());
        }
        if (consumerListener != null) {
            consumerListener.onCreateTitleLayout(htlConsumer);
        }
    }

    public void setFacePassConfirmListener(OnConsumerConfirmListener facePassConfirmListener) {
        this.facePassConfirmListener = facePassConfirmListener;
    }

    public void setConsumerListener(ConsumerListener consumerListener) {
        this.consumerListener = consumerListener;
    }

    @Override
    public void setFacePreview(boolean preview) {
        if (fp1cFace != null) {
            fp1cFace.setPreviewFace(preview);
        }
    }

    private boolean isConsumerAuthTips;

    @Override
    public void setConsumerAuthTips(String tips) {
        if (fp1cFace != null) {
            isConsumerAuthTips = true;
            fp1cFace.setFaceCameraTips(tips);
        }
    }

    @Override
    public boolean isConsumerAuthTips() {
        return isConsumerAuthTips;
    }

    @Override
    public void setConsumerTips(String tips) {
        setConsumerTips(tips, 0);
    }

    @Override
    public void setConsumerTips(String tips, int consumerPro) {

    }

    @Override
    public void setConsumerConfirmFaceInfo(FacePassPeopleInfo facePassPeopleInfo, boolean needConfirm, int consumerType) {

    }

    @Override
    public void setConsumerConfirmCardInfo(String cardNumber, boolean needConfirm) {

    }

    @Override
    public void setConsumerConfirmScanInfo(String scanData, boolean needConfirm) {

    }

    @Override
    public void setConsumerTakeMealWay() {

    }

    @Override
    public void setNormalConsumeStatus() {

    }

    @Override
    public void setPayConsumeStatus() {

    }

    @Override
    public void setPayPrice(String payPrice, boolean showCancelPay) {

    }

    @Override
    public void setCanCancelPay(boolean showCancelPay) {

    }

    @Override
    public void resetFaceConsumerLayout() {

    }

    @Override
    public void onDisplayRemoved() {
        super.onDisplayRemoved();
        if (consumerListener != null) {
            consumerListener.onConsumerDismiss();
        }
        LogHelper.print("ConsumerPresentation--onDisplayRemoved");
    }

    @Override
    public void onDisplayChanged() {
        super.onDisplayChanged();
        if (consumerListener != null) {
            consumerListener.onConsumerChanged();
        }
        LogHelper.print("ConsumerPresentation--onDisplayChanged");
    }

    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        if (res.getConfiguration().fontScale != 1) {
            //非默认值
            Configuration newConfig = new Configuration();
            newConfig.setToDefaults();//设置默认
            res.updateConfiguration(newConfig, res.getDisplayMetrics());
        }
        return res;
    }

    @Override
    public void show() {
        try {
            super.show();
        } catch (Throwable e) {
            e.printStackTrace();
            AppToast.toastMsg("副屏初始化失败");
        }
    }

    /**
     * 语音提醒
     */
    protected void speakTTSVoice(String words) {
        Activity mainActivity = AppManager.INSTANCE.getMainActivity();
        if (mainActivity instanceof BaseActivity) {
            BaseActivity baseActivity = (BaseActivity) mainActivity;
            baseActivity.getWeakRefHolder(TTSVoiceHelper.class).speakByTTSVoice(words);
        }
    }
}
