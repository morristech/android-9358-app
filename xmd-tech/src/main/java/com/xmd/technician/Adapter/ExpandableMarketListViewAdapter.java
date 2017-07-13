package com.xmd.technician.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xmd.technician.R;
import com.xmd.technician.bean.OnceCardItemBean;
import com.xmd.technician.bean.OnceCardType;
import com.xmd.technician.widget.RoundImageView;
import com.xmd.technician.widget.StartCustomTextView;

import java.util.ArrayList;
import java.util.List;


import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by Lhj on 17-5-23.
 */

public class ExpandableMarketListViewAdapter extends BaseExpandableListAdapter {


    private Context mContext;
    private List<OnceCardType> mGroupArray;
    private List<List<OnceCardItemBean>> mChildArray;
    private OnChildrenItemClickedInterface mInterface;

    public interface OnChildrenItemClickedInterface {
        void onChildrenClickedListener(OnceCardItemBean bean, int groupPosition, int childPosition, boolean isSelected);
    }

    public ExpandableMarketListViewAdapter(Context context) {
        this.mContext = context;
        mGroupArray = new ArrayList<>();
        mChildArray = new ArrayList<>();
    }

    public void setOnChildrenItemClicked(OnChildrenItemClickedInterface itemClicked) {
        this.mInterface = itemClicked;
    }

    public void setData(List<OnceCardType> groupArray, List<List<OnceCardItemBean>> childArray) {
        this.mGroupArray = groupArray;
        this.mChildArray = childArray;
        notifyDataSetChanged();
    }

    public void refreshChildData(List<List<OnceCardItemBean>> childArray) {
        this.mChildArray = childArray;
        notifyDataSetChanged();
    }

    @Override
    public int getGroupCount() {
        return mGroupArray.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mChildArray.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mGroupArray.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mChildArray.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }


    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ViewParentViewHolder mParentViewHolder;
        if (convertView == null) {
            mParentViewHolder = new ViewParentViewHolder();
            convertView = View.inflate(mContext, R.layout.layout_marketing_parent_item_view, null);
            mParentViewHolder.imageView = (ImageView) convertView.findViewById(R.id.img_index);
            mParentViewHolder.textView = (TextView) convertView.findViewById(R.id.tv_market_title);
            convertView.setTag(mParentViewHolder);
        } else {
            mParentViewHolder = (ViewParentViewHolder) convertView.getTag();
        }
        mParentViewHolder.textView.setText(mGroupArray.get(groupPosition).title);
        if (isExpanded) {
            mParentViewHolder.imageView.setImageResource(R.drawable.arrow_down);
        } else {
            mParentViewHolder.imageView.setImageResource(R.drawable.icon_up);
        }
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ViewChildViewHolder chatCouponViewHolder;
        OnceCardItemBean onceCard = mChildArray.get(groupPosition).get(childPosition);
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.layout_chat_share_once_card_item, null);
            chatCouponViewHolder = new ViewChildViewHolder(convertView);
            convertView.setTag(chatCouponViewHolder);
        } else {
            chatCouponViewHolder = (ViewChildViewHolder) convertView.getTag();
        }
        Glide.with(mContext).load(onceCard.imageUrl).into(chatCouponViewHolder.onceCardHead);
        chatCouponViewHolder.onceCardTitle.setText(onceCard.name);
        chatCouponViewHolder.onceCardCredit.setText(onceCard.comboDescription);
        chatCouponViewHolder.onceCardCredit.setTextColor(Color.parseColor("#666666"));
        chatCouponViewHolder.onceCardMoney.setMText(onceCard.techRoyalty);
        chatCouponViewHolder.onceCardMoney.setTextColor(Color.parseColor("#fb5549"));
        chatCouponViewHolder.onceCardDiscountPrice.setMText(onceCard.discountPrice);
        chatCouponViewHolder.onceCardDiscountPrice.setTextColor(Color.parseColor("#666666"));
        if (onceCard.selectedStatus == 1) {
            chatCouponViewHolder.onceCardSelect.setSelected(false);
            chatCouponViewHolder.llOnceCardShareView.setOnClickListener(v -> mInterface.onChildrenClickedListener(onceCard,groupPosition,childPosition,true));
        } else {
            chatCouponViewHolder.onceCardSelect.setSelected(true);
            chatCouponViewHolder.llOnceCardShareView.setOnClickListener(v ->mInterface.onChildrenClickedListener(onceCard,groupPosition,childPosition,false));
        }


        return convertView;

    }

    static class ViewParentViewHolder {
        ImageView imageView;
        TextView textView;
    }

    static class ViewChildViewHolder {
        
        @BindView(R.id.once_card_head)
        RoundImageView onceCardHead;
        @BindView(R.id.once_card_mark_new)
        TextView onceCardMarkNew;
        @BindView(R.id.once_card_title)
        TextView onceCardTitle;
        @BindView(R.id.once_card_credit)
        TextView onceCardCredit;
        @BindView(R.id.once_card_discount_price)
        StartCustomTextView onceCardDiscountPrice;
        @BindView(R.id.once_card_money)
        StartCustomTextView onceCardMoney;
        @BindView(R.id.once_card_select)
        TextView onceCardSelect;
        @BindView(R.id.ll_once_card_share_view)
        LinearLayout llOnceCardShareView;

        ViewChildViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
