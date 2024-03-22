package com.example.sanchitra.utils;

import com.example.sanchitra.api.SignUpRequest;
import com.example.sanchitra.api.TVRequest;
import com.example.sanchitra.api.Title;
import com.example.sanchitra.api.UserInit;
import com.example.sanchitra.api.UserRequest;
import com.example.sanchitra.api.WatchRequest;
import com.example.sanchitra.model.AnimeContentListModel;
import com.example.sanchitra.model.CommonDataModel;
import com.example.sanchitra.model.DramaContentListModel;
import com.example.sanchitra.model.EpisodeListModel;
import com.example.sanchitra.model.EpisodeVideoModel;
import com.example.sanchitra.model.SignUpModel;
import com.example.sanchitra.model.TVChannelListModel;
import com.example.sanchitra.model.TVVideoModel;
import com.example.sanchitra.model.UserModel;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RequestModule {

//    For User
    @POST("users")
    Call<UserModel> getUser(@Body UserInit body);

    @POST("login")
    Call<UserModel> getLogin(@Body UserRequest body);

    @POST("signup")
    Call<SignUpModel> signUp(@Header("x-api-key") String apikey, @Body SignUpRequest body);

//    for Dramas
    @GET("sanchitra")
    Call<DramaContentListModel> getDramaContentList(@Header("x-api-key") String apikey);


//    For Anime
    @GET("sanchitra")
    Call<AnimeContentListModel> getAnimeContentList(@Header("x-api-key") String apikey);


//    for TV
    @GET("sanchitra")
    Call<TVChannelListModel> getTvChannelList(@Header("x-api-key") String apikey);

    @GET("watch_link/")
    Call<TVVideoModel> getTvVideo(@Header("x-api-key") String apikey, @Query("id") String channelId);

    @POST("sanchitra")
    Call<TVVideoModel> getTvVideoV2(@Header("x-api-key") String apikey, @Body TVRequest body);

//    Common

    @POST("episodes")
    Call<EpisodeListModel> getEpisodeList(@Header("x-api-key") String apikey,
                                          @Body Title body);
    @POST("watch_link")
    Call<EpisodeVideoModel> getEpisodeVideo(@Header("x-api-key") String apikey, @Body WatchRequest body);

    @GET("search/")
    Call<CommonDataModel> getSearchResults(@Header("x-api-key") String apikey, @Query("name") String query);

}

