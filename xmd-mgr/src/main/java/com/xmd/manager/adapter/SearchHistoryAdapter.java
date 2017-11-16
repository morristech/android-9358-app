package com.xmd.manager.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xmd.manager.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Lhj on 17-11-1.
 */

public class SearchHistoryAdapter extends RecyclerView.Adapter {
    private List<String> mData;
    private ItemClickedListener mListener;

    public SearchHistoryAdapter(List<String> data) {
        this.mData = data;
    }

    public void setOnItemClickedListener(ItemClickedListener listener) {
        this.mListener = listener;
    }

    public interface ItemClickedListener {
        void onViewClicked(String data);
    }

    public void setData(List<String> data) {
        this.mData = data;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View historyView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_search_history_item, parent, false);
        return new HistoryViewHolder(historyView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HistoryViewHolder) {
            HistoryViewHolder viewHolder = (HistoryViewHolder) holder;
            viewHolder.tvHistory.setText(mData.get(position));
            viewHolder.itemView.setOnClickListener(v -> {
                if (mListener != null) mListener.onViewClicked(mData.get(position));
            });
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_history)
        TextView tvHistory;

        public HistoryViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
