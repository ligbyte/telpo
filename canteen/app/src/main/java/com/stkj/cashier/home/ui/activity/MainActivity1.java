package com.stkj.cashier.home.ui.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.FutureTarget;
import com.google.gson.Gson;
import com.king.camera.scan.util.LogUtils;
import com.stkj.cashier.MainApplication;
import com.stkj.cashier.R;
import com.stkj.cashier.base.callback.AppNetCallback;
import com.stkj.cashier.base.device.DeviceManager;
import com.stkj.cashier.base.model.BaseNetResponse;
import com.stkj.cashier.base.net.ParamsUtils;
import com.stkj.cashier.base.permission.AppPermissionHelper;
import com.stkj.cashier.base.tts.TTSVoiceHelper;
import com.stkj.cashier.base.ui.dialog.CommonAlertDialogFragment;
import com.stkj.cashier.base.utils.EncryptUtils;
import com.stkj.cashier.consumer.ConsumerManager;
import com.stkj.cashier.consumer.callback.ConsumerListener;
import com.stkj.cashier.greendao.CompanyMemberdbEntityDao;
import com.stkj.cashier.greendao.biz.CompanyMemberBiz;
import com.stkj.cashier.greendao.tool.DBManager;
import com.stkj.cashier.home.helper.CBGCameraHelper;
import com.stkj.cashier.home.helper.HeartBeatHelper;
import com.stkj.cashier.home.helper.SystemEventWatcherHelper;
import com.stkj.cashier.home.ui.widget.Home1TitleLayout;
import com.stkj.cashier.login.helper.LoginHelper;
import com.stkj.cashier.pay.data.PayConstants;
import com.stkj.cashier.pay.helper.ConsumerModeHelper;
import com.stkj.cashier.pay.ui.fragment.Amount1ConsumerFragment;
import com.stkj.cashier.pay.ui.weight.Simple1Calculator;
import com.stkj.cashier.setting.data.PaymentSettingMMKV;
import com.stkj.cashier.setting.helper.AppUpgradeHelper;
import com.stkj.cashier.setting.helper.StoreInfoHelper;
import com.stkj.cashier.setting.model.CompanyMemberBean;
import com.stkj.cashier.setting.model.DeviceNameBean;
import com.stkj.cashier.setting.model.IntervalCardTypeBean;
import com.stkj.cashier.setting.model.OfflineSetBean;
import com.stkj.cashier.setting.model.ReportDeviceStatusBean;
import com.stkj.cashier.setting.model.db.CompanyMemberdbEntity;
import com.stkj.cashier.setting.service.SettingService;
import com.stkj.cashier.setting.ui.fragment.Consumption1SettingFragment;
import com.stkj.cashier.setting.ui.fragment.Consumption2SettingFragment;
import com.stkj.cashier.stat.ui.fragment.ConsumeStatFragment;
import com.stkj.cashier.utils.SystemUtils;
import com.stkj.cbgfacepass.CBGFacePassHandlerHelper;
import com.stkj.cbgfacepass.data.CBGFacePassConfigMMKV;
import com.stkj.cbgfacepass.model.CBGFacePassConfig;
import com.stkj.cbgfacepass.permission.CBGPermissionRequest;
import com.stkj.common.core.AppManager;
import com.stkj.common.core.CountDownHelper;
import com.stkj.common.glide.GlideApp;
import com.stkj.common.log.LogHelper;
import com.stkj.common.net.retrofit.RetrofitManager;
import com.stkj.common.permissions.callback.PermissionCallback;
import com.stkj.common.rx.AutoDisposeUtils;
import com.stkj.common.rx.DefaultObserver;
import com.stkj.common.rx.RxTransformerUtils;
import com.stkj.common.ui.activity.BaseActivity;
import com.stkj.common.ui.fragment.BaseFragment;
import com.stkj.common.ui.toast.AppToast;
import com.stkj.common.utils.AndroidUtils;
import com.stkj.common.utils.BarUtils;
import com.stkj.common.utils.FragmentUtils;
import com.stkj.common.utils.ScreenUtils;
import com.stkj.common.utils.TimeUtils;
import com.stkj.common.utils.ToastUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Stack;
import java.util.TreeMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import mcv.facepass.types.FacePassAddFaceResult;

public class MainActivity1 extends BaseActivity implements AppNetCallback, ConsumerListener,AppUpgradeHelper.OnAppUpgradeListener{

    public final static String TAG = "MainActivity1";
    private Home1TitleLayout h1tlMain;
    //是否需要重新恢复消费者页面
    private boolean needRestartConsumer;
    private Disposable reportDeviceStatusDisposable;
    private boolean hasInitMenuData;
    private Amount1ConsumerFragment amountFragment;
    private Consumption1SettingFragment consumption1SettingFragment;
    private Consumption2SettingFragment consumption2SettingFragment;
    private ConsumeStatFragment consumeStatFragment;
    private List<BaseFragment> baseFragmentList = new ArrayList<>();
    private Disposable currentTimeDisposable;
    private CountDownLatch latch = null;
    AlertDialog progressDialog;//更新进度弹窗
    AlertDialog allFaceDownDialog;  //全量人脸
    TextView tvProgress;
    ProgressBar sbProgress;

    boolean allFaceDown = false;
    boolean callBack = false;
    private int face_0_Count = 0;
    private int face_1_Count = 0;
    private int face_2_Count = 0;
    private int face_def_Count = 0;
    private int  totalFaceCount = 0;

    private int pageIndex = 1;
    private int pageSize = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.INSTANCE.setMainActivity(this);
        setContentView(R.layout.activity_main1);
        ConsumerManager.INSTANCE.showConsumer(this, this);
        ScreenUtils.setFullScreen(this);
        BarUtils.setNavBarVisibility(this, false);
        BarUtils.setStatusBarVisibility(getWindow(),false);
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

        if (MainApplication.intervalCardType == null){
            getIntervalCardType();
        }else {
            requestConsumptionType();
        }

        clickVersionUpdate();
        initCheckStatus();
        getIntervalCardType();
        company();

        //初始化人脸识别
        CBGFacePassHandlerHelper facePassHelper = getWeakRefHolder(CBGFacePassHandlerHelper.class);
        facePassHelper.setOnInitFacePassListener(new CBGFacePassHandlerHelper.OnInitFacePassListener() {
            @Override
            public void onInitSuccess() {
                initData();
            }

            @Override
            public void onInitError(String msg) {
                initData();
            }
        });
        AppPermissionHelper.with(this)
                .requestPermission(new CBGPermissionRequest(), new PermissionCallback() {
                    @Override
                    public void onGranted() {
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
                        initData();
                    }
                });
    }

    private void initCheckStatus() {
        TreeMap<String, String> map = new TreeMap<>();
        String deviceId = DeviceManager.INSTANCE.getDeviceInterface().getMachineNumber();
        map.put("mode", "ReportDeviceStatus");
        map.put("machine_Number", deviceId);
        String md5 = EncryptUtils.encryptMD5ToString16(deviceId);
        map.put("sign", md5);
        RetrofitManager.INSTANCE.getDefaultRetrofit()
                .create(SettingService.class)
                .reportDeviceStatus(ParamsUtils.signSortParamsMap(map))
                .compose(RxTransformerUtils.mainSchedulers())
                .to(AutoDisposeUtils.onDestroyDispose(this))
                .subscribe(new DefaultObserver<BaseNetResponse<ReportDeviceStatusBean>>() {
                    @Override
                    protected void onSuccess(BaseNetResponse<ReportDeviceStatusBean> statData) {
                        Log.d(TAG, "lime == getIntervalCardType: " + (new Gson()).toJson(statData));

                        if (statData != null && statData.getCode().equals("10000") && statData.getData() != null) {
                            LogUtils.e("deviceStatus observe");
                            List<String> indexs = statData.getData() != null ? Arrays.asList(statData.getData().getUpdateUserInfo().split("&")) : null;
                            if (indexs != null) {
                                for (String item : indexs) {
                                    //LogUtils.e("心跳item" + item + "/" + callBack + "/" + (latch != null ? latch.getCount() : "null") + "/" + allFaceDown);
                                    if ("1".equals(item)) {
                                        if ((latch == null || latch.getCount() == 0L) && !allFaceDown) {
                                            companyMember(1);
                                            LogUtils.e("item下发" + item);
                                        }
                                    } else if ("2".equals(item)) {
                                        getIntervalCardType();
                                    } else if ("3".equals(item)) {
                                        offlineSet();
                                    } else if ("4".equals(item)) {
                                        company();
                                    }
                                }
                            }

                        } else {


                        }


                    }
                });
    }

    private void cancelAllFaceDownDialog() {
        if (allFaceDownDialog.isShowing() || allFaceDown) {
            allFaceDownDialog.dismiss();
            // TODO: EventBus
            //EventBus.getDefault().post(new MessageEventBean(MessageEventType.FaceDBChangeEnd));

            allFaceDown = false;

            latch = new CountDownLatch(1); // 新的latch，初始计数为1
            latch.countDown();
        }
    }


    private void companyMember(int inferior_type) {
        LogUtils.e("lime== 人脸录入companyMember" + inferior_type);
        if (inferior_type == 0) {
            if (SystemUtils.isNetWorkActive(getApplication())) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showAllFsceProgressDialog();
                    }
                });
            } else {
                // ttsSpeak(getString(R.string.result_network_unavailable_error));
                // ttsSpeak("网络已断开，请检查网络。");
                // TODO: EventBus
                //EventBus.getDefault().post(new MessageEventBean(MessageEventType.FaceDBChangeEnd));
                allFaceDown = false;
                return;
            }
        }
        allFaceDown = true;



        TreeMap<String, String> map = new TreeMap<>();
        String deviceId = DeviceManager.INSTANCE.getDeviceInterface().getMachineNumber();
        map.put("mode", "KeyBoardCompanyMember");
        map.put("machine_Number", deviceId);
        map.put("inferior_type", String.valueOf(inferior_type));
        map.put("pageIndex", String.valueOf(pageIndex));
        map.put("pageSize", String.valueOf(pageSize));

        String md5 = EncryptUtils.encryptMD5ToString16(deviceId);
        map.put("sign", md5);
        RetrofitManager.INSTANCE.getDefaultRetrofit()
                .create(SettingService.class)
                .companyMember(ParamsUtils.signSortParamsMap(map))
                .compose(RxTransformerUtils.mainSchedulers())
                .to(AutoDisposeUtils.onDestroyDispose(this))
                .subscribe(new DefaultObserver<BaseNetResponse<CompanyMemberBean>>() {
                    @Override
                    protected void onSuccess(BaseNetResponse<CompanyMemberBean> statData) {
                        LogUtils.d("lime == getIntervalCardType: " + (new Gson()).toJson(statData));
                        if (statData != null && statData.getCode().equals("10000") && statData.getData() != null) {
                            LogUtils.e("offlineSet observe");
                            LogUtils.e("lime== 人脸录入后台返回的人数" + statData.getData().getResults().size());
                            CompanyMemberBiz.addCompanyMembers(statData.getData().getResults());
                            checkFace(statData.getData().getResults());
                            // TODO: EventBus
                            // EventBus.getDefault().post(MessageEventBean(MessageEventType.FaceDBChange))

                        }else {
                            if (statData.getCode().equals("10024")) {
                                //callBack = false;
                                allFaceDown = false;
                                if (allFaceDownDialog != null) {
                                    allFaceDownDialog.dismiss();
                                }
                                //queueManager.clearTasks();
                                // TODO: EventBus
                                //EventBus.getDefault().post(new MessageEventBean(MessageEventType.FaceDBChangeEnd));
                            } else {
                                if (statData.getMessage() != null) {
                                    ToastUtils.showShort(statData.getMessage());
                                }
                            }


                        }
                    }
                });

    }

    private void checkFace(List<CompanyMemberdbEntity> entities) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                //            CompanyMemberBiz.getCompanyMemberList {
                face_0_Count = 0;
                face_1_Count = 0;
                face_2_Count = 0;
                face_def_Count = 0;
                totalFaceCount = 0;
                if (entities != null) {
                    latch = new CountDownLatch(entities.size());
                } else {
                    latch = null;
                }


                for (int index = 0;index < entities.size();index ++) {
                    LogUtils.e("lime*** 下发人脸中 379");
                    CompanyMemberdbEntity unique =
                            DBManager.getInstance().getDaoSession().getCompanyMemberdbEntityDao().queryBuilder()
                                    .where(CompanyMemberdbEntityDao.Properties.UniqueNumber.eq(entities.get(index).getUniqueNumber()))
                                    .unique();
                    LogUtils.e("lime*** 下发人脸中 386");
                    if (unique != null && unique.getCardState() != 64 && unique.getFaceToken() == null) {
//                    if (entities[index].cardState != 64) {
//                        entities[i].id = unique.id
//                        DBManager.getInstance().daoSession.companyMemberdbEntityDao.update(
//                            entities[i]
//                        )
//                    }
                        LogUtils.e("lime*** 下发人脸中 398");
                        Bitmap base64ToBitmap = null;
                        try {
                            LogUtils.e("lime*** 下发人脸中 401");
                            FutureTarget<Bitmap> futureTarget = GlideApp.with(MainApplication.mainApplication)
                                    .asBitmap()
                                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                    .load(entities.get(index).getImgData())
                                    .submit();

                            base64ToBitmap = futureTarget.get();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        // var base64ToBitmap = ImageUtils.base64ToBitmap(item.imgData)
                        try {
                            if (base64ToBitmap == null) {
                                entities.get(index).setResult(-99);
                                LogUtils.e("lime== 人脸token生成失败" + entities.get(index).getFullName() + "///下载图片失败");
                                CompanyMemberBiz.updateCompanyMember(entities.get(index));
                            } else {
                                FacePassAddFaceResult result =
                                        MainApplication.mFacePassHandler.addFace(base64ToBitmap);
                                totalFaceCount += 1;
                                if (result.result == 0) {
                                    //0：成功
                                    entities.get(index).setResult(20);
                                    face_0_Count += 1;

                                } else if (result.result == 1) {
                                    //1：没有检测到人脸
                                    face_1_Count += 1;
                                    entities.get(index).setResult(3);
                                    LogUtils.e("lime== 入库失败--没有检测到人脸" + entities.get(index).getFullName());
                                } else if (result.result == 2) {
                                    //2：检测到人脸，但是没有通过质量判断
                                    face_2_Count += 1;
                                    entities.get(index).setResult(4);
                                    LogUtils.e("lime== 入库失败--检测到人脸，但是没有通过质量判断" + entities.get(index).getFullName());
                                } else {
                                    //其他值：未知错误
                                    face_def_Count += 1;
                                    entities.get(index).setResult(5);
                                    LogUtils.e("lime== 入库失败--其他值：未知错误" + entities.get(index).getFullName());
                                }
                                if (result.result == 0) {
                                    entities.get(index).setFaceToken(new String(result.faceToken, StandardCharsets.ISO_8859_1));
//                            unique.faceToken = String(result.faceToken)
                                    LogUtils.e("lime== 人脸token生成成功" + entities.get(index).getFullName() + entities.get(index).getPhone() + index + "///" + entities.get(index).getFaceToken());
                                    CompanyMemberBiz.updateCompanyMember(entities.get(index));
                                    try {
                                        boolean b = MainApplication.mFacePassHandler.bindGroup(
                                                PayConstants.GROUP_NAME,
                                                result.faceToken
                                        );
//                                        String result = b ? "success " : "failed";
//                                        LogUtils.e("addFace", "bind  $result")
                                    } catch (Exception e) {
                                        e.printStackTrace();
//                                toast(e.message)
                                    }
                                } else {
                                    LogUtils.e("lime== 人脸token生成失败" + entities.get(index).getFullName() + "///" + entities.get(index).getFaceToken());
                                    CompanyMemberBiz.updateCompanyMember(entities.get(index));
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e("lime== addFace", entities.get(index).getFullName() + e.getMessage());
                            // toast(e.message)
                        } finally {
                            //base64ToBitmap?.recycle()


                        }

                    }

                    LogUtils.e("lime*** 下发人脸中 479");

                    downFaceFail(entities.get(index), 0);


                    if (index == entities.size() - 1) {
                        // Thread.sleep(1000)
                        callBack = false;
//                    isDownFace = false

                    }
                }

                LogUtils.w("lime============ face_0_Count: " + face_0_Count);
                LogUtils.w("lime============ face_1_Count: " + face_1_Count);
                LogUtils.w("lime============ face_2_Count: " + face_2_Count);
                LogUtils.w("lime============ face_def_Count: " + face_def_Count);
                LogUtils.w("lime============ totalFaceCount: " + totalFaceCount);


                //   companyMember()



                try {
                    LogUtils.e("lime*** 下发人脸中 507");
                    if (latch != null){
                        latch.await();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                 // 等待所有请求完成
                //LogUtils.e("人脸录入调用companyMember方法2")
                companyMember(1);
//            }
            }
        }).start();


    }

    private void showAllFsceProgressDialog() {
        LogUtils.e("全量下发蒙层");

        View view = View.inflate(this, R.layout.dialog_all_face_progress, null);

        allFaceDownDialog.show();
        LogUtils.e("显示全量下发蒙层show");
        allFaceDownDialog.getWindow().setLayout(
                (int) (ScreenUtils.getAppScreenWidth() * 0.32),
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        Objects.requireNonNull(allFaceDownDialog.getWindow()).setContentView(view);
    }


    private void downFaceFail(CompanyMemberdbEntity item,int isFinish) {
        TreeMap<String, String> map = new TreeMap<>();
        String deviceId = DeviceManager.INSTANCE.getDeviceInterface().getMachineNumber();
        map.put("mode", "DownFaceFail");
        map.put("customerId", item.getUniqueNumber());
        if (item.getResult() == null) {
            item.setResult(1);
            map.put("errorType", String.valueOf(1));
        } else {
            map.put("errorType", String.valueOf(item.getResult()));
        }
        map.put("isFinish",String.valueOf(isFinish));
        map.put("machine_Number", deviceId);
        String md5 =  EncryptUtils.encryptMD5ToString16(item.getUniqueNumber() + "&" + item.getResult() + "&" + isFinish + "&" + deviceId);

        map.put("sign", md5);
        RetrofitManager.INSTANCE.getDefaultRetrofit()
                .create(SettingService.class)
                .downFaceFail(ParamsUtils.signSortParamsMap(map))
                .compose(RxTransformerUtils.mainSchedulers())
                .to(AutoDisposeUtils.onDestroyDispose(this))
                .subscribe(new DefaultObserver<BaseNetResponse<String>>() {
                    @Override
                    protected void onSuccess(BaseNetResponse<String> statData) {
                        LogUtils.d("lime == downFaceFail: " + (new Gson()).toJson(statData));
                        if (statData != null && statData.getCode().equals("10000") && statData.getData() != null) {
                            if (latch != null) {
                                latch.countDown(); // 每个请求完成后递减计数器
                                Log.d(TAG,"callBack计数器 " +"==$callBack" + latch.getCount());
                            }
                            LogUtils.e("companyMemberStatus observe");

                        }


                    }
                });
    }

    private void company() {
        TreeMap<String, String> map = new TreeMap<>();
        String deviceId = DeviceManager.INSTANCE.getDeviceInterface().getMachineNumber();
        map.put("mode", "company_setup");
        map.put("machine_Number", deviceId);
        String md5 = EncryptUtils.encryptMD5ToString16(deviceId);
        map.put("sign", md5);
        RetrofitManager.INSTANCE.getDefaultRetrofit()
                .create(SettingService.class)
                .companySetup(ParamsUtils.signSortParamsMap(map))
                .compose(RxTransformerUtils.mainSchedulers())
                .to(AutoDisposeUtils.onDestroyDispose(this))
                .subscribe(new DefaultObserver<BaseNetResponse<DeviceNameBean>>() {
                    @Override
                    protected void onSuccess(BaseNetResponse<DeviceNameBean> statData) {
                        LogUtils.d("lime == getIntervalCardType: " + (new Gson()).toJson(statData));
                        if (statData != null && statData.getCode().equals("10000") && statData.getData() != null) {
                            LogUtils.e("offlineSet observe");
                        }


                    }
                });
    }

    private void offlineSet() {
        TreeMap<String, String> map = new TreeMap<>();
        String deviceId = DeviceManager.INSTANCE.getDeviceInterface().getMachineNumber();
        map.put("mode", "OfflineSet");
        map.put("machine_Number", deviceId);
        String md5 = EncryptUtils.encryptMD5ToString16(deviceId);
        map.put("sign", md5);
        RetrofitManager.INSTANCE.getDefaultRetrofit()
                .create(SettingService.class)
                .offlineSet(ParamsUtils.signSortParamsMap(map))
                .compose(RxTransformerUtils.mainSchedulers())
                .to(AutoDisposeUtils.onDestroyDispose(this))
                .subscribe(new DefaultObserver<BaseNetResponse<OfflineSetBean>>() {
                    @Override
                    protected void onSuccess(BaseNetResponse<OfflineSetBean> statData) {
                        Log.d(TAG, "lime == getIntervalCardType: " + (new Gson()).toJson(statData));

                        if (statData != null && statData.getCode().equals("10000") && statData.getData() != null) {
                            // TODO: EventBus
                            // EventBus.getDefault().post(
                            //    new MessageEventBean(
                            //        MessageEventType.CompanyName,
                            //        it.getCompany(),
                            //        it.getData() != null ? it.getData().getDeviceName() : null
                            //    )
                            //);

                        }


                    }
                });
    }

    private void clickVersionUpdate() {
        AppUpgradeHelper appUpgradeHelper = getWeakRefHolder(AppUpgradeHelper.class);
        appUpgradeHelper.setOnAppUpgradeListener(MainActivity1.this);
        appUpgradeHelper.checkAppVersion();
    }


    private void getIntervalCardType() {
        TreeMap<String, String> map = new TreeMap<>();
        String deviceId = DeviceManager.INSTANCE.getDeviceInterface().getMachineNumber();
        map.put("mode", "GetIntervalCardType");
        map.put("machine_Number", deviceId);
        String md5 = EncryptUtils.encryptMD5ToString16(deviceId);
        map.put("sign", md5);
        RetrofitManager.INSTANCE.getDefaultRetrofit()
                .create(SettingService.class)
                .getIntervalCardType(ParamsUtils.signSortParamsMap(map))
                .compose(RxTransformerUtils.mainSchedulers())
                .to(AutoDisposeUtils.onDestroyDispose(this))
                .subscribe(new DefaultObserver<BaseNetResponse<List<IntervalCardTypeBean>>>() {
                    @Override
                    protected void onSuccess(BaseNetResponse<List<IntervalCardTypeBean>> statData) {
                        Log.d(TAG, "lime == getIntervalCardType: " + (new Gson()).toJson(statData));

                        if (statData != null && statData.getCode().equals("10000") && statData.getData() != null) {
                            MainApplication.intervalCardType = statData.getData();
                        } else {


                        }


                    }
                });

    }

    @SuppressLint("AutoDispose")
    private void requestConsumptionType() {
        if (currentTimeDisposable != null) {
            currentTimeDisposable.dispose();
        }
        currentTimeDisposable = Observable.interval(0, 60, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    //一分钟检查金额模式 固定模式
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        checkCurrentAmountMode();
                    }
                });




    }


    @RequiresApi(Build.VERSION_CODES.O)
    private void checkCurrentAmountMode() {
        Log.d(TAG,"checkCurrentAmountMode start");
        boolean switchFixAmount = PaymentSettingMMKV.getSwitchFixAmount();
        if (switchFixAmount) {
            String currentFixAmountTime = PaymentSettingMMKV.getcurrentFixAmountTime();
            if (!TextUtils.isEmpty(currentFixAmountTime)) {
                List<String> split = List.of(currentFixAmountTime.split("-"));
                if (split.size() >= 2) {
                    LogUtils.e("checkCurrentAmountMode RefreshFixAmountMode");
                    if (!TimeUtils.isCurrentTimeIsInRound(split.get(0), split.get(1))) {
                        // TODO: EventBus.getDefault().post(new MessageEventBean(MessageEventType.RefreshFixAmountMode));
                        //EventBus.getDefault().post(new MessageEventBean(MessageEventType.RefreshFixAmountMode));
                    }
                }
            } else {
                LogUtils.e("checkCurrentAmountMode RefreshFixAmountMode empty currentFixAmountTime");
                 // TODO: EventBus.getDefault().post(new MessageEventBean(MessageEventType.RefreshFixAmountMode));
                //EventBus.getDefault().post(new MessageEventBean(MessageEventType.RefreshFixAmountMode));
            }
        }
        Log.d(TAG,"checkCurrentAmountMode end");
    }

    private void findViews() {
        h1tlMain = (Home1TitleLayout) findViewById(R.id.h1tl_main);
        //系统事件监听
        SystemEventWatcherHelper systemEventWatcherHelper = getWeakRefHolder(SystemEventWatcherHelper.class);
        if (systemEventWatcherHelper != null) {
            systemEventWatcherHelper.addSystemEventListener(h1tlMain);
        }
    }

    private void initData() {
        initHomeContent();
    }

    @Override
    public void onNetInitSuccess() {
        hideLoadingDialog();
        initHomeContent();

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

                    }
                });
    }



    /**
     * 加载主页内容
     */
    private void initHomeContent() {
        amountFragment = new Amount1ConsumerFragment();
        consumption1SettingFragment = new Consumption1SettingFragment();
        consumption2SettingFragment = new Consumption2SettingFragment();
        consumeStatFragment = new ConsumeStatFragment();


        amountFragment.setViewVisibleOnScreen(true);
        consumption1SettingFragment.setViewVisibleOnScreen(false);
        consumption2SettingFragment.setViewVisibleOnScreen(false);
        consumeStatFragment.setViewVisibleOnScreen(false);

        baseFragmentList.add(amountFragment);
        baseFragmentList.add(consumption1SettingFragment);
        baseFragmentList.add(consumption2SettingFragment);
        baseFragmentList.add(consumeStatFragment);


        //添加主页内容
        FragmentUtils.safeReplaceFragment(getSupportFragmentManager(), amountFragment, R.id.fl_content);
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
        //网络状态回调
        SystemEventWatcherHelper systemEventWatcherHelper = getWeakRefHolder(SystemEventWatcherHelper.class);
        countDownHelper.addCountDownListener(systemEventWatcherHelper);
        hasInitMenuData = true;

        amountFragment.getSc1Calc().setKeyboardListener(new Simple1Calculator.KeyboardListener() {
            @Override
            public void settings() {
                // 设置
                FragmentUtils.safeReplaceFragment(getSupportFragmentManager(), consumption1SettingFragment, R.id.fl_place_holder_content);
                setVisibleOnScreen(consumption1SettingFragment);
            }

            @Override
            public void stat() {
                Log.d(TAG, "lime==== settings: 262");
                // 统计
                FragmentUtils.safeReplaceFragment(getSupportFragmentManager(), consumeStatFragment, R.id.fl_place_holder_content);
                setVisibleOnScreen(consumeStatFragment);

            }


        });

        consumption1SettingFragment.setKeyboardListener(new Consumption1SettingFragment.KeyboardListener() {
            @Override
            public void back() {
                FragmentUtils.safeRemoveFragment(getSupportFragmentManager(), consumption1SettingFragment);
                setVisibleOnScreen(amountFragment);
            }

            @Override
            public void dingESettings() {
                FragmentUtils.safeReplaceFragment(getSupportFragmentManager(), consumption2SettingFragment, R.id.fl_place_holder_content);
                setVisibleOnScreen(consumption2SettingFragment);
            }
        });


        consumption2SettingFragment.setKeyboardListener(new Consumption2SettingFragment.KeyboardListener() {
            @Override
            public void back() {
                FragmentUtils.safeReplaceFragment(getSupportFragmentManager(), consumption1SettingFragment, R.id.fl_place_holder_content);
                setVisibleOnScreen(consumption1SettingFragment);
            }

        });



        consumeStatFragment.setKeyboardListener(new ConsumeStatFragment.KeyboardListener() {
            @Override
            public void back() {
                FragmentUtils.safeRemoveFragment(getSupportFragmentManager(), consumeStatFragment);
                setVisibleOnScreen(amountFragment);

            }
        });


    }


    public void setVisibleOnScreen(BaseFragment fragment){

        // 遍历所有fragment
        for (BaseFragment baseFragment : baseFragmentList) {
            if (baseFragment.getClass().equals(fragment.getClass())){
                baseFragment.setViewVisibleOnScreen(true);
            }else{
                baseFragment.setViewVisibleOnScreen(false);
            }
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
    public void onCreateTitleLayout(Home1TitleLayout home1TitleLayout) {
        //系统事件监听
        SystemEventWatcherHelper systemEventWatcherHelper = getWeakRefHolder(SystemEventWatcherHelper.class);
        if (systemEventWatcherHelper != null) {
            systemEventWatcherHelper.addSystemEventListener(home1TitleLayout);
        }
        //添加设备信息更新回调
        StoreInfoHelper storeInfoHelper = getWeakRefHolder(StoreInfoHelper.class);
        if (storeInfoHelper != null) {
            storeInfoHelper.addGetStoreInfoListener(home1TitleLayout);
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        super.dispatchKeyEvent(event);
        Log.d(TAG,"---MainActivity1--dispatchKeyEvent--activity event: " + event);





//        if (event.getAction() == KeyEvent.ACTION_DOWN) {
//            try {
//                LogHelper.print("lime=== amountFragment.isViewVisibleOnScreen(): " + amountFragment.isViewVisibleOnScreen());
//                LogHelper.print("lime=== consumption1SettingFragment.isViewVisibleOnScreen(): " + consumption1SettingFragment.isViewVisibleOnScreen());
//                LogHelper.print("lime=== consumption2SettingFragment.isViewVisibleOnScreen(): " + consumption2SettingFragment.isViewVisibleOnScreen());
//                LogHelper.print("lime=== consumeStatFragment.isViewVisibleOnScreen(): " + consumeStatFragment.isViewVisibleOnScreen());
//            }catch (Exception e){
//
//            }
//
//        }

//        if (!placeHolderFragmentSet.isEmpty()) {
//            Fragment fragment = placeHolderFragmentSet.lastElement();
//            if (fragment instanceof DispatchKeyEventListener) {
//                DispatchKeyEventListener dispatchKeyEventListener = (BaseDispatchKeyEventFragment) fragment;
//                dispatchKeyEventListener.dispatchKeyEvent(event);
//            }
//        } else
        if (event.getAction() == KeyEvent.ACTION_UP) {
            if (amountFragment != null && amountFragment.isViewVisibleOnScreen()) {
                amountFragment.dispatchKeyEvent(event);
            } else if (consumption1SettingFragment != null && consumption1SettingFragment.isViewVisibleOnScreen()) {
                consumption1SettingFragment.dispatchKeyEvent(event);
            } else if (consumption2SettingFragment != null && consumption2SettingFragment.isViewVisibleOnScreen()) {
                consumption2SettingFragment.dispatchKeyEvent(event);
            } else if (consumeStatFragment != null && consumeStatFragment.isViewVisibleOnScreen()) {
                consumeStatFragment.dispatchKeyEvent(event);
            }
        }
        return true;
    }

    private Stack<Fragment> placeHolderFragmentSet = new Stack<>();

    @Override
    public void addContentPlaceHolderFragment(Fragment fragment) {
        //登录过期弹窗弹出，不处理其他弹窗
        if (LoginHelper.INSTANCE.isHandleLoginValid()) {
            return;
        }
        int contentPlaceHolderId = getContentPlaceHolderId();
        if (contentPlaceHolderId != 0) {
            try {
                fragment.getLifecycle().addObserver(new LifecycleEventObserver() {
                    @Override
                    public void onStateChanged(@NonNull LifecycleOwner lifecycleOwner, @NonNull Lifecycle.Event event) {
                        if (event == Lifecycle.Event.ON_DESTROY) {
                            LogHelper.print("--addContentPlaceHolderFragment--onStateChanged remove fragment " + fragment);
                            placeHolderFragmentSet.remove(fragment);
                        }
                    }
                });
                getSupportFragmentManager().beginTransaction()
                        .add(getContentPlaceHolderId(), fragment)
                        .commitNowAllowingStateLoss();
                //添加堆栈里面
                LogHelper.print("--addContentPlaceHolderFragment--add fragment " + fragment);
                placeHolderFragmentSet.push(fragment);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        } else {
            AppToast.toastMsg("添加内容失败");
        }
    }

    @Override
    public void onCheckVersionEnd(String msg) {

    }

    @Override
    public void onCheckVersionStart() {

    }

    @Override
    public void onCheckVersionError(String msg) {

    }

    @Override
    public void onNoVersionUpgrade() {

    }
}