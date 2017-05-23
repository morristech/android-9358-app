package com.xmd.manager.window;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.xmd.manager.R;
import com.xmd.manager.common.ResourceUtils;
import com.xmd.manager.msgctrl.RxBus;
import com.xmd.manager.service.response.FavourableActivityListResult;
import com.xmd.manager.service.response.GroupInfoResult;
import com.xmd.manager.service.response.GroupListResult;
import com.xmd.manager.service.response.TechListResult;

import butterknife.ButterKnife;
import rx.Subscription;

/**
 * Created by lhj on 2016/9/26.
 */
public class GroupMessageCustomerActivity extends BaseActivity {

    private final static int CUSTOMER_FRAGMENT = 0;
    private final static int COUPON_FRAGMENT = 1;
    private final static int EDIT_CONTENT__FRAGMENT = 2;
    private final static int CONFIRM_FRAGMENT = 3;

    private GMessageSelectCustomerFragment mCustomerFragment;
    private GMessageSelectCouponFragment mCouponFragment;
    private GMessageEditContentFragment mEditContentFragment;
    private GMessageConfirmFragment mConfirmFragment;

    private Subscription mGroupListSubscription;
    private Subscription mGetGroupInfoSubscription;
    private Subscription mGetClubTechsSubscription;
    private Subscription mGetCouponListSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_message_customer);
        setRightVisible(true, ResourceUtils.getString(R.string.cancel), view -> finish());
        ButterKnife.bind(this);

        initView();

        mGroupListSubscription = RxBus.getInstance().toObservable(GroupListResult.class).subscribe(
                groupListResult -> mCustomerFragment.handlerGroupList(groupListResult));
        mGetGroupInfoSubscription = RxBus.getInstance().toObservable(GroupInfoResult.class).subscribe(
                groupInfoResult -> {
                    if (mCouponFragment != null) {
                        mCustomerFragment.handlerGroupInfoResult(groupInfoResult);
                    }

                    if (mEditContentFragment != null) {
                        mEditContentFragment.handlerGroupInfoResult(groupInfoResult);
                    }
                });
        mGetClubTechsSubscription = RxBus.getInstance().toObservable(TechListResult.class).subscribe(
                result -> mCustomerFragment.handleTechsResult(result));
        mGetCouponListSubscription = RxBus.getInstance().toObservable(FavourableActivityListResult.class).subscribe(
                activityResult -> mCouponFragment.handleFavourableActivityResult(activityResult));
    }

    private void initView() {
        showFragment(CUSTOMER_FRAGMENT);
    }

    private void setFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.fragment_message_step, fragment);
        transaction.commit();
        transaction.hide(fragment).show(fragment).commit();
    }

    private void showFragment(int index) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();

        if (mCustomerFragment != null) {
            transaction.hide(mCustomerFragment);
        }
        if (mCouponFragment != null) {
            transaction.hide(mCouponFragment);
        }
        if (mEditContentFragment != null) {
            transaction.hide(mEditContentFragment);
        }
        if (mConfirmFragment != null) {
            transaction.hide(mConfirmFragment);
        }


        switch (index) {
            case CUSTOMER_FRAGMENT:
                if (mCustomerFragment != null) {
                    transaction.show(mCustomerFragment);
                } else {
                    mCustomerFragment = new GMessageSelectCustomerFragment();
                    transaction.add(R.id.fragment_message_step, mCustomerFragment);
                }
                break;
            case COUPON_FRAGMENT:
                if (mCouponFragment != null) {
                    transaction.show(mCouponFragment);
                } else {
                    mCouponFragment = new GMessageSelectCouponFragment();
                    transaction.add(R.id.fragment_message_step, mCouponFragment);
                }
                break;
            case EDIT_CONTENT__FRAGMENT:
                if (mEditContentFragment != null) {
                    transaction.show(mEditContentFragment);
                } else {
                    mEditContentFragment = new GMessageEditContentFragment();
                    transaction.add(R.id.fragment_message_step, mEditContentFragment);
                }
                break;
            case CONFIRM_FRAGMENT:
                if (mConfirmFragment != null) {
                    transaction.show(mConfirmFragment);
                } else {
                    mConfirmFragment = new GMessageConfirmFragment();
                    transaction.add(R.id.fragment_message_step, mConfirmFragment);
                }
                break;
        }
        transaction.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unsubscribe(mGroupListSubscription, mGetGroupInfoSubscription, mGetClubTechsSubscription, mGetCouponListSubscription);
    }

    public void gotoEditContentFragment() {
        showFragment(EDIT_CONTENT__FRAGMENT);
        //setFragment(mEditContentFragment);
    }

    public void gotoCustomerFragment() {
        showFragment(CUSTOMER_FRAGMENT);
        //setFragment(mCustomerFragment);
    }

    public void gotoCouponFragment() {
        showFragment(COUPON_FRAGMENT);
        //setFragment(mCouponFragment);
    }

    public void gotoConfirmFragment() {
        showFragment(CONFIRM_FRAGMENT);
        //setFragment(mCouponFragment);
    }
}
