package com.xmd.manager.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xmd.manager.R;
import com.xmd.manager.beans.GroupBean;
import com.xmd.manager.beans.Technician;
import com.xmd.manager.common.Utils;
import com.xmd.manager.widget.FlowLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by sdcm on 17-5-17.
 */
public class CustomerTypeExpandableAdapter<T> extends BaseExpandableListAdapter {
    public interface Callback<T> {

        void onItemClicked(T bean);

        void onItemClicked(int groupPosition, int childPosition);

        void onCreateGroupButtonClicked();
    }

    private static final int GROUP_TYPE_CUSTOMER_ITEM = 1;
    private static final int GROUP_TYPE_COUPON_ITEM = 2;

    private List<String> groupArray;
    private List<List<T>> childArray;
    private Map<Integer, String> selectIdMap;

    private Callback mCallback;
    private int mViewType = GROUP_TYPE_CUSTOMER_ITEM;

    public CustomerTypeExpandableAdapter(List<String> groupArray, @NonNull Callback callback) {
        mCallback = callback;

        if (groupArray == null || groupArray.isEmpty()) {
            return;
        }

        this.groupArray = groupArray;
        this.childArray = new ArrayList<>();
        selectIdMap = new HashMap<>();
        for (int i = 0; i < groupArray.size(); i++) {
            this.childArray.add(new ArrayList<T>());
            this.selectIdMap.put(i, "");
        }
    }

    public CustomerTypeExpandableAdapter(List<String> groupArray, List<List<T>> childArray, @NonNull Callback callback) {
        mCallback = callback;
        if (groupArray == null || groupArray.isEmpty()) {
            return;
        }

        this.groupArray = groupArray;
        this.childArray = childArray;
        selectIdMap = new HashMap<>();
        for (int i = 0; i < groupArray.size(); i++) {
            this.selectIdMap.put(i, "");
        }
    }

    public void setChildData(int groupPosition, List<T> child) {
        if (child != null) {
            childArray.get(groupPosition).clear();
            childArray.get(groupPosition).addAll(child);
            notifyDataSetChanged();
        }
    }

    public void clearSelectUnlessPosition(int groupPosition) {
        for (int j = 0; j < selectIdMap.size(); j++) {
            if (j != groupPosition) {
                selectIdMap.put(j, "");
            }
        }
    }

    public void setSelect(int groupPosition, int childPosition) {
        Object bean = childArray.get(groupPosition).get(childPosition);

        clearSelectUnlessPosition(groupPosition);

        String currentId = "";
        if (bean instanceof GroupBean) {
            currentId = ((GroupBean) bean).id;
        } else if (bean instanceof String) {
            currentId = (String) bean;
        } else if (bean instanceof Technician) {
            currentId = ((Technician) bean).techId;
        }
        String ids = selectIdMap.get(groupPosition);
        if (selectIdMap.get(groupPosition).contains(currentId)) {
            ids.replace(String.format("%s,", currentId), "");
        } else {
            ids += String.format("%s,", currentId);
        }
        selectIdMap.put(groupPosition, ids);
    }

    @Override
    public int getGroupCount() {
        return groupArray == null ? 0 : groupArray.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (childArray != null && childArray.size() > groupPosition) {
            if (GROUP_TYPE_CUSTOMER_ITEM == mViewType) {
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
        switch (mViewType) {
            case GROUP_TYPE_CUSTOMER_ITEM:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_type_expandlist_group, null);
                holder = new CustomerGroupHolder(view);
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
        switch (mViewType) {
            case GROUP_TYPE_CUSTOMER_ITEM:
                CustomerGroupHolder holder = (CustomerGroupHolder) view.getTag();
                //判断是否已经打开列表
                if (isExpanded) {
                    holder.arrowImage.setBackgroundResource(R.drawable.icon_up);
                } else {
                    holder.arrowImage.setBackgroundResource(R.drawable.icon_down);
                }

                holder.customerTypeGroup.setText(groupArray.get(groupPosition));
                break;
            default:
                break;
        }


    }

    private View onCreateChildViewHolder(View convertView, ViewGroup parent) {
        View view = convertView;
        Object holder;
        switch (mViewType) {
            case GROUP_TYPE_CUSTOMER_ITEM:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_type_expandlist_child, null);
                holder = new CustomerGroupChildHolder(view, parent.getContext());
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
        switch (mViewType) {
            case GROUP_TYPE_CUSTOMER_ITEM:
                CustomerGroupChildHolder holder = (CustomerGroupChildHolder) view.getTag();
                holder.initALlGroupView(childArray.get(groupPosition), groupPosition);
                break;
            default:
                break;
        }

    }

    class CustomerGroupHolder {
        @Bind(R.id.customer_type_group)
        TextView customerTypeGroup;

        @Bind(R.id.image_arrow)
        ImageView arrowImage;

        public CustomerGroupHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    class CustomerGroupChildHolder {
        @Bind(R.id.customer_group_child_list)
        FlowLayout customerTypeChildListView;

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
                String currentId = "";
                if (bean instanceof GroupBean) {
                    textView.setText(((GroupBean) bean).name);
                    textView.setSelected(((GroupBean) bean).isChecked);
                    currentId = ((GroupBean) bean).id;
                } else if (bean instanceof String) {
                    textView.setText((String) bean);
                    currentId = (String) bean;
                } else if (bean instanceof Technician) {
                    if (Utils.isEmpty(((Technician) bean).techNo)) {
                        textView.setText(String.format("%s", ((Technician) bean).techName));
                    } else {
                        textView.setText(String.format("%s[%s]", ((Technician) bean).techName, ((Technician) bean).techNo));
                    }
                    currentId = ((Technician) bean).techId;
                }

                textView.setSelected(selectIdMap.get(groupPosition).contains(currentId));
                final int finalI = i;
                v.setOnClickListener(v1 -> {
                    textView.setSelected(!textView.isSelected());
                    mCallback.onItemClicked(groupPosition, finalI);
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
        }

        /*private void initCustomizeGroupListView(int groupPosition){
            customerTypeChildListView.removeAllViews();
            ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams(ViewGroup.MarginLayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.leftMargin = Utils.dip2px(context, 12);
            lp.topMargin = Utils.dip2px(context, 10);
            for (int i = 0; i < childArray.get(groupPosition).size(); i++) {
                View v = LayoutInflater.from(context).inflate(R.layout.customer_group_textview_item, null);
                TextView textView = (TextView) v.findViewById(R.id.customer_group_name);

                GroupBean bean = (GroupBean) childArray.get(groupPosition).get(i);
                textView.setText(((GroupBean) bean).name);
                textView.setSelected(selectIds.get(groupPosition).contains(bean.id));

                v.setOnClickListener(v1 -> {
                    textView.setSelected(!textView.isSelected());
                    for(int j = 0; j < selectIds.size(); j++){
                        if(j != groupPosition){
                            selectIds.add(j,"");
                        }
                    }

                    String ids = selectIds.get(groupPosition);
                    if(selectIds.get(groupPosition).contains(bean.id)){
                        ids.replace(String.format("%s,",bean.id),"");
                    }else {
                        ids += String.format("%s,", bean.id);
                    }
                    selectIds.add(groupPosition, ids);
                });
                customerTypeChildListView.addView(v,lp);
            }

            View v = LayoutInflater.from(context).inflate(R.layout.create_new_group_item_view, null);
            TextView textView = (TextView) v.findViewById(R.id.customer_group_name);
            v.setOnClickListener(v1 -> {
                textView.setSelected(!textView.isSelected());
                mCallback.onCreateGroupButtonClicked();
            });
            customerTypeChildListView.addView(v,lp);
        }

        private void initCustomerTechListView(int groupPosition){
            customerTypeChildListView.removeAllViews();
            ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams(ViewGroup.MarginLayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.leftMargin = Utils.dip2px(context, 12);
            lp.topMargin = Utils.dip2px(context, 10);
            for (int i = 0; i < childArray.get(groupPosition).size(); i++) {
                View v = LayoutInflater.from(context).inflate(R.layout.customer_group_textview_item, null);
                TextView textView = (TextView) v.findViewById(R.id.customer_group_name);

                Technician bean = (Technician) childArray.get(groupPosition).get(i);
                if(Utils.isEmpty(bean.techNo)){
                    textView.setText(String.format("%s",bean.techName));
                }else {
                    textView.setText(String.format("%s[%s]",bean.techName, bean.techNo));
                }
                textView.setSelected(selectIds.get(groupPosition).contains(bean.techId));
                v.setOnClickListener(v1 -> {
                    textView.setSelected(!textView.isSelected());
                    for(int j = 0; j < selectIds.size(); j++){
                        if(j != groupPosition){
                            selectIds.add(j,"");
                        }
                    }

                    String ids = selectIds.get(groupPosition);
                    if(selectIds.get(groupPosition).contains(bean.techId)){
                        ids.replace(String.format("%s,",bean.techId),"");
                    }else {
                        ids += String.format("%s,", bean.techId);
                    }
                    selectIds.add(groupPosition, ids);
                });
                customerTypeChildListView.addView(v,lp);
            }
        }

        private void initCustomerBehaviorListView(int groupPosition){
            customerTypeChildListView.removeAllViews();
            ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams(ViewGroup.MarginLayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.leftMargin = Utils.dip2px(context, 12);
            lp.topMargin = Utils.dip2px(context, 10);
            for (int i = 0; i < childArray.get(groupPosition).size(); i++) {
                View v = LayoutInflater.from(context).inflate(R.layout.customer_group_textview_item, null);
                TextView textView = (TextView) v.findViewById(R.id.customer_group_name);

                String bean = (String) childArray.get(groupPosition).get(i);
                textView.setText((String) bean);

                v.setOnClickListener(v1 -> {
                    textView.setSelected(!textView.isSelected());
                    for(int j = 0; j < selectIds.size(); j++){
                        if(j != groupPosition){
                            selectIds.add(j,"");
                        }
                    }

                    String ids = selectIds.get(groupPosition);
                    if(selectIds.get(groupPosition).contains(bean)){
                        ids.replace(String.format("%s,",bean),"");
                    }else {
                        ids += String.format("%s,", bean);
                    }
                    selectIds.add(groupPosition, ids);
                });
                customerTypeChildListView.addView(v,lp);
            }
        }*/
    }
}
