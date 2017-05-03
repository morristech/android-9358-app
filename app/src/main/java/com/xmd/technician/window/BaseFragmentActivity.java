package com.xmd.technician.window;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.xmd.technician.R;
import com.xmd.technician.TechApplication;
import com.xmd.technician.common.ActivityHelper;
import com.xmd.technician.msgctrl.RxBus;
import com.xmd.technician.widget.AlertDialogBuilder;

import rx.Subscription;

/**
 * Created by sdcm on 15-10-26.
 */
public class BaseFragmentActivity extends AppCompatActivity {

    protected TextView mAppTitle;
    protected ImageView mBack;
    protected TextView mToolbarRight;
    protected Toolbar mToolbar;
    private ProgressDialog mProgressDialog;

    private Subscription mThrowableSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityHelper.getInstance().pushActivity(this);

        mThrowableSubscription = RxBus.getInstance().toObservable(Throwable.class).subscribe(
                throwable -> {
                    dismissProgressDialogIfShowing();
                    makeShortToast(throwable.getLocalizedMessage());
                }
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        TechApplication.getNotifier().reset();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unsubscribe(mThrowableSubscription);
        ActivityHelper.getInstance().removeActivity(this);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        initToolbar();
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        initToolbar();
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        initToolbar();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    protected void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);

            mBack = (ImageView) findViewById(R.id.toolbar_back);
            mBack.setOnClickListener(v -> onBackPressed());

            mToolbarRight = (TextView) findViewById(R.id.toolbar_right);
            setRightVisible(false, -1);

            mAppTitle = (TextView) findViewById(R.id.toolbar_title);
            mAppTitle.setText(getTitle());
        }
    }

    public void setBackVisible(boolean visible) {
        if (mBack != null) {
            mBack.setVisibility(visible ? View.VISIBLE : View.GONE);
        }
    }

    public void setRightVisible(boolean isVisible, int srcId) {
        if (mToolbarRight != null) {
            int visible = isVisible ? View.VISIBLE : View.GONE;
            mToolbarRight.setVisibility(visible);
            if (isVisible && srcId != -1) {
                mToolbarRight.setText(srcId);
            }
        }
    }

    public void setTitle(int srcId) {
        if (mAppTitle != null) {
            mAppTitle.setText(srcId);
        }
    }

    public void setTitle(String title) {
        if (mAppTitle != null) {
            mAppTitle.setText(title);
        }
    }

    public void makeShortToast(String str) {
        Toast.makeText(TechApplication.getAppContext(), TextUtils.isEmpty(str) ? getString(R.string.default_tips) : str, Toast.LENGTH_SHORT).show();
    }

    public void showToast(String message) {
        makeShortToast(message);
    }

    public void showAlertDialog(String message) {
        new AlertDialogBuilder(this)
                .setMessage(message)
                .setPositiveButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                })
                .show();
    }

    public void showLoading(String message) {
        showProgressDialog(message);
    }

    public void hideLoading() {
        dismissProgressDialogIfShowing();
    }

    protected void showProgressDialog(String message) {
        mProgressDialog = getSpinnerProgressDialog(this, message);
        mProgressDialog.show();
    }


    protected void dismissProgressDialogIfShowing() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    private ProgressDialog getSpinnerProgressDialog(Context context, String msg) {
        ProgressDialog progressDialog = new ProgressDialog(
                context, ProgressDialog.THEME_TRADITIONAL);
        progressDialog.setProgressStyle(
                ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(msg);
        progressDialog.setCancelable(true);

        return progressDialog;
    }
}
