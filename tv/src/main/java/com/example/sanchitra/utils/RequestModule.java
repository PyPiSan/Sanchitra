package com.example.sanchitra.utils;

import com.example.sanchitra.api.Title;
import com.example.sanchitra.api.WatchRequest;
import com.example.sanchitra.model.AnimeContentListModel;
import com.example.sanchitra.model.DramaContentListModel;
import com.example.sanchitra.model.DramaEpisodeListModel;
import com.example.sanchitra.model.EpisodeVideoModel;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface RequestModule {

//    for Dramas
    @GET("sanchitra")
    Call<DramaContentListModel> getDramaContentList(@Header("x-api-key") String apikey);

    @POST("episodes")
    Call<DramaEpisodeListModel> getDramaEpisodeList(@Header("x-api-key") String apikey,
                                                    @Body Title body);

//    For Anime
    @GET("sanchitra")
    Call<AnimeContentListModel> getAnimeContentList(@Header("x-api-key") String apikey);

//    Common
    @POST("watch_link")
    Call<EpisodeVideoModel> getEpisodeVideo(@Header("x-api-key") String apikey, @Body WatchRequest body);

}

