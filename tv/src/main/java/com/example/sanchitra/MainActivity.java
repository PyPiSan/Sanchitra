package com.example.sanchitra;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.sanchitra.View.AnimeView;
import com.example.sanchitra.View.HomeView;
import com.google.android.gms.ads.MobileAds;

public class MainActivity extends FragmentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MobileAds.initialize(this);
        // Get reference to singleton RewardedVideoAd object

        Fragment listFragment = new AnimeView();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fragmentView, listFragment);
        transaction.commit();

    }
}