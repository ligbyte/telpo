package com.stkj.cashier.setting.ui.fragment;

import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.Choreographer;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.stkj.cashier.MainApplication;
import com.stkj.cashier.R;
import com.stkj.cashier.base.tts.TTSVoiceHelper;
import com.stkj.cashier.base.ui.fragment.BaseDispatchKeyEventFragment;
import com.stkj.cashier.setting.data.PaymentSettingMMKV;
import com.stkj.cashier.setting.model.IntervalCardTypeBean;
import com.stkj.cashier.setting.model.RefreshPayType;
import com.stkj.common.core.ActivityHolderFactory;
import com.stkj.common.log.LogHelper;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.List;

/**
 * 定额设置
 */
public class Consumption2SettingFragment extends BaseDispatchKeyEventFragment {


    public final static String TAG = "Consumption1SettingFragment";
    private KeyboardListener keyboardListener;
    //当前页面选中状态索引
    private int currentSelectIndex = -1;
    TextView tvBreakfastAmount;
    TextView tvLunchAmount;
    TextView tvDinnerAmount;

    LinearLayout llSwitchBreakfast;
    LinearLayout llBreakfastSetting;
    LinearLayout llSwitchLunch;
    LinearLayout llLunchSetting;
    LinearLayout llSwitchDinner;
    LinearLayout llDinnerSetting;

    TextView tvBreakfastTime;
    ImageView ivSwitchBreakfast;
    TextView tvLunchTime;
    ImageView ivSwitchLunch;
    TextView tvDinnerTime;
    ImageView ivSwitchDinner;

    FrameLayout flCanteenTimeSetting;
    TextView tvSwitchBreakfast;
    TextView tvSwitchLunch;
    TextView tvSwitchDinner;
    LinearLayout llSwitchFixAmount;
    TextView tvOpenFixAmount;
    TextView tvCloseFixAmount;


    FrameLayout flTips;
    TextView tvTips;


    FrameLayout flFixAmountMode;
    FrameLayout flSwitchFacePass;
    FrameLayout flRestartApp;
    FrameLayout flShutdownDevice;
    FrameLayout flRebootDevice;
    ScrollView svContent;


    private final int secondPageSelectItemCount = 8;
    private final HashMap<String, String> fixAmountDataMap = new HashMap<String, String>();


    //当前页面索引
    private int mPageIndex = 0;

    //第一层页面总item数量
    private int firstPageSelectItemCount = 5;
    private final Runnable hidTipsTask = new Runnable() {
        @Override
        public void run() {
            flTips.setVisibility(View.GONE);
        }
    };

    @Override
    protected void initViews(View rootView) {
        super.initViews(rootView);
        llSwitchBreakfast = rootView.findViewById(R.id.ll_switch_breakfast);
        llBreakfastSetting = rootView.findViewById(R.id.ll_breakfast_setting);
        llSwitchLunch = rootView.findViewById(R.id.ll_switch_lunch);
        llLunchSetting = rootView.findViewById(R.id.ll_lunch_setting);
        llSwitchDinner = rootView.findViewById(R.id.ll_switch_dinner);
        llDinnerSetting = rootView.findViewById(R.id.ll_dinner_setting);

        tvBreakfastTime = rootView.findViewById(R.id.tv_breakfast_time);
        ivSwitchBreakfast = rootView.findViewById(R.id.iv_switch_breakfast);
        tvLunchTime = rootView.findViewById(R.id.tv_lunch_time);
        ivSwitchLunch = rootView.findViewById(R.id.iv_switch_lunch);
        tvDinnerTime = rootView.findViewById(R.id.tv_dinner_time);
        ivSwitchDinner = rootView.findViewById(R.id.iv_switch_dinner);


        tvBreakfastAmount = rootView.findViewById(R.id.tv_breakfast_amount);
        tvLunchAmount = rootView.findViewById(R.id.tv_lunch_amount);
        tvDinnerAmount = rootView.findViewById(R.id.tv_dinner_amount);

        flFixAmountMode = rootView.findViewById(R.id.fl_fix_amount_mode);
        flSwitchFacePass = rootView.findViewById(R.id.fl_switch_face_pass);
        flRestartApp = rootView.findViewById(R.id.fl_restart_app);
        flShutdownDevice = rootView.findViewById(R.id.fl_shutdown_device);
        flRebootDevice = rootView.findViewById(R.id.fl_reboot_device);
        svContent = rootView.findViewById(R.id.sv_content);

        flCanteenTimeSetting = rootView.findViewById(R.id.fl_canteen_time_setting);
        tvSwitchBreakfast = rootView.findViewById(R.id.tv_switch_breakfast);
        tvSwitchLunch = rootView.findViewById(R.id.tv_switch_lunch);
        tvSwitchDinner = rootView.findViewById(R.id.tv_switch_dinner);
        llSwitchFixAmount = rootView.findViewById(R.id.ll_switch_fix_amount);
        tvOpenFixAmount = rootView.findViewById(R.id.tv_open_fix_amount);
        tvCloseFixAmount = rootView.findViewById(R.id.tv_close_fix_amount);

        flTips = rootView.findViewById(R.id.fl_tips);
        tvTips = rootView.findViewById(R.id.tv_tips);


        mPageIndex = 1;
        currentSelectIndex = -1;
        refreshSecondPageData();
        Choreographer.getInstance().postFrameCallbackDelayed(new Choreographer.FrameCallback() {
            @Override
            public void doFrame(long frameTimeNanos) {
                scrollNextItem();
            }
        }, 50);

    }

    private void showSecondPage() {

    }


    /**
     * 刷新时段信息
     */

    private void refreshSecondPageData() {
        //时段信息
        refreshIntervalCardType();

        //早餐
        tvBreakfastAmount.setText(PaymentSettingMMKV.getBreakfastAmount());

        //午餐
        tvLunchAmount.setText(PaymentSettingMMKV.getLunchAmount());//午餐金额

        //晚餐
        tvDinnerAmount.setText(PaymentSettingMMKV.getDinnerAmount());//晚餐金额
    }



    @Override
    protected void onFragmentResume(boolean isFirstOnResume) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefreshPayType(RefreshPayType refreshPayType) {

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    /**
     * 刷新时段信息
     */
    private void refreshIntervalCardType() {
        // 早餐时间
        List<IntervalCardTypeBean> intervalCardType = MainApplication.intervalCardType;
        if (intervalCardType != null && !intervalCardType.isEmpty()) {
            IntervalCardTypeBean cardTypeBean = intervalCardType.get(0);
            String oneTime = cardTypeBean.getOneTime();
            if (!TextUtils.isEmpty(oneTime)) {
                llSwitchBreakfast.setVisibility(View.VISIBLE);
                tvBreakfastTime.setText(oneTime);
                boolean breakfastSwitch = PaymentSettingMMKV.getBreakfastSwitch();
                if (breakfastSwitch) {
                    llBreakfastSetting.setVisibility(View.VISIBLE);
                    ivSwitchBreakfast.setImageResource(R.mipmap.icon_check_selected);
                } else {
                    llBreakfastSetting.setVisibility(View.GONE);
                    ivSwitchBreakfast.setImageResource(0);
                }
            } else {
                llSwitchBreakfast.setVisibility(View.GONE);
                llBreakfastSetting.setVisibility(View.GONE);
            }

            String twoTime = cardTypeBean.getTwoTime();
            if (!TextUtils.isEmpty(twoTime)) {
                llSwitchLunch.setVisibility(View.VISIBLE);
                tvLunchTime.setText(twoTime);
                boolean lunchSwitch = PaymentSettingMMKV.getLunchSwitch();
                if (lunchSwitch) {
                    llLunchSetting.setVisibility(View.VISIBLE);
                    ivSwitchLunch.setImageResource(R.mipmap.icon_check_selected);
                } else {
                    llLunchSetting.setVisibility(View.GONE);
                    ivSwitchLunch.setImageResource(0);
                }
            } else {
                llSwitchLunch.setVisibility(View.GONE);
                llLunchSetting.setVisibility(View.GONE);
            }

            String threeTime = cardTypeBean.getThreeTime();
            if (!TextUtils.isEmpty(threeTime)) {
                llSwitchDinner.setVisibility(View.VISIBLE);
                tvDinnerTime.setText(threeTime);
                boolean dinnerSwitch = PaymentSettingMMKV.getDinnerSwitch();
                if (dinnerSwitch) {
                    llDinnerSetting.setVisibility(View.VISIBLE);
                    ivSwitchDinner.setImageResource(R.mipmap.icon_check_selected);
                } else {
                    llDinnerSetting.setVisibility(View.GONE);
                    ivSwitchDinner.setImageResource(0);
                }
            } else {
                llSwitchDinner.setVisibility(View.GONE);
                llDinnerSetting.setVisibility(View.GONE);
            }
        } else {
            llSwitchBreakfast.setVisibility(View.GONE);
            llBreakfastSetting.setVisibility(View.GONE);
            llSwitchLunch.setVisibility(View.GONE);
            llLunchSetting.setVisibility(View.GONE);
            llSwitchDinner.setVisibility(View.GONE);
            llDinnerSetting.setVisibility(View.GONE);
        }
    }

    private void scrollPreItem() {
        currentSelectIndex--;

        Log.e("selectScrollItem", "-scrollPreItem-mPageIndex- = " + currentSelectIndex);
        if (currentSelectIndex <= -1) {
            currentSelectIndex = secondPageSelectItemCount - 1;
            Log.e("selectScrollItem", "-scrollPreItem--resetIndex- = " + currentSelectIndex);
        }
        if (llDinnerSetting.getVisibility() == View.GONE) {
            Log.e("selectScrollItem", "-scrollPreItem-llDinnerSetting-GONE");
            if (currentSelectIndex == 5) {
                currentSelectIndex -= 1;
            }
        }
        if (llLunchSetting.getVisibility() == View.GONE) {
            Log.e("selectScrollItem", "-scrollPreItem-llLunchSetting-GONE");
            if (currentSelectIndex == 4) {
                currentSelectIndex -= 1;
            }
        }
        if (llBreakfastSetting.getVisibility() == View.GONE) {
            Log.e("selectScrollItem", "-scrollPreItem-llBreakfastSetting-GONE");
            if (currentSelectIndex == 3) {
                currentSelectIndex -= 1;
            }
        }
        if (llSwitchDinner.getVisibility() == View.GONE) {
            Log.e("selectScrollItem", "-scrollPreItem-llSwitchDinner-GONE");
            if (currentSelectIndex == 2) {
                currentSelectIndex -= 1;
            }
        }
        if (llSwitchLunch.getVisibility() == View.GONE) {
            Log.e("selectScrollItem", "-scrollPreItem-llSwitchLunch-GONE");
            if (currentSelectIndex == 1) {
                currentSelectIndex -= 1;
            }
        }
        if (llSwitchBreakfast.getVisibility() == View.GONE) {
            Log.e("selectScrollItem", "-scrollPreItem-llSwitchBreakfast-GONE");
            if (currentSelectIndex == 0) {
                currentSelectIndex -= 1;
            }
        }
            if (currentSelectIndex <= -1) {
                currentSelectIndex = secondPageSelectItemCount - 1;
                Log.e("selectScrollItem", "-scrollPreItem--resetIndex- = " + currentSelectIndex);
            }

        Log.e("selectScrollItem", "-scrollPreItem-currentSelectIndex-- = " + currentSelectIndex);
        selectScrollItem(currentSelectIndex);
    }

    private void scrollNextItem() {
        currentSelectIndex++;

        Log.e("selectScrollItem", "-scrollNextItem-mPageIndex-" + currentSelectIndex);
        if (currentSelectIndex >= secondPageSelectItemCount) {
                currentSelectIndex = 0;
            Log.e("selectScrollItem", "-scrollNextItem--resetIndex- = " + currentSelectIndex);
        }
        if (llSwitchBreakfast.getVisibility() == View.GONE) {
            Log.e("selectScrollItem", "-scrollNextItem-llSwitchBreakfast-GONE");
            if (currentSelectIndex == 0) {
                currentSelectIndex += 1;
            }
        }
        if (llSwitchLunch.getVisibility() == View.GONE) {
            Log.e("selectScrollItem", "-scrollNextItem-llSwitchLunch-GONE");
            if (currentSelectIndex == 1) {
                currentSelectIndex += 1;
            }
        }
        if (llSwitchDinner.getVisibility() == View.GONE) {
            Log.e("selectScrollItem", "-scrollNextItem-llSwitchDinner-GONE");
            if (currentSelectIndex == 2) {
                currentSelectIndex += 1;
            }
        }
        if (llBreakfastSetting.getVisibility() == View.GONE) {
            Log.e("selectScrollItem", "-scrollNextItem-llBreakfastSetting-GONE");
            if (currentSelectIndex == 3) {
                currentSelectIndex += 1;
            }
        }
        if (llLunchSetting.getVisibility() == View.GONE) {
            Log.e("selectScrollItem", "-scrollNextItem-llLunchSetting-GONE");
            if (currentSelectIndex == 4) {
                currentSelectIndex += 1;
            }
            }
        if (llDinnerSetting.getVisibility() == View.GONE) {
            Log.e("selectScrollItem", "-scrollNextItem-llDinnerSetting-GONE");
            if (currentSelectIndex == 5) {
                currentSelectIndex += 1;
            }
        }
        if (currentSelectIndex >= secondPageSelectItemCount) {
            currentSelectIndex = 0;
            Log.e("selectScrollItem", "-scrollNextItem--resetIndex- = " + currentSelectIndex);
        }

        Log.e("selectScrollItem", "-scrollNextItem-currentSelectIndex-- = " + currentSelectIndex);
        selectScrollItem(currentSelectIndex);
    }

    private void selectScrollItem(int itemIndex) {
        if (mPageIndex == 0) {
            flFixAmountMode.setBackground(null);
            flSwitchFacePass.setBackground(null);
            flRestartApp.setBackground(null);
            flShutdownDevice.setBackground(null);
            flRebootDevice.setBackground(null);
            View focusView = null;
            switch (itemIndex) {
                case 0:
                    flFixAmountMode.setBackgroundColor(Color.parseColor("#12ffffff"));
                    focusView = flFixAmountMode;
                    break;

                case 1:
                    flSwitchFacePass.setBackgroundColor(Color.parseColor("#12ffffff"));
                    focusView = flSwitchFacePass;
                    break;

                case 2:
                    flRestartApp.setBackgroundColor(Color.parseColor("#12ffffff"));
                    focusView = flRestartApp;
                    break;

                case 3:
                    flShutdownDevice.setBackgroundColor(Color.parseColor("#12ffffff"));
                    focusView = flShutdownDevice;
                    break;

                case 4:
                    flRebootDevice.setBackgroundColor(Color.parseColor("#12ffffff"));
                    focusView = flRebootDevice;
                    break;
            }
            if (focusView != null) {
                svContent.smoothScrollTo(0, focusView.getTop());
            }
        } else if (mPageIndex == 1) {
            flCanteenTimeSetting.setBackground(null);
            llSwitchBreakfast.setSelected(false);
            ivSwitchBreakfast.setSelected(false);
            tvSwitchBreakfast.setSelected(false);
            llSwitchLunch.setSelected(false);
            ivSwitchLunch.setSelected(false);
            tvSwitchLunch.setSelected(false);
            llSwitchDinner.setSelected(false);
            ivSwitchDinner.setSelected(false);
            tvSwitchDinner.setSelected(false);
            // 早餐
            llBreakfastSetting.setBackground(null);
            tvBreakfastAmount.setSelected(false);
            tvBreakfastAmount.setTextColor(Color.parseColor("#ffffffff"));
            // 午餐
            llLunchSetting.setBackground(null);
            tvLunchAmount.setSelected(false);
            tvLunchAmount.setTextColor(Color.parseColor("#ffffffff"));
            // 晚餐
            llDinnerSetting.setBackground(null);
            tvDinnerAmount.setSelected(false);
            tvDinnerAmount.setTextColor(Color.parseColor("#ffffffff"));
            // 开关
            llSwitchFixAmount.setBackground(null);
            tvOpenFixAmount.setSelected(false);
            tvCloseFixAmount.setSelected(false);
            View focusView = null;
            switch (itemIndex) {
                // 早餐开关
                case 0:
                    flCanteenTimeSetting.setBackgroundColor(Color.parseColor("#12ffffff"));
                    llSwitchBreakfast.setSelected(true);
                    ivSwitchBreakfast.setSelected(true);
                    tvSwitchBreakfast.setSelected(true);
                    focusView = flCanteenTimeSetting;
                    break;
                // 午餐开关
                case 1:
                    flCanteenTimeSetting.setBackgroundColor(Color.parseColor("#12ffffff"));
                    llSwitchLunch.setSelected(true);
                    ivSwitchLunch.setSelected(true);
                    tvSwitchLunch.setSelected(true);
                    focusView = flCanteenTimeSetting;
                    break;
                // 晚餐开关
                case 2:
                    flCanteenTimeSetting.setBackgroundColor(Color.parseColor("#12ffffff"));
                    llSwitchDinner.setSelected(true);
                    ivSwitchDinner.setSelected(true);
                    tvSwitchDinner.setSelected(true);
                    focusView = flCanteenTimeSetting;
                    break;

                // 早餐金额
                case 3:
                    llBreakfastSetting.setBackgroundColor(Color.parseColor("#12ffffff"));
                    tvBreakfastAmount.setSelected(true);
                    tvBreakfastAmount.setTextColor(Color.parseColor("#ff00dc82"));
                    focusView = llBreakfastSetting;
                    break;

                // 午餐金额
                case 4:
                    llLunchSetting.setBackgroundColor(Color.parseColor("#12ffffff"));
                    tvLunchAmount.setSelected(true);
                    tvLunchAmount.setTextColor(Color.parseColor("#ff00dc82"));
                    focusView = llLunchSetting;
                    break;

                // 晚餐金额
                case 5:
                    llDinnerSetting.setBackgroundColor(Color.parseColor("#12ffffff"));
                    tvDinnerAmount.setSelected(true);
                    tvDinnerAmount.setTextColor(Color.parseColor("#ff00dc82"));
                    focusView = llDinnerSetting;
                    break;

                // 开启
                case 6:
                    llSwitchFixAmount.setBackgroundColor(Color.parseColor("#12ffffff"));
                    tvOpenFixAmount.setSelected(true);
                    focusView = llSwitchFixAmount;
                    break;

                // 关闭
                case 7:
                    llSwitchFixAmount.setBackgroundColor(Color.parseColor("#12ffffff"));
                    tvCloseFixAmount.setSelected(true);
                    focusView = llSwitchFixAmount;
                    break;
            }
            if (focusView != null) {
                svContent.smoothScrollTo(0, focusView.getTop());
            }
        }
    }

    private void backPress() {
        if (flTips.getVisibility() == View.VISIBLE) {
            hidTips();
            return;
        }
        showFirstPage();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        LogHelper.print("---Consumption2SettingFragment--dispatchKeyEvent--activity event: " + event);
        int action = event.getAction();
        if (action == KeyEvent.ACTION_UP) {
            int keyCode = event.getKeyCode();
            String speakWords = "";
            Log.d(TAG, "==keyCode : " + keyCode);
            switch (keyCode) {
                case KeyEvent.KEYCODE_DEL:
                    speakWords = "删除";
                    //删除
                    backPress();
                    break;
                case KeyEvent.KEYCODE_ENTER:
                    //确认
                    okLogic();
                    break;
                case KeyEvent.KEYCODE_DPAD_UP:
                    //向上
                    scrollPreItem();
                    break;
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    //向下
                    scrollNextItem();
                    break;

                case KeyEvent.KEYCODE_0:
                case KeyEvent.KEYCODE_1:
                case KeyEvent.KEYCODE_2:
                case KeyEvent.KEYCODE_3:
                case KeyEvent.KEYCODE_4:
                case KeyEvent.KEYCODE_5:
                case KeyEvent.KEYCODE_6:
                case KeyEvent.KEYCODE_7:
                case KeyEvent.KEYCODE_8:
                case KeyEvent.KEYCODE_9:
                case KeyEvent.KEYCODE_NUMPAD_DOT:
                    // 0 1 2 3 4 5 6 7 8 9 .
                    if (flTips.getVisibility() == View.VISIBLE) {
                        hidTips();
                        return false;
                    }
                    try {
                        int tempCode = keyCode;
                        if (tempCode >= KeyEvent.KEYCODE_0 && tempCode <= KeyEvent.KEYCODE_9 ) {
                            tempCode -= 7;
                            handleInputNumber(String.valueOf(tempCode));
                        }else {
                            handleInputNumber(".");
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                        showTips("输入数字异常,请重新输入");
                    }
                    break;


            }
            speakTTSVoice(speakWords);
        }
        return super.dispatchKeyEvent(event);
    }

    private void handleInputNumber(String insetNumber) {
        switch (currentSelectIndex) {
            // 早餐金额
            case 3:
                handleInputAmountNumber(insetNumber, tvBreakfastAmount);
                break;

            // 午餐金额
            case 4:
                handleInputAmountNumber(insetNumber, tvLunchAmount);
                break;

            // 晚餐金额
            case 5:
                handleInputAmountNumber(insetNumber, tvDinnerAmount);
                break;
        }
    }


    private void handleInputAmountNumber(String insetNumber, TextView amountTextView) {
        String text = amountTextView.getText().toString();
        if (TextUtils.isEmpty(text)) {
            if (insetNumber.equals(".")) {
                amountTextView.setText("0.");
            } else {
                amountTextView.setText(insetNumber);
            }
        } else {
            // 判断数字长度不能超过
            int pointIndex = text.indexOf(".");
            if (pointIndex != -1) {
                // 获取小数点位数 最多两位小数
                if (text.length() - 1 >= pointIndex + 2) {
                    showTips("最多两位小数");
                    return;
                }
            } else {
                if (text.length() >= 2 && !insetNumber.equals(".")) {
                    showTips("超过最大限值");
                    return;
                }
            }
            if (insetNumber.equals(".")) {
                if (!text.contains(".")) {
                    amountTextView.setText(text + ".");
                }
            } else {
                amountTextView.setText(text + insetNumber);
            }
        }
    }


    private void showTips(String tips) {
        flTips.removeCallbacks(hidTipsTask);
        flTips.setVisibility(View.VISIBLE);
        tvTips.setText(tips);
        flTips.postDelayed(hidTipsTask, 1500);
    }

    private void hidTips() {
        flTips.removeCallbacks(hidTipsTask);
        flTips.setVisibility(View.GONE);
    }


    private void okLogic() {
        if (flTips.getVisibility() == View.VISIBLE) {
            hidTips();
        }
        if (currentSelectIndex == 0 || currentSelectIndex == 1 || currentSelectIndex == 2) {
            handleCanteenTimeSetting();
        } else if (currentSelectIndex == 3 || currentSelectIndex == 4 || currentSelectIndex == 5) {
            scrollNextItem();
        } else if (currentSelectIndex == 6) {

            if (llBreakfastSetting.getVisibility() == View.VISIBLE) {
                PaymentSettingMMKV.putBreakfastSwitch(true);
                // 早餐金额判断
                String breakfastAmountStr = tvBreakfastAmount.getText().toString();
                Double breakfastAmount = parseDouble(breakfastAmountStr);
                if (breakfastAmount == null || breakfastAmount <= 0) {
                    showTips("早餐金额未设置，不能开启定额模式");
                    return;
                }
                // 保存本地金额
                PaymentSettingMMKV.putBreakfastAmount(breakfastAmountStr);
            } else {
                PaymentSettingMMKV.putBreakfastSwitch(false);
            }

            if (llLunchSetting.getVisibility() == View.VISIBLE) {
                PaymentSettingMMKV.putLunchSwitch(true);
                // 午餐金额判断
                String lunchAmountStr = tvLunchAmount.getText().toString();
                Double lunchAmount = parseDouble(lunchAmountStr);
                if (lunchAmount == null || lunchAmount <= 0) {
                    showTips("午餐金额未设置，不能开启定额模式");
                    return;
                }
                // 保存本地金额
                PaymentSettingMMKV.putLunchAmount(lunchAmountStr);
            } else {
                PaymentSettingMMKV.putLunchSwitch(false);
            }

            if (llDinnerSetting.getVisibility() == View.VISIBLE) {
                PaymentSettingMMKV.putDinnerSwitch(true);
                // 晚餐金额判断
                String dinnerAmountStr = tvDinnerAmount.getText().toString();
                Double dinnerAmount = parseDouble(dinnerAmountStr);
                if (dinnerAmount == null || dinnerAmount <= 0) {
                    showTips("晚餐金额未设置，不能开启定额模式");
                    return;
                }
                // 保存本地金额
                PaymentSettingMMKV.putDinnerAmount(dinnerAmountStr);
            } else {
                PaymentSettingMMKV.putDinnerSwitch(false);
            }

            PaymentSettingMMKV.putSwitchFixAmount(true);
            // TODO: 更新定额设置
            //EventBus.getDefault().post(new MessageEventBean(MessageEventType.OpenFixAmountMode));
            showFirstPage();
            returnMainPage();
        } else if (currentSelectIndex == 7) {
            // 关闭定额
            PaymentSettingMMKV.putSwitchFixAmount(false);
            // TODO: 更新定额设置
            //EventBus.getDefault().post(new MessageEventBean(MessageEventType.CloseFixAmountMode));
            showFirstPage();
            returnMainPage();
        } else {

        }


    }


    private void returnMainPage() {
//        MainActivity mainActivity = (MainActivity) getActivity();
//        mainActivity.showFragment(HomeMenu.MENU1);
//        SPUtils.getInstance().put(Constants.FRAGMENT_SET, false);


    }


    private void showFirstPage() {
//        mPageIndex = 0;
//        currentSelectIndex = -1;
//        binding.llPageFirst.setVisibility(View.VISIBLE);
//        binding.llPageSecond.setVisibility(View.GONE);
//        refreshFirstPageData();
//        Choreographer.getInstance().postFrameCallbackDelayed(new Choreographer.FrameCallback() {
//            @Override
//            public void doFrame(long frameTimeNanos) {
//                scrollNextItem();
//            }
//        }, 50);


        if (keyboardListener != null) {
            Log.d(TAG, "lime== settings: 179");
            keyboardListener.back();
        }
    }


    private void handleCanteenTimeSetting() {
        switch (currentSelectIndex) {
            // 早餐开关
            case 0:
                if (llBreakfastSetting.getVisibility() == View.VISIBLE) {
                    llBreakfastSetting.setVisibility(View.GONE);
                    ivSwitchBreakfast.setImageResource(0);
                } else {
                    llBreakfastSetting.setVisibility(View.VISIBLE);
                    ivSwitchBreakfast.setImageResource(R.mipmap.icon_check_selected);
                }
                break;

            // 午餐开关
            case 1:
                if (llLunchSetting.getVisibility() == View.VISIBLE) {
                    llLunchSetting.setVisibility(View.GONE);
                    ivSwitchLunch.setImageResource(0);
                } else {
                    llLunchSetting.setVisibility(View.VISIBLE);
                    ivSwitchLunch.setImageResource(R.mipmap.icon_check_selected);
                }
                break;

            // 晚餐开关
            case 2:
                if (llDinnerSetting.getVisibility() == View.VISIBLE) {
                    llDinnerSetting.setVisibility(View.GONE);
                    ivSwitchDinner.setImageResource(0);
                } else {
                    llDinnerSetting.setVisibility(View.VISIBLE);
                    ivSwitchDinner.setImageResource(R.mipmap.icon_check_selected);
                }
                break;
        }
    }


    // Helper method to parse double safely
    private Double parseDouble(String str) {
        try {
            return Double.parseDouble(str);
        } catch (NumberFormatException e) {
            return null;
        }
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
        public void back();
    }



    @Override
    protected int getLayoutResId() {
        return R.layout.consumption2_setting_fragment;
    }



}
