package com.xmd.technician.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xmd.technician.R;
import com.xmd.technician.bean.RecentlyVisitorBean;
import com.xmd.technician.common.RelativeDateFormatUtil;
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.common.Utils;
import com.xmd.technician.http.RequestConstant;
import com.xmd.technician.widget.CircleImageView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by sdcm on 17-3-24.
 */

public class RecentlyVisitorAdapter<T> extends RecyclerView.Adapter {

    private static final int TYPE_RECENTLY_VISIT_ITEM = 0;
    private static final int TYPE_FOOTER = 1;
    private static final int TYPE_DADA_IS_EMPTY = 2;

    private boolean mIsNoMore = false;
    private boolean mIsEmpty = false;
    private List<RecentlyVisitorBean> mData = null;
    private Context mContext;
    private CallbackInterface mCallbackInterface;

    public RecentlyVisitorAdapter(Context context, List<RecentlyVisitorBean> data) {
        this.mContext = context;
        this.mData = data;
    }

    public void setData(List<RecentlyVisitorBean> data) {
        this.mData = data;
        if(data.size() == 0){
            mIsEmpty = true;
        }else {
            mIsEmpty = false;
        }
        notifyDataSetChanged();
    }

    public void setIsNoMore(boolean isNoMore) {
        mIsNoMore = isNoMore;
    }

    public void setCallbackListener(CallbackInterface mCallback) {
        this.mCallbackInterface = mCallback;
    }


    public interface CallbackInterface<T> {

        void onSayHiButtonClicked(T bean,int position);

        void onItemClicked(T bean);

        void onLoadMoreButtonClicked();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_RECENTLY_VISIT_ITEM:
                View viewRecentlyVisit = LayoutInflater.from(parent.getContext()).inflate(R.layout.recently_visitor_item, parent, false);
                return new RecentlyVisitViewHolder(viewRecentlyVisit);
            case TYPE_FOOTER:
                View viewFoot = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_footer, parent, false);
                return new ListFooterHolder(viewFoot);
            default:
                View viewDefault = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_footer, parent, false);
                return new ListFooterHolder(viewDefault);
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof RecentlyVisitViewHolder) {
            Object obj = mData.get(position);
            if (!(obj instanceof RecentlyVisitorBean)) {
                return;
            }
            final RecentlyVisitorBean recentlyVisitor = (RecentlyVisitorBean) obj;
            RecentlyVisitViewHolder viewHolder = (RecentlyVisitViewHolder) holder;

            if (Long.parseLong(recentlyVisitor.userId) > 0) {
                if (Utils.isNotEmpty(recentlyVisitor.userNoteName)) {
                    viewHolder.mVisitorName.setText(recentlyVisitor.userNoteName);
                } else if (Utils.isNotEmpty(recentlyVisitor.userName)) {
                    viewHolder.mVisitorName.setText(recentlyVisitor.userName);
                } else {
                    viewHolder.mVisitorName.setText(ResourceUtils.getString(R.string.default_user_name));
                }
            } else {
                viewHolder.mVisitorName.setText(ResourceUtils.getString(R.string.visitor_type));
            }
            if (Utils.isNotEmpty(recentlyVisitor.customerType)) {
                if (Utils.isNotEmpty(recentlyVisitor.emchatId) && !recentlyVisitor.customerType.equals(RequestConstant.TEMP_USER)) {
                    viewHolder.mVisitorToChat.setVisibility(View.VISIBLE);
                    if (Utils.isNotEmpty(recentlyVisitor.canSayHello)) {
                        viewHolder.mVisitorToChat.setVisibility(View.VISIBLE);
                        if (recentlyVisitor.canSayHello.equals("0")) {
                            viewHolder.mVisitorToChat.setEnabled(false);
                            viewHolder.mVisitorToChat.setText(ResourceUtils.getString(R.string.had_say_hi));
                            viewHolder.mVisitorToChat.setTextColor(ResourceUtils.getColor(R.color.color_white));
                        } else if (recentlyVisitor.canSayHello.equals("1")) {
                            viewHolder.mVisitorToChat.setEnabled(true);
                            viewHolder.mVisitorToChat.setText(ResourceUtils.getString(R.string.to_say_hi));
                        }
                    } else {
                        viewHolder.mVisitorToChat.setVisibility(View.GONE);
                    }
                } else {
                    viewHolder.mVisitorToChat.setVisibility(View.GONE);
                }
            } else {
                viewHolder.mVisitorToChat.setVisibility(View.GONE);
            }

            if (Utils.isNotEmpty(recentlyVisitor.techName) && Utils.isNotEmpty(recentlyVisitor.techSerialNo)) {
                viewHolder.mVisitorTech.setVisibility(View.VISIBLE);
                viewHolder.mVisitorTechNum.setVisibility(View.VISIBLE);
                viewHolder.mVisitorTech.setText(String.format("所属技师：%s", recentlyVisitor.techName));
                String mun = String.format("[%s]", recentlyVisitor.techSerialNo);
                viewHolder.mVisitorTechNum.setText(Utils.changeColor(mun, ResourceUtils.getColor(R.color.contact_marker), 1, mun.lastIndexOf("]")));
            } else if (Utils.isNotEmpty(recentlyVisitor.techName)) {
                viewHolder.mVisitorTech.setVisibility(View.VISIBLE);
                viewHolder.mVisitorTech.setText(String.format("所属技师：%s", recentlyVisitor.techName));
            } else {
                viewHolder.mVisitorTech.setVisibility(View.GONE);
                viewHolder.mVisitorTechNum.setVisibility(View.GONE);
            }

            if (null != recentlyVisitor.customerType) {
                viewHolder.mVisitorType.setVisibility(View.GONE);
                viewHolder.mVisitorOtherType.setVisibility(View.GONE);
                if (recentlyVisitor.customerType.equals(RequestConstant.FANS_USER)) {
                    viewHolder.mVisitorType.setVisibility(View.VISIBLE);
                    viewHolder.mVisitorType.setImageDrawable(ResourceUtils.getDrawable(R.drawable.icon_fans));
                } else if (recentlyVisitor.customerType.equals(RequestConstant.TEMP_USER)) {
                    viewHolder.mVisitorType.setVisibility(View.VISIBLE);
                    viewHolder.mVisitorType.setImageDrawable(ResourceUtils.getDrawable(R.drawable.temporary_user));
                } else if (recentlyVisitor.customerType.equals(RequestConstant.FANS_WX_USER)) {
                    viewHolder.mVisitorOtherType.setVisibility(View.VISIBLE);
                    viewHolder.mVisitorType.setVisibility(View.VISIBLE);
                    viewHolder.mVisitorType.setImageDrawable(ResourceUtils.getDrawable(R.drawable.icon_weixin));
                    viewHolder.mVisitorOtherType.setImageDrawable(ResourceUtils.getDrawable(R.drawable.icon_fans));
                } else {
                    viewHolder.mVisitorType.setVisibility(View.VISIBLE);
                    viewHolder.mVisitorType.setImageDrawable(ResourceUtils.getDrawable(R.drawable.icon_weixin));
                }
            } else {
                viewHolder.mVisitorType.setVisibility(View.GONE);
                viewHolder.mVisitorOtherType.setVisibility(View.GONE);
            }
            viewHolder.mVisitorTime.setText(RelativeDateFormatUtil.format(recentlyVisitor.createdAt));
            Glide.with(mContext).load(recentlyVisitor.avatarUrl).into(viewHolder.mVisitorHead);

            viewHolder.mVisitorToChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        mCallbackInterface.onSayHiButtonClicked(recentlyVisitor,position);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            viewHolder.itemView.setOnClickListener(v -> {
                mCallbackInterface.onItemClicked(recentlyVisitor);
            });

            return;

        }else if (holder instanceof ListFooterHolder) {
            ListFooterHolder footerHolder = (ListFooterHolder) holder;
            String desc = ResourceUtils.getString(R.string.order_list_item_loading);
            if (mIsEmpty) {
                desc = ResourceUtils.getString(R.string.order_list_item_empty);
                footerHolder.itemFooter.setOnClickListener(null);
            } else if (mIsNoMore) {
                desc = ResourceUtils.getString(R.string.order_list_item_no_more);
                footerHolder.itemFooter.setOnClickListener(null);
            } else {
                footerHolder.itemFooter.setOnClickListener(v -> mCallbackInterface.onLoadMoreButtonClicked());
            }
            footerHolder.itemFooter.setText(desc);
            return;
        }
    }

    @Override
    public int getItemCount() {
        return mData.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position + 1 == getItemCount()) {
            return TYPE_FOOTER;
        } else if (!mIsEmpty) {
            return TYPE_RECENTLY_VISIT_ITEM;
        } else {
            return TYPE_DADA_IS_EMPTY;
        }
    }


    static class RecentlyVisitViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.visitor_head)
        CircleImageView mVisitorHead;
        @Bind(R.id.visitor_name)
        TextView mVisitorName;
        @Bind(R.id.visitor_type)
        ImageView mVisitorType;
        @Bind(R.id.visitor_other_type)
        ImageView mVisitorOtherType;
        @Bind(R.id.visitor_to_chat)
        TextView mVisitorToChat;
        @Bind(R.id.visitor_tech)
        TextView mVisitorTech;
        @Bind(R.id.visitor_tech_num)
        TextView mVisitorTechNum;
        @Bind(R.id.visitor_time)
        TextView mVisitorTime;

        public RecentlyVisitViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class ListFooterHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.item_footer)
        TextView itemFooter;

        public ListFooterHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
