package com.ruyiruyi.rylibrary.request;


public class RequestUtils {
    public static String REQUEST_URL_TEST = "http://192.168.1.131:10100/";
//    public static String REQUEST_URL_RELEASE = "http://218.201.136.183:12070/";//临沂生产环境
    public static String REQUEST_URL_RELEASE = "http://192.168.1.196:8444/";//省林保测试环境//"http://192.168.1.67:10100/";//临沂新测试环境
    public static String REQUEST__URL_tt = "http://119.3.230.205:10100/";           //黑龙江正式
    public static String REQUEST_URL_HUAWEI = "http://117.132.5.139:8011/";
    public static String REQUEST_URL = REQUEST_URL_RELEASE ;
    public static String LOGIN_URL = REQUEST_URL_RELEASE  ;

    public static String REQUEST_QUANXIAN = REQUEST_URL_RELEASE + "auth/";
//    public static String REQUEST_QUANXIAN = "http://172.16.10.69:10040/auth/";//姜
}
