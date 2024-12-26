package com.stkj.cashier.base.utils;

import android.app.Activity;
import android.content.Intent;

import com.stkj.common.core.AppManager;
import com.stkj.common.utils.ActivityUtils;

/**
 * 启动入口工具类
 */
public class LauncherUtils {

    /**
     * 避免从桌面启动程序后，会重新实例化入口类的activity
     * 判断当前activity是不是所在任务栈的根
     */
    public static boolean needFinishLauncher(Activity launcherActivity) {
        Intent intent = launcherActivity.getIntent();
        if (intent != null) {
            String action = intent.getAction();
            //1.避免从桌面启动程序后，会重新实例化入口类的activity , 判断当前activity是不是所在任务栈的根
            if (!launcherActivity.isTaskRoot()) {
                if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && Intent.ACTION_MAIN.equals(action)) {
                    return true;
                }
            }
            //2.经过路由跳转的，判断当前应用是否已经初始化过，首页是否存在并且未销毁
            if (Intent.ACTION_VIEW.equals(action)) {
                Activity homeActivity = AppManager.INSTANCE.getMainActivity();
                if (!ActivityUtils.isActivityFinished(homeActivity)) {
                    return true;
                }
            }
        }
        return false;
    }

}
