package com.xmd.technician.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xmd.technician.R;
import com.xmd.technician.widget.chatview.EaseChatInputMenu;

import java.util.List;

/**
 * Created by Lhj on 17-5-9.
 */

public class ChatGridViewAdapter extends BaseAdapter {

    private List<EaseChatInputMenu.MoreMenuItem> mDataList;
    private Context mContext;

    public ChatGridViewAdapter(Context contexts, List<EaseChatInputMenu.MoreMenuItem> dataList) {
        super();
        mContext = contexts;
        this.mDataList = dataList;
    }

    public void setData(List<EaseChatInputMenu.MoreMenuItem> dataList) {
        this.mDataList = dataList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (null != mDataList) {
            return mDataList.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_chat_grid_view_item, null);
            viewHolder = new ViewHolder();
            viewHolder.mImageView = (ImageView) convertView.findViewById(R.id.image_view);
            viewHolder.mTextView = (TextView) convertView.findViewById(R.id.text_view);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.mImageView.setImageResource(mDataList.get(position).iconId);
        viewHolder.mTextView.setText(mDataList.get(position).name);
        viewHolder.mImageView.setOnClickListener(mDataList.get(position).listener);
        return convertView;
    }

    class ViewHolder {
        public ImageView mImageView;
        public TextView mTextView;
    }
}
