package com.xmd.contact;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.shidou.commonlibrary.widget.XToast;
import com.xmd.app.BaseFragment;
import com.xmd.app.CharacterParser;
import com.xmd.contact.adapter.ExpandableClubEmployeeListViewAdapter;
import com.xmd.contact.bean.ClubEmployeeListResult;
import com.xmd.contact.bean.ClubRoleBean;
import com.xmd.contact.bean.ClubUserListBean;
import com.xmd.contact.httprequest.ConstantResources;
import com.xmd.contact.httprequest.DataManager;
import com.xmd.m.comment.CustomerInfoDetailActivity;
import com.xmd.m.network.NetworkSubscriber;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Lhj on 17-7-26.
 */

public class ContactsTechnicianFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, ExpandableClubEmployeeListViewAdapter.OnChildrenClicked, ExpandableListView.OnGroupClickListener {

    private ExpandableListView mTechnicianExpandList;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private View view;
    private CharacterParser characterParser;
    private List<ClubRoleBean> mCLubRoles; //服务器获取的分组
    private List<List<ClubUserListBean>> mClubUserLists;//服务器获取的各分组
    private List<ClubRoleBean> mSearchClubRoles;
    private List<ClubUserListBean> mSearchClubUsers;
    private List<ClubUserListBean> mSearchClubUser;
    private List<List<ClubUserListBean>> mSearchClubUserLists;
    private Map<String, ClubRoleBean> mapList;
    private ExpandableClubEmployeeListViewAdapter clubEmployeeAdapter;
    private boolean isFromManager;


    public ContactsTechnicianFragment(){

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_contact_technician, container, false);
        mTechnicianExpandList = (ExpandableListView) view.findViewById(R.id.technician_expand_list);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_widget);
        isFromManager = getArguments().getBoolean(ConstantResources.APP_TYPE_IS_MANAGER,false);
        initView();
        return view;
    }

    private void initView() {
        mCLubRoles = new ArrayList<>();
        mClubUserLists = new ArrayList<>();
        mapList = new HashMap<>();
        characterParser = characterParser.getInstance();
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        clubEmployeeAdapter = new ExpandableClubEmployeeListViewAdapter(getActivity());
        mTechnicianExpandList.setAdapter(clubEmployeeAdapter);
        mTechnicianExpandList.setDivider(null);
        clubEmployeeAdapter.setChildrenClickedInterface(this);
        mTechnicianExpandList.setOnGroupClickListener(this);
        getClubEmployeeData();
    }

    public void filterOrSearchCustomer(String searchText) {
        if (TextUtils.isEmpty(searchText)) {
            clubEmployeeAdapter.setData(mCLubRoles, mClubUserLists);
            for (int i = 0; i < mCLubRoles.size(); i++) {
                mTechnicianExpandList.expandGroup(i, false);
            }
        } else {
            if (mSearchClubRoles == null) {
                mSearchClubRoles = new ArrayList<>();
            } else {
                mSearchClubRoles.clear();
            }
            if (mSearchClubUserLists == null) {
                mSearchClubUserLists = new ArrayList<>();
            } else {
                mSearchClubUserLists.clear();
            }
            if (mSearchClubUsers == null) {
                mSearchClubUsers = new ArrayList<>();
            } else {
                mSearchClubUsers.clear();
            }
            for (int i = 0; i < mCLubRoles.size(); i++) {
                mSearchClubUsers.clear();
                for (int j = 0; j < mClubUserLists.get(i).size(); j++) {
                    if ((mClubUserLists.get(i).get(j).name.indexOf(searchText.toString())) != -1 || characterParser.getSelling(mClubUserLists.get(i).get(j).name).startsWith(searchText.toString())) {
                        mSearchClubUsers.add(mClubUserLists.get(i).get(j));
                    }
                }
                if (mSearchClubUsers.size() > 0) {
                    mSearchClubUser = new ArrayList<>();
                    mSearchClubUser.addAll(mSearchClubUsers);
                    mSearchClubRoles.add(new ClubRoleBean(mCLubRoles.get(i).id, mCLubRoles.get(i).name, mCLubRoles.get(i).code));
                    mSearchClubUserLists.add(mSearchClubUser);
                }

            }
            if (mSearchClubRoles.size() > 0) {
                clubEmployeeAdapter.setData(mSearchClubRoles, mSearchClubUserLists);
                for (int i = 0; i < mSearchClubRoles.size(); i++) {
                    mTechnicianExpandList.expandGroup(i, false);
                }
            } else {
                XToast.show("未查到相关联系");
            }
        }
    }


    @Override
    public void onRefresh() {
        getClubEmployeeData();
    }

    private void getClubEmployeeData() {
        DataManager.getInstance().loadClubEmployeeList(new NetworkSubscriber<ClubEmployeeListResult>() {
            @Override
            public void onCallbackSuccess(ClubEmployeeListResult result) {
                handlerClubEmployeeList(result);
            }

            @Override
            public void onCallbackError(Throwable e) {

            }
        });
    }

    private void handlerClubEmployeeList(ClubEmployeeListResult result) {
        mSwipeRefreshLayout.setRefreshing(false);
        getClubRoleList(result);
        getClubUserLists(result.getRespData().userList);
        clubEmployeeAdapter.setData(mCLubRoles, mClubUserLists);
        for (int i = 0; i < mCLubRoles.size(); i++) {
            mTechnicianExpandList.expandGroup(i, false);
        }
    }

    private List<ClubRoleBean> getClubRoleList(ClubEmployeeListResult result) {
        mCLubRoles.clear();
        mCLubRoles.addAll(result.getRespData().roleList);
        Collections.sort(mCLubRoles, new Comparator<ClubRoleBean>() {
            @Override
            public int compare(ClubRoleBean o1, ClubRoleBean o2) {
                if (o1.id > o2.id) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });
        mCLubRoles.add(new ClubRoleBean(ConstantResources.CLUB_EMPLOYEE_DEFAULT_GROUP_ID, ConstantResources.CLUB_EMPLOYEE_DEFAULT_GROUP, ConstantResources.CLUB_EMPLOYEE_HAS_NONE_GROUP));
        for (ClubRoleBean roleBean : mCLubRoles) {
            roleBean.mUserListBeans = new ArrayList<>();
            mapList.put(roleBean.code, roleBean);
        }
        return mCLubRoles;
    }


    private List<List<ClubUserListBean>> getClubUserLists(List<ClubUserListBean> userList) {
        mClubUserLists.clear();
        for (ClubUserListBean roleBean : userList) {
            if (TextUtils.isEmpty(roleBean.roles)) {
                mapList.get(ConstantResources.CLUB_EMPLOYEE_HAS_NONE_GROUP).mUserListBeans.add(roleBean);
            } else {
                String[] rolesArr = roleBean.roles.split(",");
                for (int i = 0; i < rolesArr.length; i++) {
                    if ((mapList.keySet().toString()).contains(rolesArr[i].toString())) {
                        mapList.get(rolesArr[i]).mUserListBeans.add(roleBean);
                    }
                }
            }
        }
        for (ClubRoleBean cLubRole : mCLubRoles) {
            mClubUserLists.add(cLubRole.mUserListBeans);
        }
        return mClubUserLists;
    }

    @Override
    public void onChildrenClickedListener(ClubUserListBean bean) {
        if (isFromManager) {
            CustomerInfoDetailActivity.StartCustomerInfoDetailActivity(getActivity(), bean.id, ConstantResources.APP_TYPE_MANAGER, true);
        } else {
            CustomerInfoDetailActivity.StartCustomerInfoDetailActivity(getActivity(), bean.id, ConstantResources.APP_TYPE_TECH, true);
        }

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (isFromManager) {
            if(getParentFragment() instanceof  ManagerContactFragment){
                ((ManagerContactFragment) getParentFragment()).showOrHideFilterButton(false);
            }
        } else {
            if(getParentFragment() instanceof TechContactFragment){
                ((TechContactFragment) getParentFragment()).showOrHideFilterButton(false);
            }
        }

    }

    @Override
    public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
        return false;
    }
}
