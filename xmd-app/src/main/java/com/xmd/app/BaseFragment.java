package com.xmd.app;

import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by mo on 17-6-21.
 */

public class BaseFragment extends Fragment {

    public void setBackVisible(boolean visible) {
        RelativeLayout rlBack = (RelativeLayout) getView().findViewById(R.id.rl_toolbar_back);
        ImageView imageBack = (ImageView) getView().findViewById(R.id.img_toolbar_back);
        if (imageBack != null) {
            imageBack.setVisibility(visible ? View.VISIBLE : View.GONE);
        }

    }

    public void setTitle(String title) {
        TextView textView = (TextView) getView().findViewById(R.id.tv_title);
        if (textView != null) {
            textView.setText(title);
        }
    }

    public void setRightVisible(boolean isVisible, int srcId) {
        ImageView rightImage = (ImageView) getView().findViewById(R.id.img_toolbar_right);
        RelativeLayout toolbarRightImage = (RelativeLayout) getView().findViewById(R.id.rl_toolbar_right);
        if (toolbarRightImage != null) {
            int visible = isVisible ? View.VISIBLE : View.GONE;
            toolbarRightImage.setVisibility(visible);
            if (isVisible && srcId != -1) {
                rightImage.setImageResource(srcId);
                toolbarRightImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onRightImageClickedListener();
                    }


                });
            }
        }

    }

    protected void onRightImageClickedListener() {
    }
}
