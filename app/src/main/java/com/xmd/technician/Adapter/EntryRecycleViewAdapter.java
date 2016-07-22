package com.xmd.technician.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xmd.technician.R;
import com.xmd.technician.bean.Entry;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/7/22.
 */
public class EntryRecycleViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_ITEM = 0;

    private List<Entry> mData;

    public EntryRecycleViewAdapter(List<Entry> data) {
        mData = data;
    }

    public void setData(List<Entry> data) {
        mData = data;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return TYPE_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.entry_list_item, parent, false);
        return new EntryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        EntryViewHolder itemHolder = (EntryViewHolder) holder;
        Entry Entry = mData.get(position);
        itemHolder.entryKey.setText(Entry.key);
        itemHolder.entryValue.setText(Entry.value);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    static class EntryViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.entry_key)
        TextView entryKey;
        @Bind(R.id.entry_value) TextView entryValue;

        public EntryViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
