package com.whoimi.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 坐标计算
 *
 * @author whoimi
 * @since 2024-11-22
 */
public class PositionUtil {

    /**
     * 赤道半径[米]
     */
    private static final double EQUATOR_RADIUS = 6378137;
    /**
     * 地球平均半径[米]
     */
    private static final double EARTH_AVG_RADIUS = 6371000;

    /**
     * 坐标计算距离[高德]
     *
     * @param source source
     * @param target target
     * @return distance metres.00
     */
    public static double computeDistance(Coordinate source, Coordinate target) {
        return computeDistance(source.getLongitude(), source.getLatitude(), target.getLongitude(), target.getLatitude());
    }

    /**
     * 反余弦计算方式
     */
    public static double computeDistance(double sourceLongitude, double sourceLatitude, double targetLongitude, double targetLatitude) {
        double lat1 = Math.toRadians(sourceLatitude);
        double lat2 = Math.toRadians(targetLatitude);
        double lon1 = Math.toRadians(sourceLongitude);
        double lon2 = Math.toRadians(targetLongitude);
        double a = lat1 - lat2;
        double b = lon1 - lon2;
        double distance = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin(b / 2), 2)));
        distance = distance * EQUATOR_RADIUS;
        return new BigDecimal(distance).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    /**
     * 经纬度计算距离[高德]
     *
     * @param sourceLongitude source longitude
     * @param sourceLatitude  source latitude
     * @param targetLongitude target longitude
     * @param targetLatitude  target latitude
     * @return distance metres.00
     */
    public static double computeDistanceGd(double sourceLongitude, double sourceLatitude, double targetLongitude, double targetLatitude) {
        if (sourceLongitude == 0 || sourceLatitude == 0 || targetLatitude == 0 || targetLongitude == 0) {
            return -1.0;
        }
        sourceLongitude *= 0.01745329251994329;
        sourceLatitude *= 0.01745329251994329;
        targetLongitude *= 0.01745329251994329;
        targetLatitude *= 0.01745329251994329;
        double var1 = Math.sin(sourceLongitude);
        double var2 = Math.sin(sourceLatitude);
        double var3 = Math.cos(sourceLongitude);
        double var4 = Math.cos(sourceLatitude);
        double var5 = Math.sin(targetLongitude);
        double var6 = Math.sin(targetLatitude);
        double var7 = Math.cos(targetLongitude);
        double var8 = Math.cos(targetLatitude);
        double[] var10 = new double[3];
        double[] var20 = new double[3];
        var10[0] = var4 * var3;
        var10[1] = var4 * var1;
        var10[2] = var2;
        var20[0] = var8 * var7;
        var20[1] = var8 * var5;
        var20[2] = var6;

        double distance = Math.asin(Math.sqrt((var10[0] - var20[0]) * (var10[0] - var20[0]) + (var10[1] - var20[1]) * (var10[1] - var20[1]) + (var10[2] - var20[2]) * (var10[2] - var20[2])) / 2.0) * 1.27420015798544E7;
        return new BigDecimal(distance).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }

    /**
     * 经纬度计算距离[Google]
     *
     * @param sourceLongitude source longitude
     * @param sourceLatitude  source latitude
     * @param targetLongitude target longitude
     * @param targetLatitude  target latitude
     * @return distance metres.00
     */
    public static double computeDistanceGoogle(double sourceLongitude, double sourceLatitude, double targetLongitude, double targetLatitude) {
        double radLat1 = rad(sourceLatitude);
        double radLat2 = rad(targetLatitude);
        double a = radLat1 - radLat2;
        double b = rad(sourceLongitude) - rad(targetLongitude);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_AVG_RADIUS;
        s = Math.round(s * 10000d) / 10000d;
        return s;
    }

    public static void main(String[] args) {
        Coordinate source = new Coordinate(106.623495, 30.293617);
        Coordinate target = new Coordinate(106.568086, 29.645704);
        System.out.println(computeDistance(source, target));
    }

}