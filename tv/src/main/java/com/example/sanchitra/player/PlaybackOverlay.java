package com.example.sanchitra.player;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.leanback.app.VideoSupportFragment;
import androidx.leanback.app.VideoSupportFragmentGlueHost;
import androidx.leanback.media.MediaPlayerAdapter;
import androidx.leanback.media.PlaybackGlue;
import androidx.leanback.media.PlaybackTransportControlGlue;
import android.view.View;

public class PlaybackOverlay extends VideoSupportFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final PlaybackTransportControlGlue<MediaPlayerAdapter> playerGlue =
                new PlaybackTransportControlGlue<>(getActivity(),
                        new MediaPlayerAdapter(getActivity()));

        playerGlue.setHost(new VideoSupportFragmentGlueHost(this));
        playerGlue.addPlayerCallback(new PlaybackGlue.PlayerCallback() {
            @Override
            public void onPreparedStateChanged(PlaybackGlue glue) {
                if (glue.isPrepared()) {
//                    playerGlue.setSeekProvider(new MySeekProvider());
                    playerGlue.play();
                }
            }
        });
        playerGlue.setSubtitle("Episode 1");
        playerGlue.setTitle("Naruto");
        String uriPath = "https://www005.vipanicdn.net/streamhls/027e9529af2b06fe7b4f47e507a787eb/ep.1.1677593055.1080.m3u8";
        playerGlue.getPlayerAdapter().setDataSource(Uri.parse(uriPath));
    }

    public interface OnPlayPauseClickedListener {

    }
}