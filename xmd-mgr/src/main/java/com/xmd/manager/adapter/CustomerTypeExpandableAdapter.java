package com.xmd.manager.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xmd.manager.Constant;
import com.xmd.manager.R;
import com.xmd.manager.beans.FavourableActivityBean;
import com.xmd.manager.beans.FavourableActivityGroup;
import com.xmd.manager.beans.GroupBean;
import com.xmd.manager.beans.GroupTagBean;
import com.xmd.manager.beans.GroupTagList;
import com.xmd.manager.beans.Technician;
import com.xmd.manager.common.ResourceUtils;
import com.xmd.manager.common.Utils;
import com.xmd.manager.widget.FlowLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sdcm on 17-5-17.
 */
public class CustomerTypeExpandableAdapter<T> extends BaseExpandableListAdapter {
    public interface Callback {
        void onItemClicked(int groupPosition, int childPosition);

        void onCreateGroupButtonClicked();

        boolean isChecked(int groupPosition, int childPosition);
    }

    public static final int GROUP_TYPE_CUSTOMER_GROUP = 0x01;
    public static final int GROUP_TYPE_ACTIVITY_GROUP = 0x02;

    private List<T> groupArray;
    private List<List<T>> childArray;

    private Callback mCallback;
    private int mGroupType = GROUP_TYPE_CUSTOMER_GROUP;

    public CustomerTypeExpandableAdapter(List<T> groupArray, List<List<T>> childArray, int type, @NonNull Callback callback) {
        this.mCallback = callback;
        this.mGroupType = type;
        this.groupArray = groupArray;
        this.childArray = childArray;
    }

    public void setData(List<T> groupArray, List<List<T>> childArray) {
        this.groupArray = groupArray;
        this.childArray = childArray;
        notifyDataSetChanged();
    }

    @Override
    public int getGroupCount() {
        return groupArray == null ? 0 : groupArray.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (childArray != null && childArray.size() > groupPosition) {
            if (GROUP_TYPE_CUSTOMER_GROUP == mGroupType) {
                return 1;
            }
            return childArray.get(groupPosition).size();
        }
        return 0;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groupArray.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return childArray.get(groupPosition).get(childPosition);
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
        View view = convertView;
        if (view == null) {
            view = onCreateGroupViewHolder(view, parent);
        }
        onBindGroupViewHolder(groupPosition, isExpanded, view);
        return view;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = onCreateChildViewHolder(convertView, parent);
        }
        onBindChildViewHolder(groupPosition, childPosition, view);
        return view;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private View onCreateGroupViewHolder(View convertView, ViewGroup parent) {
        View view = convertView;
        Object holder;
        switch (mGroupType) {
            case GROUP_TYPE_CUSTOMER_GROUP:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_type_expandlist_group, null);
                holder = new CustomerGroupHolder(view);
                break;
            case GROUP_TYPE_ACTIVITY_GROUP:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.coupon_expandlist_group, null);
                holder = new CouponGroupHolder(view);
                break;
            default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_type_expandlist_group, null);
                holder = new CustomerGroupHolder(view);
                break;
        }
        view.setTag(holder);
        return view;
    }

    private void onBindGroupViewHolder(int groupPosition, boolean isExpanded, View view) {
        switch (mGroupType) {
            case GROUP_TYPE_CUSTOMER_GROUP:
                CustomerGroupHolder holder = (CustomerGroupHolder) view.getTag();
                //判断是否已经打开列表
                if (isExpanded) {
                    holder.arrowImage.setBackgroundResource(R.drawable.icon_up);
                } else {
                    holder.arrowImage.setBackgroundResource(R.drawable.icon_down);
                }
                holder.customerTypeGroup.setText(Constant.USE_GROUP_LABELS.get(((GroupTagList) groupArray.get(groupPosition)).category));
                break;
            case GROUP_TYPE_ACTIVITY_GROUP:
                CouponGroupHolder couponHolder = (CouponGroupHolder) view.getTag();
                FavourableActivityGroup bean = (FavourableActivityGroup) groupArray.get(groupPosition);
                if(Constant.MSG_TYPE_COUPON.equals(bean.category)){
                    couponHolder.couponIcon.setBackgroundResource(R.drawable.icon_coupon);
                    couponHolder.couponGroup.setText(ResourceUtils.getString(R.string.club_coupon));
                }else if(Constant.MSG_TYPE_TIME_LIMIT.equals(bean.category)){
                    couponHolder.couponIcon.setBackgroundResource(R.drawable.icon_panicbuying);
                    couponHolder.couponGroup.setText(ResourceUtils.getString(R.string.club_time_limit));
                }else if(Constant.MSG_TYPE_ONE_YUAN.equals(bean.category)){
                    couponHolder.couponIcon.setBackgroundResource(R.drawable.icon_pay);
                    couponHolder.couponGroup.setText(ResourceUtils.getString(R.string.club_one_yuan));
                }else if(Constant.MSG_TYPE_LUCKY_WHEEL.equals(bean.category)){
                    couponHolder.couponIcon.setBackgroundResource(R.drawable.icon_turntable);
                    couponHolder.couponGroup.setText(ResourceUtils.getString(R.string.club_lucky_wheel));
                }else if(Constant.MSG_TYPE_JOURNAL.equals(bean.category)){
                    couponHolder.couponIcon.setBackgroundResource(R.drawable.icon_periodical);
                    couponHolder.couponGroup.setText(ResourceUtils.getString(R.string.club_journal));
                }
                //判断是否已经打开列表
                if (isExpanded) {
                    couponHolder.arrowImage.setBackgroundResource(R.drawable.icon_up);
                } else {
                    couponHolder.arrowImage.setBackgroundResource(R.drawable.icon_down);
                }

                break;
            default:
                break;
        }


    }

    private View onCreateChildViewHolder(View convertView, ViewGroup parent) {
        View view = convertView;
        Object holder;
        switch (mGroupType) {
            case GROUP_TYPE_CUSTOMER_GROUP:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_type_expandlist_child, null);
                holder = new CustomerGroupChildHolder(view, parent.getContext());
                break;
            case GROUP_TYPE_ACTIVITY_GROUP:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.coupon_expandlist_child, null);
                holder = new CouponChildHolder(view);
                break;
            default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_type_expandlist_child, null);
                holder = new CustomerGroupChildHolder(view, parent.getContext());
                break;
        }
        view.setTag(holder);
        return view;
    }

    private void onBindChildViewHolder(int groupPosition, int childPosition, View view) {
        switch (mGroupType) {
            case GROUP_TYPE_CUSTOMER_GROUP:
                CustomerGroupChildHolder holder = (CustomerGroupChildHolder) view.getTag();
                holder.initALlGroupView(childArray.get(groupPosition), groupPosition);
                break;
            case GROUP_TYPE_ACTIVITY_GROUP:
                CouponChildHolder couponHolder = (CouponChildHolder) view.getTag();
                FavourableActivityBean bean = (FavourableActivityBean) childArray.get(groupPosition).get(childPosition);
                couponHolder.couponName.setText(bean.name);
                couponHolder.couponType.setText(bean.actTypeName);
                couponHolder.couponRemark.setText(bean.description);
                if(mCallback.isChecked(groupPosition, childPosition)){
                    couponHolder.checkImage.setBackgroundResource(R.drawable.icon_checkbox_checked);
                }else {
                    couponHolder.checkImage.setBackgroundResource(R.drawable.icon_checkbox);
                }

                view.setOnClickListener(v -> {
                    mCallback.onItemClicked(groupPosition, childPosition);
                    notifyDataSetChanged();
                });
                break;
            default:
                break;
        }

    }

    class CustomerGroupHolder {
        @BindView(R.id.customer_type_group)
        TextView customerTypeGroup;

        @BindView(R.id.image_arrow)
        ImageView arrowImage;

        public CustomerGroupHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    class CustomerGroupChildHolder {
        @BindView(R.id.customer_group_child_list)
        FlowLayout customerTypeChildListView;

        @BindView(R.id.group_tag_detail)
        TextView groupTagDetail;

        private Context context;

        public CustomerGroupChildHolder(View view, Context context) {
            ButterKnife.bind(this, view);
            this.context = context;
        }

        private void initALlGroupView(List<T> allGroups, int groupPosition) {
            customerTypeChildListView.removeAllViews();
            ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams(ViewGroup.MarginLayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.leftMargin = Utils.dip2px(context, 12);
            lp.topMargin = Utils.dip2px(context, 10);
            for (int i = 0; i < allGroups.size(); i++) {
                View v = LayoutInflater.from(context).inflate(R.layout.customer_group_textview_item, null);
                TextView textView = (TextView) v.findViewById(R.id.customer_group_name);

                T bean = allGroups.get(i);
                if (bean instanceof GroupBean) {
                    textView.setText(((GroupBean) bean).name);
                } else if (bean instanceof GroupTagBean) {
                    textView.setText(((GroupTagBean) bean).tagName);
                } else if (bean instanceof Technician) {
                    if (Utils.isEmpty(((Technician) bean).techNo)) {
                        textView.setText(String.format("%s", ((Technician) bean).techName));
                    } else {
                        textView.setText(String.format("%s[%s]", ((Technician) bean).techName, ((Technician) bean).techNo));
                    }
                } else {
                    textView.setText(bean.toString());
                }

                final int finalI = i;
                textView.setSelected(mCallback.isChecked(groupPosition, finalI));
                v.setOnClickListener(v1 -> {
                    mCallback.onItemClicked(groupPosition, finalI);
                    textView.setSelected(mCallback.isChecked(groupPosition, finalI));
                    if(bean instanceof GroupTagBean){
                        groupTagDetail.setText(((GroupTagBean) bean).description);
                    }
                });
                customerTypeChildListView.addView(v, lp);
            }

            if (groupPosition == 2) {
                View v = LayoutInflater.from(context).inflate(R.layout.create_new_group_item_view, null);
                TextView textView = (TextView) v.findViewById(R.id.customer_group_name);
                v.setOnClickListener(v1 -> {
                    textView.setSelected(!textView.isSelected());
                    mCallback.onCreateGroupButtonClicked();
                });
                customerTypeChildListView.addView(v, lp);
            }

            if(groupPosition == 0){
                groupTagDetail.setVisibility(View.VISIBLE);
                if(allGroups.size() == 0){
                    groupTagDetail.setText("会所未开通客户标签管理功能");
                }
            }else {
                groupTagDetail.setVisibility(View.GONE);
            }
        }
    }

    class CouponGroupHolder {
        @BindView(R.id.coupon_group)
        TextView couponGroup;

        @BindView(R.id.image_arrow)
        ImageView arrowImage;

        @BindView(R.id.coupon_icon)
        ImageView couponIcon;

        public CouponGroupHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    class CouponChildHolder {
        @BindView(R.id.coupon_name_text)
        TextView couponName;

        @BindView(R.id.check_image)
        ImageView checkImage;

        @BindView(R.id.coupon_type_text)
        TextView couponType;

        @BindView(R.id.coupon_remark_text)
        TextView couponRemark;

        public CouponChildHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
