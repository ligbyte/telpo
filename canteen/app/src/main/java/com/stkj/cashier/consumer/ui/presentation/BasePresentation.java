package com.stkj.cashier.consumer.ui.presentation;

import android.app.Presentation;
import android.content.Context;
import android.view.Display;

import com.stkj.cashier.base.callback.OnConsumerConfirmListener;
import com.stkj.cashier.consumer.callback.ConsumerController;
import com.stkj.cashier.consumer.callback.ConsumerListener;
import com.stkj.cashier.setting.model.FacePassPeopleInfo;

/**
 * 基类的presentation
 */
public class BasePresentation extends Presentation implements ConsumerController {

    protected ConsumerListener consumerListener;
    protected OnConsumerConfirmListener facePassConfirmListener;

    public void setFacePassConfirmListener(OnConsumerConfirmListener facePassConfirmListener) {
        this.facePassConfirmListener = facePassConfirmListener;
    }

    public void setConsumerListener(ConsumerListener consumerListener) {
        this.consumerListener = consumerListener;
    }

    public BasePresentation(Context outerContext, Display display) {
        super(outerContext, display);
    }

    public BasePresentation(Context outerContext, Display display, int theme) {
        super(outerContext, display, theme);
    }

    @Override
    public void setFacePreview(boolean preview) {

    }

    @Override
    public void setConsumerTips(String tips) {

    }

    @Override
    public void setConsumerTips(String tips, int consumerPro) {

    }

    @Override
    public void setConsumerAuthTips(String tips) {

    }

    @Override
    public boolean isConsumerAuthTips() {
        return false;
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
    public void resetFaceConsumerLayout() {

    }

    @Override
    public void setNormalConsumeStatus() {

    }

    @Override
    public void setPayConsumeStatus() {

    }

    @Override
    public void setPayPrice(String payPrice, boolean canCancelPay) {

    }

    @Override
    public void setCanCancelPay(boolean showCancelPay) {

    }
}
