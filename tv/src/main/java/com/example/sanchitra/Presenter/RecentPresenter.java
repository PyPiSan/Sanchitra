package com.example.sanchitra.Presenter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.HeaderItem;
import androidx.leanback.widget.ListRow;
import androidx.leanback.widget.Presenter;

import com.bumptech.glide.Glide;
import com.example.sanchitra.Model.AnimeModel;
import com.example.sanchitra.R;

public class RecentPresenter extends Presenter {

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_view, parent, false);
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.width = getWidthInPercent(parent.getContext(), 12);
        params.height = getHeightInPercent(parent.getContext(), 32);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Object item) {
        AnimeModel animeModel = (AnimeModel) item;
        View rootView = viewHolder.view;

        ImageView animeView = rootView.findViewById(R.id.main_image);
        TextView animeTitle = rootView.findViewById(R.id.title);
        CardView animeCard = rootView.findViewById(R.id.drama_card);
        String animeImage = animeModel.getImage();
        String  animeName = animeModel.getTitle();
        Glide.with(viewHolder.view.getContext())
                .load(animeImage)
                .into(animeView);
        animeTitle.setText(animeName);

//        animeCard.setOnClickListener(view -> {
//            Toast.makeText(view.getContext(), "Card is " + animeName, Toast.LENGTH_SHORT).show();
//        });


    }

    @Override
    public void onUnbindViewHolder(ViewHolder viewHolder) {

    }

    public int getWidthInPercent(Context context, int percent){
       int widthPixels = context.getResources().getDisplayMetrics().widthPixels;
        return (widthPixels*percent)/100;
    }

    public int getHeightInPercent(Context context, int percent){
        int heightPixels = context.getResources().getDisplayMetrics().heightPixels;
        return (heightPixels*percent)/100;
    }
}
