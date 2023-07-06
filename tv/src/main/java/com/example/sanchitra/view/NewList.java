package com.example.sanchitra.view;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.widget.Toast;

import com.example.sanchitra.model.AnimeModel;
import com.example.sanchitra.model.AnimeRecentModel;
import com.example.sanchitra.presenter.RecentPresenter;
import com.example.sanchitra.utils.RequestModule;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NewList extends RowsSupportFragment {

    private final ArrayObjectAdapter rootAdapter = new ArrayObjectAdapter(
            new ListRowPresenter(FocusHighlight.ZOOM_FACTOR_MEDIUM));
    private HeaderItem header;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        insertDataToCard("1");
        setAdapter(rootAdapter);
        setupEventListeners();
    }


    private void insertDataToCard(String pageNum) {
//        Add the cards data and display them
//        fetching data
        if (pageNum.equals("")){
            pageNum="1";
        }
        ArrayObjectAdapter animeList = new ArrayObjectAdapter(new RecentPresenter());
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://anime.pypisan.com/v1/anime/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RequestModule animeRecent = retrofit.create(RequestModule.class);
        Call<AnimeRecentModel> call = animeRecent.getAnime(pageNum);

        call.enqueue(new Callback<AnimeRecentModel>() {
            @Override
            public void onResponse(Call<AnimeRecentModel> call, Response<AnimeRecentModel> response) {
                Log.d("Log1", "Response code is : " + response.code());
                AnimeRecentModel resources = response.body();
                boolean status = resources.getSuccess();
                if (status){
                    List<AnimeRecentModel.datum> data = resources.getData();
                    AnimeModel model = new AnimeModel();
                    for (AnimeRecentModel.datum animes : data) {
//                        Log.d("Log2", "Response code is : " + response.body() +  i);
                        model = new AnimeModel(animes.getImageLink(), animes.getAnimeDetailLink(),
                                animes.getTitle(), animes.getReleased());
                        animeList.add(model);
                    }
                    header = new HeaderItem("New Anime");
                    rootAdapter.add(new ListRow(header, animeList));
                }
//                rootAdapter.notifyArrayItemRangeChanged(1, 20);
            }

            @Override
            public void onFailure(Call<AnimeRecentModel> call, Throwable t) {
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

            if (item instanceof AnimeModel) {
                AnimeModel animeModel = (AnimeModel) item;
                Log.d(getTag(), "Item: " + item.toString());
                Toast.makeText(getContext(), "Card is " + animeModel.getTitle(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), DetailViewActivity.class);
                intent.putExtra("title", animeModel.getTitle());
                getActivity().startActivity(intent);
            }
        }
    }
}