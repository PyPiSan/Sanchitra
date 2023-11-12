package com.example.sanchitra.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sanchitra.R;

public class TvView extends Fragment {

    public TvView() {
    }
    public static TvView newInstance() {
        return new TvView();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tv_view, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        Fragment tvListFragment = new TVContent();
        transaction.add(R.id.tv_content_fragment, tvListFragment);
        transaction.commit();
    }
}