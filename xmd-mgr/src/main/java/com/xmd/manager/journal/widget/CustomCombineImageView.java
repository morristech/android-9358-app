package com.xmd.manager.journal.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xmd.manager.R;
import com.xmd.manager.common.ImageTool;
import com.xmd.manager.journal.manager.ImageManager;

/**
 * Created by heyangya on 16-11-3.
 */

public class CustomCombineImageView extends LinearLayout {
    private ImageView mImageView;
    private TextView mNameTextView;
    private View mReplaceView;
    private View mDeleteView;
    private TextView mUploadTextView;
    private View mUploadSuccessView;
    private boolean mShowCircleImage;


    private OnClickImageListener mOnClickImageListener;
    private OnClickDeleteViewListener mOnClickDeleteViewListener;

    public CustomCombineImageView(Context context) {
        super(context);
    }

    public CustomCombineImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomCombineImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CustomCombineImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mImageView = (ImageView) findViewById(R.id.imageview);
        mNameTextView = (TextView) findViewById(R.id.tv_name);
        mReplaceView = findViewById(R.id.tv_replace);
        mDeleteView = findViewById(R.id.img_delete);
        mUploadTextView = (TextView) findViewById(R.id.tv_upload);
        mUploadSuccessView = findViewById(R.id.tv_upload_success);

        mDeleteView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnClickDeleteViewListener != null) {
                    mOnClickDeleteViewListener.onClick(v);
                }
            }
        });

        clear();
    }

    public void setName(String name) {
        mNameTextView.setVisibility(VISIBLE);
        mNameTextView.setText(name);
    }

    public void setImageSize(int width, int height) {
        ViewGroup.LayoutParams layoutParams = mImageView.getLayoutParams();
        layoutParams.width = width;
        layoutParams.height = height;
    }

    public void setShowCircleImage(boolean circleImage) {
        mShowCircleImage = circleImage;
    }

    public void setImage(Context context, String imageUrl) {
        if (mShowCircleImage) {
            ImageTool.loadCircleImage(context, imageUrl, mImageView);
        } else {
            ImageTool.loadImage(context, imageUrl, mImageView);
        }
    }

    public void setImage(Context context, String imageUrl, int placeHolderDrawable) {
        if (mShowCircleImage) {
            ImageTool.loadCircleImage(context, imageUrl, mImageView, placeHolderDrawable);
        } else {
            ImageTool.loadImage(context, imageUrl, mImageView, placeHolderDrawable);
        }
    }

    public void showAddView() {
        mImageView.setImageResource(R.drawable.img_add_image);
        mImageView.setOnClickListener(mOnClickImageView);
    }

    public void showReplaceView() {
        mReplaceView.setVisibility(VISIBLE);
        mImageView.setOnClickListener(mOnClickImageView);
    }

    public void hideReplaceView() {
        mReplaceView.setVisibility(GONE);
        mImageView.setOnClickListener(null);
    }

    public void showDeleteView() {
        mDeleteView.setVisibility(VISIBLE);
    }

    public void hideDeleteView() {
        mDeleteView.setVisibility(GONE);
    }

    public void showUploadSuccessView() {
        mUploadSuccessView.setVisibility(VISIBLE);
    }

    public void hideUploadSuccessView() {
        mUploadSuccessView.setVisibility(GONE);
    }

    public void setOnClickImageListener(OnClickImageListener listener) {
        mOnClickImageListener = listener;
    }

    public void setOnClickDeleteViewListener(OnClickDeleteViewListener listener) {
        mOnClickDeleteViewListener = listener;
    }

    public void clear() {
        hideDeleteView();
        hideReplaceView();
        hideUploadSuccessView();
        mUploadTextView.setVisibility(GONE);
        mNameTextView.setVisibility(GONE);
        showAddView();
    }

    public interface OnClickImageListener {
        void onClick(View v);
    }

    public interface OnClickDeleteViewListener {
        void onClick(View v);
    }

    private View.OnClickListener mOnClickImageView = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mOnClickImageListener != null) {
                mOnClickImageListener.onClick(v);
            }
        }
    };

    public ImageManager.ImageUploadListener getUpdateListener() {
        return mOnImageUploadListener;
    }

    private ImageManager.ImageUploadListener mOnImageUploadListener = new ImageManager.ImageUploadListener() {
        @Override
        public void onWait() {
            mUploadTextView.setText("等待上传");
            mUploadTextView.setVisibility(VISIBLE);
        }

        @Override
        public void onStart() {
            mUploadTextView.setText("正在上传");
            mUploadTextView.setVisibility(VISIBLE);
        }

        @Override
        public void onProgress(int progress) {
            mUploadTextView.setText(progress + "%");
        }

        @Override
        public void onFinished(String error) {
            mUploadTextView.setVisibility(GONE);
            if (error == null) {
                showUploadSuccessView();
            }
        }

        @Override
        public void onCanceled() {
            mUploadTextView.setVisibility(GONE);
        }
    };


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        //清除数据
    }
}
