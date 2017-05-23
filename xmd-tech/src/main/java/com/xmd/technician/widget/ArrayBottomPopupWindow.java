package com.xmd.technician.widget;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.xmd.technician.R;
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.common.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.Bind;

/**
 * Created by sdcm on 16-4-15.
 */
public class ArrayBottomPopupWindow<T> extends BasePopupWindow {

    @Bind(R.id.popup_window_list)
    ListView mView;

    private FiltrateAdapter mFiltrateAdapter;
    private List<String> mListDate;
    public String selectedItem;
    private AdapterView.OnItemClickListener mItemClickListener;

    public ArrayBottomPopupWindow(View parentView, Map<String, String> params, int windowWidth) {
        super(parentView, params);
        mListDate = new ArrayList<>();
        View popupView = LayoutInflater.from(mActivity).inflate(R.layout.list_pop_window, null);
        initPopupWindow(popupView, windowWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
        setAnimationStyle(R.style.anim_top_to_bottom_style);
        mPopupWindow.setBackgroundDrawable(ResourceUtils.getDrawable(R.drawable.filter_order));
        mFiltrateAdapter = new FiltrateAdapter();
        mView.setAdapter(mFiltrateAdapter);
        mFiltrateAdapter.notifyDataSetChanged();
        mView.setOnItemClickListener((parent, view, position, id) -> {
            onDismiss();
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(parent, view, position, id);
            }
        });
    }


    public void setDataSet(List<String> info) {
        mListDate.clear();
        mListDate.addAll(info);
    }

    public void setDataSet(List<String> info, String currentString) {
        selectedItem = currentString;
        mListDate.clear();
        mListDate.addAll(info);
    }

    public void setItemClickListener(AdapterView.OnItemClickListener listener) {
        mItemClickListener = listener;
    }

    @Override
    public void onDismiss() {
        this.dismiss();
    }

    private class FiltrateAdapter extends BaseAdapter {


        @Override
        public int getCount() {
            return mListDate.size();
        }

        @Override
        public Object getItem(int position) {
            return mListDate.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View view = LayoutInflater.from(mActivity).inflate(R.layout.array_popup_window_item, null);
            TextView textView = (TextView) view.findViewById(R.id.tv_item);
            textView.setText(mListDate.get(position));
          /*  if (Utils.isNotEmpty(selectedItem) && selectedItem.equals(mListDate.get(position))) {
                textView.setTextColor(ResourceUtils.getColor(R.color.primary_color));
            }*/
            if(selectedItem.equals(mListDate.get(position))){
                textView.setSelected(true);
            }else {
                textView.setSelected(false);
            }
            return view;
        }
    }
}
