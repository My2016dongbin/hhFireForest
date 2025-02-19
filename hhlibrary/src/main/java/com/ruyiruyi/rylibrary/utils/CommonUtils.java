package com.ruyiruyi.rylibrary.utils;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.ruyiruyi.rylibrary.db.DbConfig;

import java.util.Objects;

public class CommonUtils {
    public String parseNull(String str,String defStr){
        boolean result = false;
        if(str == null){
            result = true;
        }
        if(Objects.equals(str, "null")){
            result = true;
        }
        if(Objects.equals(str, "")){
            result = true;
        }
        return result?defStr:str;
    }
    public String parseDate(String str){
        if(str.length()<19){
            return str.replace("null","").replace("T"," ");
        }else{
            return str.substring(0,19).replace("null","").replace("T"," ");
        }
    }

    /**
     * 权限判断
     * @param context 上下文
     * @param permissionCode 权限编码
     * @return
     */
    public static boolean hasPermission(Context context , String permissionCode){
        /*String permissions = new DbConfig(context).getPermissions();
        Log.e("TAG", "hasPermission: " + permissions );
        if(permissions == null){
            return false;
        }

        return permissions.contains(permissionCode+"_");*/
        return true;
    }
    public String parseZero(int num){
        if(num > 9){
           return num +"";
        }else {
            return "0" + num;
        }
    }
}
