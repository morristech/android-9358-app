package com.xmd.cashier.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xmd.cashier.R;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.dal.bean.PackagePlanItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zr on 17-7-15.
 * 会员充值套餐
 */

public class MemberPlanAdapter extends RecyclerView.Adapter<MemberPlanAdapter.ViewHolder> implements View.OnClickListener {
    private Context mContext;
    private List<PackagePlanItem> mData = new ArrayList<>();
    private CallBack mCallBack;
    private int selectedPosition;

    public MemberPlanAdapter(Context context) {
        mContext = context;
        selectedPosition = -1;
    }

    public void setData(List<PackagePlanItem> list) {
        mData.addAll(list);
        notifyDataSetChanged();
    }

    public void clearData() {
        mData.clear();
    }

    public void setSelectedPosition(int position) {
        int temp = selectedPosition;
        selectedPosition = position;
        notifyItemChanged(selectedPosition);
        notifyItemChanged(temp);
    }

    public void setCallBack(CallBack callback) {
        mCallBack = callback;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_member_plan, parent, false);
        view.setOnClickListener(this);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final PackagePlanItem packagePlan = mData.get(position);
        if (selectedPosition == position) {
            holder.mPlanLayout.setBackgroundResource(R.drawable.bg_check_negative);
            holder.mSelectImg.setVisibility(View.VISIBLE);
            holder.mRechargeDesc.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
        } else {
            holder.mPlanLayout.setBackgroundResource(R.drawable.bg_check_line);
            holder.mSelectImg.setVisibility(View.GONE);
            holder.mRechargeDesc.setTextColor(mContext.getResources().getColor(R.color.colorText4));
        }
        holder.mPlanName.setText("套餐" + packagePlan.name);
        holder.mPlanAmount.setText(Utils.moneyToString(packagePlan.amount));
        if (packagePlan.packageItems != null && packagePlan.packageItems.size() > 0) {
            holder.mSendDesc.setVisibility(View.VISIBLE);
            PlanItemAdapter adapter = new PlanItemAdapter();
            adapter.setCallBack(new PlanItemAdapter.CallBack() {
                @Override
                public void onPlanItemClick() {
                    setSelectedPosition(position);
                    mCallBack.onItemClick(mData.get(position), position);
                }
            });
            adapter.setData(packagePlan.packageItems);
            holder.mSendList.setLayoutManager(new LinearLayoutManager(mContext));
            holder.mSendList.setAdapter(adapter);
            holder.mSendList.setVisibility(View.VISIBLE);
        } else {
            holder.mSendDesc.setVisibility(View.INVISIBLE);
            holder.mSendList.setVisibility(View.GONE);
        }
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        if (!mData.isEmpty()) {
            return mData.size();
        }
        return 0;
    }

    @Override
    public void onClick(View v) {
        int position = (int) v.getTag();
        setSelectedPosition(position);
        mCallBack.onItemClick(mData.get(position), position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout mPlanLayout;
        public TextView mPlanName;
        public TextView mRechargeDesc;
        public TextView mPlanAmount;
        public TextView mSendDesc;
        public RecyclerView mSendList;
        public ImageView mSelectImg;

        public ViewHolder(View itemView) {
            super(itemView);
            mPlanLayout = (LinearLayout) itemView.findViewById(R.id.layout_item_plan);
            mPlanName = (TextView) itemView.findViewById(R.id.tv_recharge_name);
            mRechargeDesc = (TextView) itemView.findViewById(R.id.tv_recharge_desc);
            mPlanAmount = (TextView) itemView.findViewById(R.id.tv_recharge_amount);
            mSendDesc = (TextView) itemView.findViewById(R.id.tv_send_desc);
            mSendList = (RecyclerView) itemView.findViewById(R.id.ry_send_list);
            mSelectImg = (ImageView) itemView.findViewById(R.id.img_recharge_select);
        }
    }

    public interface CallBack {
        void onItemClick(PackagePlanItem item, int position);
    }
}
