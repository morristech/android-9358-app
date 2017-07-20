package com.xmd.technician.window;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.hyphenate.exceptions.HyphenateException;
import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.app.user.User;
import com.xmd.app.user.UserInfoServiceImpl;
import com.xmd.chat.event.EventStartChatActivity;
import com.xmd.m.comment.httprequest.ConstantResources;
import com.xmd.technician.Constant;
import com.xmd.technician.R;
import com.xmd.technician.bean.SayHiVisitorResult;
import com.xmd.technician.bean.UserRecentBean;
import com.xmd.technician.chat.ChatConstant;
import com.xmd.technician.common.CharacterParser;
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.common.UINavigation;
import com.xmd.technician.common.Utils;
import com.xmd.technician.http.RequestConstant;
import com.xmd.technician.http.gson.CustomerUserRecentListResult;
import com.xmd.technician.http.gson.NearbyCusCountResult;
import com.xmd.technician.model.HelloSettingManager;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

/**
 * Created by Lhj on 17-5-27.
 */

public class ContactsVisitorsFragment extends BaseListFragment<UserRecentBean> {


    @BindView(R.id.btn_nearby_people)
    Button btnNearbyPeople;
    @BindView(R.id.ll_visitor_none)
    LinearLayout llVisitorNone;

    private Subscription mContactRecentUserListSubscription;
    private Subscription mSayHiVisitorResultSubscription;
    private Subscription mGetNearbyCusCountSubscription;    // 附近的人:获取会所附近客户数量;
    private List<UserRecentBean> mVisitors;
    private List<UserRecentBean> mFilterVisitors;
    private Map<String, String> mSayHiParams = new HashMap<>();
    private int position;
    private String mUserName;
    private CharacterParser characterParser;
    private boolean hasNearbyPeople;


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
        mUserName = "";
        btnNearbyPeople.setText(ResourceUtils.getString(R.string.contact_to_develop_customer));
        mContactRecentUserListSubscription = RxBus.getInstance().toObservable(CustomerUserRecentListResult.class).subscribe(
                result -> handlerRecentUserList(result)
        );
        mSayHiVisitorResultSubscription = RxBus.getInstance().toObservable(SayHiVisitorResult.class).subscribe(
                result -> handlerSayHiVisitorResult(result)
        );
        mGetNearbyCusCountSubscription = RxBus.getInstance().toObservable(NearbyCusCountResult.class).subscribe(
                this::handleNearbyStatus);
    }


    private void handlerRecentUserList(CustomerUserRecentListResult result) {
        mSwipeRefreshLayout.setRefreshing(false);
        if (result.statusCode == 200) {
            mVisitors.clear();
            if (result.respData.userList.size() == 0 && Utils.isEmpty(mUserName)) {
                llVisitorNone.setVisibility(View.VISIBLE);
            } else {
                llVisitorNone.setVisibility(View.GONE);
            }
            if (result.respData.userList.size() > 0) {
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
            }

        } else {
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
            HelloSettingManager.getInstance().sendHelloTemplate(UserInfoServiceImpl.getInstance().getUserByChatId(result.userEmchatId));
            Map<String, String> saveParams = new HashMap<>();
            saveParams.put(RequestConstant.KEY_FRIEND_CHAT_ID, result.userEmchatId);
            saveParams.put(RequestConstant.KEY_CHAT_MSG_ID, "");
            MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_SAVE_CHAT_TO_CHONTACT, saveParams);
        }

    }

    // 附近的人
    private void handleNearbyStatus(NearbyCusCountResult result) {

        if (result.statusCode == 200) {
            if (result.respData <= 0) {
                hasNearbyPeople = false;
                btnNearbyPeople.setText(ResourceUtils.getString(R.string.contact_to_develop_customer));
            } else {
                hasNearbyPeople = true;
                btnNearbyPeople.setText(ResourceUtils.getString(R.string.contact_to_nearby_people));
            }
        }
    }

    @Override
    public void onPositiveButtonClicked(UserRecentBean bean) {//感谢
        super.onPositiveButtonClicked(bean);
        EventBus.getDefault().post(new EventStartChatActivity(bean.emchatId));
    }

    @Override
    public void onNegativeButtonClicked(UserRecentBean bean) {//打招呼
        super.onNegativeButtonClicked(bean);
        sayHiRequest(bean.userId, Utils.isNotEmpty(bean.userNoteName) ? bean.userNoteName : bean.name, bean.avatarUrl, bean.customerType, bean.emchatId, String.valueOf(bean.intListPosition));
    }

    @Override
    public void onItemClicked(UserRecentBean bean) throws HyphenateException {
        super.onItemClicked(bean);
        if (Utils.isEmpty(bean.userId) || Long.parseLong(bean.userId) <= 0) {
            Utils.makeShortToast(getActivity(), ResourceUtils.getString(R.string.visitor_has_no_message));
            return;
        } else {
            UINavigation.gotoCustomerDetailActivity(getActivity(), bean.userId, ConstantResources.INTENT_TYPE_TECH, false);
        }
    }

    @OnClick({R.id.btn_nearby_people})
    public void onViewClicked() {
        // 打开附近的人
        if (hasNearbyPeople) {
            Intent intent = new Intent(getActivity(), NearbyActivity.class);
            startActivity(intent);
        } else {
            // 跳转到营销页面
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.switchFragment(mainActivity.getFragmentSize() - 1);
        }

    }

    public void filterCustomer(String searchName) {
        mUserName = searchName;
        if (Utils.isNotEmpty(searchName)) {
            if (mFilterVisitors == null) {
                mFilterVisitors = new ArrayList<>();
            } else {
                mFilterVisitors.clear();
            }
            for (UserRecentBean recentBean : mVisitors) {
                if (Utils.isEmpty(recentBean.name)) {
                    recentBean.name = ResourceUtils.getString(R.string.contact_recent_default_name);
                }
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
        RxBus.getInstance().unsubscribe(mContactRecentUserListSubscription, mSayHiVisitorResultSubscription, mGetNearbyCusCountSubscription);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void sayHiRequest(String userId, String userName, String avatarUrl, String userType, String userEmchatId, String position) {
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
