package com.example.sanchitra.view;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.example.sanchitra.R;
import com.example.sanchitra.player.VideoPlayer;

public class DetailViewActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_detail_view);
        com.example.sanchitra.databinding.ActivityDetailViewBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_detail_view);

        Glide.with(this)
             .load(getIntent().getStringExtra("image"))
              .into(binding.headBanner);
        binding.headTitle.setText(getIntent().getStringExtra("title"));
        binding.subtitle.setText(getIntent().getStringExtra("release"));
        binding.description.setText(getIntent().getStringExtra("summary"));
        changeFragment(new EpisodeListView(), getIntent().getStringExtra("title"),
                getIntent().getStringExtra("type"));
        binding.play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickedPlay(getIntent().getStringExtra("title"), "1",
                        getIntent().getStringExtra("type"));
            }
        });
    }

    private void changeFragment(Fragment fragment, String title, String type){
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putString("type", type);
        fragment.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.episode_fragment, fragment);
        transaction.commit();
    }

    private void onClickedPlay(String title, String episode, String type){
        Intent intent = new Intent(this, VideoPlayer.class);
        intent.putExtra("title", title);
        intent.putExtra("episode", episode);
        intent.putExtra("type", type);
        startActivity(intent);
    }
}