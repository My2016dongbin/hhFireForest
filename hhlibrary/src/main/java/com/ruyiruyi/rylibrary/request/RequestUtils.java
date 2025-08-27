package com.ruyiruyi.rylibrary.request;

/**
 * Created by geyang on 2020/6/2.
 */

public class RequestUtils {
//    public static String REQUEST__URL_HLJ = "http://220.170.165.1:8011/";//release
    public static String REQUEST__URL_HLJ = "http://172.16.200.32:8011/";//release 新疆七师内网
//    public static String REQUEST__URL_HLJ = "http://117.132.5.139:18012/";//release

    public static String REQUEST_UPLOAD = REQUEST__URL_HLJ + "oa/api/workReport/fileUploadAnByNotToken" ;
    public static String REQUEST_URL = REQUEST__URL_HLJ ;
    public static String LOGIN_URL = REQUEST__URL_HLJ  ;

    public static String REQUEST_QUANXIAN = REQUEST__URL_HLJ + "auth/";//release
}
