package com.example.sanchitra.model;

import com.google.gson.annotations.SerializedName;

public class TVContentModel {

    @SerializedName("channel_id")
    private Integer channelId;
    @SerializedName("channel_order")
    private String channelOrder;
    @SerializedName("channel_name")
    private String channelName;
    @SerializedName("channel_category")
    private String channelCategory;

    @SerializedName("logoUrl")
    private String logoUrl;
    @SerializedName("channelLanguageId")
    private Integer channelLanguageId;
    @SerializedName("channelCategoryId")
    private Integer channelCategoryId;

    public Integer getChannelId() {
        return channelId;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public String getChannelOrder() {
        return channelOrder;
    }

    public String getChannelName() {
        return channelName;
    }

    public String getChannelCategory() {
        return channelCategory;
    }

    public Integer getChannelLanguageId() {
        return channelLanguageId;
    }

    public Integer getChannelCategoryId() {
        return channelCategoryId;
    }
}
