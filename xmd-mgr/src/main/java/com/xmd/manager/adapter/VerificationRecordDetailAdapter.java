package com.xmd.manager.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xmd.manager.R;
import com.xmd.manager.beans.VerificationRecordCouponDetailBean;
import com.xmd.manager.common.ResourceUtils;
import com.xmd.manager.common.Utils;

import java.util.List;

/**
 * Created by Lhj on 2017/1/16.
 */

public class VerificationRecordDetailAdapter extends BaseAdapter {

    private Context mContext;
    private List<VerificationRecordCouponDetailBean> mData;

    public VerificationRecordDetailAdapter(Context context, List<VerificationRecordCouponDetailBean> data) {
        this.mContext = context;
        this.mData = data;
        this.notifyDataSetChanged();
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
        final View view = View.inflate(mContext, R.layout.layout_verifivation_detail_item, null);
        TextView title = (TextView) view.findViewById(R.id.verification_detail_title);

        title.setText(mData.get(position).title + "：");
        TextView content = (TextView) view.findViewById(R.id.user_verification_detail_content);
        if (Utils.isNotEmpty(mData.get(position).text)) {
            content.setText(mData.get(position).text);
        } else {
            content.setText(ResourceUtils.getString(R.string.no_data));
        }

        return view;
    }
}
