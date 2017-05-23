package com.xmd.cashier.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.xmd.cashier.R;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.contract.PayTypeChoiceContract;

/**
 * Created by heyangya on 16-10-18.
 */

public class PayTypeChoiceListAdapter extends RecyclerView.Adapter<PayTypeChoiceListAdapter.PayTypeListHolder> {
    private PayTypeChoiceContract.Presenter mPresenter;
    private long mCurrentPayTypeId = AppConstants.PAY_TYPE_UNKNOWN;
    private int[] mPayIconIds = {
            R.drawable.pay_type_cash,
            R.drawable.pay_type_card,
            R.drawable.pay_type_wechat,
            R.drawable.pay_type_zhifubao
    };
    private int[] mPayTextIds = {
            R.string.pay_type_cash,
            R.string.pay_type_card,
            R.string.pay_type_wechat,
            R.string.pay_type_alipay
    };

    public PayTypeChoiceListAdapter(PayTypeChoiceContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public PayTypeListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.list_item_pay_type, parent, false);
        return new PayTypeListHolder(view, mPresenter);
    }

    @Override
    public void onBindViewHolder(PayTypeListHolder holder, int position) {
        holder.bind(mPayIconIds[position], mPayTextIds[position], getItemId(position), mCurrentPayTypeId);
    }

    public void setCurrentPayTypeId(long id) {
        mCurrentPayTypeId = id;
    }


    @Override
    public int getItemCount() {
        return mPayTextIds.length;
    }

    @Override
    public long getItemId(int position) {
        int payType;
        switch (position) {
            case 0:
                payType = AppConstants.PAY_TYPE_CASH;
                break;
            case 1:
                payType = AppConstants.PAY_TYPE_CARD;
                break;
            case 2:
                payType = AppConstants.PAY_TYPE_WECHART;
                break;
            case 3:
                payType = AppConstants.PAY_TYPE_ALIPAY;
                break;
            default:
                payType = AppConstants.PAY_TYPE_UNKNOWN;
                break;
        }
        return payType;
    }

    static class PayTypeListHolder extends RecyclerView.ViewHolder {
        private ImageView payTypeIcon;
        private TextView payTypeText;
        private RadioButton payTypeRadio;
        private PayTypeChoiceContract.Presenter mPresenter;


        public PayTypeListHolder(View itemView, PayTypeChoiceContract.Presenter presenter) {
            super(itemView);
            payTypeIcon = (ImageView) itemView.findViewById(R.id.pay_type_icon);
            payTypeText = (TextView) itemView.findViewById(R.id.pay_type_text);
            payTypeRadio = (RadioButton) itemView.findViewById(R.id.pay_type_radio);
            mPresenter = presenter;
        }

        public void bind(int imageId, int textId, final long id, long checkedId) {
            payTypeIcon.setImageResource(imageId);
            payTypeText.setText(textId);
            payTypeRadio.setChecked(id == checkedId);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPresenter.onSelectPayType((int) id);
                }
            });
        }
    }
}
