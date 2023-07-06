package com.example.sanchitra.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ContentListModel {

    @SerializedName("results")
    private List<ContentListModel.datum> results;

    @SerializedName("success")
    private Boolean success;

    public List<datum> getData() {
        return results;
    }

    public Boolean getSuccess() {
        return success;
    }

    public class datum {

        @SerializedName("content_header")
        private String contentHeader;

        @SerializedName("content_list")
        private List<ContentModel> contentList;


        public String getContentHeader() {
            return contentHeader;
        }

        public List<ContentModel> getContentList() {
            return contentList;
        }
    }
}
