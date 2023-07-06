package com.example.sanchitra.model;

import com.google.gson.annotations.SerializedName;

public class ContentModel {
    @SerializedName("title")
    private String title;

    @SerializedName("image")
    private String image;

    @SerializedName("link")
    private String link;

    @SerializedName("release")
    private String release;

    @SerializedName("summary")
    private String summary;

    @SerializedName("type")
    private String type;

    @SerializedName("episodes")
    private int episodes;

    public String getTitle() {
        return title;
    }

    public String getImage() {
        return image;
    }

    public String getLink() {
        return link;
    }

    public String getRelease() {
        return release;
    }

    public String getSummary() {
        return summary;
    }

    public String getType() {
        return type;
    }

    public int getEpisodes() {
        return episodes;
    }
}
