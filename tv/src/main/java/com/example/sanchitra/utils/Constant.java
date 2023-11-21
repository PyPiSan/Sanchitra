package com.example.sanchitra.utils;

import android.content.Context;

public class Constant {

    public static String MENU_SEARCH = "search";
    public static String MENU_HOME = "home";
    public static String MENU_ANIME = "anime";
    public static String MENU_DRAMA = "drama";
    public static String MENU_TV = "tv";
    public static String MENU_MOVIE = "movie";
    public static String MENU_SETTINGS = "settings";

    public final static String key ="e7y6acFyHGqwtkBLKHx6eA";
    public static String cookies = null;
    public final static String dramaUrl = "https://drama.pypisan.com/v1/drama/";
    public final static String animeUrl = "https://anime.pypisan.com/v1/anime/";
    public final static String tvUrl = "https://drama.pypisan.com/v1/tv/";

    public final static  String local = "https://tv.pypisan.com/";

    public static int getWidthInPercent(Context context, int percent){
        int width = context.getResources().getDisplayMetrics().widthPixels;
        return (width*percent)/100;
    }
}
