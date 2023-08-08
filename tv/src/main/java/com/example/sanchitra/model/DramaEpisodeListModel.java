package com.example.sanchitra.model;

import com.google.gson.annotations.SerializedName;
public class DramaEpisodeListModel {
    @SerializedName("data")
    private final DramaEpisodeListModel.datum data = null;
    @SerializedName("success")
    private Boolean success;

    public Boolean getSuccess() {
        return success;
    }

    public DramaEpisodeListModel.datum getData() {
        return data;
    }

    public class datum {

        @SerializedName("image")
        private String image;

        @SerializedName("link")
        private String animeDetailLink;
        @SerializedName("title")
        private String title;
        @SerializedName("summary")
        private String summary;
        @SerializedName("episodes")
        private int episodes;
        @SerializedName("status")
        private String status;
        @SerializedName("released")
        private String released;
        @SerializedName("anime_id")
        private String animeId;
        @SerializedName("genres")
        private String[] genres;

        public String getImageLink() {
            return image;
        }

        public String getAnimeDetailLink() {
            return animeDetailLink;
        }

        public String getTitle() {
            return title;
        }

        public String getSummary() {
            return summary;
        }

        public int getEpisodes() {
            return episodes;
        }

        public String getStatus() { return status; }

        public String getReleased() { return released; }

        public String[] getGenres(){ return genres;}
    }
}
