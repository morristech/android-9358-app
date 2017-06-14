package com.xmd.technician.window;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hyphenate.exceptions.HyphenateException;
import com.shidou.commonlibrary.helper.XLogger;
import com.shidou.commonlibrary.widget.XToast;
import com.xmd.app.user.User;
import com.xmd.app.user.UserInfoServiceImpl;
import com.xmd.technician.Constant;
import com.xmd.technician.R;
import com.xmd.technician.bean.RecentlyVisitorBean;
import com.xmd.technician.bean.SayHiVisitorResult;
import com.xmd.technician.bean.UserRecentBean;
import com.xmd.technician.chat.ChatConstant;
import com.xmd.technician.chat.ChatHelper;
import com.xmd.technician.common.CharacterParser;
import com.xmd.technician.common.Logger;
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.common.Utils;
import com.xmd.technician.http.RequestConstant;
import com.xmd.technician.http.gson.CustomerUserRecentListResult;
import com.xmd.technician.model.HelloSettingManager;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;
import com.xmd.technician.widget.EmptyView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscription;

/**
 * Created by Lhj on 17-5-27.
 */

public class ContactsVisitorsFragment extends BaseListFragment<UserRecentBean> {

    @Bind(R.id.contact_all_emptyView)
    EmptyView contactAllEmptyView;

    private Subscription mContactRecentUserListSubscription;
    private Subscription mSayHiVisitorResultSubscription;
    private List<UserRecentBean> mVisitors;
    private List<UserRecentBean> mFilterVisitors;
    private Map<String, String> mSayHiParams = new HashMap<>();
    private int position;
    private CharacterParser characterParser;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts_visiter, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void dispatchRequest() {
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_CLUB_CUSTOMER_RECENT_LIST);
    }

    @Override
    protected void initView() {
        mVisitors = new ArrayList<>();
        characterParser = CharacterParser.getInstance();
        mContactRecentUserListSubscription = RxBus.getInstance().toObservable(CustomerUserRecentListResult.class).subscribe(
                result -> handlerRecentUserList(result)
        );
        mSayHiVisitorResultSubscription = RxBus.getInstance().toObservable(SayHiVisitorResult.class).subscribe(
                result -> handlerSayHiVisitorResult(result)
        );
    }

    private void handlerRecentUserList(CustomerUserRecentListResult result) {

        if (result.statusCode == 200) {
            mVisitors.clear();
            if (result.respData.userList.size() > 0) {
                contactAllEmptyView.setStatus(EmptyView.Status.Gone);
                mVisitors.addAll(result.respData.userList);
                onGetListSucceeded(1, mVisitors);
                XLogger.d("userService", "update by recently visitor data");
                for (UserRecentBean visitor : result.respData.userList) {
                    User user = new User(visitor.userId);
                    user.setChatId(visitor.emchatId);
                    user.setName(visitor.name);
                    user.setMarkName(visitor.userNoteName);
                    user.setAvatar(visitor.avatarUrl);
                    UserInfoServiceImpl.getInstance().saveUser(user);
                }
            } else {
                contactAllEmptyView.setStatus(EmptyView.Status.Empty);
            }

        } else {
            contactAllEmptyView.setStatus(EmptyView.Status.Failed);
            onGetListFailed(result.msg);
        }

    }

    private void handlerSayHiVisitorResult(SayHiVisitorResult result) {
        if (result.statusCode == 200) {
            position = Integer.parseInt(result.position);
            if (position != -1) {
                mVisitors.get(position).canSayHello = "N";
                mListAdapter.notifyItemChanged(position);
            }

            // 环信打招呼
            // HelloSettingManager.getInstance().sendHelloTemplate(result.userName, result.userEmchatId, result.userAvatar, result.userType);
            HelloSettingManager.getInstance().sendHelloTemplate(UserInfoServiceImpl.getInstance().getUserByChatId(result.userEmchatId));
            Map<String, String> saveParams = new HashMap<>();
            saveParams.put(RequestConstant.KEY_FRIEND_CHAT_ID, result.userEmchatId);
            saveParams.put(RequestConstant.KEY_CHAT_MSG_ID, "");
            MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_SAVE_CHAT_TO_CHONTACT, saveParams);
        }

    }

    @Override
    public void onPositiveButtonClicked(UserRecentBean bean) {//感谢
        super.onPositiveButtonClicked(bean);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_START_CHAT, Utils.wrapChatParams(bean.emchatId,
                Utils.isNotEmpty(bean.userNoteName) ? bean.userNoteName : bean.name, bean.avatarUrl, ChatConstant.TO_CHAT_USER_TYPE_CUSTOMER));
    }

    @Override
    public void onNegativeButtonClicked(UserRecentBean bean) {//打招呼
        super.onNegativeButtonClicked(bean);
        sayHiRequest(bean.userId, Utils.isNotEmpty(bean.userNoteName) ? bean.userNoteName : bean.name, bean.avatarUrl, bean.customerType, bean.emchatId, String.valueOf(bean.intListPosition));
    }

    @Override
    public void onItemClicked(UserRecentBean bean) throws HyphenateException {
        super.onItemClicked(bean);
        if (Long.parseLong(bean.userId) > 0) {
            Intent intent = new Intent(getActivity(), ContactInformationDetailActivity.class);
            intent.putExtra(RequestConstant.KEY_USER_ID, bean.userId);
            intent.putExtra(RequestConstant.KEY_IS_MY_CUSTOMER, false);
            intent.putExtra(RequestConstant.KEY_CONTACT_TYPE, Constant.CONTACT_INFO_DETAIL_TYPE_CUSTOMER);
            startActivity(intent);
        } else {
            Utils.makeShortToast(getActivity(), ResourceUtils.getString(R.string.visitor_has_no_message));
        }
    }

    public void filterCustomer(String searchName) {
        if (Utils.isNotEmpty(searchName)) {
            if (mFilterVisitors == null) {
                mFilterVisitors = new ArrayList<>();
            } else {
                mFilterVisitors.clear();
            }
            for (UserRecentBean recentBean : mVisitors) {
                if ((recentBean.name.indexOf(searchName.toString())) != -1 || characterParser.getSelling(recentBean.name).startsWith(searchName.toString())) {
                    mFilterVisitors.add(recentBean);
                }
            }
            if (mFilterVisitors.size() > 0) {
                onGetListSucceeded(1, mFilterVisitors);
            } else {
                Utils.makeShortToast(getActivity(), "未查到相关联系人");
            }
        } else {
            onGetListSucceeded(1, mVisitors);
        }
    }

    @Override
    public boolean isPaged() {
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unsubscribe(mContactRecentUserListSubscription, mSayHiVisitorResultSubscription);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private void sayHiRequest(String userId, String userName, String avatarUrl, String userType, String userEmchatId, String position) {
        if (!ChatHelper.getInstance().isConnected()) {
            XToast.show("当前已经离线，请稍后再试!");
            return;
        }
        mSayHiParams.clear();
        mSayHiParams.put(RequestConstant.KEY_REQUEST_SAY_HI_TYPE, Constant.REQUEST_SAY_HI_TYPE_VISITOR);
        mSayHiParams.put(RequestConstant.KEY_NEW_CUSTOMER_ID, userId);
        mSayHiParams.put(RequestConstant.KEY_USERNAME, userName);
        mSayHiParams.put(RequestConstant.KEY_USER_AVATAR, avatarUrl);
        mSayHiParams.put(RequestConstant.KEY_USER_TYPE, userType);
        mSayHiParams.put(RequestConstant.KEY_GAME_USER_EMCHAT_ID, userEmchatId);
        mSayHiParams.put(ChatConstant.KEY_SAY_HI_POSITION, position);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_TECH_SAY_HELLO, mSayHiParams);
    }
}
