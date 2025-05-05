package com.ruyiruyi.rylibrary.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import com.ruyiruyi.rylibrary.db.DbConfig;

import java.util.Objects;

/**
 * Created by qc
 * on 2022/8/4.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class CommonUtil {

    /**
     * 权限判断
     * @param context 上下文
     * @param permissionCode 权限编码
     * @return
     */
    public static boolean hasPermission(Context context , String permissionCode){
        String permissions = new DbConfig(context).getPermissions();
        Log.e("TAG", "hasPermission: " + permissions );
        if(permissions == null){
            return false;
        }

        return permissions.contains(permissionCode+"_");
    }

    public static void enterWhiteListSetting(Context context){
        try {
            context.startActivity(getAutostartSettingIntent(context));
        }catch (Exception e){
            context.startActivity(new Intent(Settings.ACTION_SETTINGS));
        }
    }

    /**
     * 获取自启动管理页面的Intent
     * @param context context
     * @return 返回自启动管理页面的Intent
     * */
    public static Intent getAutostartSettingIntent(Context context) {
        ComponentName componentName = null;

        String brand = Build.MANUFACTURER;

        Intent intent = new Intent();

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        switch (brand.toLowerCase()) {
            case "samsung"://三星

                componentName = new ComponentName("com.samsung.android.sm", "com.samsung.android.sm.app.dashboard.SmartManagerDashBoardActivity");

                break;

            case "huawei"://华为

//荣耀V8，EMUI 8.0.0，Android 8.0上，以下两者效果一样

                componentName = new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.appcontrol.activity.StartupAppControlActivity");

// componentName = new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity");//目前看是通用的

                break;

            case "xiaomi"://小米

                componentName = new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity");

                break;

            case "vivo"://VIVO

// componentName = new ComponentName("com.iqoo.secure", "com.iqoo.secure.safaguard.PurviewTabActivity");

                componentName = new ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity");

                break;

            case "oppo"://OPPO

// componentName = new ComponentName("com.oppo.safe", "com.oppo.safe.permission.startup.StartupAppListActivity");

                componentName = new ComponentName("com.coloros.oppoguardelf", "com.coloros.powermanager.fuelgaue.PowerUsageModelActivity");

                break;

            case "yulong":

            case "360"://360

                componentName = new ComponentName("com.yulong.android.coolsafe", "com.yulong.android.coolsafe.ui.activity.autorun.AutoRunListActivity");

                break;

            case "meizu"://魅族

                componentName = new ComponentName("com.meizu.safe", "com.meizu.safe.permission.SmartBGActivity");

                break;

            case "oneplus"://一加

                componentName = new ComponentName("com.oneplus.security", "com.oneplus.security.chainlaunch.view.ChainLaunchAppListActivity");

                break;

            case "letv"://乐视

                intent.setAction("com.letv.android.permissionautoboot");

            default://其他

                intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");

                intent.setData(Uri.fromParts("package", context.getPackageName(), null));

                break;

        }

        intent.setComponent(componentName);

        return intent;

    }

    public static String parseNull(String value, String def) {
        if(value == null || Objects.equals(value, "null") || value.contains("null")){
            return def;
        }
        return value;
    }
}
