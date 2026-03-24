package com.example.sanchitra.view;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.progressindicator.CircularProgressIndicator;

public class SpinnerView extends Fragment {
    private static final String TAG = SpinnerView.class.getSimpleName();
    private static final int SPINNER_WIDTH = ViewGroup.LayoutParams.WRAP_CONTENT;
    private static final int SPINNER_HEIGHT = ViewGroup.LayoutParams.WRAP_CONTENT;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        CircularProgressIndicator progressBar = new CircularProgressIndicator(container.getContext());
        progressBar.setIndeterminate(true);
        if (container instanceof FrameLayout) {
            FrameLayout.LayoutParams layoutParams =
                    new FrameLayout.LayoutParams(SPINNER_WIDTH, SPINNER_HEIGHT, Gravity.CENTER);
            progressBar.setLayoutParams(layoutParams);
        }
        return progressBar;
    }
}
