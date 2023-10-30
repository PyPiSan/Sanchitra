package com.example.sanchitra.player;


import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;

import com.example.sanchitra.R;

public class VideoPlayer extends Activity {


    public static final String TAG = "playback_frag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_video_player);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.playback_controls_fragment, new VideoPlaybackFragment());
        transaction.commit();
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

}