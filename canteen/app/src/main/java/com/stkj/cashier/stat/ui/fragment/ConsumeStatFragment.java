package com.stkj.cashier.stat.ui.fragment;

import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jakewharton.processphoenix.ProcessPhoenix;
import com.stkj.cashier.BuildConfig;
import com.stkj.cashier.MainApplication;
import com.stkj.cashier.R;
import com.stkj.cashier.base.device.DeviceManager;
import com.stkj.cashier.base.model.BaseNetResponse;
import com.stkj.cashier.base.model.StatNetResponse;
import com.stkj.cashier.base.net.ParamsUtils;
import com.stkj.cashier.base.tts.TTSVoiceHelper;
import com.stkj.cashier.base.ui.fragment.BaseDispatchKeyEventFragment;
import com.stkj.cashier.base.utils.EncryptUtils;
import com.stkj.cashier.setting.data.PaymentSettingMMKV;
import com.stkj.cashier.setting.model.RefreshPayType;
import com.stkj.cashier.stat.model.CanteenSummary;
import com.stkj.cashier.stat.model.ConsumeStatBean;
import com.stkj.cashier.stat.service.StatService;
import com.stkj.cashier.stat.ui.weight.CommonTipsView;
import com.stkj.cashier.utils.ShellUtils;
import com.stkj.common.core.ActivityHolderFactory;
import com.stkj.common.log.LogHelper;
import com.stkj.common.net.retrofit.RetrofitManager;
import com.stkj.common.rx.AutoDisposeUtils;
import com.stkj.common.rx.DefaultObserver;
import com.stkj.common.rx.RxTransformerUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.TreeMap;

/**
 * 设置
 */
public class ConsumeStatFragment extends BaseDispatchKeyEventFragment {




    public final static String TAG = "ConsumeStatFragment";
    private KeyboardListener keyboardListener;
    //当前页面选中状态索引
    private int currentSelectIndex = -1;

    // 页面总item数量
    private final int pageSelectItemCount = 4;

    private boolean isRequestingPageData = false;

    private TextView tvTabToday;
    private TextView tvTabWeek;
    private TextView tvTabThisMonth;
    private TextView tvTabLastMonth;
    private TextView tvSumConsume;
    private TextView tvSumRefund;
    private TextView tvSumIncome;
    private TextView tvBreakfastConsume;
    private TextView tvBreakfastRefund;
    private TextView tvBreakfastIncome;
    private TextView tvLunchConsume;
    private TextView tvLunchRefund;
    private TextView tvLunchIncome;
    private TextView tvDinnerConsume;
    private TextView tvDinnerRefund;
    private TextView tvDinnerIncome;
    private CommonTipsView ctvStat;


    public static ConsumeStatFragment newInstance() {
        return new ConsumeStatFragment();
    }



    //当前页面索引
    private int mPageIndex = 0;

    //第一层页面总item数量
    private int firstPageSelectItemCount = 5;

    @Override
    protected void initViews(View rootView) {
        super.initViews(rootView);

        tvTabToday = rootView.findViewById(R.id.tv_tab_today);
        tvTabWeek = rootView.findViewById(R.id.tv_tab_week);
        tvTabThisMonth = rootView.findViewById(R.id.tv_tab_this_month);
        tvTabLastMonth = rootView.findViewById(R.id.tv_tab_last_month);
        tvSumConsume = rootView.findViewById(R.id.tv_sum_consume);
        tvSumRefund = rootView.findViewById(R.id.tv_sum_refund);
        tvSumIncome = rootView.findViewById(R.id.tv_sum_income);
        tvBreakfastConsume = rootView.findViewById(R.id.tv_breakfast_consume);
        tvBreakfastRefund = rootView.findViewById(R.id.tv_breakfast_refund);
        tvBreakfastIncome = rootView.findViewById(R.id.tv_breakfast_income);
        tvLunchConsume = rootView.findViewById(R.id.tv_lunch_consume);
        tvLunchRefund = rootView.findViewById(R.id.tv_lunch_refund);
        tvLunchIncome = rootView.findViewById(R.id.tv_lunch_income);
        tvDinnerConsume = rootView.findViewById(R.id.tv_dinner_consume);
        tvDinnerRefund= rootView.findViewById(R.id.tv_dinner_refund);
        tvDinnerIncome = rootView.findViewById(R.id.tv_dinner_income);
        ctvStat = rootView.findViewById(R.id.ctv_stat);


        currentSelectIndex = -1;
        scrollNextItem();

    }

    /**
     * 设置消费统计数据
     */
    private void setConsumeStatData(List<ConsumeStatBean.ConsumeStatItem> statData) {
        ctvStat.hideTipsView();

        for (ConsumeStatBean.ConsumeStatItem statItem : statData) {
            switch (statItem.getFeeType()) {
                case "1":
                    tvBreakfastConsume.setText(statItem.getConsume());
                    tvBreakfastRefund.setText("-" + statItem.getRefund());
                    tvBreakfastIncome.setText(statItem.getIncome());
                    break;
                case "2":
                    tvLunchConsume.setText(statItem.getConsume());
                    tvLunchRefund.setText("-" + statItem.getRefund());
                    tvLunchIncome.setText(statItem.getIncome());
                    break;
                case "3":
                    tvDinnerConsume.setText(statItem.getConsume());
                    tvDinnerRefund.setText("-" + statItem.getRefund());
                    tvDinnerIncome.setText(statItem.getIncome());
                    break;
            }
        }
    }


    /**
     * 刷新页面数据信息
     */
    private void requestConsumeStatDate(int itemIndex) {
        ctvStat.setLoading("加载中");
        isRequestingPageData = true;
        String totalTime = String.valueOf(itemIndex + 1);
        TreeMap<String, String> map = new TreeMap<>();
        map.put("mode", "ConsumeFeeTypeTotal");
        map.put("machine_Number", DeviceManager.INSTANCE.getDeviceInterface().getMachineNumber());
        map.put("totalTime", totalTime);
        String md5 = EncryptUtils.encryptMD5ToString16(DeviceManager.INSTANCE.getDeviceInterface().getMachineNumber() + "&" + totalTime);
        map.put("sign", md5);

        RetrofitManager.INSTANCE.getDefaultRetrofit()
                .create(StatService.class)
                .getConsumeStat(ParamsUtils.signSortParamsMap(map))
                .compose(RxTransformerUtils.mainSchedulers())
                .to(AutoDisposeUtils.onDestroyDispose(this))
                .subscribe(new DefaultObserver<StatNetResponse<List<ConsumeStatBean.ConsumeStatItem>>>() {
                    @Override
                    protected void onSuccess(StatNetResponse<List<ConsumeStatBean.ConsumeStatItem>> statData) {
                        isRequestingPageData = false;
                        Log.d(TAG, "lime == statData: " + (new Gson()).toJson(statData));

                        if (statData != null && statData.getCode().equals("10000") && statData.getData() != null) {
                            tvSumConsume.setText(statData.getSumConsume());
                            tvSumRefund.setText(statData.getSumRefund());
                            tvSumIncome.setText(statData.getSumIncome());
                            setConsumeStatData(statData.getData());
                        } else {
                            String errorMsg = "获取数据失败,请点击确认键重试";
                            if (statData != null && statData.getMessage() != null) {
                                errorMsg = statData.getMessage();
                            }
                            ctvStat.setTips(errorMsg);
                        }


                    }
                });
    }


    private void scrollPreItem() {
        currentSelectIndex--;
        if (currentSelectIndex <= -1) {
            currentSelectIndex = pageSelectItemCount - 1;
        }
        Log.e("selectScrollItem", "-scrollPreItem-currentSelectIndex-- = " + currentSelectIndex);
        selectScrollItem(currentSelectIndex);
    }

    private void scrollNextItem() {
        currentSelectIndex++;
        if (currentSelectIndex >= pageSelectItemCount) {
            currentSelectIndex = 0;
        }
        Log.e("selectScrollItem", "-scrollNextItem-currentSelectIndex-- = " + currentSelectIndex);
        selectScrollItem(currentSelectIndex);
    }


    private void selectScrollItem(int itemIndex) {
        tvTabToday.setSelected(false);
        tvTabWeek.setSelected(false);
        tvTabThisMonth.setSelected(false);
        tvTabLastMonth.setSelected(false);
        tvSumConsume.setText("—");
        tvSumRefund.setText("—");
        tvSumIncome.setText("—");
        tvBreakfastConsume.setText("—");
        tvBreakfastRefund.setText("—");
        tvBreakfastIncome.setText("—");
        tvLunchConsume.setText("—");
        tvLunchRefund.setText("—");
        tvLunchIncome.setText("—");
        tvDinnerConsume.setText("—");
        tvDinnerRefund.setText("—");
        tvDinnerIncome.setText("—");
        switch (itemIndex) {
            case 0:
                tvTabToday.setSelected(true);
                break;
            case 1:
                tvTabWeek.setSelected(true);
                break;
            case 2:
                tvTabThisMonth.setSelected(true);
                break;
            case 3:
                tvTabLastMonth.setSelected(true);
                break;
        }
        requestConsumeStatDate(itemIndex);
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




    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        LogHelper.print("---ConsumeStatFragment--dispatchKeyEvent--activity event: " + event);
        int action = event.getAction();
        if (action == KeyEvent.ACTION_UP) {
            int keyCode = event.getKeyCode();
            String speakWords = "";
            Log.d(TAG, "==keyCode : " + keyCode);
            switch (keyCode) {
                case KeyEvent.KEYCODE_DEL:
                    speakWords = "清除";
                    //清除|回退键
                    if (keyboardListener != null){
                        Log.d(TAG, "lime== settings: 179");
                        keyboardListener.back();
                    }
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

            }
            speakTTSVoice(speakWords);
        }
        return super.dispatchKeyEvent(event);
    }

    private void okLogic() {
        if (isRequestingPageData) {
            return;
        }
        requestConsumeStatDate(currentSelectIndex);
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
        return R.layout.consume_stat_fragment;
    }



}
