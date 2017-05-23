package com.xmd.cashier.widget;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.xmd.cashier.R;

import java.util.ArrayList;

/**
 * Created by zr on 17-5-11.
 */

public class ActionSheetDialog extends Dialog implements AdapterView.OnItemClickListener, View.OnClickListener {
    private SheetArrayAdapter mSheetAdapter;
    private ListView mSheetListView;
    private TextView mCancelTextView;
    private OnEventListener mListener;
    private ArrayList<String> mListArray = new ArrayList<>();

    public ActionSheetDialog(@NonNull Context context) {
        super(context, R.style.SheetActionDialogTheme);
        initViews();
        initWindow();
    }

    private class SheetArrayAdapter extends ArrayAdapter<String> {
        private LayoutInflater mLayoutInflater = null;

        public SheetArrayAdapter(Context context, ArrayList<String> items) {
            super(context, 0, items);
            mLayoutInflater = LayoutInflater.from(context);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = mLayoutInflater.inflate(R.layout.item_sheet_dialog, parent, false);
            }
            String item = getItem(position);
            TextView textContent = (TextView) convertView.findViewById(R.id.tv_item);
            textContent.setText(item);
            return convertView;
        }
    }

    public interface OnEventListener {
        void onActionItemClick(ActionSheetDialog dialog, String item, int position);

        void onCancelItemClick(ActionSheetDialog dialog);
    }

    public void setEventListener(OnEventListener listener) {
        mListener = listener;
    }

    public void setCancelText(String text) {
        mCancelTextView.setText(text);
    }

    public void setContents(String[] titles) {
        mListArray.clear();
        for (String title : titles) {
            mListArray.add(title);
        }
        mSheetAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        if (v == mCancelTextView) {
            if (mListener != null) {
                mListener.onCancelItemClick(this);
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mListener != null) {
            mListener.onActionItemClick(this, mListArray.get(position), position);
        }
    }

    private void initViews() {
        final LayoutInflater inflater = LayoutInflater.from(getContext());
        final View viewContent = inflater.inflate(R.layout.layout_sheet_dialog, null);
        setContentView(viewContent);

        // Adapter
        mSheetAdapter = new SheetArrayAdapter(getContext(), mListArray);

        // List
        mSheetListView = (ListView) viewContent.findViewById(R.id.lv_sheet_contents);
        mSheetListView.setAdapter(mSheetAdapter);
        mSheetListView.setOnItemClickListener(this);

        // Cancel
        mCancelTextView = (TextView) viewContent.findViewById(R.id.tv_sheet_cancel);
        mCancelTextView.setOnClickListener(this);
    }

    private void initWindow() {
        final Window window = getWindow();
        final WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.width = getContext().getResources().getDisplayMetrics().widthPixels;
        window.setAttributes(layoutParams);
        window.setGravity(Gravity.BOTTOM);
    }
}
