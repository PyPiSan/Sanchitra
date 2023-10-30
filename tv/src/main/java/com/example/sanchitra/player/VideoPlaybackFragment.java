package com.example.sanchitra.player;

import android.content.Context;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.leanback.app.VideoFragment;
import androidx.leanback.app.VideoFragmentGlueHost;
import androidx.leanback.media.PlaybackGlue;
import androidx.leanback.widget.PlaybackControlsRow;

import android.util.Log;
import android.view.View;

public class VideoPlaybackFragment extends VideoFragment {

    private String uriPath = "https://www047.vipanicdn.net/streamhls/31014405f62933f3613ce2ec15abf3c4/ep.6.1693502864.720.m3u8";

//    public static final String TAG = "playback_frag";

    private VideoMediaPlayerGlue<ExoPlayerAdapter> mMediaPlayerGlue;

    final VideoFragmentGlueHost mHost = new VideoFragmentGlueHost(this);

    static void playWhenReady(PlaybackGlue glue) {
        if (glue.isPrepared()) {
            glue.play();
        } else {
            glue.addPlayerCallback(new PlaybackGlue.PlayerCallback() {
                @Override
                public void onPreparedStateChanged(PlaybackGlue glue) {
                    if (glue.isPrepared()) {
                        glue.removePlayerCallback(this);
                        glue.play();
                    }
                }
            });
        }
    }

//    AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener
//            = new AudioManager.OnAudioFocusChangeListener() {
//        @Override
//        public void onAudioFocusChange(int state) {
//        }
//    };
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ExoPlayerAdapter playerAdapter = new ExoPlayerAdapter(getActivity());
        mMediaPlayerGlue = new VideoMediaPlayerGlue(getActivity(), playerAdapter);
        mMediaPlayerGlue.setHost(mHost);
        AudioManager audioManager = (AudioManager) getActivity()
                .getSystemService(Context.AUDIO_SERVICE);
//        if (audioManager.requestAudioFocus(mOnAudioFocusChangeListener, AudioManager.STREAM_MUSIC,
//                AudioManager.AUDIOFOCUS_GAIN) != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
////            Log.w(TAG, "video player cannot obtain audio focus!");
//        }

        mMediaPlayerGlue.setMode(PlaybackControlsRow.RepeatAction.NONE);
        mMediaPlayerGlue.setTitle("Diving with Sharks");
        mMediaPlayerGlue.setSubtitle("A Googler");
        mMediaPlayerGlue.getPlayerAdapter().setDataSource(Uri.parse(uriPath));
        playWhenReady(mMediaPlayerGlue);
        setBackgroundType(BG_LIGHT);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onPause() {
        if (mMediaPlayerGlue != null) {
            mMediaPlayerGlue.pause();
        }
        super.onPause();
    }


}