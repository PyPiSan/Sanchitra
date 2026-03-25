package com.example.sanchitra.view;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.leanback.app.RowsSupportFragment;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.FocusHighlight;
import androidx.leanback.widget.HeaderItem;
import androidx.leanback.widget.ListRow;
import androidx.leanback.widget.ListRowPresenter;
import androidx.leanback.widget.OnItemViewClickedListener;
import androidx.leanback.widget.Presenter;
import androidx.leanback.widget.Row;
import androidx.leanback.widget.RowPresenter;

import android.util.Log;
import android.view.View;

import com.example.sanchitra.data.models.AnimeContentListModel;
import com.example.sanchitra.data.models.ContentModel;
import com.example.sanchitra.presentation.AnimeContentPresenter;
import com.example.sanchitra.utils.Constant;
import com.example.sanchitra.utils.RequestModule;

import com.example.sanchitra.R;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AnimeContent extends RowsSupportFragment {

    private final ArrayObjectAdapter rootAnimeAdapter = new ArrayObjectAdapter(
            new ListRowPresenter(FocusHighlight.ZOOM_FACTOR_MEDIUM));
    private HeaderItem header;

    private androidx.fragment.app.Fragment mSpinnerFragment;

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onPreExecute();
        insertDataToCard();
        setAdapter(rootAnimeAdapter);
        setupEventListeners();
    }


    private void insertDataToCard() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constant.animeUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RequestModule contentAnimeRequest = retrofit.create(RequestModule.class);
        Call<AnimeContentListModel> call = contentAnimeRequest.getAnimeContentList(Constant.key);

        call.enqueue(new Callback<AnimeContentListModel>() {
            @Override
            public void onResponse(Call<AnimeContentListModel> call, Response<AnimeContentListModel> response) {
                Log.d("Log1", "API request: " + call.request().url().toString());
                Log.d("Log1", "Response code is : " + response.code());
                AnimeContentListModel resources = response.body();
                boolean status = resources.getSuccess();
                if (status) {
                    List<AnimeContentListModel.datum> data = resources.getData();
                    for (AnimeContentListModel.datum contentListHeader : data){
                        ArrayObjectAdapter animeList = new ArrayObjectAdapter(new AnimeContentPresenter());
                        for (ContentModel contentDetail : contentListHeader.getContentList()){
                            animeList.add(contentDetail);
                        }
                        header = new HeaderItem(contentListHeader.getContentHeader());
                        rootAnimeAdapter.add(new ListRow(header, animeList));
                    }
                    onPostExecute();
                }
//                rootAdapter.notifyArrayItemRangeChanged(1, 20);
            }

            @Override
            public void onFailure(Call<AnimeContentListModel> call, Throwable t) {
                onPostExecute();
                Log.d("Log4", "Response code is : 400" + t.getMessage());
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        onPostExecute();
    }

    private void setupEventListeners() {
        setOnItemViewSelectedListener(new ItemViewSelectedListener());
        setOnItemViewClickedListener(new ItemViewClickedListener());
    }

    public static final class ItemViewSelectedListener implements androidx.leanback.widget.OnItemViewSelectedListener {
        @Override
        public void onItemSelected(Presenter.ViewHolder itemViewHolder, Object item,
                                   RowPresenter.ViewHolder rowViewHolder, Row row) {
            if (item instanceof ContentModel) {
                ContentModel animeContent = (ContentModel) item;
                String animeImage = animeContent.getImage();
                String animeName = animeContent.getTitle();

                android.widget.ImageView mainImage = itemViewHolder.view.getRootView().findViewById(R.id.main_image);
                android.widget.TextView title = itemViewHolder.view.getRootView().findViewById(R.id.title);
                android.widget.TextView releaseView = itemViewHolder.view.getRootView().findViewById(R.id.released);
                android.widget.TextView summary = itemViewHolder.view.getRootView().findViewById(R.id.summary);

                if (mainImage != null) {
                    com.bumptech.glide.Glide.with(itemViewHolder.view.getContext())
                            .load(animeImage)
                            .into(mainImage);
                }
                if (title != null) title.setText(animeName);
                if (releaseView != null) releaseView.setText(animeContent.getReleased());
                if (summary != null) summary.setText(animeContent.getSummary());
            }
        }
    }


    final class ItemViewClickedListener implements OnItemViewClickedListener {
        @Override
        public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
                                  RowPresenter.ViewHolder rowViewHolder, Row row) {
            // each time the item is clicked, code inside here will be executed.

            if (item instanceof ContentModel) {
                ContentModel animeContentModel = (ContentModel) item;
                Intent intent = new Intent(getActivity(), DetailViewActivity.class);
                intent.putExtra("title", animeContentModel.getTitle());
                intent.putExtra("summary", animeContentModel.getSummary());
                intent.putExtra("image", animeContentModel.getImage());
                intent.putExtra("release", animeContentModel.getReleased());
                intent.putExtra("type", "anime");
                getActivity().startActivity(intent);
            }
        }
    }

    protected void onPreExecute(){
        mSpinnerFragment = new SpinnerView();
        if (getFragmentManager() != null) {
            getFragmentManager().beginTransaction().add(android.R.id.content, mSpinnerFragment).commit();
        }
    }

    protected void onPostExecute(){
        if (mSpinnerFragment != null && getFragmentManager() != null && !getFragmentManager().isStateSaved()) {
            getFragmentManager().beginTransaction().remove(mSpinnerFragment).commit();
            mSpinnerFragment = null;
        }
    }

}