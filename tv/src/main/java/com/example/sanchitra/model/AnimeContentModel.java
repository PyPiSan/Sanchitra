package com.example.sanchitra.model;

import com.google.gson.annotations.SerializedName;

public class AnimeContentModel {

    @SerializedName("anime_title")
    private String title;

    @SerializedName("anime_detail_link")
    private String detailLink;

    @SerializedName("summary")
    private String summary;

    @SerializedName("image_url")
    private String image;

    @SerializedName("released")
    private String released;

    @SerializedName("type")
    private String type;

    public String getTitle() {
        return title;
    }

    public String getDetailLink() {
        return detailLink;
    }

    public String getSummary() {
        return summary;
    }

    public String getImage() {
        return image;
    }

    public String getReleased() {
        return released;
    }

    public String getType() {
        return type;
    }
}
