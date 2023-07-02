package com.example.sanchitra.Utils;

import com.example.sanchitra.Model.AnimeRecentModel;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RequestModule {

    String apiKey = "e7y6acFyHGqwtkBLKHx6eA";

    @Headers({
            "x-api-key: e7y6acFyHGqwtkBLKHx6eA"
    })
    @GET("new/")
    Call<AnimeRecentModel> getAnime(@Query("page") String num);

}

