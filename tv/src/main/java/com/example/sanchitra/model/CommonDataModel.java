package com.example.sanchitra.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CommonDataModel {
    @SerializedName("results")
    private List<ContentModel> results;

    @SerializedName("success")
    private Boolean success;

    public List<ContentModel> getResults() {
        return results;
    }

    public Boolean getSuccess() {
        return success;
    }

}
