package com.xmd.manager.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xmd.manager.R;
import com.xmd.manager.beans.Technician;
import com.xmd.manager.common.ResourceUtils;
import com.xmd.manager.common.Utils;
import com.xmd.manager.widget.SelectorTextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by linms@xiaomodo.com on 16-5-18.
 */
public class TechnicianRecycleViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface Callback {
        void onItemClicked(int position, Technician bean);
    }

    private List<Technician> mData;
    private Callback mCallback;
    protected View mCheckedView;
    protected int mCheckedPosition = 0;

    public TechnicianRecycleViewAdapter(List<Technician> data, Callback callback) {
        mData = data;
        mCallback = callback;
    }

    public void setCheckedPosition(int position) {
        mCheckedPosition = position;
    }

    public int getCheckedPosition() {
        return mCheckedPosition;
    }

    public void setCheckedView(View view) {
        updateViewChecked(mCheckedView, false);
        mCheckedView = view;
        updateViewChecked(mCheckedView, true);
    }

    private void updateViewChecked(View v, boolean isChecked) {
        if (v != null) {
            TextView tv = (TextView) v;
            if (isChecked) {
                tv.setSelected(true);
                mCheckedView = v;
            } else {
                tv.setSelected(false);
            }
        }
    }

    public void setData(List<Technician> data, int checkedPosition) {
        mData = data;
        mCheckedView = null;
        //mCheckedPosition = checkedPosition < 0 ? 0 : checkedPosition;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.technician_list_item, parent, false);
        return new TechnicanListItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof TechnicanListItemViewHolder) {
            Technician technician = mData.get(position);
            TechnicanListItemViewHolder viewHolder = (TechnicanListItemViewHolder) holder;
            if (Utils.isNotEmpty(technician.techNo)) {
                int startIndex = technician.techName.length() + ("\n[").length();
                int endIndex = startIndex + technician.techNo.length();
                Spannable spanString = Utils.changeColor(technician.techName + "\n[" + technician.techNo + "]",
                        ResourceUtils.getColor(R.color.number_color), startIndex, endIndex);
                viewHolder.mTvName.setText(spanString);
            } else {
                viewHolder.mTvName.setText(technician.techName);
            }

            updateViewChecked(viewHolder.itemView, false);

            if (position == mCheckedPosition) {
                setCheckedView(viewHolder.itemView);
            }

            viewHolder.itemView.setOnClickListener(v -> {
                setCheckedView(v);
                mCheckedPosition = position;
                mCallback.onItemClicked(position, technician);
            });
        }

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    static class TechnicanListItemViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_name)
        SelectorTextView mTvName;

        public TechnicanListItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
