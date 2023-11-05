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

import com.example.sanchitra.model.AnimeContentListModel;
import com.example.sanchitra.model.ContentModel;
import com.example.sanchitra.presenter.AnimeContentPresenter;
import com.example.sanchitra.utils.Constant;
import com.example.sanchitra.utils.RequestModule;

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

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
                }
//                rootAdapter.notifyArrayItemRangeChanged(1, 20);
            }

            @Override
            public void onFailure(Call<AnimeContentListModel> call, Throwable t) {
                Log.d("Log4", "Response code is : 400" + t.getMessage());
            }
        });
    }

    private void setupEventListeners() {
        setOnItemViewSelectedListener(new AnimeView.ItemViewSelectedListener());
        setOnItemViewClickedListener(new ItemViewClickedListener());
    }


    final class ItemViewClickedListener implements OnItemViewClickedListener {
        @Override
        public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
                                  RowPresenter.ViewHolder rowViewHolder, Row row) {
            // each time the item is clicked, code inside here will be executed.

            if (item instanceof ContentModel) {
                ContentModel animeContentModel = (ContentModel) item;
//                Log.d(getTag(), "Item: " + item.toString());
//                Toast.makeText(getContext(), "Card is " + animeContentModel.getTitle(),
//                        Toast.LENGTH_SHORT).show();
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
}