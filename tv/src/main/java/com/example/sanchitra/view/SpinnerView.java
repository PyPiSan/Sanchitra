package com.example.sanchitra.view;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SpinnerView extends Fragment {
    private static final String TAG = SpinnerView.class.getSimpleName();
    private static final int SPINNER_WIDTH = 100;
    private static final int SPINNER_HEIGHT = 100;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ProgressBar progressBar = new ProgressBar(container.getContext());
        if (container instanceof FrameLayout) {
            FrameLayout.LayoutParams layoutParams =
                    new FrameLayout.LayoutParams(SPINNER_WIDTH, SPINNER_HEIGHT, Gravity.CENTER);
            progressBar.setLayoutParams(layoutParams);
        }
        return progressBar;
    }
}
