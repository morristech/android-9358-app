package com.xmd.cashier.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xmd.cashier.R;
import com.xmd.cashier.dal.bean.ConfigEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zr on 17-7-31.
 */

public class ConfigurationAdapter extends RecyclerView.Adapter<ConfigurationAdapter.ViewHolder> {
    private Context mContext;
    private List<ConfigEntry> mData = new ArrayList<>();

    public ConfigurationAdapter(Context context) {
        mContext = context;
    }

    public void setData(List<ConfigEntry> list) {
        mData.addAll(list);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_configuration, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ConfigEntry entry = mData.get(position);
        holder.mKey.setText(entry.key);
        holder.mValue.setText(entry.value);
    }

    @Override
    public int getItemCount() {
        if (mData != null) {
            return mData.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mKey;
        public TextView mValue;

        public ViewHolder(View itemView) {
            super(itemView);
            mKey = (TextView) itemView.findViewById(R.id.tv_key);
            mValue = (TextView) itemView.findViewById(R.id.tv_value);
        }
    }
}
