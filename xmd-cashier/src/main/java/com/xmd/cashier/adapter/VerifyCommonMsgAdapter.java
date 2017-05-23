package com.xmd.cashier.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xmd.cashier.R;
import com.xmd.cashier.dal.bean.CommonVerifyInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zr on 16-12-13.
 */

public class VerifyCommonMsgAdapter extends BaseAdapter {
    private Context mContext;
    private List<CommonVerifyInfo.CommonVerifyMsgItem> mData = new ArrayList<>();

    public VerifyCommonMsgAdapter(Context context) {
        mContext = context;
    }

    public void setData(List<CommonVerifyInfo.CommonVerifyMsgItem> data) {
        mData = data;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View view = View.inflate(mContext, R.layout.item_common_verify_msg, null);
        TextView tvTitle = (TextView) view.findViewById(R.id.item_title);
        tvTitle.setText(mData.get(position).title);
        TextView tvText = (TextView) view.findViewById(R.id.item_text);
        tvText.setText(mData.get(position).text);
        return view;
    }
}
