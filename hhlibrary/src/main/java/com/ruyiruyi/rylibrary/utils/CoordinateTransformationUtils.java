package com.ruyiruyi.rylibrary.utils;

import java.util.HashMap;
import java.util.Map;

public class CoordinateTransformationUtils {

    public final static double x_pi = 3.14159265358979324 * 3000.0 / 180.0;

    /**
     * 高德坐标转百度坐标
     *
     * @param gd_lon 经度
     * @param gd_lat 纬度
     * @return
     */
    public static Map<String, Double> gd2bd(double gd_lon, double gd_lat) {
        Map<String, Double> data = new HashMap<>();
        double x = gd_lon, y = gd_lat;
        double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * x_pi);
        double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * x_pi);
        double bd_lon = z * Math.cos(theta) + 0.0065;
        double bd_lat = z * Math.sin(theta) + 0.006;
        data.put("lon", bd_lon);
        data.put("lat", bd_lat);
        return data;
    }


    /**
     * 百度坐标转换高德坐标
     *
     * @param bd_lon 经度
     * @param bd_lat 纬度
     * @return
     */
    public static Map<String, Double> bd2gd(double bd_lon, double bd_lat) {
        double x = bd_lon - 0.0065, y = bd_lat - 0.006;
        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * x_pi);
        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * x_pi);
        double gd_lon = z * Math.cos(theta);
        double gd_lat = z * Math.sin(theta);
        Map<String, Double> data = new HashMap<>();
        data.put("lon", gd_lon);
        data.put("lat", gd_lat);
        return data;
    }

}
