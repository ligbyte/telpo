package com.stkj.cashier.pay.ui.weight;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Choreographer;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.jakewharton.processphoenix.ProcessPhoenix;
import com.stkj.cashier.MainApplication;
import com.stkj.cashier.R;
import com.stkj.cashier.base.callback.DispatchKeyEventListener;
import com.stkj.cashier.base.device.DeviceManager;
import com.stkj.cashier.base.tts.TTSVoiceHelper;
import com.stkj.cashier.base.utils.CommonDialogUtils;
import com.stkj.cashier.pay.callback.OnCalculateListener;
import com.stkj.common.core.ActivityHolderFactory;
import com.stkj.common.log.LogHelper;
import com.stkj.common.ui.toast.AppToast;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * 简易计算器(主页样式1)
 */
public class Simple1Calculator extends FrameLayout implements DispatchKeyEventListener {

    public final static String TAG = "Simple1Calculator";

    private static final BigDecimal MAX_VALUE = new BigDecimal(99999999);
    private static final BigDecimal MIN_VALUE = new BigDecimal(0);
    private static final String SPLITE_STRING = " ";
    private static final String PLUS_OPERATE = "+";
    private static final String MINUS_OPERATE = "-";
    private static final String[] OPERATE_ALL = {PLUS_OPERATE, MINUS_OPERATE};
    private TextView tvConsume;
    private ScrollView svConsume;
    private KeyboardListener keyboardListener;
    private RefundListener refundListener;
    private OnCalculateListener onCalculateListener;
    private boolean isCalculatorEnable = true;


    public Simple1Calculator(@NonNull Context context) {
        super(context);
        init(context);
    }

    public Simple1Calculator(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public Simple1Calculator(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.include_simple1_calculator, this);
        tvConsume = (TextView) findViewById(R.id.tv_consume);
        svConsume = (ScrollView) findViewById(R.id.sv_calc);
        tvConsume.addOnLayoutChangeListener(new OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                int svConsumeHeight = svConsume.getHeight();
                int tvConsumeHeight = tvConsume.getHeight();
                if (svConsumeHeight > 0 && tvConsumeHeight > svConsumeHeight) {
                    Choreographer.getInstance().postFrameCallback(new Choreographer.FrameCallback() {
                        @Override
                        public void doFrame(long frameTimeNanos) {
                            svConsume.fullScroll(View.FOCUS_DOWN);
                        }
                    });
                }
                LogHelper.print("---onLayoutChange---scrollHeight:" + svConsumeHeight + " tvConsumeHeight:" + tvConsumeHeight);
            }
        });
        svConsume.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int halfHeight = (svConsume.getHeight() - tvConsume.getHeight()) / 2;
                svConsume.setPadding(0, halfHeight, 0, halfHeight);
                svConsume.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        LogHelper.print("---Simple1Calculator--dispatchKeyEvent--activity event: " + event);
        int action = event.getAction();
        if (action == KeyEvent.ACTION_UP) {
            int keyCode = event.getKeyCode();
            String speakWords = "";
            Log.d(TAG, "==keyCode : " + keyCode);
            switch (keyCode) {
                case KeyEvent.KEYCODE_0:
                    speakWords = "0";
                    inputNumber("0");
                    break;
                case KeyEvent.KEYCODE_1:
                    speakWords = "1";
                    inputNumber("1");
                    break;
                case KeyEvent.KEYCODE_2:
                    speakWords = "2";
                    inputNumber("2");
                    break;
                case KeyEvent.KEYCODE_3:
                    speakWords = "3";
                    inputNumber("3");
                    break;
                case KeyEvent.KEYCODE_4:
                    speakWords = "4";
                    inputNumber("4");
                    break;
                case KeyEvent.KEYCODE_5:
                    speakWords = "5";
                    inputNumber("5");
                    break;
                case KeyEvent.KEYCODE_6:
                    speakWords = "6";
                    inputNumber("6");
                    break;
                case KeyEvent.KEYCODE_7:
                    speakWords = "7";
                    inputNumber("7");
                    break;
                case KeyEvent.KEYCODE_8:
                    speakWords = "8";
                    inputNumber("8");
                    break;
                case KeyEvent.KEYCODE_9:
                    speakWords = "9";
                    inputNumber("9");
                    break;
                case android.view.KeyEvent.KEYCODE_PERIOD:
                    speakWords = "点";
                    //小数点
                    inputSpot();
                    break;
                case android.view.KeyEvent.KEYCODE_NUMPAD_ADD:
                    speakWords = "加";
                    //加号
                    inputOperate(PLUS_OPERATE);
                    break;
                case android.view.KeyEvent.KEYCODE_MINUS:
                    speakWords = "减";
                    //减号
                    inputOperate(MINUS_OPERATE);
                    break;
                case android.view.KeyEvent.KEYCODE_DEL:
                    speakWords = "清除";
                    //清除|回退键
                    deleteInput();
                    break;
                case android.view.KeyEvent.KEYCODE_ENTER:
                    //确认
                    confirmOperateNumberResult();
                    break;
                case android.view.KeyEvent.KEYCODE_DPAD_UP:
                    //向上
                    svConsume.scrollBy(0, -getConsumeTextSize());
                    break;
                case android.view.KeyEvent.KEYCODE_DPAD_DOWN:
                    //向下
                    svConsume.scrollBy(0, getConsumeTextSize());
                    break;
                case android.view.KeyEvent.KEYCODE_F1:
                    speakWords = "功能";
                    if (keyboardListener != null){
                        Log.d(TAG, "lime== settings: 179");
                        keyboardListener.settings();
                    }
                    break;

                case android.view.KeyEvent.KEYCODE_BACK:
                    if (refundListener != null){
                        refundListener.refund();
                    }
                    break;
                case android.view.KeyEvent.KEYCODE_F2:
                    speakWords = "统计";
                    if (keyboardListener != null){
                        Log.d(TAG, "lime==== settings: 192");
                        keyboardListener.stat();
                    }
                    break;
            }
            speakTTSVoice(speakWords);
        }
        return super.dispatchKeyEvent(event);
    }

    private int getConsumeTextSize() {
        return (int) tvConsume.getPaint().getTextSize();
    }

    /**
     * 获取当前输入的数值
     */
    public String getCurrentInputText() {
        return tvConsume.getText().toString();
    }

    private void confirmOperateNumberResult() {
        String currentInputText = getCurrentInputText();
        if (TextUtils.isEmpty(currentInputText)) {
            return;
        }

        if(currentInputText.equals("88888888")){
//            ((Activity)getContext()).finishAffinity();
            try {
                //android.os.Process.killProcess(android.os.Process.myPid());
                DeviceManager.INSTANCE.getDeviceInterface().showOrHideSysNavBar(false);
                System.exit(0);
            }catch (Exception e){

            }
            return;
        }


        if(currentInputText.equals("99999999")){
            Log.d(TAG, "lime confirmOperateNumberResult: 99999999");

            return;
        }

        //分割最后的输入的数据
        String[] splitInput = currentInputText.split(SPLITE_STRING);
        if (splitInput.length > 2) {
            //获取最后一个公式
            String lastOperateNumber = splitInput[splitInput.length - 1];
            //如果最后是操作符则不进行运算
            int needOperateLength = 0;
            if (isOperate(lastOperateNumber)) {
                needOperateLength = splitInput.length - 1;
            } else {
                needOperateLength = splitInput.length;
            }
            StringBuilder lastFormulaBuilder = new StringBuilder();
            String tempLastResult = "";
            String tempLastOperate = "";
            for (int i = 0; i < needOperateLength; i++) {
                String s = splitInput[i];
                int pointIndex = s.indexOf(".");
                if (pointIndex != -1 && pointIndex + 1 == s.length()) {
                    lastFormulaBuilder.append(s.substring(0, pointIndex)).append(SPLITE_STRING);
                } else {
                    lastFormulaBuilder.append(s).append(SPLITE_STRING);
                }
                if (!isOperate(s)) {
                    if (TextUtils.isEmpty(tempLastResult)) {
                        //第一次赋值result
                        tempLastResult = s;
                        continue;
                    }
                    if (!TextUtils.isEmpty(tempLastOperate)) {
                        //有操作符去计算结果
                        try {
                            double numberResult = 0;
                            BigDecimal lastNumberValue = new BigDecimal(tempLastResult);
                            BigDecimal currentNumberValue = new BigDecimal(s);
                            switch (tempLastOperate) {
                                case PLUS_OPERATE:
                                    numberResult = lastNumberValue.add(currentNumberValue).doubleValue();
                                    break;
                                case MINUS_OPERATE:
                                    numberResult = lastNumberValue.subtract(currentNumberValue).doubleValue();
                                    break;
                            }
                            LogHelper.print("---SimpleCalculator---lastNumberValue: " + lastNumberValue + "--currentNumberValue: " + currentNumberValue + "--numberResult: " + numberResult);
                            tempLastResult = String.valueOf(numberResult);
                            tempLastOperate = "";
                        } catch (Throwable e) {
                            e.printStackTrace();
                            tempLastResult = "0";
                            tempLastOperate = "";
                            CommonDialogUtils.showTipsDialog(getContext(), "计算出错了:" + e.getMessage());
                        }
                    }
                } else {
                    tempLastOperate = s;
                }
            }
            BigDecimal result = new BigDecimal(tempLastResult);
            BigDecimal max = result.max(MAX_VALUE);
            if (Objects.equals(max, result)) {
                AppToast.toastMsg("超出最大数99999999");
                return;
            }
            BigDecimal min = result.min(MIN_VALUE);
            if (Objects.equals(min, result)) {
                AppToast.toastMsg("低于最小数0");
                return;
            }
            String mLastResult = result.stripTrailingZeros().toPlainString();
            tvConsume.setText(mLastResult);
            if (onCalculateListener != null) {
                onCalculateListener.onConfirmMoney(mLastResult);
            }
        } else {
            if (splitInput.length >= 1) {
                String mLastResult = splitInput[0];
                tvConsume.setText(mLastResult);
                if (onCalculateListener != null) {
                    onCalculateListener.onConfirmMoney(mLastResult);
                }
            }
        }
    }

    /**
     * 获取最后的操作符或者数字
     */
    private String getLastOperateOrNumber() {
        String currentInputText = getCurrentInputText();
        if (TextUtils.isEmpty(currentInputText)) {
            LogHelper.print("---SimpleCalculator-getLastOperateOrNumber currentInputText is null");
            return "";
        }
        String[] split = currentInputText.split(SPLITE_STRING);
        if (split.length > 0) {
            String endOperateOrNumber = split[split.length - 1];
            LogHelper.print("---SimpleCalculator-getLastOperateOrNumber split endOperateOrNumber = " + endOperateOrNumber);
            return endOperateOrNumber;
        }
        LogHelper.print("---SimpleCalculator-getLastOperateOrNumber split is null");
        return "";
    }

    /**
     * 是否是操作度
     */
    private boolean isOperate(String operateOrNumber) {
        for (String operate : OPERATE_ALL) {
            if (TextUtils.equals(operateOrNumber, operate)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 输入小数点
     */
    private void inputSpot() {
        try {
            String lastOperateOrNumber = getLastOperateOrNumber();
            if (!TextUtils.isEmpty(lastOperateOrNumber) && !isOperate(lastOperateOrNumber)) {
                if (!lastOperateOrNumber.contains(".")) {
                    inputFormula(".");
                }
            } else {
                //当前没有任何输入直接插入0.
                String currentInputText = getCurrentInputText();
                if (TextUtils.isEmpty(currentInputText)) {
                    inputFormula("0.");
                } else {
                    inputFormula(SPLITE_STRING + "0.");
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
            CommonDialogUtils.showTipsDialog(getContext(), "输入小数点出错了:" + e.getMessage());
        }
    }

    /**
     * 删除输入
     */
    private void deleteInput() {
        try {
            StringBuilder inputBuilder = new StringBuilder(getCurrentInputText());
            if (inputBuilder.length() > 0) {
                //删除最后的一个字符
                int length = inputBuilder.length();
                inputBuilder.delete(length - 1, inputBuilder.length());
                //删除最后字符,判断最后一个字符为空格则直接删除
                int afterLength = inputBuilder.length();
                if (afterLength > 0) {
                    String lastString = inputBuilder.substring(afterLength - 1);
                    if (SPLITE_STRING.equals(lastString)) {
                        inputBuilder.delete(afterLength - 1, inputBuilder.length());
                    }
                }
                tvConsume.setText(inputBuilder.toString());
            }
        } catch (Throwable e) {
            e.printStackTrace();
            CommonDialogUtils.showTipsDialog(getContext(), "删除出错了:" + e.getMessage());
        }
    }

    private void inputFormula(String operateOrNumber) {
        String currentInputText = getCurrentInputText();
        if (!TextUtils.isEmpty(currentInputText)) {
            tvConsume.setText(currentInputText + operateOrNumber);
        } else {
            tvConsume.setText(operateOrNumber);
        }
    }

    /**
     * 输入操作符
     */
    private void inputOperate(String operate) {
        try {
            String lastOperateOrNumber = getLastOperateOrNumber();
            if (!TextUtils.isEmpty(lastOperateOrNumber)) {
                //当前输入框没有数字则不能输入
                //当前输入的是操作符，先插入分隔符再插入数字
                if (isOperate(lastOperateOrNumber)) {
                    //判断如果最后是操作符 则替换成当前输入的操作符
                    //删除最后的操作符
                    deleteInput();
                    inputFormula(SPLITE_STRING + operate);
                } else {
                    //如果结尾是小数点 则先删除小数点
                    if (lastOperateOrNumber.endsWith(".")) {
                        deleteInput();
                        inputFormula(SPLITE_STRING + operate);
                    } else {
                        inputFormula(SPLITE_STRING + operate);
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
            CommonDialogUtils.showTipsDialog(getContext(), "输入操作符出错了:" + e.getMessage());
        }
    }

    /**
     * 输入数字
     */
    private void inputNumber(String number) {
        try {
            String lastOperateOrNumber = getLastOperateOrNumber();
            if (!TextUtils.isEmpty(lastOperateOrNumber)) {
                //如果最后是操作符,先插入分隔符再插入数字
                if (isOperate(lastOperateOrNumber)) {
                    inputFormula(SPLITE_STRING + number);
                } else {
                    //判断数字长度不能超多
                    int pointIndex = lastOperateOrNumber.indexOf(".");
                    if (pointIndex != -1) {
                        //获取小数点位数 最多两位小数
                        if (lastOperateOrNumber.length() - 1 >= pointIndex + 2) {
                            return;
                        }
                        //小数可以输入最大11位
                        if (lastOperateOrNumber.length() >= 11) {
                            return;
                        }
                    } else {
                        //整数可以输入最大8位
                        if (lastOperateOrNumber.length() >= 8) {
                            return;
                        }
                        //007类似不可以输入
                        if (lastOperateOrNumber.equals("0")) {
                            //当前如果还是0 则不可以输入
                            if (!number.equals("0")) {
                                //删除一个值 插入新数字
                                deleteInput();
                                //当前没有任何输入直接插入数字
                                String currentInputText = getCurrentInputText();
                                if (TextUtils.isEmpty(currentInputText)) {
                                    //插入数字
                                    inputFormula(number);
                                } else {
                                    inputFormula(SPLITE_STRING + number);
                                }
                            }
                            return;
                        }
                    }
                    //插入数字
                    inputFormula(number);
                }
            } else {
                inputFormula(number);
            }
        } catch (Throwable e) {
            e.printStackTrace();
            CommonDialogUtils.showTipsDialog(getContext(), "输入数字出错了:" + e.getMessage());
        }
    }

    /**
     * 清空重置数据
     */
    public void clearCalcData() {
        tvConsume.setText("");
    }

    public void setCalcEnable(boolean calcEnable) {
        isCalculatorEnable = calcEnable;
    }

    public boolean isCalculatorEnable() {
        return isCalculatorEnable;
    }

    public void setOnCalculateListener(OnCalculateListener onCalculateListener) {
        this.onCalculateListener = onCalculateListener;
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

    public void setKeyboardListener(KeyboardListener keyboardListener) {
        this.keyboardListener = keyboardListener;
    }

    public static interface KeyboardListener{
        public void settings();
        public void stat();

    }


    public void setRefundListener(RefundListener refundListener) {
        this.refundListener = refundListener;
    }

    public static interface RefundListener{
        public void refund();
    }


}
