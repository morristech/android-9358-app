package com.m.pk.component;

import android.databinding.BindingAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.m.pk.R;

/**
 * Created by Lhj on 18-1-5.
 */

public class DataBindingAdapter {

    @BindingAdapter({"imageUrl"})
    public static void loadImageFromUrl(ImageView view, String url) {
        Glide.with(view.getContext())
                .load(url)
                .placeholder(R.drawable.icon22)
                .into(view);
    }
}
