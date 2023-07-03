package com.example.sanchitra.model;

import com.google.gson.annotations.SerializedName;
public class AnimeEpisodeListModel {
    @SerializedName("data")
    private final AnimeEpisodeListModel.datum data = null;
    @SerializedName("success")
    private Boolean success;

    public Boolean getSuccess() {
        return success;
    }

    public AnimeEpisodeListModel.datum getData() {
        return data;
    }

    public class datum {

        @SerializedName("image_url")
        private String imageUrl;

        @SerializedName("anime_detail_link")
        private String animeDetailLink;
        @SerializedName("anime_title")
        private String title;
        @SerializedName("summary")
        private String summary;
        @SerializedName("episodes")
        private String episode_num;
        @SerializedName("status")
        private String status;
        @SerializedName("released")
        private String released;
        @SerializedName("anime_id")
        private String animeId;
        @SerializedName("genres")
        private String[] genres;

        public String getImageLink() {
            return imageUrl;
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

        public String getEpisode_num() {
            return episode_num;
        }

        public String getStatus() { return status; }

        public String getReleased() { return released; }

        public String getAnimeId() { return animeId; }

        public String[] getGenres(){ return genres;}
    }
}
