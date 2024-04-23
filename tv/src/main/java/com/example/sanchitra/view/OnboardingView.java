package com.example.sanchitra.view;


import androidx.fragment.app.FragmentActivity;
import android.content.Intent;
import android.os.Bundle;

import com.example.sanchitra.R;

public class OnboardingView extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding_view);
        startActivity(new Intent(this, OnboardingView.class));
    }
}