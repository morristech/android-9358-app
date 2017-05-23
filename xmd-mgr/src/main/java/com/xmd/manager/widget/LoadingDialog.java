package com.xmd.manager.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xmd.manager.R;

/**
 * Created by lhj on 2016/6/27.
 */
public class LoadingDialog extends Dialog {
    private ImageView imageView;
    private TextView state;


    public LoadingDialog(Context context) {
        super(context, R.style.proDialog);
        setContentView(R.layout.loading_dialog);
        setCanceledOnTouchOutside(false);
        getWindow().getAttributes().gravity = Gravity.CENTER;
        imageView = (ImageView) findViewById(R.id.loadingImageView);
        state = (TextView) findViewById(R.id.state);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getBackground();
        animationDrawable.start();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getBackground();
        animationDrawable.stop();
    }

    public void show() {
        state.setVisibility(View.GONE);
        super.show();
    }

    public void show(String msg) {
        state.setVisibility(View.VISIBLE);
        state.setText(msg);
        super.show();
    }
}
