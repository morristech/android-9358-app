package com.xmd.manager.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntDef;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.xmd.manager.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by heyangya on 16-11-22.
 */

public class CombineLoadingView extends FrameLayout {
    //加载动画
    private View mLoadingView;

    //空数据
    private View mEmptyView;
    private ImageView mEmptyImageView;
    private TextView mEmptyTextView;

    //错误页面
    private View mErrorView;
    private ImageView mErrorImageView;
    private TextView mErrorTextView;

    public static final int STATUS_LOADING = 1;
    public static final int STATUS_SUCCESS = 2;
    public static final int STATUS_EMPTY = 3;
    public static final int STATUS_ERROR = 4;

    @IntDef({STATUS_LOADING, STATUS_SUCCESS, STATUS_EMPTY, STATUS_ERROR})
    @Retention(RetentionPolicy.SOURCE)
    public @interface STATUS {
    }

    public CombineLoadingView(Context context) {
        super(context);
    }

    public CombineLoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CombineLoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CombineLoadingView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void init(View loadingView, @DrawableRes int emptyImage, String emptyText, @DrawableRes int errorImage, String errorText) {
        if (loadingView != null) {
            mLoadingView = loadingView;
            addView(mLoadingView);
            ((LayoutParams) mLoadingView.getLayoutParams()).gravity = Gravity.CENTER;
        }

        if (emptyImage != -1) {
            mEmptyView = LayoutInflater.from(getContext()).inflate(R.layout.widget_combine_loading_view, null);
            addView(mEmptyView);

            mEmptyImageView = (ImageView) mEmptyView.findViewById(R.id.image);
            mEmptyImageView.setImageResource(emptyImage);

            mEmptyTextView = (TextView) mEmptyView.findViewById(R.id.text);
            mEmptyTextView.setText(emptyText);
        }

        if (errorImage != -1) {
            mErrorView = LayoutInflater.from(getContext()).inflate(R.layout.widget_combine_loading_view, null);
            addView(mErrorView);

            mErrorImageView = (ImageView) mErrorView.findViewById(R.id.image);
            mErrorImageView.setImageResource(errorImage);

            mErrorTextView = (TextView) mErrorView.findViewById(R.id.text);
            mErrorTextView.setText(errorText);
        }

        setViewVisible(mLoadingView, GONE);
        setViewVisible(mEmptyView, GONE);
        setViewVisible(mErrorView, GONE);
    }

    public void setStatus(@STATUS int status) {
        setVisibility(VISIBLE);
        switch (status) {
            case STATUS_LOADING:
                setViewVisible(mLoadingView, VISIBLE);
                setViewVisible(mEmptyView, GONE);
                setViewVisible(mErrorView, GONE);
                break;
            case STATUS_EMPTY:
                setViewVisible(mLoadingView, GONE);
                setViewVisible(mEmptyView, VISIBLE);
                setViewVisible(mErrorView, GONE);
                break;
            case STATUS_ERROR:
                setViewVisible(mLoadingView, GONE);
                setViewVisible(mEmptyView, GONE);
                setViewVisible(mErrorView, VISIBLE);
                break;
            default:
                setVisibility(GONE);
                setViewVisible(mLoadingView, GONE);
                setViewVisible(mEmptyView, GONE);
                setViewVisible(mErrorView, GONE);
        }
    }

    private void setViewVisible(View view, int visible) {
        if (view != null) {
            view.setVisibility(visible);
        }
    }

    public void setOnClickErrorViewListener(View.OnClickListener listener) {
        if (mErrorView != null) {
            mErrorView.setOnClickListener(listener);
        }
    }

}
