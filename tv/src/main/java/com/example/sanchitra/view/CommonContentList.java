package com.example.sanchitra.view;

import android.content.Intent;
import android.os.Bundle;

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
import com.example.sanchitra.model.DramaContentListModel;
import com.example.sanchitra.model.DramaContentModel;
import com.example.sanchitra.presenter.CommonContentPresenter;
import com.example.sanchitra.utils.Constant;
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
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        Insert Data
        insertDataToCard();
//        Set Adapter
        setAdapter(contentAdapter);
//        Set Click Listener
        setupEventListeners();
    }

    private void insertDataToCard() {
//        Add the cards data and display them
//        fetching data
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constant.dramaUrl)
                .addConverterFactory(GsonConverterFactory
                .create())
                .build();

        RequestModule contentGetRequest = retrofit.create(RequestModule.class);
        Call<DramaContentListModel> call = contentGetRequest.getDramaContentList(Constant.key);

        call.enqueue(new Callback<DramaContentListModel>() {
            @Override
            public void onResponse(Call<DramaContentListModel> call, Response<DramaContentListModel> response) {
                Log.d("Log1", "Response code is : " + response.code());
                DramaContentListModel resources = response.body();
                boolean status = resources.getSuccess();
                if (status) {
                    List<DramaContentListModel.datum> data = resources.getData();
                    for (DramaContentListModel.datum contentListHeader : data){
                        ArrayObjectAdapter contents = new ArrayObjectAdapter(new CommonContentPresenter());
                        for (DramaContentModel contentDetail : contentListHeader.getContentList()){
                            contents.add(contentDetail);
                        }
                        header = new HeaderItem(contentListHeader.getContentHeader());
                        contentAdapter.add(new ListRow(header, contents));
                    }
                }
//                contentAdapter.notifyArrayItemRangeChanged(1, 20);
            }

            @Override
            public void onFailure(Call<DramaContentListModel> call, Throwable t) {
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

            if (item instanceof DramaContentModel) {
                DramaContentModel contents = (DramaContentModel) item;
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