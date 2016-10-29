package com.xmd.technician.window;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hyphenate.util.DateUtils;
import com.xmd.technician.Adapter.MainPageTechOrderListAdapter;
import com.xmd.technician.Adapter.MainPageTechVisitListAdapter;
import com.xmd.technician.AppConfig;
import com.xmd.technician.Constant;
import com.xmd.technician.R;
import com.xmd.technician.SharedPreferenceHelper;
import com.xmd.technician.bean.DynamicDetail;
import com.xmd.technician.bean.Order;
import com.xmd.technician.bean.RecentlyVisitorBean;
import com.xmd.technician.bean.RecentlyVisitorResult;
import com.xmd.technician.bean.TechInfo;
import com.xmd.technician.bean.UserSwitchesResult;
import com.xmd.technician.chat.UserProfileProvider;
import com.xmd.technician.common.HeartBeatTimer;
import com.xmd.technician.common.OnScrollChangedCallback;
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.common.Utils;
import com.xmd.technician.http.RequestConstant;
import com.xmd.technician.http.gson.DynamicListResult;
import com.xmd.technician.http.gson.OrderListResult;
import com.xmd.technician.http.gson.OrderManageResult;
import com.xmd.technician.http.gson.TechInfoResult;
import com.xmd.technician.http.gson.TechRankDataResult;
import com.xmd.technician.http.gson.TechStatisticsDataResult;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;
import com.xmd.technician.widget.CircleImageView;
import com.xmd.technician.widget.HorizontalListView;
import com.xmd.technician.widget.InviteDialog;
import com.xmd.technician.widget.RewardConfirmDialog;
import com.xmd.technician.widget.ScrollChangeScrollView;
import com.xmd.technician.widget.SlidingLayout;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

/**
 * Created by Administrator on 2016/10/19.
 */
public class MainFragment extends BaseFragment implements View.OnClickListener, OnScrollChangedCallback {

    @Bind(R.id.toolbar_back)
    ImageView toolbarBack;
    @Bind(R.id.toolbar_right_img)
    ImageView toolbarRightImg;
    @Bind(R.id.rl_toolbar)
    RelativeLayout mRlToolBar;
    @Bind(R.id.menu_work_time)
    RelativeLayout mMenuWorkTime;
    @Bind(R.id.menu_work_project)
    RelativeLayout mMenuWorkProject;
    @Bind(R.id.app_version)
    TextView mAppVersion;
    @Bind(R.id.menu_about_us)
    RelativeLayout mMenuAboutUs;
    @Bind(R.id.menu_suggest)
    RelativeLayout mMenuSuggest;
    @Bind(R.id.settings_activity_modify_pw)
    RelativeLayout mMenuSettingsActivityModifyPw;
    @Bind(R.id.settings_activity_join_club)
    RelativeLayout mMenuSettingsActivityJoinClub;
    @Bind(R.id.menu_club_name)
    TextView mMenuClubName;
    @Bind(R.id.settings_activity_quit_club)
    RelativeLayout mSettingsActivityQuitClub;
    @Bind(R.id.settings_activity_logout)
    RelativeLayout mSettingsActivityLogout;
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
    @Bind(R.id.btn_main_credit_center)
    ImageView mBtnMainCreditCenter;
    @Bind(R.id.main_info_too_keen_number)
    TextView mMainInfoTooKeenNumber;
    @Bind(R.id.main_too_keen)
    RelativeLayout mMainTooKeen;
    @Bind(R.id.main_send_coupon_number)
    TextView mMainSendCouponNumber;
    @Bind(R.id.main_send_coupon)
    RelativeLayout mMainSendCoupon;
    @Bind(R.id.main_get_comment_number)
    TextView mMainGetCommentNumber;
    @Bind(R.id.main_get_comment)
    RelativeLayout mMainGetComment;
    @Bind(R.id.main_total_income_number)
    TextView mMainTotalIncomeNumber;
    @Bind(R.id.main_total_income)
    RelativeLayout mMainTotalIncome;
    @Bind(R.id.main_info_item)
    LinearLayout mMainInfoItem;
    @Bind(R.id.main_tech_dynamic_all)
    RelativeLayout mMainTechDynamicAll;
    @Bind(R.id.main_tech_who_care_all)
    RelativeLayout mMainTechWhoCareAll;
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
    @Bind(R.id.main_dynamic_avatar)
    CircleImageView mMainDynamicAvatar3;
    @Bind(R.id.main_dynamic_name)
    TextView mMainDynamicName3;
    @Bind(R.id.main_dynamic_describe)
    TextView mMainDynamicDescribe3;
    @Bind(R.id.main_dynamic_time)
    TextView mMainDynamicTime3;
    @Bind(R.id.main_dynamic_none)
    TextView mMainDynamicNone;
    @Bind(R.id.main_tech_order_all)
    RelativeLayout mMainTechOrderAll;
    @Bind(R.id.main_who_care_list)
    HorizontalListView mMainWhoCareList;
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
    @Bind(R.id.layout_technician_ranking)
    LinearLayout mLayoutTechnicianRanking;
    @Bind(R.id.main_scroll_view)
    ScrollChangeScrollView mMainScrollView;
    @Bind(R.id.main_page)
    LinearLayout mMainPage;
    @Bind(R.id.main_sliding_layout)
    SlidingLayout mMainSlidingLayout;
    @Bind(R.id.main_page_head)
    RelativeLayout mMainPageHead;
    @Bind(R.id.main_dynamic1)
    RelativeLayout mMainDynamic1;
    @Bind(R.id.main_dynamic2)
    RelativeLayout mMainDynamic2;
    @Bind(R.id.main_dynamic3)
    RelativeLayout mMainDynamic3;

    private ImageView imageLeft, imageRight;
    private Context mContext;
    private boolean isCreditCanExchange;
    private String mClubId;
    private TechInfo mTechInfo;
    private List<Order> mAllTechOrderList = new ArrayList<>();
    private List<Order> mTechOrderList = new ArrayList<>();
    private List<RecentlyVisitorBean> mTechVisitor = new ArrayList<>();
    private List<RecentlyVisitorBean> mAllTechVisitor = new ArrayList<>();
    private List<DynamicDetail> mDynamicList = new ArrayList<>();
    private MainPageTechOrderListAdapter orderListAdapter;
    private MainPageTechVisitListAdapter visitListAdapter;

    private Subscription mGetTechCurrentInfoSubscription;
    private Subscription mUserSwitchesSubscription;
    private Subscription mGetTechOrderListSubscription;
    private Subscription mGetTechStatisticsDataSubscription;
    private Subscription mGetTechRankIndexDataSubscription;
    private Subscription mGetRecentlyVisitorSubscription;
    private Subscription mOrderManageSubscription;
    private Subscription mGetDynamicListSubscription;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_fragment, container, false);
        mContext = getActivity();
        ButterKnife.bind(this, view);
        initView(view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        Map<String, String> visitParams = new HashMap<>();
        visitParams.put(RequestConstant.KEY_CUSTOMER_TYPE, "");
        visitParams.put(RequestConstant.KEY_LAST_TIME, "");
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_RECENTLY_VISITOR, visitParams);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unsubscribe(mGetTechCurrentInfoSubscription, mUserSwitchesSubscription, mGetTechOrderListSubscription, mGetTechStatisticsDataSubscription,
                mGetTechRankIndexDataSubscription, mGetRecentlyVisitorSubscription, mOrderManageSubscription, mGetDynamicListSubscription);
    }

    private void initView(View view) {
        initTitleView(view);
        getData();
        handlerDataResult();
        mMainScrollView.setOnScrollChangedCallback(this);
    }

    private void getData() {
        MsgDispatcher.dispatchMessage(MsgDef.MSF_DEF_GET_TECH_INFO);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_USER_CLUB_SWITCHES);
        MsgDispatcher.dispatchMessage(MsgDef.MSF_DEF_GET_TECH_STATISTICS_DATA);
        refreshOrderListData();
        getDynamicList();
        MsgDispatcher.dispatchMessage(MsgDef.MSF_DEF_GET_TECH_RANK_INDEX_DATA);
        HeartBeatTimer.getInstance().start(60, mTask);
    }


    private void initTitleView(View view) {
        ((TextView) view.findViewById(R.id.toolbar_title)).setText(R.string.main_page);
        ((TextView) view.findViewById(R.id.toolbar_title)).setTextColor(Color.WHITE);
        view.findViewById(R.id.divide_line).setVisibility(View.GONE);
        view.findViewById(R.id.contact_more).setVisibility(View.VISIBLE);
        view.findViewById(R.id.rl_toolbar).setBackgroundColor(ResourceUtils.getColor(R.color.main_tool_bar_bg));
        imageRight = ((ImageView) view.findViewById(R.id.toolbar_right_img));
        imageLeft = (ImageView) view.findViewById(R.id.toolbar_back);
        imageLeft.setImageDrawable(ResourceUtils.getDrawable(R.drawable.ic_main_menu));
        imageRight.setImageDrawable(ResourceUtils.getDrawable(R.drawable.btn_main_qr_code));
        imageLeft.setVisibility(View.VISIBLE);
        imageLeft.setOnClickListener(this);
        imageRight.setOnClickListener(this);
        mAppVersion.setText("v" + AppConfig.getAppVersionNameAndCode());
    }

    private void handlerDataResult() {
        mGetTechCurrentInfoSubscription = RxBus.getInstance().toObservable(TechInfoResult.class).subscribe(
                techCurrentResult -> handleTechCurrentResult(techCurrentResult));
        mUserSwitchesSubscription = RxBus.getInstance().toObservable(UserSwitchesResult.class).subscribe(
                switchesResult -> handleUserSwitchResult(switchesResult));
        mGetTechStatisticsDataSubscription = RxBus.getInstance().toObservable(TechStatisticsDataResult.class).subscribe(
                statisticsData -> initTechWorkView(statisticsData));
        mGetTechOrderListSubscription = RxBus.getInstance().toObservable(OrderListResult.class).subscribe(
                orderListResult -> initOrderView(orderListResult));
        mGetTechRankIndexDataSubscription = RxBus.getInstance().toObservable(TechRankDataResult.class).subscribe(
                techRankResult -> initTechRankingView(techRankResult));
        mGetRecentlyVisitorSubscription = RxBus.getInstance().toObservable(RecentlyVisitorResult.class).subscribe(
                visitResult -> initRecentlyViewView(visitResult));
        mOrderManageSubscription = RxBus.getInstance().toObservable(OrderManageResult.class).subscribe(
                orderManageResult -> refreshOrderListData());
        mGetDynamicListSubscription = RxBus.getInstance().toObservable(DynamicListResult.class).subscribe(
                dynamicListResult -> initDynamicView(dynamicListResult));
    }

    public void refreshOrderListData() {
        Map<String, String> param = new HashMap<>();
        param.put(RequestConstant.KEY_PAGE, "1");
        param.put(RequestConstant.KEY_PAGE_SIZE, String.valueOf(5));
        param.put(RequestConstant.KEY_ORDER_STATUS, RequestConstant.KEY_ORDER_STATUS_SUBMIT_AND_ACCEPT);
        MsgDispatcher.dispatchMessage(MsgDef.MSF_DEF_GET_TECH_ORDER_LIST, param);
    }

    public void getDynamicList() {
        Map<String, String> param = new HashMap<>();
        param.put(RequestConstant.KEY_PAGE, "1");
        param.put(RequestConstant.KEY_PAGE_SIZE, String.valueOf(3));
        param.put(RequestConstant.KEY_ORDER_STATUS, RequestConstant.KEY_ORDER_STATUS_SUBMIT_AND_ACCEPT);
        MsgDispatcher.dispatchMessage(MsgDef.MSF_DEF_GET_TECH_DYNAMIC_LIST, param);
    }

    private void handleTechCurrentResult(TechInfoResult result) {
        if (result.respData != null) {
            mTechInfo = result.respData;
            UserProfileProvider.getInstance().updateCurrentUserInfo(mTechInfo.userName, mTechInfo.imageUrl);
            if (Utils.isNotEmpty(result.respData.clubId)) {
                mClubId = result.respData.clubId;
                SharedPreferenceHelper.setUserClubId(result.respData.clubId);
            }
            if (Utils.isNotEmpty(result.respData.clubName)) {
                SharedPreferenceHelper.setUserClubName(result.respData.clubName);
            }
            if (Utils.isNotEmpty(result.respData.serialNo)) {
                SharedPreferenceHelper.setSerialNo(result.respData.serialNo);
            }
            initHeadView(mTechInfo);
        }
    }

    private void handleUserSwitchResult(UserSwitchesResult switchResult) {
        if (switchResult.statusCode == 200) {
            if (switchResult.respData.credit.clubSwitch != null && switchResult.respData.credit.systemSwitch != null) {
                if (switchResult.respData.credit.clubSwitch.equals(RequestConstant.KEY_SWITCH_ON) && switchResult.respData.credit.systemSwitch.equals(RequestConstant.KEY_SWITCH_ON)) {
                    mBtnMainCreditCenter.setVisibility(View.VISIBLE);
                    isCreditCanExchange = true;
                } else {
                    mBtnMainCreditCenter.setVisibility(View.GONE);
                }
            } else {
                mBtnMainCreditCenter.setVisibility(View.GONE);
            }

        }
    }


    @OnClick({R.id.btn_main_tech_free, R.id.btn_main_tech_busy, R.id.btn_main_tech_rest, R.id.btn_main_credit_center})
    public void onMainHeadClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_main_tech_free:
                resetTechStatusView(R.id.btn_main_tech_free);
                break;
            case R.id.btn_main_tech_busy:
                resetTechStatusView(R.id.btn_main_tech_busy);
                break;
            case R.id.btn_main_tech_rest:
                resetTechStatusView(R.id.btn_main_tech_rest);
                break;
            case R.id.btn_main_credit_center:
                if (isCreditCanExchange) {
                    Intent intentCredit = new Intent(getActivity(), UserCreditCenterActivity.class);
                    startActivity(intentCredit);
                } else {
                    ((BaseFragmentActivity) getActivity()).makeShortToast(getString(R.string.personal_fragment_status_check));
                }
                break;
        }

    }

    private void resetTechStatusView(int id) {
        switch (id) {
            case R.id.btn_main_tech_free:
                mBtnMainTechFree.setImageDrawable(ResourceUtils.getDrawable(R.drawable.btn_main_free_selected));
                mBtnMainTechBusy.setImageDrawable(ResourceUtils.getDrawable(R.drawable.btn_main_busy_default));
                mBtnMainTechRest.setImageDrawable(ResourceUtils.getDrawable(R.drawable.btn_main_busy_default));
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
        }
    }

    private void updateWorkTimeResult(String mStatus) {
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_STATUS, mStatus);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_UPDATE_WORK_STATUS, params);
    }


    @OnClick({R.id.menu_work_time, R.id.menu_work_project, R.id.menu_about_us, R.id.menu_suggest, R.id.settings_activity_modify_pw, R.id.settings_activity_join_club,
            R.id.settings_activity_quit_club, R.id.settings_activity_logout})
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
                new InviteDialog(getActivity(), R.style.default_dialog_style).show();
                break;
            case R.id.settings_activity_quit_club:
                new RewardConfirmDialog(getActivity(), getString(R.string.quit_club_title), getString(R.string.quit_club_tips), "") {
                    @Override
                    public void onConfirmClick() {
                        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_QUIT_CLUB);
                        super.onConfirmClick();
                    }
                }.show();
                break;
            case R.id.settings_activity_logout:
                new RewardConfirmDialog(getActivity(), "", getString(R.string.logout_tips), "") {
                    @Override
                    public void onConfirmClick() {
                        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GETUI_UNBIND_CLIENT_ID);
                        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_LOGOUT);
                        super.onConfirmClick();
                    }
                }.show();
                break;

        }
    }

    @OnClick({R.id.main_page_head, R.id.main_tech_order_all, R.id.main_tech_dynamic_all, R.id.main_tech_who_care_all, R.id.layout_technician_ranking})
    public void onMainPagePieceClicked(View view) {
        switch (view.getId()) {
            case R.id.main_page_head:
                Intent intent = new Intent(getActivity(), TechInfoActivity.class);
                startActivity(intent);
                break;
            case R.id.main_tech_order_all:
                startActivity(new Intent(getActivity(), OrderFragmentActivity.class));
                break;
            case R.id.main_tech_dynamic_all:
                startActivity(new Intent(getActivity(), DynamicDetailActivity.class));
                break;
            case R.id.main_tech_who_care_all:
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.switchFragment(2);
                MsgDispatcher.dispatchMessage(MsgDef.MSF_DEF_SET_PAGE_SELECTED, 0);
                break;
            case R.id.layout_technician_ranking:
                if (TextUtils.isEmpty(mClubId)) {
                    ((BaseFragmentActivity) getActivity()).makeShortToast(getString(R.string.personal_fragment_join_club));
                    return;
                } else {
                    String url = SharedPreferenceHelper.getServerHost() + String.format(RequestConstant.URL_RANKING, System.currentTimeMillis(), RequestConstant.USER_TYPE_TECH,
                            RequestConstant.SESSION_TYPE, SharedPreferenceHelper.getUserToken()
                    );
                    Intent intentRanking = new Intent(getActivity(), BrowserActivity.class);
                    intentRanking.putExtra(BrowserActivity.EXTRA_SHOW_MENU, false);
                    intentRanking.putExtra(BrowserActivity.EXTRA_URL, url);
                    startActivity(intentRanking);
                }
                break;

        }
    }

    @OnClick({R.id.main_too_keen, R.id.main_send_coupon, R.id.main_get_comment, R.id.main_total_income})
    public void onMainDetailClicked(View view) {
        switch (view.getId()) {
            case R.id.main_too_keen:
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.switchFragment(2);
                MsgDispatcher.dispatchMessage(MsgDef.MSF_DEF_SET_PAGE_SELECTED, 1);
                break;
            case R.id.main_send_coupon:
                ((BaseFragmentActivity) getActivity()).makeShortToast(getString(R.string.main_no_coupon_alert_message));
                break;
            case R.id.main_get_comment:
                startActivity(new Intent(getActivity(), CommentActivity.class));
                break;
            case R.id.main_total_income:
                startActivity(new Intent(getActivity(), MyAccountActivity.class));
                break;

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_back:
                if (mMainSlidingLayout.isLeftLayoutVisible()) {
                    mMainSlidingLayout.scrollToRightLayout();
                } else {
                    mMainSlidingLayout.scrollToLeftLayout();
                }

                break;
            case R.id.toolbar_right_img:
                //分享二维码
                if (mTechInfo == null || TextUtils.isEmpty(mTechInfo.qrCodeUrl)) {
                    return;
                }
                boolean canShare = true;
                if (Constant.TECH_STATUS_VALID.equals(mTechInfo.status) || Constant.TECH_STATUS_REJECT.equals(mTechInfo.status) || Constant.TECH_STATUS_UNCERT.equals(mTechInfo.status)) {
                    canShare = false;
                }
                if (Utils.isNotEmpty(mTechInfo.clubId)) {
                    Intent intent = new Intent(getActivity(), shareCardActivity.class);
                    StringBuilder url = new StringBuilder(SharedPreferenceHelper.getServerHost());
                    url.append(String.format("/spa-manager/spa2/?club=%s#technicianDetail&id=%s&techInviteCode=%s", mTechInfo.clubId, mTechInfo.id, mTechInfo.inviteCode));
                    intent.putExtra(Constant.TECH_USER_HEAD_URL, mTechInfo.imageUrl);
                    intent.putExtra(Constant.TECH_USER_NAME, mTechInfo.userName);
                    intent.putExtra(Constant.TECH_USER_TECH_NUM, mTechInfo.serialNo);
                    intent.putExtra(Constant.TECH_USER_CLUB_NAME, mTechInfo.clubName);
                    intent.putExtra(Constant.TECH_SHARE_URL, url.toString());
                    intent.putExtra(Constant.TECH_ShARE_CODE_IMG, mTechInfo.qrCodeUrl);
                    intent.putExtra(Constant.TECH_CAN_SHARE, canShare);
                    startActivity(intent);

                } else {
                    ((BaseFragmentActivity) getActivity()).makeShortToast(getString(R.string.personal_fragment_join_club));
                }

                break;
        }
    }

    private void initHeadView(TechInfo info) {
        if (null == info) {
            return;
        }
        if (Utils.isNotEmpty(info.imageUrl) && mContext != null) {
            Glide.with(mContext).load(info.imageUrl).error(R.drawable.icon22).into(mMainHeadAvatar);
        }
        mMainHeadTechName.setText(info.userName);
        if (Utils.isNotEmpty(info.serialNo)) {
            mMainHeadTechSerial.setText(info.serialNo);
        } else {
            mMainHeadTechSerial.setVisibility(View.GONE);
        }

        if (info.status.equals(RequestConstant.KEY_TECH_STATUS_FREE)) {
            resetTechStatusView(R.id.btn_main_tech_free);
        } else if (info.status.equals(RequestConstant.KEY_TECH_STATUS_BUSY)) {
            resetTechStatusView(R.id.btn_main_tech_busy);
        } else {
            resetTechStatusView(R.id.btn_main_tech_rest);
        }
        if (Utils.isNotEmpty(info.clubName)) {
            mMenuClubName.setText(info.clubName);
        } else {
            mMenuSettingsActivityJoinClub.setVisibility(View.VISIBLE);
            mMenuClubName.setVisibility(View.GONE);
        }


    }

    private void initTechWorkView(TechStatisticsDataResult result) {
        mMainInfoTooKeenNumber.setText(result.respData.userCount);
        mMainSendCouponNumber.setText(result.respData.getCouponCount);
        mMainTotalIncomeNumber.setText(String.format("%1.2f", result.respData.incomeAmount));
        mMainGetCommentNumber.setText(result.respData.goodCommentCount);
        String textVisit = "今天共有" + result.respData.todayVisitCount + "人看了我～";
        mMainWhoCareTotal.setText(Utils.changeColor(textVisit, ResourceUtils.getColor(R.color.colorMainBtn), 4, textVisit.length() - 5));
    }

    private void initOrderView(OrderListResult orderList) {
        if (orderList.respData != null && orderList.respData.size() > 0) {
            mAllTechOrderList.clear();
            for (int i = 0; i < orderList.respData.size(); i++) {
                if (!orderList.respData.get(i).remainTime.contains("-") && (orderList.respData.get(i).status.equals(RequestConstant.KEY_ORDER_STATUS_SUBMIT) || orderList.respData.get(i).status.equals(RequestConstant.KEY_ORDER_STATUS_ACCEPT))) {
                    mAllTechOrderList.add(orderList.respData.get(i));
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

        }
    }

    private void initDynamicView(DynamicListResult result) {

        if (null != result.respData && result.respData.size() > 0) {
            mDynamicList.clear();
            mDynamicList.addAll(result.respData);
            if (mDynamicList.size() == 1) {
                mMainDynamic1.setVisibility(View.VISIBLE);
                Glide.with(mContext).load(mDynamicList.get(0).imageUrl).into(mMainDynamicAvatar1);
                mMainDynamicName1.setText(mDynamicList.get(0).userName);
                mMainDynamicDescribe1.setText(getRecentStatusDes(mDynamicList.get(0).bizType));
                mMainDynamicTime1.setText(DateUtils.getTimestampString(new Date(mDynamicList.get(0).createTime)));
                mMainDynamic2.setVisibility(View.GONE);
                mMainDynamic3.setVisibility(View.GONE);
            } else if (mDynamicList.size() == 2) {
                mMainDynamic1.setVisibility(View.VISIBLE);
                Glide.with(mContext).load(mDynamicList.get(0).imageUrl).into(mMainDynamicAvatar1);
                mMainDynamicName1.setText(mDynamicList.get(0).userName);
                mMainDynamicDescribe1.setText(getRecentStatusDes(mDynamicList.get(0).bizType));
                mMainDynamicTime1.setText(DateUtils.getTimestampString(new Date(mDynamicList.get(0).createTime)));
                mMainDynamic2.setVisibility(View.VISIBLE);
                Glide.with(mContext).load(mDynamicList.get(1).imageUrl).into(mMainDynamicAvatar2);
                mMainDynamicName2.setText(mDynamicList.get(1).userName);
                mMainDynamicDescribe2.setText(getRecentStatusDes(mDynamicList.get(1).bizType));
                mMainDynamicTime2.setText(DateUtils.getTimestampString(new Date(mDynamicList.get(1).createTime)));
                mMainDynamic3.setVisibility(View.GONE);
            } else if (mDynamicList.size() == 3) {
                mMainDynamic1.setVisibility(View.VISIBLE);
                Glide.with(mContext).load(mDynamicList.get(0).imageUrl).into(mMainDynamicAvatar1);
                mMainDynamicName1.setText(mDynamicList.get(0).userName);
                mMainDynamicDescribe1.setText(getRecentStatusDes(mDynamicList.get(0).bizType));
                mMainDynamicTime1.setText(DateUtils.getTimestampString(new Date(mDynamicList.get(0).createTime)));
                mMainDynamic2.setVisibility(View.VISIBLE);
                Glide.with(mContext).load(mDynamicList.get(1).imageUrl).into(mMainDynamicAvatar2);
                mMainDynamicName2.setText(mDynamicList.get(1).userName);
                mMainDynamicDescribe2.setText(getRecentStatusDes(mDynamicList.get(1).bizType));
                mMainDynamicTime2.setText(DateUtils.getTimestampString(new Date(mDynamicList.get(1).createTime)));
                mMainDynamic3.setVisibility(View.VISIBLE);
                Glide.with(mContext).load(mDynamicList.get(2).imageUrl).into(mMainDynamicAvatar3);
                mMainDynamicName3.setText(mDynamicList.get(2).userName);
                mMainDynamicDescribe3.setText(getRecentStatusDes(mDynamicList.get(2).bizType));
                mMainDynamicTime3.setText(DateUtils.getTimestampString(new Date(mDynamicList.get(2).createTime)));

            }
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
            llHorizontalList.setVisibility(View.VISIBLE);
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
            visitListAdapter = new MainPageTechVisitListAdapter(mContext, mTechVisitor);
            mMainWhoCareList.setAdapter(visitListAdapter);
            visitListAdapter.notifyDataSetChanged();
            mMainWhoCareList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_START_CHAT, Utils.wrapChatParams(mTechVisitor.get(position).emchatId,
                            Utils.isEmpty(mTechVisitor.get(position).userNoteName) ? mTechVisitor.get(position).userName : mTechVisitor.get(position).userNoteName, mTechVisitor.get(position).avatarUrl, ""));
                }
            });

        } else {
            llHorizontalList.setVisibility(View.GONE);
        }
    }

    private void initTechRankingView(TechRankDataResult result) {
        if (null != result.respData.commentRanking) {
            Glide.with(mContext).load(result.respData.commentRanking.avatarUrl).error(ResourceUtils.getDrawable(R.drawable.icon22)).into(mCvStarRegister);
            mTvStarRegisterUser.setText(result.respData.commentRanking.name);
            if (Utils.isNotEmpty(result.respData.commentRanking.serialNo)) {
                mTvStarRegisterTechNo.setText(result.respData.commentRanking.serialNo);
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
        if (null != result.respData.userRanking) {
            Glide.with(mContext).load(result.respData.userRanking.avatarUrl).into(mCvStarService);
            mTvStarService.setText(result.respData.userRanking.name);
            if (Utils.isNotEmpty(result.respData.userRanking.serialNo)) {
                mTvTitleService.setText(result.respData.userRanking.serialNo);
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
            refreshOrderListData();
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

    @Override
    public void onScroll(int l, int t) {
        if (t > 100) {
            mRlToolBar.setBackgroundColor(ResourceUtils.getColor(R.color.recent_status_reward));
        } else {
            mRlToolBar.setBackgroundColor(ResourceUtils.getColor(R.color.main_tool_bar_bg));

        }

    }

}
