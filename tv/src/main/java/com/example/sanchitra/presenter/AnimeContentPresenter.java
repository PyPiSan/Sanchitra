package com.example.sanchitra.presenter;

import static androidx.leanback.widget.BaseCardView.CARD_REGION_VISIBLE_SELECTED;
import static androidx.leanback.widget.BaseCardView.CARD_TYPE_MAIN_ONLY;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.leanback.widget.ImageCardView;
import androidx.leanback.widget.Presenter;

import com.bumptech.glide.Glide;
import com.example.sanchitra.model.AnimeContentModel;
import com.example.sanchitra.model.AnimeModel;
import com.example.sanchitra.R;

public class AnimeContentPresenter extends Presenter {

    private Context context;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
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
        AnimeContentModel animeContentModel = (AnimeContentModel) item;
        ImageCardView contentCard = (ImageCardView) viewHolder.view;
        contentCard.setTitleText(animeContentModel.getTitle());

        if (animeContentModel.getImage() != null){
            Resources res = contentCard.getResources();
            int width = res.getDimensionPixelSize(R.dimen.card_width);
            int height = res.getDimensionPixelSize(R.dimen.card_height);
            contentCard.setMainImageDimensions(width, height);

            Glide.with(contentCard.getContext())
                    .load(animeContentModel.getImage())
                    .into(contentCard.getMainImageView());
        }


    }

    @Override
    public void onUnbindViewHolder(ViewHolder viewHolder) {

    }

//    public int getWidthInPercent(Context context, int percent){
//       int widthPixels = context.getResources().getDisplayMetrics().widthPixels;
//        return (widthPixels*percent)/100;
//    }
//
//    public int getHeightInPercent(Context context, int percent){
//        int heightPixels = context.getResources().getDisplayMetrics().heightPixels;
//        return (heightPixels*percent)/100;
//    }
}
