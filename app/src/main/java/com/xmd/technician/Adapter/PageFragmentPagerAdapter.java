package com.xmd.technician.Adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;


public class PageFragmentPagerAdapter extends FragmentPagerAdapter {

    private Context mContext;

    private List<Fragment> mFragments;

    public PageFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        mContext = context;
        mFragments = new ArrayList<Fragment>();
    }

    public void removeFragments() {
        mFragments.clear();
    }

    public void addFragment(String className, Bundle args) {
        Fragment fragment = Fragment.instantiate(mContext, className, args);
        mFragments.add(fragment);
        notifyDataSetChanged();
    }

    public void addFragment(Fragment fragment) {
        mFragments.add(fragment);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mFragments.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }

    public List<Fragment> getFragments() {
        return mFragments;
    }

    public void setFragments(List<Fragment> mFragments) {
        this.mFragments = mFragments;
    }

}