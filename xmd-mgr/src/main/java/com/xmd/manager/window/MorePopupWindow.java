package com.xmd.manager.window;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.xmd.manager.R;
import com.xmd.manager.common.ResourceUtils;
import com.xmd.manager.common.Utils;
import com.xmd.manager.msgctrl.MsgDef;
import com.xmd.manager.msgctrl.MsgDispatcher;
import com.xmd.manager.widget.AlertDialogBuilder;
import com.xmd.manager.widget.BasePopupWindow;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by sdcm on 15-11-20.
 */
public class MorePopupWindow extends BasePopupWindow {

    @Bind(R.id.about_us)
    Button mBtnAboutUs;
    @Bind(R.id.logout)
    Button mBtnLogout;
    @Bind(R.id.setting)
    Button mBtnSetting;

    protected MorePopupWindow(View parentView) {
        super(parentView, null);

        int[] sizes = Utils.getScreenWidthHeight(mActivity);
        int screenWidth = sizes[0];

        View popupView = LayoutInflater.from(mActivity).inflate(R.layout.pw_more, null);
        initPopupWidnow(popupView, screenWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
    }


    @OnClick({R.id.about_us, R.id.logout, R.id.setting})
    public void onClick(View v) {
        Intent intent = null;
        if (v == mBtnAboutUs) {
            intent = new Intent(mActivity, AboutUsActivity.class);
        } else if (v == mBtnSetting) {
            intent = new Intent(mActivity, SettingActivity.class);
        } else if (v == mBtnLogout) {
            doLogout();
        }

        if (intent != null) {
            mActivity.startActivity(intent);
        }
        dismiss();
    }

    private void doLogout() {
        new AlertDialogBuilder(mActivity)
                .setMessage(ResourceUtils.getString(R.string.logout_confirm_message))
                .setPositiveButton(ResourceUtils.getString(R.string.btn_confirm), v -> doInnerLogout())
                .setNegativeButton(ResourceUtils.getString(R.string.btn_back), null)
                .show();
    }

    private void doInnerLogout() {
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GETUI_UNBIND_CLIENT_ID);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_LOGOUT);
    }
}
