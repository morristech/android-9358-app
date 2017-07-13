package com.xmd.technician.Adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xmd.technician.Constant;
import com.xmd.technician.R;
import com.xmd.technician.bean.NearbyCusInfo;
import com.xmd.technician.common.DateUtils;
import com.xmd.technician.common.RelativeDateFormatUtil;
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.common.ThreadManager;
import com.xmd.technician.common.Utils;
import com.xmd.technician.http.RequestConstant;
import com.xmd.technician.widget.CircleAvatarView;

import java.util.ArrayList;
import java.util.List;


import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ZR on 17-3-2.
 */

public class NearbyCusAdapter extends RecyclerView.Adapter<NearbyCusAdapter.ViewHolder> {
    private Context mContext;
    private AdapterHelper mHelper = new AdapterHelper();
    private List<NearbyCusInfo> mList = new ArrayList<>();

    private OnItemCallBack mCallback;

    public NearbyCusAdapter(Context context) {
        mContext = context;
    }

    public void setCallback(OnItemCallBack callback) {
        this.mCallback = callback;
    }

    public void updateCurrentItem(int position, int count, String time) {
        mList.get(position).techHelloRecently = true;
        mList.get(position).userLeftHelloCount = count;
        mList.get(position).lastTechHelloTime = time;
        notifyItemChanged(position);
    }

    public void setData(List<NearbyCusInfo> list) {
        if (list != null) {
            mList.clear();
            mList.addAll(list);
            ThreadManager.postRunnable(ThreadManager.THREAD_TYPE_MAIN, () -> notifyDataSetChanged());
        }
    }

    public NearbyCusInfo getItem(int position) {
        if (mList != null && mList.size() > position) {
            return mList.get(position);
        }
        return null;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_nearby_customer_item, parent, false);
        mHelper.onCreateViewHolder(parent, itemView);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        mHelper.onBindViewHolder(holder.itemView, position, getItemCount());
        NearbyCusInfo info = getItem(position);
        holder.mPosition.setText(info.userPosition);    // 位置
        holder.mDistance.setText("距离" + Math.round(info.userClubDistance) + "m");   // 距离
        holder.mAvatar.setUserInfo(info.userId, info.userAvatar,false);
        holder.mNickName.setText(info.userName);    // 名称

        // 用户类型
        if (info.userType != null) {
            holder.mType.setVisibility(View.GONE);
            holder.mOtherType.setVisibility(View.GONE);
            if (info.userType.equals(RequestConstant.FANS_USER)) {
                holder.mType.setVisibility(View.VISIBLE);
                holder.mType.setImageDrawable(ResourceUtils.getDrawable(R.drawable.icon_fans));
            } else if (info.userType.equals(RequestConstant.TEMP_USER)) {
                holder.mType.setVisibility(View.VISIBLE);
                holder.mType.setImageDrawable(ResourceUtils.getDrawable(R.drawable.temporary_user));
            } else if (info.userType.equals(RequestConstant.FANS_WX_USER)) {
                holder.mType.setVisibility(View.VISIBLE);
                holder.mOtherType.setVisibility(View.VISIBLE);
                holder.mType.setImageDrawable(ResourceUtils.getDrawable(R.drawable.icon_weixin));
                holder.mOtherType.setImageDrawable(ResourceUtils.getDrawable(R.drawable.icon_fans));
            } else {
                holder.mType.setVisibility(View.VISIBLE);
                holder.mType.setImageDrawable(ResourceUtils.getDrawable(R.drawable.icon_weixin));
            }
        } else {
            holder.mType.setVisibility(View.GONE);
            holder.mType.setVisibility(View.GONE);
        }

        holder.mLastLogin.setText(RelativeDateFormatUtil.formatModify(DateUtils.StringToDate(info.userPositionUpdateTime, Constant.FORMAT_DATE_TIME).getTime()) + "登录");  //登录情况
        holder.mHelloCount.setText(String.valueOf(info.userLeftHelloCount));    //可到招呼次数
        holder.mBookingCount.setText(String.valueOf(info.orderCount));      //预约次数

        //打赏金额
        float reward = info.rewardAmount / 100f;
        if (info.rewardAmount > 10000) {
            float payMoney = info.rewardAmount / 10000f;
            holder.mRewardCount.setText(String.format("%1.2f", payMoney) + "万");
        } else {
            holder.mRewardCount.setText(String.format("%1.2f", reward));
        }

        if (TextUtils.isEmpty(info.lastTechHelloTime)) {
            holder.mLastHelloDynamic.setText(R.string.nearby_have_no_hello_say);
        } else {
            holder.mLastHelloDynamic.setText(RelativeDateFormatUtil.formatModify(DateUtils.StringToDate(info.lastTechHelloTime, Constant.FORMAT_DATE_TIME).getTime()) + "打过招呼");  //打招呼动态
        }

        Drawable drawable;
        if (info.techHelloRecently) {
            // 近期已招呼
            holder.mSayHello.setText("已招呼");
            holder.mSayHello.setTextColor(0xffff6666);
            holder.mLayoutSayHello.setEnabled(false);
            drawable = mContext.getResources().getDrawable(R.drawable.ic_hi_red);
            holder.mLayoutSayHello.setOnClickListener(null);
        } else if (info.userLeftHelloCount <= 0) {
            // 客户被招呼次数用完
            holder.mSayHello.setText("不可招呼");
            holder.mSayHello.setTextColor(0xffff6666);
            holder.mLayoutSayHello.setEnabled(false);
            drawable = mContext.getResources().getDrawable(R.drawable.ic_hi_red);
            holder.mLayoutSayHello.setOnClickListener(null);
        } else {
            holder.mSayHello.setText("打招呼");
            holder.mSayHello.setTextColor(0xffffffff);
            holder.mLayoutSayHello.setEnabled(true);
            drawable = mContext.getResources().getDrawable(R.drawable.ic_hi_white);
            holder.mLayoutSayHello.setOnClickListener(v -> mCallback.onBtnClick(info, position));
        }
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        holder.mSayHello.setCompoundDrawables(drawable, null, null, null);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.cus_item_position)
        TextView mPosition;
        @BindView(R.id.cus_item_distance)
        TextView mDistance;
        @BindView(R.id.cus_item_avatar)
        CircleAvatarView mAvatar;
        @BindView(R.id.cus_item_nickname)
        TextView mNickName;
        @BindView(R.id.cus_item_type)
        ImageView mType;
        @BindView(R.id.cus_item_other_type)
        ImageView mOtherType;
        @BindView(R.id.cus_item_hello_count)
        TextView mHelloCount;
        @BindView(R.id.cus_item_booking_count)
        TextView mBookingCount;
        @BindView(R.id.cus_item_reward_count)
        TextView mRewardCount;
        @BindView(R.id.cus_item_last_login)
        TextView mLastLogin;
        @BindView(R.id.cus_item_last_hello_dynamic)
        TextView mLastHelloDynamic;
        @BindView(R.id.cus_item_btn_hello)
        TextView mSayHello;

        @BindView(R.id.layout_btn_say_hi)
        View mLayoutSayHello;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnItemCallBack {
        void onBtnClick(NearbyCusInfo info, int position);
    }


    // 控制Item的大小
    public class AdapterHelper {
        // 列表项之间的间隙
        private int mPagePadding = 8;
        // 附近页面展示的边缘宽度
        private int mNearPageWidth = 15;

        public void onCreateViewHolder(ViewGroup parent, View itemView) {
            // 初始化页面的宽度
            RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) itemView.getLayoutParams();
            lp.width = parent.getWidth() - Utils.dip2px(itemView.getContext(), 2 * (mPagePadding + mNearPageWidth));
        }

        public void onBindViewHolder(View itemView, final int position, int itemCount) {
            // 设置padding和margin
            int padding = Utils.dip2px(itemView.getContext(), mPagePadding);
            itemView.setPadding(padding, 0, padding, 0);
            int marginLeft = position == 0 ? padding + Utils.dip2px(itemView.getContext(), mNearPageWidth) : 0;
            int marginRight = position == itemCount - 1 ? padding + Utils.dip2px(itemView.getContext(), mNearPageWidth) : 0;
            setMargin(itemView, marginLeft, 0, marginRight, 0);
        }

        public void setMargin(View view, int left, int top, int right, int bottom) {
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            if (lp.leftMargin != left || lp.topMargin != top || lp.rightMargin != right || lp.bottomMargin != bottom) {
                lp.setMargins(left, top, right, bottom);
                view.setLayoutParams(lp);
            }
        }

        public void setPagePadding(int padding) {
            this.mPagePadding = padding;
        }

        public void setNearPageWidth(int width) {
            this.mNearPageWidth = width;
        }
    }
}
