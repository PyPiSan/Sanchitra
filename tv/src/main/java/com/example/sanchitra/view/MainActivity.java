package com.example.sanchitra.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.leanback.widget.BrowseFrameLayout;

import com.example.sanchitra.R;
import com.example.sanchitra.presenter.TopMenuAdapter;
import com.example.sanchitra.utils.Constant;
import com.example.sanchitra.view.AnimeView;
import com.example.sanchitra.view.DramaView;
import com.example.sanchitra.view.HomeView;
import com.example.sanchitra.view.MovieView;
import com.example.sanchitra.view.SearchView;
import com.example.sanchitra.view.TvView;
import com.google.android.gms.ads.MobileAds;
import androidx.leanback.tab.LeanbackTabLayout;
import androidx.leanback.tab.LeanbackViewPager;
import androidx.viewpager.widget.PagerAdapter;

import java.util.Objects;

public class MainActivity extends FragmentActivity implements View.OnKeyListener {

    private TextView searchPage, homePage, animePage, dramaPage, tvPage, moviePage,
            settingsPage;
    private BrowseFrameLayout sideMenu;
    private  boolean SIDE_MENU = false;
    private FrameLayout fragmentContainer;

//    private PagerAdapter TopMenuAdapter;
    private String selectedMenu = Constant.MENU_HOME;

    private View lastSelectedMenu;

    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        MobileAds.initialize(this);
        // Get reference to singleton RewardedVideoAd object
        sideMenu = findViewById(R.id.side_menu);
        fragmentContainer = findViewById(R.id.fragmentView);

//        final LeanbackTabLayout leanbackTabLayout = (LeanbackTabLayout) ActivityCompat.
//                requireViewById(this, R.id.tab_layout);
//        leanbackTabLayout.setFocusable(true);
//        final LeanbackViewPager leanbackViewPager = (LeanbackViewPager) ActivityCompat.
//                requireViewById(this, R.id.pager);
//        leanbackViewPager.setAdapter(TopMenuAdapter);
//        leanbackTabLayout.setupWithViewPager(leanbackViewPager);
//        fragmentContainer = findViewById(R.id.fragmentView);

        searchPage = findViewById(R.id.search_page);
        homePage = findViewById(R.id.home_page);
        animePage = findViewById(R.id.anime_page);
        dramaPage = findViewById(R.id.drama_page);
        tvPage = findViewById(R.id.tv_page);
        moviePage = findViewById(R.id.movie_page);
        settingsPage = findViewById(R.id.settings_page);

        searchPage.setOnKeyListener(this);
        homePage.setOnKeyListener(this);
        animePage.setOnKeyListener(this);
        dramaPage.setOnKeyListener(this);
        tvPage.setOnKeyListener(this);
        moviePage.setOnKeyListener(this);
        settingsPage.setOnKeyListener(this);

        lastSelectedMenu = homePage;
        lastSelectedMenu.setActivated(true);
        changeFragment(new HomeView());
    }


    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        if (i == KeyEvent.KEYCODE_DPAD_LEFT) {
            if (!SIDE_MENU) {
                openSideMenu();
                switchToLastSelectedMenu();
                SIDE_MENU = true;
            }
        } else if (i == KeyEvent.KEYCODE_DPAD_RIGHT){
            if (SIDE_MENU) {
                SIDE_MENU = false;
                closeSideMenu();
            }
        }
        if (i == KeyEvent.KEYCODE_DPAD_CENTER){
//            Log.d("main", "item selected "+lastSelectedMenu);
            lastSelectedMenu.setActivated(false);
            lastSelectedMenu = view;
            view.setActivated(true);
            if (view.getId() == R.id.search_page && !Objects.equals(selectedMenu, Constant.MENU_SEARCH)){
                selectedMenu = Constant.MENU_SEARCH;
                Intent intent = new Intent(this, SearchActivity.class);
                startActivity(intent);
            } else if (view.getId() == R.id.anime_page){
                selectedMenu = Constant.MENU_ANIME;
                changeFragment(new AnimeView());
            } else if (view.getId() == R.id.drama_page){
                selectedMenu = Constant.MENU_DRAMA;
                changeFragment(new DramaView());
            }else if (view.getId() == R.id.tv_page){
                selectedMenu = Constant.MENU_TV;
                changeFragment(new TvView());
            } else if (view.getId() == R.id.movie_page){
                selectedMenu = Constant.MENU_MOVIE;
                changeFragment(new MovieView());
            } else {
                selectedMenu = Constant.MENU_HOME;
                changeFragment(new HomeView());
            }
        }
        return false;
    }


    private void changeFragment(Fragment fragment){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentView, fragment);
        transaction.commit();
        closeSideMenu();
    }

    @Override
    public void onBackPressed() {
        if(SIDE_MENU){
            SIDE_MENU = false;
            closeSideMenu();
        }
        super.onBackPressed();
    }

    private void openSideMenu(){
        Animation animeSlide = AnimationUtils.loadAnimation(this, R.anim.left_side);
        sideMenu.startAnimation(animeSlide);
        sideMenu.requestLayout();
        sideMenu.getLayoutParams().width = Constant.getWidthInPercent(this, 16);
    }

    private void closeSideMenu(){
        sideMenu.requestLayout();
        sideMenu.getLayoutParams().width = Constant.getWidthInPercent(this, 5);
        fragmentContainer.requestFocus();
        SIDE_MENU = false;
    }

    private void switchToLastSelectedMenu(){
        if (Objects.equals(selectedMenu, Constant.MENU_SEARCH)){
            searchPage.requestFocus();
        } else if (Objects.equals(selectedMenu, Constant.MENU_HOME)){
            homePage.requestFocus();
        }else if (Objects.equals(selectedMenu, Constant.MENU_ANIME)){
            animePage.requestFocus();
        }else if (Objects.equals(selectedMenu, Constant.MENU_DRAMA)){
            dramaPage.requestFocus();
        }else if (Objects.equals(selectedMenu, Constant.MENU_TV)){
            tvPage.requestFocus();
        }else if (Objects.equals(selectedMenu, Constant.MENU_MOVIE)){
            moviePage.requestFocus();
        }else if (Objects.equals(selectedMenu, Constant.MENU_SETTINGS)){
            settingsPage.requestFocus();
        }
    }


}