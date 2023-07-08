package com.example.sanchitra.api;

public class EpisodeBody {
    private final String title;
    private final String episode;

    private final String image;

    public EpisodeBody(String title, String episode, String image) {
        this.title = title;
        this.episode = episode;
        this.image = image;
    }

//    public EpisodeBody(String title, String episode) {
//        this.title = title;
//        this.episode = episode;
//    }

    public String getImage() {
        return image;
    }


    public String getTitle() {
        return title;
    }

    public String getEpisode() {
        return episode;
    }
}
