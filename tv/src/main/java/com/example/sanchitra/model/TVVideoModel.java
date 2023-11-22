package com.example.sanchitra.model;

import com.google.gson.annotations.SerializedName;

public class TVVideoModel {

    @SerializedName("value")
    private final TVVideoModel.datum value = null;
    @SerializedName("success")
    private Boolean success;

    @SerializedName("cookies")
    private String cookies;

    public String getCookies() {
        return cookies;
    }

    public TVVideoModel.datum getValue() {return value;}

    public Boolean getSuccess() {
        return success;
    }

    public class datum {
        @SerializedName("low")
        public String low;
        @SerializedName("medium")
        public String medium;
        @SerializedName("high")
        public String high;
        @SerializedName("ultra_high")
        public String ultraHigh;

        public String getLow() {
            return low;
        }

        public String getMedium() {
            return medium;
        }

        public String getHigh() {
            return high;
        }

        public String getUltraHigh() {
            return ultraHigh;
        }

    }
}
