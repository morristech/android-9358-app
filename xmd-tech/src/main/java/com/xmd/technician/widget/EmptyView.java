/**
 *
 */
package com.xmd.technician.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xmd.technician.R;

/**
 */
public class EmptyView extends RelativeLayout {
    private View mainView;
    private View emptyView;
    private View loadingView;
    private View failedView;
    private TextView tvEmptyTip;
    private ImageView ivEmptyPic;
    private TextView tvLoadingTip;
    private TextView tvFailedTip;
    private OnRefreshListener mRefreshListener;
    private Status mCurrentStatus;

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
        emptyView = mainView.findViewById(R.id.layout_empty_in);
        loadingView = mainView.findViewById(R.id.layout_loading_in);
        failedView = mainView.findViewById(R.id.layout_failed_in);
        tvEmptyTip = (TextView) mainView.findViewById(R.id.tv_empty_tip);
        ivEmptyPic = (ImageView) mainView.findViewById(R.id.iv_empty_tip);
        tvLoadingTip = (TextView) mainView.findViewById(R.id.tv_loadingtip);
        tvFailedTip = (TextView) mainView.findViewById(R.id.tv_failed_tip);
        tvFailedTip.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRefreshListener != null)
                    mRefreshListener.onRefresh();
            }
        });
        mCurrentStatus = Status.Empty;
    }

    public Status getStatus() {
        return mCurrentStatus;
    }

    public EmptyView setStatus(Status status) {
        mCurrentStatus = status;
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

    public EmptyView setEmptyViewWithDescription(int resId, CharSequence tip) {
        setStatus(Status.Empty);
        ivEmptyPic.setImageResource(resId);
        tvEmptyTip.setText(tip);
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
