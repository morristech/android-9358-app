package com.xmd.cashier.widget;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.widget.PopupWindow;

import com.xmd.cashier.common.Utils;

import java.util.Map;

/**
 * Created by zr on 16-11-29.
 * from com.xmd.manager
 */

public abstract class BasePopupWindow implements PopupWindow.OnDismissListener {
    protected Activity mActivity;
    protected View mParentView;
    protected Map<String, String> mParams;
    protected PopupWindow mPopupWindow;
    protected PopupWindow.OnDismissListener dismissListener;

    protected BasePopupWindow(Activity activity, View parent, Map<String, String> params) {
        mActivity = activity;
        mParentView = parent != null ? parent : mActivity.getWindow().getDecorView();
        mParams = params;
    }

    protected void initPopupWindow(View contentView, int width, int height) {
        mPopupWindow = new PopupWindow(contentView, width, height);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOnDismissListener(this);
    }

    // 设置弹出动画
    protected void setAnimationStyle(int animId) {
        mPopupWindow.setAnimationStyle(animId);
    }

    // 设置背景
    protected void setBackGround(Drawable drawable) {
        mPopupWindow.setBackgroundDrawable(drawable);
    }

    // 设置POP关闭时的监听
    public void setDismissListener(PopupWindow.OnDismissListener listener) {
        dismissListener = listener;
    }

    @Override
    public void onDismiss() {
        dismissPopupWindow(mActivity, mPopupWindow);
        if (dismissListener != null) {
            dismissListener.onDismiss();
        }
    }

    private void dismissPopupWindow(Activity activity, PopupWindow pw) {
        if (pw != null && pw.isShowing()) {
            pw.dismiss();
        }
        Utils.maskScreen(activity, false);
    }


    // 显示在点击的View下方
    public void showAsDropDown() {
        showPopupWindowAsDropDown(mActivity, mPopupWindow, mParentView, 0, 0, true);
    }

    public void showAsDown(int offsetX, int offsetY, boolean toMask) {
        showPopupWindowAsDropDown(mActivity, mPopupWindow, mParentView, offsetX, offsetY, toMask);
    }

    public void showAsDownCenter(boolean toMask) {
        showAsDown(mParentView.getWidth() / 2 - mPopupWindow.getWidth() / 2, -18, toMask);
    }

    private void showPopupWindowAsDropDown(Activity activity, PopupWindow popupWindow, View parent, int x, int y, boolean toMask) {
        popupWindow.showAsDropDown(parent, x, y);
        if (toMask) {
            Utils.maskScreen(activity, true);
        }
    }

    // 显示在屏幕中央
    public void show() {
        show(Gravity.CENTER, 0, 0);
    }

    // 显示在屏幕底部
    public void showAtBottom() {
        show(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    public void show(int gravity, int x, int y) {
        showPopupWindow(mActivity, mPopupWindow, mParentView, gravity, x, y, true);
    }

    public void showWithoutMask(int gravity, int x, int y) {
        showPopupWindow(mActivity, mPopupWindow, mParentView, gravity, x, y, false);
    }

    private void showPopupWindow(Activity activity, PopupWindow popupWindow, View parent, int gravity, int x, int y, boolean toMask) {
        popupWindow.showAtLocation(parent, gravity, x, y);
        if (toMask) {
            Utils.maskScreen(activity, true);
        }
    }
}
