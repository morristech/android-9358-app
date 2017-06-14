package com.xmd.technician.common;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.xmd.technician.window.ContactsAllFragment;
import com.xmd.technician.window.ContactsRegisterFragment;
import com.xmd.technician.window.ContactsTechnicianFragment;
import com.xmd.technician.window.ContactsVisitorsFragment;

import java.util.ArrayList;

/**
 * Created by Lhj on 17-5-27.
 */

public class FragmentController {

    private int mContainerId;
    private FragmentManager mFragmentManager;
    private ArrayList<Fragment> mFragments;
    private static FragmentController mController;


    public static FragmentController getInstance(Fragment fragment, int containerId) {
        if (mController == null) {
            mController = new FragmentController(fragment, containerId);
        }
        return mController;
    }

    private FragmentController(Fragment fragment, int containerId) {
        this.mContainerId = containerId;
        mFragmentManager = fragment.getChildFragmentManager();
        initFragment();
    }

    private void initFragment() {
        mFragments = new ArrayList<>();
        mFragments.add(new ContactsAllFragment());
        mFragments.add(new ContactsRegisterFragment());
        mFragments.add(new ContactsVisitorsFragment());
        mFragments.add(new ContactsTechnicianFragment());

        FragmentTransaction ft = mFragmentManager.beginTransaction();
        for (Fragment fragment : mFragments) {
            ft.add(mContainerId, fragment);
        }
        ft.commit();
    }

    public void showFragment(int position) {
        hideFragments();
        Fragment fragment = mFragments.get(position);
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        ft.show(fragment);
        ft.commit();
    }

    private void hideFragments() {
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        for (Fragment fragment : mFragments) {
            ft.hide(fragment);
        }
        ft.commit();
    }

    public Fragment getFragment(int position) {
        return mFragments.get(position);
    }

    public static void destoryController() {
        mController = null;
    }

}
