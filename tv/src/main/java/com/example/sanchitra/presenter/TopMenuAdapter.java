package com.example.sanchitra.presenter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.sanchitra.view.HomeView;

public class TopMenuAdapter extends FragmentStatePagerAdapter {
    public TopMenuAdapter(@NonNull FragmentManager fm) {
        super(fm,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "object " + (position + 1);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = new HomeView();
        Bundle args = new Bundle();
        // Our object is just an integer :-P
        args.putInt("object", position + 1);
        fragment.setArguments(args);
        return fragment;
    }
}
