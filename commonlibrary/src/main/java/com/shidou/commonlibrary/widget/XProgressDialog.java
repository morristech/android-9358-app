package com.shidou.commonlibrary.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.shidou.commonlibrary.R;

/**
 * Created by heyangya on 16-6-8.
 */
public class XProgressDialog extends Dialog {
    private Context mContext;
    private TextView mTextView;

    public XProgressDialog(Context context) {
        super(context, R.style.XProgressDialog);
        init(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.process_dialog);

//        setCancelable(false);

        ImageView imageView = (ImageView) findViewById(R.id.progress);
        Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.progress);
        imageView.startAnimation(animation);

        mTextView = (TextView) findViewById(R.id.message);
    }

    private void init(Context context) {
        mContext = context;
    }

    public void show(String message) {
        super.show();
        if (!TextUtils.isEmpty(message)) {
            mTextView.setVisibility(View.VISIBLE);
            mTextView.setText(message);
        } else {
            mTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public void show() {
        super.show();
        mTextView.setVisibility(View.GONE);
    }
}
