package com.example.sanchitra.view;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.leanback.app.OnboardingSupportFragment;
import androidx.lifecycle.viewmodel.CreationExtras;

import com.example.sanchitra.R;

public class OnboardingFragment extends OnboardingSupportFragment {
    private static final long SPLASH_DURATION_MS = 2000;
    @Override
    protected int getPageCount() {
        return 1;
    }

    @Override
    protected CharSequence getPageTitle(int pageIndex) {
        return null;
    }

    @Override
    protected CharSequence getPageDescription(int pageIndex) {
        return null;
    }

    @Nullable
    @Override
    protected View onCreateBackgroundView(LayoutInflater inflater, ViewGroup container) {
        return null;
    }

    @Nullable
    @Override
    protected View onCreateContentView(LayoutInflater inflater, ViewGroup container) {
        return null;
    }

    @Nullable
    @Override
    protected View onCreateForegroundView(LayoutInflater inflater, ViewGroup container) {
        return null;
    }

    @NonNull
    @Override
    public CreationExtras getDefaultViewModelCreationExtras() {
        return super.getDefaultViewModelCreationExtras();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        if (view != null) {
            view.setBackgroundColor(Color.RED);
        }
        setLogoResourceId(R.mipmap.ic_banner);
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                onFinishFragment();
            }
        };
        handler.postDelayed(runnable, SPLASH_DURATION_MS);
        return view;
    }

    @Override
    protected void onFinishFragment() {
        super.onFinishFragment();
        getActivity().finish();
    }
}
