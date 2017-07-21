package com.xmd.manager.window;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;

import com.google.gson.Gson;
import com.shidou.commonlibrary.Callback;
import com.shidou.commonlibrary.helper.XLogger;
import com.shidou.commonlibrary.widget.XToast;
import com.xmd.app.Constants;
import com.xmd.app.EventBusSafeRegister;
import com.xmd.app.user.User;
import com.xmd.app.user.UserInfoService;
import com.xmd.app.user.UserInfoServiceImpl;
import com.xmd.chat.XmdChat;
import com.xmd.chat.event.EventStartChatActivity;
import com.xmd.chat.event.EventTotalUnreadCount;
import com.xmd.chat.view.ConversationListFragment;
import com.xmd.m.comment.event.UserInfoEvent;
import com.xmd.m.notify.display.XmdDisplay;
import com.xmd.m.notify.push.PushMessageDataOrder;
import com.xmd.m.notify.push.XmdPushManager;
import com.xmd.m.notify.push.XmdPushMessage;
import com.xmd.m.notify.push.XmdPushMessageListener;
import com.xmd.m.notify.redpoint.RedPointService;
import com.xmd.m.notify.redpoint.RedPointServiceImpl;
import com.xmd.manager.ClubData;
import com.xmd.manager.Constant;
import com.xmd.manager.Manager;
import com.xmd.manager.R;
import com.xmd.manager.SharedPreferenceHelper;
import com.xmd.manager.UINavigation;
import com.xmd.manager.adapter.PageFragmentPagerAdapter;
import com.xmd.manager.beans.ClubInfo;
import com.xmd.manager.beans.SwitchIndex;
import com.xmd.manager.beans.SwitchIndexBean;
import com.xmd.manager.chat.EmchatUserHelper;
import com.xmd.manager.common.ImageLoader;
import com.xmd.manager.common.ResourceUtils;
import com.xmd.manager.common.ThreadManager;
import com.xmd.manager.common.Utils;
import com.xmd.manager.msgctrl.MsgDef;
import com.xmd.manager.msgctrl.MsgDispatcher;
import com.xmd.manager.msgctrl.RxBus;
import com.xmd.manager.service.RequestConstant;
import com.xmd.manager.service.response.ClubResult;
import com.xmd.manager.service.response.NewOrderCountResult;
import com.xmd.manager.widget.ViewPagerTabIndicator;
import com.xmd.permission.BusinessPermissionManager;
import com.xmd.permission.CheckBusinessPermission;
import com.xmd.permission.PermissionConstants;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import rx.Subscription;

public class MainActivity extends BaseActivity implements BaseFragment.IFragmentCallback, ViewPagerTabIndicator.IOnPageChangeListener, ViewPagerTabIndicator.IOnTabClickListener {

    public static int sTabIndex = -1;
    public static int sTabChat = -1;
    public static int sTabCustomerManagement = -1;
    public static int sTabOrder = -1;
    public static int sTabCoupon = -1;


    @BindView(R.id.vp_home)
    ViewPager mViewPager;
    @BindView(R.id.tab_indicator)
    ViewPagerTabIndicator mViewPagerTabIndicator;


    private PageFragmentPagerAdapter mPageFragmentPagerAdapter;
    private Subscription mGetNewOrderCountSubscription;
    private Subscription mGetClubSubscription;
    private Subscription mSwitchIndex;
    private int mCurrentPosition;
    private UserInfoService userInfoService = UserInfoServiceImpl.getInstance();

    private List<String> tabTexts = new ArrayList<>();
    private List<Drawable> icons = new ArrayList<>();

    private int pendingOrderCount;
    private RedPointService redPointService = RedPointServiceImpl.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //加载权限菜单
        BusinessPermissionManager.getInstance().loadPermissions(new Callback<Void>() {
            @Override
            public void onResponse(Void result, Throwable error) {
                if (error != null) {
                    XToast.show("加载权限失败！");
                    UINavigation.gotoLogin(MainActivity.this);
                } else {
                    initView();
                    BusinessPermissionManager.getInstance().syncPermissionsImmediately(null);
                }
            }
        });


        Manager.getInstance().checkUpgrade(true);

        mGetNewOrderCountSubscription = RxBus.getInstance().toObservable(NewOrderCountResult.class).subscribe(
                result -> {
                    pendingOrderCount = result.respData;
                    mViewPagerTabIndicator.setNotice(sTabOrder, pendingOrderCount);
                }
        );

        mGetClubSubscription = RxBus.getInstance().toObservable(ClubResult.class).subscribe(
                clubResult -> handleGetClubResult(clubResult)
        );

        mSwitchIndex = RxBus.getInstance().toObservable(SwitchIndex.class).subscribe(
                switchIndex -> {
                    mViewPager.setCurrentItem(switchIndex.index);
                }
        );
        loadData();
        processXmdDisplay(getIntent());

        EventBusSafeRegister.register(this);
        XmdPushManager.getInstance().addListener(xmdPushMessageListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unsubscribe(mGetNewOrderCountSubscription, mSwitchIndex, mGetClubSubscription);
        EventBusSafeRegister.unregister(this);
        XmdPushManager.getInstance().removeListener(xmdPushMessageListener);
    }

    private void initView() {
        sTabIndex = -1;
        sTabChat = -1;
        sTabCustomerManagement = -1;
        sTabOrder = -1;
        sTabCoupon = -1;
        mPageFragmentPagerAdapter = new PageFragmentPagerAdapter(getSupportFragmentManager(), this);

        tabTexts.clear();
        icons.clear();

        initPageIndex();
        initPageChat();
        initPageCustomerManager();
        initPageOrder();
        initPageMarketing();

        if (tabTexts.size() == 0) {
            makeShortToast(ResourceUtils.getString(R.string.authority_forbidden));
            gotoLoginActivity("权限不足，请切换账号登录");
            return;
        }
        mViewPager.setAdapter(mPageFragmentPagerAdapter);
        mViewPager.setOffscreenPageLimit(tabTexts.size());
        setRightIcon(0);
        if (tabTexts.size() == 1) {
            return;
        }
        mViewPagerTabIndicator.setTabTexts(tabTexts.toArray(new String[]{}));
        mViewPagerTabIndicator.setTabIcons(icons.toArray(new Drawable[]{}));
        mViewPagerTabIndicator.setWithDivider(false);
        mViewPagerTabIndicator.setWithIndicator(false);
        mViewPagerTabIndicator.setDrawbleDirection(ViewPagerTabIndicator.DRAWABLE_TOP);
        mViewPagerTabIndicator.setViewPager(mViewPager);
        mViewPagerTabIndicator.setup();
        mViewPagerTabIndicator.setOnPageChangeListener(this);
        mViewPagerTabIndicator.setOnTabclickListener(this);
    }

    @CheckBusinessPermission(PermissionConstants.MG_TAB_INDEX)
    public void initPageIndex() {
        mPageFragmentPagerAdapter.addFragment(new MainPageFragment());
        tabTexts.add("首页");
        icons.add(ResourceUtils.getDrawable(R.drawable.ic_tab_index));
        sTabIndex = tabTexts.size() - 1;
    }

    @CheckBusinessPermission(PermissionConstants.MG_TAB_CHAT)
    public void initPageChat() {
        ConversationListFragment fragment = ConversationListFragment.newInstance("消息");
        mPageFragmentPagerAdapter.addFragment(fragment);
        tabTexts.add("消息");
        icons.add(ResourceUtils.getDrawable(R.drawable.ic_tab_chat));
        sTabChat = tabTexts.size() - 1;
        mViewPagerTabIndicator.setNotice(sTabChat, XmdChat.getInstance().getTotalUnreadCount());
    }

    @CheckBusinessPermission(PermissionConstants.MG_TAB_CUSTOMER)
    public void initPageCustomerManager() {
        mPageFragmentPagerAdapter.addFragment(new CustomerManagementFragment());
        tabTexts.add("客户");
        icons.add(ResourceUtils.getDrawable(R.drawable.ic_tab_customer_management));
        sTabCustomerManagement = tabTexts.size() - 1;
    }

    @CheckBusinessPermission(PermissionConstants.MG_TAB_ORDER)
    public void initPageOrder() {
        mPageFragmentPagerAdapter.addFragment(new OrderListFragment());
        icons.add(ResourceUtils.getDrawable(R.drawable.ic_tab_order));
        tabTexts.add("订单");
        sTabOrder = tabTexts.size() - 1;
    }

    @CheckBusinessPermission(PermissionConstants.MG_TAB_COUPON)
    public void initPageMarketing() {
        mPageFragmentPagerAdapter.addFragment(new MarketingFragment());
        icons.add(ResourceUtils.getDrawable(R.drawable.ic_tab_marketing));
        tabTexts.add("营销");
        sTabCoupon = tabTexts.size() - 1;
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        processXmdDisplay(intent);
    }

    private boolean processXmdDisplay(Intent intent) {
        XmdDisplay display = (XmdDisplay) intent.getSerializableExtra(UINavigation.EXTRA_XMD_DISPLAY);
        if (display != null) {
            UINavigation.processXmdDisplay(this, display);
            return true;
        }
        return false;
    }

    private void loadData() {
        EmchatUserHelper.login(null);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_CLUB_INFO);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_NEW_ORDER_COUNT);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_AUTH_CONFIG);
    }


    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void setFragmentTitle(String title) {

    }

    private void setRightIcon(int position) {
        if (position == sTabCustomerManagement) {
            setNoticeVisible(false);
            setRightVisible(false, "", null);
            setRightVisible(true, R.drawable.ic_search_selector, view -> startActivity(new Intent(this, CustomerSearchActivity.class)));
        } else if (position == sTabIndex) {
            setRightVisible(false, "", null);
            setNoticeVisible(true);
        } else if (position == sTabCoupon) {
            setRightVisible(false, -1, null);
        } else if (position == sTabChat) {
            ClubData.getInstance().clearEmchatMsgCount();
            mViewPagerTabIndicator.setNotice(sTabChat, 0);
        } else {
            setRightVisible(false, -1, null);
        }
        setCustomerFilterVisible(position == sTabCustomerManagement);
    }

    private void handleGetClubResult(ClubResult result) {
        if (result.statusCode == RequestConstant.RESP_ERROR_CODE_FOR_LOCAL) {
            makeShortToast(ResourceUtils.getString(R.string.get_club_failed));
        } else {
            onGetClubResultSucceeded(result);
        }
    }

    private void onGetClubResultSucceeded(ClubResult result) {
        ClubData.getInstance().setClubInfo(result.respData);
        ClubInfo clubInfo = ClubData.getInstance().getClubInfo();
        if (clubInfo != null) {
            ThreadManager.postRunnable(ThreadManager.THREAD_TYPE_BACKGROUND, () -> {
                ImageLoader.saveImageFile(clubInfo.imageUrl, ClubData.getInstance().getClubImageLocalPath());
                SharedPreferenceHelper.setUserAvatar(clubInfo.imageUrl);
                SharedPreferenceHelper.setCurrentClubName(clubInfo.name);
                SharedPreferenceHelper.setCurrentClubCreateTime(Utils.StrSubstring(10, clubInfo.createTime, false));
                EmchatUserHelper.saveUser(SharedPreferenceHelper.getEmchatId(), clubInfo.name, clubInfo.imageUrl);
            });
        }
    }


    @Override
    public void onPageSelected(int position) {
        setRightIcon(position);
        mCurrentPosition = position;
        if (position == sTabOrder) {
            redPointService.clear(Constant.RED_POINT_NEW_ORDER);
        }
    }

    @Override
    public void gotoLoginActivity(String message) {
        super.gotoLoginActivity(message);
    }

    @Override
    public void onTabClick(int position) {
        setRightIcon(position);
        RxBus.getInstance().post(new SwitchIndexBean(position));
    }

    //跳转到某个页面
    public void switchTo(int position) {
        onTabClick(position);
        mViewPagerTabIndicator.updateSelectedPosition(position);
    }

    @Subscribe
    public void onChatMessageTotalUnreadCount(EventTotalUnreadCount event) {
        if (sTabChat >= 0) {
            mViewPagerTabIndicator.setNotice(sTabChat, event.getCount());
        }
    }

    @Subscribe
    public void sendMessageOrCall(UserInfoEvent event) {
        if (event.appType == 0) {
            if (event.toDoType == 1) {
                XLogger.i(">>>", "此处接受到消息,应该去聊天...");
                User user = new User(event.bean.userId);
                user.setChatId(event.bean.emChatId);
                user.setName(event.bean.emChatName);
                user.setAvatar(event.bean.chatHeadUrl);
                userInfoService.saveUser(user);
                EventBus.getDefault().post(new EventStartChatActivity(event.bean.emChatId));
            } else if (event.toDoType == 3) { //打招呼

            }
        }
    }

    private XmdPushMessageListener xmdPushMessageListener = new XmdPushMessageListener() {
        @Override
        public void onMessage(XmdPushMessage message) {
            switch (message.getBusinessType()) {
                case XmdPushMessage.BUSINESS_TYPE_ORDER:
                    if (sTabOrder >= 0) {
                        PushMessageDataOrder data = new Gson().fromJson(message.getData(), PushMessageDataOrder.class);
                        if (data == null) {
                            return;
                        }
                        if (Constants.ORDER_STATUS_SUBMIT.equals(data.status)) {
                            pendingOrderCount++;
                        } else if (Constants.ORDER_STATUS_ACCEPT.equals(data.status)
                                || Constants.ORDER_STATUS_REJECT.equals(data.status)) {
                            pendingOrderCount--;
                        }
                        mViewPagerTabIndicator.setNotice(sTabOrder, pendingOrderCount);
                        redPointService.set(Constant.RED_POINT_NEW_ORDER, pendingOrderCount);
                    }
                    break;
            }
        }

        @Override
        public void onRawMessage(String message) {

        }
    };

}