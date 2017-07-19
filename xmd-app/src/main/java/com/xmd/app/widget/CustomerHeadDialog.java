package com.xmd.app.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.xmd.app.R;

/**
 * Created by Lhj on 17-7-18.
 */

public class CustomerHeadDialog extends Dialog {

    private ImageView mImageView;
    private Context mContext;

    public CustomerHeadDialog(Context context) {
        this(context, R.style.default_dialog_style);
        mContext = context;
    }

    public CustomerHeadDialog(Context context, int themeResId) {
        super(context, R.style.default_dialog_style);
        mContext = context;

    }

    protected CustomerHeadDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        mContext = context;
    }

    public void setImageHead(String url) {
        if (mImageView != null) {
            Glide.with(mContext).load(url).into(mImageView);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_customer_head_dialog);
        mImageView = (ImageView) findViewById(R.id.img_head);
    }


}
