package com.xmd.manager.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xmd.manager.R;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by sdcm on 15-10-27.
 */
public class EmptyView extends RelativeLayout {
    private View mainView;
    @BindView(R.id.layout_empty_in)
    View emptyView;
    @BindView(R.id.layout_loading_in)
    View loadingView;
    @BindView(R.id.layout_failed_in)
    View failedView;
    @BindView(R.id.tv_empty_tip)
    TextView tvEmptyTip;
    @BindView(R.id.iv_empty_tip)
    ImageView ivEmptyPic;
    @BindView(R.id.iv_failed_tip)
    ImageView ivFailedPic;
    @BindView(R.id.tv_loadingtip)
    TextView tvLoadingTip;
    @BindView(R.id.tv_failed_tip)
    TextView tvFailedTip;
    private OnRefreshListener mRefreshListener;

    public interface OnRefreshListener {
        void onRefresh();
    }

    public EmptyView(Context context) {
        super(context);
        init(context);
    }

    public EmptyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public EmptyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mainView = LayoutInflater.from(context).inflate(R.layout.empty_view, this);
        ButterKnife.bind(this, mainView);
    }

    @OnClick({R.id.tv_failed_tip, R.id.iv_failed_tip})
    public void refresh() {
        if (mRefreshListener != null) {
            mRefreshListener.onRefresh();
        }
    }

    public EmptyView setStatus(Status status) {
        if (status == Status.Gone) {
            setVisibility(View.GONE);
            mainView.setVisibility(View.GONE);
        } else if (status == Status.Empty) {
            setVisibility(View.VISIBLE);
            mainView.setVisibility(View.VISIBLE);
            loadingView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
            failedView.setVisibility(View.GONE);
        } else if (status == Status.Loading) {
            setVisibility(View.VISIBLE);
            mainView.setVisibility(View.VISIBLE);
            loadingView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
            failedView.setVisibility(View.GONE);
        } else if (status == Status.Failed) {
            setVisibility(View.VISIBLE);
            mainView.setVisibility(View.VISIBLE);
            loadingView.setVisibility(View.GONE);
            emptyView.setVisibility(View.GONE);
            failedView.setVisibility(View.VISIBLE);
        }
        return this;
    }

    public EmptyView setEmptyTip(int resId) {
        tvEmptyTip.setText(resId);
        return this;
    }

    public EmptyView setEmptyTip(CharSequence tip) {
        tvEmptyTip.setText(tip);
        return this;
    }

    public EmptyView setEmptyTop(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        int px = (int) (dpValue * scale + 0.5f);
        ivEmptyPic.setPadding(0, px, 0, 0);
        tvEmptyTip.setPadding(0, 0, 0, 0);
        return this;
    }


    public EmptyView setLoadingTip(int resId) {
        tvLoadingTip.setText(resId);
        return this;
    }

    public EmptyView setLoadingTip(CharSequence tip) {
        tvLoadingTip.setText(tip);
        return this;
    }

    public EmptyView setEmptyPic(int resId) {
        ivEmptyPic.setImageResource(resId);
        return this;
    }

    public void setOnRefreshListener(OnRefreshListener listener) {
        mRefreshListener = listener;
    }

    public enum Status {
        Loading,
        Empty,
        Gone,
        Failed
    }
}
