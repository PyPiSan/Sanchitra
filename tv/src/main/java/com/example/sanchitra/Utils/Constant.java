package com.example.sanchitra.Utils;

import android.content.Context;

public class Constant {


    public static int getWidthInPercent(Context context, int percent){
        int width = context.getResources().getDisplayMetrics().widthPixels;
        return (width*percent)/100;
    }
}
