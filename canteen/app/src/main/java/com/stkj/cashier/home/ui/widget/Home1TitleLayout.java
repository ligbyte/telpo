package com.stkj.cashier.home.ui.widget;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.stkj.cashier.BuildConfig;
import com.stkj.cashier.R;
import com.stkj.cashier.base.device.DeviceManager;
import com.stkj.cashier.home.callback.OnGetStoreInfoListener;
import com.stkj.cashier.home.helper.SystemEventWatcherHelper;
import com.stkj.cashier.home.model.StoreInfo;
import com.stkj.cashier.pay.callback.OnConsumerModeListener;
import com.stkj.cashier.pay.data.PayConstants;
import com.stkj.cashier.pay.helper.ConsumerModeHelper;
import com.stkj.cashier.setting.helper.StoreInfoHelper;
import com.stkj.common.core.ActivityHolderFactory;
import com.stkj.common.core.AppManager;
import com.stkj.common.log.LogHelper;
import com.stkj.common.ui.widget.shapelayout.ShapeTextView;
import com.stkj.common.utils.IntentUtils;
import com.stkj.common.utils.NetworkUtils;

/**
 * 样式1顶部title
 */
public class Home1TitleLayout extends FrameLayout implements SystemEventWatcherHelper.OnSystemEventListener, OnGetStoreInfoListener, OnConsumerModeListener {

    private ImageView ivLogoIcon;
    private TextView tvStoreName;
    private TextView tvStoreId;
    private ImageView ivSysWifi;
    private TextView tvSysTime;
    private TextView tvNetDelayTime;
    private ShapeTextView stvConsumerMode;
    private ImageView ivBattery;

    public Home1TitleLayout(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public Home1TitleLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public Home1TitleLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        boolean isConsumer = false;
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, com.stkj.cashier.R.styleable.Home1TitleLayout);
            isConsumer = a.getBoolean(R.styleable.Home1TitleLayout_h1tl_is_consumer, false);
        }
        LayoutInflater.from(context).inflate(R.layout.include_home1_title_lay, this);
        ivLogoIcon = (ImageView) findViewById(R.id.iv_canteen_logo);
        tvStoreName = (TextView) findViewById(R.id.tv_store_name);
        tvStoreId = (TextView) findViewById(R.id.tv_store_id);
        tvStoreId.setText(DeviceManager.INSTANCE.getDeviceInterface().getMachineNumber());
        ivSysWifi = (ImageView) findViewById(R.id.iv_sys_wifi);
        tvSysTime = (TextView) findViewById(R.id.tv_sys_time);
        tvNetDelayTime = (TextView) findViewById(R.id.tv_net_delay_time);
        stvConsumerMode = (ShapeTextView) findViewById(R.id.stv_consumer_mode);
        if (isConsumer) {
            tvNetDelayTime.setVisibility(GONE);
            stvConsumerMode.setVisibility(GONE);
        } else {
            tvNetDelayTime.setVisibility(VISIBLE);
            //stvConsumerMode.setVisibility(VISIBLE);
        }
        ivBattery = (ImageView) findViewById(R.id.iv_battery);
        LogHelper.print("--HomeTitleLayout-getContext-" + getContext());
        Activity mainActivity = AppManager.INSTANCE.getMainActivity();
        if (mainActivity != null) {
            ConsumerModeHelper consumerModeHelper = ActivityHolderFactory.get(ConsumerModeHelper.class, mainActivity);
            if (consumerModeHelper != null) {
                int currentConsumerMode = consumerModeHelper.getCurrentConsumerMode();
                stvConsumerMode.setText(PayConstants.getConsumerModeStr(currentConsumerMode));
                if (BuildConfig.DEBUG) {
                    stvConsumerMode.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            consumerModeHelper.showSelectConsumerModeDialog();
                        }
                    });
                }
            }
        }
        refreshDate();
        refreshWifiLay();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Activity mainActivity = AppManager.INSTANCE.getMainActivity();
        if (mainActivity != null) {
            SystemEventWatcherHelper systemEventWatcherHelper = ActivityHolderFactory.get(SystemEventWatcherHelper.class, mainActivity);
            if (systemEventWatcherHelper != null) {
                systemEventWatcherHelper.removeSystemEventListener(this);
            }
            StoreInfoHelper storeInfoHelper = ActivityHolderFactory.get(StoreInfoHelper.class, mainActivity);
            if (storeInfoHelper != null) {
                storeInfoHelper.removeGetStoreInfoListener(this);
            }
            ConsumerModeHelper consumerModeHelper = ActivityHolderFactory.get(ConsumerModeHelper.class, mainActivity);
            if (consumerModeHelper != null) {
                consumerModeHelper.removeConsumerModeListener(this);
            }
        }
    }

    private void refreshDate() {
        try {
            String formatDate = DateFormat.format("MM-dd HH:mm", System.currentTimeMillis()).toString();
            tvSysTime.setText(formatDate);
            LogHelper.print("---HomeTitleLayout----refreshDate: " + formatDate);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDateTick() {
        refreshDate();
    }

    @Override
    public void onDateChange() {
        refreshDate();
    }

    @Override
    public void onNetworkAvailable() {
        LogHelper.print("--HomeTitleLayout--onNetworkAvailable");
        //获取当wifi信号强度
        refreshWifiLay();
    }

    @Override
    public void onNetworkLost() {
        LogHelper.print("--HomeTitleLayout--onNetworkLost");
        ivSysWifi.setImageResource(R.mipmap.icon_wifi_no);
        tvNetDelayTime.setTextColor(0xff999999);
        tvNetDelayTime.setText("网络已断开");
    }

    @Override
    public void onNetworkUnavailable() {
        LogHelper.print("--HomeTitleLayout--onNetworkUnavailable");
        ivSysWifi.setImageResource(R.mipmap.icon_wifi_no);
        tvNetDelayTime.setTextColor(0xff999999);
        tvNetDelayTime.setText("网络已断开");
    }

    @Override
    public void onNetworkRssiChange(int level, long delayTime) {
        LogHelper.print("--HomeTitleLayout--onNetworkRssiChange level: " + level);
        switch (level) {
            case 0:
                tvNetDelayTime.setTextColor(0xffFF3030);
                ivSysWifi.setImageResource(R.mipmap.icon_wifi_level0);
                break;
            case 1:
                tvNetDelayTime.setTextColor(0xffFF3030);
                ivSysWifi.setImageResource(R.mipmap.icon_wifi_level1);
                break;
            case 2:
                tvNetDelayTime.setTextColor(0xffEE9A00);
                ivSysWifi.setImageResource(R.mipmap.icon_wifi_level2);
                break;
            case 3:
            default:
                tvNetDelayTime.setTextColor(0xff00EE76);
                ivSysWifi.setImageResource(R.mipmap.icon_wifi_level3);
                break;
        }
        if (delayTime <= 0) {
            tvNetDelayTime.setText("");
        } else {
            tvNetDelayTime.setText(SystemEventWatcherHelper.getNetworkLevelTips(level));
        }
    }

    @Override
    public void onGetStoreInfo(StoreInfo storeInfo) {
        tvStoreName.setText(storeInfo.getDeviceName());
    }

    private void refreshWifiLay() {
        if (NetworkUtils.isConnected()) {
//            //有网络
//            boolean wifiConnected = NetworkUtils.isWifiConnected();
//            if (wifiConnected) {
//                int wifiNetworkRSSI = NetworkUtils.getWifiNetworkRSSILevel(4);
//                onNetworkRssiChange(wifiNetworkRSSI);
//            } else {
//                //获取移动信号强度等待回调
//                onNetworkRssiChange(4);
//            }
            onNetworkRssiChange(4, 0);
        } else {
            //无网络
            onNetworkUnavailable();
        }
    }

    @Override
    public void onBatteryChange(float batteryPercent, boolean isChanging) {
        if (isChanging) {
            ivBattery.setImageResource(R.mipmap.icon_battery_ischarging);
        } else {
            if (batteryPercent == 100) {
                ivBattery.setImageResource(R.mipmap.icon_battery_100);
            } else if (batteryPercent >= 80) {
                ivBattery.setImageResource(R.mipmap.icon_battery_80);
            } else if (batteryPercent >= 60) {
                ivBattery.setImageResource(R.mipmap.icon_battery_60);
            } else if (batteryPercent >= 40) {
                ivBattery.setImageResource(R.mipmap.icon_battery_40);
            } else if (batteryPercent >= 20) {
                ivBattery.setImageResource(R.mipmap.icon_battery_20);
            } else {
                ivBattery.setImageResource(R.mipmap.icon_battery_0);
            }
        }
    }

    @Override
    public void onChangeConsumerMode(int consumerMode, int lastConsumerMode) {
        if (stvConsumerMode != null) {
            stvConsumerMode.setText(PayConstants.getConsumerModeStr(consumerMode));
        }
    }
}
