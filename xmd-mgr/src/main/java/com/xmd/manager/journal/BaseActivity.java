package com.xmd.manager.journal;


import android.app.Dialog;
import android.view.View;

import com.xmd.manager.R;
import com.xmd.manager.common.ToastUtils;
import com.xmd.manager.widget.AlertDialogBuilder;
import com.xmd.manager.widget.CombineLoadingView;
import com.xmd.manager.widget.LoadingDialog;

/**
 * Created by heyangya on 16-10-31.
 */

public class BaseActivity extends com.xmd.manager.window.BaseActivity {
    protected CombineLoadingView mCombineLoadingView;
    private Dialog mLoadingView;

    public void finishSelf() {
        finish();
    }


    public void showLoadingAnimation() {
        if (mLoadingView == null) {
            mLoadingView = new LoadingDialog(this);
        }
        mLoadingView.show();
    }


    public void hideLoadingAnimation() {
        if (mLoadingView != null) {
            mLoadingView.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLoadingView != null) {
            mLoadingView.dismiss();
        }
    }

    public void showToast(String msg) {
        ToastUtils.showToastShort(this, msg);
    }


    public void showAlertDialog(String msg) {
        new AlertDialogBuilder(this).setMessage(msg).setPositiveButton("确定", new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        }).show();
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        mCombineLoadingView = (CombineLoadingView) findViewById(R.id.combine_loading_view);
    }
}
