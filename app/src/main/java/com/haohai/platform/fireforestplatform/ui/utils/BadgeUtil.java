package com.haohai.platform.fireforestplatform.ui.utils;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.haohai.platform.fireforestplatform.MyApplication;
import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.ui.receiver.TengxunReceiver;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

import static com.iflytek.speech.UtilityConfig.CHANNEL_ID;

public class BadgeUtil {
    private static final String TAG = BadgeUtil.class.getSimpleName();

    public static void setBadgeCount(Context context, int count) {
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = Uri.parse("content://com.android.badge/badge");
        ContentValues contentValues = new ContentValues();
        contentValues.put("package", context.getPackageName());
        contentValues.put("class", getLauncherClassName(context));
        contentValues.put("badgecount", count);

        ///检查URI是否可用
        Cursor cursor = null;
        try {
            cursor = contentResolver.query(uri, null, null, null, null);
            contentResolver.update(uri, contentValues, null, null);
            return;  // 只要查询成功，就说明 URI 可用
        } catch (Exception e) {
            return;  // 捕获异常，说明 URI 不支持
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private static String getLauncherClassName(Context context) {
        PackageManager packageManager = context.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(intent, 0);
        for (ResolveInfo resolveInfo : resolveInfos) {
            String packageName = resolveInfo.activityInfo.packageName;
            if (packageName.equalsIgnoreCase(context.getPackageName())) {
                return resolveInfo.activityInfo.name;
            }
        }
        return null;
    }

    /**
     * 根据手机厂商设置角标数量
     * @param num
     */
    public static void setBadge(int num){
        //三星 samsung，OPPO旗下 realme，OPPO oppo，一加 oneplus，华为 huawei，小米 xiaomi，红米 redmi，荣耀 honor，vivo vivo，
        String brand = Build.BRAND.toLowerCase();
        Log.e(TAG, "型号 " + brand);
        if(Objects.equals(brand, "samsung") || Objects.equals(brand, "lg")){
            //三星 开启权限后默认自带

        }else if (Objects.equals(brand, "huawei")){
            //华为
            setHuaWeiBadgeNum(1);

        }else if (Objects.equals(brand, "xiaomi") || Objects.equals(brand, "redmi")){
            //小米
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                setXiaoMiBadgeNum(1);
            }
        }else if (Objects.equals(brand, "oppo") || Objects.equals(brand, "realme")){
            //OPPO 暂不支持

        }else if (Objects.equals(brand, "oneplus")){
            //一加-oppo 暂不支持

        }else if (Objects.equals(brand, "honor")){
            //荣耀
            setHonorBadgeNum(1);
        }else if (Objects.equals(brand, "meizu")){
            //魅族 暂不支持

        }else if (Objects.equals(brand, "vivo")){
            //vivo
            setVIVOBadgeNum(1);
        }else{
            setBadgeCount(MyApplication.getInstance(),1);
        }
    }



    public static final int FLAG_RECEIVER_INCLUDE_BACKGROUND = 0x01000000;
    /**
     * Vivo角标
     * @param num
     */
    @SuppressLint("WrongConstant")
    public static void setVIVOBadgeNum(int num) {
        Intent intent = new Intent();
        intent.setAction("launcher.action.CHANGE_APPLICATION_NOTIFICATION_NUM");
        intent.putExtra("packageName", "com.haohai.platform.fireforestplatform");
        intent.putExtra("className", "com.haohai.platform.fireforestplatform.ui.acticity.LauncherActivity");
        intent.putExtra("notificationNum", num);
        intent.addFlags(FLAG_RECEIVER_INCLUDE_BACKGROUND);
        MyApplication.getInstance().sendBroadcast(intent);
    }

    /**
     * 华为角标
     * @param num
     */
    public static void setHuaWeiBadgeNum(int num) {
        try {
            Bundle bunlde = new Bundle();
            bunlde.putString("package", "com.haohai.platform.fireforestplatform"); // com.test.badge is your package name
            bunlde.putString("class", "com.haohai.platform.fireforestplatform.ui.acticity.LauncherActivity"); // com.test. badge.MainActivity is your apk main activity
            bunlde.putInt("badgenumber", num);
            MyApplication.getInstance().getContentResolver().call(Uri.parse("content://com.huawei.android.launcher.settings/badge/"), "change_badge", null, bunlde);
        } catch (Exception e) {

            Log.e(TAG, e.getMessage() );
        }
    }
    /**
     * 荣耀角标
     * @param num
     */
    public static void setHonorBadgeNum(int num) {
        try {
            Bundle bunlde = new Bundle();
            bunlde.putString("package", "com.haohai.platform.fireforestplatform"); // com.test.badge is your package name
            bunlde.putString("class", "com.haohai.platform.fireforestplatform.ui.acticity.LauncherActivity"); // com.test. badge.MainActivity is your apk main activity
            bunlde.putInt("badgenumber", num);
            MyApplication.getInstance().getContentResolver().call(Uri.parse("content://com.hihonor.android.launcher.settings/badge/"), "change_badge", null, bunlde);
        } catch (Exception e) {

            Log.e(TAG, e.getMessage() );
        }
    }

    /**
     * 小米角标
     * @param num
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void setXiaoMiBadgeNum(int num) {
        try {
            //MIUI6-MIUI11桌面应用角标适配方法
            Notification notification = new Notification.Builder(MyApplication.getInstance(), CHANNEL_ID)
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setContentTitle("提醒")
                    .setContentText("您有新的消息").build();
            Field field = notification.getClass().getDeclaredField("extraNotification");
            Object extraNotification = field.get(notification);
            Method method = extraNotification.getClass().getDeclaredMethod("setMessageCount", int.class);
            method.invoke(extraNotification, num);
        } catch (Exception e) {
            //MIUI12及以后桌面应用角标适配方法
            try{
                Notification notification = new Notification.Builder(MyApplication.getInstance(), CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher_round)
                        .setContentTitle("提醒")
                        .setContentText("您有新的消息")
                        .setNumber(num)
                        .build();
            }catch (Exception e2){
                Log.e(TAG, e2.getMessage() );
            }
        }
    }
}