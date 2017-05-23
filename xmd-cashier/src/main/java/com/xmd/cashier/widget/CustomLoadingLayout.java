package com.xmd.cashier.widget;

import android.content.Context;
import android.support.annotation.IntDef;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.xmd.cashier.R;

/**
 * Created by zr on 16-12-2.
 */

public class CustomLoadingLayout extends FrameLayout {
    public final static int STATUS_SUCCESS = 0;
    public final static int STATUS_EMPTY = 1;
    public final static int STATUS_ERROR = 2;
    public final static int STATUS_NONETWORK = 3;
    public final static int STATUS_LOADING = 4;

    private static String strEmpty = "暂无数据";
    private static String strError = "加载失败，请稍后重试...";
    private static String strNoNetwork = "网络异常，请检查网络...";
    private static String strReload = "点击重试";

    private Context mContext;

    private View mContentView;

    private View mPageLoading;
    private View mPageError;
    private View mPageEmpty;
    private View mPageNetwork;

    private ImageView mImgError;
    private ImageView mImgEmpty;
    private ImageView mImgNetwork;

    private TextView mTextError;
    private TextView mTextEmpty;
    private TextView mTextNetwork;

    private TextView mClickError;
    private TextView mClickNetwork;

    private OnReloadListener mReloadListener;

    private int mStatus;

    public CustomLoadingLayout(Context context) {
        super(context);
        this.mContext = context;
    }

    public CustomLoadingLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    public CustomLoadingLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() > 1) {
            throw new IllegalStateException("LoadingLayout can host only one direct child");
        }
        mContentView = getChildAt(0);
        build();
    }

    private void build() {
        mPageLoading = LayoutInflater.from(mContext).inflate(R.layout.widget_load_ing, null);
        mPageError = LayoutInflater.from(mContext).inflate(R.layout.widget_load_error, null);
        mPageEmpty = LayoutInflater.from(mContext).inflate(R.layout.widget_load_empty, null);
        mPageNetwork = LayoutInflater.from(mContext).inflate(R.layout.widget_load_nonetwork, null);

        mTextError = (TextView) mPageError.findViewById(R.id.tv_error);
        mTextError.setText(strError);
        mImgError = (ImageView) mPageError.findViewById(R.id.img_error);
        mImgError.setImageResource(R.drawable.ic_load_error);
        mClickError = (TextView) mPageError.findViewById(R.id.tv_click_error);
        mClickError.setText(strReload);

        mTextEmpty = (TextView) mPageEmpty.findViewById(R.id.tv_empty);
        mTextEmpty.setText(strEmpty);
        mImgEmpty = (ImageView) mPageEmpty.findViewById(R.id.img_empty);
        mImgEmpty.setImageResource(R.drawable.ic_load_empty);

        mTextNetwork = (TextView) mPageNetwork.findViewById(R.id.tv_no_network);
        mTextNetwork.setText(strNoNetwork);
        mImgNetwork = (ImageView) mPageNetwork.findViewById(R.id.img_no_network);
        mImgNetwork.setImageResource(R.drawable.ic_load_nonetwork);
        mClickNetwork = (TextView) mPageNetwork.findViewById(R.id.tv_click_no_network);
        mClickNetwork.setText(strReload);

        mClickError.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mReloadListener != null) {
                    mReloadListener.onReload(v);
                }
            }
        });

        mClickNetwork.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mReloadListener != null) {
                    mReloadListener.onReload(v);
                }
            }
        });

        this.addView(mPageNetwork);
        this.addView(mPageEmpty);
        this.addView(mPageError);
        this.addView(mPageLoading);
    }

    public void setOnReloadListener(OnReloadListener listener) {
        this.mReloadListener = listener;
    }

    public void setStatus(@Flavour int status) {
        this.mStatus = status;
        switch (status) {
            case STATUS_SUCCESS:
                mContentView.setVisibility(VISIBLE);
                mPageError.setVisibility(GONE);
                mPageEmpty.setVisibility(GONE);
                mPageLoading.setVisibility(GONE);
                mPageNetwork.setVisibility(GONE);
                break;
            case STATUS_EMPTY:
                mContentView.setVisibility(GONE);
                mPageError.setVisibility(GONE);
                mPageEmpty.setVisibility(VISIBLE);
                mPageLoading.setVisibility(GONE);
                mPageNetwork.setVisibility(GONE);
                break;
            case STATUS_ERROR:
                mContentView.setVisibility(GONE);
                mPageError.setVisibility(VISIBLE);
                mPageEmpty.setVisibility(GONE);
                mPageLoading.setVisibility(GONE);
                mPageNetwork.setVisibility(GONE);
                break;
            case STATUS_NONETWORK:
                mContentView.setVisibility(GONE);
                mPageError.setVisibility(GONE);
                mPageEmpty.setVisibility(GONE);
                mPageLoading.setVisibility(GONE);
                mPageNetwork.setVisibility(VISIBLE);
                break;
            case STATUS_LOADING:
                mContentView.setVisibility(GONE);
                mPageError.setVisibility(GONE);
                mPageEmpty.setVisibility(GONE);
                mPageLoading.setVisibility(VISIBLE);
                mPageNetwork.setVisibility(GONE);
                break;
            default:
                break;
        }
    }

    public int getStatus() {
        return mStatus;
    }

    @IntDef({STATUS_SUCCESS, STATUS_EMPTY, STATUS_ERROR, STATUS_NONETWORK, STATUS_LOADING})
    public @interface Flavour {
    }

    public interface OnReloadListener {
        void onReload(View view);
    }
}
