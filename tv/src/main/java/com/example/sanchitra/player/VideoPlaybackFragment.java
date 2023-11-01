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
import android.widget.Toast;

import com.example.sanchitra.api.WatchRequest;
import com.example.sanchitra.model.EpisodeVideoModel;
import com.example.sanchitra.utils.Constant;
import com.example.sanchitra.utils.RequestModule;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class VideoPlaybackFragment extends VideoFragment {

    private VideoMediaPlayerGlue<ExoPlayerAdapter> mMediaPlayerGlue;
    private String[] videoLink = new String[4];

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
        AudioManager audioManager = (AudioManager) getActivity()
                .getSystemService(Context.AUDIO_SERVICE);
        if (audioManager.requestAudioFocus(mOnAudioFocusChangeListener, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN) != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            Log.d("video", "video player cannot obtain audio focus!");
        }

        mMediaPlayerGlue.setMode(PlaybackControlsRow.RepeatAction.NONE);
        mMediaPlayerGlue.setTitle(String.format("%s ( Episode %s )", getArguments().getString("title"), getArguments().getString("subTitle")));
        getEpisodeLink(getArguments().getString("title"), getArguments().getString("subTitle"),
                getArguments().getString("type"));

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

    private void getEpisodeLink(String title, String episode_num, String type) {
        String url, server;
        if (type=="anime"){
            url = Constant.animeUrl;
            server ="";
        }else{
            url = Constant.dramaUrl;
            server = "streamsb";
        }
        //      fetching data
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RequestModule episodeLink = retrofit.create(RequestModule.class);
        Call<EpisodeVideoModel> call = episodeLink.getEpisodeVideo(Constant.key,
                new WatchRequest(title, episode_num, server));
        call.enqueue(new Callback<EpisodeVideoModel>() {
            @Override
            public void onResponse(Call<EpisodeVideoModel> call, Response<EpisodeVideoModel> response) {
                boolean flag = false;
                EpisodeVideoModel resource = response.body();
                if (response.code() == 200) {
                    boolean status = resource.getSuccess();
                    flag = status;
                }
//                Log.d("video", "link is"+resource.getSuccess());
                if (flag) {
                    videoLink[0] = resource.getValue().getQuality1();
                    videoLink[1] = resource.getValue().getQuality2();
                    videoLink[2] = resource.getValue().getQuality3();
                    videoLink[3] = resource.getValue().getQuality4();
                }
                if (videoLink[3] == null || videoLink[3].equals("") && videoLink[2].equals("")) {
                    Toast.makeText(getContext(), "Not found, Click Retry", Toast.LENGTH_LONG).show();
                } else if (!videoLink[2].equals("") && videoLink[3].equals("")) {
                    mMediaPlayerGlue.getPlayerAdapter().setDataSource(Uri.parse(videoLink[2]));
                    playWhenReady(mMediaPlayerGlue);
                    setBackgroundType(BG_LIGHT);
                }
                else{
                    mMediaPlayerGlue.getPlayerAdapter().setDataSource(Uri.parse(videoLink[3]));
                    playWhenReady(mMediaPlayerGlue);
                    setBackgroundType(BG_LIGHT);
                }

            }

            @Override
            public void onFailure(Call<EpisodeVideoModel> call, Throwable t) {
                Toast.makeText(getContext(), "Not found, Click Retry", Toast.LENGTH_LONG).show();
            }
        });

    }

}