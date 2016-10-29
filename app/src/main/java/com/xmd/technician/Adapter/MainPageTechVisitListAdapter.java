package com.xmd.technician.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.xmd.technician.R;
import com.xmd.technician.bean.RecentlyVisitorBean;

import java.util.List;

/**
 * Created by Administrator on 2016/10/26.
 */
public class MainPageTechVisitListAdapter extends BaseAdapter {

    private Context mContext;
    private List<RecentlyVisitorBean> mData;

    public MainPageTechVisitListAdapter(Context context, List<RecentlyVisitorBean> data) {
        this.mContext = context;
        this.mData = data;
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
        convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_main_page_visit_item, null);
        ImageView visitHead = (ImageView) convertView.findViewById(R.id.main_visit_avatar);
        Glide.with(mContext).load(mData.get(position).avatarUrl).into(visitHead);
        return convertView;
    }

}
