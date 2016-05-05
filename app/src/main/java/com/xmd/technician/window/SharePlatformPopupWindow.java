package com.xmd.technician.window;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xmd.technician.R;
import com.xmd.technician.common.Utils;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.widget.BasePopupWindow;

import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by sdcm on 15-12-11.
 */
public class SharePlatformPopupWindow extends BasePopupWindow {

    @Bind(R.id.share_to_friend) TextView mBtnShareToFriend;
    @Bind(R.id.share_to_timeline) TextView mBtnShareToTimeline;
    private Map<String, Object> params;

    public SharePlatformPopupWindow(Map<String, String> params) {
        super(null, params);

        int[] sizes = Utils.getScreenWidthHeight(mActivity);
        int screenWidth = sizes[0];

        View popupView = LayoutInflater.from(mActivity).inflate(R.layout.pw_share, null);
        initPopupWidnow(popupView, screenWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @OnClick({R.id.share_to_timeline, R.id.share_to_friend, R.id.share_to_others})
    public void onClick(View v) {
        if (v.getId() == R.id.share_to_friend) {
            MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_SHARE_TO_FRIEND, mParams);
        } else if (v.getId() == R.id.share_to_timeline) {
            MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_SHARE_TO_TIMELINE, mParams);
        } else if (v.getId() == R.id.share_to_others) {
            MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_SHARE_TO_OTHER, mParams);
        }
        dismiss();
    }
}
