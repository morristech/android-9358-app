package com.xmd.technician.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.xmd.technician.R;
import com.xmd.technician.common.ResourceUtils;

/**
 * Created by Administrator on 2016/7/8.
 */
public class DropDownMenuDialog extends PopupWindow implements View.OnClickListener {
    private static final int TYPE_DOWN = 1;
    private static final int TYPE_UP = 2;
    private int type;
    private Context context;
    private LayoutInflater inflater;
    private String[] items;
    private ViewGroup contentView;
    private OnItemClickListener mOnItemClickListener;

    private DropDownMenuDialog(ViewGroup contentView, String[] items, OnItemClickListener onItemClickListener, int type) {
        super(contentView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        this.context = contentView.getContext();
        this.inflater = LayoutInflater.from(context);
        this.items = items;
        this.contentView = contentView;
        this.mOnItemClickListener = onItemClickListener;
        super.setOutsideTouchable(true);
        this.type = type;
        switch (type) {
            case TYPE_DOWN:
                super.setBackgroundDrawable(new ColorDrawable(ResourceUtils.getColor(R.color.side_bar_text)));
                initViews();
                break;
            case TYPE_UP:
                super.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                initViews2();
                break;
        }
    }

    public static DropDownMenuDialog getDropDownMenuDialog(Context context, String[] items, OnItemClickListener onItemClickListener) {
        return getDropDownMenuDialog(context, items, onItemClickListener, TYPE_DOWN);
    }

    public static DropDownMenuDialog getDropDownMenuDialog2(Context context, String[] items, OnItemClickListener onItemClickListener) {
        return getDropDownMenuDialog(context, items, onItemClickListener, TYPE_UP);
    }

    private static DropDownMenuDialog getDropDownMenuDialog(Context context, String[] items, OnItemClickListener onItemClickListener, int type) {
        LinearLayout contentView = new LinearLayout(context);
        contentView.setOrientation(LinearLayout.VERTICAL);
        return new DropDownMenuDialog(contentView, items, onItemClickListener, type);
    }

    private void initViews() {
        if (items == null) {
            return;
        }
        for (int i = 0; i < items.length; i++) {
            String item = items[i];
            TextView textView = (TextView) inflater.inflate(R.layout.drop_down_menu_item, contentView, false);
            textView.setText(item);
            textView.setTag(i);
            textView.setOnClickListener(this);
            contentView.addView(textView);
            if (i != items.length - 1)
                inflater.inflate(R.layout.divider_horizontal, contentView);
        }
    }

    private void initViews2() {
        ViewGroup container = (ViewGroup) inflater.inflate(R.layout.drop_down_menu_container, contentView, false);
        contentView.addView(container);
        container = (ViewGroup) container.findViewById(R.id.container);
        for (int i = 0; i < items.length; i++) {
            String item = items[i];
            TextView textView = (TextView) inflater.inflate(R.layout.drop_down_menu_item2, container, false);
            textView.setText(item);
            textView.setTag(i);
            textView.setOnClickListener(this);
            container.addView(textView);
            if (i != items.length - 1) {
                View divider = inflater.inflate(R.layout.divider_vertical, container, false);
                divider.setBackgroundColor(context.getResources().getColor(R.color.color_main_title));
                container.addView(divider);
            }
        }
    }

    public void show(View view) {
        showAsDropDown(view, 0, 10);
    }

    public void showPopUp(View v) {
        int[] location = new int[2];
        v.getLocationOnScreen(location);
        contentView.measure(0, 0);
        showAtLocation(v, Gravity.CENTER_HORIZONTAL | Gravity.TOP, location[0], location[1] - contentView.getMeasuredHeight());
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick((Integer) v.getTag());
        }
        dismiss();
    }

    public interface OnItemClickListener {
        void onItemClick(int index);
    }


}

