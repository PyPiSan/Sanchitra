package com.example.sanchitra.presenter;

import static androidx.leanback.widget.BaseCardView.CARD_REGION_VISIBLE_SELECTED;
import static androidx.leanback.widget.BaseCardView.CARD_TYPE_MAIN_ONLY;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.leanback.widget.ImageCardView;
import androidx.leanback.widget.Presenter;
import com.bumptech.glide.Glide;
import com.example.sanchitra.R;
import com.example.sanchitra.model.ContentModel;

public class MoviePresenter extends Presenter {
    private Context context;
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        Log.d("movie presenter", "presenter Created");
        context = parent.getContext();
        ImageCardView contentCard = new ImageCardView(context) {
            @Override
            public void setSelected(boolean selected) {
                super.setSelected(selected);
            }
        };
        contentCard.setFocusable(true);
        contentCard.setFocusableInTouchMode(true);
        contentCard.setInfoVisibility(CARD_REGION_VISIBLE_SELECTED);
        contentCard.setCardType(CARD_TYPE_MAIN_ONLY);
        return new ViewHolder(contentCard);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Object item) {
        Log.d("movie presenter", "presenter Created");
        ContentModel movieContentModel = (ContentModel) item;
        String image = "https://imagecdn.me/cover/eye-of-the-storm-2023-1699063261.png";
        Log.d("movie presenter", "onBindViewHolder for " + item.toString());
        ImageCardView contentCard = (ImageCardView) viewHolder.view;
        if (image != null){
            Resources res = contentCard.getResources();
            int width = res.getDimensionPixelSize(R.dimen.card_width);
            int height = res.getDimensionPixelSize(R.dimen.card_height);
            contentCard.setMainImageDimensions(width, height);

            Glide.with(contentCard.getContext())
                    .load(image)
//                    .placeholder("No Name")
                    .into(contentCard.getMainImageView());
        }

    }

    @Override
    public void onUnbindViewHolder(ViewHolder viewHolder) {
        Log.d("movie presenter", "onUnbindViewHolder");

    }
}
