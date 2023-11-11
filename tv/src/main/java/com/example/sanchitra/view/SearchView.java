package com.example.sanchitra.view;

import android.content.Intent;
import android.os.Bundle;

import androidx.leanback.app.SearchSupportFragment;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.HeaderItem;
import androidx.leanback.widget.ListRow;
import androidx.leanback.widget.ListRowPresenter;
import androidx.leanback.widget.ObjectAdapter;
import androidx.leanback.widget.OnItemViewClickedListener;
import androidx.leanback.widget.Presenter;
import androidx.leanback.widget.Row;
import androidx.leanback.widget.RowPresenter;

import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.widget.Toast;

import com.example.sanchitra.model.AnimeContentListModel;
import com.example.sanchitra.model.CommonDataModel;
import com.example.sanchitra.model.ContentModel;
import com.example.sanchitra.presenter.AnimeContentPresenter;
import com.example.sanchitra.presenter.CommonContentPresenter;
import com.example.sanchitra.utils.Constant;
import com.example.sanchitra.utils.RequestModule;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchView extends SearchSupportFragment implements SearchSupportFragment.SearchResultProvider {

    private ArrayObjectAdapter searchAdapter;
    private SpeechRecognizer speechRecognizer;
    private HeaderItem header;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSearchResultProvider(this);
        searchAdapter = new ArrayObjectAdapter(new ListRowPresenter());

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(getContext());
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float v) {

            }

            @Override
            public void onBufferReceived(byte[] bytes) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int i) {

            }

            @Override
            public void onResults(Bundle results) {
                String query = results.getString(SpeechRecognizer.RESULTS_RECOGNITION);

                if (!query.isEmpty()){
                    Log.d("search", "query "+ query);
                    setSearchQuery(query, true);
                }
            }

            @Override
            public void onPartialResults(Bundle bundle) {

            }

            @Override
            public void onEvent(int i, Bundle bundle) {

            }
        });
        setupEventListeners();
    }


    private void startSpechRecognitation(){
//        try{
//            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
//            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
//                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
//        } catch (){
//
//        }
    }

    @Override
    public ObjectAdapter getResultsAdapter() {
        return searchAdapter;
    }

    @Override
    public boolean onQueryTextChange(String newQuery) {
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        Toast.makeText(getContext(), query, Toast.LENGTH_SHORT).show();
        searchData(query, "Drama Results");
        searchData(query, "Anime Results");
        return true;
    }

    private void searchData(String query, String type) {
        String url;
        if (type.equals("Drama Results")){
            url = Constant.dramaUrl;
        }else {
            url = Constant.animeUrl;
        }
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RequestModule searchRequest = retrofit.create(RequestModule.class);
        Call<CommonDataModel> call = searchRequest.getSearchResults(Constant.key, query);

        call.enqueue(new Callback<CommonDataModel>() {
            @Override
            public void onResponse(Call<CommonDataModel> call, Response<CommonDataModel> response) {
                Log.d("Search", "Response code is : " + response.code());
                CommonDataModel resources = response.body();
                boolean status = resources.getSuccess();
                if (status) {
                    List<ContentModel> data = resources.getResults();
                    ArrayObjectAdapter searchList = new ArrayObjectAdapter(new CommonContentPresenter());;
                    for (ContentModel contentList : data) {
                        searchList.add(contentList);
                    }
                    header = new HeaderItem(type);
                    searchAdapter.add(new ListRow(header, searchList));
                }
//                rootAdapter.notifyArrayItemRangeChanged(1, 20);
            }

            @Override
            public void onFailure(Call<CommonDataModel> call, Throwable t) {
                Log.d("Log4", "Response code is : 400" + t.getMessage());
            }
        });
    }
    private void setupEventListeners() {
        setOnItemViewClickedListener(new SearchView.ItemViewClickedListener());
    }

    final class ItemViewClickedListener implements OnItemViewClickedListener {
        @Override
        public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
                                  RowPresenter.ViewHolder rowViewHolder, Row row) {
            // each time the item is clicked, code inside here will be executed.

            if (item instanceof ContentModel) {
                ContentModel ContentModel = (ContentModel) item;
                Intent intent = new Intent(getActivity(), DetailViewActivity.class);
                intent.putExtra("title", ContentModel.getTitle());
                intent.putExtra("summary", ContentModel.getSummary());
                intent.putExtra("image", ContentModel.getImage());
                intent.putExtra("release", ContentModel.getReleased());
                intent.putExtra("type", ContentModel.getType());
                getActivity().startActivity(intent);
            }
        }
    }
}