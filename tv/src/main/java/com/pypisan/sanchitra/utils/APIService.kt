package com.pypisan.sanchitra.utils

import com.pypisan.sanchitra.api.RefreshTokenRequest
import com.pypisan.sanchitra.data.entities.IPTVCategoryDto
import com.pypisan.sanchitra.data.models.ChannelDto
import com.pypisan.sanchitra.data.models.CommonResponse
import com.pypisan.sanchitra.data.models.DeviceLoginInitResponse
import com.pypisan.sanchitra.data.models.IPTVResponseDto
import com.pypisan.sanchitra.data.models.LoginStatusResponse
import com.pypisan.sanchitra.data.models.MovieListResponseDTO
import com.pypisan.sanchitra.data.models.TVResponse
import com.pypisan.sanchitra.data.models.TrendingResponseDTO
import com.pypisan.sanchitra.data.models.UserDetailResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface APIService {

    //   login init
    @POST("users/device/login/init/")
    suspend fun initDeviceLogin(
        @Header("No-Auth") noAuth: Boolean = true
    ): Response<DeviceLoginInitResponse>

    //    login status check
    @GET("users/device/login/status/")
    suspend fun checkLoginStatus(
        @Query("device_code") deviceCode: String,
        @Header("No-Auth") noAuth: Boolean = true
    ): Response<LoginStatusResponse>

    //    get user details
    @GET("users/detail/")
    suspend fun getUserDetail(): Response<UserDetailResponse>

    //    logout
    @POST("users/logout/")
    suspend fun userLogout(): Response<CommonResponse>

    //    delete
    @POST("users/delete/")
    suspend fun deleteUserAccount(): Response<CommonResponse>


    //    get TV channel list
    @GET("home/tv/hd/")
    suspend fun getLiveTV(): Response<TVResponse>

    //    get TV carousel
    @GET("home/tv/carousel/")
    suspend fun getCarouselTV(): Response<TVResponse>

    @GET("home/channels/")
    suspend fun getChannelDetail(
        @Query("id") id: Int,
        @Query("type") type: String
    ): Response<ChannelDto>

    @POST("home/featured/trends/view/")
    suspend fun updateViewCount(
        @Query("channel_id") channelID: Int,
    ): Response<CommonResponse>

    //    IPTV
    @GET("iptv/category/list/")
    suspend fun getIPTVCategoryList(): Response<List<IPTVCategoryDto>>

    @GET("iptv/category/{category}")
    suspend fun getIPTVCategory(
    @Path("category") category: String
    ): Response<IPTVResponseDto>

    //  Movies
    @GET("videos/")
    suspend fun getMoviesList(): Response<MovieListResponseDTO>

    @GET("home/video/carousel/")
    suspend fun getCarouselVideoList(): Response<MovieListResponseDTO>


    //    Home
    @GET("home/trending/live/channels/")
    suspend fun getTrending(): Response<TrendingResponseDTO>

}

interface RefreshAPIService{
    @POST("users/refresh/auth/")
    suspend fun refreshToken(
        @Body request: RefreshTokenRequest
    ): Response<LoginStatusResponse>
}