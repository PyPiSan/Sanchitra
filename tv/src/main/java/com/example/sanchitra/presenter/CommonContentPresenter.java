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
import com.example.sanchitra.model.ContentModel;

public class CommonContentPresenter extends Presenter {

    private Context context;
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
//        View view = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.content_presenter_view, parent, false);
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
    public void onBindViewHolder(Presenter.ViewHolder viewHolder, Object item) {
        ContentModel contents = (ContentModel) item;
        ImageCardView contentCard = (ImageCardView) viewHolder.view;
        contentCard.setTitleText(contents.getTitle());

        if (contents.getImage() != null){
            Resources res = contentCard.getResources();
            int width = res.getDimensionPixelSize(R.dimen.card_width);
            int height = res.getDimensionPixelSize(R.dimen.card_height);
            contentCard.setMainImageDimensions(width, height);

            Glide.with(contentCard.getContext())
                    .load(contents.getImage())
                    .into(contentCard.getMainImageView());
        }
    }

    @Override
    public void onUnbindViewHolder(ViewHolder viewHolder) {

    }
}
