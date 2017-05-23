package com.xmd.manager.window;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.crazyman.library.PermissionTool;
import com.xmd.manager.R;
import com.xmd.manager.beans.BadComment;
import com.xmd.manager.chat.EmchatUserHelper;
import com.xmd.manager.common.ResourceUtils;
import com.xmd.manager.common.ReturnVisitMenu;
import com.xmd.manager.common.Utils;
import com.xmd.manager.msgctrl.MsgDef;
import com.xmd.manager.msgctrl.MsgDispatcher;
import com.xmd.manager.msgctrl.RxBus;
import com.xmd.manager.service.RequestConstant;
import com.xmd.manager.service.response.BadCommentListResult;
import com.xmd.manager.service.response.ChangeStatusResult;
import com.xmd.manager.widget.BottomPopupWindow;

import java.util.HashMap;
import java.util.Map;

import rx.Subscription;

import static android.app.Activity.RESULT_OK;

/**
 * Created by lhj on 2016/11/18.
 */
public class HadReturnFragment extends BaseListFragment<BadComment> {

    private Subscription mGetWaitingReturnVisitCommentsSubscription;
    private Subscription mBadCommentStatusSubscription;
    private View view;
    private String callPhone;
    private static final int REQUEST_CODE_PHONE = 0x0066;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_had_visited, container, false);
        mGetWaitingReturnVisitCommentsSubscription = RxBus.getInstance().toObservable(BadCommentListResult.class).subscribe(
                badComment -> handleBadCommentList(badComment));
        return view;
    }

    @Override
    protected void dispatchRequest() {
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_PAGE, String.valueOf(mPages));
        params.put(RequestConstant.KEY_PAGE_SIZE, String.valueOf(PAGE_SIZE));
        params.put(RequestConstant.KEY_COMMENT_STATUS, RequestConstant.FINISH_COMMENT);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_BAD_COMMENT_LIST, params);
    }

    @Override
    protected void initView() {
        mBadCommentStatusSubscription = RxBus.getInstance().toObservable(ChangeStatusResult.class).subscribe(
                changeStatusResult -> {
                    if (changeStatusResult.statusCode == 200) {
                        Utils.makeShortToast(getActivity(), changeStatusResult.msg);
                        onRefresh();
                    } else {
                        Utils.makeShortToast(getActivity(), changeStatusResult.msg);
                    }
                });
    }


    private void handleBadCommentList(BadCommentListResult badComment) {
        if (badComment.statusCode == 200 && badComment.commentState.equals(RequestConstant.FINISH_COMMENT)) {
            onGetListSucceeded(badComment.pageCount, badComment.respData);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unsubscribe(mGetWaitingReturnVisitCommentsSubscription, mBadCommentStatusSubscription);
    }

    @Override
    public void onItemClicked(BadComment bean) {
        super.onItemClicked(bean);
        Intent intent = new Intent(getActivity(), BadCommentDetailActivity.class);
        if (bean.status.equals(RequestConstant.DELETE_COMMENT)) {
            intent.putExtra(RequestConstant.KEY_IS_DELETED, true);
        } else {
            intent.putExtra(RequestConstant.KEY_IS_DELETED, false);
        }
        intent.putExtra(RequestConstant.KEY_COMMENT_ID, bean.id);
        startActivity(intent);
    }

    @Override
    public void onPositiveButtonClicked(BadComment bean) {
        super.onPositiveButtonClicked(bean);
        showServiceOutMenu(bean.phoneNum, bean.userEmchatId, bean.userName, bean.avatarUrl, bean.id, bean.returnStatus);
    }

    @Override
    public void onNegativeButtonClicked(BadComment bean) {
        super.onNegativeButtonClicked(bean);
        doRemarkComment(bean.id, RequestConstant.DELETE_COMMENT);
    }

    private void showServiceOutMenu(String phone, String emChatId, String userName, String userHeadImgUrl, String commentId, String returnStatus) {
        BottomPopupWindow popupWindow = BottomPopupWindow.getInstance(getActivity(), phone, emChatId, commentId, returnStatus, new BottomPopupWindow.OnRootSelectedListener() {
            @Override
            public void onItemSelected(ReturnVisitMenu rootMenu) {
                switch (rootMenu.getType()) {
                    case 1:
                        PermissionTool.requestPermission(HadReturnFragment.this, new String[]{Manifest.permission.CALL_PHONE}, new String[]{"拨打电话"}, REQUEST_CODE_PHONE);
                        break;
                    case 2:
                        EmchatUserHelper.startToChat(emChatId, userName, userHeadImgUrl);
                        break;
                    case 3:
                        doRemarkComment(commentId, returnStatus);
                        break;
                    case 4:
                        doRemarkComment(commentId, returnStatus);
                        break;

                }
            }
        });
        popupWindow.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PHONE) {
            if (resultCode == RESULT_OK) {
                callPhone = ResourceUtils.getString(R.string.service_phone);
                if (Utils.isNotEmpty(callPhone)) {
                    toCallPhone(callPhone);
                } else {
                    Utils.makeShortToast(getActivity(), ResourceUtils.getString(R.string.phone_is_null));
                }
            } else {
                Toast.makeText(getActivity(), "获取权限失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void toCallPhone(String servicePhone) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        Uri data = Uri.parse("tel:" + servicePhone);
        intent.setData(data);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        startActivity(intent);
    }


    private void doRemarkComment(String commentId, String returnStatus) {
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_BAD_COMMENT_ID, commentId);
        if (returnStatus.equals("N")) {
            params.put(RequestConstant.KEY_COMMENT_STATUS, RequestConstant.FINISH_COMMENT);
        } else if (returnStatus.equals("Y")) {
            params.put(RequestConstant.KEY_COMMENT_STATUS, RequestConstant.VALID_COMMENT);
        } else {
            params.put(RequestConstant.KEY_COMMENT_STATUS, RequestConstant.DELETE_COMMENT);
        }
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_CHANGE_COMMENT_STATUS, params);
    }
}
