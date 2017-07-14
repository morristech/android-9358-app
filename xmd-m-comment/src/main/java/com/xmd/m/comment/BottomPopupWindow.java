package com.xmd.m.comment;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.xmd.app.utils.Utils;
import com.xmd.m.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lhj on 17-7-4.
 */

public class BottomPopupWindow extends PopupWindow {
    private Activity mContext;
    private LayoutInflater inflater;
    private View root;
    private ViewGroup ll_container;
    private TextView tvCancel;
    private String[] items;
    private OnPopupWindowClickedListener clickedListener;
    private OnRootSelectedListener onRootSelectedListener;
    private List<ReturnVisitMenu> rootMenus = null;

    public static BottomPopupWindow getInstance(Activity context, String[] items, OnPopupWindowClickedListener onPopupWindowClickedListener) {
        View contentView = LayoutInflater.from(context).inflate(R.layout.layout_popup_window, null);
        return new BottomPopupWindow(context, contentView, items, onPopupWindowClickedListener);
    }

    public static BottomPopupWindow getInstance(Activity context, String phone, String emChaId, String commentId, String returnStatus, OnRootSelectedListener onRootSelectedListener) {
        View contentView = LayoutInflater.from(context).inflate(R.layout.layout_popup_window, null);

        return new BottomPopupWindow(context, contentView, phone, emChaId, commentId, returnStatus, onRootSelectedListener);
    }

    private BottomPopupWindow(Activity context, View contentView, String[] items, OnPopupWindowClickedListener windowClicked) {
        super(contentView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);
        this.mContext = context;
        this.items = items;
        this.clickedListener = windowClicked;
        inflater = LayoutInflater.from(mContext);
        root = contentView;
        this.setBackgroundDrawable(new BitmapDrawable());
        this.setAnimationStyle(R.style.popup_window_style);
        this.setOutsideTouchable(true);
        this.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                windowDismiss();
            }
        });
        initViews();
    }

    private BottomPopupWindow(Activity context, View contentView, String phone, String emChatId, String commentId, String returnStatus, OnRootSelectedListener onRootSelectedListener) {
        super(contentView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);
        this.mContext = context;
        this.setOutsideTouchable(true);
        this.setBackgroundDrawable(new BitmapDrawable());
        rootMenus = new ArrayList<>();
        if (!TextUtils.isEmpty(phone)) {
            ReturnVisitMenu.callPhone.setName(String.format("呼叫：  %s", phone));
            rootMenus.add(ReturnVisitMenu.callPhone);
        }
        if (!TextUtils.isEmpty(emChatId)) {
            rootMenus.add(ReturnVisitMenu.toChat);
        }
        if (!TextUtils.isEmpty(commentId) && !TextUtils.isEmpty(returnStatus)) {
            if (returnStatus.equals("N")) {
                rootMenus.add(ReturnVisitMenu.markState);
            } else if (returnStatus.equals("Y")) {
                rootMenus.add(ReturnVisitMenu.markStateUn);
            }
        }
        this.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                Utils.maskScreen(mContext, false);
            }
        });
        this.onRootSelectedListener = onRootSelectedListener;
        inflater = LayoutInflater.from(mContext);
        root = contentView;
        this.setAnimationStyle(R.style.popup_window_style);
        initRootViews();
    }

    private void initRootViews() {
        this.ll_container = (ViewGroup) root.findViewById(R.id.ll_container);
        this.tvCancel = (TextView) root.findViewById(R.id.tv_cancel);
        for (int i = 0; i < rootMenus.size(); i++) {
            ReturnVisitMenu rootMenu = rootMenus.get(i);
            Button textView = (Button) inflater.inflate(R.layout.layout_popu_window_item, ll_container, false);

            textView.setText(rootMenu.getName());
            textView.setTag(rootMenu);
            textView.setOnClickListener(onClickListener);
            ll_container.addView(textView);
        }
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                windowDismiss();
            }
        });
    }

    private void initViews() {
        this.ll_container = (ViewGroup) root.findViewById(R.id.ll_container);
        this.tvCancel = (TextView) root.findViewById(R.id.tv_cancel);
        for (int i = 0; i < items.length; i++) {
            Button textView = (Button) inflater.inflate(R.layout.layout_popu_window_item, ll_container, false);
            textView.setText(items[i] == null ? "" : items[i]);
            textView.setTag(i);
            textView.setOnClickListener(onClickListener);
            ll_container.addView(textView);
        }
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                windowDismiss();
            }
        });
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (null != clickedListener) {
                int index = (Integer) v.getTag();
                clickedListener.onItemSelected(index);
            }
            if (onRootSelectedListener != null) {
                ReturnVisitMenu rootMenu = (ReturnVisitMenu) v.getTag();
                onRootSelectedListener.onItemSelected(rootMenu);
            }
            windowDismiss();
        }
    };

    public interface OnPopupWindowClickedListener {
        void onItemSelected(int index);
    }

    public interface OnRootSelectedListener {
        void onItemSelected(ReturnVisitMenu rootMenu);
    }

    public void show() {
        Utils.maskScreen(mContext, true);
        Utils.hideKeyboard(mContext);
        this.showAtLocation(mContext.getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);

    }

    public void windowDismiss() {
        this.dismiss();
        Utils.maskScreen(mContext, false);
    }

    public void setBackgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = ((Activity) mContext).getWindow()
                .getAttributes();
        lp.alpha = bgAlpha;
        ((Activity) mContext).getWindow().setAttributes(lp);
    }

}


