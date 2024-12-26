package com.stkj.cashier.setting.service;

import com.stkj.cashier.base.model.BaseNetResponse;
import com.stkj.cashier.setting.model.CheckAppVersion;
import com.stkj.cashier.setting.model.CompanyMemberBean;
import com.stkj.cashier.setting.model.DeviceNameBean;
import com.stkj.cashier.setting.model.FacePassPeopleListInfo;
import com.stkj.cashier.setting.model.IntervalCardTypeBean;
import com.stkj.cashier.setting.model.OfflineSetBean;
import com.stkj.cashier.setting.model.ReportDeviceStatusBean;
import com.stkj.cashier.stat.model.ConsumeStatBean;

import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface SettingService {
    /**
     * 设备录入人员信息接口
     */
    @GET("home/v3/index")
    Observable<BaseNetResponse<FacePassPeopleListInfo>> getAllFacePass(@QueryMap Map<String, String> requestParams);

    /**
     * 设备录入人员回调
     */
    @GET("home/v3/index")
    Observable<BaseNetResponse<String>> facePassCallback(@QueryMap Map<String, String> requestParams);

    /**
     * 设备录入时段接口
     */
    @GET("home/v2/index")
    Observable<BaseNetResponse<List<IntervalCardTypeBean>>> getIntervalCardType(@QueryMap Map<String, String> requestParams);

    /**
     * 设备查询心跳接口
     */
    @GET("home/v2/index")
    Observable<BaseNetResponse<ReportDeviceStatusBean>> reportDeviceStatus(@QueryMap Map<String, String> requestParams);

    /**
     * 设备录入人员回调(同步)
     */
    @GET("home/v3/index")
    Call<BaseNetResponse<String>> syncFacePassCallback(@QueryMap Map<String, String> requestParams);


    /**
     * 检查更新
     */
    @GET("home/v2/index")
    Observable<BaseNetResponse<CheckAppVersion>> checkAppVersion(@QueryMap Map<String, String> requestParams);


    /**
     * 设备 APP 升级回调接口
     */
    @GET("home/v2/index")
    Call<BaseNetResponse<String>> appUpgradeCallback(@QueryMap Map<String, String> requestParams);


    /**
     * 设备录入公司名称接口
     */
    @GET("home/v2/index")
    Observable<BaseNetResponse<DeviceNameBean>> companySetup(@QueryMap Map<String, String> requestParams);

    /**
     * 设备录入人员信息接口
     */
    @GET("home/v2/index")
    Observable<BaseNetResponse<CompanyMemberBean>> companyMember(@QueryMap Map<String, String> requestParams);

    /**
     * 设备录入脱机参数接口
     */
    @GET("home/v2/index")
    Observable<BaseNetResponse<OfflineSetBean>> offlineSet(@QueryMap Map<String, String> requestParams);

    /**
     * 设备录入人员回调接口
     */
    @GET("home/v2/index")
    Observable<BaseNetResponse<String>> downFaceFail(@QueryMap Map<String, String> requestParams);

}
