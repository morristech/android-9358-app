package com.xmd.manager.window;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.shidou.commonlibrary.widget.XProgressDialog;
import com.umeng.analytics.MobclickAgent;
import com.xmd.app.XmdActivityManager;
import com.xmd.manager.ClubData;
import com.xmd.manager.Constant;
import com.xmd.manager.ManagerApplication;
import com.xmd.manager.R;
import com.xmd.manager.common.ResourceUtils;
import com.xmd.manager.common.ToastUtils;
import com.xmd.manager.common.Utils;
import com.xmd.manager.msgctrl.MsgDef;
import com.xmd.manager.msgctrl.MsgDispatcher;
import com.xmd.manager.widget.AlertDialogBuilder;
import com.xmd.manager.widget.ArrayBottomPopupWindow;

import java.util.ArrayList;

import butterknife.ButterKnife;

/**
 * Created by sdcm on 15-10-26.
 */
public class BaseActivity extends com.xmd.app.BaseActivity {

    protected ImageView mBack;
    protected FrameLayout mToolbarRight;
    protected ImageView mImageRight;
    protected TextView mAppTitle;
    protected TextView mCustomerFilterBtn;
    protected Toolbar mToolbar;
    protected TextView mUnreadMsg;
    protected TextView mRightText;

    private ArrayBottomPopupWindow<String> mCustomerPopupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        XmdActivityManager.getInstance().addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        XmdActivityManager.getInstance().removeActivity(this);
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

    protected void initToolbar() {

        ButterKnife.bind(this);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);

            mBack = (ImageView) findViewById(R.id.toolbar_left);
            mToolbarRight = (FrameLayout) findViewById(R.id.toolbar_right);
            mImageRight = (ImageView) findViewById(R.id.toolbar_right_image);
            mAppTitle = (TextView) findViewById(R.id.toolbar_title);
            mUnreadMsg = (TextView) findViewById(R.id.toolbar_notice_unread_msg);
            mCustomerFilterBtn = (TextView) findViewById(R.id.toolbar_customer_filter_btn);
            mRightText = (TextView) findViewById(R.id.toolbar_right_text);

            setLeftVisible(true, R.drawable.actionbar_back);
            setRightVisible(false, -1, null);
            setNotice(0);
            mAppTitle.setText(getTitle());

            if (mCustomerFilterBtn != null) {
                mCustomerPopupWindow = new ArrayBottomPopupWindow<>(mCustomerFilterBtn, null, ResourceUtils.getDimenInt(R.dimen.customer_type_item_width));
                mCustomerPopupWindow.setDataSet(new ArrayList<>(Constant.CUSTOMER_TYPE_LABELS.keySet()));
                mCustomerPopupWindow.setItemClickListener((parent, view, position, id) -> {
                    mCustomerFilterBtn.setText((CharSequence) parent.getAdapter().getItem(position));
                    //// TODO: 16-7-26 filter customer
                    MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_FILTER_CUSTOMER_TYPE, Constant.CUSTOMER_TYPE_LABELS.get(parent.getAdapter().getItem(position)));
                });
                mCustomerFilterBtn.setOnClickListener(v -> {
                    mCustomerPopupWindow.showAsDownCenter(true);
                });
            }
        }
    }

    public void setLeftVisible(boolean visible, int imgSrc) {
        setLeftVisible(visible, imgSrc, null);
    }

    public void setLeftVisible(boolean visible, int imgSrc, View.OnClickListener listener) {
        if (mBack != null) {
            mBack.setVisibility(visible ? View.VISIBLE : View.GONE);
            if (visible && imgSrc != -1) {
                mBack.setImageResource(imgSrc);
            }
            if (listener != null) {
                mBack.setOnClickListener(listener);
            } else {
                mBack.setOnClickListener(v -> onBackPressed());
            }
        }
    }

    public void setRightVisible(boolean isVisible, int imgSrc, View.OnClickListener listener) {
        if (mToolbarRight != null) {
            int visible = isVisible ? View.VISIBLE : View.GONE;
            mToolbarRight.setVisibility(visible);
            mImageRight.setVisibility(visible);
            if (isVisible && imgSrc != -1) {
                mImageRight.setImageResource(imgSrc);
            }
            if (listener != null) {
                mToolbarRight.setOnClickListener(listener);
            } else {
                mToolbarRight.setOnClickListener(null);
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void setRightVisible(boolean isVisible, Drawable drawable, View.OnClickListener listener) {
        if (mToolbarRight != null) {
            int visible = isVisible ? View.VISIBLE : View.GONE;
            mToolbarRight.setVisibility(visible);
            mImageRight.setVisibility(visible);
            if (isVisible && drawable != null) {
                mImageRight.setBackground(drawable);
            }
            if (listener != null) {
                mToolbarRight.setOnClickListener(listener);
            } else {
                mToolbarRight.setOnClickListener(null);
            }
        }
    }

    public void setRightVisible(boolean isVisible, String text, View.OnClickListener listener) {
        if (mToolbarRight != null) {
            int visible = isVisible ? View.VISIBLE : View.GONE;
            mToolbarRight.setVisibility(visible);
            mRightText.setVisibility(visible);
            mUnreadMsg.setVisibility(View.GONE);
            if (isVisible && Utils.isNotEmpty(text)) {
                mRightText.setText(text);
            }
            if (listener != null) {
                mToolbarRight.setOnClickListener(listener);
            } else {
                mToolbarRight.setOnClickListener(null);
            }
        }
    }

    public void setRightVisible(boolean isVisible, String text, Drawable drawableRight, View.OnClickListener listener, Boolean isBadComment) {
        if (mToolbarRight != null) {
            int visible = isVisible ? View.VISIBLE : View.GONE;
            mToolbarRight.setVisibility(visible);
            mRightText.setVisibility(visible);
            mUnreadMsg.setVisibility(View.GONE);
            if (isVisible && Utils.isNotEmpty(text)) {
                mRightText.setText(text);
                if (null != drawableRight) {
                    Drawable drawable = drawableRight;
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    mRightText.setCompoundDrawablePadding(5);
                    mRightText.setCompoundDrawables(null, null, drawableRight, null);
                    if (isBadComment) {
                        mRightText.setBackgroundResource(R.color.right_btn_color);
                    } else {
                        mRightText.setBackgroundResource(R.color.primary_color);
                    }

                }
            }
            if (listener != null) {
                mToolbarRight.setOnClickListener(listener);
            } else {
                mToolbarRight.setOnClickListener(null);
            }
        }
    }

    public void setNoticeVisible(boolean visible) {
        if (mUnreadMsg != null) {
            mUnreadMsg.setVisibility(visible && ClubData.getInstance().getEmchatMsgCount() > 0 ? View.VISIBLE : View.GONE);
        }
    }

    public void setCustomerFilterVisible(boolean visible) {
        if (mCustomerFilterBtn != null) {
            mCustomerFilterBtn.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
            mAppTitle.setVisibility(visible ? View.INVISIBLE : View.VISIBLE);
        }
    }

    public void setNotice(int count) {

        if (mUnreadMsg != null) {
            // if count == 0, then hide the notice
            if (count == 0) {
                mUnreadMsg.setVisibility(View.GONE);
                return;
            }
            // if count > 0 and mUnreadMsg has the value before, then plus the value, otherwise, make the mUnreadMsg visible with the count value
            if (View.VISIBLE == mUnreadMsg.getVisibility()) {
                int value = Integer.parseInt(mUnreadMsg.getText().toString());
                mUnreadMsg.setText(String.valueOf(value + count));
            } else {
                mUnreadMsg.setVisibility(View.VISIBLE);
                mUnreadMsg.setText(String.valueOf(count));
            }

        }
    }

    public void setTitle(String title) {
        if (mAppTitle != null) {
            mAppTitle.setText(title);
        }
    }

    protected void makeShortToast(String str) {
        ToastUtils.showToastShort(ManagerApplication.getAppContext(), str);
    }


    protected void showWarningAlertDialor(String description) {
        new AlertDialogBuilder(this)
                .setMessage(description)
                .setPositiveButton(ResourceUtils.getString(R.string.confirm), null)
                .show();
    }

    /**
     * @param message whether to alert the message before going to login activity
     */
    public void gotoLoginActivity(String message) {

        //Before go to login activity, alert the message if it exists
        if (Utils.isNotEmpty(message)) {
            makeShortToast(message);
        }
        XmdActivityManager.getInstance().finishAll();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    public void showLoading(String message) {
        if (progressDialog == null) {
            progressDialog = new XProgressDialog(this);
        }
        progressDialog.show(message);
    }
}
