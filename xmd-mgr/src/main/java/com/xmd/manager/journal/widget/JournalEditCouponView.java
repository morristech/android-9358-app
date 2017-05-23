package com.xmd.manager.journal.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.xmd.manager.R;
import com.xmd.manager.journal.Callback;
import com.xmd.manager.journal.contract.JournalContentEditContract;
import com.xmd.manager.journal.model.CouponActivity;
import com.xmd.manager.journal.model.JournalContent;
import com.xmd.manager.journal.model.JournalItemCoupon;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by heyangya on 16-11-9.
 */

public class JournalEditCouponView extends LinearLayout {
    protected Context mContext;
    protected JournalContent mContent;
    private boolean mIsInitData;
    private JournalContentEditContract.Presenter mPresenter;
    private List<ViewHolder> mViewHolders = new ArrayList<>();
    private int mViewPosition;

    public JournalEditCouponView(Context context, JournalContent content, JournalContentEditContract.Presenter presenter) {
        super(context);
        mContent = content;
        mContext = context;
        mContent.setView(mViewUpdater);
        mPresenter = presenter;
        setOrientation(VERTICAL);
    }

    public void setViewPosition(int viewPosition) {
        mViewPosition = viewPosition;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (!mIsInitData) {
            mIsInitData = true;
            TextView textView = new TextView(mContext);
            addView(textView);
            textView.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
            textView.getLayoutParams().height = 128;
            textView.setText("正在加载中...");
            textView.setGravity(Gravity.CENTER);
            mPresenter.onLoadCouponActivityList(new Callback<List<CouponActivity>>() {
                @Override
                public void onResult(Throwable error, List<CouponActivity> result) {
                    removeAllViews();
                    if (result.size() > 0) {
                        boolean haveSubData = false;
                        for (int i = 0; i < result.size(); i++) {
                            if (result.get(i).getData().size() == 0) {
                                continue;
                            }
                            haveSubData = true;
                            View itemView = LayoutInflater.from(mContext).inflate(R.layout.journal_coupon_sub_view, null);
                            addView(itemView);
                            ViewHolder viewHolder = new ViewHolder(itemView);
                            viewHolder.bind(result.get(i));
                            mViewHolders.add(viewHolder);
                        }
                        if (!haveSubData) {
                            showEmptyView();
                        }
                    } else {
                        showEmptyView();
                    }
                    notifyDataChanged();
                }
            });
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void showEmptyView() {
        removeAllViews();
        TextView textView = new TextView(getContext());
        addView(textView);
        textView.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
        textView.getLayoutParams().height = 128;
        textView.setText("当前没有" + mContent.getType().getName());
        textView.setGravity(Gravity.CENTER);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    public void notifyDataChanged() {
        if (mContent.getDataSize() > 0) {
            JournalItemCoupon journalContentCoupon = (JournalItemCoupon) mContent.getData(0);
            for (ViewHolder viewHolder : mViewHolders) {
                CouponActivity couponActivity = journalContentCoupon.getCouponActivity();
                if (couponActivity != null) {
                    viewHolder.setSelect(couponActivity, journalContentCoupon.getSelectedItemIndex());
                }
            }
        }
    }

    //presenter 使用它来通知view更新
    protected BaseContentView mViewUpdater = new BaseContentView() {
        @Override
        public void notifyDataChanged() {
            JournalEditCouponView.this.notifyDataChanged();
        }

        @Override
        public View getView() {
            return JournalEditCouponView.this;
        }
    };

    private class ViewHolder {
        private View itemView;
        private RadioButton mRadioButton;
        private TextView mNameTextView;
        private Spinner mSpinner;
        private CouponActivity mCouponActivity;

        public ViewHolder(View itemView) {
            this.itemView = itemView;
            mRadioButton = (RadioButton) itemView.findViewById(R.id.radio_button);
            mNameTextView = (TextView) itemView.findViewById(R.id.tv_name);
            mSpinner = (Spinner) itemView.findViewById(R.id.spinner);
            mSpinner.setEnabled(false);
        }


        public void bind(CouponActivity couponActivity) {
            mCouponActivity = couponActivity;
            mRadioButton.setChecked(false);
            mRadioButton.setOnClickListener(mOnClickRadioButton);
            mNameTextView.setText(couponActivity.getName());
            List<String> data = new ArrayList<>();
            for (CouponActivity.Item item : couponActivity.getData()) {
                data.add(item.getTitle());
            }
            mSpinner.setAdapter(new ArrayAdapter<>(itemView.getContext(), android.R.layout.simple_list_item_1, android.R.id.text1, data));
            mSpinner.setSelection(0);
        }

        public void setSelect(CouponActivity selectedCouponActivity, int itemIndex) {
            boolean select = selectedCouponActivity.getCategory().equals(mCouponActivity.getCategory());
            mRadioButton.setChecked(select);
            mSpinner.setEnabled(select);
            if (select) {
                mSpinner.setSelection(itemIndex);
                mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mPresenter.onSelectCouponActivity(mContent, mCouponActivity, position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            } else {
                mSpinner.setOnItemSelectedListener(null);
            }
        }

        private OnClickListener mOnClickRadioButton = new OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onSelectCouponActivity(mContent, mCouponActivity, mSpinner.getSelectedItemPosition());
            }
        };
    }
}
