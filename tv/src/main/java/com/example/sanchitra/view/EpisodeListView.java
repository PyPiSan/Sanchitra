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
import com.example.sanchitra.api.EpisodeBody;
import com.example.sanchitra.api.Title;
import com.example.sanchitra.model.EpisodeListModel;
import com.example.sanchitra.player.VideoPlayer;
import com.example.sanchitra.presenter.EpisodePresenter;
import com.example.sanchitra.utils.Constant;
import com.example.sanchitra.utils.RequestModule;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EpisodeListView extends RowsSupportFragment {

    private final ArrayObjectAdapter episodeAdapter =
            new ArrayObjectAdapter(new ListRowPresenter(FocusHighlight.ZOOM_FACTOR_MEDIUM));

    private HeaderItem header;

    private String title, type;
    public EpisodeListView() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        title = getArguments().getString("title");
        type = getArguments().getString("type");
//      Insert Data
        insertDataToCardDrama(title, type);
//        Set Adapter
        setAdapter(episodeAdapter);
        setupEventListeners();

    }


    private void insertDataToCardDrama(String title, String type) {
//        Add the cards data and display them
        String url;
        if (type=="anime"){
            url = Constant.animeUrl;
        }else{
            url = Constant.dramaUrl;
        }
//        Log.d("episodeList", "url is "+ url);
//        fetching data
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RequestModule episodeGetRequest = retrofit.create(RequestModule.class);
        Call<EpisodeListModel> call = episodeGetRequest.getEpisodeList(Constant.key, new Title(title));

        call.enqueue(new Callback<EpisodeListModel>() {
            @Override
            public void onResponse(Call<EpisodeListModel> call, Response<EpisodeListModel> response) {
                Log.d("episodeList", "Response code is : " + response.code());
                EpisodeListModel resources = response.body();
                ArrayObjectAdapter episodes = new ArrayObjectAdapter(new EpisodePresenter());
                boolean status = resources.getSuccess();
                if (status){
                        EpisodeListModel.datum details = resources.getData();
                        int episodeNumbers = details.getEpisodes();
                    Log.d("episodeList", "Episode number is : " + String.valueOf(details.getEpisodes()));
                        for(int i =1;i<=episodeNumbers; i++){
                            EpisodeBody episodeBody = new EpisodeBody(details.getTitle(),
                                    String.valueOf(i), details.getImageLink());
                            episodes.add(episodeBody);
                        }
                        header = new HeaderItem("Episodes");
                        episodeAdapter.add(new ListRow(header, episodes));
                    }
                }
//                contentAdapter.notifyArrayItemRangeChanged(1, 20);

            @Override
            public void onFailure(Call<EpisodeListModel> call, Throwable t) {
                Log.d("episodeList", "Response code is : 400" + t.getMessage());
            }
        });
    }
    private void setupEventListeners() {
        setOnItemViewClickedListener(new ItemViewClickedListener());
    }

    private final class ItemViewClickedListener implements OnItemViewClickedListener {
        @Override
        public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {
            // each time the item is clicked, code inside here will be executed.

            if (item instanceof EpisodeBody) {
                EpisodeBody episodes = (EpisodeBody) item;
                Intent intent = new Intent(getActivity(), VideoPlayer.class);
                intent.putExtra("title", title);
                intent.putExtra("episode", episodes.getEpisode());
                intent.putExtra("type", type);
                getActivity().startActivity(intent);
            }
        }
    }
}