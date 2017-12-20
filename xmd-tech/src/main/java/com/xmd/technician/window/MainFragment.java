package com.xmd.technician.window;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.hyphenate.util.DateUtils;
import com.shidou.commonlibrary.widget.ScreenUtils;
import com.shidou.commonlibrary.widget.XToast;
import com.xmd.app.Constants;
import com.xmd.app.event.UserInfoChangedEvent;
import com.xmd.app.widget.CircleAvatarView;
import com.xmd.chat.XmdChat;
import com.xmd.contact.event.SwitchTableToContactRecentEvent;
import com.xmd.contact.event.SwitchTableToContactRegisterEvent;
import com.xmd.m.comment.CommentListActivity;
import com.xmd.m.comment.httprequest.ConstantResources;
import com.xmd.m.network.BaseBean;
import com.xmd.m.network.NetworkSubscriber;
import com.xmd.m.network.XmdNetwork;
import com.xmd.permission.BusinessPermissionManager;
import com.xmd.permission.CheckBusinessPermission;
import com.xmd.permission.PermissionConstants;
import com.xmd.technician.Adapter.MainPageTechOrderListAdapter;
import com.xmd.technician.Adapter.PKRankingAdapter;
import com.xmd.technician.AppConfig;
import com.xmd.technician.Constant;
import com.xmd.technician.R;
import com.xmd.technician.SharedPreferenceHelper;
import com.xmd.technician.bean.DynamicDetail;
import com.xmd.technician.bean.Order;
import com.xmd.technician.bean.RecentlyVisitorBean;
import com.xmd.technician.bean.UserRecentBean;
import com.xmd.technician.clubinvite.ClubInviteActivity;
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.common.UINavigation;
import com.xmd.technician.common.Utils;
import com.xmd.technician.event.EventRequestJoinClub;
import com.xmd.technician.event.MainPageStatistics;
import com.xmd.technician.http.RequestConstant;
import com.xmd.technician.http.SpaService;
import com.xmd.technician.http.gson.AuditConfirmResult;
import com.xmd.technician.http.gson.ContactPermissionVisitorResult;
import com.xmd.technician.http.gson.CustomerUserRecentListResult;
import com.xmd.technician.http.gson.DynamicListResult;
import com.xmd.technician.http.gson.HelloGetTemplateResult;
import com.xmd.technician.http.gson.NearbyCusCountResult;
import com.xmd.technician.http.gson.OrderCountResult;
import com.xmd.technician.http.gson.OrderListResult;
import com.xmd.technician.http.gson.OrderManageResult;
import com.xmd.technician.http.gson.TechInfoResult;
import com.xmd.technician.http.gson.TechPKRankingResult;
import com.xmd.technician.http.gson.TechRankDataResult;
import com.xmd.technician.http.gson.TechStatisticsDataResult;
import com.xmd.technician.http.gson.UpdateWorkStatusResult;
import com.xmd.technician.model.HelloSettingManager;
import com.xmd.technician.model.LoginTechnician;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;
import com.xmd.technician.onlinepaynotify.model.PayNotifyInfo;
import com.xmd.technician.onlinepaynotify.view.OnlinePayNotifyActivity;
import com.xmd.technician.onlinepaynotify.view.OnlinePayNotifyFragment;
import com.xmd.technician.widget.CircleImageView;
import com.xmd.technician.widget.RewardConfirmDialog;
import com.xmd.technician.widget.SlidingMenu;
import com.xmd.technician.widget.SwitchButton;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscription;

/**
 * Created by Lhj on 2016/10/19.
 */
public class MainFragment extends BaseFragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {


    @BindView(R.id.rl_toolbar)
    RelativeLayout mRlToolBar;
    @BindView(R.id.app_version)
    TextView mAppVersion;
    @BindView(R.id.join_or_quit_club)
    TextView mJoinOrQuitClub;
    @BindView(R.id.img_join_or_quit_club)
    ImageView imgJoinOrQuitClub;
    @BindView(R.id.settings_activity_join_or_quit_club)
    RelativeLayout mMenuJoinOrQuitClub;
    @BindView(R.id.menu_club_name)
    TextView mMenuClubName;
    @BindView(R.id.main_head_avatar)
    CircleImageView mMainHeadAvatar;
    @BindView(R.id.main_head_tech_name)
    TextView mMainHeadTechName;
    @BindView(R.id.main_head_tech_serial)
    TextView mMainHeadTechSerial;
    @BindView(R.id.btn_main_tech_rest)
    ImageView mBtnMainTechRest;
    @BindView(R.id.btn_main_tech_busy)
    ImageView mBtnMainTechBusy;
    @BindView(R.id.btn_main_tech_free)
    ImageView mBtnMainTechFree;
    @BindView(R.id.main_info_too_keen_number)
    TextView mMainInfoTooKeenNumber;
    @BindView(R.id.main_send_coupon_number)
    TextView mMainSendCouponNumber;
    @BindView(R.id.main_get_comment_number)
    TextView mMainGetCommentNumber;
    @BindView(R.id.main_total_income_number)
    TextView mMainTotalIncomeNumber;
    @BindView(R.id.main_order_list)
    RecyclerView mMainOrderList;
    @BindView(R.id.main_dynamic_avatar1)
    CircleAvatarView mMainDynamicAvatar1;
    @BindView(R.id.main_dynamic_name1)
    TextView mMainDynamicName1;
    @BindView(R.id.main_dynamic_describe1)
    TextView mMainDynamicDescribe1;
    @BindView(R.id.main_dynamic_time1)
    TextView mMainDynamicTime1;
    @BindView(R.id.main_dynamic_avatar2)
    CircleAvatarView mMainDynamicAvatar2;
    @BindView(R.id.main_dynamic_name2)
    TextView mMainDynamicName2;
    @BindView(R.id.main_dynamic_describe2)
    TextView mMainDynamicDescribe2;
    @BindView(R.id.main_dynamic_time2)
    TextView mMainDynamicTime2;
    @BindView(R.id.main_dynamic_avatar3)
    CircleAvatarView mMainDynamicAvatar3;
    @BindView(R.id.main_dynamic_name3)
    TextView mMainDynamicName3;
    @BindView(R.id.main_dynamic_describe3)
    TextView mMainDynamicDescribe3;
    @BindView(R.id.main_dynamic_time3)
    TextView mMainDynamicTime3;
    @BindView(R.id.main_who_care_total)
    TextView mMainWhoCareTotal;
    @BindView(R.id.layout_visitor_list)
    LinearLayout layoutVisitorList;
    @BindView(R.id.cv_star_register)
    CircleImageView mCvStarRegister;
    @BindView(R.id.tv_star_register_user)
    TextView mTvStarRegisterUser;
    @BindView(R.id.tv_star_register_tech_no)
    TextView mTvStarRegisterTechNo;
    @BindView(R.id.cv_star_sales)
    CircleImageView mCvStarSales;
    @BindView(R.id.tv_star_sales)
    TextView mTvStarSales;
    @BindView(R.id.tv_title_sale)
    TextView mTvTitleSale;
    @BindView(R.id.cv_star_service)
    CircleImageView mCvStarService;
    @BindView(R.id.tv_star_service)
    TextView mTvStarService;
    @BindView(R.id.tv_title_service)
    TextView mTvTitleService;
    @BindView(R.id.main_scroll_view)
    NestedScrollView mMainScrollView;
    @BindView(R.id.main_sliding_layout)
    SlidingMenu mMainSlidingLayout;
    @BindView(R.id.main_dynamic1)
    LinearLayout mMainDynamic1;
    @BindView(R.id.main_dynamic2)
    LinearLayout mMainDynamic2;
    @BindView(R.id.main_dynamic3)
    LinearLayout mMainDynamic3;
    @BindView(R.id.swipe_refresh_widget)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.rl_visit_null)
    RelativeLayout mVisitNull;
    @BindView(R.id.main_head_tech_status)
    TextView mTechStatus;
    @BindView(R.id.order_figure_out)
    TextView mOrderFigureOut;
    @BindView(R.id.team_list)
    RecyclerView mTeamList;
    @BindView(R.id.ranking_more)
    TextView mRankingMore;

    // 附近的人
    @BindView(R.id.nearby_layout)
    RelativeLayout mNearbyLayout;
    @BindView(R.id.main_nearby_position)
    TextView mNearbyPosition;
    @BindView(R.id.main_nearby_desc)
    TextView mNearbyDesc;
    @BindView(R.id.main_nearby_say_hello)
    Button mNearbySayHello;
    @BindView(R.id.main_nearby_go_toker)
    Button mNearbyGoToker;

    //支付通知
    @BindView(R.id.online_pay_notify_layout)
    View mPayNotifyLayout;
    @BindView(R.id.pay_notify_header)
    RelativeLayout mPayNotifyHeader;
    //声音，震动
    @BindView(R.id.switch_sound)
    SwitchButton mSwitchSound;
    @BindView(R.id.switch_vibrate)
    SwitchButton mSwitchVibrate;
    //空View
    @BindView(R.id.view_transparent)
    View mViewTransparent;
    @BindView(R.id.menu_club_invite)
    View mMenuClubInvite;
    @BindView(R.id.menu_club_invite_count)
    TextView mMenuClubInviteCount;
    //邀请入职
    @BindView(R.id.tech_audit_view)
    LinearLayout techAuditView;
    @BindView(R.id.ll_audit_des)
    LinearLayout llAuditDes;
    @BindView(R.id.tv_tech_status_des)
    TextView tvTechStatusDes;
    @BindView(R.id.club_avatar)
    CircleImageView clubAvatar;
    @BindView(R.id.tv_club_name)
    TextView tvClubName;
    @BindView(R.id.tv_tech_role)
    TextView tvTechRole;
    @BindView(R.id.tv_tech_num)
    TextView tvTechNum;
    @BindView(R.id.ll_handle_status_audit)
    LinearLayout llHandleStatusAudit;
    @BindView(R.id.ll_audit_cancel)
    LinearLayout llAuditCancel;
    @BindView(R.id.ll_audit_confirm)
    LinearLayout llAuditConfirm;
    @BindView(R.id.ll_handle_status_reject)
    LinearLayout llHandleStatusReject;
    @BindView(R.id.ll_audit_apply)
    LinearLayout llAuditApply;
    @BindView(R.id.ll_audit_sure)
    LinearLayout llAuditSure;
    @BindView(R.id.menu_work_project)
    RelativeLayout menuWorkProject;


    private OnlinePayNotifyFragment mPayNotifyFragment;

    private final static int REQUEST_CODE_CLUB_POSITION_INVITE = 100;


    private ImageView imageLeft, imageRight;
    private Context mContext;
    private List<Order> mAllTechOrderList = new ArrayList<>();
    private List<Order> mTechOrderList = new ArrayList<>();
    private List<UserRecentBean> mAllTechVisitor = new ArrayList<>();
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
    private Subscription mRequestJoinClubSubscription;
    private Subscription mGetNearbyCusCountSubscription;    // 附近的人:获取会所附近客户数量;
    private Subscription mGetHelloSetTemplateSubscription;  // 获取打招呼内容
    private Subscription mContactPermissionVisitorSubscription; // 获取聊天限制
    private Subscription mTechPKRankingSubscription;
    private Subscription mUpdateWorkStatusSubscription;
    private Subscription mTechOrderCountSubscription;
    private Subscription mTechAuditConfirmSubscription; // 申请被拒，确认

    private LoginTechnician mTech = LoginTechnician.getInstance();
    private HelloSettingManager mHelloSettingManager = HelloSettingManager.getInstance();

    private View mRootView;
    private boolean isHasPk;
    private boolean isInitNormalRanking;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.main_fragment, container, false);
        mContext = getActivity();
        ButterKnife.bind(this, mRootView);
        initView(mRootView);
        registerRequestHandlers(); //注册监听器
        initStatistic();
        initOnlinePay();
        initOrder();
        initVisitor();
        initMoment();
        initCredit();
        initNearbyUser();
        showHeadView();
        //   HeartBeatTimer.getInstance().start(60, mTask);
        initPkRanking();
        initClubInvite();
        sendDataRequest();
        return mRootView;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().post(new MainPageStatistics(Constants.UMENG_STATISTICS_HOME_BROWSE));
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unsubscribe(
                mGetTechCurrentInfoSubscription,
                mGetTechOrderListSubscription,
                mGetTechStatisticsDataSubscription,
                mGetTechRankIndexDataSubscription,
                mOrderManageSubscription,
                mGetMomentListSubscription,
                mGetRecentlyVisitorSubscription,
                mRequestJoinClubSubscription,
                mGetNearbyCusCountSubscription,
                mGetHelloSetTemplateSubscription,
                mContactPermissionVisitorSubscription,
                mTechPKRankingSubscription,
                mUpdateWorkStatusSubscription,
                mTechOrderCountSubscription,
                mTechAuditConfirmSubscription);
    }

    private void initView(View view) {
        initTitleView(view);
        initMenu();

        mSwipeRefreshLayout.setColorSchemeResources(R.color.color_main_btn);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        mMainScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                // onScrollViewChanged(scrollX, scrollY);
            }
        });
        mMainSlidingLayout.setOnCloseOrOpenListener(new SlidingMenu.CloseOrOpenListener() {
            @Override
            public void isOpen(boolean isOpen) {
                if (isOpen) {
                    mViewTransparent.setVisibility(View.VISIBLE);
                } else {
                    mViewTransparent.setVisibility(View.GONE);
                }
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
        loadVisitor();
        loadMomentData();
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_SET_TEMPLATE);// 技师登录进入首页后,获取打招呼内容
        loadRankingData();
        getClubInviteCount();
    }


    private void initTitleView(View view) {
        view.findViewById(R.id.toolbar).setBackgroundColor(Color.parseColor("#FF826c"));
        ((TextView) view.findViewById(R.id.toolbar_title)).setText(R.string.main_page);
        view.findViewById(R.id.divide_line).setVisibility(View.GONE);
        mContactMore = (LinearLayout) view.findViewById(R.id.contact_more);
        mContactMore.setVisibility(View.VISIBLE);
        mContactMore.setOnClickListener(this);
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

        mRequestJoinClubSubscription = RxBus.getInstance().toObservable(EventRequestJoinClub.class).subscribe(this::onEventRequestJoinClub);

        // 打招呼:获取打招呼内容
        mGetHelloSetTemplateSubscription = RxBus.getInstance().toObservable(HelloGetTemplateResult.class).subscribe(helloGetTemplateResult -> {
            handleSetTemplateResult(helloGetTemplateResult);
        });

        mContactPermissionVisitorSubscription = RxBus.getInstance().toObservable(ContactPermissionVisitorResult.class).subscribe(contactPermissionVisitorResult -> {
            handleContactPermissionVisitor(contactPermissionVisitorResult);
        });

        mTechOrderCountSubscription = RxBus.getInstance().toObservable(OrderCountResult.class).subscribe(orderCountResult -> handleOrderCount(orderCountResult));

        mTechAuditConfirmSubscription = RxBus.getInstance().toObservable(AuditConfirmResult.class).subscribe(
                result -> handleAuditConfirmResult(result)
        );

    }

    private void handleAuditConfirmResult(AuditConfirmResult result) {
        if (result.statusCode == 200) {
            mTech.loadTechInfo();
        }
    }


    @CheckBusinessPermission((PermissionConstants.QR_CODE))
    public void initQRCode() {
        imageRight.setVisibility(View.VISIBLE);
        imageRight.setImageDrawable(ResourceUtils.getDrawable(R.drawable.btn_main_qr_code));
    }

    /**************************
     * 统计数据
     ***************************/
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

    /**************************
     * 在线买单
     ***************************/
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
    }

    /**************************
     * 订单
     ***************************/
    @CheckBusinessPermission(PermissionConstants.ORDER)
    public void initOrder() {
        mRootView.findViewById(R.id.order_layout).setVisibility(View.VISIBLE);
        orderListAdapter = new MainPageTechOrderListAdapter(getActivity(), mTechOrderList);
        mMainOrderList.setLayoutManager(new LinearLayoutManager(getContext()));
        mMainOrderList.setHasFixedSize(true);
        mMainOrderList.setNestedScrollingEnabled(false);
        mMainOrderList.setAdapter(orderListAdapter);
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

    /**************************
     * 最近访客
     ***************************/
    @CheckBusinessPermission(PermissionConstants.VISITOR)
    public void initVisitor() {
        mRootView.findViewById(R.id.visitor_layout).setVisibility(View.VISIBLE);
        if (mGetRecentlyVisitorSubscription == null) {
            mGetRecentlyVisitorSubscription = RxBus.getInstance().toObservable(CustomerUserRecentListResult.class).subscribe(
                    this::initRecentlyViewView);
        }
    }

    @CheckBusinessPermission(PermissionConstants.VISITOR)
    public void loadVisitor() {
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_NEARBY_CUS_COUNT);
    }

    /**************************
     * 动态
     ***************************/
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


    /**************************
     * 排行榜无PK
     ***************************/
    @CheckBusinessPermission(PermissionConstants.RANKING_TECHNICIAN)
    public void initRanking() {
        mRootView.findViewById(R.id.layout_technician_ranking).setVisibility(View.VISIBLE);
        mGetTechRankIndexDataSubscription = RxBus.getInstance().toObservable(TechRankDataResult.class).subscribe(
                this::initTechRankingView);
    }

    @CheckBusinessPermission(PermissionConstants.RANKING_TECHNICIAN)
    public void initPkRanking() {
        mTechPKRankingSubscription = RxBus.getInstance().toObservable(TechPKRankingResult.class).subscribe(
                techPKRankingResult -> handleTechPKRankingView(techPKRankingResult)
        );
        isInitNormalRanking = false;
    }


    @CheckBusinessPermission(PermissionConstants.RANKING_TECHNICIAN)
    public void loadRankingData() {
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_TECH_PK_RANKING);

    }

    /**************************
     * 积分中心
     ***************************/
    @CheckBusinessPermission(PermissionConstants.CREDIT)
    public void initCredit() {
        mRootView.findViewById(R.id.btn_main_credit_center).setVisibility(View.VISIBLE);
    }

    /**************************
     * 工作状态
     ***************************/
    @CheckBusinessPermission(PermissionConstants.WORK_STATUS)
    public void initWorkStatus() {
        mRootView.findViewById(R.id.work_status_layout).setVisibility(View.VISIBLE);
        mRootView.findViewById(R.id.menu_work_project).setVisibility(View.VISIBLE);
    }


    /**************************
     * 附近的人
     ***************************/
    @CheckBusinessPermission(PermissionConstants.NEARBY_USER)
    public void initNearbyUser() {
        mRootView.findViewById(R.id.nearby_layout).setVisibility(View.VISIBLE);
        // 附近的人:订阅会所附近客户数量的事件监听
        mGetNearbyCusCountSubscription = RxBus.getInstance().toObservable(NearbyCusCountResult.class).subscribe(
                this::handleNearbyStatus);
    }

    /*******************************
     * 入职邀请
     */
    public void initClubInvite() {
        if (!LoginTechnician.getInstance().isActiveStatus()) {
            mMenuClubInvite.setVisibility(View.VISIBLE);
        } else {
            mMenuClubInvite.setVisibility(View.GONE);
        }
    }

    private void getClubInviteCount() {
        if (LoginTechnician.getInstance().isActiveStatus()) {
            return;
        }
        mMenuClubInviteCount.setVisibility(View.GONE);
        Observable<BaseBean<Integer>> observable = XmdNetwork.getInstance()
                .getService(SpaService.class)
                .getClubInviteCount("inviting");
        XmdNetwork.getInstance().request(observable, new NetworkSubscriber<BaseBean<Integer>>() {
            @Override
            public void onCallbackSuccess(BaseBean<Integer> result) {
                Integer count = result.getRespData();
                if (count != null && count > 0) {
                    mMenuClubInviteCount.setVisibility(View.VISIBLE);
                    mMenuClubInviteCount.setText(String.valueOf(count));
                }
            }

            @Override
            public void onCallbackError(Throwable e) {

            }
        });
    }


    private void handleTechCurrentResult(TechInfoResult result) {
        mSwipeRefreshLayout.setRefreshing(false);
        if (result.respData == null) {
            return;
        }
        if (result.statusCode >= 200 && result.statusCode <= 299) {
            mTech.onLoadTechInfo(result);
            showHeadView();
            EventBus.getDefault().post(new UserInfoChangedEvent(result.respData.imageUrl));
        }
    }

    public void showTechStatus(String status) {
        mMainHeadTechSerial.setVisibility(View.GONE);
        menuWorkProject.setVisibility(View.GONE);
        mTechStatus.setVisibility(View.GONE);
        mJoinOrQuitClub.setText("退出会所");
        imgJoinOrQuitClub.setImageResource(R.drawable.icon_quit);
        mMenuClubName.setText(Utils.StrSubstring(6, mTech.getClubName(), true));
        showStatusAuditView(status);
        switch (status) {
            case Constant.TECH_STATUS_VALID:
                mTechStatus.setVisibility(View.VISIBLE);
                mTechStatus.setText(ResourceUtils.getString(R.string.join_club_before));
                mJoinOrQuitClub.setText("加入会所");
                mMenuClubName.setText("");
                imgJoinOrQuitClub.setImageResource(R.drawable.icon_join);
                break;
            case Constant.TECH_STATUS_REJECT:
                mJoinOrQuitClub.setText("加入会所");
                mMenuClubName.setText("");
                imgJoinOrQuitClub.setImageResource(R.drawable.icon_join);
                break;
            case Constant.TECH_STATUS_UNCERT:
                mJoinOrQuitClub.setText("取消申请");
                mMenuClubName.setText(String.format("%s（待审核）", Utils.StrSubstring(6, mTech.getClubName(), true)));
                break;
            case Constant.TECH_STATUS_FREE:
                initWorkStatus();
                mMainHeadTechSerial.setText(mTech.getTechNo());
                mMainHeadTechSerial.setVisibility(TextUtils.isEmpty(mTech.getTechNo()) ? View.GONE : View.VISIBLE);
                resetTechStatusView(R.id.btn_main_tech_free);
                break;
            case Constant.TECH_STATUS_BUSY:
                initWorkStatus();
                mMainHeadTechSerial.setText(mTech.getTechNo());
                mMainHeadTechSerial.setVisibility(TextUtils.isEmpty(mTech.getTechNo()) ? View.GONE : View.VISIBLE);
                resetTechStatusView(R.id.btn_main_tech_busy);
                break;
            case Constant.TECH_STATUS_REST:
                initWorkStatus();
                mMainHeadTechSerial.setText(mTech.getTechNo());
                mMainHeadTechSerial.setVisibility(TextUtils.isEmpty(mTech.getTechNo()) ? View.GONE : View.VISIBLE);
                resetTechStatusView(R.id.btn_main_tech_rest);
                break;
            default:
                break;
        }

    }

    private void showStatusAuditView(String status) {
        switch (status) {
            case Constant.TECH_STATUS_UNCERT:
                mRootView.findViewById(R.id.statistic_layout).setVisibility(View.GONE);
                techAuditView.setVisibility(View.VISIBLE);
                llAuditDes.setBackgroundResource(R.drawable.state_bg_default);
                tvTechStatusDes.setText(ResourceUtils.getString(R.string.tech_wait_audit));
                Glide.with(getActivity()).load(mTech.getClubImageUrl()).error(R.drawable.icon22).into(clubAvatar);
                tvClubName.setText(mTech.getClubName());
                tvTechRole.setText(mTech.getRoles().equals("tech") ? "技师" : "楼面");
                tvTechNum.setText(TextUtils.isEmpty(mTech.getTechNo()) ? ResourceUtils.getString(R.string.tech_num_default) : mTech.getTechNo());
                llHandleStatusAudit.setVisibility(View.VISIBLE);
                llHandleStatusReject.setVisibility(View.GONE);
                break;
            case Constant.TECH_STATUS_REJECT:
                mRootView.findViewById(R.id.statistic_layout).setVisibility(View.GONE);
                techAuditView.setVisibility(View.VISIBLE);
                llAuditDes.setBackgroundResource(R.drawable.state_bg_refuse);
                tvTechStatusDes.setText(ResourceUtils.getString(R.string.tech_club_reject));
                Glide.with(getActivity()).load(mTech.getClubImageUrl()).error(R.drawable.icon22).into(clubAvatar);
                tvClubName.setText(mTech.getClubName());
                tvTechRole.setText(mTech.getRoles().equals("tech") ? "技师" : "楼面");
                tvTechNum.setText(TextUtils.isEmpty(mTech.getTechNo()) ? ResourceUtils.getString(R.string.tech_num_default) : mTech.getTechNo());
                llHandleStatusAudit.setVisibility(View.GONE);
                llHandleStatusReject.setVisibility(View.VISIBLE);
                break;
            case Constant.TECH_STATUS_VALID:
            case Constant.TECH_STATUS_FREE:
            case Constant.TECH_STATUS_BUSY:
            case Constant.TECH_STATUS_REST:
                mRootView.findViewById(R.id.statistic_layout).setVisibility(View.VISIBLE);
                techAuditView.setVisibility(View.GONE);
                break;
        }

    }

    @OnClick({R.id.ll_audit_cancel, R.id.ll_audit_confirm, R.id.ll_audit_apply, R.id.ll_audit_sure})
    public void onAuditManager(View view) {
        switch (view.getId()) {
            case R.id.ll_audit_cancel: //取消申请
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction ft = fragmentManager.beginTransaction();
                Fragment prev = fragmentManager.findFragmentByTag("quit_club");
                if (prev != null) {
                    ft.remove(prev);
                }
                QuitClubDialogFragment newFragment = new QuitClubDialogFragment();
                newFragment.setListener(() -> showTechStatus(mTech.getStatus()));
                newFragment.show(ft, "quit_club");
                break;
            case R.id.ll_audit_confirm: //修改申请
                UINavigation.gotoJoinClubForResult(getActivity(), MainActivity.REQUEST_CODE_JOIN_CLUB);
                break;
            case R.id.ll_audit_apply: //再次申请
                UINavigation.gotoJoinClubForResult(getActivity(), MainActivity.REQUEST_CODE_JOIN_CLUB);
                break;
            case R.id.ll_audit_sure: //拒绝确认
                MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_TECH_AUDIT_CONFIRM);
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
                mNearbyDesc.setText(Utils.changeColor(textCount, ResourceUtils.getColor(R.color.color_main_btn), 3, textCount.length() - 10));
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
                    updateWorkTimeResult(RequestConstant.KEY_TECH_STATUS_FREE);
                }
                break;
            case R.id.btn_main_tech_busy:
                if (Utils.isNotEmpty(mTech.getInnerProvider())) {
                    ((BaseFragmentActivity) getActivity()).makeShortToast(getString(R.string.main_fragment_tech_status_select));
                } else {
                    updateWorkTimeResult(RequestConstant.KEY_TECH_STATUS_BUSY);
                }
                break;
            case R.id.btn_main_tech_rest:
                if (Utils.isNotEmpty(mTech.getInnerProvider())) {
                    ((BaseFragmentActivity) getActivity()).makeShortToast(getString(R.string.main_fragment_tech_status_select));
                } else {
                    MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_TECH_ORDER_COUNT, Constant.ORDER_PENDING_TREATMENT);
                }
                break;
            case R.id.btn_main_credit_center:
                UINavigation.gotoCreditCenter(getActivity());
                break;
        }
    }

    private void resetTechStatusView(int id) {
        switch (id) {
            case R.id.btn_main_tech_free:
                mBtnMainTechFree.setImageDrawable(ResourceUtils.getDrawable(R.drawable.btn_main_free_selected));
                mBtnMainTechBusy.setImageDrawable(ResourceUtils.getDrawable(R.drawable.btn_main_busy_default));
                mBtnMainTechRest.setImageDrawable(ResourceUtils.getDrawable(R.drawable.btn_main_rest_default));
                break;
            case R.id.btn_main_tech_busy:
                mBtnMainTechFree.setImageDrawable(ResourceUtils.getDrawable(R.drawable.btn_main_free_default));
                mBtnMainTechBusy.setImageDrawable(ResourceUtils.getDrawable(R.drawable.btn_main_busy_selected));
                mBtnMainTechRest.setImageDrawable(ResourceUtils.getDrawable(R.drawable.btn_main_rest_default));
                break;
            case R.id.btn_main_tech_rest:
                mBtnMainTechFree.setImageDrawable(ResourceUtils.getDrawable(R.drawable.btn_main_free_default));
                mBtnMainTechBusy.setImageDrawable(ResourceUtils.getDrawable(R.drawable.btn_main_busy_default));
                mBtnMainTechRest.setImageDrawable(ResourceUtils.getDrawable(R.drawable.btn_main_rest_selected));
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
        initMenuHelloSetting();
        initMenuAbout();
        initMenuSuggest();
        initMenuChangePassword();
        initMenuJoinOrQuitClub();
    }

    @CheckBusinessPermission(PermissionConstants.WORK_TIME)
    public void initMenuWorkTime() {
        initMenuItem(R.id.menu_work_time);
    }

    @CheckBusinessPermission(PermissionConstants.NEARBY_USER)
    public void initMenuHelloSetting() {
        initMenuItem(R.id.menu_hello_setting);
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


    @OnClick({R.id.menu_work_time, R.id.menu_work_project, R.id.menu_hello_setting, R.id.menu_about_us, R.id.menu_suggest, R.id.settings_activity_modify_pw, R.id.settings_activity_join_club,
            R.id.settings_activity_join_or_quit_club, R.id.settings_activity_logout, R.id.view_transparent, R.id.menu_fast_reply, R.id.menu_club_invite})
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
            case R.id.menu_hello_setting:
                Intent intentHelloSetting = new Intent(getActivity(), HelloSettingActivity.class);
                startActivity(intentHelloSetting);
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
                    newFragment.setListener(() -> showTechStatus(mTech.getStatus()));
                    newFragment.show(ft, "quit_club");
                }
                break;
            case R.id.settings_activity_logout:
                new RewardConfirmDialog(getActivity(), "", getString(R.string.logout_tips), "") {
                    @Override
                    public void onConfirmClick() {
                        mTech.logout();
                        super.onConfirmClick();
                    }
                }.show();
                break;
            case R.id.menu_fast_reply:
                XmdChat.getInstance().showFastReplyEditView();
                break;
            case R.id.view_transparent:
                mMainSlidingLayout.closeMenu();
                break;
            case R.id.menu_club_invite:
                Intent clubInviteIntent = new Intent(getContext(), ClubInviteActivity.class);
                startActivityForResult(clubInviteIntent, REQUEST_CODE_CLUB_POSITION_INVITE);
                break;
        }
    }

    @CheckBusinessPermission(PermissionConstants.PERSONAL_EDIT)
    public void gotoEditPersonalData() {
        Intent intent = new Intent(getActivity(), TechUserCenterActivity.class);
        getActivity().startActivityForResult(intent, MainActivity.REQUEST_CODE_EDIT_TECH_INFO);
    }

    @OnClick({R.id.main_page_head, R.id.main_tech_order_all, R.id.main_tech_dynamic_all, R.id.main_tech_who_care_all, R.id.layout_technician_ranking, R.id.layout_technician_pk_ranking})
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
                if (mainActivity.getFragmentSize() == 5) {
                    mainActivity.switchFragment(3);
                    EventBus.getDefault().post(new SwitchTableToContactRecentEvent());
                } else if (mainActivity.getFragmentSize() == 4) {
                    mainActivity.switchFragment(2);
                    EventBus.getDefault().post(new SwitchTableToContactRecentEvent());
                }
                break;
            case R.id.layout_technician_ranking:
                if (isHasPk) {
                    startActivity(new Intent(getActivity(), TechPKActiveActivity.class));
                } else {
                    Intent personalRanking = new Intent(getActivity(), TechPersonalRankingDetailActivity.class);
                    startActivity(personalRanking);
                }
                break;
            case R.id.layout_technician_pk_ranking:
                startActivity(new Intent(getActivity(), TechPKActiveActivity.class));
                break;

        }
    }

    @OnClick({R.id.main_too_keen, R.id.main_send_coupon, R.id.main_get_comment, R.id.main_total_income, R.id.pay_notify_header})
    public void onMainDetailClicked(View view) {
        switch (view.getId()) {
            case R.id.main_total_income:
                startActivity(new Intent(getActivity(), TechAccountActivity.class));
                break;
            case R.id.main_too_keen:
                MainActivity mainActivity = (MainActivity) getActivity();
                if (mainActivity.getFragmentSize() == 5) {
                    mainActivity.switchFragment(3);
                    EventBus.getDefault().post(new SwitchTableToContactRegisterEvent());
                } else if (mainActivity.getFragmentSize() == 4) {
                    mainActivity.switchFragment(2);
                    EventBus.getDefault().post(new SwitchTableToContactRegisterEvent());
                }

                break;
            case R.id.main_send_coupon:
                ((BaseFragmentActivity) getActivity()).makeShortToast(getString(R.string.main_no_coupon_alert_message));
                break;
            case R.id.main_get_comment:
                CommentListActivity.startCommentListActivity(getActivity(), false, mTech.getUserId());
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
                EventBus.getDefault().post((new MainPageStatistics(Constants.UMENG_STATISTICS_NEARBY_CLICK)));
                Intent intent = new Intent(getActivity(), NearbyActivity.class);
                startActivity(intent);
                break;
            case R.id.main_nearby_go_toker:
                // 跳转到营销页面
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.switchFragment(3);
                break;
        }
    }

    private void showHeadView() {
        if (!TextUtils.isEmpty(mTech.getAvatarUrl())) {
            Glide.with(mContext).load(mTech.getAvatarUrl()).error(R.drawable.icon22).into(mMainHeadAvatar);
        }
        mMainHeadTechName.setText(Utils.StrSubstring(10, mTech.getNickName(), true));
        showTechStatus(mTech.getStatus());
        mUpdateWorkStatusSubscription = RxBus.getInstance().toObservable(UpdateWorkStatusResult.class).subscribe(
                updateWorkStatusResult -> handlerUpdateStatusResult(updateWorkStatusResult));
    }

    private void handlerUpdateStatusResult(UpdateWorkStatusResult result) {
        if (result.statusCode == 200) {
            switch (result.targetStatus) {
                case Constant.TECH_STATUS_FREE:
                    resetTechStatusView(R.id.btn_main_tech_free);
                    break;
                case Constant.TECH_STATUS_BUSY:
                    resetTechStatusView(R.id.btn_main_tech_busy);
                    break;
                case Constant.TECH_STATUS_REST:
                    resetTechStatusView(R.id.btn_main_tech_rest);
                    break;
            }
        } else {
            XToast.show(result.msg);
        }
    }

    private void initTechWorkView(TechStatisticsDataResult result) {
        if (result.respData == null) {
            XToast.show("加载统计数据失败");
            return;
        }
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
        mMainWhoCareTotal.setText(Utils.changeColor(textVisit, ResourceUtils.getColor(R.color.color_main_btn), 4, textVisit.length() - 5));
    }

    private void initOrderView(OrderListResult orderList) {
        if (orderList.isIndexPage.equals("N")) {
            return;
        }
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
            orderListAdapter.setData(mTechOrderList);
            orderListAdapter.setOnItemClickedListener(new MainPageTechOrderListAdapter.ItemClickedInterface() {
                @Override
                public void itemClicked(Order bean) {
                    Intent intent = new Intent(getActivity(), OrderDetailActivity.class);
                    intent.putExtra(OrderDetailActivity.KEY_ORDER, bean);
                    startActivity(intent);
                }
            });
        } else {
            mOrderFigureOut.setVisibility(View.VISIBLE);
            mTechOrderList.clear();
            orderListAdapter.setData(mTechOrderList);
        }
    }

    private void initDynamicView(DynamicListResult result) {
        if (null != result.respData && result.respData.size() > 0) {
            mVisitNull.setVisibility(View.GONE);
            hasDynamic = true;
            mDynamicList.clear();
            mDynamicList.addAll(result.respData);
            if (mDynamicList.size() > 0) {
                mMainDynamic1.setVisibility(View.VISIBLE);
                if (Utils.isNotEmpty(mDynamicList.get(0).userName) && mDynamicList.get(0).userName.endsWith("**(匿名)")) {
                    mMainDynamicAvatar1.setImageResource(R.drawable.img_default_avatar);
                } else {
                    mMainDynamicAvatar1.setUserInfo(mDynamicList.get(0).userId, Utils.isNotEmpty(mDynamicList.get(0).avatarUrl) ? mDynamicList.get(0).avatarUrl : mDynamicList.get(0).imageUrl, false);
                }
                mMainDynamicName1.setText(TextUtils.isEmpty(mDynamicList.get(0).userName) ? "匿名用户" : Utils.StrSubstring(6, mDynamicList.get(0).userName, true));
                mMainDynamicDescribe1.setText(getRecentStatusDes(mDynamicList.get(0).bizType));
                mMainDynamicTime1.setText(DateUtils.getTimestampString(new Date(mDynamicList.get(0).createTime)));
                mMainDynamic2.setVisibility(View.GONE);
                mMainDynamic3.setVisibility(View.GONE);
            }
            if (mDynamicList.size() > 1) {
                mMainDynamic2.setVisibility(View.VISIBLE);
                if (Utils.isNotEmpty(mDynamicList.get(1).userName) && mDynamicList.get(1).userName.endsWith("**(匿名)")) {
                    mMainDynamicAvatar2.setImageResource(R.drawable.img_default_avatar);
                } else {
                    mMainDynamicAvatar2.setUserInfo(mDynamicList.get(1).userId, Utils.isNotEmpty(mDynamicList.get(1).avatarUrl) ? mDynamicList.get(1).avatarUrl : mDynamicList.get(1).imageUrl, false);
                }

                mMainDynamicName2.setText(TextUtils.isEmpty(mDynamicList.get(1).userName) ? "匿名用户" : Utils.StrSubstring(6, mDynamicList.get(1).userName, true));
                mMainDynamicDescribe2.setText(getRecentStatusDes(mDynamicList.get(1).bizType));
                mMainDynamicTime2.setText(DateUtils.getTimestampString(new Date(mDynamicList.get(1).createTime)));
                mMainDynamic3.setVisibility(View.GONE);
            }
            if (mDynamicList.size() > 2) {
                mMainDynamic3.setVisibility(View.VISIBLE);
                if (Utils.isNotEmpty(mDynamicList.get(2).userName) && mDynamicList.get(2).userName.endsWith("**(匿名)")) {
                    mMainDynamicAvatar2.setImageResource(R.drawable.img_default_avatar);
                } else {
                    mMainDynamicAvatar3.setUserInfo(mDynamicList.get(2).userId, Utils.isNotEmpty(mDynamicList.get(2).avatarUrl) ? mDynamicList.get(2).avatarUrl : mDynamicList.get(2).imageUrl, false);
                }
                mMainDynamicName3.setText(TextUtils.isEmpty(mDynamicList.get(2).userName) ? "匿名用户" : Utils.StrSubstring(6, mDynamicList.get(2).userName, true));
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

    private void initRecentlyViewView(CustomerUserRecentListResult result) {
        if (null != result.respData && result.respData.userList.size() > 0) {
            layoutVisitorList.removeAllViews();
            layoutVisitorList.setVisibility(View.VISIBLE);
            mAllTechVisitor.clear();
            for (int i = 0; i < result.respData.userList.size(); i++) {
                if (result.respData.userList.get(i).emchatId != null) {
                    mAllTechVisitor.add(result.respData.userList.get(i));
                }
            }
            int count = 0;
            for (UserRecentBean visitor : mAllTechVisitor) {
                addVisitor(visitor);
                if (count++ == 4) {
                    break;
                }
            }
        } else {
            layoutVisitorList.setVisibility(View.GONE);
        }
    }

    private void addVisitor(UserRecentBean visitor) {
        CircleAvatarView avatarView = new CircleAvatarView(getContext());
        layoutVisitorList.addView(avatarView);
        avatarView.getLayoutParams().width = 0;
        avatarView.getLayoutParams().height = ScreenUtils.dpToPx(45);
        ((LinearLayout.LayoutParams) avatarView.getLayoutParams()).weight = 1;
        avatarView.setUserInfo(visitor.userId, visitor.avatarUrl, false);
    }

    // 最近访客:跳转聊天或者详情
    private void handleContactPermissionVisitor(ContactPermissionVisitorResult result) {
        RecentlyVisitorBean bean = result.bean;
        if (result.statusCode == 200 && result.respData.echat) {
            // 聊天
            MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_START_CHAT, Utils.wrapChatParams(bean.emchatId,
                    Utils.isEmpty(bean.userNoteName) ? bean.userName : bean.userNoteName, bean.avatarUrl, "customer"));
        } else {
            // 详情
            if (Utils.isEmpty(bean.userId) || Long.parseLong(bean.userId) <= 0) {
                Utils.makeShortToast(getActivity(), ResourceUtils.getString(R.string.visitor_has_no_message));
                return;
            } else {
                UINavigation.gotoCustomerDetailActivity(getActivity(), bean.userId, ConstantResources.INTENT_TYPE_TECH, false);
            }
        }
    }

    //将状态变更为休假时，查询技师未处理订单数
    private void handleOrderCount(OrderCountResult orderCountResult) {
        if (orderCountResult.statusCode == 200) {
            new RewardConfirmDialog(getActivity(), ResourceUtils.getString(R.string.tech_poster_alter_message), String.format(ResourceUtils.getString(R.string.tech_change_status_alter_message),
                    String.valueOf(orderCountResult.respData)), "", true) {
                @Override
                public void onConfirmClick() {
                    super.onConfirmClick();
                    updateWorkTimeResult(RequestConstant.KEY_TECH_STATUS_REST);
                }
            }.show();
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


    public Runnable mTask = new Runnable() {
        @Override
        public void run() {
            loadOrderListData();
        }
    };


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public void doUpdateTechInfoSuccess() {
        mTech.loadTechInfo();
    }

    @Override
    public void onRefresh() {
        mTech.loadTechInfo();
        sendDataRequest();
        BusinessPermissionManager.getInstance().syncPermissionsImmediately(null);//刷新权限
    }

    //申请加入会所事件
    private void onEventRequestJoinClub(EventRequestJoinClub event) {
        showTechStatus(mTech.getStatus());
    }


    private void handleTechPKRankingView(TechPKRankingResult techPKRankingResult) {
        if (techPKRankingResult.statusCode == 200) {
            if (techPKRankingResult.respData.count == Constant.HAS_RUNNING_PK_GROUP) {
                mRootView.findViewById(R.id.layout_technician_pk_ranking).setVisibility(View.VISIBLE);
                mRootView.findViewById(R.id.layout_technician_ranking).setVisibility(View.GONE);
                PKRankingAdapter adapter;
                if (Utils.isNotEmpty(techPKRankingResult.respData.categoryId)) {
                    adapter = new PKRankingAdapter(getActivity(), techPKRankingResult.respData.rankingList, techPKRankingResult.respData.categoryId);
                } else {
                    adapter = new PKRankingAdapter(getActivity(), techPKRankingResult.respData.rankingList, "");
                }

                mTeamList.setItemAnimator(new DefaultItemAnimator());
                mTeamList.setHasFixedSize(true);
                mTeamList.setNestedScrollingEnabled(true);
                mTeamList.setLayoutManager(new GridLayoutManager(mContext, 3));
                mTeamList.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                isHasPk = true;
                return;
            }
            mRootView.findViewById(R.id.layout_technician_ranking).setVisibility(View.VISIBLE);
            mRootView.findViewById(R.id.layout_technician_pk_ranking).setVisibility(View.GONE);
            if (techPKRankingResult.respData.count == Constant.HAS_NONE_RUNNING_PK_GROUP) {
                mRankingMore.setText(ResourceUtils.getString(R.string.layout_technician_ranking_check_all));
                isHasPk = true;
            } else {
                isHasPk = false;
                mRankingMore.setText("");
            }
            if (!isInitNormalRanking) {
                isInitNormalRanking = true;
                initRanking();
            }
            MsgDispatcher.dispatchMessage(MsgDef.MSF_DEF_GET_TECH_RANK_INDEX_DATA);
        }


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_CLUB_POSITION_INVITE) {
            getClubInviteCount();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (hidden) {
                getActivity().getWindow().setStatusBarColor(ResourceUtils.getColor(R.color.colorPrimary));
            } else {
                getActivity().getWindow().setStatusBarColor(0xFFFF826c);
            }
        }
    }

}
