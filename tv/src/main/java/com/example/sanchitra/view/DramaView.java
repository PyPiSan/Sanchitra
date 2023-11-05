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

public class DramaView extends Fragment {

    @SuppressLint("StaticFieldLeak")
    private static ImageView titleImage;
    @SuppressLint("StaticFieldLeak")
    private static TextView name;
    @SuppressLint("StaticFieldLeak")
    private static TextView summary;
    @SuppressLint("StaticFieldLeak")
    private static Context context;
    public DramaView() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {super.onCreate(savedInstanceState);}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_drama_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        titleImage = view.findViewById(R.id.main_image);
        name = view.findViewById(R.id.title);
        summary = view.findViewById(R.id.summary);
        context = getContext();

        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        Fragment commonContentListView = new CommonContentList();
        transaction.add(R.id.content_list, commonContentListView);
        transaction.commit();
    }

    public static final class ItemViewSelectedListener implements OnItemViewSelectedListener {
        @Override
        public void onItemSelected(Presenter.ViewHolder itemViewHolder, Object item,
                                   RowPresenter.ViewHolder rowViewHolder, Row row) {
            // each time the item is selected, code inside here will be executed.
            if (item instanceof ContentModel) {
                ContentModel contents = (ContentModel) item;

                Glide.with(context)
                        .load(contents.getImage())
                        .into(titleImage);
                name.setText(contents.getTitle());
                summary.setText(contents.getSummary());
            }
        }
    }

}