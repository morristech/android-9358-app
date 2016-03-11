package com.xmd.technician.window;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bugtags.library.Bugtags;
import com.xmd.technician.R;
import com.xmd.technician.SharedPreferenceHelper;
import com.xmd.technician.TechApplication;
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.http.gson.LogoutResult;
import com.xmd.technician.http.gson.TokenExpiredResult;
import com.xmd.technician.msgctrl.RxBus;

import rx.Subscription;

/**
 * Created by sdcm on 15-10-26.
 */
public class BaseActivity extends AppCompatActivity {
    
    protected ImageView mBack;
    protected FrameLayout mToolbarRight;
    protected ImageView mImageRight;
    protected TextView mAppTitle;
    protected Toolbar mToolbar;
    private ProgressDialog mProgressDialog;

    private Subscription mLogouSubscription;
    private Subscription mTokenExpiredSubscription;
    private Subscription mThrowableSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLogouSubscription = RxBus.getInstance().toObservable(LogoutResult.class).subscribe(
                logoutResult -> gotoLoginActivity(null)
        );

        mTokenExpiredSubscription = RxBus.getInstance().toObservable(TokenExpiredResult.class).subscribe(
                tokenExpiredResult -> gotoLoginActivity(tokenExpiredResult.expiredReason)
        );

        mThrowableSubscription = RxBus.getInstance().toObservable(Throwable.class).subscribe(
                throwable -> makeShortToast(throwable.getLocalizedMessage())
        );

    }

    @Override
    protected void onResume() {
        super.onResume();
        Bugtags.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Bugtags.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unsubscribe(mLogouSubscription, mTokenExpiredSubscription, mThrowableSubscription);
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
        Bugtags.onDispatchTouchEvent(this, ev);
        return super.dispatchTouchEvent(ev);
    }

    protected void initToolbar() {
        /*mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);

            mBack = (ImageView) findViewById(R.id.toolbar_back);
            mBack.setOnClickListener(v -> onBackPressed());

            mToolbarRight = (FrameLayout) findViewById(R.id.toolbar_right);
            mImageRight = (ImageView) findViewById(R.id.toolbar_right_image);
            setRightVisible(false, -1);

            mAppTitle = (TextView) findViewById(R.id.toolbar_title);
            mAppTitle.setText(getTitle());
        }*/
    }

    public void setBackVisible(boolean visible) {
        if (mBack != null) {
            mBack.setVisibility(visible ? View.VISIBLE : View.GONE);
        }
    }

    public void setRightVisible(boolean isVisible, int imgSrc) {
        if (mToolbarRight != null) {
            int visible = isVisible ? View.VISIBLE : View.GONE;
            mToolbarRight.setVisibility(visible);
            mImageRight.setVisibility(visible);
            if(isVisible && imgSrc != -1) {
                mImageRight.setBackgroundDrawable(ResourceUtils.getDrawable(imgSrc));
            }
        }
    }

    public void setTitle(String title) {
        if (mAppTitle != null) {
            mAppTitle.setText(title);
        }
    }

    protected void makeShortToast(String str){
       //Utils.makeShortToast(TechApplication.getAppContext(), str);
    }

    protected void showProgressDialog(String message){
        /*mProgressDialog = Utils.getSpinnerProgressDialog(this, message);
        mProgressDialog.show();*/
    }

    protected void dismissProgressDialogIfShowing(){
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    /**
     *
     * @param message whether to alert the message before going to login activity
     */
    protected void gotoLoginActivity(String message) {

        //Before go to login activity, alert the message if it exists
        if(!TextUtils.isEmpty(message)) {
            makeShortToast(message);
        }

        SharedPreferenceHelper.clearUserInfo();

        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
