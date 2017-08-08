package com.xmd.cashier.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.xmd.cashier.R;
import com.xmd.cashier.adapter.VerifyDiscountAdapter;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.dal.bean.CouponInfo;

import java.util.List;

/**
 * Created by zr on 17-8-4.
 */

public class VerifyDiscountDialog extends Dialog {
    private Context mContext;
    private List<CouponInfo> discounts;
    private VerifyDiscountAdapter mAdapter;
    private CallBack mCallBack;

    private ClearableEditText mAmountInput;
    private RecyclerView mDiscountList;
    private TextView mDiscountTotal;
    private Button mNegative;
    private Button mPositive;

    public VerifyDiscountDialog(@NonNull Context context) {
        super(context);
    }

    public VerifyDiscountDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

    protected VerifyDiscountDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public VerifyDiscountDialog(Context context, List<CouponInfo> discountInfos) {
        super(context);
        this.mContext = context;
        this.discounts = discountInfos;
    }

    public void setCallBack(CallBack callback) {
        mCallBack = callback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_dialog_discount);
        mAmountInput = (ClearableEditText) findViewById(R.id.dialog_input_amount);
        mDiscountList = (RecyclerView) findViewById(R.id.dialog_discount_list);
        mDiscountTotal = (TextView) findViewById(R.id.dialog_discount_total);
        mNegative = (Button) findViewById(R.id.dialog_negative);
        mPositive = (Button) findViewById(R.id.dialog_positive);

        if (!discounts.isEmpty() && discounts.size() > 1) {
            mDiscountTotal.setVisibility(View.VISIBLE);
        } else {
            mDiscountTotal.setVisibility(View.GONE);
        }

        mAdapter = new VerifyDiscountAdapter();
        mAdapter.setData(discounts);
        mDiscountList.setLayoutManager(new LinearLayoutManager(mContext));
        mDiscountList.setAdapter(mAdapter);

        mAmountInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int originAmount = Utils.stringToMoney(mAmountInput.getText().toString());  //输入的消费金额
                int totalActAmount = 0; //总的折扣
                // 每一张折扣券都设置消费金额
                for (CouponInfo info : discounts) {
                    info.originAmount = originAmount;
                    totalActAmount += info.actAmount;
                }
                // 更新列表
                mAdapter.notifyDataSetChanged();

                if (totalActAmount > 100000) {
                    mDiscountTotal.setText("总优惠金额:" + Utils.moneyToStringEx(originAmount) + "元");
                } else {
                    long discountAmount = (long) originAmount * (100000 - totalActAmount) / 100000;
                    mDiscountTotal.setText("总优惠金额:" + Utils.moneyToStringEx((int) discountAmount) + "元");
                }
            }
        });

        mNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallBack.onNegative();
            }
        });

        mPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallBack.onPositive(mAmountInput.getText().toString());
            }
        });
    }

    public interface CallBack {
        void onNegative();

        void onPositive(String input);
    }
}
