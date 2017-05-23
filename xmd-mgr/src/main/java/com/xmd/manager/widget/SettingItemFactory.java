package com.xmd.manager.widget;

import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.xmd.manager.R;
import com.xmd.manager.beans.SettingItemInfo;
import com.xmd.manager.common.ResourceUtils;
import com.xmd.manager.common.Utils;

import java.util.List;

/**
 * Created by sdcm on 16-1-22.
 */
public class SettingItemFactory {

    private Context mContext;
    private ISettingItem mSettingItem;
    private LinearLayout mContainer;
    private List<SettingItemInfo> mSettingItemInfoList;

    public SettingItemFactory(Context context, LinearLayout container, List<SettingItemInfo> settingItemInfoList) {
        mContext = context;
        mContainer = container;
        mSettingItemInfoList = settingItemInfoList;
    }

    public void setSettingItemImpl(ISettingItem iSettingItem) {
        mSettingItem = iSettingItem;
    }

    private Context getContext() {
        return mContext;
    }

    public View createSettingItem(SettingItemInfo settingItemInfo) {

        switch (settingItemInfo.type) {
            case SettingItemInfo.TYPE_SPACE:
                return createSpaceItem();
            case SettingItemInfo.TYPE_DESCRIPTOIN:
                return createDescriptionItem(settingItemInfo);
            case SettingItemInfo.TYPE_SWITCH:
                return createSwitchItem(settingItemInfo);
            case SettingItemInfo.TYPE_LINE:
                return createLineItem();
            default:
                return null;
        }
    }

    private View createLineItem() {
        View view = new View(getContext());
        view.setBackgroundDrawable(ResourceUtils.getDrawable(R.color.dialog_line_gray));
        return view;
    }

    private View createSpaceItem() {
        View view = new View(getContext());
        return view;
    }

    private View createDescriptionItem(SettingItemInfo settingItemInfo) {
        TextView view = new TextView(getContext());
        view.setBackgroundColor(ResourceUtils.getColor(R.color.white));
        view.setHeight(ResourceUtils.getDimenInt(R.dimen.setting_activity_item_height));
        view.setText(settingItemInfo.title);
        view.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
        view.setTextSize(TypedValue.COMPLEX_UNIT_PX, ResourceUtils.getDimenInt(R.dimen.setting_activity_description_text_size));
        return view;
    }

    private View createSwitchItem(SettingItemInfo settingItemInfo) {
        RelativeLayout layout = new RelativeLayout(getContext());
        layout.setBackgroundColor(ResourceUtils.getColor(R.color.white));

        TextView aLabel = new TextView(getContext());
        aLabel.setText(settingItemInfo.title);
        aLabel.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
        aLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX, ResourceUtils.getDimenInt(R.dimen.setting_activity_item_label_text_size));

        RelativeLayout.LayoutParams params = createWCMPLayoutParams();
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        params.addRule(RelativeLayout.CENTER_VERTICAL);

        layout.addView(aLabel, params);

        Switch aSwitch = new Switch(getContext());
        aSwitch.setTextOff(Utils.isNotEmpty(settingItemInfo.textOff) ? settingItemInfo.textOff : ResourceUtils.getString(R.string.switch_off));
        aSwitch.setTextOn(Utils.isNotEmpty(settingItemInfo.textOn) ? settingItemInfo.textOn : ResourceUtils.getString(R.string.switch_on));
        aSwitch.setChecked((Boolean) mSettingItem.getValue(settingItemInfo.settingKey));
        aSwitch.setOnCheckedChangeListener(((buttonView, isChecked) -> mSettingItem.setValue(settingItemInfo.settingKey, isChecked)));

        params = createWCMPLayoutParams();
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params.addRule(RelativeLayout.CENTER_VERTICAL);

        layout.addView(aSwitch, params);

        return layout;
    }

    private RelativeLayout.LayoutParams createWCMPLayoutParams() {
        return new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
    }

    public void build() {
        for (SettingItemInfo settingItemInfo : mSettingItemInfoList) {
            View view = createSettingItem(settingItemInfo);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    ResourceUtils.getDimenInt(R.dimen.setting_activity_item_height));
            if (settingItemInfo.type == SettingItemInfo.TYPE_LINE) {
                layoutParams.height = 1;
            } else {
                int padding = ResourceUtils.getDimenInt(R.dimen.inter_space);
                view.setPadding(padding, padding, padding, padding);
            }
            mContainer.addView(view, layoutParams);

        }
    }

}
