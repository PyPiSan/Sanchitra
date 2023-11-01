package com.example.sanchitra.player;


import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;

import com.example.sanchitra.R;

public class VideoPlayer extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_video_player);
//      for getting video summary params
        Intent videoIntent = getIntent();
        Bundle bundle = new Bundle();
        bundle.putString("title", videoIntent.getStringExtra("title"));
        bundle.putString("subTitle", videoIntent.getStringExtra("episode"));
        bundle.putString("type", videoIntent.getStringExtra("type"));
        VideoPlaybackFragment fragment = new VideoPlaybackFragment();
        fragment.setArguments(bundle);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.playback_controls_fragment, fragment);
        transaction.commit();
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

}