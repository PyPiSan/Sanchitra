package com.example.sanchitra.presenter;

import static androidx.leanback.widget.BaseCardView.CARD_REGION_VISIBLE_SELECTED;
import static androidx.leanback.widget.BaseCardView.CARD_TYPE_MAIN_ONLY;

import android.content.Context;
import android.content.res.Resources;
import android.view.ViewGroup;

import androidx.leanback.widget.ImageCardView;
import androidx.leanback.widget.Presenter;

import com.bumptech.glide.Glide;
import com.example.sanchitra.R;
import com.example.sanchitra.model.TVContentModel;

public class TVPresenter extends Presenter {

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
        Resources res = contentCard.getResources();
        contentCard.setFocusable(true);
        contentCard.setFocusableInTouchMode(true);
        contentCard.setInfoVisibility(CARD_REGION_VISIBLE_SELECTED);
        contentCard.setCardType(CARD_TYPE_MAIN_ONLY);
        contentCard.setBackgroundColor(res.getColor(R.color.color_grey));
        return new ViewHolder(contentCard);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Object item) {
        TVContentModel tvContentModel = (TVContentModel) item;
        ImageCardView contentCard = (ImageCardView) viewHolder.view;
        contentCard.setTitleText(tvContentModel.getChannelName());

        if (tvContentModel.getLogoUrl() != null){
            Resources res = contentCard.getResources();
            int width = res.getDimensionPixelSize(R.dimen.tv_card_width);
            int height = res.getDimensionPixelSize(R.dimen.tv_card_height);
            contentCard.setMainImageDimensions(width, height);

            Glide.with(contentCard.getContext())
                    .load(tvContentModel.getLogoUrl())
//                    .override(600, 200) // resizes the image to these dimensions (in pixel).
                    .fitCenter()
                    .into(contentCard.getMainImageView());
        }

    }

    @Override
    public void onUnbindViewHolder(ViewHolder viewHolder) {

    }
}
