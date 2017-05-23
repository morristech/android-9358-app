package com.xmd.technician.Adapter;


import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.TextAppearanceSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xmd.technician.R;
import com.xmd.technician.bean.CouponInfo;
import com.xmd.technician.bean.CouponType;
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.common.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by Lhj on 2016/8/4.
 */
public class ChatCouponAdapter extends BaseExpandableListAdapter {

    private Context mContext;
    private List<CouponType> mGroupArray;//组列表
    private List<List<CouponInfo>> mChildArray;//子列表
    private OnChildrenClicked mInterface;

    public ChatCouponAdapter(Context context) {
        this.mContext = context;
        mGroupArray = new ArrayList<>();
        mChildArray = new ArrayList<>();
    }

    public interface OnChildrenClicked {
        void onChildrenClickedListener(CouponInfo bean, int groupPosition, int childPosition, boolean isSelected);
    }

    public void setChildrenClickedInterface(OnChildrenClicked clickedInterface) {
        this.mInterface = clickedInterface;
    }

    public void setData(List<CouponType> groupArray, List<List<CouponInfo>> childArray) {
        this.mGroupArray = groupArray;
        this.mChildArray = childArray;
        notifyDataSetChanged();
    }

    public void refreshChildData(List<List<CouponInfo>> childArray) {
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
        CouponInfo couponInfo = mChildArray.get(groupPosition).get(childPosition);
        if (convertView == null) {
            if (groupPosition == 0) {
                convertView = View.inflate(mContext, R.layout.chat_coupon_cash_item, null);
            } else {
                if (couponInfo.useTypeName.equals(ResourceUtils.getString(R.string.cash_coupon))) {
                    convertView = View.inflate(mContext, R.layout.chat_coupon_delivery_item, null);
                } else {
                    convertView = View.inflate(mContext, R.layout.chat_coupon_favorable_item, null);
                }
            }
            chatCouponViewHolder = new ViewChildViewHolder(convertView);
            convertView.setTag(chatCouponViewHolder);
        } else {
            chatCouponViewHolder = (ViewChildViewHolder) convertView.getTag();
        }

        if (couponInfo.selectedStatus == 0) {
            chatCouponViewHolder.mTextCheck.setSelected(false);
            chatCouponViewHolder.llView.setOnClickListener(v -> mInterface.onChildrenClickedListener(couponInfo, groupPosition, childPosition, true));
        } else {
            chatCouponViewHolder.mTextCheck.setSelected(true);
            chatCouponViewHolder.llView.setOnClickListener(v -> mInterface.onChildrenClickedListener(couponInfo, groupPosition, childPosition, false));
        }
        chatCouponViewHolder.mTvCouponTitle.setText(couponInfo.actTitle);
        if (couponInfo.useTypeName.equals("点钟券")) {
            chatCouponViewHolder.mTvCouponTitle.setText("点钟券");
            chatCouponViewHolder.mCouponType.setVisibility(View.GONE);
        } else {
            chatCouponViewHolder.mTvCouponTitle.setText(Utils.StrSubstring(6, couponInfo.actTitle, true));
            chatCouponViewHolder.mCouponType.setVisibility(View.VISIBLE);
        }
        chatCouponViewHolder.mTvConsumeMoneyDescription.setText(couponInfo.consumeMoneyDescription);
        chatCouponViewHolder.mCouponPeriod.setText("有效时间：" + Utils.StrSubstring(18, couponInfo.couponPeriod, true));
        if (couponInfo.commission > 0) {
            chatCouponViewHolder.mTvCouponReward.setVisibility(View.VISIBLE);
            String money = Utils.getFloat2Str(String.valueOf(couponInfo.commission));
            String text = String.format(ResourceUtils.getString(R.string.coupon_fragment_coupon_reward), money);
            SpannableString spannableString = new SpannableString(text);
            spannableString.setSpan(new TextAppearanceSpan(mContext, R.style.text_bold), 3, text.length() - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            chatCouponViewHolder.mTvCouponReward.setText(spannableString);
        } else {
            chatCouponViewHolder.mTvCouponReward.setVisibility(View.GONE);
        }
        if (Utils.isNotEmpty(couponInfo.consumeMoney)) {
            chatCouponViewHolder.mCouponAmount.setText(String.valueOf(couponInfo.actValue));
        }

        return convertView;
    }


    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    static class ViewParentViewHolder {
        ImageView imageView;
        TextView textView;
    }

    static class ViewChildViewHolder {
        @Bind(R.id.tv_consume_money_description)
        TextView mTvConsumeMoneyDescription;
        @Bind(R.id.tv_coupon_title)
        TextView mTvCouponTitle;
        @Bind(R.id.tv_coupon_reward)
        TextView mTvCouponReward;
        @Bind(R.id.tv_coupon_period)
        TextView mCouponPeriod;
        @Bind(R.id.coupon_amount)
        TextView mCouponAmount;
        @Bind(R.id.coupon_type)
        TextView mCouponType;
        @Bind(R.id.coupon_select)
        TextView mTextCheck;
        @Bind(R.id.ll_view)
        LinearLayout llView;

        ViewChildViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
