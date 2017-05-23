package com.xmd.cashier.widget;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.xmd.cashier.R;

import java.util.List;
import java.util.Map;

/**
 * Created by zr on 16-11-29.
 * from com.xmd.manager
 */

public class ArrayPopupWindow<T> extends BasePopupWindow {
    private ListView mListView;
    private ArrayAdapter mArrayAdapter;
    private AdapterView.OnItemClickListener mItemClickListener;

    public ArrayPopupWindow(Activity activity, View parent, Map<String, String> params, int windowWidth, int animStyle, Drawable background, int itemLayout) {
        super(activity, parent, params);
        View popupView = LayoutInflater.from(activity).inflate(R.layout.list_popup_window, null);
        initPopupWindow(popupView, windowWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
        mListView = (ListView) popupView.findViewById(R.id.popup_window_list);

        // 自定义弹出动画
        setAnimationStyle(animStyle);
        // 自定义背景
        if (background != null) {
            setBackGround(background);
        }
        if (itemLayout != 0) {
            mArrayAdapter = new ArrayAdapter(mActivity, itemLayout);
        } else {
            mArrayAdapter = new ArrayAdapter(mActivity, R.layout.item_popup_window_normal);
        }
        mListView.setAdapter(mArrayAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onDismiss();
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClick(parent, view, position, id);
                }
            }
        });
    }

    // 设置数据集
    public void setDataSet(List<T> info) {
        mArrayAdapter.clear();
        mArrayAdapter.addAll(info);
    }

    // 设置item监听
    public void setItemClickListener(AdapterView.OnItemClickListener listener) {
        mItemClickListener = listener;
    }
}
