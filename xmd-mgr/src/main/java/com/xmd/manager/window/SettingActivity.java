package com.xmd.manager.window;

import android.os.Bundle;
import android.widget.LinearLayout;

import com.xmd.manager.R;
import com.xmd.manager.SettingFlags;
import com.xmd.manager.beans.SettingItemInfo;
import com.xmd.manager.common.ResourceUtils;
import com.xmd.manager.widget.ISettingItem;
import com.xmd.manager.widget.SettingItemFactory;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by sdcm on 16-1-22.
 */
public class SettingActivity extends BaseActivity implements ISettingItem {

    @BindView(R.id.setting_list)
    LinearLayout mSettingList;

    private List<SettingItemInfo> mSettingItemList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_setting);

        setLeftVisible(true, R.drawable.actionbar_back);

        initSettingItems();

        setupViews();
    }

    private void initSettingItems() {

        mSettingItemList.add(new SettingItemInfo(SettingItemInfo.TYPE_LINE));

        SettingItemInfo normalDesc = new SettingItemInfo(SettingItemInfo.TYPE_DESCRIPTOIN, ResourceUtils.getString(R.string.setting_activity_group_normal));
        mSettingItemList.add(normalDesc);

        mSettingItemList.add(new SettingItemInfo(SettingItemInfo.TYPE_LINE));

        SettingItemInfo orderNotification = new SettingItemInfo(SettingItemInfo.TYPE_SWITCH,
                ResourceUtils.getString(R.string.setting_activity_order_notification),
                SettingFlags.ORDER_NOTIFIATION_ON);
        mSettingItemList.add(orderNotification);
        mSettingItemList.add(new SettingItemInfo(SettingItemInfo.TYPE_LINE));
    }

    private void setupViews() {
        SettingItemFactory factory = new SettingItemFactory(this, mSettingList, mSettingItemList);
        factory.setSettingItemImpl(this);
        factory.build();
    }

    @Override
    public void setValue(String key, Object value) {
        switch (key) {
            case SettingFlags.ORDER_NOTIFIATION_ON:
                boolean isOn = (boolean) value;
                SettingFlags.setBoolean(key, isOn);
                //FIXME
//                MsgDispatcher.dispatchMessage(isOn ? MsgDef.MSG_DEF_GETUI_BIND_CLIENT_ID : MsgDef.MSG_DEF_GETUI_UNBIND_CLIENT_ID);
                break;
        }
    }

    @Override
    public Object getValue(String key) {
        switch (key) {
            case SettingFlags.ORDER_NOTIFIATION_ON:
                return SettingFlags.getBoolean(key);
        }
        return null;
    }
}
