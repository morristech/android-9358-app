package com.xmd.manager.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.xmd.manager.R;
import com.xmd.manager.beans.GroupMemberBean;
import com.xmd.manager.widget.CircularBeadImageView;

import java.util.ArrayList;
import java.util.List;


import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Lhj on 2016/12/20.
 */

public class SelectedCustomerAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private List<GroupMemberBean> mCustomers;

    public SelectedCustomerAdapter(Context context) {
        mCustomers = new ArrayList<>();
        mContext = context;
    }

    public void setCustomersData(List<GroupMemberBean> memberBeen) {
        mCustomers.clear();
        mCustomers.addAll(memberBeen);
        notifyDataSetChanged();

    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_selected_customer_item, parent, false);
        return new CustomerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        CustomerViewHolder customerViewHolder = (CustomerViewHolder) holder;
        GroupMemberBean customer = mCustomers.get(position);
        Glide.with(mContext).load(customer.avatarUrl).error(R.drawable.default_rectangular_avatar).into(customerViewHolder.customerHead);
    }

    @Override
    public int getItemCount() {
        return mCustomers.size();
    }

    static class CustomerViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.customer_head)
        CircularBeadImageView customerHead;

        public CustomerViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
