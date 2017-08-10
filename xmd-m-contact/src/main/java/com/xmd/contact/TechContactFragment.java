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
import com.xmd.contact.event.SwitchTableToContactRecentEvent;
import com.xmd.contact.httprequest.ConstantResources;
import com.xmd.contact.httprequest.DataManager;
import com.xmd.m.network.NetworkSubscriber;
import com.xmd.permission.CheckBusinessPermission;
import com.xmd.permission.PermissionConstants;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Lhj on 17-7-26.
 */

public class TechContactFragment extends BaseFragment {

    @BindView(R2.id.edit_search_contact)
    ClearableEditText editSearchContact;
    @BindView(R2.id.tv_customer_all)
    TextView tvCustomerAll;
    @BindView(R2.id.tv_customer_register)
    TextView tvCustomerRegister;
    @BindView(R2.id.tv_customer_visitor)
    TextView tvCustomerVisitor;
    @BindView(R2.id.tv_customer_technician)
    TextView tvCustomerTechnician;
    ImageView imgScreenContact;


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
        view = inflater.inflate(R.layout.fragment_tech_contact, container, false);
        ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);
        imgScreenContact = (ImageView) view.findViewById(R.id.tech_img_screen_contact);
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
        tableViewAddRegister();
        tableViewsAddVisitor();
        tableViewsAddTechnician();
        mHelp = TagManagerHelp.getInstance();
        mFragmentController = FragmentController.getInstance(this, R.id.contact_fragment_view, false);
        changeViewState(ConstantResources.CONTACT_ALL_INDEX);
        editSearchContact.setCleanTextListener(new ClearableEditText.CleanTextListener() {
            @Override
            public void cleanText() {
                filterOrSearchCustomer("");
            }
        });
    }

    //全部客户
    @CheckBusinessPermission(PermissionConstants.CONTACTS_CUSTOMER)
    public void tableViewsAddCustomer() {
        tableViews.add(tvCustomerAll);
        tvCustomerAll.setVisibility(View.VISIBLE);
    }

    //我的拓客
    @CheckBusinessPermission(PermissionConstants.CONTACTS_MY_CUSTOMER)
    public void tableViewAddRegister() {
        tableViews.add(tvCustomerRegister);
        tvCustomerRegister.setVisibility(View.VISIBLE);
    }

    //最近访客
    @CheckBusinessPermission(PermissionConstants.CONTACTS_VISITOR)
    public void tableViewsAddVisitor() {
        tableViews.add(tvCustomerVisitor);
        tvCustomerVisitor.setVisibility(View.VISIBLE);
    }

    //本店
    @CheckBusinessPermission(PermissionConstants.CONTACTS_MY_CLUB)
    public void tableViewsAddTechnician() {
        tableViews.add(tvCustomerTechnician);
        tvCustomerTechnician.setVisibility(View.VISIBLE);
    }

    //首页点击了谁看了我
    @Subscribe
    public void switchTableToRecent(SwitchTableToContactRecentEvent event) {
        if (mFragmentController.getFragment(2) != null) {
            mFragmentController.showFragment(2);
            changeViewState(ConstantResources.CONTACT_VISITOR_INDEX);
        }
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
            case ConstantResources.CONTACT_ALL_INDEX:
                ((ContactsAllFragment) (getChildFragmentManager().getFragments().get(mCurrentFragmentIndex))).filterOrSearchCustomer(searchText, mTagName, mUserGroup, mCustomerLevel, mCustomerType, mSerialNo);
                break;
            case ConstantResources.CONTACT_REGISTER_INDEX:
                  ((ContactsRegisterFragment) (getChildFragmentManager().getFragments().get(mCurrentFragmentIndex))).filterOrSearchCustomer(searchText, mTagName, mUserGroup, mCustomerLevel, mCustomerType, mSerialNo);
                break;
            case ConstantResources.CONTACT_VISITOR_INDEX:
                ((ContactsVisitorsFragment) (getChildFragmentManager().getFragments().get(mCurrentFragmentIndex))).filterOrSearchCustomer(searchText);
                break;
            case ConstantResources.CONTACT_CLUB_INDEX:
                ((ContactsTechnicianFragment) (getChildFragmentManager().getFragments().get(mCurrentFragmentIndex))).filterOrSearchCustomer(searchText);
                break;
        }
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

    @OnClick(R2.id.tech_img_screen_contact)
    public void onImgScreenContactClicked() {
        if (contactFilter == null) {
            initPopupWindow();
            contactFilter.show();
        } else {
            contactFilter.show();
        }
    }

    @OnClick(R2.id.tv_customer_all)
    public void onTvCustomerAllClicked() {
        changeViewState(ConstantResources.CONTACT_ALL_INDEX);
    }

    @OnClick(R2.id.tv_customer_register)
    public void onTvCustomerRegisterClicked() {
        changeViewState(ConstantResources.CONTACT_REGISTER_INDEX);
    }

    @OnClick(R2.id.tv_customer_visitor)
    public void onTvCustomerVisitorClicked() {
        changeViewState(ConstantResources.CONTACT_VISITOR_INDEX);
    }

    @OnClick(R2.id.tv_customer_technician)
    public void onTvCustomerTechnicianClicked() {
        changeViewState(ConstantResources.CONTACT_CLUB_INDEX);
    }

    private void changeViewState(int index) {
        for (View v : tableViews) {
            v.setSelected(false);
        }
        switch (index) {
            case ConstantResources.CONTACT_ALL_INDEX:
                tvCustomerAll.setSelected(true);
                mFragmentController.showFragment(ConstantResources.CONTACT_ALL_INDEX);
                mCurrentFragmentIndex = ConstantResources.CONTACT_ALL_INDEX;
                break;
            case ConstantResources.CONTACT_REGISTER_INDEX:
                tvCustomerRegister.setSelected(true);
                mFragmentController.showFragment(ConstantResources.CONTACT_REGISTER_INDEX);
                mCurrentFragmentIndex = ConstantResources.CONTACT_REGISTER_INDEX;
                break;
            case ConstantResources.CONTACT_VISITOR_INDEX:
                tvCustomerVisitor.setSelected(true);
                mFragmentController.showFragment(ConstantResources.CONTACT_VISITOR_INDEX);
                mCurrentFragmentIndex = ConstantResources.CONTACT_VISITOR_INDEX;
                break;
            case ConstantResources.CONTACT_CLUB_INDEX:
                tvCustomerTechnician.setSelected(true);
                mFragmentController.showFragment(ConstantResources.CONTACT_CLUB_INDEX);
                mCurrentFragmentIndex = ConstantResources.CONTACT_CLUB_INDEX;
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


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFragmentController != null) {
            FragmentController.destroyController();
        }
        EventBus.getDefault().unregister(this);
    }
}
