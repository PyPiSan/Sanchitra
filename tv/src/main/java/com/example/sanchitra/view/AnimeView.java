package com.example.sanchitra.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.leanback.widget.OnItemViewSelectedListener;
import androidx.leanback.widget.Presenter;
import androidx.leanback.widget.Row;
import androidx.leanback.widget.RowPresenter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.sanchitra.R;
import com.example.sanchitra.model.ContentModel;

public class AnimeView extends Fragment {

    @SuppressLint("StaticFieldLeak")
    private static ImageView mainImage;
    @SuppressLint("StaticFieldLeak")
    private static TextView title, summary, releaseView, genresView;
    @SuppressLint("StaticFieldLeak")
    private static Context context;
    public AnimeView() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_anime_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mainImage = view.findViewById(R.id.main_image);
        title = view.findViewById(R.id.title);
        releaseView= view.findViewById(R.id.released);
        genresView = view.findViewById(R.id.genres);
        summary = view.findViewById(R.id.summary);
        context = getContext();

        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        Fragment newListFragment = new AnimeContent();
        transaction.add(R.id.new_list, newListFragment);
        transaction.commit();

    }
    public static final class ItemViewSelectedListener implements OnItemViewSelectedListener {
        @Override
        public void onItemSelected(Presenter.ViewHolder itemViewHolder, Object item,
                                   RowPresenter.ViewHolder rowViewHolder, Row row) {
            // each time the item is selected, code inside here will be executed.
            if (item instanceof ContentModel) {
                ContentModel animeContent = (ContentModel) item;
                String animeImage = animeContent.getImage();
                String  animeName = animeContent.getTitle();
                Glide.with(context)
                        .load(animeImage)
                        .into(mainImage);
                title.setText(animeName);
                releaseView.setText(animeContent.getReleased());
                summary.setText(animeContent.getSummary());
            }
        }
    }
}