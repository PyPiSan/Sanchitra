package com.example.sanchitra;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.leanback.widget.BrowseFrameLayout;

import com.example.sanchitra.Utils.Constant;
import com.example.sanchitra.View.AnimeView;
import com.google.android.gms.ads.MobileAds;

public class MainActivity extends FragmentActivity implements View.OnKeyListener {

    private TextView searchPage, homePage, animePage, dramaPage, tvPage, moviePage;
    private BrowseFrameLayout sideMenu;
    private  boolean SIDE_MENU = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MobileAds.initialize(this);
        // Get reference to singleton RewardedVideoAd object
        sideMenu = findViewById(R.id.side_menu);

        searchPage = findViewById(R.id.search_page);
        homePage = findViewById(R.id.home_page);
        animePage = findViewById(R.id.anime_page);
        dramaPage = findViewById(R.id.drama_page);
        tvPage = findViewById(R.id.tv_page);
        moviePage = findViewById(R.id.movie_page);

        searchPage.setOnKeyListener(this);
        homePage.setOnKeyListener(this);
        animePage.setOnKeyListener(this);
        dramaPage.setOnKeyListener(this);
        tvPage.setOnKeyListener(this);
        moviePage.setOnKeyListener(this);

        Fragment listFragment = new AnimeView();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fragmentView, listFragment);
        transaction.commit();
    }


    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        if (i == KeyEvent.KEYCODE_DPAD_LEFT) {
            if (!SIDE_MENU) {
                openSideMenu();
                SIDE_MENU = true;
            }
        } else if (i == KeyEvent.KEYCODE_DPAD_RIGHT){
            if (SIDE_MENU) {
                SIDE_MENU = false;
                closeSideMenu();
            }
        }
        return false;
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT && SIDE_MENU){
//
//        }
//        return super.onKeyDown(keyCode, event);
//    }


    @Override
    public void onBackPressed() {
        if(SIDE_MENU){
            SIDE_MENU = false;
            closeSideMenu();
        }
        super.onBackPressed();
    }

    private void openSideMenu(){
        sideMenu.requestLayout();
        sideMenu.getLayoutParams().width = Constant.getWidthInPercent(this, 16);
    }

    private void closeSideMenu(){
        sideMenu.requestLayout();
        sideMenu.getLayoutParams().width = Constant.getWidthInPercent(this, 5);
    }
}