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
import com.xmd.technician.bean.ClubRoleBean;
import com.xmd.technician.bean.ClubUserListBean;
import com.xmd.technician.common.Utils;
import com.xmd.technician.widget.CircleImageView;

import java.util.ArrayList;
import java.util.List;


import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by Lhj on 17-5-15.
 */

public class ExpandableClubEmployeeListViewAdapter extends BaseExpandableListAdapter {
    private Context mContext;
    private List<ClubRoleBean> mGroupArray;//组列表
    private List<List<ClubUserListBean>> mChildArray;//子列表
    private OnChildrenClicked mInterface;

    public ExpandableClubEmployeeListViewAdapter(Context context) {
        this.mContext = context;
        mGroupArray = new ArrayList<>();
        mChildArray = new ArrayList<>();
    }

    public interface OnChildrenClicked {
        void onChildrenClickedListener(ClubUserListBean bean);
    }

    public void setChildrenClickedInterface(OnChildrenClicked clickedInterface) {
        this.mInterface = clickedInterface;
    }

    public void setData(List<ClubRoleBean> groupArray, List<List<ClubUserListBean>> childArray) {
        this.mGroupArray = groupArray;
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
            convertView = View.inflate(mContext, R.layout.layout_club_employee_parent_item_view, null);
            mParentViewHolder.imageView = (ImageView) convertView.findViewById(R.id.img_index);
            mParentViewHolder.textView = (TextView) convertView.findViewById(R.id.tv_market_title);
            convertView.setTag(mParentViewHolder);
        } else {
            mParentViewHolder = (ViewParentViewHolder) convertView.getTag();
        }
        mParentViewHolder.textView.setText(mGroupArray.get(groupPosition).name);
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
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.layout_club_employee_item, null);
            mChildViewHolder = new ViewChildViewHolder(convertView);
            convertView.setTag(mChildViewHolder);
        } else {
            mChildViewHolder = (ViewChildViewHolder) convertView.getTag();
        }
        Glide.with(mContext).load(mChildArray.get(groupPosition).get(childPosition).avatarUrl).into(mChildViewHolder.employeeAvatar);
        mChildViewHolder.employeeName.setText(Utils.isNotEmpty(mChildArray.get(groupPosition).get(childPosition).name) ? mChildArray.get(groupPosition).get(childPosition).name : "技师");
        if (Utils.isNotEmpty(mChildArray.get(groupPosition).get(childPosition).techNo)) {
            mChildViewHolder.employeeTechNo.setVisibility(View.VISIBLE);
            String techNo = String.format("[ %s ]", mChildArray.get(groupPosition).get(childPosition).techNo);
            mChildViewHolder.employeeTechNo.setText(Utils.changeColor(techNo, "#ff9d09", 1, techNo.length()));
        } else {
            mChildViewHolder.employeeTechNo.setVisibility(View.GONE);
        }
        mChildViewHolder.llEmployeeView.setOnClickListener(v -> mInterface.onChildrenClickedListener(mChildArray.get(groupPosition).get(childPosition)));
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
        @BindView(R.id.employee_avatar)
        CircleImageView employeeAvatar;
        @BindView(R.id.employee_name)
        TextView employeeName;
        @BindView(R.id.employee_tech_no)
        TextView employeeTechNo;
        @BindView(R.id.ll_employee_view)
        LinearLayout llEmployeeView;

        ViewChildViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
