package com.example.sanchitra.player;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.media.session.MediaSession;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.VideoView;

import com.example.sanchitra.R;

public class VideoPlayer extends FragmentActivity implements
        PlaybackOverlay.OnPlayPauseClickedListener {

    private VideoView mVideoView;
//    private LeanbackPlaybackState mPlaybackState;
    private MediaSession mSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_video_player);

        mSession = new MediaSession(this, "Sanchitra");
//        mSession.setCallback(new MediaSessionCallback());
        mSession.setFlags(MediaSession.FLAG_HANDLES_MEDIA_BUTTONS
                | MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS
        );

        mSession.setActive(true);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.playback_control_fragment, new PlaybackOverlay());
        transaction.commit();

    }

}