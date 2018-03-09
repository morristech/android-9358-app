package com.xmd.chat.xmdchat.messagebean;

/**
 * Created by Lhj on 18-1-26.
 */

public class ClubLocationMessageBean {
    private String lat;
    private String lng;
    private String address;
    private String staticMap;

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStaticMap() {
        return staticMap;
    }

    public void setStaticMap(String staticMap) {
        this.staticMap = staticMap;
    }

}
