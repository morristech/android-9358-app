package com.m.pk.adapter;

import android.databinding.ObservableInt;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

/**
 * Created by Lhj on 18-1-8.
 */

public class TechCommonRankingDetailAdapter extends RecyclerView.Adapter<BindingViewHolder> {

    public ObservableInt position = new ObservableInt();

    @Override
    public BindingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(BindingViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
