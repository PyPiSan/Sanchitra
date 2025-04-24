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
        ContentModel movieContentModel = (ContentModel) item;
        ImageCardView contentCard = (ImageCardView) viewHolder.view;

        // Assuming ContentModel has getTitle() and getImageUrl() methods
        String title = "Default Title"; // Default value
        if (movieContentModel != null && movieContentModel.getTitle() != null) {
            title = movieContentModel.getTitle();
        }
        String imageUrl = null; // Default value
        if (movieContentModel != null && movieContentModel.getImageUrl() != null) {
            imageUrl = movieContentModel.getImageUrl();
        }

        Log.d("MoviePresenter", "onBindViewHolder for: " + title);

        // Set Title and Content Text
        contentCard.setTitleText(title);
        // Using title also for content text as placeholder, assuming no specific genre/year field for now
        contentCard.setContentText(title);

        // Set Card Dimensions
        Resources res = contentCard.getResources();
        int width = res.getDimensionPixelSize(R.dimen.card_width);
        int height = res.getDimensionPixelSize(R.dimen.card_height);
        contentCard.setMainImageDimensions(width, height);

        // Load Image using Glide
        if (imageUrl != null) {
            Glide.with(contentCard.getContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.default_background) // Optional: Add a placeholder drawable
                    .error(R.drawable.default_background) // Optional: Add an error drawable
                    .into(contentCard.getMainImageView());
        } else {
            // Optional: Set a default image if imageUrl is null
             Glide.with(contentCard.getContext())
                    .load(R.drawable.default_background)
                    .into(contentCard.getMainImageView());
        }
    }

    @Override
    public void onUnbindViewHolder(ViewHolder viewHolder) {
        Log.d("movie presenter", "onUnbindViewHolder");

    }
}
