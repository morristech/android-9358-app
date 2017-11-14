package com.xmd.cashier.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xmd.cashier.R;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.dal.bean.InnerSearchInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zr on 17-11-6.
 */

public class InnerSearchAdapter extends BaseAdapter {
    private Context mContext;
    private List<InnerSearchInfo> mData = new ArrayList<>();
    private LayoutInflater mInflater;

    public InnerSearchAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        if (mData != null && !mData.isEmpty()) {
            return mData.size();
        }
        return 0;
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
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_inner_search, parent, false); //加载布局
            holder = new ViewHolder();

            holder.mTextName = (TextView) convertView.findViewById(R.id.tv_search_name);
            holder.mTextType = (TextView) convertView.findViewById(R.id.tv_search_type);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        InnerSearchInfo info = mData.get(position);
        switch (info.type) {
            case AppConstants.INNER_SEARCH_TYPE_ROOM:
                holder.mTextType.setText("[房间]");
                break;
            case AppConstants.INNER_SEARCH_TYPE_ORDER:
                holder.mTextType.setText("[手牌]");
                break;
            case AppConstants.INNER_SEARCH_TYPE_TECH:
                holder.mTextType.setText("[技师]");
                break;
            case AppConstants.INNER_SEARCH_TYPE_FLOOR_STAFF:
                holder.mTextType.setText("[楼面]");
                break;
            default:
                holder.mTextType.setText("[其他]");
                break;
        }
        holder.mTextName.setText(info.name);
        return convertView;
    }

    public void setData(List<InnerSearchInfo> list) {
        if (list != null) {
            mData.addAll(list);
            notifyDataSetChanged();
        }
    }

    private class ViewHolder {
        public TextView mTextName;
        public TextView mTextType;
    }
}
