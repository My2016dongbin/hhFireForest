package com.ruyiruyi.rylibrary.request;

public class RequestUtils {
    public static String IP = "112.6.162.92";//"10.10.2.23";//"58.58.115.2";//"218.201.180.118";
    public static int PORT = 18444;///8011;//8444;//10100;
//    public static String IP = "58.58.115.2";//"10.10.2.23";//"58.58.115.2";//"218.201.180.118";
//    public static int PORT = 8444;///8011;//8444;//10100;
//    public static String IP = "192.168.1.67";//"10.10.2.23";//"58.58.115.2";//"218.201.180.118";
//    public static int PORT = 8444;///8011;//8444;//10100;
    public static String REQUEST_URL_TEST = "http://192.168.1.131:10200/";//测试

    public static String REQUEST_URL = REQUEST_URL_BASE();
    public static String LOGIN_URL = REQUEST_URL_BASE();

    public static String REQUEST_URL_BASE(){
        return "http://" + IP + ":" + PORT + "/";//正式;
    }
    public static String REQUEST_QUANXIAN(){
        return REQUEST_URL_BASE() + "auth/";
    }
}
