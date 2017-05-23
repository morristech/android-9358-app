package com.xmd.manager.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xmd.manager.R;
import com.xmd.manager.beans.FavourableActivityBean;

import java.util.List;

/**
 * Created by Administrator on 2016/9/26.
 */
public class CouponListAdapter extends BaseAdapter {
    private List<FavourableActivityBean> mList;
    private Context mContext;

    public CouponListAdapter(Context context, List<FavourableActivityBean> data) {
        this.mContext = context;
        this.mList = data;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View view = View.inflate(mContext, R.layout.adapter_coupon_item, null);
        TextView tv_number = (TextView) view.findViewById(R.id.tv_number);
        tv_number.setText(mList.get(position).name);
        return view;
    }
}
