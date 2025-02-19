package com.ruyiruyi.rylibrary.utils;

import java.math.BigDecimal;

public class DistanceUtil {

    //平均半径,单位：m；不是赤道半径。赤道为6378左右
    private static final double EARTH_RADIUS = 6371000;

    public static BigDecimal getDistanceBigDecimalOneDecimalPlace(BigDecimal lat1, BigDecimal lng1, BigDecimal lat2, BigDecimal lng2) {
        return getDistanceBigDecimal(lat1, lng1, lat2, lng2).setScale(1, BigDecimal.ROUND_HALF_UP);
    }

    public static BigDecimal getDistanceBigDecimal(BigDecimal lat1, BigDecimal lng1, BigDecimal lat2, BigDecimal lng2) {
        //经纬度（角度）转弧度。弧度用作参数，以调用Math.cos和Math.sin
        BigDecimal radiansAX = new BigDecimal(Math.toRadians(lng1.doubleValue()));//A经弧度
        BigDecimal radiansAY = new BigDecimal(Math.toRadians(lat1.doubleValue()));//A纬弧度
        BigDecimal radiansBX = new BigDecimal(Math.toRadians(lng2.doubleValue()));//B经弧度
        BigDecimal radiansBY = new BigDecimal(Math.toRadians(lat2.doubleValue()));//B纬弧度
        //公式中“cosβ1cosβ2cos（α1-α2）+sinβ1sinβ2”的部分，得到∠AOB的cos值
        BigDecimal cos =  new BigDecimal(Math.cos(radiansAY.doubleValue()) * Math.cos(radiansBY.doubleValue()) * Math.cos(radiansAX.doubleValue() - radiansBX.doubleValue())
                + Math.sin(radiansAY.doubleValue()) * Math.sin(radiansBY.doubleValue()));
//        log.info("cos = " + cos);//值域[-1,1]
        BigDecimal acos = new BigDecimal(Math.acos(cos.doubleValue()));//反余弦值
//        log.info("acos = " + acos);//值域[0,π]
//        log.info("∠AOB = " + Math.toDegrees(acos));//球心角 值域[0,180]
        return new BigDecimal(EARTH_RADIUS).multiply(acos);//最终结果
    }

    public static double getDistanceDouble(Double lat1, Double lng1, Double lat2, Double lng2) {
        //经纬度（角度）转弧度。弧度用作参数，以调用Math.cos和Math.sin
        double radiansAX = Math.toRadians(lng1);//A经弧度
        double radiansAY = Math.toRadians(lat1);//A纬弧度
        double radiansBX = Math.toRadians(lng2);//B经弧度
        double radiansBY = Math.toRadians(lat2);//B纬弧度
        //公式中“cosβ1cosβ2cos（α1-α2）+sinβ1sinβ2”的部分，得到∠AOB的cos值
        double cos = Math.cos(radiansAY) * Math.cos(radiansBY) * Math.cos(radiansAX - radiansBX) + Math.sin(radiansAY) * Math.sin(radiansBY);
//        log.info("cos = " + cos);//值域[-1,1]
        double acos = Math.acos(cos);//反余弦值
//        log.info("acos = " + acos);//值域[0,π]
//        log.info("∠AOB = " + Math.toDegrees(acos));//球心角 值域[0,180]
        return EARTH_RADIUS * acos;//最终结果
    }

    public static void main(String[] args) {
        System.out.println("距离" + getDistanceDouble(31.22814, 121.400136,31.229016, 121.398455)  + "米");
        System.out.println("距离" + getDistanceBigDecimal(new BigDecimal("31.22814"), new BigDecimal("121.400136"),
                new BigDecimal("31.229016"), new BigDecimal("121.398455"))  + "米");
        System.out.println("距离" + getDistanceBigDecimalOneDecimalPlace(new BigDecimal("31.22814"), new BigDecimal("121.400136"),
                new BigDecimal("31.229016"), new BigDecimal("121.398455"))  + "米");
    }

}
