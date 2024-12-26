package com.stkj.cashier.home.ui.activity;

import android.annotation.SuppressLint;
import android.graphics.Rect;
import android.hardware.usb.UsbDevice;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.stkj.cashier.BuildConfig;
import com.stkj.cashier.R;
import com.stkj.cashier.base.callback.AppNetCallback;
import com.stkj.cashier.base.device.DeviceManager;
import com.stkj.cashier.base.model.BaseResponse;
import com.stkj.cashier.base.net.AppNetManager;
import com.stkj.cashier.base.permission.AppPermissionHelper;
import com.stkj.cashier.base.tts.TTSVoiceHelper;
import com.stkj.cashier.base.ui.dialog.CommonAlertDialogFragment;
import com.stkj.cashier.base.ui.dialog.CommonInputDialogFragment;
import com.stkj.cashier.base.utils.CommonDialogUtils;
import com.stkj.cashier.consumer.ConsumerManager;
import com.stkj.cashier.consumer.callback.ConsumerListener;
import com.stkj.cashier.home.helper.CBGCameraHelper;
import com.stkj.cashier.home.helper.HeartBeatHelper;
import com.stkj.cashier.home.helper.ScreenProtectHelper;
import com.stkj.cashier.home.helper.SystemEventWatcherHelper;
import com.stkj.cashier.home.model.HomeMenuList;
import com.stkj.cashier.home.model.HomeTabInfo;
import com.stkj.cashier.home.service.HomeService;
import com.stkj.cashier.home.ui.adapter.HomeTabPageAdapter;
import com.stkj.cashier.home.ui.widget.HomeTabLayout;
import com.stkj.cashier.home.ui.widget.HomeTitleLayout;
import com.stkj.cashier.login.callback.LoginCallback;
import com.stkj.cashier.login.helper.LoginHelper;
import com.stkj.cashier.pay.helper.ConsumerModeHelper;
import com.stkj.cashier.setting.data.ServerSettingMMKV;
import com.stkj.cashier.setting.helper.StoreInfoHelper;
import com.stkj.cbgfacepass.CBGFacePassHandlerHelper;
import com.stkj.cbgfacepass.data.CBGFacePassConfigMMKV;
import com.stkj.cbgfacepass.model.CBGFacePassConfig;
import com.stkj.cbgfacepass.permission.CBGPermissionRequest;
import com.stkj.common.core.AppManager;
import com.stkj.common.core.CountDownHelper;
import com.stkj.common.log.LogHelper;
import com.stkj.common.net.retrofit.RetrofitManager;
import com.stkj.common.permissions.callback.PermissionCallback;
import com.stkj.common.rx.AutoDisposeUtils;
import com.stkj.common.rx.DefaultObserver;
import com.stkj.common.rx.RxTransformerUtils;
import com.stkj.common.ui.activity.BaseActivity;
import com.stkj.common.ui.toast.AppToast;
import com.stkj.common.utils.AndroidUtils;
import com.stkj.common.utils.KeyBoardUtils;
import com.stkj.deviceinterface.UsbDeviceHelper;
import com.stkj.deviceinterface.callback.UsbDeviceListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends BaseActivity implements AppNetCallback, ConsumerListener {

    private View scanHolderView;
    private HomeTabLayout htlLeftNav;
    private ViewPager2 vp2Content;
    private FrameLayout flScreenProtect;
    private HomeTabPageAdapter homeTabPageAdapter;
    //是否需要重新恢复消费者页面
    private boolean needRestartConsumer;
    //是否初始化了菜单数据
    private boolean hasInitMenuData;
    private boolean isSoftKeyboardShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.INSTANCE.setMainActivity(this);
        readSaveInstanceState(savedInstanceState);
        setContentView(com.stkj.cashier.R.layout.activity_main);
        ConsumerManager.INSTANCE.showConsumer(this, this);
        findViews();
        initApp();
        LogHelper.print("-main--getDisplayMetrics--" + getResources().getDisplayMetrics());
    }

    @Override
    public int getContentPlaceHolderId() {
        return R.id.fl_place_holder_content;
    }

    @Override
    protected void onPause() {
        super.onPause();
        //关闭屏幕常亮
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //打开屏幕常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (needRestartConsumer) {
            needRestartConsumer = false;
            ConsumerManager.INSTANCE.showConsumer(this, this);
        }
    }

    /**
     * 初始化app
     */
    private void initApp() {
        //初始化人脸识别
        CBGFacePassHandlerHelper facePassHelper = getWeakRefHolder(CBGFacePassHandlerHelper.class);
        facePassHelper.setOnInitFacePassListener(new CBGFacePassHandlerHelper.OnInitFacePassListener() {
            @Override
            public void onInitSuccess() {
                initData();
            }

            @Override
            public void onInitError(String msg) {
                hideLoadingDialog();
                CommonDialogUtils.showTipsDialog(MainActivity.this, msg, "知道了", new CommonAlertDialogFragment.OnSweetClickListener() {
                    @Override
                    public void onClick(CommonAlertDialogFragment alertDialogFragment) {
                        initData();
                    }
                });
            }
        });
        AppPermissionHelper.with(this)
                .requestPermission(new CBGPermissionRequest(), new PermissionCallback() {
                    @Override
                    public void onGranted() {
                        showLoadingDialog();
                        //设备识别距离阈值
                        int defaultFaceMinThreshold = DeviceManager.INSTANCE.getDeviceInterface().getDefaultDetectFaceMinThreshold();
                        CBGFacePassConfigMMKV.setDefDetectFaceMinThreshold(defaultFaceMinThreshold);
                        //设备人脸入库阈值
                        int defaultAddFaceMinThreshold = DeviceManager.INSTANCE.getDeviceInterface().getDefaultAddFaceMinThreshold();
                        CBGFacePassConfigMMKV.setDefAddFaceMinThreshold(defaultAddFaceMinThreshold);
                        //设备人脸角度阈值
                        int defaultPoseThreshold = DeviceManager.INSTANCE.getDeviceInterface().getDefaultPoseThreshold();
                        CBGFacePassConfigMMKV.setDefPoseThreshold(defaultPoseThreshold);
                        boolean supportDualCamera = DeviceManager.INSTANCE.getDeviceInterface().isSupportDualCamera();
                        CBGFacePassConfig facePassConfig = CBGFacePassConfigMMKV.getFacePassConfig(supportDualCamera);
                        CBGFacePassHandlerHelper facePassHelper = getWeakRefHolder(CBGFacePassHandlerHelper.class);
                        facePassHelper.initAndAuthSdk(facePassConfig);
                    }

                    @Override
                    public void onCancel() {
                        CommonDialogUtils.showTipsDialog(MainActivity.this, "人脸识别功能请求系统权限失败", "知道了", new CommonAlertDialogFragment.OnSweetClickListener() {
                            @Override
                            public void onClick(CommonAlertDialogFragment alertDialogFragment) {
                                initData();
                            }
                        });
                    }
                });
    }

    /**
     * 清理焦点
     */
    public void clearMainFocus() {
        //清理焦点信息
        View currentFocus = getCurrentFocus();
        if (currentFocus != null) {
            currentFocus.clearFocus();
        }
        if (DeviceManager.INSTANCE.getDeviceInterface().isCanDispatchKeyEvent()) {
            scanHolderView.requestFocus();
        }
    }

    private void findViews() {
        scanHolderView = (View) findViewById(R.id.scan_holder_view);
        HomeTitleLayout htlMain = (HomeTitleLayout) findViewById(R.id.htl_main);
        //系统事件监听
        SystemEventWatcherHelper systemEventWatcherHelper = getWeakRefHolder(SystemEventWatcherHelper.class);
        if (systemEventWatcherHelper != null) {
            systemEventWatcherHelper.addSystemEventListener(htlMain);
        }
        //添加设备信息更新回调
        StoreInfoHelper storeInfoHelper = getWeakRefHolder(StoreInfoHelper.class);
        if (storeInfoHelper != null) {
            storeInfoHelper.addGetStoreInfoListener(htlMain);
        }
        //消费模式
        ConsumerModeHelper consumerModeHelper = getWeakRefHolder(ConsumerModeHelper.class);
        if (consumerModeHelper != null) {
            consumerModeHelper.addConsumerModeListener(htlMain);
        }
        flScreenProtect = (FrameLayout) findViewById(R.id.fl_screen_protect);
        flScreenProtect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flScreenProtect.setVisibility(View.GONE);
                ScreenProtectHelper screenProtectHelper = getWeakRefHolder(ScreenProtectHelper.class);
                screenProtectHelper.startScreenProtect();
            }
        });
        findViewById(R.id.iv_logo).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                String buildInfo = "编译时间: " + BuildConfig.BUILD_TIME + "\n"
                        + "编译id: " + BuildConfig.GIT_SHA + "\n"
                        + "编译类型: " + (BuildConfig.DEBUG ? "测试版" : "正式版") + "\n"
                        + "版本号: " + BuildConfig.VERSION_NAME + "\n"
                        + "设备名称: " + DeviceManager.INSTANCE.getDeviceInterface().getDeviceName() + "\n"
                        + "人脸授权: " + (CBGFacePassHandlerHelper.hasFacePassSDKAuth() ? "已授权" : "未授权");
                CommonAlertDialogFragment commonAlertDialogFragment = CommonAlertDialogFragment.build()
                        .setAlertTitleTxt("版本信息")
                        .setAlertContentTxt(buildInfo);
                if (BuildConfig.DEBUG) {
                    commonAlertDialogFragment.setLeftNavTxt("切换服务器")
                            .setLeftNavClickListener(new CommonAlertDialogFragment.OnSweetClickListener() {
                                @Override
                                public void onClick(CommonAlertDialogFragment alertDialogFragment) {
                                    showInputServerAddressDialog();
                                }
                            })
                            .setRightNavTxt("确定")
                            .show(MainActivity.this);
                } else {
                    commonAlertDialogFragment.setLeftNavTxt("关闭App")
                            .setLeftNavClickListener(new CommonAlertDialogFragment.OnSweetClickListener() {
                                @Override
                                public void onClick(CommonAlertDialogFragment alertDialogFragment) {
                                    DeviceManager.INSTANCE.getDeviceInterface().release();
                                    AndroidUtils.killApp(MainActivity.this);
                                }
                            })
                            .setRightNavTxt("确定")
                            .show(MainActivity.this);
                }
                return true;
            }
        });
        View rootPlaceHolderView = findViewById(R.id.root_view_placeholder);
        rootPlaceHolderView.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        //获取占位view高度
                        int placeViewHeight = rootPlaceHolderView.getHeight();
                        if (placeViewHeight <= 0) {
                            return;
                        }
                        Rect rect = new Rect();
                        getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
                        //获取被遮挡高度
                        int keyBoardHeight = placeViewHeight - rect.height();
                        LogHelper.print("keyboard onGlobalLayout: placeViewHeight = " + placeViewHeight + " rect.height = " + rect.height());
                        //软键盘显示或者隐藏
                        boolean needTouchHideKeyboard = keyBoardHeight >= 200;
                        if (needTouchHideKeyboard) {
                            isSoftKeyboardShow = true;
                            LogHelper.print("show keyboard--offset = " + keyBoardHeight);
                            rootPlaceHolderView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    KeyBoardUtils.hideSoftKeyboard(MainActivity.this, rootPlaceHolderView);
                                    rootPlaceHolderView.setOnClickListener(null);
                                    rootPlaceHolderView.setClickable(false);
                                    clearMainFocus();
                                }
                            });
                        } else {
                            isSoftKeyboardShow = false;
                            LogHelper.print("hide keyboard--offset = " + keyBoardHeight);
                            rootPlaceHolderView.setOnClickListener(null);
                            rootPlaceHolderView.setClickable(false);
                        }
                    }
                });
        htlLeftNav = (HomeTabLayout) findViewById(R.id.htl_left_nav);
        vp2Content = (ViewPager2) findViewById(R.id.vp2_content);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        LogHelper.print("---MainActivity--dispatchKeyEvent--activity event: " + event);
        if (isSoftKeyboardShow && DeviceManager.INSTANCE.getDeviceInterface().isFinishDispatchKeyEvent()) {
            return super.dispatchKeyEvent(event);
        }
        //判断扫码枪是否连接
        if (DeviceManager.INSTANCE.getDeviceInterface().isCanDispatchKeyEvent()) {
            if (event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                KeyBoardUtils.hideSoftKeyboard(this, scanHolderView);
            } else {
                if (!scanHolderView.hasFocus()) {
                    scanHolderView.requestFocus();
                }
            }
            DeviceManager.INSTANCE.getDeviceInterface().dispatchKeyEvent(event);
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN) {
            ScreenProtectHelper screenProtectHelper = getWeakRefHolder(ScreenProtectHelper.class);
            screenProtectHelper.stopScreenProtect();
        } else if (action == MotionEvent.ACTION_UP) {
            ScreenProtectHelper screenProtectHelper = getWeakRefHolder(ScreenProtectHelper.class);
            screenProtectHelper.startScreenProtect();
        }
        return super.dispatchTouchEvent(event);
    }

    private void initData() {

    }

    @Override
    public void onNetInitSuccess() {


    }

    @Override
    public void onNetInitError(String message) {
        hideLoadingDialog();
        showAppNetInitErrorDialog(message);
    }

    /**
     * 展示 app 初始化失败弹窗
     */
    private void showAppNetInitErrorDialog(String errorMsg) {
        CommonAlertDialogFragment.build()
                .setAlertTitleTxt("提示")
                .setAlertContentTxt("初始化失败,错误原因:\n" + errorMsg)
                .setLeftNavTxt("重试")
                .setLeftNavClickListener(new CommonAlertDialogFragment.OnSweetClickListener() {
                    @Override
                    public void onClick(CommonAlertDialogFragment alertDialogFragment) {
                        showLoadingDialog();

                    }
                })
                .setRightNavTxt("修改地址")
                .setRightNavClickListener(new CommonAlertDialogFragment.OnSweetClickListener() {
                    @Override
                    public void onClick(CommonAlertDialogFragment alertDialogFragment) {
                        showInputServerAddressDialog();
                    }
                }).show(this);
    }

    /**
     * 显示修改服务器地址
     */
    private void showInputServerAddressDialog() {
        CommonInputDialogFragment.build()
                .setTitle("修改服务器地址")
                .setOnInputListener(new CommonInputDialogFragment.OnInputListener() {
                    @Override
                    public void onInputEnd(String input) {
                        ServerSettingMMKV.handleChangeServerAddress(MainActivity.this, input);
                    }
                }).show(this);
    }



    /**
     * 加载主页内容
     */
    private void initHomeContent(List<HomeMenuList.Menu> menuList) {
        //添加左侧tab列表
        List<HomeTabInfo<HomeMenuList.Menu>> homeTabInfoList = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            if (i == 0) {
                for (HomeMenuList.Menu menu : menuList) {
                    if (TextUtils.equals(HomeTabPageAdapter.TAB_PAYMENT_TAG, menu.getPath())) {
                        //收银
                        HomeTabInfo<HomeMenuList.Menu> paymentTabInfo = new HomeTabInfo<>();
                        paymentTabInfo.setExtraInfo(menu);
                        paymentTabInfo.setSelectRes(R.mipmap.icon_payment);
                        paymentTabInfo.setUnSelectRes(R.mipmap.icon_payment);
                        homeTabInfoList.add(paymentTabInfo);
                        break;
                    }
                }
            } else if (i == 1) {
                for (HomeMenuList.Menu menu : menuList) {
                    if (TextUtils.equals(HomeTabPageAdapter.TAB_STAT_TAG, menu.getPath())) {
                        //统计
                        HomeTabInfo<HomeMenuList.Menu> goodsTabInfo = new HomeTabInfo<>();
                        goodsTabInfo.setExtraInfo(menu);
                        goodsTabInfo.setSelectRes(R.mipmap.icon_stat);
                        goodsTabInfo.setUnSelectRes(R.mipmap.icon_stat);
                        homeTabInfoList.add(goodsTabInfo);
                        break;
                    }
                }
            } else if (i == 2) {
//                for (HomeMenuList.Menu menu : menuList) {
//                    if (TextUtils.equals(HomeTabPageAdapter.TAB_COUPON_TAG, menu.getPath())) {
//                        //优惠
//                        HomeTabInfo<HomeMenuList.Menu> discountsTabInfo = new HomeTabInfo<>();
//                        discountsTabInfo.setExtraInfo(menu);
//                        discountsTabInfo.setSelectRes(R.mipmap.icon_coupon);
//                        discountsTabInfo.setUnSelectRes(R.mipmap.icon_coupon);
//                        homeTabInfoList.add(discountsTabInfo);
//                        break;
//                    }
//                }
            } else {
                for (HomeMenuList.Menu menu : menuList) {
                    if (TextUtils.equals(HomeTabPageAdapter.TAB_SETTING_TAG, menu.getPath())) {
                        //设置
                        HomeTabInfo<HomeMenuList.Menu> settingTabInfo = new HomeTabInfo<>();
                        settingTabInfo.setExtraInfo(menu);
                        settingTabInfo.setSelectRes(R.mipmap.icon_setting);
                        settingTabInfo.setUnSelectRes(R.mipmap.icon_setting);
                        homeTabInfoList.add(settingTabInfo);
                        break;
                    }
                }
            }
        }
//        if (homeTabInfoList.isEmpty()) {
//            showMenuListErrorDialog("菜单数据为空!");
//            return;
//        }
        htlLeftNav.addTabList(homeTabInfoList);
        htlLeftNav.setOnTabChangeListener(new HomeTabLayout.OnTabChangeListener() {
            @Override
            public void onTabSelected(int tabIndex) {
                clearMainFocus();
                vp2Content.setCurrentItem(tabIndex, false);
            }
        });
        //添加右侧内容页面
        homeTabPageAdapter = new HomeTabPageAdapter(this, homeTabInfoList);
        //禁止viewPager左右滑动切换tab页
        vp2Content.setUserInputEnabled(false);
        vp2Content.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                htlLeftNav.setCurrentTab(position);
            }
        });
        vp2Content.setAdapter(homeTabPageAdapter);
        vp2Content.setOffscreenPageLimit(2);
        htlLeftNav.setCurrentTab(saveStateCurrentTabPage);
        htlLeftNav.setEnableTabClick(true);
        //每秒回调helper
        CountDownHelper countDownHelper = getWeakRefHolder(CountDownHelper.class);
        countDownHelper.startCountDown();
        //开始心跳设置
        HeartBeatHelper heartBeatHelper = getWeakRefHolder(HeartBeatHelper.class);
        heartBeatHelper.requestHeartBeat();
        countDownHelper.addCountDownListener(heartBeatHelper);
        //请求设备信息
        StoreInfoHelper storeInfoHelper = getWeakRefHolder(StoreInfoHelper.class);
        storeInfoHelper.requestStoreInfo();
        //获取餐厅时段信息
        ConsumerModeHelper consumerModeHelper = getWeakRefHolder(ConsumerModeHelper.class);
        consumerModeHelper.requestCanteenCurrentTimeInfo();
        countDownHelper.addCountDownListener(consumerModeHelper);
        //初始化语音
        TTSVoiceHelper ttsVoiceHelper = getWeakRefHolder(TTSVoiceHelper.class);
        ttsVoiceHelper.initTTSVoice(null);
        //屏幕保护程序
        ScreenProtectHelper screenProtectHelper = getWeakRefHolder(ScreenProtectHelper.class);
        screenProtectHelper.setOnScreenProtectListener(new ScreenProtectHelper.OnScreenProtectListener() {
            @Override
            public void onNeedScreenProtect() {
                flScreenProtect.setVisibility(View.VISIBLE);
            }
        });
        countDownHelper.addCountDownListener(screenProtectHelper);
        //网络状态回调
        SystemEventWatcherHelper systemEventWatcherHelper = getWeakRefHolder(SystemEventWatcherHelper.class);
        countDownHelper.addCountDownListener(systemEventWatcherHelper);
        //usb设备初始化
        boolean supportUSBDevice = DeviceManager.INSTANCE.getDeviceInterface().isSupportUSBDevice();
        if (supportUSBDevice) {
            UsbDeviceHelper usbDeviceHelper = getWeakRefHolder(UsbDeviceHelper.class);
            usbDeviceHelper.addUsbListener(new UsbDeviceListener() {
                @Override
                public void onAttachDevice(UsbDevice device, HashMap<String, UsbDevice> allDevices) {
                    DeviceManager.INSTANCE.getDeviceInterface().attachUsbDevice(device);
                }

                @Override
                public void onDetachDevice(UsbDevice device, HashMap<String, UsbDevice> allDevices) {
                    DeviceManager.INSTANCE.getDeviceInterface().detachUsbDevice(device);
                }
            });
            DeviceManager.INSTANCE.getDeviceInterface().initUsbDevices(usbDeviceHelper.getUsbDeviceMap());
        }
        hasInitMenuData = true;
    }

    //当前TAB界面
    private static final String TAB_CURRENT_PAGE = "currentTabPage";
    private int saveStateCurrentTabPage;

    @Override
    protected void onSaveInstanceState(@androidx.annotation.NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(TAB_CURRENT_PAGE, vp2Content.getCurrentItem());
    }

    private void readSaveInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            saveStateCurrentTabPage = savedInstanceState.getInt(TAB_CURRENT_PAGE, 0);
        }
    }

    private long lastBackClickTime = 0;

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        long currentTime = System.currentTimeMillis();
        if ((currentTime - lastBackClickTime) > 2000) {
            AppToast.toastMsg("再按一次退出程序");
            lastBackClickTime = currentTime;
        } else {
            //杀掉进程
            DeviceManager.INSTANCE.getDeviceInterface().release();
            AndroidUtils.killApp(this);
        }
    }

    @Override
    protected void onDestroy() {
        AppManager.INSTANCE.clearMainActivity();
        super.onDestroy();
    }

    @Override
    public void onCreateFacePreviewView(SurfaceView previewView, SurfaceView irPreview) {
        CBGCameraHelper cbgCameraHelper = getWeakRefHolder(CBGCameraHelper.class);
        boolean isFaceDualCamera = DeviceManager.INSTANCE.getDeviceInterface().isSupportDualCamera() &&
                CBGFacePassConfigMMKV.isOpenDualCamera();
        cbgCameraHelper.setPreviewView(previewView, irPreview, isFaceDualCamera);
        //异步初始化相机模块
        Schedulers.io().scheduleDirect(new Runnable() {
            @Override
            public void run() {
                try {
                    cbgCameraHelper.prepareFacePassDetect();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onConsumerDismiss() {
        needRestartConsumer = true;
        ConsumerManager.INSTANCE.clearConsumerPresentation();
        //清理相机相关引用,释放相机
        CBGCameraHelper cbgCameraHelper = getWeakRefHolder(CBGCameraHelper.class);
        cbgCameraHelper.releaseCameraHelper();
        clearWeakRefHolder(CBGCameraHelper.class);
    }

    @Override
    public void onCreateTitleLayout(HomeTitleLayout homeTitleLayout) {
        //系统事件监听
        SystemEventWatcherHelper systemEventWatcherHelper = getWeakRefHolder(SystemEventWatcherHelper.class);
        if (systemEventWatcherHelper != null) {
            systemEventWatcherHelper.addSystemEventListener(homeTitleLayout);
        }
        //添加设备信息更新回调
        StoreInfoHelper storeInfoHelper = getWeakRefHolder(StoreInfoHelper.class);
        if (storeInfoHelper != null) {
            storeInfoHelper.addGetStoreInfoListener(homeTitleLayout);
        }
    }

    @Override
    public void addContentPlaceHolderFragment(Fragment fragment) {
        //登录过期弹窗弹出，不处理其他弹窗
        if (LoginHelper.INSTANCE.isHandleLoginValid()) {
            return;
        }
        super.addContentPlaceHolderFragment(fragment);
    }

    public Fragment getCurrentTabFragment() {
        if (vp2Content != null && homeTabPageAdapter != null) {
            int currentItem = vp2Content.getCurrentItem();
            return homeTabPageAdapter.findPageFragment(this, currentItem);
        }
        return null;
    }
}