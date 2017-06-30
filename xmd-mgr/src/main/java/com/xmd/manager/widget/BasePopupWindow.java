package com.xmd.manager.widget;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.widget.PopupWindow;

import com.xmd.manager.R;
import com.xmd.manager.common.ActivityHelper;
import com.xmd.manager.common.ResourceUtils;
import com.xmd.manager.common.Utils;

import java.util.Map;

import butterknife.ButterKnife;

/**
 * Created by sdcm on 15-11-20.
 */
public abstract class BasePopupWindow implements PopupWindow.OnDismissListener {

    protected View mParentView;
    protected PopupWindow mPopupWindow;
    protected Activity mActivity;
    protected Map<String, String> mParams;
    protected int[] mWh;

    protected BasePopupWindow(View parentView, Map<String, String> params) {
        mActivity = ActivityHelper.getInstance().getCurrentActivity();
        mParentView = parentView != null ? parentView : mActivity.getWindow().getDecorView();
        mParams = params;

        mWh = Utils.getScreenWidthHeight(mActivity);
    }

    protected void initPopupWidnow(View contentView, int width, int height) {
        mPopupWindow = new PopupWindow(contentView, width, height);
        mPopupWindow.setBackgroundDrawable(ResourceUtils.getDrawable(R.drawable.shape_popup_window));
        mPopupWindow.setFocusable(true);
        mPopupWindow.setAnimationStyle(R.style.anim_bottom_to_top_style);
        mPopupWindow.setOnDismissListener(this);

        ButterKnife.bind(this, contentView);
    }

    protected void setAnimationStyle(int animId) {
        mPopupWindow.setAnimationStyle(animId);
    }

    @Override
    public void onDismiss() {
        dismissPopupWindow(mActivity, mPopupWindow);
    }

    /**
     * 默认显示在屏幕中央
     */
    public void show() {
        show(Gravity.CENTER, 0, 0);
    }

    public void showAtBottom() {
        show(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    /**
     * 显示在点击的View下方
     */
    public void showAsDropDown(int offsetX, int offsetY) {
        showPopupWindowAsDropDown(mActivity, mPopupWindow, mParentView, offsetX, offsetY);
    }

    /**
     * Show with mask in the specific location
     *
     * @param gravity
     * @param x
     * @param y
     */
    public void show(int gravity, int x, int y) {
        showPopupWindow(mActivity, mPopupWindow, mParentView, gravity, x, y, true);
    }

    /**
     * 显示在点击的View上方
     */
    public void showAsAbove(int offsetX, int offsetY) {
        int[] location = new int[2];
        mParentView.getLocationOnScreen(location);
        showPopupWindow(mActivity, mPopupWindow, mParentView, Gravity.START | Gravity.BOTTOM,
                location[0] - mPopupWindow.getWidth() / 2 + offsetX, mWh[1] - location[1] + offsetY, true);
    }

    public void showAsAboveCenter() {
        showAsAbove(mParentView.getWidth() / 2, 0);
    }

    public void showAsAboveLeft() {
        showAsAbove(mPopupWindow.getWidth() / 4, 0);
    }

    public void showAsDownCenter(boolean toMask) {
        showAsDown(mParentView.getWidth() / 2 - mPopupWindow.getWidth() / 2, -18);
    }
    public void showAsDownCenter() {
        showAsDown(mParentView.getWidth() / 2 - mPopupWindow.getWidth() / 2, 0);
    }

    /**
     * 显示在点击的View下方
     */
    public void showAsDown(int offsetX, int offsetY) {
        showPopupWindowAsDropDown(mActivity, mPopupWindow, mParentView, offsetX, offsetY);
    }

    /**
     * show with mask in the specific location
     *
     * @param gravity
     * @param x
     * @param y
     */
    public void showWithoutMask(int gravity, int x, int y) {
        showPopupWindow(mActivity, mPopupWindow, mParentView, gravity, x, y, false);
    }

    /**
     * dismiss the popup window
     */
    public void dismiss() {
        dismissPopupWindow(mActivity, mPopupWindow);
    }


    /**
     * 显示弹窗，并根据标识toMask遮罩背景层
     *
     * @param activity
     * @param popupWindow
     * @param parent
     * @param gravity
     * @param x
     * @param y
     * @param toMask
     */
    private void showPopupWindow(Activity activity, PopupWindow popupWindow, View parent, int gravity, int x,
                                 int y, boolean toMask) {
        popupWindow.showAtLocation(parent, gravity, x, y);
//        if (toMask) {
//            Utils.maskScreen(activity, true);
//        }
    }

    /**
     * @param activity
     * @param popupWindow
     * @param parent
     *
     */
    private void showPopupWindowAsDropDown(Activity activity, PopupWindow popupWindow, View parent, int x,
                                           int y) {
        popupWindow.showAsDropDown(parent, x, y);
        Utils.maskScreen(activity, true);
    }

    /**
     * 隐藏弹窗，并取消遮罩效果
     *
     * @param activity
     * @param pw
     */
    private void dismissPopupWindow(Activity activity, PopupWindow pw) {
        if (pw != null && pw.isShowing()) {
            pw.dismiss();

        }
        Utils.maskScreen(activity, false);
    }
}
