package com.xmd.technician.window;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.xmd.technician.R;
import com.xmd.technician.SharedPreferenceHelper;
import com.xmd.technician.bean.RecentlyVisitorBean;
import com.xmd.technician.bean.RecentlyVisitorResult;
import com.xmd.technician.bean.SayHiResult;
import com.xmd.technician.chat.ChatConstant;
import com.xmd.technician.chat.ChatUser;
import com.xmd.technician.chat.UserUtils;
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.common.Utils;
import com.xmd.technician.http.RequestConstant;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;

import java.util.HashMap;
import java.util.Map;

import rx.Subscription;


public class RecentlyVisitorFragment extends BaseListFragment<RecentlyVisitorBean> {

    private Map<String, String> params = new HashMap<>();
    private Subscription mGetRecentlyVisitorSubscription;
    private Subscription mSayHiResultSubscription;

    private String lastTime;
    private String friendUserId;
    private boolean isRefresh;
    private int pageCount = 1;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recently_visitor, container, false);
    }

    @Override
    protected void dispatchRequest() {
        isRefresh = false;
        params.clear();
        if (TextUtils.isEmpty(lastTime)) {
            lastTime = "";
        }
        params.put(RequestConstant.KEY_CUSTOMER_TYPE, "");
        params.put(RequestConstant.KEY_LAST_TIME, lastTime);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_RECENTLY_VISITOR, params);

    }

    @Override
    public void onResume() {
        super.onResume();
        mGetRecentlyVisitorSubscription = RxBus.getInstance().toObservable(RecentlyVisitorResult.class).subscribe(
                result -> handlerClubInfoList(result)
        );
    }

    @Override
    public void onPause() {
        super.onPause();
        RxBus.getInstance().unsubscribe(mGetRecentlyVisitorSubscription);
    }

    @Override
    protected void initView() {

        mSayHiResultSubscription = RxBus.getInstance().toObservable(SayHiResult.class).subscribe(
                result -> handlerSayHiResult(result)
        );
    }

    private void handlerSayHiResult(SayHiResult result) {

        if (result.statusCode == 200) {
            onRefresh();
            if (Utils.isNotEmpty(SharedPreferenceHelper.getSerialNo())) {
                sendGreetingTextMessage(String.format("客官您好，我是%s[%s]技师，希望能够为您服务，约我哟～", SharedPreferenceHelper.getUserName(), SharedPreferenceHelper.getSerialNo()), friendUserId);
            } else {
                sendGreetingTextMessage(String.format("客官您好，我是%s技师，希望能够为您服务，约我哟～", SharedPreferenceHelper.getUserName()), friendUserId);
            }
            Map<String, String> saveParams = new HashMap<>();
            saveParams.put(RequestConstant.KEY_FRIEND_CHAT_ID, friendUserId);
            MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_SAVE_CHAT_TO_CHONTACT, saveParams);

        }

    }

    private void handlerClubInfoList(RecentlyVisitorResult result) {
        if (result.statusCode == RequestConstant.RESP_ERROR_CODE_FOR_LOCAL) {
            onGetListFailed(result.msg);

        } else if(result.statusCode == 200){
            for (int i = 0; i < result.respData.size(); i++) {
                if (Utils.isNotEmpty(result.respData.get(i).emchatId)) {
                    ChatUser user;
                    user = new ChatUser(result.respData.get(i).emchatId);
                    user.setAvatar(result.respData.get(i).avatarUrl);
                    user.setNick(result.respData.get(i).userName);
                    UserUtils.saveUser(user);
                }

            }

            if(result.respData.size()>0){
                lastTime = String.valueOf(result.respData.get(result.respData.size() -1 ).createdAt);
            }

            if(isRefresh){
                onGetListSucceeded(-101, result.respData);
            }else{
                if(result.respData.size()==20){
                    pageCount++;
                }
                onGetListSucceeded(pageCount, result.respData);
            }

        }else{
            mSwipeRefreshLayout.setRefreshing(false);
        }

    }

    @Override
    public void onItemClicked(RecentlyVisitorBean bean) {
        if (Long.parseLong(bean.userId) > 0) {
            Intent intent = new Intent(getActivity(), ContactInformationDetailActivity.class);

            intent.putExtra(RequestConstant.KEY_USER_ID, bean.userId);
            intent.putExtra(RequestConstant.CONTACT_TYPE, bean.customerType);
            intent.putExtra(RequestConstant.KEY_TECH_NAME, bean.techName);
            intent.putExtra(RequestConstant.KEY_TECH_NO, bean.techSerialNo);
            intent.putExtra(RequestConstant.KEY_CONTACT_TYPE, RequestConstant.TYPE_CUSTOMER);
            intent.putExtra(RequestConstant.KEY_IS_MY_CUSTOMER, false);
            startActivity(intent);
        } else {
            Utils.makeShortToast(getActivity(), ResourceUtils.getString(R.string.visitor_has_no_message));
        }

    }

    @Override
    public void onSayHiButtonClicked(RecentlyVisitorBean bean) {
        super.onSayHiButtonClicked(bean);
        friendUserId = bean.emchatId;
        params.clear();
        params.put(RequestConstant.KEY_USER_ID, bean.userId);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_DO_SAY_HI, params);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onRefresh() {
        isRefresh = true;
        params.clear();
        if (!TextUtils.isEmpty(lastTime)) {
            lastTime = "";
        }
        pageCount = 0;
        params.put(RequestConstant.KEY_CUSTOMER_TYPE, "");
        params.put(RequestConstant.KEY_LAST_TIME, lastTime);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_RECENTLY_VISITOR, params);

    }

    private void sendGreetingTextMessage(String content, String mToChatUsername) {
        EMMessage message = EMMessage.createTxtSendMessage(content, mToChatUsername);
        sendMessage(message);
    }

    protected void sendMessage(EMMessage message) {
        message.setAttribute(ChatConstant.KEY_TECH_ID, SharedPreferenceHelper.getUserId());
        message.setAttribute(ChatConstant.KEY_NAME, SharedPreferenceHelper.getUserName());
        message.setAttribute(ChatConstant.KEY_HEADER, SharedPreferenceHelper.getUserAvatar());
        message.setAttribute(ChatConstant.KEY_TIME, String.valueOf(System.currentTimeMillis()));
        message.setAttribute(ChatConstant.KEY_SERIAL_NO, SharedPreferenceHelper.getSerialNo());
        //发送消息
        EMClient.getInstance().chatManager().sendMessage(message);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unsubscribe(mGetRecentlyVisitorSubscription, mSayHiResultSubscription);
    }
}

