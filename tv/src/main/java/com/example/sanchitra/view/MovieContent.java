package com.example.sanchitra.view;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.leanback.app.VerticalGridSupportFragment;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.FocusHighlight;
import androidx.leanback.widget.ListRowPresenter;
import androidx.leanback.widget.OnItemViewClickedListener;
import androidx.leanback.widget.OnItemViewSelectedListener;
import androidx.leanback.widget.Presenter;
import androidx.leanback.widget.Row;
import androidx.leanback.widget.RowPresenter;
import androidx.leanback.widget.VerticalGridPresenter;

import com.example.sanchitra.R;
import com.example.sanchitra.presenter.MoviePresenter;

public class MovieContent extends VerticalGridSupportFragment {

    private static final int NUM_COLUMNS = 3;
    private static final int NUM_ITEMS = 50;
    private static final int HEIGHT = 200;
    private static final boolean TEST_ENTRANCE_TRANSITION = false;

    private static class Adapter extends ArrayObjectAdapter {
        public Adapter(MoviePresenter presenter) {
            super(presenter);
        }
        public void callNotifyChanged() {
            super.notifyChanged();
        }
    }
    private Adapter movieAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupFragment();
        if (TEST_ENTRANCE_TRANSITION) {
            // don't run entrance transition if fragment is restored.
            if (savedInstanceState == null) {
                prepareEntranceTransition();
            }
        }
//         simulates in a real world use case  data being loaded two seconds later
//        new Handler().postDelayed(new Runnable() {
//            public void run() {
//
////                startEntranceTransition();
//            }
//        },  0);
        loadData();
        setSelectedPosition(0);
        Log.d("movie view", "View Created");
    }

    private void loadData() {
        for (int i = 0; i < NUM_ITEMS; i++) {
            Log.d("movie", "Adding data to presenter "+i);
            movieAdapter.add(Integer.toString(i));

        }
    }

    private void setupFragment() {
        VerticalGridPresenter gridPresenter = new VerticalGridPresenter();
        gridPresenter.setNumberOfColumns(NUM_COLUMNS);
        setGridPresenter(gridPresenter);
        movieAdapter = new Adapter(new MoviePresenter());
        setAdapter(movieAdapter);
        setOnItemViewSelectedListener(new OnItemViewSelectedListener() {
            @Override
            public void onItemSelected(Presenter.ViewHolder itemViewHolder, Object item,
                                       RowPresenter.ViewHolder rowViewHolder, Row row) {
                Log.i("movie", "onItemSelected: " + item + " row " + row);
            }
        });
        setOnItemViewClickedListener(new OnItemViewClickedListener() {
            @Override
            public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
                                      RowPresenter.ViewHolder rowViewHolder, Row row) {
                Log.i("movie", "onItemClicked: " + item + " row " + row);
//                movieAdapter.notify();
            }
        });

    }
}
