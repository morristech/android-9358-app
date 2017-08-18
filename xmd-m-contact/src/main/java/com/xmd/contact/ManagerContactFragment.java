package com.xmd.contact;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.shidou.commonlibrary.widget.XToast;
import com.xmd.app.BaseFragment;
import com.xmd.app.widget.ClearableEditText;
import com.xmd.contact.bean.TagListResult;
import com.xmd.contact.bean.TreatedTagList;
import com.xmd.contact.httprequest.ConstantResources;
import com.xmd.contact.httprequest.DataManager;
import com.xmd.m.network.NetworkSubscriber;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Lhj on 17-7-26.
 */

public class ManagerContactFragment extends BaseFragment {

    @BindView(R2.id.edit_search_contact)
    ClearableEditText editSearchContact;
    @BindView(R2.id.tv_customer_all)
    TextView tvCustomerAll;
    @BindView(R2.id.tv_customer_visitor)
    TextView tvCustomerVisitor;
    @BindView(R2.id.tv_customer_technician)
    TextView tvCustomerTechnician;
    @BindView(R2.id.img_screen_contact)
    ImageView imgScreenContact;
    Unbinder unbinder;

    private BottomContactFilterPopupWindow contactFilter;
    private List<View> tableViews;
    private FragmentController mFragmentController;
    private View view;
    private int mCurrentFragmentIndex;
    private String mSearchText = "";
    private TagManagerHelp mHelp;
    private String mTagName;
    private String mUserGroup;
    private String mCustomerLevel;
    private String mCustomerType;
    private String mSerialNo;
    private List<TreatedTagList> mTreatedTagLists;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_manager_contact, container, false);
        unbinder = ButterKnife.bind(this, view);
        initView();
        getAllTags();
        return view;
    }


    private void initView() {
        mTagName = "";
        mUserGroup = "";
        mCustomerLevel = "";
        mCustomerType = "";
        mSerialNo = "";
        tableViews = new ArrayList<>();
        mTreatedTagLists = new ArrayList<>();
        tableViewsAddCustomer();
        tableViewsAddVisitor();
        tableViewsAddTechnician();
        mHelp = TagManagerHelp.getInstance();
        mFragmentController = FragmentController.getInstance(this, R.id.contact_fragment_view, true);
        changeViewState(ConstantResources.CONTACT_CLUB_ALL_INDEX);
        editSearchContact.setCleanTextListener(new ClearableEditText.CleanTextListener() {
            @Override
            public void cleanText() {
                filterOrSearchCustomer("");
            }
        });

    }


    public void tableViewsAddCustomer() {
        tableViews.add(tvCustomerAll);
        tvCustomerAll.setVisibility(View.VISIBLE);
    }

    public void tableViewsAddVisitor() {
        tableViews.add(tvCustomerVisitor);
        tvCustomerVisitor.setVisibility(View.VISIBLE);
    }

    public void tableViewsAddTechnician() {
        tableViews.add(tvCustomerTechnician);
        tvCustomerTechnician.setVisibility(View.VISIBLE);
    }

    private void initPopupWindow() {
        contactFilter = new BottomContactFilterPopupWindow(getActivity(), imgScreenContact, mTreatedTagLists);
        contactFilter.setContactFilterListener(new BottomContactFilterPopupWindow.ContactFilterListener() {
            @Override
            public void contactFilter(String tagName, String userGroup, String customerLevel, String customerType, String serialNo) {
                mTagName = tagName;
                mUserGroup = userGroup;
                mCustomerLevel = customerLevel;
                mCustomerType = customerType;
                mSerialNo = serialNo;
                filterOrSearchCustomer("");
            }
        });
    }

    /**
     * 搜索用户
     *
     * @param searchText
     */
    private void filterOrSearchCustomer(String searchText) {
        switch (mCurrentFragmentIndex) {
            case ConstantResources.CONTACT_CLUB_ALL_INDEX:
                ((ManagerContactsAllFragment) (getChildFragmentManager().getFragments().get(mCurrentFragmentIndex))).filterOrSearchCustomer(searchText, mTagName, mUserGroup, mCustomerLevel, mCustomerType, mSerialNo);
                break;
            case ConstantResources.CONTACT_CLUB_RECENT_INDEX:
                ((ManagerContactsVisitorsFragment) (getChildFragmentManager().getFragments().get(mCurrentFragmentIndex))).filterOrSearchCustomer(searchText);
                break;
            case ConstantResources.CONTACT_CLUB_TECHNICIAN_INDEX:
                ((ContactsTechnicianFragment) (getChildFragmentManager().getFragments().get(mCurrentFragmentIndex))).filterOrSearchCustomer(searchText);
                break;
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R2.id.img_btn_search)
    public void onImgBtnSearchClicked() {
        mSearchText = editSearchContact.getText().toString();
        if (TextUtils.isEmpty(mSearchText)) {
            XToast.show("请输入搜索内容");
            return;
        }
        filterOrSearchCustomer(mSearchText);
    }

    @OnClick(R2.id.img_screen_contact)
    public void onImgScreenContactClicked() {
        if (contactFilter == null) {
            initPopupWindow();
            contactFilter.show();
        } else {
            contactFilter.show();
        }
    }

    private void changeViewState(int index) {
        for (View v : tableViews) {
            v.setSelected(false);
        }
        switch (index) {
            case ConstantResources.CONTACT_CLUB_ALL_INDEX:
                tvCustomerAll.setSelected(true);
                mFragmentController.showFragment(ConstantResources.CONTACT_CLUB_ALL_INDEX);
                mCurrentFragmentIndex = ConstantResources.CONTACT_CLUB_ALL_INDEX;
                break;
            case ConstantResources.CONTACT_CLUB_RECENT_INDEX:
                tvCustomerVisitor.setSelected(true);
                mFragmentController.showFragment(ConstantResources.CONTACT_CLUB_RECENT_INDEX);
                mCurrentFragmentIndex = ConstantResources.CONTACT_CLUB_RECENT_INDEX;
                break;
            case ConstantResources.CONTACT_CLUB_TECHNICIAN_INDEX:
                tvCustomerTechnician.setSelected(true);
                mFragmentController.showFragment(ConstantResources.CONTACT_CLUB_TECHNICIAN_INDEX);
                mCurrentFragmentIndex = ConstantResources.CONTACT_CLUB_TECHNICIAN_INDEX;
                break;
        }

    }

    /**
     * 是否显示过滤按钮
     *
     * @param isShow
     */
    public void showOrHideFilterButton(boolean isShow) {
        if (isShow) {
            imgScreenContact.setVisibility(View.VISIBLE);
        } else {
            imgScreenContact.setVisibility(View.GONE);
        }
    }


    @OnClick(R2.id.tv_customer_all)
    public void onTvCustomerAllClicked() {
        changeViewState(ConstantResources.CONTACT_CLUB_ALL_INDEX);
    }

    @OnClick(R2.id.tv_customer_visitor)
    public void onTvCustomerVisitorClicked() {
        changeViewState(ConstantResources.CONTACT_CLUB_RECENT_INDEX);
    }

    @OnClick(R2.id.tv_customer_technician)
    public void onTvCustomerTechnicianClicked() {
        changeViewState(ConstantResources.CONTACT_CLUB_TECHNICIAN_INDEX);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFragmentController != null) {
            FragmentController.destroyController();
        }
        if (contactFilter != null) {
            contactFilter = null;
        }
        //EventBus.getDefault().unregister(this);
    }

    public void getAllTags() {
        DataManager.getInstance().loadAllTags(new NetworkSubscriber<TagListResult>() {
            @Override
            public void onCallbackSuccess(TagListResult result) {
                mHelp.setData(result.getRespData());
                mTreatedTagLists.clear();
                mTreatedTagLists.addAll(mHelp.getTreatedTagList());
            }

            @Override
            public void onCallbackError(Throwable e) {

            }
        });
    }
}
