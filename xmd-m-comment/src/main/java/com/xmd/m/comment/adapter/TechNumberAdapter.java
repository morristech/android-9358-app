package com.xmd.m.comment.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xmd.m.R;
import com.xmd.m.comment.bean.TechBean;

import java.util.List;

/**
 * Created by Lhj on 17-7-3.
 */

public class TechNumberAdapter extends RecyclerView.Adapter {
    private List<TechBean> mData;
    private TechNoClickedListener mTechNoClickedListener;


    public TechNumberAdapter(List<TechBean> data, TechNoClickedListener listener) {
        this.mData = data;
        this.mTechNoClickedListener = listener;
    }

    public void setData(List<TechBean> data) {
        this.mData = data;
        notifyDataSetChanged();
    }

    public interface TechNoClickedListener {

        void techNoItem(TechBean bean, int position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_tech_number_item, parent, false);
        return new TechNumberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (!(mData.get(position) instanceof TechBean)) {
            return;
        }
        final TechBean techNumber = mData.get(position);
        TechNumberViewHolder viewHolder = (TechNumberViewHolder) holder;
        viewHolder.techNo.setText(techNumber.techNo);
        if (techNumber.isSelected) {
            viewHolder.techNo.setSelected(true);
        } else {
            viewHolder.techNo.setSelected(false);
        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTechNoClickedListener.techNoItem(techNumber, position);
            }
        });


    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    static class TechNumberViewHolder extends RecyclerView.ViewHolder {
        private TextView techNo;

        public TechNumberViewHolder(View itemView) {
            super(itemView);
            techNo = (TextView) itemView.findViewById(R.id.tech_no);
        }
    }
}
