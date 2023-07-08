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
import com.example.sanchitra.model.ContentListModel;
import com.example.sanchitra.model.ContentModel;
import com.example.sanchitra.presenter.CommonContentPresenter;
import com.example.sanchitra.utils.RequestModule;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CommonContentList extends RowsSupportFragment {

    private final ArrayObjectAdapter contentAdapter =
            new ArrayObjectAdapter(new ListRowPresenter(FocusHighlight.ZOOM_FACTOR_MEDIUM));

    private HeaderItem header;


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final String apikey = "e7y6acFyHGqwtkBLKHx6eA";
        final String baseUrl = "https://drama.pypisan.com/v1/drama/";
//        Insert Data
        insertDataToCard(apikey, baseUrl);
//        Set Adapter
        setAdapter(contentAdapter);
//        Set Click Listener
        setupEventListeners();
    }

    private void insertDataToCard(String apikey, String baseURL) {
//        Add the cards data and display them
//        fetching data
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseURL)
                .addConverterFactory(GsonConverterFactory
                .create())
                .build();

        RequestModule contentGetRequest = retrofit.create(RequestModule.class);
        Call<ContentListModel> call = contentGetRequest.getDramaContentList(apikey);

        call.enqueue(new Callback<ContentListModel>() {
            @Override
            public void onResponse(Call<ContentListModel> call, Response<ContentListModel> response) {
                Log.d("Log1", "Response code is : " + response.code());
                ContentListModel resources = response.body();
                boolean status = resources.getSuccess();
                if (status) {
                    List<ContentListModel.datum> data = resources.getData();
                    for (ContentListModel.datum contentListHeader : data){
                        ArrayObjectAdapter contents = new ArrayObjectAdapter(new CommonContentPresenter());
                        for (ContentModel contentDetail : contentListHeader.getContentList()){
                            contents.add(contentDetail);
                        }
                        header = new HeaderItem(contentListHeader.getContentHeader());
                        contentAdapter.add(new ListRow(header, contents));
                    }
                }
//                contentAdapter.notifyArrayItemRangeChanged(1, 20);
            }

            @Override
            public void onFailure(Call<ContentListModel> call, Throwable t) {
                Log.d("Log4", "Response code is : 400" + t.getMessage());
            }
        });
    }


    //    Click Listener
    private void setupEventListeners() {
        setOnItemViewSelectedListener(new DramaView.ItemViewSelectedListener());
        setOnItemViewClickedListener(new ItemViewClickedListener());
    }

    private final class ItemViewClickedListener implements OnItemViewClickedListener {
        @Override
        public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {
            // each time the item is clicked, code inside here will be executed.

            if (item instanceof ContentModel) {
                ContentModel contents = (ContentModel) item;
//                Log.d(getTag(), "Item: " + item.toString());
//                Toast.makeText(getContext(), "Card is " + animeModel.getTitle(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), DetailViewActivity.class);
                intent.putExtra("title", contents.getTitle());
                intent.putExtra("summary", contents.getSummary());
                intent.putExtra("image", contents.getImage());
                intent.putExtra("release", contents.getRelease());
                intent.putExtra("type", contents.getType());
                getActivity().startActivity(intent);
            }
        }
    }
}