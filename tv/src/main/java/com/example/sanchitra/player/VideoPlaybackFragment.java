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

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.Objects;

public class VideoPlaybackFragment extends VideoFragment {

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

    AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener
            = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int state) {
        }
    };
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ExoPlayerAdapter playerAdapter = new ExoPlayerAdapter(getActivity());
        mMediaPlayerGlue = new VideoMediaPlayerGlue<>(getActivity(), playerAdapter);
        mMediaPlayerGlue.setHost(mHost);
        mMediaPlayerGlue.setSeekEnabled(true);
        AudioManager audioManager = (AudioManager) getActivity()
                .getSystemService(Context.AUDIO_SERVICE);
        if (audioManager.requestAudioFocus(mOnAudioFocusChangeListener, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN) != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            Log.d("video", "video player cannot obtain audio focus!");
        }

//        mMediaPlayerGlue.setMode(PlaybackControlsRow.RepeatAction.NONE);

//
        if (Objects.equals(getArguments().getString("type"), "tv")){
            mMediaPlayerGlue.setTitle(getArguments().getString("title"));
            mMediaPlayerGlue.getPlayerAdapter().setDataSource(Uri.parse(getArguments().getString("720")),"tv");
            playWhenReady(mMediaPlayerGlue);
            setBackgroundType(BG_LIGHT);
        }else{
            mMediaPlayerGlue.setTitle(String.format("%s ( Episode %s )",
                    getArguments().getString("title"),
                    getArguments().getString("subTitle")));
        }
        if (getArguments().getString("1080") == null || Objects.equals(getArguments().getString("1080"), "") &&
                Objects.equals(getArguments().getString("720"), "")) {
            Toast.makeText(getContext(), "Not found format is Blank, Click Retry", Toast.LENGTH_LONG).show();
        } else if (!Objects.equals(getArguments().getString("720"), "") &&
                Objects.equals(getArguments().getString("1080"), "")) {
            mMediaPlayerGlue.getPlayerAdapter().setDataSource(Uri.parse(getArguments().getString("720")),"drama");
            playWhenReady(mMediaPlayerGlue);
            setBackgroundType(BG_LIGHT);
        }
        else{
            mMediaPlayerGlue.getPlayerAdapter().setDataSource(Uri.parse(getArguments().getString("1080")),"drama");
            playWhenReady(mMediaPlayerGlue);
            setBackgroundType(BG_LIGHT);
            }

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