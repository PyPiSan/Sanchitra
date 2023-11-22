package com.example.sanchitra.api;

public class TVRequest {
    final String channel_id;
    final String lang_id;

    public TVRequest(String channel_id, String lang_id) {
        this.channel_id = channel_id;
        this.lang_id = lang_id;
    }
}
