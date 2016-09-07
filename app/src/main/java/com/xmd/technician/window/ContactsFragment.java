package com.xmd.technician.window;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.xmd.technician.Adapter.PageFragmentPagerAdapter;
import com.xmd.technician.R;
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.http.RequestConstant;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/7/1.
 */
public class ContactsFragment extends BaseFragment implements View.OnClickListener {
    @Bind(R.id.table_contact)
    TextView mTableContact;
    @Bind(R.id.contact_left)
    RelativeLayout mContactLeft;
    @Bind(R.id.table_club)
    TextView mTableClub;
    @Bind(R.id.contact_right)
    RelativeLayout mContactRight;
    @Bind(R.id.contact)
    LinearLayout mContact;
    @Bind(R.id.iv_tab_bottom_img)
    ImageView mTabBottomImg;
    @Bind(R.id.vp_contact)
    ViewPager mViewpagerContact;
    @Bind(R.id.contact_visitor)
    RelativeLayout mContactVisitor;
    @Bind(R.id.recently_visitor)
    TextView mRecentlyVisitor;
    private PageFragmentPagerAdapter mPageFragmentPagerAdapter;
    private List<Fragment> mlistViews;
    private int mCurrentPage;
    private int screenWidth;
    private Activity ac;
    private Map<String, String> params = new HashMap<>();
    private View viewM;
    private View view;
    private PopupWindow window = null;
    private LayoutInflater layoutInflater;
    private ImageView imgRight;
    private boolean currentFragmentIsContact;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ac = getActivity();
        initView();
        initImageView();
        initViewPager();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_constacts, container, false);
        ButterKnife.bind(this, view);
        ((TextView) view.findViewById(R.id.toolbar_title)).setText(R.string.main_conversion);
        view.findViewById(R.id.contact_more).setVisibility(View.VISIBLE);
        imgRight = ((ImageView) view.findViewById(R.id.toolbar_right_img));
        imgRight.setImageDrawable(ResourceUtils.getDrawable(R.drawable.contact_add));
        imgRight.setOnClickListener(this);
        mRecentlyVisitor.setTextColor(ResourceUtils.getColor(R.color.colorMainBtn));
        return view;
    }

    private void initView() {
        mTableClub.setOnClickListener(this);
        mContactLeft.setOnClickListener(this);
        mContactVisitor.setOnClickListener(this);

    }

    private void initImageView() {
        DisplayMetrics dpMetrics = new DisplayMetrics();
        getActivity().getWindow().getWindowManager().getDefaultDisplay().getMetrics(dpMetrics);
        screenWidth = dpMetrics.widthPixels;
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mTabBottomImg.getLayoutParams();
        lp.width = screenWidth / 3;
        mTabBottomImg.setLayoutParams(lp);
    }

    private void resetTextView() {
        mRecentlyVisitor.setTextColor(ResourceUtils.getColor(R.color.colorHead));
        mTableClub.setTextColor(ResourceUtils.getColor(R.color.colorHead));
        mTableContact.setTextColor(ResourceUtils.getColor(R.color.colorHead));
    }

    private void initViewPager() {
        mlistViews = new ArrayList<>();
        mPageFragmentPagerAdapter = new PageFragmentPagerAdapter(getChildFragmentManager(), getActivity());
        mPageFragmentPagerAdapter.addFragment(new RecentLyVisitorFragment());
        mPageFragmentPagerAdapter.addFragment(new CustomerListFragment());
        mPageFragmentPagerAdapter.addFragment(new MyClubListFragment());
        mViewpagerContact.setAdapter(mPageFragmentPagerAdapter);
        mViewpagerContact.setCurrentItem(0);
        mViewpagerContact.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float offset, int positionOffsetPixels) {
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mTabBottomImg.getLayoutParams();
                if(mCurrentPage ==0 &&position==0){
                    lp.leftMargin = mCurrentPage *(screenWidth / 3);
                } else if(mCurrentPage ==1 && position ==1){
                    lp.leftMargin =  mCurrentPage * (screenWidth / 3);
                }else if(mCurrentPage ==2 && position ==2){
                    lp.leftMargin =  mCurrentPage * (screenWidth / 3);
                }
                mTabBottomImg.setLayoutParams(lp);
            }

            @Override
            public void onPageSelected(int position) {
                resetTextView();
                switch (position) {
                    case 0:
                        mRecentlyVisitor.setTextColor(ResourceUtils.getColor(R.color.colorMainBtn));
                        currentFragmentIsContact = false;
                        break;
                    case 1:
                        mTableContact.setTextColor(ResourceUtils.getColor(R.color.colorMainBtn));
                        currentFragmentIsContact = true;
                        break;
                    case 2:
                        mTableClub.setTextColor(ResourceUtils.getColor(R.color.colorMainBtn));
                        currentFragmentIsContact = false;
                        break;
                }
                mCurrentPage = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.contact_visitor:
                resetTextView();
                mViewpagerContact.setCurrentItem(0);
                mCurrentPage = 0;
                mRecentlyVisitor.setTextColor(ResourceUtils.getColor(R.color.colorMainBtn));
                currentFragmentIsContact = false;

                break;
            case R.id.contact_left:
                if (currentFragmentIsContact) {
                    showOutMenu();
                } else {
                    resetTextView();
                    mViewpagerContact.setCurrentItem(1);
                    mCurrentPage = 1;
                    mTableContact.setTextColor(ResourceUtils.getColor(R.color.colorMainBtn));
                    currentFragmentIsContact = true;
                }
                break;
            case R.id.table_club:
                resetTextView();
                mViewpagerContact.setCurrentItem(2);
                mCurrentPage = 2;
                mTableClub.setTextColor(ResourceUtils.getColor(R.color.colorMainBtn));

                break;
            case R.id.toolbar_right_img:
                Intent intent = new Intent(ac, AddFriendActivity.class);
                ac.startActivity(intent);
                break;
        }
    }

    public void showOutMenu() {
        layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        viewM = layoutInflater.inflate(R.layout.search_contacts_layout, null);
        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
        lp.alpha = 0.5f;
        lp.dimAmount = 0.5f;
        getActivity().getWindow().setAttributes(lp);
        if (window == null) {
            window = new PopupWindow(viewM, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
            window.setAnimationStyle(R.style.popup_window_style);
            ColorDrawable dw = new ColorDrawable(Color.parseColor("#00FF0000"));
            window.setBackgroundDrawable(dw);
            window.setOnDismissListener(new PopupWindow.OnDismissListener() {

                @Override
                public void onDismiss() {
                    WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
                    lp.alpha = 1.0f;
                    lp.dimAmount = 1.0f;
                    getActivity().getWindow().setAttributes(lp);
                }
            });
            LinearLayout allContact = (LinearLayout) viewM.findViewById(R.id.all_contact);
            LinearLayout phoneContact = (LinearLayout) viewM.findViewById(R.id.phone_contact);
            LinearLayout fansContact = (LinearLayout) viewM.findViewById(R.id.fans_contact);
            LinearLayout wxContact = (LinearLayout) viewM.findViewById(R.id.wx_contact);
            TextView cancel = (TextView) viewM.findViewById(R.id.cancel_contact);

            allContact.setOnClickListener((v) -> {
                requestContactList("");
                mTableContact.setText(ResourceUtils.getString(R.string.all_contact));
                window.dismiss();
            });
            phoneContact.setOnClickListener((v) -> {
                requestContactList(RequestConstant.TECH_ADD);
                mTableContact.setText(ResourceUtils.getString(R.string.phone_contact));
                window.dismiss();
            });
            fansContact.setOnClickListener((v) -> {
                requestContactList(RequestConstant.FANS_USER);
                mTableContact.setText(ResourceUtils.getString(R.string.fans_contact));
                window.dismiss();
            });
            wxContact.setOnClickListener((v) -> {
                requestContactList(RequestConstant.WX_USER);
                mTableContact.setText(ResourceUtils.getString(R.string.wx_contact));
                window.dismiss();
            });
            cancel.setOnClickListener((v) -> {
                if (window != null) {
                    window.dismiss();
                }
            });
        }
        try {
            if (window != null) {
                window.showAtLocation(view.findViewById(R.id.search_contact), Gravity.BOTTOM, 0, 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void requestContactList(String type) {
        params.clear();
        params.put(RequestConstant.KEY_CUSTOMER_TYPE, type);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_CUSTOMER_LIST, params);
    }
}
