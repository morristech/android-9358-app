package com.xmd.technician.window;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.xmd.technician.R;
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.common.Utils;

/**
 * Created by Lhj on 2017/2/9.
 */

public class ShareDetailListActivity extends BaseActivity implements BaseFragment.IFragmentCallback {

    public static final String SHARE_TOTAL_AMOUNT = "share_total_amount";
    public static final int PAID_COUPON = 0x001;//点钟券
    public static final int NORMAL_COUPON = 0x002;//普通券
    public static final int ONCE_CARD = 0x003;//次卡
    public static final int LIMIT_GRAB = 0x004;//限时抢
    public static final int PAY_FOR_ME = 0x005;//夺宝
    public static final int CLUB_JOURNAL = 0x006;//会所期刊
    public static final int REWARD_ACTIVITY = 0x007;//抽奖活动
    public static final int INVITATION_REWARD_ACTIVITY = 0x008;//邀请有礼
    public static final int GROUPS_ACTIVITY = 0x009;//拼团活动
    private static final String SHARE_TYPE = "share_type";
    private static final String SHARE_TITLE = "share_title";
    //   public static final int OFFLINE_ACTIVITY = 0x008;//线下活动
    public String mCurrentTitle;
    private int mCurrentShareList;
    private int mTotal;

    public static void startShareDetailListActivity(Activity activity, int shareType, String title, int totalAmount) {
        Intent intent = new Intent(activity, ShareDetailListActivity.class);
        intent.putExtra(SHARE_TYPE, shareType);
        intent.putExtra(SHARE_TITLE, title);
        intent.putExtra(SHARE_TOTAL_AMOUNT, totalAmount);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_detail_list);
        mCurrentShareList = getIntent().getIntExtra(SHARE_TYPE, 0);
        mCurrentTitle = getIntent().getStringExtra(SHARE_TITLE);
        mTotal = getIntent().getIntExtra(SHARE_TOTAL_AMOUNT, 0);
        initView(mCurrentShareList);
    }

    private void initView(int type) {
        setBackVisible(true);
        switch (type) {
            case 0:
                return;
            case PAID_COUPON:
                setTitle(Utils.isEmpty(mCurrentTitle) ? ResourceUtils.getString(R.string.paid_coupon_list_title) : mCurrentTitle);
                setFragment(PaidCouponListFragment.getInstance(mTotal));
                break;
            case NORMAL_COUPON:
                setTitle(Utils.isEmpty(mCurrentTitle) ? ResourceUtils.getString(R.string.normal_coupon_list_title) : mCurrentTitle);
                setFragment(NormalCouponListFragment.getInstance(mTotal));
                break;
            case ONCE_CARD:
                setTitle(Utils.isEmpty(mCurrentTitle) ? ResourceUtils.getString(R.string.once_card) : mCurrentTitle);
                setFragment(OnceCardListFragment.getInstance(mTotal));
                break;
            case LIMIT_GRAB:
                setTitle(Utils.isEmpty(mCurrentTitle) ? ResourceUtils.getString(R.string.limit_grab) : mCurrentTitle);
                setFragment(LimitGrabListFragment.getInstance(mTotal));
                break;
            case PAY_FOR_ME:
                setTitle(Utils.isEmpty(mCurrentTitle) ? ResourceUtils.getString(R.string.pay_for_me) : mCurrentTitle);
                setFragment(PayForMeListFragment.getInstance(mTotal));
                break;
            case CLUB_JOURNAL:
                setTitle(Utils.isEmpty(mCurrentTitle) ? ResourceUtils.getString(R.string.club_journal) : mCurrentTitle);
                setFragment(ClubJournalListFragment.getInstance(mTotal));
                break;
            case REWARD_ACTIVITY:
                setTitle(Utils.isEmpty(mCurrentTitle) ? ResourceUtils.getString(R.string.reward_activity) : mCurrentTitle);
                setFragment(RewardActivityListFragment.getInstance(mTotal));
                break;
            case INVITATION_REWARD_ACTIVITY:
                setTitle(Utils.isEmpty(mCurrentTitle) ? ResourceUtils.getString(R.string.invitation_reward_activity) : mCurrentTitle);
                setFragment(InvitationRewardActivityListFragment.getInstance());
                break;
            case GROUPS_ACTIVITY:
                setTitle(Utils.isEmpty(mCurrentTitle)?ResourceUtils.getString(R.string.groups_activity):mCurrentTitle);
                setFragment(GroupBuyActivityListFragment.getInstance(mTotal));
                break;

       /*     case OFFLINE_ACTIVITY:
                setTitle(Utils.isEmpty(mCurrentTitle)? ResourceUtils.getString(R.string.offline_activity_list_title):mCurrentTitle);
                setFragment(OffLineActivityListFragment.getInstance());
                break;*/
        }
    }

    private void setFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.share_detail_fragment, fragment);
        transaction.commit();
    }

}
