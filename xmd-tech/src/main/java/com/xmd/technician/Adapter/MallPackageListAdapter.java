package com.xmd.technician.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.xmd.technician.Constant;
import com.xmd.technician.R;
import com.xmd.technician.bean.OnceCardItemBean;
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.widget.RoundImageView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Lhj on 17-5-22.
 */

public class MallPackageListAdapter extends RecyclerView.Adapter {


    private Context mContext;
    private List<OnceCardItemBean> mCardList;
    private ItemClickedInterface mCallback;

    public MallPackageListAdapter(Context context, List<OnceCardItemBean> beanList) {
        this.mContext = context;
        this.mCardList = beanList;
    }

    public interface ItemClickedInterface {
        void onShareClicked(OnceCardItemBean bean);

        void onPositiveButtonClicked(OnceCardItemBean bean);
    }

    public void setItemClickedInterface(ItemClickedInterface interfaceClick) {
        this.mCallback = interfaceClick;
    }

    public void setData(List<OnceCardItemBean> beanList) {
        this.mCardList = beanList;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View viewOnceCard = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_once_card_item, parent, false);
        return new MallPackageViewHolder(viewOnceCard);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MallPackageViewHolder) {
            Object obj = mCardList.get(position);
            if (!(obj instanceof OnceCardItemBean)) {
                return;
            }
            final OnceCardItemBean onceCard = (OnceCardItemBean) obj;
            MallPackageViewHolder cardItemViewHolder = (MallPackageViewHolder) holder;
            if (onceCard.cardType.equals(Constant.ITEM_CARD_TYPE) && onceCard.position == 0) {
                cardItemViewHolder.mIvOnceCardTitle.setVisibility(View.VISIBLE);
                Glide.with(mContext).load(R.drawable.img_once_card).into(cardItemViewHolder.mIvOnceCardTitle);

            } else if (onceCard.cardType.equals(Constant.ITEM_PACKAGE_TYPE) && onceCard.position == 0) {
                cardItemViewHolder.mIvOnceCardTitle.setVisibility(View.VISIBLE);
                Glide.with(mContext).load(R.drawable.img_package).into(cardItemViewHolder.mIvOnceCardTitle);

            } else if (onceCard.cardType.equals(Constant.CREDIT_GIFT_TYPE) && onceCard.position == 0) {
                cardItemViewHolder.mIvOnceCardTitle.setVisibility(View.VISIBLE);
                Glide.with(mContext).load(R.drawable.img_credit_gift).into(cardItemViewHolder.mIvOnceCardTitle);
            } else {
                cardItemViewHolder.mIvOnceCardTitle.setVisibility(View.GONE);

            }
            if (onceCard.cardType.equals(Constant.ITEM_PACKAGE_TYPE)) {
                cardItemViewHolder.mOnceCardDiscount.setVisibility(View.VISIBLE);
                cardItemViewHolder.mOnceCardDiscount.setText(onceCard.depositRate);
            } else {
                cardItemViewHolder.mOnceCardDiscount.setVisibility(View.GONE);
            }

            if(onceCard.cardType.equals(Constant.CREDIT_GIFT_TYPE)){
                cardItemViewHolder.tvShowCodeText.setText(ResourceUtils.getString(R.string.scan_code_to_exchange));
            }else{
                cardItemViewHolder.tvShowCodeText.setText(ResourceUtils.getString(R.string.scan_code_to_buy));
            }
            Glide.with(mContext).load(onceCard.imageUrl).into(cardItemViewHolder.mOnceCardHead);
            cardItemViewHolder.mOnceCardTitle.setText(onceCard.name);
            cardItemViewHolder.expandTextView.setText(onceCard.comboDescription);

            cardItemViewHolder.mOnceCardMoney.setText(onceCard.techRoyalty);
            cardItemViewHolder.mOnceCardPrice.setText(onceCard.discountPrice);
            cardItemViewHolder.mOnceCardShare.setOnClickListener(v -> mCallback.onShareClicked(onceCard));
            cardItemViewHolder.mShowCode.setOnClickListener(v -> mCallback.onPositiveButtonClicked(onceCard));
            return;
        }
    }

    @Override
    public int getItemCount() {
        return mCardList.size();
    }

    static class MallPackageViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.iv_once_card_title)
        ImageView mIvOnceCardTitle;
        @Bind(R.id.once_card_head)
        RoundImageView mOnceCardHead;
        @Bind(R.id.once_card_title)
        TextView mOnceCardTitle;
        @Bind(R.id.expand_text_view)
        ExpandableTextView expandTextView;
        @Bind(R.id.once_card_money)
        TextView mOnceCardMoney;
        @Bind(R.id.once_card_price)
        TextView mOnceCardPrice;
        @Bind(R.id.once_card_share)
        Button mOnceCardShare;
        @Bind(R.id.once_card_discount)
        TextView mOnceCardDiscount;
        @Bind(R.id.ll_show_code)
        LinearLayout mShowCode;
        @Bind(R.id.tv_show_code_text)
        TextView tvShowCodeText;


        MallPackageViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }


}
