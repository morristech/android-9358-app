package com.xmd.technician.window;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xmd.technician.Constant;
import com.xmd.technician.R;
import com.xmd.technician.common.Utils;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.widget.BasePopupWindow;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by sdcm on 15-12-11.
 */
public class SharePlatformPopupWindow extends BasePopupWindow {

    @BindView(R.id.share_to_friend)
    TextView mBtnShareToFriend;
    @BindView(R.id.share_to_timeline)
    TextView mBtnShareToTimeline;
    TextView mBtnShareToOthers;
    private Map<String, Object> mParams;

    public SharePlatformPopupWindow(Map<String, String> params) {
        super(null, params);
        mParams = new HashMap<>();
        int[] sizes = Utils.getScreenWidthHeight(mActivity);
        int screenWidth = sizes[0];
        mParams.putAll(params);
        View popupView = LayoutInflater.from(mActivity).inflate(R.layout.pw_share, null);
        mBtnShareToOthers = (TextView) popupView.findViewById(R.id.share_to_others);
        if (Utils.isEmpty((String) mParams.get(Constant.PARAM_SHARE_LOCAL_IMAGE))) {
            mBtnShareToOthers.setVisibility(View.VISIBLE);
        } else {
            mBtnShareToOthers.setVisibility(View.GONE);
        }
        initPopupWindow(popupView, screenWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
    }


    @OnClick({R.id.share_to_timeline, R.id.share_to_friend, R.id.share_to_others})
    public void onClick(View v) {
        if (v.getId() == R.id.share_to_friend) {
            if (Utils.isEmpty((String) mParams.get(Constant.PARAM_SHARE_LOCAL_IMAGE))) {
                MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_SHARE_TO_FRIEND, mParams);
            } else {
                MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_SHARE_IMAGE_TO_FRIENDS, mParams);
            }
        } else if (v.getId() == R.id.share_to_timeline) {
            if (Utils.isEmpty((String) mParams.get(Constant.PARAM_SHARE_LOCAL_IMAGE))) {
                MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_SHARE_TO_TIMELINE, mParams);
            } else {
                MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_SHARE_IMAGE_TO_TIMELINE, mParams);
            }
        } else if (v.getId() == R.id.share_to_others) {
            MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_SHARE_TO_OTHER, mParams);
        }
        dismiss();
    }
}
