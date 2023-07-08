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
import com.example.sanchitra.model.ContentModel;
import com.example.sanchitra.model.EpisodeListModel;
import com.example.sanchitra.player.PlaybackOverlay;
import com.example.sanchitra.player.VideoPlayer;
import com.example.sanchitra.presenter.EpisodePresenter;
import com.example.sanchitra.utils.RequestModule;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EpisodeListView extends RowsSupportFragment {

    private final ArrayObjectAdapter episodeAdapter =
            new ArrayObjectAdapter(new ListRowPresenter(FocusHighlight.ZOOM_FACTOR_MEDIUM));

    private HeaderItem header;
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

        final String apikey = "e7y6acFyHGqwtkBLKHx6eA";
        final String baseUrl = "https://drama.pypisan.com/v1/drama/" ;
        String title = getArguments().getString("title");
//      Insert Data
        insertDataToCard(apikey, title, baseUrl);
//        Set Adapter
        setAdapter(episodeAdapter);
        setupEventListeners();

    }


    private void insertDataToCard(String apikey, String title, String baseURL) {
//        Add the cards data and display them
//        fetching data
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RequestModule episodeGetRequest = retrofit.create(RequestModule.class);
        Call<EpisodeListModel> call = episodeGetRequest.getEpisodeList(apikey, new Title(title));

        call.enqueue(new Callback<EpisodeListModel>() {
            @Override
            public void onResponse(Call<EpisodeListModel> call, Response<EpisodeListModel> response) {
                Log.d("Log1", "Response code is : " + response.code());
                EpisodeListModel resources = response.body();
                ArrayObjectAdapter episodes = new ArrayObjectAdapter(new EpisodePresenter());
                boolean status = resources.getSuccess();
                if (status){
                        EpisodeListModel.datum details = resources.getData();
                        int episodeNumbers = details.getEpisodes();
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
                Log.d("Log4", "Response code is : 400" + t.getMessage());
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
                intent.putExtra("title", episodes.getTitle());
                intent.putExtra("episode", episodes.getEpisode());
                getActivity().startActivity(intent);
            }
        }
    }
}