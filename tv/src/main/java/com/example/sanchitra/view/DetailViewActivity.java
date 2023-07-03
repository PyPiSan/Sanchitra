package com.example.sanchitra.view;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;

import com.example.sanchitra.R;
import com.example.sanchitra.databinding.ActivityDetailViewBinding;

public class DetailViewActivity extends FragmentActivity {

    private ActivityDetailViewBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_detail_view);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail_view);
    }
}