package com.example.sanchitra.api;

public class UserInit {

    final String uid;
    final String origin;
    final String os;
    final String version;

    final String deviceType;

    public UserInit(String uid, String origin, String os, String version, String deviceType) {
        this.uid = uid;
        this.origin = origin;
        this.os = os;
        this.version = version;
        this.deviceType = deviceType;
    }
}
