package com.xmd.contact;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.xmd.contact.httprequest.ConstantResources;
import com.xmd.permission.CheckBusinessPermission;
import com.xmd.permission.PermissionConstants;

import java.util.ArrayList;

/**
 * Created by Lhj on 17-7-26.
 */

public class FragmentController {
    private int mContainerId;
    private FragmentManager mFragmentManager;
    private ArrayList<Fragment> mFragments;
    private static FragmentController mController;
    private boolean isFromManager;

    public static FragmentController getInstance(Fragment fragment, int containerId, Boolean isManager) {
        if (mController == null) {
            mController = new FragmentController(fragment, containerId, isManager);
        }
        return mController;
    }

    private FragmentController(Fragment fragment, int containerId, Boolean fromManager) {
        this.mContainerId = containerId;
        mFragmentManager = fragment.getChildFragmentManager();
        this.isFromManager = fromManager;
        initFragment();
    }

    private void initFragment() {
        mFragments = new ArrayList<>();

        if (isFromManager) {
            addCLubAllCustomer();
            addClubRecentCustomer();
            addClubTechnician();
        } else {
            addContactsAllFragment();
            addRegisterFragment();
            addVisitorFragment();
            addTechnicianFragment();
        }
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        for (Fragment fragment : mFragments) {
            ft.add(mContainerId, fragment);
        }
        ft.commit();
    }

    public void addCLubAllCustomer() {
        mFragments.add(new ManagerContactsAllFragment());
    }

    public void addClubRecentCustomer() {
        mFragments.add(new ManagerContactsVisitorsFragment());
    }

    public void addClubTechnician() {
        ContactsTechnicianFragment fragment = new ContactsTechnicianFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(ConstantResources.APP_TYPE_IS_MANAGER,true);
        fragment.setArguments(bundle);
        mFragments.add(fragment);
    }

    //全部客户
    @CheckBusinessPermission(PermissionConstants.CONTACTS_CUSTOMER)
    public void addContactsAllFragment() {
            mFragments.add(new ContactsAllFragment());
    }

    //我的拓客
    @CheckBusinessPermission(PermissionConstants.CONTACTS_MY_CUSTOMER)
    public void addRegisterFragment() {
        mFragments.add(new ContactsRegisterFragment());
    }

    //最近访客
    @CheckBusinessPermission(PermissionConstants.CONTACTS_VISITOR)
    public void addVisitorFragment() {
        mFragments.add(new ContactsVisitorsFragment());
    }

    //本店
    @CheckBusinessPermission(PermissionConstants.CONTACTS_MY_CLUB)
    public void addTechnicianFragment() {
        ContactsTechnicianFragment fragment = new ContactsTechnicianFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(ConstantResources.APP_TYPE_IS_MANAGER,false);
        fragment.setArguments(bundle);
        mFragments.add(fragment);
    }

    public void showFragment(int position) {
        hideFragments();
        Fragment fragment = mFragments.get(position);
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        ft.show(fragment);
        ft.commitAllowingStateLoss();
    }

    private void hideFragments() {
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        for (Fragment fragment : mFragments) {
            ft.hide(fragment);
        }
        ft.commitAllowingStateLoss();
    }

    public Fragment getFragment(int position) {
        return mFragments.get(position);
    }

    public static void destroyController() {
        mController = null;
    }
}
