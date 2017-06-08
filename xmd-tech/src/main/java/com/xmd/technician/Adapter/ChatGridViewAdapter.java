package com.xmd.technician.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xmd.technician.Constant;
import com.xmd.technician.R;
import com.xmd.technician.bean.CategoryBean;

import java.util.List;

/**
 * Created by Lhj on 17-5-9.
 */

public class ChatGridViewAdapter extends BaseAdapter {

    private List<CategoryBean> mCategoryBeanList;
    private Context mContext;

    public ChatGridViewAdapter(Context contexts, List<CategoryBean> mCategoryBeans) {
        super();
        mContext = contexts;
        this.mCategoryBeanList = mCategoryBeans;
    }

    public void setData(List<CategoryBean> categoryBeans) {
        this.mCategoryBeanList = categoryBeans;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (null != mCategoryBeanList) {
            return mCategoryBeanList.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return mCategoryBeanList.get(position);
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
        if ((mCategoryBeanList.get(position).constKey).equals(Constant.CHAT_MENU_APPOINTMENT_REQUEST)) {
            viewHolder.mImageView.setImageResource(R.drawable.ic_order_request);
        } else if ((mCategoryBeanList.get(position).constKey).equals("05")) {
            viewHolder.mImageView.setImageResource(R.drawable.chat_pay_icon_bg);
        } else if ((mCategoryBeanList.get(position).constKey).equals("06")) {
            viewHolder.mImageView.setImageResource(R.drawable.chat_market_icon_bg);
        } else if ((mCategoryBeanList.get(position).constKey).equals("07")) {
            viewHolder.mImageView.setImageResource(R.drawable.chat_periodical_icon_bg);
        } else if ((mCategoryBeanList.get(position).constKey).equals("08")) {
            viewHolder.mImageView.setImageResource(R.drawable.chat_preference_icon_bg);
        } else if ((mCategoryBeanList.get(position).constKey).equals("09")) {
            viewHolder.mImageView.setImageResource(R.drawable.chat_game_icon_bg);
        } else {
            viewHolder.mImageView.setImageResource(R.drawable.chat_position_icon_bg);
        }
        viewHolder.mTextView.setText(mCategoryBeanList.get(position).constValue);
        return convertView;
    }

    class ViewHolder {
        public ImageView mImageView;
        public TextView mTextView;
    }
}
