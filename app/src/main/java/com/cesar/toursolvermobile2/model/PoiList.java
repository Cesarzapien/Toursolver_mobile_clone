package com.cesar.toursolvermobile2.model;

public class PoiList {
    private String key_poi;
    private double latitude;
    private double longitude;

    public String getKey_poi() {
        return key_poi;
    }

    public void setKey_poi(String key_poi) {
        this.key_poi = key_poi;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public PoiList(String key_poi, double latitude, double longitude) {
        this.key_poi = key_poi;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public PoiList() {
    }

    @Override
    public String toString() {
        return "PoiList{" +
                "key_poi='" + key_poi + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
