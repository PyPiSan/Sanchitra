package com.example.sanchitra.player;


import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.example.sanchitra.R;
import com.example.sanchitra.api.TVRequest;
import com.example.sanchitra.api.WatchRequest;
import com.example.sanchitra.model.EpisodeVideoModel;
import com.example.sanchitra.model.TVVideoModel;
import com.example.sanchitra.utils.Constant;
import com.example.sanchitra.utils.RequestModule;
import com.example.sanchitra.view.SpinnerView;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class VideoPlayer extends FragmentActivity {
    private String title, episode,type;
    private ProgressBar loader;
    private FrameLayout errorFragment;

    private TextView reloadButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_video_player);
//      for getting video summary params
        Intent videoIntent = getIntent();
        type = videoIntent.getStringExtra("type");
        loader = findViewById(R.id.spinner);
        errorFragment = findViewById(R.id.errorFrag);
        reloadButton = findViewById(R.id.reload);
        if (type.equals("tv")){
            title = videoIntent.getStringExtra("channel");
            String id = videoIntent.getStringExtra("id");
            getTvLink(id,videoIntent.getStringExtra("language"), type);
        }else{
            title = videoIntent.getStringExtra("title");
            episode = videoIntent.getStringExtra("episode");
            getEpisodeLink(title,episode,type);
        }
        reloadButton.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_DPAD_CENTER){
                    loader.setVisibility(View.VISIBLE);
                    errorFragment.setVisibility(View.GONE);
                    if (type.equals("tv")){
                        title = videoIntent.getStringExtra("channel");
                        String id = videoIntent.getStringExtra("id");
                        getTvLink(id,videoIntent.getStringExtra("language"), type);
                    }else{
                        title = videoIntent.getStringExtra("title");
                        episode = videoIntent.getStringExtra("episode");
                        getEpisodeLink(title,episode,type);
                    }

                }
                return false;
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    private void getEpisodeLink(String title, String episode_num, String type) {
        final String[] videoLink = new String[4];
        String url, server;
        if (Objects.equals(type, "anime")){
            url = Constant.animeUrl;
        }else{
            url = Constant.dramaUrl;
        }
//        Log.d("episodeList", "type is "+ type +"url is "+url);
        //      fetching data
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RequestModule episodeLink = retrofit.create(RequestModule.class);
        Call<EpisodeVideoModel> call = episodeLink.getEpisodeVideo(Constant.key,
                new WatchRequest(title, episode_num, ""));
        call.enqueue(new Callback<EpisodeVideoModel>() {
            @Override
            public void onResponse(Call<EpisodeVideoModel> call, Response<EpisodeVideoModel> response) {
                boolean flag = false;
                EpisodeVideoModel resource = response.body();
                if (response.code() == 200) {
                    flag = resource.getSuccess();
                }
//                Log.d("video", "link is"+resource.getSuccess());
                if (flag) {
                    videoLink[0] = resource.getValue().getQuality1();
                    videoLink[1] = resource.getValue().getQuality2();
                    videoLink[2] = resource.getValue().getQuality3();
                    videoLink[3] = resource.getValue().getQuality4();
                    loader.setVisibility(View.GONE);
                    changeFragment(videoLink);
                }else {
                    loader.setVisibility(View.GONE);
                    errorFragment.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<EpisodeVideoModel> call, Throwable t) {
//                Log.d("Video", "Error 404 not found");
                loader.setVisibility(View.GONE);
                errorFragment.setVisibility(View.VISIBLE);
            }
        });

    }

    private void getTvLink(String id, String lang, String type) {
        final String[] tvLink = new String[4];
        //      fetching data
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constant.local)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
//        Toast.makeText(this, Constant.local, Toast.LENGTH_SHORT).show();
        RequestModule videoLink = retrofit.create(RequestModule.class);
        Call<TVVideoModel> call = videoLink.getTvVideoV2(Constant.key, new TVRequest(id,lang));
        call.enqueue(new Callback<TVVideoModel>() {
            @Override
            public void onResponse(Call<TVVideoModel> call, Response<TVVideoModel> response) {
                boolean flag = false;
                TVVideoModel resource = response.body();
                if (response.code() == 200) {
                    flag = resource.getSuccess();
                }
//                Log.d("video", "link is"+resource.getSuccess());
                if (flag) {
                    Constant.cookies =resource.getCookies();
                    tvLink[0] = resource.getValue().getLow();
                    tvLink[1] = resource.getValue().getMedium();
                    tvLink[2] = resource.getValue().getHigh();
                    tvLink[3] = resource.getValue().getUltraHigh();

                    loader.setVisibility(View.GONE);
                    changeFragment(tvLink);
                } else {
                    loader.setVisibility(View.GONE);
                    errorFragment.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<TVVideoModel> call, Throwable t) {
//                Log.d("Video", "TV Error 404 not found");
                loader.setVisibility(View.GONE);
                errorFragment.setVisibility(View.VISIBLE);
            }
        });

    }

    protected void changeFragment(String[] args) {
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        if (!type.equals("tv")) {
            bundle.putString("subTitle", episode);
            bundle.putString("type", "drama");
        }else{
            bundle.putString("type", type);
        }
        bundle.putString("360", args[0]);
        bundle.putString("480", args[1]);
        bundle.putString("720", args[2]);
        bundle.putString("1080", args[3]);
        VideoPlaybackFragment fragment = new VideoPlaybackFragment();
        fragment.setArguments(bundle);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.playback_controls_fragment, fragment);
        transaction.commit();

    }

}