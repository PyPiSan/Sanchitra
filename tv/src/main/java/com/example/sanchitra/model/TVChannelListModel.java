package com.example.sanchitra.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TVChannelListModel {

    @SerializedName("results")
    private List<TVChannelListModel.datum> results;

    @SerializedName("success")
    private Boolean success;

    public List<TVChannelListModel.datum> getData() {
        return results;
    }

    public Boolean getSuccess() {
        return success;
    }

    public class datum {

        @SerializedName("content_header")
        private String contentHeader;

        @SerializedName("content_list")
        private List<TVContentModel> contentList;


        public String getContentHeader() {
            return contentHeader;
        }

        public List<TVContentModel> getContentList() {
            return contentList;
        }
    }
}
