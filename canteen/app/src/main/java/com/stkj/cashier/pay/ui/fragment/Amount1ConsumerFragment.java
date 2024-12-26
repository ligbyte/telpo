package com.stkj.cashier.pay.ui.fragment;

import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.stkj.cashier.R;
import com.stkj.cashier.base.callback.DispatchKeyEventListener;
import com.stkj.cashier.base.callback.OnConsumerConfirmListener;
import com.stkj.cashier.base.tts.TTSVoiceHelper;
import com.stkj.cashier.base.utils.CommonDialogUtils;
import com.stkj.cashier.consumer.ConsumerManager;
import com.stkj.cashier.pay.callback.OnCalculateListener;
import com.stkj.cashier.pay.callback.OnPayListener;
import com.stkj.cashier.pay.model.ModifyBalanceResult;
import com.stkj.cashier.pay.ui.weight.Simple1Calculator;
import com.stkj.cashier.setting.data.PaymentSettingMMKV;
import com.stkj.cashier.setting.model.FacePassPeopleInfo;
import com.stkj.common.core.ActivityHolderFactory;
import com.stkj.common.log.LogHelper;

import java.util.Map;

/**
 * 金额模式(主页样式1)
 */
public class Amount1ConsumerFragment extends BasePayHelperFragment implements OnPayListener, OnConsumerConfirmListener, DispatchKeyEventListener {

    public final static String TAG = "Amount1ConsumerFragment";
    private Simple1Calculator sc1Calc;
    private TextView tvTitle;
    private boolean isViewVisibleOnScreen = true;
    private boolean mIsRefund = false;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_amount1_consumer;
    }

    @Override
    protected void initViews(View rootView) {
        sc1Calc = (Simple1Calculator) findViewById(R.id.sc1_calc);
        tvTitle = (TextView) findViewById(R.id.tvTitle);

        sc1Calc.setOnCalculateListener(new OnCalculateListener() {
            @Override
            public void onConfirmMoney(String payMoney) {
                goToAmountPay(payMoney);
            }


        });


        sc1Calc.setRefundListener(new Simple1Calculator.RefundListener() {
            @Override
            public void refund() {
               showRefundList();
            }
        });


    }

    private void showRefundList() {
        boolean switchFacePassPay = PaymentSettingMMKV.getSwitchFacePassPay();
        if (switchFacePassPay) {
            speakTTSVoice("请用户刷脸或刷卡，以确认可退款订单");
            tvTitle.setText("请刷脸或刷卡，以确认可退款订单");
        } else {
            speakTTSVoice("请用户刷卡，以确认可退款订单");
            tvTitle.setText("请刷卡，以确认可退款订单");
        }
        sc1Calc.setVisibility(View.GONE);


        mIsRefund = true;
        // TODO: EventBus AmountRefund
        // EventBus.getDefault().post(new MessageEventBean(MessageEventType.AmountRefund));
    }



    @Override
    public void onCancelCardNumber(String cardNumber) {
        super.onCancelCardNumber(cardNumber);

    }

    @Override
    public void onCancelFacePass(FacePassPeopleInfo passPeopleInfo) {
        super.onCancelFacePass(passPeopleInfo);

    }

    /**
     * 金额结算
     */
    private void goToAmountPay(String realPayMoney) {
        ConsumerManager.INSTANCE.resetFaceConsumerLayout();
        int goToPay = goToPay(realPayMoney);
        if (goToPay != NORMAL_TO_PAY) {
            CommonDialogUtils.showTipsDialog(mActivity, getGoToPayStatus(goToPay));
        } else {
            sc1Calc.setCalcEnable(false);
        }
    }

    /**
     * 取消金额结算
     */
    private void stopAmountPay() {
        speakTTSVoice("取消结算");
        stopToPay();
        sc1Calc.setCalcEnable(true);
    }

    @Override
    protected void onFragmentResume(boolean isFirstOnResume) {
        if (isFirstOnResume) {
            //FragmentUtils.safeReplaceFragment(getChildFragmentManager(), new ConsumerRecordListFragment(), R.id.fl_consumer_list_content);
        }
    }

    @Override
    public void onPayError(String responseCode, Map<String, String> payRequest, @Nullable ModifyBalanceResult modifyBalanceResult, String msg) {
        super.onPayError(responseCode, payRequest, modifyBalanceResult, msg);
        sc1Calc.setCalcEnable(true);
    }

    @Override
    public void onPaySuccess(Map<String, String> payRequest, ModifyBalanceResult modifyBalanceResult) {
        super.onPaySuccess(payRequest, modifyBalanceResult);
        sc1Calc.clearCalcData();
        sc1Calc.setCalcEnable(true);
    }

    @Override
    protected void onPayCancel(int payType) {
        stopAmountPay();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        stopToPay();
    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        LogHelper.print("---Amount1ConsumerFragment--dispatchKeyEvent--activity event: " + event);
        int action = event.getAction();
        if (action == KeyEvent.ACTION_UP) {
            if (event.getKeyCode() == KeyEvent.KEYCODE_DEL && (mIsRefund == true)) {
                tvTitle.setText("当前订单");
                sc1Calc.setVisibility(View.VISIBLE);
                mIsRefund = false;
                return true;
            }

            if (sc1Calc.isCalculatorEnable()) {
                sc1Calc.dispatchKeyEvent(event);
                return true;
            } else {
                int keyCode = event.getKeyCode();
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    stopAmountPay();
                } else {
                    speakTTSVoice("请先点击清除按钮");
                }
            }
        }
        return false;
    }

    public Simple1Calculator getSc1Calc() {
        return sc1Calc;
    }

    /**
     * 语音提醒
     */
    protected void speakTTSVoice(String words) {
        if (TextUtils.isEmpty(words)) {
            return;
        }
        TTSVoiceHelper ttsVoiceHelper = ActivityHolderFactory.get(TTSVoiceHelper.class, getContext());
        if (ttsVoiceHelper != null) {
            ttsVoiceHelper.speakByTTSVoice(words);
        }
    }
}
