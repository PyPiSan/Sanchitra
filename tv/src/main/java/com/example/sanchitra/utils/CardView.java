package com.example.sanchitra.utils;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.sanchitra.R;
import com.example.sanchitra.model.ContentModel;

public class CardView extends BindableCardView<ContentModel> {

    ImageView mPoster;

    TextView name;

    public CardView(Context context) {
        super(context);
        mPoster = findViewById(R.id.poster);
//        name = findViewById(R.id.name);
    }

    @Override
    public void bind(ContentModel data) {
        Glide.with(getContext())
                .load(data.getImage())
                .into(mPoster);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.card_content;
    }

    public ImageView getPoster() {
        return mPoster;
    }
}
