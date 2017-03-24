package com.xmd.technician.window;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.hyphenate.util.DateUtils;
import com.xmd.technician.Adapter.MainPageTechOrderListAdapter;
import com.xmd.technician.AppConfig;
import com.xmd.technician.Constant;
import com.xmd.technician.DataRefreshService;
import com.xmd.technician.R;
import com.xmd.technician.SharedPreferenceHelper;
import com.xmd.technician.bean.DynamicDetail;
import com.xmd.technician.bean.Order;
import com.xmd.technician.bean.RecentlyVisitorBean;
import com.xmd.technician.bean.RecentlyVisitorResult;
import com.xmd.technician.common.ActivityHelper;
import com.xmd.technician.common.HeartBeatTimer;
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.common.ThreadManager;
import com.xmd.technician.common.UINavigation;
import com.xmd.technician.common.Utils;
import com.xmd.technician.event.EventJoinedClub;
import com.xmd.technician.event.EventRequestJoinClub;
import com.xmd.technician.http.RequestConstant;
import com.xmd.technician.http.gson.ContactPermissionWithBeanResult;
import com.xmd.technician.http.gson.DynamicListResult;
import com.xmd.technician.http.gson.HelloGetTemplateResult;
import com.xmd.technician.http.gson.NearbyCusCountResult;
import com.xmd.technician.http.gson.OrderListResult;
import com.xmd.technician.http.gson.OrderManageResult;
import com.xmd.technician.http.gson.TechInfoResult;
import com.xmd.technician.http.gson.TechRankDataResult;
import com.xmd.technician.http.gson.TechStatisticsDataResult;
import com.xmd.technician.model.HelloSettingManager;
import com.xmd.technician.model.LoginTechnician;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;
import com.xmd.technician.onlinepaynotify.model.PayNotifyInfo;
import com.xmd.technician.onlinepaynotify.view.OnlinePayNotifyActivity;
import com.xmd.technician.onlinepaynotify.view.OnlinePayNotifyFragment;
import com.xmd.technician.permission.CheckBusinessPermission;
import com.xmd.technician.permission.PermissionConstants;
import com.xmd.technician.widget.CircleImageView;
import com.xmd.technician.widget.RewardConfirmDialog;
import com.xmd.technician.widget.SlidingMenu;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by Lhj on 2016/10/19.
 */
public class MainFragment extends BaseFragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {


    @Bind(R.id.rl_toolbar)
    RelativeLayout mRlToolBar;
    @Bind(R.id.app_version)
    TextView mAppVersion;
    @Bind(R.id.join_or_quit_club)
    TextView mJoinOrQuitClub;
    @Bind(R.id.settings_activity_join_or_quit_club)
    RelativeLayout mMenuJoinOrQuitClub;
    @Bind(R.id.menu_club_name)
    TextView mMenuClubName;
    @Bind(R.id.main_head_avatar)
    CircleImageView mMainHeadAvatar;
    @Bind(R.id.main_head_tech_name)
    TextView mMainHeadTechName;
    @Bind(R.id.main_head_tech_serial)
    TextView mMainHeadTechSerial;
    @Bind(R.id.btn_main_tech_rest)
    ImageView mBtnMainTechRest;
    @Bind(R.id.btn_main_tech_busy)
    ImageView mBtnMainTechBusy;
    @Bind(R.id.btn_main_tech_free)
    ImageView mBtnMainTechFree;
    @Bind(R.id.main_info_too_keen_number)
    TextView mMainInfoTooKeenNumber;
    @Bind(R.id.main_send_coupon_number)
    TextView mMainSendCouponNumber;
    @Bind(R.id.main_get_comment_number)
    TextView mMainGetCommentNumber;
    @Bind(R.id.main_total_income_number)
    TextView mMainTotalIncomeNumber;
    @Bind(R.id.main_order_list)
    ListView mMainOrderList;
    @Bind(R.id.main_dynamic_avatar1)
    CircleImageView mMainDynamicAvatar1;
    @Bind(R.id.main_dynamic_name1)
    TextView mMainDynamicName1;
    @Bind(R.id.main_dynamic_describe1)
    TextView mMainDynamicDescribe1;
    @Bind(R.id.main_dynamic_time1)
    TextView mMainDynamicTime1;
    @Bind(R.id.main_dynamic_avatar2)
    CircleImageView mMainDynamicAvatar2;
    @Bind(R.id.main_dynamic_name2)
    TextView mMainDynamicName2;
    @Bind(R.id.main_dynamic_describe2)
    TextView mMainDynamicDescribe2;
    @Bind(R.id.main_dynamic_time2)
    TextView mMainDynamicTime2;
    @Bind(R.id.main_dynamic_avatar3)
    CircleImageView mMainDynamicAvatar3;
    @Bind(R.id.main_dynamic_name3)
    TextView mMainDynamicName3;
    @Bind(R.id.main_dynamic_describe3)
    TextView mMainDynamicDescribe3;
    @Bind(R.id.main_dynamic_time3)
    TextView mMainDynamicTime3;
    @Bind(R.id.main_who_care_total)
    TextView mMainWhoCareTotal;
    @Bind(R.id.ll_horizontalList)
    LinearLayout llHorizontalList;
    @Bind(R.id.cv_star_register)
    CircleImageView mCvStarRegister;
    @Bind(R.id.tv_star_register_user)
    TextView mTvStarRegisterUser;
    @Bind(R.id.tv_star_register_tech_no)
    TextView mTvStarRegisterTechNo;
    @Bind(R.id.cv_star_sales)
    CircleImageView mCvStarSales;
    @Bind(R.id.tv_star_sales)
    TextView mTvStarSales;
    @Bind(R.id.tv_title_sale)
    TextView mTvTitleSale;
    @Bind(R.id.cv_star_service)
    CircleImageView mCvStarService;
    @Bind(R.id.tv_star_service)
    TextView mTvStarService;
    @Bind(R.id.tv_title_service)
    TextView mTvTitleService;
    @Bind(R.id.main_scroll_view)
    NestedScrollView mMainScrollView;
    @Bind(R.id.main_sliding_layout)
    SlidingMenu mMainSlidingLayout;
    @Bind(R.id.main_dynamic1)
    LinearLayout mMainDynamic1;
    @Bind(R.id.main_dynamic2)
    LinearLayout mMainDynamic2;
    @Bind(R.id.main_dynamic3)
    LinearLayout mMainDynamic3;
    @Bind(R.id.main_visit_avatar1)
    CircleImageView visitAvatar1;
    @Bind(R.id.main_visit_avatar2)
    CircleImageView visitAvatar2;
    @Bind(R.id.main_visit_avatar3)
    CircleImageView visitAvatar3;
    @Bind(R.id.main_visit_avatar4)
    CircleImageView visitAvatar4;
    @Bind(R.id.main_visit_avatar5)
    CircleImageView visitAvatar5;
    @Bind(R.id.swipe_refresh_widget)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @Bind(R.id.rl_visit_null)
    RelativeLayout mVisitNull;
    @Bind(R.id.main_head_tech_status)
    TextView mTechStatus;
    @Bind(R.id.order_figure_out)
    TextView mOrderFigureOut;

    // 附近的人
    @Bind(R.id.nearby_layout)
    RelativeLayout mNearbyLayout;
    @Bind(R.id.main_nearby_position)
    TextView mNearbyPosition;
    @Bind(R.id.main_nearby_desc)
    TextView mNearbyDesc;
    @Bind(R.id.main_nearby_say_hello)
    Button mNearbySayHello;
    @Bind(R.id.main_nearby_go_toker)
    Button mNearbyGoToker;

    //支付通知
    @Bind(R.id.online_pay_notify_layout)
    View mPayNotifyLayout;
    @Bind(R.id.pay_notify_header)
    RelativeLayout mPayNotifyHeader;
    private OnlinePayNotifyFragment mPayNotifyFragment;


    private ImageView imageLeft, imageRight;
    private Context mContext;
    private List<Order> mAllTechOrderList = new ArrayList<>();
    private List<Order> mTechOrderList = new ArrayList<>();
    private List<RecentlyVisitorBean> mTechVisitor = new ArrayList<>();
    private List<RecentlyVisitorBean> mAllTechVisitor = new ArrayList<>();
    private List<View> visitViewList = new ArrayList<>();
    private List<DynamicDetail> mDynamicList = new ArrayList<>();
    private MainPageTechOrderListAdapter orderListAdapter;
    private boolean hasDynamic;
    private LinearLayout mContactMore;

    private Subscription mGetTechCurrentInfoSubscription;
    private Subscription mGetTechOrderListSubscription;
    private Subscription mGetTechStatisticsDataSubscription;
    private Subscription mGetTechRankIndexDataSubscription;
    private Subscription mGetRecentlyVisitorSubscription;
    private Subscription mOrderManageSubscription;
    private Subscription mGetMomentListSubscription;
    private Subscription mTechStatusSubscription;
    private Subscription mJoinedClubSubscription;
    private Subscription mRequestJoinClubSubscription;
    private Subscription mGetNearbyCusCountSubscription;    // 附近的人:获取会所附近客户数量;
    private Subscription mGetHelloSetTemplateSubscription;  // 获取打招呼内容
    private Subscription mGetContactPermissionSubscription; // 获取聊天限制

    private LoginTechnician mTech = LoginTechnician.getInstance();
    private HelloSettingManager mHelloSettingManager = HelloSettingManager.getInstance();

    private View mRootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.main_fragment, container, false);
        mContext = getActivity();
        ButterKnife.bind(this, mRootView);
        initView(mRootView);

        initStatistic();
        initOnlinePay();
        initOrder();
        initVisitor();
        initMoment();
        initRanking();
        initCredit();
        initWorkStatus();
        initNearbyUser();

        showHeadView();

        registerRequestHandlers(); //注册监听器

        HeartBeatTimer.getInstance().start(60, mTask);

        sendDataRequest();
        return mRootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        HeartBeatTimer.getInstance().shutdown();

        RxBus.getInstance().unsubscribe(
                mGetTechCurrentInfoSubscription,
                mGetTechOrderListSubscription,
                mGetTechStatisticsDataSubscription,
                mGetTechRankIndexDataSubscription,
                mTechStatusSubscription,
                mOrderManageSubscription,
                mGetMomentListSubscription,
                mGetRecentlyVisitorSubscription,
                mJoinedClubSubscription,
                mRequestJoinClubSubscription,
                mGetNearbyCusCountSubscription,
                mGetHelloSetTemplateSubscription,
                mGetContactPermissionSubscription);
    }

    private void initView(View view) {
        initTitleView(view);
        initMenu();

        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorMainBtn);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        mMainScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                onScrollViewChanged(scrollX, scrollY);
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        mMainSlidingLayout.closeMenu();
    }

    private void sendDataRequest() {
        loadStatisticData();
        loadOrderListData();
        loadRankingData();
        loadVisitor();
        loadMomentData();
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_SET_TEMPLATE);// 技师登录进入首页后,获取打招呼内容
    }


    private void initTitleView(View view) {
        ((TextView) view.findViewById(R.id.toolbar_title)).setText(R.string.main_page);
        ((TextView) view.findViewById(R.id.toolbar_title)).setTextColor(Color.WHITE);
        view.findViewById(R.id.divide_line).setVisibility(View.GONE);
        mContactMore = (LinearLayout) view.findViewById(R.id.contact_more);
        mContactMore.setVisibility(View.VISIBLE);
        mContactMore.setOnClickListener(this);
        view.findViewById(R.id.rl_toolbar).setBackgroundColor(ResourceUtils.getColor(R.color.main_tool_bar_bg));
        imageRight = ((ImageView) view.findViewById(R.id.toolbar_right_img));
        imageRight.setVisibility(View.GONE);
        initQRCode();
        imageLeft = (ImageView) view.findViewById(R.id.toolbar_back);
        imageLeft.setImageDrawable(ResourceUtils.getDrawable(R.drawable.ic_main_menu));
        imageLeft.setVisibility(View.VISIBLE);
        imageLeft.setOnClickListener(this);
        mAppVersion.setText("v" + AppConfig.getAppVersionNameAndCode());
    }

    private void registerRequestHandlers() {
        mGetTechCurrentInfoSubscription = RxBus.getInstance().toObservable(TechInfoResult.class).subscribe(
                techCurrentResult -> handleTechCurrentResult(techCurrentResult));

        mJoinedClubSubscription = RxBus.getInstance().toObservable(EventJoinedClub.class).subscribe(this::onEventJoinedClub);

        mRequestJoinClubSubscription = RxBus.getInstance().toObservable(EventRequestJoinClub.class).subscribe(this::onEventRequestJoinClub);

        // 打招呼:获取打招呼内容
        mGetHelloSetTemplateSubscription = RxBus.getInstance().toObservable(HelloGetTemplateResult.class).subscribe(helloGetTemplateResult -> {
            handleSetTemplateResult(helloGetTemplateResult);
        });

        mGetContactPermissionSubscription = RxBus.getInstance().toObservable(ContactPermissionWithBeanResult.class).subscribe(new Action1<ContactPermissionWithBeanResult>() {
            @Override
            public void call(ContactPermissionWithBeanResult contactPermissionWithBeanResult) {
                handleContactPermission(contactPermissionWithBeanResult);
            }
        });
    }

    @CheckBusinessPermission((PermissionConstants.QR_CODE))
    public void initQRCode() {
        imageRight.setVisibility(View.VISIBLE);
        imageRight.setImageDrawable(ResourceUtils.getDrawable(R.drawable.btn_main_qr_code));
    }

    /**************************统计数据***************************/
    @CheckBusinessPermission(PermissionConstants.STATISTIC)
    public void initStatistic() {
        mRootView.findViewById(R.id.statistic_layout).setVisibility(View.VISIBLE);
        initStatisticInviteCustomer();
        initStatisticDistributeCoupon();
        initStatisticIncome();
        initStatisticPraise();
        mGetTechStatisticsDataSubscription = RxBus.getInstance().toObservable(TechStatisticsDataResult.class).subscribe(
                statisticsData -> initTechWorkView(statisticsData));
    }

    @CheckBusinessPermission(PermissionConstants.STATISTIC)
    public void loadStatisticData() {
        MsgDispatcher.dispatchMessage(MsgDef.MSF_DEF_GET_TECH_STATISTICS_DATA);
    }


    @CheckBusinessPermission(PermissionConstants.STATISTIC_INVITE_CUSTOMER)
    public void initStatisticInviteCustomer() {
        mRootView.findViewById(R.id.main_too_keen).setVisibility(View.VISIBLE);
    }

    @CheckBusinessPermission(PermissionConstants.STATISTIC_DISTRIBUTE_COUPON)
    public void initStatisticDistributeCoupon() {
        mRootView.findViewById(R.id.main_send_coupon).setVisibility(View.VISIBLE);
    }

    @CheckBusinessPermission(PermissionConstants.STATISTIC_PRAISE)
    public void initStatisticPraise() {
        mRootView.findViewById(R.id.main_get_comment).setVisibility(View.VISIBLE);
    }

    @CheckBusinessPermission(PermissionConstants.STATISTIC_INCOME)
    public void initStatisticIncome() {
        mRootView.findViewById(R.id.main_total_income).setVisibility(View.VISIBLE);
    }

    /**************************在线买单***************************/
    @CheckBusinessPermission(PermissionConstants.ONLINE_PAY)
    public void initOnlinePay() {
        mPayNotifyLayout.setVisibility(View.VISIBLE);
        final long startTime = System.currentTimeMillis() - Constant.PAY_NOTIFY_MAIN_PAGE_TIME_LIMIT;
        final long endTime = System.currentTimeMillis() + (3600 * 1000);
        mPayNotifyFragment = (OnlinePayNotifyFragment) getActivity().getSupportFragmentManager().findFragmentByTag("fragment_pay_notify");
        if (mPayNotifyFragment == null) {
            mPayNotifyFragment = OnlinePayNotifyFragment.newInstance(startTime, endTime, PayNotifyInfo.STATUS_ALL, true, Constant.PAY_NOTIFY_MAIN_PAGE_SHOW_LIMIT);
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.add(R.id.fragment_pay_notify_container, mPayNotifyFragment, "fragment_pay_notify");
            ft.commit();
        } else {
            mPayNotifyFragment.setFilter(startTime, endTime, PayNotifyInfo.STATUS_ALL, true);
            mPayNotifyFragment.loadData(true);
        }
        DataRefreshService.refreshPayNotify(true);
    }

    /**************************订单***************************/
    @CheckBusinessPermission(PermissionConstants.ORDER)
    public void initOrder() {
        mRootView.findViewById(R.id.order_layout).setVisibility(View.VISIBLE);
        mOrderManageSubscription = RxBus.getInstance().toObservable(OrderManageResult.class).subscribe(
                orderManageResult -> loadOrderListData());
        mGetTechOrderListSubscription = RxBus.getInstance().toObservable(OrderListResult.class).subscribe(
                this::initOrderView);
    }

    @CheckBusinessPermission(PermissionConstants.ORDER)
    public void loadOrderListData() {
        Map<String, String> param = new HashMap<>();
        param.put(RequestConstant.KEY_PAGE, "1");
        param.put(RequestConstant.KEY_IS_INDEX_PAGE, "Y");
        param.put(RequestConstant.KEY_PAGE_SIZE, String.valueOf(20));
        param.put(RequestConstant.KEY_ORDER_STATUS, RequestConstant.KEY_ORDER_STATUS_SUBMIT_AND_ACCEPT);
        MsgDispatcher.dispatchMessage(MsgDef.MSF_DEF_GET_TECH_ORDER_LIST, param);
    }

    /**************************最近访客***************************/
    @CheckBusinessPermission(PermissionConstants.VISITOR)
    public void initVisitor() {
        mRootView.findViewById(R.id.visitor_layout).setVisibility(View.VISIBLE);
        visitViewList.add(visitAvatar1);
        visitViewList.add(visitAvatar2);
        visitViewList.add(visitAvatar3);
        visitViewList.add(visitAvatar4);
        visitViewList.add(visitAvatar5);
        if (mGetRecentlyVisitorSubscription == null) {
            mGetRecentlyVisitorSubscription = RxBus.getInstance().toObservable(RecentlyVisitorResult.class).subscribe(
                    this::initRecentlyViewView);
        }
    }

    @CheckBusinessPermission(PermissionConstants.VISITOR)
    public void loadVisitor() {
        Map<String, String> visitParams = new HashMap<>();
        visitParams.put(RequestConstant.KEY_CUSTOMER_TYPE, "");
        visitParams.put(RequestConstant.KEY_LAST_TIME, "");
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_RECENTLY_VISITOR, visitParams);
        // 附近的人:获取会所附近客户数量(条件:技师已经加入了会所)
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_NEARBY_CUS_COUNT);
    }

    /**************************动态***************************/
    @CheckBusinessPermission(PermissionConstants.MOMENT)
    public void initMoment() {
        mRootView.findViewById(R.id.moment_layout).setVisibility(View.VISIBLE);
        mGetMomentListSubscription = RxBus.getInstance().toObservable(DynamicListResult.class).subscribe(
                this::initDynamicView);
    }

    @CheckBusinessPermission(PermissionConstants.MOMENT)
    public void loadMomentData() {
        Map<String, String> param = new HashMap<>();
        param.put(RequestConstant.KEY_PAGE, "1");
        param.put(RequestConstant.KEY_PAGE_SIZE, String.valueOf(3));
        param.put(RequestConstant.KEY_ORDER_STATUS, RequestConstant.KEY_ORDER_STATUS_SUBMIT);
        MsgDispatcher.dispatchMessage(MsgDef.MSF_DEF_GET_TECH_DYNAMIC_LIST, param);
    }

    /**************************排行榜***************************/
    @CheckBusinessPermission(PermissionConstants.RANKING_TECHNICIAN)
    public void initRanking() {
        mRootView.findViewById(R.id.ranking_layout).setVisibility(View.VISIBLE);
        mGetTechRankIndexDataSubscription = RxBus.getInstance().toObservable(TechRankDataResult.class).subscribe(
                this::initTechRankingView);
    }

    @CheckBusinessPermission(PermissionConstants.RANKING_TECHNICIAN)
    public void loadRankingData() {
        MsgDispatcher.dispatchMessage(MsgDef.MSF_DEF_GET_TECH_RANK_INDEX_DATA);
    }

    /**************************积分中心***************************/
    @CheckBusinessPermission(PermissionConstants.CREDIT)
    public void initCredit() {
        mRootView.findViewById(R.id.btn_main_credit_center).setVisibility(View.VISIBLE);
    }

    /**************************工作状态***************************/
    @CheckBusinessPermission(PermissionConstants.WORK_STATUS)
    public void initWorkStatus() {
        mRootView.findViewById(R.id.work_status_layout).setVisibility(View.VISIBLE);
    }


    /**************************附近的人***************************/
    @CheckBusinessPermission(PermissionConstants.NEARBY_USER)
    public void initNearbyUser() {
        mRootView.findViewById(R.id.nearby_layout).setVisibility(View.VISIBLE);
        // 附近的人:订阅会所附近客户数量的事件监听
        mGetNearbyCusCountSubscription = RxBus.getInstance().toObservable(NearbyCusCountResult.class).subscribe(
                this::handleNearbyStatus);
    }


    private void handleTechCurrentResult(TechInfoResult result) {
        mSwipeRefreshLayout.setRefreshing(false);
        if (result.respData == null) {
            return;
        }
        if (result.statusCode >= 200 && result.statusCode <= 299) {
            mTech.onLoadTechInfo(result);
            showHeadView();
        }
    }

    public void showTechStatus(String status) {
        mMainHeadTechSerial.setVisibility(View.GONE);
        mTechStatus.setVisibility(View.GONE);
        mJoinOrQuitClub.setText("退出会所");
        mMenuClubName.setText(mTech.getClubName());
        switch (status) {
            case Constant.TECH_STATUS_VALID:
                mTechStatus.setVisibility(View.VISIBLE);
                mTechStatus.setText(ResourceUtils.getString(R.string.join_club_before));
                mJoinOrQuitClub.setText("加入会所");
                mMenuClubName.setText("");
                break;
            case Constant.TECH_STATUS_REJECT:
                mTechStatus.setVisibility(View.VISIBLE);
                mTechStatus.setText(ResourceUtils.getString(R.string.club_reject_apply));
                mJoinOrQuitClub.setText("加入会所");
                mMenuClubName.setText("");
                break;
            case Constant.TECH_STATUS_UNCERT:
                mMainHeadTechSerial.setVisibility(View.VISIBLE);
                mMainHeadTechSerial.setText(mTech.getTechNo());
                mTechStatus.setVisibility(View.VISIBLE);
                mTechStatus.setText(ResourceUtils.getString(R.string.wait_club_examine));
                mJoinOrQuitClub.setText("取消申请");
                break;
            case Constant.TECH_STATUS_FREE:
                mMainHeadTechSerial.setVisibility(View.VISIBLE);
                mMainHeadTechSerial.setText(mTech.getTechNo());
                resetTechStatusView(R.id.btn_main_tech_free);
                break;
            case Constant.TECH_STATUS_BUSY:
                mMainHeadTechSerial.setVisibility(View.VISIBLE);
                mMainHeadTechSerial.setText(mTech.getTechNo());
                resetTechStatusView(R.id.btn_main_tech_busy);
                break;
            default:
                break;
        }
    }

    // 处理打招呼内容
    private void handleSetTemplateResult(HelloGetTemplateResult result) {
        if (result.statusCode == 200 && result.respData != null) {
            mHelloSettingManager.setTemplate(result.respData);
            // 缓存打招呼图片
            MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_DOWNLOAD_HELLO_IMAGE_CACHE);
        }
    }

    // 附近的人
    private void handleNearbyStatus(NearbyCusCountResult result) {
        if (Utils.isNotEmpty(mTech.getClubPosition())) {
            mNearbyPosition.setText(mTech.getClubPosition());
        }
        if (result.statusCode == 200) {
            if (result.respData <= 0) {
                mNearbyDesc.setText(R.string.nearby_no_customer_text);
                mNearbySayHello.setVisibility(View.GONE);
                mNearbyGoToker.setVisibility(View.VISIBLE);
            } else {
                String textCount = "附近有" + result.respData + "个客人，打个招呼吧~";
                mNearbyDesc.setText(Utils.changeColor(textCount, ResourceUtils.getColor(R.color.colorMainBtn), 3, textCount.length() - 10));
                mNearbySayHello.setVisibility(View.VISIBLE);
                mNearbyGoToker.setVisibility(View.GONE);
            }
        } else {
            mNearbyDesc.setText(R.string.nearby_get_customer_exception);
            mNearbyGoToker.setVisibility(View.GONE);
            mNearbySayHello.setVisibility(View.GONE);
        }
    }

    @OnClick({R.id.btn_main_tech_free, R.id.btn_main_tech_busy, R.id.btn_main_tech_rest, R.id.btn_main_credit_center})
    public void onMainHeadClicked(View view) {

        switch (view.getId()) {
            case R.id.btn_main_tech_free:
                if (Utils.isNotEmpty(mTech.getInnerProvider())) {
                    ((BaseFragmentActivity) getActivity()).makeShortToast(getString(R.string.main_fragment_tech_status_select));
                } else {
                    resetTechStatusView(R.id.btn_main_tech_free);
                }
                break;
            case R.id.btn_main_tech_busy:
                if (Utils.isNotEmpty(mTech.getInnerProvider())) {
                    ((BaseFragmentActivity) getActivity()).makeShortToast(getString(R.string.main_fragment_tech_status_select));
                } else {
                    resetTechStatusView(R.id.btn_main_tech_busy);
                }
                break;
            case R.id.btn_main_tech_rest:
                if (Utils.isNotEmpty(mTech.getInnerProvider())) {
                    ((BaseFragmentActivity) getActivity()).makeShortToast(getString(R.string.main_fragment_tech_status_select));
                } else {
                    resetTechStatusView(-1);
                }
                break;
            case R.id.btn_main_credit_center:
                Intent intentCredit = new Intent(getActivity(), UserCreditCenterActivity.class);
                startActivity(intentCredit);
                break;
        }
    }

    private void resetTechStatusView(int id) {
        switch (id) {
            case R.id.btn_main_tech_free:
                mBtnMainTechFree.setImageDrawable(ResourceUtils.getDrawable(R.drawable.btn_main_free_selected));
                mBtnMainTechBusy.setImageDrawable(ResourceUtils.getDrawable(R.drawable.btn_main_busy_default));
                mBtnMainTechRest.setImageDrawable(ResourceUtils.getDrawable(R.drawable.btn_main_rest_default));
                updateWorkTimeResult(RequestConstant.KEY_TECH_STATUS_FREE);
                break;
            case R.id.btn_main_tech_busy:
                mBtnMainTechFree.setImageDrawable(ResourceUtils.getDrawable(R.drawable.btn_main_free_default));
                mBtnMainTechBusy.setImageDrawable(ResourceUtils.getDrawable(R.drawable.btn_main_busy_selected));
                mBtnMainTechRest.setImageDrawable(ResourceUtils.getDrawable(R.drawable.btn_main_rest_default));
                updateWorkTimeResult(RequestConstant.KEY_TECH_STATUS_BUSY);
                break;
            case R.id.btn_main_tech_rest:
                mBtnMainTechFree.setImageDrawable(ResourceUtils.getDrawable(R.drawable.btn_main_free_default));
                mBtnMainTechBusy.setImageDrawable(ResourceUtils.getDrawable(R.drawable.btn_main_busy_default));
                mBtnMainTechRest.setImageDrawable(ResourceUtils.getDrawable(R.drawable.btn_main_rest_selected));
                updateWorkTimeResult(RequestConstant.KEY_TECH_STATUS_REST);
                break;
            case -1:
                mBtnMainTechFree.setImageDrawable(ResourceUtils.getDrawable(R.drawable.btn_main_free_selected));
                mBtnMainTechBusy.setImageDrawable(ResourceUtils.getDrawable(R.drawable.btn_main_busy_default));
                mBtnMainTechRest.setImageDrawable(ResourceUtils.getDrawable(R.drawable.btn_main_rest_default));
                break;
            default:
                mBtnMainTechFree.setImageDrawable(ResourceUtils.getDrawable(R.drawable.btn_main_free_selected));
                mBtnMainTechBusy.setImageDrawable(ResourceUtils.getDrawable(R.drawable.btn_main_busy_default));
                mBtnMainTechRest.setImageDrawable(ResourceUtils.getDrawable(R.drawable.btn_main_rest_default));
                break;

        }
    }

    private void updateWorkTimeResult(String mStatus) {
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_STATUS, mStatus);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_UPDATE_WORK_STATUS, params);
    }

    public void initMenu() {
        initMenuWorkTime();
        initMenuWorkProject();
        initMenuAbout();
        initMenuSuggest();
        initMenuChangePassword();
        initMenuJoinOrQuitClub();
    }

    @CheckBusinessPermission(PermissionConstants.WORK_TIME)
    public void initMenuWorkTime() {
        initMenuItem(R.id.menu_work_time);
    }

    @CheckBusinessPermission(PermissionConstants.WORK_PROJECT)
    public void initMenuWorkProject() {
        initMenuItem(R.id.menu_work_project);
    }

    public void initMenuAbout() {
        initMenuItem(R.id.menu_about_us);
    }

    public void initMenuSuggest() {
        initMenuItem(R.id.menu_suggest);
    }

    public void initMenuChangePassword() {
        initMenuItem(R.id.settings_activity_modify_pw);
    }

    @CheckBusinessPermission(PermissionConstants.JOIN_OR_QUIT_CLUB)
    public void initMenuJoinOrQuitClub() {
        initMenuItem(R.id.settings_activity_join_or_quit_club);
    }

    public void initMenuItem(int resourceId) {
        mRootView.findViewById(resourceId).setVisibility(View.VISIBLE);
    }


    @OnClick({R.id.menu_work_time, R.id.menu_work_project, R.id.menu_about_us, R.id.menu_suggest, R.id.settings_activity_modify_pw, R.id.settings_activity_join_club,
            R.id.settings_activity_join_or_quit_club, R.id.settings_activity_logout})
    public void onMainMenuSettingClicked(View view) {
        switch (view.getId()) {
            case R.id.menu_work_time:

                Intent intent = new Intent(getActivity(), WorkTimeActivity.class);
                startActivity(intent);
                break;
            case R.id.menu_work_project:

                Intent intentProject = new Intent(getActivity(), ServiceItemActivity.class);
                startActivity(intentProject);
                break;
            case R.id.menu_about_us:
                startActivity(new Intent(getActivity(), AppInfoActivity.class));
                break;
            case R.id.menu_suggest:
                startActivity(new Intent(getActivity(), FeedbackActivity.class));
                break;
            case R.id.settings_activity_modify_pw:
                startActivity(new Intent(getActivity(), ModifyPasswordActivity.class));
                break;
            case R.id.settings_activity_join_club:
                UINavigation.gotoJoinClubForResult(getActivity(), MainActivity.REQUEST_CODE_JOIN_CLUB);
                break;
            case R.id.settings_activity_join_or_quit_club:
                if (!mTech.hasClub()) {
                    //加入会所
                    UINavigation.gotoJoinClubForResult(getActivity(), MainActivity.REQUEST_CODE_JOIN_CLUB);
                } else {
                    //退出会所/取消申请加入会所
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction ft = fragmentManager.beginTransaction();
                    Fragment prev = fragmentManager.findFragmentByTag("quit_club");
                    if (prev != null) {
                        ft.remove(prev);
                    }
                    QuitClubDialogFragment newFragment = new QuitClubDialogFragment();
                    newFragment.setListener(new QuitClubDialogFragment.QuitClubListener() {
                        @Override
                        public void onQuitClubSuccess() {
                            showTechStatus(mTech.getStatus());
                        }
                    });
                    newFragment.show(ft, "quit_club");
                }
                break;
            case R.id.settings_activity_logout:
                new RewardConfirmDialog(getActivity(), "", getString(R.string.logout_tips), "") {
                    @Override
                    public void onConfirmClick() {
                        mTech.logout();
                        ActivityHelper.getInstance().removeAllActivities();
                        UINavigation.gotoLogin(getActivity());
                        getActivity().stopService(new Intent(getActivity(), DataRefreshService.class));
                        super.onConfirmClick();
                    }
                }.show();
                break;
        }
    }

    @CheckBusinessPermission(PermissionConstants.PERSONAL_EDIT)
    public void gotoEditPersonalData() {
        Intent intent = new Intent(getActivity(), TechInfoActivity.class);
        getActivity().startActivityForResult(intent, MainActivity.REQUEST_CODE_EDIT_TECH_INFO);
    }

    @OnClick({R.id.main_page_head, R.id.main_tech_order_all, R.id.main_tech_dynamic_all, R.id.main_tech_who_care_all, R.id.ranking_layout})
    public void onMainPagePieceClicked(View view) {
        switch (view.getId()) {
            case R.id.main_page_head:
                gotoEditPersonalData();
                break;
            case R.id.main_tech_order_all:
                startActivity(new Intent(getActivity(), OrderFragmentActivity.class));
                break;
            case R.id.main_tech_dynamic_all:
                if (hasDynamic) {
                    startActivity(new Intent(getActivity(), DynamicDetailActivity.class));
                } else {
                    Intent intentDynamic = new Intent(getActivity(), DynamicShareTechActivity.class);
                    gotoShareUi(intentDynamic);
                }
                break;
            case R.id.main_tech_who_care_all:
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.switchFragment(2);
                MsgDispatcher.dispatchMessage(MsgDef.MSF_DEF_SET_PAGE_SELECTED, 0);
                break;
            case R.id.layout_technician_ranking:
                String url = SharedPreferenceHelper.getServerHost() + String.format(RequestConstant.URL_RANKING, System.currentTimeMillis(), RequestConstant.USER_TYPE_TECH,
                        RequestConstant.SESSION_TYPE, SharedPreferenceHelper.getUserToken()
                );
                Intent intentRanking = new Intent(getActivity(), BrowserActivity.class);
                intentRanking.putExtra(BrowserActivity.EXTRA_SHOW_MENU, false);
                intentRanking.putExtra(BrowserActivity.EXTRA_URL, url);
                startActivity(intentRanking);
                break;

        }
    }

    @OnClick({R.id.main_too_keen, R.id.main_send_coupon, R.id.main_get_comment, R.id.main_total_income, R.id.pay_notify_header})
    public void onMainDetailClicked(View view) {
        switch (view.getId()) {
            case R.id.main_total_income:
                startActivity(new Intent(getActivity(), TechAccountActivity.class));
                return;
        }
        switch (view.getId()) {
            case R.id.main_too_keen:
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.switchFragment(2);
                ThreadManager.postDelayed(ThreadManager.THREAD_TYPE_MAIN, new Runnable() {
                    @Override
                    public void run() {
                        MsgDispatcher.dispatchMessage(MsgDef.MSF_DEF_SET_PAGE_SELECTED, 1);
                    }
                }, 300);
                break;
            case R.id.main_send_coupon:
                ((BaseFragmentActivity) getActivity()).makeShortToast(getString(R.string.main_no_coupon_alert_message));
                break;
            case R.id.main_get_comment:
                startActivity(new Intent(getActivity(), CommentActivity.class));
                break;
            case R.id.pay_notify_header:
                startActivity(new Intent(getActivity(), OnlinePayNotifyActivity.class));
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_back:
                mMainSlidingLayout.toggle();
                break;
            case R.id.contact_more:
                //分享二维码
                Intent intent = new Intent(getActivity(), TechShareCardActivity.class);
                gotoShareUi(intent);
                break;
        }
    }

    private void gotoShareUi(Intent intent) {
        if (TextUtils.isEmpty(mTech.getQrCodeUrl())) {
            Toast.makeText(getContext(), "二维码为空！", Toast.LENGTH_LONG).show();
            return;
        }
        StringBuilder url;
        if (Utils.isEmpty(mTech.getShareUrl())) {
            url = new StringBuilder(SharedPreferenceHelper.getServerHost());
            url.append(String.format("/spa-manager/spa2/?club=%s#technicianDetail&id=%s&techInviteCode=%s", mTech.getClubId(), mTech.getUserId(), mTech.getInviteCode()));
        } else {
            url = new StringBuilder(mTech.getShareUrl());
        }
        intent.putExtra(Constant.TECH_USER_HEAD_URL, mTech.getAvatarUrl());
        intent.putExtra(Constant.TECH_USER_NAME, mTech.getNickName());
        intent.putExtra(Constant.TECH_USER_TECH_NUM, mTech.getTechNo());
        intent.putExtra(Constant.TECH_USER_CLUB_NAME, mTech.getClubName());
        intent.putExtra(Constant.TECH_SHARE_URL, url.toString());
        intent.putExtra(Constant.TECH_ShARE_CODE_IMG, mTech.getQrCodeUrl());
        intent.putExtra(Constant.TECH_CAN_SHARE, true);
        startActivity(intent);
    }

    // 附近的人:打招呼按钮的点击事件
    @OnClick({R.id.main_nearby_say_hello, R.id.main_nearby_go_toker})
    public void onNearbyClick(View view) {
        switch (view.getId()) {
            case R.id.main_nearby_say_hello:
                // 打开附近的人
                Intent intent = new Intent(getActivity(), NearbyActivity.class);
                startActivity(intent);
                break;
            case R.id.main_nearby_go_toker:
                // 跳转到营销页面
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.switchFragment(3);
                MsgDispatcher.dispatchMessage(MsgDef.MSF_DEF_SET_PAGE_SELECTED, 0);
                break;
        }
    }

    private void showHeadView() {
        if (!TextUtils.isEmpty(mTech.getAvatarUrl())) {
            Glide.with(mContext).load(mTech.getAvatarUrl()).error(R.drawable.icon22).into(mMainHeadAvatar);
        }
        mMainHeadTechName.setText(mTech.getNickName());
        showTechStatus(mTech.getStatus());
    }

    private void initTechWorkView(TechStatisticsDataResult result) {
        mMainInfoTooKeenNumber.setText(result.respData.userCount);
        mMainSendCouponNumber.setText(result.respData.getCouponCount);

        if (result.respData.incomeAmount > 10000) {
            float payMoney = result.respData.incomeAmount / 10000f;
            mMainTotalIncomeNumber.setText(String.format("%1.2f", payMoney) + "万");
        } else {
            mMainTotalIncomeNumber.setText(String.format("%1.2f", result.respData.incomeAmount));
        }

        mMainGetCommentNumber.setText(result.respData.goodCommentCount);
        String textVisit = "今天共有" + result.respData.todayVisitCount + "人看了我～";
        mMainWhoCareTotal.setText(Utils.changeColor(textVisit, ResourceUtils.getColor(R.color.colorMainBtn), 4, textVisit.length() - 5));
    }

    private void initOrderView(OrderListResult orderList) {
        if (orderList.respData != null && orderList.respData.size() > 0) {
            mAllTechOrderList.clear();
            for (int i = 0; i < orderList.respData.size(); i++) {
                if (!orderList.respData.get(i).remainTime.contains("-") && (orderList.respData.get(i).status.equals(RequestConstant.KEY_ORDER_STATUS_SUBMIT) || orderList.respData.get(i).status.equals(RequestConstant.KEY_ORDER_STATUS_ACCEPT))) {
                    if (!(orderList.respData.get(i).orderType.equals(Constant.ORDER_TYPE_PAID) && orderList.respData.get(i).status.equals(RequestConstant.KEY_ORDER_STATUS_ACCEPT))) {
                        mAllTechOrderList.add(orderList.respData.get(i));
                    }
                }
            }
            if (mAllTechOrderList.size() > 5) {
                mTechOrderList.clear();
                for (int i = 0; i < 5; i++) {
                    mTechOrderList.add(mAllTechOrderList.get(i));
                }
            } else {
                mTechOrderList.clear();
                mTechOrderList.addAll(mAllTechOrderList);
            }
            if (mTechOrderList.size() > 0) {
                mOrderFigureOut.setVisibility(View.GONE);
            } else {
                mOrderFigureOut.setVisibility(View.VISIBLE);
            }
            orderListAdapter = new MainPageTechOrderListAdapter(mContext, mTechOrderList);
            mMainOrderList.setAdapter(orderListAdapter);
            setListViewHeightBasedOnChildren(mMainOrderList);
            orderListAdapter.notifyDataSetChanged();
            mMainOrderList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getActivity(), OrderDetailActivity.class);
                    intent.putExtra(OrderDetailActivity.KEY_ORDER, mTechOrderList.get(position));
                    startActivity(intent);
                }
            });
        } else {
            mOrderFigureOut.setVisibility(View.VISIBLE);
            mTechOrderList.clear();
            setListViewHeightBasedOnChildren(mMainOrderList);
            orderListAdapter = new MainPageTechOrderListAdapter(mContext, mTechOrderList);
            mMainOrderList.setAdapter(orderListAdapter);
            orderListAdapter.notifyDataSetChanged();
        }
    }

    private void initDynamicView(DynamicListResult result) {
        if (null != result.respData && result.respData.size() > 0) {
            mVisitNull.setVisibility(View.GONE);
            hasDynamic = true;
            mDynamicList.clear();
            mDynamicList.addAll(result.respData);
            if (mDynamicList.size() == 1) {
                mMainDynamic1.setVisibility(View.VISIBLE);
                Glide.with(mContext).load(Utils.isNotEmpty(mDynamicList.get(0).avatarUrl) ? mDynamicList.get(0).avatarUrl : mDynamicList.get(0).imageUrl).into(mMainDynamicAvatar1);
                mMainDynamicName1.setText(Utils.StrSubstring(6, mDynamicList.get(0).userName, true));
                mMainDynamicDescribe1.setText(getRecentStatusDes(mDynamicList.get(0).bizType));
                mMainDynamicTime1.setText(DateUtils.getTimestampString(new Date(mDynamicList.get(0).createTime)));
                mMainDynamic2.setVisibility(View.GONE);
                mMainDynamic3.setVisibility(View.GONE);
            } else if (mDynamicList.size() == 2) {
                mMainDynamic1.setVisibility(View.VISIBLE);
                Glide.with(mContext).load(Utils.isNotEmpty(mDynamicList.get(0).avatarUrl) ? mDynamicList.get(0).avatarUrl : mDynamicList.get(0).imageUrl).into(mMainDynamicAvatar1);
                mMainDynamicName1.setText(Utils.StrSubstring(6, mDynamicList.get(0).userName, true));
                mMainDynamicDescribe1.setText(getRecentStatusDes(mDynamicList.get(0).bizType));
                mMainDynamicTime1.setText(DateUtils.getTimestampString(new Date(mDynamicList.get(0).createTime)));
                mMainDynamic2.setVisibility(View.VISIBLE);
                Glide.with(mContext).load(Utils.isNotEmpty(mDynamicList.get(1).avatarUrl) ? mDynamicList.get(1).avatarUrl : mDynamicList.get(1).imageUrl).into(mMainDynamicAvatar2);
                mMainDynamicName2.setText(Utils.StrSubstring(6, mDynamicList.get(1).userName, true));
                mMainDynamicDescribe2.setText(getRecentStatusDes(mDynamicList.get(1).bizType));
                mMainDynamicTime2.setText(DateUtils.getTimestampString(new Date(mDynamicList.get(1).createTime)));
                mMainDynamic3.setVisibility(View.GONE);
            } else if (mDynamicList.size() == 3) {
                mMainDynamic1.setVisibility(View.VISIBLE);
                Glide.with(mContext).load(Utils.isNotEmpty(mDynamicList.get(0).avatarUrl) ? mDynamicList.get(0).avatarUrl : mDynamicList.get(0).imageUrl).into(mMainDynamicAvatar1);
                mMainDynamicName1.setText(Utils.StrSubstring(6, mDynamicList.get(0).userName, true));
                mMainDynamicDescribe1.setText(getRecentStatusDes(mDynamicList.get(0).bizType));
                mMainDynamicTime1.setText(DateUtils.getTimestampString(new Date(mDynamicList.get(0).createTime)));
                mMainDynamic2.setVisibility(View.VISIBLE);
                Glide.with(mContext).load(Utils.isNotEmpty(mDynamicList.get(1).avatarUrl) ? mDynamicList.get(1).avatarUrl : mDynamicList.get(1).imageUrl).into(mMainDynamicAvatar2);
                mMainDynamicName2.setText(Utils.StrSubstring(6, mDynamicList.get(1).userName, true));
                mMainDynamicDescribe2.setText(getRecentStatusDes(mDynamicList.get(1).bizType));
                mMainDynamicTime2.setText(DateUtils.getTimestampString(new Date(mDynamicList.get(1).createTime)));
                mMainDynamic3.setVisibility(View.VISIBLE);
                Glide.with(mContext).load(Utils.isNotEmpty(mDynamicList.get(2).avatarUrl) ? mDynamicList.get(2).avatarUrl : mDynamicList.get(2).imageUrl).into(mMainDynamicAvatar3);
                mMainDynamicName3.setText(Utils.StrSubstring(6, mDynamicList.get(2).userName, true));
                mMainDynamicDescribe3.setText(getRecentStatusDes(mDynamicList.get(2).bizType));
                mMainDynamicTime3.setText(DateUtils.getTimestampString(new Date(mDynamicList.get(2).createTime)));
            }
        } else {
            mVisitNull.setVisibility(View.VISIBLE);
            mMainDynamic1.setVisibility(View.GONE);
            mMainDynamic2.setVisibility(View.GONE);
            mMainDynamic3.setVisibility(View.GONE);
            hasDynamic = false;
        }
    }

    private String getRecentStatusDes(int type) {
        switch (type) {
            case 1:
                return ResourceUtils.getString(R.string.recent_status_type_comment);
            case 2:
                return ResourceUtils.getString(R.string.recent_status_type_collect);
            case 3:
                return ResourceUtils.getString(R.string.recent_status_type_coupon);
            case 4:
                return ResourceUtils.getString(R.string.recent_status_type_paid_coupon);
            case 5:
                return ResourceUtils.getString(R.string.recent_status_type_paid);
            default:
                return ResourceUtils.getString(R.string.recent_status_type_comment);
        }
    }

    private void initRecentlyViewView(RecentlyVisitorResult result) {
        if (null != result.respData && result.respData.size() > 0) {

            mAllTechVisitor.clear();
            for (int i = 0; i < result.respData.size(); i++) {
                if (null != result.respData.get(i).emchatId) {
                    mAllTechVisitor.add(result.respData.get(i));
                }
            }
            if (mAllTechVisitor.size() > 5) {
                mTechVisitor.clear();
                for (int i = 0; i < 5; i++) {
                    mTechVisitor.add(mAllTechVisitor.get(i));
                }
            } else {
                mTechVisitor.clear();
                mTechVisitor.addAll(mAllTechVisitor);
            }
            if (mTechVisitor.size() > 0) {
                llHorizontalList.setVisibility(View.VISIBLE);

                initVisitAvatar(mTechVisitor);
            }

        } else {
            llHorizontalList.setVisibility(View.GONE);
            for (int i = 0; i < visitViewList.size(); i++) {
                visitViewList.get(i).setVisibility(View.INVISIBLE);
            }
        }
    }

    private void initVisitAvatar(List<RecentlyVisitorBean> visitList) {
        for (int i = 0; i < visitList.size(); i++) {
            visitViewList.get(i).setVisibility(View.VISIBLE);
            Glide.with(mContext).load(visitList.get(i).avatarUrl).into((CircleImageView) visitViewList.get(i));
            final int finalI = i;
            visitViewList.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RecentlyVisitorBean bean = visitList.get(finalI);
                    Map<String, Object> params = new HashMap<>();
                    params.put(RequestConstant.KEY_REQUEST_TAG, false);
                    params.put(RequestConstant.KEY_NEARBY_CUSTOMER_ID, bean.userId);
                    params.put(RequestConstant.KEY_RECENTLY_VISITOR_BEAN, bean);
                    MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_CONTACT_PERMISSION, params);
                }
            });
        }
    }

    private void handleContactPermission(ContactPermissionWithBeanResult result) {
        RecentlyVisitorBean bean = result.bean;
        if (result.statusCode == 200 && result.respData.echat) {
            // 聊天
            MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_START_CHAT, Utils.wrapChatParams(bean.emchatId,
                    Utils.isEmpty(bean.userNoteName) ? bean.userName : bean.userNoteName, bean.avatarUrl, ""));
        } else {
            // 详情
            if (Long.parseLong(bean.userId) > 0) {
                Intent intent = new Intent(getActivity(), ContactInformationDetailActivity.class);
                intent.putExtra(RequestConstant.KEY_USER_ID, bean.userId);
                intent.putExtra(RequestConstant.CONTACT_TYPE, bean.customerType);
                intent.putExtra(RequestConstant.KEY_TECH_NAME, bean.techName);
                intent.putExtra(RequestConstant.KEY_TECH_No, bean.techSerialNo);
                intent.putExtra(RequestConstant.KEY_CONTACT_TYPE, RequestConstant.TYPE_CUSTOMER);
                intent.putExtra(RequestConstant.KEY_IS_MY_CUSTOMER, false);
                intent.putExtra(RequestConstant.KEY_CAN_SAY_HELLO, bean.canSayHello);
                startActivity(intent);
            } else {
                Utils.makeShortToast(getActivity(), ResourceUtils.getString(R.string.visitor_has_no_message));
            }
        }
    }

    private void initTechRankingView(TechRankDataResult result) {
        if (result.respData == null) {
            return;
        }
        if (null != result.respData.userRanking) {
            Glide.with(mContext).load(result.respData.userRanking.avatarUrl).into(mCvStarRegister);
            mTvStarRegisterUser.setText(result.respData.userRanking.name);
            if (Utils.isNotEmpty(result.respData.userRanking.serialNo)) {
                mTvStarRegisterTechNo.setText(result.respData.userRanking.serialNo);
                mTvStarRegisterTechNo.setVisibility(View.VISIBLE);
            } else {
                mTvStarRegisterTechNo.setVisibility(View.GONE);
            }
        } else {
            mTvStarRegisterUser.setText(ResourceUtils.getString(R.string.rank_null_text));
            mTvStarRegisterTechNo.setVisibility(View.GONE);
        }
        if (null != result.respData.paidRanking) {
            Glide.with(mContext).load(result.respData.paidRanking.avatarUrl).into(mCvStarSales);
            mTvStarSales.setText(result.respData.paidRanking.name);
            if (Utils.isNotEmpty(result.respData.paidRanking.serialNo)) {
                mTvTitleSale.setText(result.respData.paidRanking.serialNo);
            } else {
                mTvTitleSale.setVisibility(View.GONE);
            }

        } else {
            mTvStarSales.setText(ResourceUtils.getString(R.string.rank_null_text));
            mTvTitleSale.setVisibility(View.GONE);
        }
        if (null != result.respData.commentRanking) {
            Glide.with(mContext).load(result.respData.commentRanking.avatarUrl).into(mCvStarService);
            mTvStarService.setText(result.respData.commentRanking.name);
            if (Utils.isNotEmpty(result.respData.commentRanking.serialNo)) {
                mTvTitleService.setText(result.respData.commentRanking.serialNo);
            } else {
                mTvTitleService.setVisibility(View.GONE);
            }
        } else {
            mTvStarService.setText(ResourceUtils.getString(R.string.rank_null_text));
            mTvTitleService.setVisibility(View.GONE);
        }

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }

    public Runnable mTask = new Runnable() {
        @Override
        public void run() {
            loadOrderListData();
        }
    };

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        if (listView == null) {
            return;
        }
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + listView.getDividerHeight() * (listAdapter.getCount() - 1);
        listView.setLayoutParams(params);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


    public void onScrollViewChanged(int l, int t) {
        if (t > Utils.dip2px(getActivity(), 100)) {
            mRlToolBar.setBackgroundColor(ResourceUtils.getColor(R.color.recent_status_reward));
        } else {
            mRlToolBar.setBackgroundColor(ResourceUtils.getColor(R.color.main_tool_bar_bg));
        }
    }

    public void doUpdateTechInfoSuccess() {
        mTech.loadTechInfo();
    }

    @Override
    public void onRefresh() {
        mTech.loadTechInfo();
        sendDataRequest();
    }

    //成功通过会所审核
    private void onEventJoinedClub(EventJoinedClub event) {
        Toast.makeText(getContext(), "成功通过会所审核！", Toast.LENGTH_LONG).show();
        onRefresh(); //刷新界面
    }

    //申请加入会所事件
    private void onEventRequestJoinClub(EventRequestJoinClub event) {
        showTechStatus(mTech.getStatus());
        onRefresh();
    }
}
