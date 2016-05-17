package com.xmd.technician.widget;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.xmd.technician.R;
import com.xmd.technician.common.ResourceUtils;

import java.util.List;
import java.util.Map;

import butterknife.Bind;

/**
 * Created by sdcm on 16-4-15.
 */
public class ArrayBottomPopupWindow<T> extends BasePopupWindow {

    @Bind(R.id.popup_window_list) ListView mView;

    private ArrayAdapter mArrayAdapter;
    private AdapterView.OnItemClickListener mItemClickListener;

    public ArrayBottomPopupWindow(View parentView, Map<String, String> params, int windowWidth) {
        super(parentView, params);
        View popupView = LayoutInflater.from(mActivity).inflate(R.layout.list_pop_window, null);
        initPopupWidnow(popupView, windowWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
        setAnimationStyle(R.style.anim_bottom_to_top_style);

        mPopupWindow.setBackgroundDrawable(ResourceUtils.getDrawable(R.drawable.common_bg));

        mArrayAdapter = new ArrayAdapter(mActivity,R.layout.array_popup_window_item, R.id.tv_item);
        mView.setAdapter(mArrayAdapter);
        mView.setOnItemClickListener((parent, view, position, id) -> {
            dismiss();
            if(mItemClickListener != null){
                mItemClickListener.onItemClick(parent, view, position, id);
            }
        });
    }

    public void setDataSet(List<T> info){
        mArrayAdapter.clear();
        mArrayAdapter.addAll(info);
    }

    public void showAsAboveCenter() {
        super.showAsAbove(mParentView.getWidth()/2, 0);
    }

    public void showAsAboveLeft() {
        super.showAsAbove(mPopupWindow.getWidth()/4, 0);
    }

    public void setItemClickListener(AdapterView.OnItemClickListener listener){
        mItemClickListener = listener;
    }
}
