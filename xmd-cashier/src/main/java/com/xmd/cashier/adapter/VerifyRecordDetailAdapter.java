package com.xmd.cashier.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xmd.cashier.R;
import com.xmd.cashier.dal.bean.VerifyRecordDetailInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zr on 17-5-3.
 */

public class VerifyRecordDetailAdapter extends RecyclerView.Adapter<VerifyRecordDetailAdapter.ViewHolder> {
    private Context mContext;
    private List<VerifyRecordDetailInfo> mData = new ArrayList<>();

    public VerifyRecordDetailAdapter(Context context) {
        mContext = context;
    }

    public void setData(List<VerifyRecordDetailInfo> list) {
        mData.addAll(list);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_verify_record_detail, null, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        VerifyRecordDetailInfo info = mData.get(position);
        holder.mTitle.setText(info.title + "：");
        holder.mText.setText(TextUtils.isEmpty(info.text) ? "无" : info.text);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mTitle;
        private TextView mText;

        public ViewHolder(View itemView) {
            super(itemView);
            mTitle = (TextView) itemView.findViewById(R.id.item_detail_title);
            mText = (TextView) itemView.findViewById(R.id.item_detail_text);
        }
    }
}