package com.xmd.app.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.xmd.app.R;

/**
 * Created by Lhj on 17-7-8.
 */

public class DropDownMenuDialog extends PopupWindow implements View.OnClickListener {

    private Context context;
    private LayoutInflater inflater;
    private String[] items;
    private ViewGroup contentView;
    private OnItemClickListener mOnItemClickListener;




    public interface OnItemClickListener {
        void onItemClick(int index);
    }

    public static DropDownMenuDialog getDropDownMenuDialog(Context context, String[] items, OnItemClickListener onItemClickListener) {
        LinearLayout contentView = new LinearLayout(context);
        contentView.setOrientation(LinearLayout.VERTICAL);
        return new DropDownMenuDialog(contentView, items, onItemClickListener);
    }

    private DropDownMenuDialog(ViewGroup contentView, String[] items, OnItemClickListener onItemClickListener) {
        super(contentView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        this.context = contentView.getContext();
        this.inflater = LayoutInflater.from(context);
        this.items = items;
        this.contentView = contentView;
        this.mOnItemClickListener = onItemClickListener;
        super.setOutsideTouchable(true);
        super.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#f0f0f0")));
        initViews();

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

    public void show(View view) {
        showAsDropDown(view, 0, 0);
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick((Integer) v.getTag());
        }
        dismiss();
    }
}
