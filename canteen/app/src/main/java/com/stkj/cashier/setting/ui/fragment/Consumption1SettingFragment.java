package com.stkj.cashier.setting.ui.fragment;

import android.graphics.Color;
import android.media.Image;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.jakewharton.processphoenix.ProcessPhoenix;
import com.stkj.cashier.BuildConfig;
import com.stkj.cashier.MainApplication;
import com.stkj.cashier.R;
import com.stkj.cashier.base.device.DeviceManager;
import com.stkj.cashier.base.tts.TTSVoiceHelper;
import com.stkj.cashier.base.ui.dialog.CommonInputDialogFragment;
import com.stkj.cashier.base.ui.fragment.BaseDispatchKeyEventFragment;
import com.stkj.cashier.base.ui.widget.CommonSeekProgressBar;
import com.stkj.cashier.base.utils.EventBusUtils;

import com.stkj.cashier.pay.data.PayConstants;
import com.stkj.cashier.setting.data.PaymentSettingMMKV;
import com.stkj.cashier.setting.data.TTSSettingMMKV;
import com.stkj.cashier.setting.model.RefreshPayType;
import com.stkj.cashier.utils.ShellUtils;
import com.stkj.common.core.ActivityHolderFactory;
import com.stkj.common.log.LogHelper;
import com.stkj.common.ui.fragment.BaseRecyclerFragment;
import com.stkj.common.ui.toast.AppToast;
import com.stkj.common.ui.widget.shapelayout.ShapeTextView;
import com.stkj.common.utils.AudioMngHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 设置
 */
public class Consumption1SettingFragment extends BaseDispatchKeyEventFragment {


    public final static String TAG = "Consumption1SettingFragment";
    private KeyboardListener keyboardListener;
    //当前页面选中状态索引
    private int currentSelectIndex = -1;
    FrameLayout flFixAmountMode;
    FrameLayout flSwitchFacePass;
    FrameLayout flRestartApp;
    FrameLayout flShutdownDevice;
    FrameLayout flRebootDevice;
    ScrollView svContent;
    ImageView ivSwitchFacePass;
    TextView tvDeviceSerialNumber;

    //当前页面索引
    private int mPageIndex = 0;

    //第一层页面总item数量
    private int firstPageSelectItemCount = 5;

    @Override
    protected void initViews(View rootView) {
        super.initViews(rootView);
         flFixAmountMode = rootView.findViewById(R.id.fl_fix_amount_mode);
         flSwitchFacePass = rootView.findViewById(R.id.fl_switch_face_pass);
         flRestartApp = rootView.findViewById(R.id.fl_restart_app);
         flShutdownDevice = rootView.findViewById(R.id.fl_shutdown_device);
         flRebootDevice = rootView.findViewById(R.id.fl_reboot_device);
        svContent = rootView.findViewById(R.id.sv_content);
        ivSwitchFacePass = rootView.findViewById(R.id.iv_switch_face_pass);
        ivSwitchFacePass.setSelected(PaymentSettingMMKV.getSwitchFacePassPay());
        tvDeviceSerialNumber = rootView.findViewById(R.id.tv_device_serial_number);
        tvDeviceSerialNumber.setText(DeviceManager.INSTANCE.getDeviceInterface().getMachineNumber() + "/" + BuildConfig.VERSION_NAME);

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


    private void selectScrollItem(int itemIndex) {

            flFixAmountMode.setBackground(null);
            flSwitchFacePass.setBackground(null);
            flRestartApp.setBackground(null);
            flShutdownDevice.setBackground(null);
            flRebootDevice.setBackground(null);

        View focusView = null;



            switch (itemIndex) {
                case 0: {
                    flFixAmountMode.setBackgroundColor(Color.parseColor("#12ffffff"));
                    focusView = flFixAmountMode;
                }

                break;

                case 1: {
                    flSwitchFacePass.setBackgroundColor(Color.parseColor("#12ffffff"));
                    focusView = flSwitchFacePass;
                }
                break;
                case 2: {
                    flRestartApp.setBackgroundColor(Color.parseColor("#12ffffff"));
                    focusView = flRestartApp;
                }
                break;
                case 3: {
                    flShutdownDevice.setBackgroundColor(Color.parseColor("#12ffffff"));
                    focusView = flShutdownDevice;
                }
                break;
                case 4: {
                    flRebootDevice.setBackgroundColor(Color.parseColor("#12ffffff"));
                    focusView = flRebootDevice;
                }
                break;
            }
            if (focusView != null) {
                svContent.smoothScrollTo(0, focusView.getTop());
            }


    }

    private void scrollPreItem() {
        currentSelectIndex--;
        if (mPageIndex == 0) {
            if (currentSelectIndex <= -1) {
                currentSelectIndex = firstPageSelectItemCount - 1;
            }
        }
        Log.e("selectScrollItem", "-scrollPreItem-currentSelectIndex-- = $currentSelectIndex");
        selectScrollItem(currentSelectIndex);
    }

    private void scrollNextItem() {
        currentSelectIndex++;
        if (mPageIndex == 0) {
            if (currentSelectIndex >= firstPageSelectItemCount) {
                currentSelectIndex = 0;
            }
        }
        Log.e("selectScrollItem", "-scrollNextItem-currentSelectIndex-- = $currentSelectIndex");
        selectScrollItem(currentSelectIndex);
    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        LogHelper.print("---Consumption1SettingFragment--dispatchKeyEvent--activity event: " + event);
        int action = event.getAction();
        if (action == KeyEvent.ACTION_UP) {
            int keyCode = event.getKeyCode();
            String speakWords = "";
            Log.d(TAG, "==keyCode : " + keyCode);
            switch (keyCode) {
                case android.view.KeyEvent.KEYCODE_DEL:
                    speakWords = "清除";
                    //清除|回退键
                    if (keyboardListener != null){
                        Log.d(TAG, "lime== settings: 179");
                        keyboardListener.back();
                    }
                    break;
                case android.view.KeyEvent.KEYCODE_ENTER:
                    //确认
                    okLogic();
                    break;
                case android.view.KeyEvent.KEYCODE_DPAD_UP:
                    //向上
                    scrollPreItem();
                    break;
                case android.view.KeyEvent.KEYCODE_DPAD_DOWN:
                    //向下
                    scrollNextItem();
                    break;

            }
            speakTTSVoice(speakWords);
        }
        return super.dispatchKeyEvent(event);
    }

    private void okLogic() {
        if (currentSelectIndex == 1) {
            boolean switchFacePassPay = ivSwitchFacePass.isSelected();
            ivSwitchFacePass.setSelected(!switchFacePassPay);
            if (ivSwitchFacePass.isSelected()) {
                PaymentSettingMMKV.putSwitchFacePassPay(true);
                // TODO: 开启刷脸设置
//                EventBus.getDefault()
//                        .post(MessageEventBean(MessageEventType.OpenFacePassPay));
            } else {
                PaymentSettingMMKV.putSwitchFacePassPay(false);
               // TODO: 关闭刷脸设置
//                EventBus.getDefault()
//                        .post(MessageEventBean(MessageEventType.CloseFacePassPay));
            }
        } else if (currentSelectIndex == 0) {
            if (keyboardListener != null){
                Log.d(TAG, "lime== settings: 222");
                keyboardListener.dingESettings();
            }
        } else if (currentSelectIndex == 2) {
            ProcessPhoenix.triggerRebirth(MainApplication.mainApplication);
        } else if (currentSelectIndex == 3){
            ShellUtils.execCommand("reboot -p",false);
        } else if (currentSelectIndex == 4){
            ShellUtils.execCommand("reboot",false);
        } else {

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
        public void dingESettings();
    }



    @Override
    protected int getLayoutResId() {
        return R.layout.consumption1_setting_fragment;
    }



}
