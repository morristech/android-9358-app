package com.xmd.technician.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.xmd.technician.R;

/**
 * Created by Lhj on 17-5-27.
 */

public class BottomContactFilterPopupWindow {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private View view;
    private PopupWindow mPopupWindow;
    private ContactFilterListener mFilterListener;
    private View tagView;

    public BottomContactFilterPopupWindow(Context context, View tagView) {
        this.mContext = context;
        this.tagView = tagView;

    }

    public void show() {
        initView();
    }

    public void dismiss() {
        mPopupWindow.dismiss();
    }

    public void setContactFilterListener(ContactFilterListener filterListener) {
        this.mFilterListener = filterListener;
    }

    public interface ContactFilterListener {
        void contactFilter(int selectedType);
    }

    private void initView() {
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = mLayoutInflater.inflate(R.layout.search_contacts_layout, null);
        Activity activity = (Activity) mContext;
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.alpha = 0.5f;
        lp.dimAmount = 0.5f;
        activity.getWindow().setAttributes(lp);
        mPopupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        mPopupWindow.setAnimationStyle(R.style.popup_window_style);
        ColorDrawable dw = new ColorDrawable(Color.parseColor("#00FF0000"));
        mPopupWindow.setBackgroundDrawable(dw);
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
                lp.alpha = 1.0f;
                lp.dimAmount = 1.0f;
                activity.getWindow().setAttributes(lp);
            }
        });
        LinearLayout allContact = (LinearLayout) view.findViewById(R.id.all_contact);
        LinearLayout phoneContact = (LinearLayout) view.findViewById(R.id.phone_contact);
        LinearLayout fansContact = (LinearLayout) view.findViewById(R.id.fans_contact);
        LinearLayout wxContact = (LinearLayout) view.findViewById(R.id.wx_contact);
        LinearLayout fansAndWx = (LinearLayout) view.findViewById(R.id.fans_and_wx_contact);
        TextView cancel = (TextView) view.findViewById(R.id.cancel_contact);
        if (mFilterListener != null) {
            allContact.setOnClickListener(v -> {
                mFilterListener.contactFilter(0);
                mPopupWindow.dismiss();
            });
            wxContact.setOnClickListener(v -> {
                mFilterListener.contactFilter(1);
                mPopupWindow.dismiss();
            });
            fansContact.setOnClickListener(v -> {
                mFilterListener.contactFilter(2);
                mPopupWindow.dismiss();
            });
            fansAndWx.setOnClickListener(v -> {
                mFilterListener.contactFilter(3);
                mPopupWindow.dismiss();
            });
            phoneContact.setOnClickListener(v -> {
                mFilterListener.contactFilter(4);
                mPopupWindow.dismiss();
            });

        }
        cancel.setOnClickListener(v -> {
            mPopupWindow.dismiss();
        });
        try {
            if (mPopupWindow != null) {
                mPopupWindow.showAtLocation(tagView, Gravity.BOTTOM, 0, 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
