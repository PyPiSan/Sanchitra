package com.example.sanchitra.api;

public class EpisodeBody {
    private final String title;
    private final String episode;

    public EpisodeBody(String title, String episode) {
        this.title = title;
        this.episode = episode;
    }

    public String getTitle() {
        return title;
    }

    public String getEpisode() {
        return episode;
    }
}
