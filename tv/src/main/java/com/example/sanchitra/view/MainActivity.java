package com.example.sanchitra.view;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.leanback.widget.BrowseFrameLayout;

import com.example.sanchitra.R;
import com.example.sanchitra.utils.Constant;
import com.example.sanchitra.view.AnimeView;
import com.example.sanchitra.view.DramaView;
import com.example.sanchitra.view.HomeView;
import com.example.sanchitra.view.MovieView;
import com.example.sanchitra.view.SearchView;
import com.example.sanchitra.view.TvView;
import com.google.android.gms.ads.MobileAds;

import java.util.Objects;

public class MainActivity extends FragmentActivity implements View.OnKeyListener {

    private AppCompatButton searchPage, homePage, animePage, dramaPage, tvPage, moviePage,
            settingsPage;
//    private FrameLayout topMenu;
    private FrameLayout fragmentContainer;

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
//        topMenu = findViewById(R.id.top_menu);
        fragmentContainer = findViewById(R.id.fragmentView);

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

            if (view.getId() == R.id.search_page){
                selectedMenu = Constant.MENU_SEARCH;
                changeFragment(new SearchView());

            } else if (view.getId() == R.id.home_page){
                selectedMenu = Constant.MENU_HOME;
                changeFragment(new HomeView());

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
        return false;
    }


    private void changeFragment(Fragment fragment){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentView, fragment);
        transaction.commit();
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