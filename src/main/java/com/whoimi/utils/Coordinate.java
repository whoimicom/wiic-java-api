package com.whoimi.utils;

/**
 * @author whoimi
 * @since 2024-11-22
 **/
public class Coordinate {
    public Coordinate() {
        super();
    }

    public Coordinate(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    /**
     * 经度
     */
    private double longitude = 0;
    /**
     * 纬度
     */
    private double latitude = 0;

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    @Override
    public String toString() {
        return "Coordinate{" +
               "longitude=" + longitude +
               ", latitude=" + latitude +
               '}';
    }
}
