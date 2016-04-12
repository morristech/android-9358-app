package com.xmd.technician.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xmd.technician.R;
import com.xmd.technician.common.ThreadManager;
import com.xmd.technician.model.ConsumeInfo;
import com.xmd.technician.widget.CircleImageView;
import com.xmd.technician.window.ConsumeDetailActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by sdcm on 16-3-31.
 */
public class ConsumeDetailAdapter extends RecyclerView.Adapter{

    private static final byte USER_REWARD = 1;
    private static final byte COUPON_REWARD = 2;
    private static final byte PAID_COUPON = 3;
    private static final byte WITHDRAWAL = 4;
    private static final byte TYPE_FOOTER = 0;

    private List<ConsumeInfo> mConsumeList;
    private int mConsumeType = WITHDRAWAL;
    private Context mContext;
    private boolean mIsNoMore = false;
    private View.OnClickListener mFooterClickListener;

    public ConsumeDetailAdapter(Context context, int consumeType){
        mContext = context;
        mConsumeList = new ArrayList<>();
        mConsumeType = consumeType;

    }

    public void setOnFooterClickListener(View.OnClickListener listener){
        mFooterClickListener = listener;
    }

    public void refreshDataSet(List<ConsumeInfo> infos){
        if(infos == null){
            return;
        }
        mConsumeList.clear();
        mConsumeList.addAll(infos);
        ThreadManager.postRunnable(ThreadManager.THREAD_TYPE_MAIN, new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }

    /**
     * There is data, but need to show footer "No More"
     *
     * @param isNoMore
     */
    public void setIsNoMore(boolean isNoMore) {
        mIsNoMore = isNoMore;
    }

    @Override
    public int getItemViewType(int position) {
        if(position + 1 == getItemCount()){
            return TYPE_FOOTER;
        }

        ConsumeInfo consumeInfo = mConsumeList.get(position);
        if(consumeInfo.consumeChannel.equals("user_reward")){
            return USER_REWARD;
        }else if(consumeInfo.consumeChannel.equals("red_commission")){
            return COUPON_REWARD;
        } else if(consumeInfo.consumeChannel.equals("commission")){
            return PAID_COUPON;
        }else {
            return WITHDRAWAL;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == TYPE_FOOTER){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_footer, parent, false);
            return new FooterViewHolder(view);
        }

        View view;
        switch (viewType){
            case USER_REWARD:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.consume_record_item, parent,false);
                break;
            case COUPON_REWARD:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.consume_record_item, parent,false);
                break;
            case PAID_COUPON:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.clock_commission_item, parent,false);
                break;
            default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.consume_record_item, parent,false);
                break;
        }
        return new DetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof DetailViewHolder){
            DetailViewHolder viewHolder = (DetailViewHolder) holder;
            ConsumeInfo info = mConsumeList.get(position);
            viewHolder.mTitle.setText(info.title);
            viewHolder.mRemark.setText(info.remark);
            viewHolder.mTimeStamp.setText(info.dealDate);

            int viewType = getItemViewType(position);
            if(viewType == WITHDRAWAL){
                viewHolder.mAmount.setTextColor(mContext.getResources().getColor(R.color.colorTitle));
                viewHolder.mAmount.setText(String.format(mContext.getString(R.string.consume_record), info.amount));
                viewHolder.mAvatar.setImageResource(R.drawable.icon35);
            }else {
                viewHolder.mAmount.setText(String.format(mContext.getString(R.string.reward_record), info.amount));
                if(viewType == COUPON_REWARD){
                    viewHolder.mAvatar.setVisibility(View.GONE);
                }else {
                    Glide.with(mContext).load(info.headImgUrl).into(viewHolder.mAvatar);
                }
            }
        }else if(holder instanceof FooterViewHolder){
            FooterViewHolder footerHolder = (FooterViewHolder) holder;
            String desc = mContext.getResources().getString(R.string.list_item_loading);
            if (mConsumeList.isEmpty()) {
                desc = mContext.getResources().getString(R.string.list_item_empty);
                footerHolder.itemFooter.setOnClickListener(null);
            } else if (mIsNoMore) {
                desc = mContext.getResources().getString(R.string.list_item_no_more);
                footerHolder.itemFooter.setOnClickListener(null);
            } else {
                footerHolder.itemFooter.setOnClickListener(v -> {
                    if(mFooterClickListener != null){
                        mFooterClickListener.onClick(v);
                    }
                });
            }
            footerHolder.itemFooter.setText(desc);
        }
    }

    @Override
    public int getItemCount() {
        return mConsumeList.size() + 1;
    }

    public class DetailViewHolder extends RecyclerView.ViewHolder{

        @Bind(R.id.avatar) CircleImageView mAvatar;
        @Bind(R.id.title) TextView mTitle;
        @Bind(R.id.remark) TextView mRemark;
        @Bind(R.id.amount) TextView mAmount;
        @Bind(R.id.time) TextView mTimeStamp;

        public DetailViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class FooterViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.item_footer) TextView itemFooter;

        public FooterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
