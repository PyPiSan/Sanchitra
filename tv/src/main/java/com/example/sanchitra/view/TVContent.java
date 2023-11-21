package com.example.sanchitra.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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

import com.example.sanchitra.R;
import com.example.sanchitra.model.TVChannelListModel;
import com.example.sanchitra.model.TVContentModel;
import com.example.sanchitra.player.VideoPlayer;
import com.example.sanchitra.presenter.TVPresenter;
import com.example.sanchitra.utils.Constant;
import com.example.sanchitra.utils.RequestModule;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TVContent extends RowsSupportFragment {

    private final ArrayObjectAdapter tvChannelAdapter = new ArrayObjectAdapter(
            new ListRowPresenter());
    private HeaderItem header;

    private Fragment mSpinnerFragment;

    private ProgressBar loader;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onPreExecute();
        insertDataToCard();
        setAdapter(tvChannelAdapter);
        setupEventListeners();
    }

    private void insertDataToCard() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constant.tvUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RequestModule tvChannelRequest = retrofit.create(RequestModule.class);
        Call<TVChannelListModel> call = tvChannelRequest.getTvChannelList(Constant.key);

        call.enqueue(new Callback<TVChannelListModel>() {
            @Override
            public void onResponse(Call<TVChannelListModel> call, Response<TVChannelListModel> response) {
                Log.d("TV", "Response code is : " + response.code());
                TVChannelListModel resources = response.body();
                boolean status = resources.getSuccess();
                if (status) {
                    List<TVChannelListModel.datum> data = resources.getData();
                    for (TVChannelListModel.datum contentListHeader : data){
                        ArrayObjectAdapter channelList = new ArrayObjectAdapter(new TVPresenter());
                        for (TVContentModel contentDetail : contentListHeader.getContentList()){
                            channelList.add(contentDetail);
                        }
                        header = new HeaderItem(contentListHeader.getContentHeader());
                        tvChannelAdapter.add(new ListRow(header, channelList));
                    }
                }
                onPostExecute();
//                rootAdapter.notifyArrayItemRangeChanged(1, 20);
            }

            @Override
            public void onFailure(Call<TVChannelListModel> call, Throwable t) {
                Log.d("Log4", "Response code is : 400" + t.getMessage());
            }
        });
    }

    private void setupEventListeners() {
//        setOnItemViewSelectedListener(this);
        setOnItemViewClickedListener(new TVContent.ItemViewClickedListener());
    }

    final class ItemViewClickedListener implements OnItemViewClickedListener {
        @Override
        public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
                                  RowPresenter.ViewHolder rowViewHolder, Row row) {
            // each time the item is clicked, code inside here will be executed.

            if (item instanceof TVContentModel) {
                TVContentModel tvContentModel = (TVContentModel) item;
                Intent intent = new Intent(getActivity(), VideoPlayer.class);
                intent.putExtra("channel", tvContentModel.getChannelName());
                intent.putExtra("id", tvContentModel.getChannelId().toString());
                intent.putExtra("logo", tvContentModel.getLogoUrl());
                intent.putExtra("language", tvContentModel.getChannelLanguageId());
                intent.putExtra("type", "tv");
                getActivity().startActivity(intent);
            }
        }
    }

    protected void onPreExecute(){
        mSpinnerFragment = new SpinnerView();
        getFragmentManager().beginTransaction().add(R.id.tv_content_fragment, mSpinnerFragment).commit();
    }

    protected void onPostExecute(){
        getFragmentManager().beginTransaction().remove(mSpinnerFragment).commit();
    }

}
