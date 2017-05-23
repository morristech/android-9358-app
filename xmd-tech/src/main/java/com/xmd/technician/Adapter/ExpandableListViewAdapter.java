package com.xmd.technician.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xmd.technician.R;
import com.xmd.technician.bean.MarketingBean;
import com.xmd.technician.bean.MarketingChatShareBean;
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.common.Utils;
import com.xmd.technician.widget.RoundImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Lhj on 17-5-15.
 */

public class ExpandableListViewAdapter extends BaseExpandableListAdapter {

    private Context mContext;
    private List<MarketingBean> mGroupArray;//组列表
    private List<List<MarketingChatShareBean>> mChildArray;//子列表
    private OnChildrenClicked mInterface;

    public ExpandableListViewAdapter(Context context) {
        this.mContext = context;
        mGroupArray = new ArrayList<>();
        mChildArray = new ArrayList<>();
    }

    public interface OnChildrenClicked {
        void onChildrenClickedListener(MarketingChatShareBean bean, int groupPosition, int childPosition, boolean isSelected);
    }

    public void setChildrenClickedInterface(OnChildrenClicked clickedInterface) {
        this.mInterface = clickedInterface;
    }

    public void setData(List<MarketingBean> groupArray, List<List<MarketingChatShareBean>> childArray) {
        this.mGroupArray = groupArray;
        this.mChildArray = childArray;
        notifyDataSetChanged();
    }

    public void refreshChildData(List<List<MarketingChatShareBean>> childArray) {
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
        mParentViewHolder.textView.setText(mGroupArray.get(groupPosition).categoryName);
        if (isExpanded) {
            mParentViewHolder.imageView.setImageResource(R.drawable.arrow_down);
        } else {
            mParentViewHolder.imageView.setImageResource(R.drawable.icon_up);
        }
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ViewChildViewHolder mChildViewHolder;
        MarketingChatShareBean bean = mChildArray.get(groupPosition).get(childPosition);
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.layout_marketing_children_item_view, null);
            mChildViewHolder = new ViewChildViewHolder(convertView);
            convertView.setTag(mChildViewHolder);
        } else {
            mChildViewHolder = (ViewChildViewHolder) convertView.getTag();
        }

        if (bean.selectedStatus == 0) {
            mChildViewHolder.marketingSelect.setSelected(false);
            mChildViewHolder.llView.setOnClickListener(v -> mInterface.onChildrenClickedListener(bean, groupPosition, childPosition, true));
        } else {
            mChildViewHolder.marketingSelect.setSelected(true);
            mChildViewHolder.llView.setOnClickListener(v -> mInterface.onChildrenClickedListener(bean, groupPosition, childPosition, false));
        }

        if (Utils.isNotEmpty(bean.itemName)) {//限时抢
            Glide.with(mContext).load(bean.image).into(mChildViewHolder.marketingHead);
            mChildViewHolder.marketingTitle.setText(bean.itemName);
            mChildViewHolder.marketingMoney.setText(String.valueOf(bean.amount));
            mChildViewHolder.unit.setVisibility(View.VISIBLE);
            mChildViewHolder.marketingMoney.setVisibility(View.VISIBLE);
            mChildViewHolder.marketingMoneyMark.setVisibility(View.GONE);
            if (bean.credits > 0) {
                mChildViewHolder.marketingCredit.setVisibility(View.VISIBLE);
                String des = String.format("（或%s积分）", bean.credits);
                mChildViewHolder.marketingCredit.setText(Utils.changeColor(des, ResourceUtils.getColor(R.color.order_item_surplus_time_color), 2, des.length() - 3));
            } else {
                mChildViewHolder.marketingCredit.setVisibility(View.GONE);
            }
            if (bean.limitedUse) {
                mChildViewHolder.marketingTitleMark.setVisibility(View.VISIBLE);
            } else {
                mChildViewHolder.marketingTitleMark.setVisibility(View.GONE);
            }
            mChildViewHolder.marketingMark.setVisibility(View.GONE);
            if (Utils.isNotEmpty(bean.price)) {
                mChildViewHolder.marketingDetail.setVisibility(View.VISIBLE);
                String des = String.format("原价：%s元", String.valueOf(bean.price));
                mChildViewHolder.marketingDetail.setText(Utils.textStrikeThrough(des, 0, des.length()));
            } else {
                mChildViewHolder.marketingDetail.setVisibility(View.GONE);
            }
        } else if (Utils.isNotEmpty(bean.startTime)) {//大转盘
            Glide.with(mContext).load(bean.image).into(mChildViewHolder.marketingHead);
            mChildViewHolder.marketingTitle.setText(bean.actName);
            mChildViewHolder.marketingCredit.setText(String.format("赢取%s", bean.firstPrizeName));
            mChildViewHolder.marketingMark.setVisibility(View.GONE);
            mChildViewHolder.marketingDetail.setVisibility(View.GONE);
            mChildViewHolder.marketingCredit.setVisibility(View.VISIBLE);
            mChildViewHolder.marketingTitleMark.setVisibility(View.GONE);
            mChildViewHolder.marketingMoney.setVisibility(View.GONE);
            mChildViewHolder.unit.setVisibility(View.GONE);
            mChildViewHolder.marketingMoneyMark.setVisibility(View.GONE);

        } else {//谁替我买单
            Glide.with(mContext).load(bean.image).into(mChildViewHolder.marketingHead);
            if (bean.maxPeriod == 0) { //无限连期
                mChildViewHolder.marketingTitle.setText(bean.actName + String.format("(%s/%s)", String.valueOf(bean.currentPeriod), "无限期"));
            } else {
                mChildViewHolder.marketingTitle.setText(bean.actName + String.format("(%s/%s期)", String.valueOf(bean.currentPeriod), String.valueOf(bean.maxPeriod)));
            }
           // String price = String.format("单价：%s", );
            mChildViewHolder.marketingMoney.setText(String.valueOf(bean.unitPrice));
            mChildViewHolder.unit.setVisibility(View.VISIBLE);
            mChildViewHolder.marketingMoney.setVisibility(View.VISIBLE);
            mChildViewHolder.marketingCredit.setVisibility(View.GONE);
            mChildViewHolder.marketingMoneyMark.setVisibility(View.VISIBLE);
            mChildViewHolder.marketingDetail.setVisibility(View.VISIBLE);
            mChildViewHolder.marketingDetail.setText(String.format("已售：%s/%s", String.valueOf(bean.paidCount), String.valueOf(bean.totalPaidCount)));
            mChildViewHolder.marketingTitleMark.setVisibility(View.GONE);
            if (bean.unitPrice == 1) {
                mChildViewHolder.marketingMark.setVisibility(View.VISIBLE);
                mChildViewHolder.marketingMark.setText("一元夺");
            } else {
                mChildViewHolder.marketingMark.setVisibility(View.VISIBLE);
            }

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
        @Bind(R.id.marketing_head)
        RoundImageView marketingHead;
        @Bind(R.id.marketing_mark)
        TextView marketingMark;
        @Bind(R.id.marketing_title)
        TextView marketingTitle;
        @Bind(R.id.marketing_title_mark)
        TextView marketingTitleMark;
        @Bind(R.id.marketing_money)
        TextView marketingMoney;
        @Bind(R.id.unit)
        TextView unit;
        @Bind(R.id.marketing_credit)
        TextView marketingCredit;
        @Bind(R.id.marketing_detail)
        TextView marketingDetail;
        @Bind(R.id.marketing_select)
        TextView marketingSelect;
        @Bind(R.id.ll_view)
        LinearLayout llView;
        @Bind(R.id.marketing_money_mark)
         TextView marketingMoneyMark;

        ViewChildViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
