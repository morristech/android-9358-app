package com.xmd.technician.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xmd.app.utils.ResourceUtils;
import com.xmd.technician.R;
import com.xmd.technician.bean.TechAccountBean;
import com.xmd.technician.common.Utils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Lhj on 2017/03/9.
 */
public class TechAccountListAdapter extends RecyclerView.Adapter<TechAccountListAdapter.TechAccountViewHolder> {
    private Context mContext;
    private List<TechAccountBean> mData;
    private CallBack mCallBack;
    private String mWithdrawal = "";

    public interface CallBack {
        void onWithDrawClicked(TechAccountBean bean);

        void onItemClicked(TechAccountBean bean);
    }

    public TechAccountListAdapter(Context context, List<TechAccountBean> data) {
        this.mContext = context;
        this.mData = data;

    }

    public void setData(List<TechAccountBean> data, String withdrawal) {
        this.mData = data;
        this.mWithdrawal = withdrawal;
        notifyDataSetChanged();
    }

    public void setOnWithDrawClickedListener(CallBack callBack) {
        this.mCallBack = callBack;
    }

    @Override
    public TechAccountViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_tech_account_item, parent, false);
        TechAccountViewHolder holder = new TechAccountViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(TechAccountViewHolder holder, int position) {
        TechAccountBean bean = mData.get(position);
        holder.accountName.setText(bean.name);
        holder.rewardAmount.setText(Utils.getFloat2Str(String.valueOf(bean.amount / 100f)));
        if (mWithdrawal.equals("Y") && bean.status.equals("normal")) {
            holder.accountConsume.setBackgroundResource(R.drawable.account_button_selector_bg);
            holder.accountConsume.setTextColor(ResourceUtils.getColor(R.color.colorBtnSelector));
        } else {
            holder.accountConsume.setBackgroundResource(R.drawable.account_button_none_selector_bg);
            holder.accountConsume.setTextColor(ResourceUtils.getColor(R.color.colorBody));
        }
        Glide.with(mContext).load(bean.imageUrl).error(R.drawable.icon30).into(holder.imgAccountHead);
        holder.itemView.setOnClickListener(v -> mCallBack.onItemClicked(bean));
        holder.accountConsume.setOnClickListener(v -> mCallBack.onWithDrawClicked(bean));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    static class TechAccountViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.img_account_head)
        ImageView imgAccountHead;
        @BindView(R.id.account_name)
        TextView accountName;
        @BindView(R.id.reward_amount)
        TextView rewardAmount;
        @BindView(R.id.account_consume)
        TextView accountConsume;

        public TechAccountViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
