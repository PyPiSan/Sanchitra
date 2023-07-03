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

    public static int getWidthInPercent(Context context, int percent){
        int width = context.getResources().getDisplayMetrics().widthPixels;
        return (width*percent)/100;
    }
}
