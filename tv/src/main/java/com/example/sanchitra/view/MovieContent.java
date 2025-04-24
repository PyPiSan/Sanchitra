package com.example.sanchitra.view;

import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
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
// Assuming ContentModel exists in this package or is imported correctly
import com.example.sanchitra.model.ContentModel;
import com.example.sanchitra.presenter.MoviePresenter;

import java.util.ArrayList;
import java.util.List;

public class MovieContent extends VerticalGridSupportFragment {

    private static final int NUM_ITEMS = 50;
    private static final int HEIGHT = 200;
    private static final boolean TEST_ENTRANCE_TRANSITION = false;
    // Threshold for screen width in pixels to change column count
    private static final int WIDTH_THRESHOLD_PX = 1920;
    private static final int COLUMNS_HIGH_RES = 5;
    private static final int COLUMNS_LOW_RES = 4;

    // Adapter now specifically holds ContentModel objects
    private static class Adapter extends ArrayObjectAdapter {
        public Adapter(MoviePresenter presenter) {
            super(presenter);
        }
        // This method might not be necessary anymore, depending on usage
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
        // TODO: Replace this with actual data fetching logic (e.g., from ViewModel/Repository)
        // Example: viewModel.getMovies().observe(getViewLifecycleOwner(), movies -> { ... });

        // Create dummy data for demonstration
        List<ContentModel> dummyMovies = new ArrayList<>();
        for (int i = 0; i < NUM_ITEMS; i++) {
            // Assuming ContentModel has a constructor or setters for title/poster
            // We don't have the definition, so creating a basic object.
            // The presenter seems to use a fixed image URL currently.
            ContentModel movie = new ContentModel();
            // movie.setTitle("Movie " + i); // Example if setTitle exists
            // movie.setPosterUrl("some_url_" + i); // Example if setPosterUrl exists
            dummyMovies.add(movie);
        }

        // Add the dummy movie data to the adapter
        for (ContentModel movie : dummyMovies) {
            movieAdapter.add(movie);
        }
        // Notify the adapter that the data has changed (if needed, ArrayObjectAdapter might handle this)
        // movieAdapter.notifyChanged(); // Or callNotifyChanged() if that specific method is required
    }

    private void setupFragment() {
        // Get screen width
        DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
        int screenWidthPx = displayMetrics.widthPixels;

        // Determine number of columns based on screen width
        int numColumns = (screenWidthPx > WIDTH_THRESHOLD_PX) ? COLUMNS_HIGH_RES : COLUMNS_LOW_RES;

        VerticalGridPresenter gridPresenter = new VerticalGridPresenter();
        gridPresenter.setNumberOfColumns(numColumns);
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
