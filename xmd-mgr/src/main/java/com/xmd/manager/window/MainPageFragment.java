package com.xmd.manager.window;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.crazyman.library.PermissionTool;
import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.chat.XmdChat;
import com.xmd.m.comment.CommentDetailActivity;
import com.xmd.m.comment.CommentListActivity;
import com.xmd.m.comment.bean.CommentBean;
import com.xmd.m.comment.bean.UserInfoBean;
import com.xmd.m.comment.event.UserInfoEvent;
import com.xmd.m.notify.push.XmdPushManager;
import com.xmd.m.notify.push.XmdPushMessage;
import com.xmd.m.notify.push.XmdPushMessageListener;
import com.xmd.m.notify.redpoint.RedPointService;
import com.xmd.m.notify.redpoint.RedPointServiceImpl;
import com.xmd.manager.AppConfig;
import com.xmd.manager.BuildConfig;
import com.xmd.manager.Constant;
import com.xmd.manager.Manager;
import com.xmd.manager.R;
import com.xmd.manager.SharedPreferenceHelper;
import com.xmd.manager.UINavigation;
import com.xmd.manager.adapter.MainPageBadCommentListAdapter;
import com.xmd.manager.adapter.PKRankingAdapter;
import com.xmd.manager.beans.IndexOrderData;
import com.xmd.manager.beans.QrResult;
import com.xmd.manager.beans.SwitchIndexBean;
import com.xmd.manager.common.DateUtil;
import com.xmd.manager.common.ResourceUtils;
import com.xmd.manager.common.ReturnVisitMenu;
import com.xmd.manager.common.ToastUtils;
import com.xmd.manager.common.Utils;
import com.xmd.manager.common.VerificationManagementHelper;
import com.xmd.manager.msgctrl.MsgDef;
import com.xmd.manager.msgctrl.MsgDispatcher;
import com.xmd.manager.msgctrl.RxBus;
import com.xmd.manager.service.RequestConstant;
import com.xmd.manager.service.response.AccountDataResult;
import com.xmd.manager.service.response.ChangeStatusResult;
import com.xmd.manager.service.response.CheckVerificationTypeResult;
import com.xmd.manager.service.response.CommentAndComplaintListResult;
import com.xmd.manager.service.response.CouponDataResult;
import com.xmd.manager.service.response.PropagandaDataResult;
import com.xmd.manager.service.response.RegistryDataResult;
import com.xmd.manager.service.response.TechPKRankingResult;
import com.xmd.manager.service.response.TechRankDataResult;
import com.xmd.manager.service.response.VerificationSaveResult;
import com.xmd.manager.verification.VerificationListActivity;
import com.xmd.manager.widget.AlertDialogBuilder;
import com.xmd.manager.widget.BottomPopupWindow;
import com.xmd.manager.widget.CircleImageView;
import com.xmd.manager.widget.ClearableEditText;
import com.xmd.manager.widget.SlidingMenu;
import com.xmd.permission.CheckBusinessPermission;
import com.xmd.permission.PermissionConstants;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

import static android.app.Activity.RESULT_OK;

/**
 * Created by lhj on 2016/11/17.
 */
public class MainPageFragment extends BaseFragment implements View.OnClickListener {

    @BindView(R.id.menu_app_version)
    TextView mMenuAppVersion;
    @BindView(R.id.menu_version_update)
    RelativeLayout mMenuVersionUpdate;
    @BindView(R.id.menu_setting)
    RelativeLayout mMenuSetting;
    @BindView(R.id.menu_service_phone)
    TextView mMenuServicePhone;
    @BindView(R.id.menu_service)
    RelativeLayout mMenuService;
    @BindView(R.id.menu_suggest)
    RelativeLayout mMenuSuggest;
    @BindView(R.id.menu_club_name)
    TextView mMenuClubName;
    @BindView(R.id.menu_choice_club)
    RelativeLayout mMenuChoiceClub;
    @BindView(R.id.menu_change_password)
    RelativeLayout mMenuChangePassword;
    @BindView(R.id.club_list)
    RelativeLayout mClubList;
    @BindView(R.id.menu_activity_logout)
    RelativeLayout mSettingsActivityLogout;
    @BindView(R.id.tv_qr_code)
    TextView mTvQrCode;
    @BindView(R.id.cet_paid_order_consume_code)
    ClearableEditText mCetPaidOrderConsumeCode;
    @BindView(R.id.btn_consume)
    TextView mBtnConsume;
    @BindView(R.id.ll_verify)
    LinearLayout mVerifyLayout;

    @BindView(R.id.main_bad_comment)
    RelativeLayout mMainBadComment;

    @BindView(R.id.bad_comment_finish)
    ImageView mBadCommentFinish;
    @BindView(R.id.main_ranking)
    RelativeLayout mMainRanking;
    @BindView(R.id.cv_star_register)
    CircleImageView mCvStarRegister;
    @BindView(R.id.tv_star_register_user)
    TextView mTvStarRegisterUser;
    @BindView(R.id.tv_star_register_tech_no)
    TextView mTvStarRegisterTechNo;
    @BindView(R.id.ll_star_user)
    LinearLayout mLayoutStarUser;
    @BindView(R.id.cv_star_sales)
    CircleImageView mCvStarSales;
    @BindView(R.id.tv_star_sales)
    TextView mTvStarSales;
    @BindView(R.id.tv_star_sales_tech_no)
    TextView mTvStarSalesTechNo;
    @BindView(R.id.ll_star_sale)
    LinearLayout mLayoutStarSale;
    @BindView(R.id.cv_star_service)
    CircleImageView mCvStarService;
    @BindView(R.id.tv_star_service)
    TextView mTvStarService;
    @BindView(R.id.tv_star_service_tech_no)
    TextView mTvStarServiceTechNo;
    @BindView(R.id.ll_star_service)
    LinearLayout mLayoutStarService;
    @BindView(R.id.swipe_refresh_widget)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.team_list)
    RecyclerView mTeamList;
    @BindView(R.id.ranking_more)
    TextView mRankingMore;
    @BindView(R.id.main_page_scroll)
    NestedScrollView mMainPageScroll;
    @BindView(R.id.sliding_menu)
    SlidingMenu mSlidingMenu;

    @BindView(R.id.layout_statistics)
    LinearLayout mStatisticsLayout;
    @BindView(R.id.layout_obtain_client)
    LinearLayout mClientLayout;
    @BindView(R.id.layout_bad_comment)
    LinearLayout mBadCommentLayout;
    @BindView(R.id.main_publicity_time_switch)
    TextView mMainPublicityTimeSwitch;
    @BindView(R.id.tv_current_time)
    TextView mMainPublicityCurrentTime;

    //Wifi//网店访客
    @BindView(R.id.ll_wifi_today)
    LinearLayout mWifiTodayLayout;
    @BindView(R.id.tv_title_wifi)
    TextView mTvTitleWifi;
    @BindView(R.id.tv_wifi_today)
    TextView mTvWifiToday;
    @BindView(R.id.tv_wifi_accumulate)
    TextView mTvWifiAccumulate;
    @BindView(R.id.tv_title_client)
    TextView mTvTitleClient;
    @BindView(R.id.tv_title_statistics)
    TextView mTvTitleStatistics;
    @BindView(R.id.ll_visit_today)
    LinearLayout mVisitTodayLayout;
    @BindView(R.id.tv_title_visit)
    TextView mTvTitleVisit;
    @BindView(R.id.tv_visit_today)
    TextView mTvVisitToday;
    @BindView(R.id.tv_visit_accumulate)
    TextView mTvVisitAccumulate;
    @BindView(R.id.ll_propaganda_view)
    LinearLayout mLlPropagandaView;
    @BindView(R.id.ll_visit_view)
    LinearLayout mLlVisitView;
    @BindView(R.id.tv_visit_today_data)
    TextView mTvVisitTodayData;
    @BindView(R.id.tv_visit_yesterday_data)
    TextView mTvVisitYesterdayData;
    @BindView(R.id.tv_visit_accumulate_data)
    TextView mTvVisitAccumulateData;
    @BindView(R.id.main_marketing_time_switch)
    TextView mMainMarketingTimeSwitch;
    @BindView(R.id.tv_marketing_current_time)
    TextView mMainMarketingCurrentTime;
    //新注册用户
    @BindView(R.id.ll_new_register_today)
    LinearLayout mNewRegisterLayout;
    @BindView(R.id.tv_title_register)
    TextView mTvTitleRegister;
    @BindView(R.id.tv_new_register_today)
    TextView mTvNewRegisterToday;
    @BindView(R.id.tv_new_register_accumulate)
    TextView mTvNewRegisterAccumulate;

    //领券用户
    @BindView(R.id.ll_coupon_get_today)
    LinearLayout mCouponGetLayout;
    @BindView(R.id.tv_title_coupon)
    TextView mTvTitleCoupon;
    @BindView(R.id.tv_coupon_get_today)
    TextView mTvCouponGetToday;
    @BindView(R.id.tv_coupon_get_accumulate)
    TextView mTvCouponGetAccumulate;

    //订单
    @BindView(R.id.tv_title_order)
    TextView mTvTitleOrder;
    @BindView(R.id.layout_order)
    LinearLayout mOrderLayout;
    @BindView(R.id.tv_pending_order_count)
    TextView mTvPendingOrderCount;
    @BindView(R.id.tv_accepted_order_count)
    TextView mTvAcceptedOrderCount;
    @BindView(R.id.tv_completed_order_count)
    TextView mTvCompletedOrderCount;

    @BindView(R.id.main_bad_comment_list)
    RecyclerView badCommentList;
    @BindView(R.id.toolbar_right_text)
    TextView mToolbarRightText;
    //线上流水
    @BindView(R.id.tv_title_account)
    TextView tvTitleAccount;
    @BindView(R.id.tv_account_current_time)
    TextView tvAccountCurrentTime;
    @BindView(R.id.main_account_time_switch)
    TextView mainAccountTimeSwitch;
    @BindView(R.id.tv_title_paid)
    TextView tvTitlePaid;
    @BindView(R.id.tv_check_account)
    TextView tvCheckAccount;
    @BindView(R.id.tv_paid_account_total)
    TextView tvPaidAccountTotal;
    @BindView(R.id.ll_account_paid)
    LinearLayout llAccountPaid;
    @BindView(R.id.tv_title_sail)
    TextView tvTitleSail;
    @BindView(R.id.tv_sail_account)
    TextView tvSailAccount;
    @BindView(R.id.tv_sail_account_total)
    TextView tvSailAccountTotal;
    @BindView(R.id.ll_account_sail_view)
    LinearLayout llAccountSailView;
    @BindView(R.id.ll_account_view)
    LinearLayout llAccountView;
    @BindView(R.id.tv_once_card_title)
    TextView tvOnceCardTitle;
    @BindView(R.id.tv_once_card_account)
    TextView tvOnceCardAccount;
    @BindView(R.id.ll_once_card_view)
    RelativeLayout llOnceCardView;
    @BindView(R.id.tv_panic_title)
    TextView tvPanicTitle;
    @BindView(R.id.tv_panic_account)
    TextView tvPanicAccount;
    @BindView(R.id.ll_panic_view)
    RelativeLayout llPanicView;
    @BindView(R.id.tv_pay_for_me_title)
    TextView tvPayForMeTitle;
    @BindView(R.id.tv_pay_for_me_account)
    TextView tvPayForMeAccount;
    @BindView(R.id.ll_pay_for_me_view)
    RelativeLayout llPayForMeView;
    @BindView(R.id.tv_paid_title)
    TextView tvPaidTitle;
    @BindView(R.id.tv_paid_account)
    TextView tvPaidAccount;
    @BindView(R.id.ll_paid_view)
    RelativeLayout llPaidView;
    @BindView(R.id.tv_sail_income)
    TextView tvSailIncome;
    @BindView(R.id.ll_sail_view)
    LinearLayout llSailView;
    @BindView(R.id.layout_technician_pk_ranking)
    LinearLayout technicianPkRanking;
    @BindView(R.id.layout_technician_ranking)
    LinearLayout technicianRanking;

    @BindView(R.id.newOrderMark)
    TextView newOrderMark;
    @BindView(R.id.fastPayMark)
    TextView fastPayMark;
    @BindView(R.id.newCustomerCountMark)
    TextView customerCountMark;
    @BindView(R.id.newOrderLayout)
    View newOrderLayout;
    //空View
    @BindView(R.id.view_transparent)
    View mViewTransparent;

    private static final int REQUEST_CODE_PHONE = 0x0001;
    private static final int REQUEST_CODE_CAMERA = 0x002;


    private ImageView imageLeft;
    private View view;
    private RelativeLayout mRlToolbar;
    private String mPhoneNoOrCouponNo;
    private String mQrNo;
    private String mRid;
    private String mTime;
    private String servicePhone;
    private int mCurrentPublicityData = 1;
    private int mCurrentAccountData = 1;
    private int mCurrentMarketingData = 1;
    private VerificationManagementHelper mVerificationHelper;
    private MainPageBadCommentListAdapter badCommentListAdapter;
    private List<CommentBean> mCommentList;
    private boolean isHasPk;

    private Subscription mIndexOrderDataSubscription;
    private Subscription mQrResultSubscription;
    private Subscription mRegistryDataSubscription;
    private Subscription mCouponDataSubscription;
    private Subscription mRankingDataSubscription;
    private Subscription mCommentAndComplaintSubscription;
    private Subscription mBadCommentStatusSubscription;
    private Subscription mGetVerificationTypeSubscription;
    private Subscription mVerificationHandleSubscription;
    private Subscription mPropagandaDataSubscription;
    private Subscription mAccountDataSubSubscription;
    private Subscription mTechPKRankingSubscription;
    private Subscription mSwitchChangedSubscription;

    private RedPointService redPointService = RedPointServiceImpl.getInstance();

    private int customerCount;
    private int fastPayAmount;
    private boolean showFastPay;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_main_page, container, false);
        ButterKnife.bind(this, view);
        initView(view);
        redPointService.bind(Constant.RED_POINT_NEW_ORDER, newOrderMark, RedPointService.SHOW_TYPE_POINT);
        redPointService.bind(XmdPushMessage.BUSINESS_TYPE_FAST_PAY, fastPayMark, RedPointService.SHOW_TYPE_POINT);
        redPointService.bind(XmdPushMessage.BUSINESS_TYPE_NEW_CUSTOMER, customerCountMark, RedPointService.SHOW_TYPE_POINT);
        XmdPushManager.getInstance().addListener(xmdPushMessageListener);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        RxBus.getInstance().unsubscribe(mRegistryDataSubscription, mCouponDataSubscription, mRankingDataSubscription,
                mCommentAndComplaintSubscription, mBadCommentStatusSubscription, mIndexOrderDataSubscription, mQrResultSubscription,
                mGetVerificationTypeSubscription, mVerificationHandleSubscription, mPropagandaDataSubscription, mAccountDataSubSubscription,
                mTechPKRankingSubscription, mSwitchChangedSubscription);
        mVerificationHelper.destroySubscription();
        redPointService.unBind(Constant.RED_POINT_NEW_ORDER, newOrderMark);
        redPointService.unBind(XmdPushMessage.BUSINESS_TYPE_FAST_PAY, fastPayMark);
        redPointService.unBind(XmdPushMessage.BUSINESS_TYPE_NEW_CUSTOMER, customerCountMark);
        XmdPushManager.getInstance().removeListener(xmdPushMessageListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        mMainPageScroll.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                onScrollChanged(scrollX, scrollY);
            }
        });
    }

    private void onScrollChanged(int scrollX, int scrollY) {
        if (scrollY > Utils.dip2px(getActivity(), 100)) {
            mRlToolbar.setBackgroundColor(ResourceUtils.getColor(R.color.toolbarBackground));
        } else {
            mRlToolbar.setBackgroundColor(ResourceUtils.getColor(R.color.main_tool_bar_bg));
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        mSlidingMenu.closeMenu();
    }


    @Override
    protected void initView() {
        if (Constant.MULTI_CLUB_ROLE.equals(SharedPreferenceHelper.getUserRole())) {
            mMenuChoiceClub.setVisibility(View.VISIBLE);
            mMenuClubName.setText(Utils.StrSubstring(8, SharedPreferenceHelper.getCurrentClubName(), true));
        }
        ((TextView) view.findViewById(R.id.toolbar_title)).setText(Utils.briefString(SharedPreferenceHelper.getCurrentClubName(), 6));
        mMainPublicityTimeSwitch.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        mMainMarketingTimeSwitch.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        mainAccountTimeSwitch.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
    }

    private void refreshMainPageData() {
        getPublicityData(mCurrentPublicityData);
        getAccountData(mCurrentAccountData);
        getMarketingData(mCurrentMarketingData);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET__INDEX_ORDER_DATA);
        getBadCommentData();
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_TECH_PK_RANKING);

    }


    private void initView(View view) {
        mToolbarRightText.setText(ResourceUtils.getString(R.string.verification_record));
        mToolbarRightText.setVisibility(View.VISIBLE);
        servicePhone = ResourceUtils.getString(R.string.service_phone);
        initVisibilityForViews();
        initTitleView(view);
        initBadCommentView();
        mSlidingMenu.setOnCloseOrOpenListener(new SlidingMenu.CloseOrOpenListener() {
            @Override
            public void isOpen(boolean isOpen) {
                if (isOpen) {
                    XLogger.i(">>>", "open");

                    mViewTransparent.setVisibility(View.VISIBLE);
                } else {
                    XLogger.i(">>>", "close");
                    mViewTransparent.setVisibility(View.GONE);
                }
            }
        });

        mPropagandaDataSubscription = RxBus.getInstance().toObservable(PropagandaDataResult.class).subscribe(
                result -> handlerPropagandaDataResult(result)
        );
        mRegistryDataSubscription = RxBus.getInstance().toObservable(RegistryDataResult.class).subscribe(
                registryDataResult -> handlerRegistryResult(registryDataResult));

        mCouponDataSubscription = RxBus.getInstance().toObservable(CouponDataResult.class).subscribe(
                couponDataResult -> handlerCouponResult(couponDataResult));

        mIndexOrderDataSubscription = RxBus.getInstance().toObservable(IndexOrderData.class).subscribe(
                orderDataResult -> handlerIndexOrderResult(orderDataResult));

        mQrResultSubscription = RxBus.getInstance().toObservable(QrResult.class).subscribe(
                qrResult -> onScanQrCodeSuccess(qrResult));

        mCommentAndComplaintSubscription = RxBus.getInstance().toObservable(CommentAndComplaintListResult.class).subscribe(
                commentAndComplaintListResult -> handlerCommentAndComplaint(commentAndComplaintListResult)
        );

        mBadCommentStatusSubscription = RxBus.getInstance().toObservable(ChangeStatusResult.class).subscribe(
                changeStatusResult -> {
                    if (changeStatusResult.statusCode == 200) {
                        getBadCommentData();
                    }
                });

        mGetVerificationTypeSubscription = RxBus.getInstance().toObservable(CheckVerificationTypeResult.class).subscribe(
                resultType -> {
                    if (resultType.statusCode == 200) {
                        VerificationManagementHelper.handlerVerificationType(getActivity(), resultType.respData, resultType.code);
                    } else {
                        Utils.makeShortToast(getActivity(), resultType.msg);
                    }
                }
        );
        mVerificationHandleSubscription = RxBus.getInstance().toObservable(VerificationSaveResult.class).subscribe(saveResult -> {
            if (saveResult.statusCode == 200) {
                mCetPaidOrderConsumeCode.setText("");
            }
        });
        mAccountDataSubSubscription = RxBus.getInstance().toObservable(AccountDataResult.class).subscribe(
                result -> handlerAccountDataResult(result)
        );
        mTechPKRankingSubscription = RxBus.getInstance().toObservable(TechPKRankingResult.class).subscribe(
                techPKRankingResult -> handleTechPKRankingView(techPKRankingResult)
        );
        mSwitchChangedSubscription = RxBus.getInstance().toObservable(SwitchIndexBean.class).subscribe(
                result -> {
                    if (result.index != 0) {
                        mSlidingMenu.closeMenu();
                    }
                }
        );
        mRankingDataSubscription = RxBus.getInstance().toObservable(TechRankDataResult.class).subscribe(
                rankingDataResult -> handlerRankingResult(rankingDataResult));
        refreshMainPageData();
        mVerificationHelper = VerificationManagementHelper.getInstance();
        mVerificationHelper.initializeHelper(getActivity());
    }


    private void handlerAccountDataResult(AccountDataResult result) {
        if (result.statusCode == 200) {
            if (result.respData == null || result.respData.amountList == null) {
                return;
            }
            if (result.respData.amountList.size() == 2) {
                llAccountView.setVisibility(View.VISIBLE);
                llSailView.setVisibility(View.GONE);
                if (Utils.isNotEmpty(result.respData.amountList.get(0).accountTypeName)) {
                    tvTitlePaid.setText(String.format("%s：", result.respData.amountList.get(0).accountTypeName));
                }
                tvCheckAccount.setText(Utils.getNumToString(result.respData.amountList.get(0).amount, false));
                fastPayAmount = result.respData.amountList.get(0).totalAmount;
                tvPaidAccountTotal.setText(Utils.getNumToString(fastPayAmount, false));
                if (result.respData.amountList.get(0).accountType.equals("fast_pay")) {
                    //在线买单
                    showFastPay = true;
                    int lastViewFastPayValue = SharedPreferenceHelper.getListViewFastPayValue();
                    if (lastViewFastPayValue < 0) {
                        SharedPreferenceHelper.setLastViewFastPayValue(fastPayAmount);
                    } else if (fastPayAmount > lastViewFastPayValue) {
                        fastPayMark.setVisibility(View.VISIBLE);
                    }
                } else {
                    showFastPay = false;
                }
                if (Utils.isNotEmpty(result.respData.amountList.get(1).accountTypeName)) {
                    tvTitleSail.setText(String.format("%s：", result.respData.amountList.get(1).accountTypeName));
                }
                tvSailAccount.setText(Utils.getNumToString(result.respData.amountList.get(1).amount, false));
                tvSailAccountTotal.setText(Utils.getNumToString(result.respData.amountList.get(1).totalAmount, false));
                return;
            }
            llAccountView.setVisibility(View.GONE);
            llSailView.setVisibility(View.VISIBLE);
            tvSailIncome.setText(Utils.getNumToString(result.respData.totalAmount, false));

            tvOnceCardTitle.setText(result.respData.amountList.get(0).accountTypeName);
            tvOnceCardAccount.setText(Utils.getNumToString(result.respData.amountList.get(0).amount, false));
            tvPanicTitle.setText(result.respData.amountList.get(1).accountTypeName);
            tvPanicAccount.setText(Utils.getNumToString(result.respData.amountList.get(1).amount, false));
            tvPayForMeTitle.setText(result.respData.amountList.get(2).accountTypeName);
            tvPayForMeAccount.setText(Utils.getNumToString(result.respData.amountList.get(2).amount, false));

            if (result.respData.amountList.size() == 3) {
                llPaidView.setVisibility(View.GONE);

            } else if (result.respData.amountList.size() == 4) {
                llPaidView.setVisibility(View.VISIBLE);
                tvPaidTitle.setText(result.respData.amountList.get(3).accountTypeName);
                tvPaidAccount.setText(Utils.getNumToString(result.respData.amountList.get(3).amount, false));
            }
        } else {
            ToastUtils.showToastShort(getActivity(), result.msg);
        }

    }

    private void handleTechPKRankingView(TechPKRankingResult techPKRankingResult) {
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
        if (techPKRankingResult.statusCode == 200) {
            // 从来没有分组
            if (techPKRankingResult.respData.count == Constant.HAS_NONE_PK_GROUP) {
                isHasPk = false;
                technicianPkRanking.setVisibility(View.GONE);
                technicianRanking.setVisibility(View.VISIBLE);
                initTechRankingView();
                return;
            }
            //存在正在进行的分组
            if (techPKRankingResult.respData.count == Constant.HAS_RUNNING_PK_GROUP) {
                technicianPkRanking.setVisibility(View.VISIBLE);
                technicianRanking.setVisibility(View.GONE);
                PKRankingAdapter adapter;
                if (Utils.isNotEmpty(techPKRankingResult.respData.categoryId)) {
                    adapter = new PKRankingAdapter(getActivity(), techPKRankingResult.respData.rankingList, techPKRankingResult.respData.categoryId);
                } else {
                    adapter = new PKRankingAdapter(getActivity(), techPKRankingResult.respData.rankingList, "");
                }
                mTeamList.setItemAnimator(new DefaultItemAnimator());
                mTeamList.setHasFixedSize(true);
                mTeamList.setNestedScrollingEnabled(true);
                mTeamList.setLayoutManager(new GridLayoutManager(getActivity(), 3));
                mTeamList.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                isHasPk = true;
                return;
            }
            //曾经有现在没有
            if (techPKRankingResult.respData.count == Constant.HAS_NONE_RUNNING_PK_GROUP) {
                technicianRanking.setVisibility(View.VISIBLE);
                technicianPkRanking.setVisibility(View.GONE);
                if (techPKRankingResult.respData.count == Constant.HAS_NONE_RUNNING_PK_GROUP) {
                    mRankingMore.setText(ResourceUtils.getString(R.string.layout_technician_ranking_check_all));
                    isHasPk = true;
                } else {
                    isHasPk = false;
                    mRankingMore.setText("");
                }
                initTechRankingView();
                return;
            }

        } else {
            initTechRankingView();
        }


    }

    private void initBadCommentView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setSmoothScrollbarEnabled(true);
        layoutManager.setAutoMeasureEnabled(true);

        mCommentList = new ArrayList<>();

        badCommentListAdapter = new MainPageBadCommentListAdapter(getActivity(), mCommentList);
        badCommentListAdapter.setCommentClickedListener(new MainPageBadCommentListAdapter.OnCommentClickedListener() {
            @Override
            public void onCommentCallBackClick(CommentBean badComment) {
                showServiceOutMenu(badComment.userId, badComment.phoneNum, badComment.userEmchatId, badComment.userName, badComment.avatarUrl, badComment.id, badComment.returnStatus);
            }

            @Override
            public void onItemClicked(CommentBean badComment) {
                CommentDetailActivity.startCommentDetailActivity(getActivity(), badComment, true, "");
            }
        });
        badCommentList.setLayoutManager(layoutManager);
        badCommentList.setHasFixedSize(true);
        badCommentList.setNestedScrollingEnabled(false);
        badCommentList.setAdapter(badCommentListAdapter);
    }

    /**
     * When manager scan the qrcode, and return the qrcode result
     * only for PaidOrder
     *
     * @param qrResult
     */
    private void onScanQrCodeSuccess(QrResult qrResult) {
        if ("consume".equals(qrResult.type)) {
            mQrNo = qrResult.qrNo;
            mRid = qrResult.rid;
            mTime = qrResult.time;
            Intent intent = new Intent(getActivity(), PayActivity.class);
            intent.putExtra(RequestConstant.KEY_PAY_QRNO, mQrNo);
            intent.putExtra(RequestConstant.KEY_PAY_RID, mRid);
            intent.putExtra(RequestConstant.KEY_TIME, mTime);
            startActivity(intent);
            return;
        } else {
            mPhoneNoOrCouponNo = qrResult.qrNo;
            VerificationManagementHelper.checkVerificationType(mPhoneNoOrCouponNo);
        }

    }

    private void handlerPropagandaDataResult(PropagandaDataResult result) {
        mSwipeRefreshLayout.setRefreshing(false);
        ((TextView) view.findViewById(R.id.toolbar_title)).setText(Utils.briefString(SharedPreferenceHelper.getCurrentClubName(), 8));
        mMenuClubName.setText(SharedPreferenceHelper.getCurrentClubName());
        if (result.respData == null) {
            return;
        }
        if (result.statusCode == 200) {
            if (result.respData.deviceCount.equals("0")) {
                mTvTitleStatistics.setText("网店宣传");
                mMainPublicityCurrentTime.setVisibility(View.GONE);
                mMainPublicityTimeSwitch.setVisibility(View.GONE);
                mLlPropagandaView.setVisibility(View.GONE);
                mLlVisitView.setVisibility(View.VISIBLE);
                mTvVisitTodayData.setText(Utils.getNumToString(Integer.parseInt(result.respData.todayUv), true));
                mTvVisitYesterdayData.setText(Utils.getNumToString(Integer.parseInt(result.respData.yesterdayUv), true));
                mTvVisitAccumulateData.setText(Utils.getNumToString(Integer.parseInt(result.respData.totalUv), true));
            } else {
                mTvTitleStatistics.setText(ResourceUtils.getString(R.string.layout_statistics_title));
                mMainPublicityCurrentTime.setVisibility(View.VISIBLE);
                mMainPublicityTimeSwitch.setVisibility(View.VISIBLE);
                mLlPropagandaView.setVisibility(View.VISIBLE);
                mLlVisitView.setVisibility(View.GONE);
                mTvWifiToday.setText(Utils.getNumToString(Integer.parseInt(result.respData.wifiCount), true));
                mTvWifiAccumulate.setText(Utils.getNumToString(Integer.parseInt(result.respData.totalWifiCount), true));
                mTvVisitToday.setText(Utils.getNumToString(Integer.parseInt(result.respData.uv), true));
                mTvVisitAccumulate.setText(Utils.getNumToString(Integer.parseInt(result.respData.totalUv), true));
            }
        }


    }

    private void handlerRegistryResult(RegistryDataResult registryData) {
        if (registryData != null) {
            mTvNewRegisterToday.setText(Utils.getNumToString(registryData.respData.userCount, true));
            customerCount = registryData.respData.totalUserCount;
            mTvNewRegisterAccumulate.setText(String.valueOf(customerCount));

            int lastViewCount = SharedPreferenceHelper.getLastViewCustomerCount();
            if (lastViewCount < 0) {
                SharedPreferenceHelper.setLastViewCustomerCount(customerCount);
            } else if (customerCount != lastViewCount) {
                customerCountMark.setVisibility(View.VISIBLE);
            }
        }
    }

    private void handlerCouponResult(CouponDataResult couponData) {
        if (couponData != null) {
            mTvCouponGetToday.setText(Utils.getNumToString(couponData.respData.couponGetCount, true));
            mTvCouponGetAccumulate.setText(Utils.getNumToString(couponData.respData.totalCouponGetCount, true));
        }

    }


    private void handlerIndexOrderResult(IndexOrderData orderData) {
        if (orderData != null) {
            try {
                mTvPendingOrderCount.setText(orderData.respData.submitCount);
                mTvAcceptedOrderCount.setText(Utils.getNumToString(Integer.parseInt(orderData.respData.acceptTotal), true));
                mTvCompletedOrderCount.setText(orderData.respData.acceptCount);
                try {
                    ((MainActivity) getActivity()).setPendingOrderCount(Integer.parseInt(orderData.respData.submitCount));
                } catch (Exception ignore) {

                }
            } catch (Exception e) {
                XLogger.e("parse int error: " + e.getMessage());
            }
        }
    }

    @CheckBusinessPermission(PermissionConstants.MG_INDEX_VERIFY)
    public void initVerify() {
        mVerifyLayout.setVisibility(View.VISIBLE);
    }

    @CheckBusinessPermission(PermissionConstants.MG_INDEX_BAD_COMMENT)
    public void initBadComments() {
        mBadCommentLayout.setVisibility(View.VISIBLE);
    }

    @CheckBusinessPermission(PermissionConstants.MG_INDEX_ORDER)
    public void initOrderLayout() {
        mOrderLayout.setVisibility(View.VISIBLE);
    }

    @CheckBusinessPermission(PermissionConstants.MG_INDEX_STAT)
    public void initStatisticLayout() {
        mStatisticsLayout.setVisibility(View.VISIBLE);
        initStatisticsWifi();
        initStatisticsVisit();
    }

    @CheckBusinessPermission(PermissionConstants.MG_INDEX_WIFI)
    public void initStatisticsWifi() {
        mWifiTodayLayout.setVisibility(View.VISIBLE);
    }

    @CheckBusinessPermission(PermissionConstants.MG_INDEX_ONLINE)
    public void initStatisticsVisit() {
        mVisitTodayLayout.setVisibility(View.VISIBLE);
    }

    @CheckBusinessPermission(PermissionConstants.MG_EXPAND_CUSTOMERS)
    public void initMarketingLayout() {
        mClientLayout.setVisibility(View.VISIBLE);
        initStatisticsNewCustomer();
        initMarketingCouponDeliver();
    }

    @CheckBusinessPermission(PermissionConstants.MG_INDEX_REGISTER)
    public void initStatisticsNewCustomer() {
        mNewRegisterLayout.setVisibility(View.VISIBLE);
    }

    @CheckBusinessPermission(PermissionConstants.MG_INDEX_COUPON)
    public void initMarketingCouponDeliver() {
        mCouponGetLayout.setVisibility(View.VISIBLE);
    }

    private void initVisibilityForViews() {
        //核销
        initVerify();
        //统计数据
        initStatisticLayout();
        //营销数据
        initMarketingLayout();
        //订单
        initOrderLayout();
        //差评
        initBadComments();

        //排行榜
        //  WidgetUtils.setViewVisibleOrGone(mRankingLayout, AuthHelper.checkAuth(AuthConstants.AUTH_CODE_INDEX_RANKING) != null, false);
    }

    private void initTitleView(View view) {
        mRlToolbar = (RelativeLayout) view.findViewById(R.id.rl_toolbar);
        mRlToolbar.setBackgroundColor(ResourceUtils.getColor(R.color.main_tool_bar_bg));
        imageLeft = (ImageView) view.findViewById(R.id.toolbar_left);
        imageLeft.setImageResource(R.drawable.mainpage_imgleft_selected);
        imageLeft.setVisibility(View.VISIBLE);
        imageLeft.setOnClickListener(this);
        mMenuAppVersion.setText("v" + AppConfig.getAppVersionNameAndCode());
        mSwipeRefreshLayout.setColorSchemeResources(R.color.primary_color);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshMainPageData();
            }
        });
    }

    private void getBadCommentData() {
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_BAD_COMMENT_AND_COMPLAINT_LIST);
    }


    private void handlerCommentAndComplaint(CommentAndComplaintListResult result) {
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
        if (result.statusCode == 200) {
            if (result.respData == null || result.respData.size() == 0) {
                badCommentList.setVisibility(View.GONE);
                mBadCommentFinish.setVisibility(View.VISIBLE);
                return;
            } else {
                badCommentList.setVisibility(View.VISIBLE);
                mBadCommentFinish.setVisibility(View.GONE);
                mCommentList.clear();
                mCommentList.addAll((Collection<? extends CommentBean>) result.respData);
                badCommentListAdapter.setData(mCommentList);
            }
        } else {
            badCommentList.setVisibility(View.GONE);
            mBadCommentFinish.setVisibility(View.VISIBLE);
        }
    }


    private void initTechRankingView() {

        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_TECH_RANK_DATA);
    }

    private void handlerRankingResult(TechRankDataResult result) {
        if (null != result.respData.userRanking) {
            Glide.with(getActivity()).load(result.respData.userRanking.avatarUrl).into(mCvStarRegister);
            mTvStarRegisterUser.setText(result.respData.userRanking.name);
            if (Utils.isNotEmpty(result.respData.userRanking.serialNo)) {
                Spannable spanString = Utils.changeColor("[ " + result.respData.userRanking.serialNo + " ]",
                        ResourceUtils.getColor(R.color.customer_comment_reward_text_color), 2, 2 + String.valueOf(result.respData.userRanking.serialNo).length());
                mTvStarRegisterTechNo.setText(spanString);
                mTvStarRegisterTechNo.setVisibility(View.VISIBLE);
            } else {
                mTvStarRegisterTechNo.setVisibility(View.GONE);
            }
        } else {
            mTvStarRegisterUser.setText(ResourceUtils.getString(R.string.rank_null_text));
            mTvStarRegisterTechNo.setVisibility(View.GONE);
        }
        if (null != result.respData.paidRanking) {
            Glide.with(getActivity()).load(result.respData.paidRanking.avatarUrl).into(mCvStarSales);
            mTvStarSales.setText(result.respData.paidRanking.name);
            if (Utils.isNotEmpty(result.respData.paidRanking.serialNo)) {
                mTvStarSalesTechNo.setText(result.respData.paidRanking.serialNo);
            } else {
                mTvStarSalesTechNo.setVisibility(View.GONE);
            }

        } else {
            mTvStarSales.setText(ResourceUtils.getString(R.string.rank_null_text));
            mTvStarSalesTechNo.setVisibility(View.GONE);
        }
        if (null != result.respData.commentRanking) {
            Glide.with(getActivity()).load(result.respData.commentRanking.avatarUrl).into(mCvStarService);
            mTvStarService.setText(result.respData.commentRanking.name);
            if (Utils.isNotEmpty(result.respData.commentRanking.serialNo)) {
                mTvStarServiceTechNo.setText(result.respData.commentRanking.serialNo);
            } else {
                mTvStarServiceTechNo.setVisibility(View.GONE);
            }
        } else {
            mTvStarService.setText(ResourceUtils.getString(R.string.rank_null_text));
            mTvStarServiceTechNo.setVisibility(View.GONE);
        }

    }

    private void showServiceOutMenu(String userId, String phone, String emChatId, String userName, String userHeadImgUrl, String commentId, String returnStatus) {


        BottomPopupWindow popupWindow = BottomPopupWindow.getInstance(getActivity(), phone, emChatId, commentId, returnStatus, new BottomPopupWindow.OnRootSelectedListener() {
            @Override
            public void onItemSelected(ReturnVisitMenu rootMenu) {
                switch (rootMenu.getType()) {
                    case 1:
                        PermissionTool.requestPermission(MainPageFragment.this,
                                new String[]{Manifest.permission.CALL_PHONE},
                                new String[]{"拨打电话"},
                                REQUEST_CODE_PHONE);
                        break;
                    case 2:
                        EventBus.getDefault().post(new UserInfoEvent(0, 1, new UserInfoBean(userId, emChatId, userName, userHeadImgUrl)));
                        break;
                    case 3:
                        doRemarkComment(commentId, RequestConstant.FINISH_COMMENT);
                        break;
                    case 4:
                        doRemarkComment(commentId, RequestConstant.VALID_COMMENT);
                        break;

                }
            }

        });
        popupWindow.show();
    }

    private void doRemarkComment(String commentId, String status) {
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_BAD_COMMENT_ID, commentId);
        params.put(RequestConstant.KEY_COMMENT_STATUS, status);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_CHANGE_COMMENT_STATUS, params);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PHONE) {
            if (resultCode == RESULT_OK) {
                String callPhone = ResourceUtils.getString(R.string.service_phone);
                if (Utils.isNotEmpty(callPhone)) {
                    toCallPhone(callPhone);
                } else {
                    Utils.makeShortToast(getActivity(), ResourceUtils.getString(R.string.phone_is_null));
                }
            } else {
                Toast.makeText(getActivity(), "获取权限失败", Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == REQUEST_CODE_CAMERA) {
            if (resultCode == RESULT_OK) {
                startActivity(new Intent(getActivity(), CaptureActivity.class));
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

    @OnClick({R.id.tv_qr_code, R.id.btn_consume})
    public void onMainHeadClicked(View v) {
        switch (v.getId()) {
            case R.id.tv_qr_code:
                PermissionTool.requestPermission(MainPageFragment.this, new String[]{Manifest.permission.CAMERA}, new String[]{"扫描二维码"}, REQUEST_CODE_CAMERA);
                break;
            case R.id.btn_consume:
                mPhoneNoOrCouponNo = mCetPaidOrderConsumeCode.getText().toString();
                if (Utils.isEmpty(mPhoneNoOrCouponNo)) {
                    Utils.makeShortToast(getActivity(), ResourceUtils.getString(R.string.main_fragment_consume_code_hint));
                    return;
                }
                if (Utils.matchPhoneNumFormat(mPhoneNoOrCouponNo)) {
                    Intent intent = new Intent(getActivity(), VerificationListActivity.class);
                    intent.putExtra(Constant.PARAM_PHONE_NUMBER, mPhoneNoOrCouponNo);
                    startActivity(intent);
                } else {
                    VerificationManagementHelper.checkVerificationType(mPhoneNoOrCouponNo);
                }

                break;
        }
    }

    @OnClick({R.id.main_marketing_time_switch, R.id.main_publicity_time_switch, R.id.main_account_time_switch,
            R.id.main_bad_comment, R.id.layout_technician_ranking, R.id.layout_technician_pk_ranking, R.id.layout_order, R.id.ll_wifi_today, R.id.ll_visit_today,
            R.id.ll_new_register_today, R.id.ll_coupon_get_today, R.id.tv_qr_code, R.id.toolbar_right_text,
            R.id.ll_account_paid, R.id.ll_account_sail_view, R.id.ll_sail_view, R.id.ll_visit_view, R.id.newOrderLayout})
    public void onClickView(View v) {
        switch (v.getId()) {
            case R.id.main_publicity_time_switch:
                switchPublicityTimeData();
                break;
            case R.id.main_marketing_time_switch:
                switchMainMarketingTimeData();
                break;
            case R.id.main_account_time_switch:
                switchMainAccountTimeData();
                break;
            case R.id.layout_order:
                Intent intentOrder = new Intent(getActivity(), OrdersDetailActivity.class);
                intentOrder.putExtra(Constant.PARAM_RANGE, 0);
                startActivity(intentOrder);
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
            case R.id.ll_wifi_today:
                Intent intentWifi = new Intent(getActivity(), WifiReportActivity.class);
                intentWifi.putExtra(RequestConstant.KEY_MAIN_TITLE, mTvTitleWifi.getText().toString().substring(0, mTvTitleWifi.getText().toString().length() - 1));
                startActivity(intentWifi);
                break;
            case R.id.ll_visit_today:
                Intent intentVisit = new Intent(getActivity(), VisitReportActivity.class);
                intentVisit.putExtra(RequestConstant.KEY_MAIN_TITLE, mTvTitleVisit.getText().toString().substring(0, mTvTitleVisit.getText().toString().length() - 1));
                startActivity(intentVisit);
                break;
            case R.id.ll_new_register_today:
                Intent intentRegister = new Intent(getActivity(), RegisterReportActivity.class);
                intentRegister.putExtra(RequestConstant.KEY_MAIN_TITLE, mTvTitleRegister.getText().toString().substring(0, mTvTitleRegister.getText().toString().length() - 1));
                startActivity(intentRegister);
                SharedPreferenceHelper.setLastViewCustomerCount(customerCount);
                redPointService.clear(XmdPushMessage.BUSINESS_TYPE_NEW_CUSTOMER);
                break;
            case R.id.ll_coupon_get_today:
                Intent intentCoupon = new Intent(getActivity(), CouponReportActivity.class);
                intentCoupon.putExtra(RequestConstant.KEY_MAIN_TITLE, mTvTitleCoupon.getText().toString().substring(0, mTvTitleCoupon.getText().toString().length() - 1));
                startActivity(intentCoupon);
                break;
            case R.id.main_bad_comment:
                //全部差评
                CommentListActivity.startCommentListActivity(getActivity(), true, "");
                break;
            case R.id.toolbar_right_text:
                startActivity(new Intent(getActivity(), VerificationRecordListActivity.class));
                break;
            case R.id.ll_account_paid:
                //线上买单
                UINavigation.gotoOnlinePayNotifyList(getContext());
                SharedPreferenceHelper.setLastViewFastPayValue(fastPayAmount);
                redPointService.clear(XmdPushMessage.BUSINESS_TYPE_FAST_PAY);
                break;
            case R.id.ll_account_sail_view:
            case R.id.ll_sail_view:
                //营销收入
                startActivity(new Intent(getActivity(), MarketingIncomeActivity.class));
                break;
            case R.id.ll_visit_view:
                Intent visitIntent = new Intent(getActivity(), VisitReportActivity.class);
                visitIntent.putExtra(RequestConstant.KEY_MAIN_TITLE, mTvTitleVisit.getText().toString().substring(0, mTvTitleVisit.getText().toString().length() - 1));
                startActivity(visitIntent);
                break;
            case R.id.newOrderLayout:
                redPointService.clear(XmdPushMessage.BUSINESS_TYPE_ORDER);
                ((MainActivity) getActivity()).switchTo(MainActivity.sTabOrder);
                break;

        }
    }

    private void switchPublicityTimeData() {
        if (mMainPublicityTimeSwitch.getText().equals(ResourceUtils.getString(R.string.layout_main_btn_today))) {
            mMainPublicityTimeSwitch.setText(ResourceUtils.getString(R.string.layout_main_btn_yesterday));
            mMainPublicityCurrentTime.setText(ResourceUtils.getString(R.string.layout_main_publicity_today));
            getPublicityData(0);
            mCurrentPublicityData = 0;
        } else {
            mMainPublicityTimeSwitch.setText(ResourceUtils.getString(R.string.layout_main_btn_today));
            mMainPublicityCurrentTime.setText(ResourceUtils.getString(R.string.layout_main_publicity_yesterday));
            getPublicityData(1);
            mCurrentPublicityData = 1;
        }
    }

    private void switchMainMarketingTimeData() {
        if (mMainMarketingTimeSwitch.getText().equals(ResourceUtils.getString(R.string.layout_main_btn_today))) {
            mMainMarketingTimeSwitch.setText(ResourceUtils.getString(R.string.layout_main_btn_yesterday));
            mMainMarketingCurrentTime.setText(ResourceUtils.getString(R.string.layout_main_publicity_today));
            mCurrentMarketingData = 0;
        } else {
            mMainMarketingTimeSwitch.setText(ResourceUtils.getString(R.string.layout_main_btn_today));
            mMainMarketingCurrentTime.setText(ResourceUtils.getString(R.string.layout_main_publicity_yesterday));
            mCurrentMarketingData = 1;
        }
        getMarketingData(mCurrentMarketingData);
    }

    private void switchMainAccountTimeData() {
        if (mainAccountTimeSwitch.getText().equals(ResourceUtils.getString(R.string.layout_main_btn_today))) {
            mainAccountTimeSwitch.setText(ResourceUtils.getString(R.string.layout_main_btn_yesterday));
            tvAccountCurrentTime.setText(ResourceUtils.getString(R.string.layout_main_publicity_today));
            mCurrentAccountData = 0;
        } else {
            mainAccountTimeSwitch.setText(ResourceUtils.getString(R.string.layout_main_btn_today));
            tvAccountCurrentTime.setText(ResourceUtils.getString(R.string.layout_main_publicity_yesterday));
            mCurrentAccountData = 1;
        }
        getAccountData(mCurrentAccountData);
    }

    private void getMarketingData(int i) {
        if (i == 0) {
            Map<String, String> params = new HashMap<>();
            params.put(RequestConstant.KEY_DATE, "");
            MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_COUPON_DATA_INDEX, params);
            MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_REGISTRY_DATA, params);

        } else {
            Map<String, String> params = new HashMap<>();
            long yesterday = System.currentTimeMillis() - 24 * 60 * 60 * 1000;
            params.put(RequestConstant.KEY_DATE, DateUtil.longToDate(yesterday));
            MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_COUPON_DATA_INDEX, params);
            MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_REGISTRY_DATA, params);
        }
    }

    private void getPublicityData(int i) {
        Map<String, String> params = new HashMap<>();
        if (i == 0) {
            params.put(RequestConstant.KEY_DATE, "");
        } else {
            long yesterday = System.currentTimeMillis() - 24 * 60 * 60 * 1000;
            params.put(RequestConstant.KEY_DATE, DateUtil.longToDate(yesterday));
        }
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_DATA_STATISTICS_WIFI_DATA, params);
    }

    private void getOrderData() {
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET__INDEX_ORDER_DATA);
    }

    private void getAccountData(int currentAccountData) {
        Map<String, String> params = new HashMap<>();
        if (currentAccountData == 0) {
            params.put(RequestConstant.KEY_DATE, "");
        } else {
            long yesterday = System.currentTimeMillis() - 24 * 60 * 60 * 1000;
            params.put(RequestConstant.KEY_DATE, DateUtil.longToDate(yesterday));
        }
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_DATA_STATISTICS_ACCOUNT_DATA, params);
    }


    @OnClick({R.id.tv_setting_head, R.id.menu_version_update, R.id.menu_setting, R.id.menu_fast_reply, R.id.menu_service, R.id.menu_suggest, R.id.menu_choice_club, R.id.menu_change_password, R.id.menu_activity_logout, R.id.view_transparent})
    public void onMenuClicked(View v) {
        switch (v.getId()) {
            case R.id.tv_setting_head:
                if (BuildConfig.DEBUG) {
                    startActivity(new Intent(getActivity(), ConfigurationMonitorActivity.class));
                } else {
                    mSlidingMenu.toggle();
                }
                break;
            case R.id.menu_version_update:
                startActivity(new Intent(getActivity(), AboutUsActivity.class));
                break;
            case R.id.menu_setting:
                startActivity(new Intent(getActivity(), SettingActivity.class));
                break;
            case R.id.menu_fast_reply:
                XmdChat.getInstance().showFastReplyEditView();
                break;
            case R.id.menu_service:
                showServiceOutMenu("", servicePhone, "", "", "", "", "");
                break;
            case R.id.menu_suggest:
                startActivity(new Intent(getActivity(), FeedbackActivity.class));
                break;
            case R.id.menu_choice_club:
                Intent intent = new Intent(getActivity(), ClubListActivity.class);
                intent.putExtra(ClubListActivity.EXTRA_SHOW_LEFT, true);
                startActivity(intent);
                break;
            case R.id.menu_change_password:
                startActivity(new Intent(getActivity(), ModifyPasswordActivity.class));
                break;
            case R.id.menu_activity_logout:
                doLogout();
                break;
            case R.id.view_transparent:
                mSlidingMenu.closeMenu();
                break;
        }

    }

    private void doLogout() {
        new AlertDialogBuilder(getActivity())
                .setTitle(ResourceUtils.getString(R.string.dialog_system_message))
                .setMessage(ResourceUtils.getString(R.string.logout_confirm_message))
                .setPositiveButton(ResourceUtils.getString(R.string.btn_confirm), v -> doInnerLogout())
                .setNegativeButton(ResourceUtils.getString(R.string.cancel), null)
                .show();
    }

    private void doInnerLogout() {
        Manager.getInstance().prepareBeforeUserLogout();
        MainActivity activity;
        activity = (MainActivity) this.getActivity();
        activity.gotoLoginActivity("");
    }

    @Override
    public void onClick(View v) {
        mSlidingMenu.toggle();
    }


    private XmdPushMessageListener xmdPushMessageListener = new XmdPushMessageListener() {
        @Override
        public void onMessage(XmdPushMessage message) {
            switch (message.getBusinessType()) {
                case XmdPushMessage.BUSINESS_TYPE_ORDER:
                    getOrderData();
                    break;
                case XmdPushMessage.BUSINESS_TYPE_FAST_PAY:
                    if (showFastPay) {
                        redPointService.inc(XmdPushMessage.BUSINESS_TYPE_FAST_PAY);
                        getAccountData(mCurrentAccountData);
                    }
                    break;
                case XmdPushMessage.BUSINESS_TYPE_NEW_CUSTOMER:
                    redPointService.inc(XmdPushMessage.BUSINESS_TYPE_NEW_CUSTOMER);
                    customerCount++;
                    mTvNewRegisterAccumulate.setText(String.valueOf(customerCount));
                    break;
                case XmdPushMessage.BUSINESS_TYPE_COMMENT:
                    getBadCommentData();
                    break;
            }
        }

        @Override
        public void onRawMessage(String message) {

        }
    };
}
