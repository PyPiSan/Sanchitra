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

    public static String key;
    public static String uid;
    public static String versionName;
    public static String cookies = null;
    public final static String dramaUrl = "https://drama.pypisan.com/v1/drama/";
    public final static String animeUrl = "https://anime.pypisan.com/v1/anime/";
    public final static String tvUrl = "https://drama.pypisan.com/v1/tv/";

    public final static  String local = "https://tv.pypisan.com/v1/tv/";
    public static String userUrl = "https://anime.pypisan.com/v1/";
    public static boolean loggedInStatus;
    public static Integer logo;
    public static String userName;
//    public static String message;
    public static Boolean isFree;

    public static int getWidthInPercent(Context context, int percent){
        int width = context.getResources().getDisplayMetrics().widthPixels;
        return (width*percent)/100;
    }
}
