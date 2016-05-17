package com.xmd.technician.widget;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.xmd.technician.R;
import com.xmd.technician.TechApplication;
import com.xmd.technician.bean.Entry;
import com.xmd.technician.common.ResourceUtils;

import java.util.List;

import butterknife.Bind;

/**
 * Created by linms@xiaomodo.com on 16-5-5.
 */
public class ListPopupWindow<T> extends BasePopupWindow {

    public interface Callback<T> {
        void onPopupWindowItemClicked(T bean);
    }

    @Bind(R.id.ll_popup_window) LinearLayout mContainer;

    public ListPopupWindow(View parentView, List<T> data, Callback<T> callback) {
        super(parentView, null);
        View popupView = LayoutInflater.from(TechApplication.getAppContext()).inflate(R.layout.pw_list, null);
        initPopupWidnow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setBackgroundDrawable(ResourceUtils.getDrawable(R.drawable.pw_bg));
        setAnimationStyle(R.style.anim_top_to_bottom_style);
        createContent(data, callback);
    }

    private void createContent(List<T> data, Callback<T> callback) {
        if (data.size() == 0) {
            return;
        }

        if (data.get(0) instanceof Entry) {
            for (T obj : data) {
                Entry entry = (Entry) obj;
                Button item = new Button(TechApplication.getAppContext());
                item.setText(entry.value);
                item.setMinWidth(ResourceUtils.getDimenInt(R.dimen.popup_window_button_min_width));
                item.setBackground(ResourceUtils.getDrawable(R.drawable.selector_popup_window_button_bg));
                item.setTextColor(ResourceUtils.getColor(R.color.selector_popup_window_button_text));
                item.setTextSize(TypedValue.COMPLEX_UNIT_PX, ResourceUtils.getDimenInt(R.dimen.popup_window_button_text_size));
                item.setOnClickListener(
                        v -> {
                            callback.onPopupWindowItemClicked(obj);
                            dismiss();
                        }
                );
                int lineWidth = ResourceUtils.getDimenInt(R.dimen.line_width);
                mContainer.addView(item, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                View line = new View(TechApplication.getAppContext());
                line.setBackgroundColor(ResourceUtils.getColor(R.color.list_item_line_color));
                LinearLayout.LayoutParams lineParams = new LinearLayout.LayoutParams(
                        ResourceUtils.getDimenInt(R.dimen.popup_window_button_min_width),
                        lineWidth);
                lineParams.setMargins(lineWidth, 0, lineWidth, 0);
                mContainer.addView(line, lineParams);
            }
        }

    }
}
