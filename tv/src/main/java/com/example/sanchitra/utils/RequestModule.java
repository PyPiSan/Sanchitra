package com.example.sanchitra.utils;

import com.example.sanchitra.api.Title;
import com.example.sanchitra.model.AnimeEpisodeListModel;
import com.example.sanchitra.model.AnimeRecentModel;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RequestModule {

    @Headers({
            "x-api-key: e7y6acFyHGqwtkBLKHx6eA"
    })
    @GET("new/")
    Call<AnimeRecentModel> getAnime(@Query("page") String num);

    @Headers({
            "x-api-key: e7y6acFyHGqwtkBLKHx6eA"
    })
    @POST("episodes")
    Call<AnimeEpisodeListModel> getEpisodeList(@Body Title body);

}

