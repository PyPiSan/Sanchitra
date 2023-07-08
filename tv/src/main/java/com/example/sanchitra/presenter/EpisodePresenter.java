package com.example.sanchitra.presenter;

import static androidx.leanback.widget.BaseCardView.CARD_REGION_VISIBLE_SELECTED;
import static androidx.leanback.widget.BaseCardView.CARD_TYPE_INFO_OVER;
import static androidx.leanback.widget.BaseCardView.CARD_TYPE_MAIN_ONLY;

import android.content.Context;
import android.content.res.Resources;
import android.view.ViewGroup;

import androidx.leanback.widget.ImageCardView;
import androidx.leanback.widget.Presenter;

import com.bumptech.glide.Glide;
import com.example.sanchitra.R;
import com.example.sanchitra.api.EpisodeBody;

public class EpisodePresenter extends Presenter {

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        Context context = parent.getContext();
        ImageCardView episodeCard = new ImageCardView(context) {
            @Override
            public void setSelected(boolean selected) {
                super.setSelected(selected);
            }
        };
        episodeCard.setFocusable(true);
        episodeCard.setFocusableInTouchMode(true);
        episodeCard.setInfoVisibility(CARD_REGION_VISIBLE_SELECTED);
        episodeCard.setCardType(CARD_TYPE_INFO_OVER);
        return new ViewHolder(episodeCard);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Object item) {
        EpisodeBody episodes = (EpisodeBody) item;
        ImageCardView episodeCard = (ImageCardView) viewHolder.view;

        Resources res = episodeCard.getResources();
        int width = res.getDimensionPixelSize(R.dimen.episode_card_width);
        int height = res.getDimensionPixelSize(R.dimen.episode_card_height);
        episodeCard.setMainImageDimensions(width, height);

        episodeCard.setTitleText(String.format("Episode %s", episodes.getEpisode()));
        Glide.with(episodeCard.getContext())
                .load(episodes.getImage())
                .into(episodeCard.getMainImageView());
    }

    @Override
    public void onUnbindViewHolder(ViewHolder viewHolder) {

    }
}
