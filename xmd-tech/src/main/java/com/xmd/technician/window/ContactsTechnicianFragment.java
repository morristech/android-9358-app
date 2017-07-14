package com.xmd.technician.window;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.example.xmd_m_comment.httprequest.ConstantResources;
import com.xmd.technician.Adapter.ExpandableClubEmployeeListViewAdapter;
import com.xmd.technician.Constant;
import com.xmd.technician.R;
import com.xmd.technician.bean.ClubRoleBean;
import com.xmd.technician.bean.ClubUserListBean;
import com.xmd.technician.common.CharacterParser;
import com.xmd.technician.common.UINavigation;
import com.xmd.technician.common.Utils;
import com.xmd.technician.http.RequestConstant;
import com.xmd.technician.http.gson.ClubEmployeeListResult;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;
import com.xmd.technician.widget.EmptyView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscription;

/**
 * Created by Lhj on 17-5-27.
 */

public class ContactsTechnicianFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.technician_empty_view)
    EmptyView technicianEmptyView;
    @BindView(R.id.technician_expand_list)
    ExpandableListView technicianExpandList;
    @BindView(R.id.swipe_refresh_widget)
    SwipeRefreshLayout swipeRefreshWidget;
    private Subscription mClubEmployeeSubscription;
    private CharacterParser characterParser;
    private List<ClubRoleBean> mCLubRoles; //服务器获取的分组
    private List<List<ClubUserListBean>> mClubUserLists;//服务器获取的各分组
    private List<ClubRoleBean> mSearchClubRoles;
    private List<ClubUserListBean> mSearchClubUsers;
    private List<ClubUserListBean> mSearchClubUser;
    private List<List<ClubUserListBean>> mSearchClubUserLists;
    private Map<String, ClubRoleBean> mapList;
    private ExpandableClubEmployeeListViewAdapter clubEmployeeAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts_technician, container, false);
        ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        mCLubRoles = new ArrayList<>();
        mClubUserLists = new ArrayList<>();
        mapList = new HashMap<>();
        characterParser = CharacterParser.getInstance();
        swipeRefreshWidget.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshWidget.setOnRefreshListener(this);
        clubEmployeeAdapter = new ExpandableClubEmployeeListViewAdapter(getActivity());
        technicianExpandList.setAdapter(clubEmployeeAdapter);
        technicianExpandList.setDivider(null);
        clubEmployeeAdapter.setChildrenClickedInterface(new ExpandableClubEmployeeListViewAdapter.OnChildrenClicked() {
            @Override
            public void onChildrenClickedListener(ClubUserListBean bean) {
//                Intent intent = new Intent(getActivity(), ContactInformationDetailActivity.class);
//                intent.putExtra(RequestConstant.KEY_CUSTOMER_ID, bean.id);
//                intent.putExtra(RequestConstant.KEY_IS_MY_CUSTOMER, false);
//                if (bean.userType.equals(Constant.CLUB_EMPLOYEE_TYPE_MANAGER)) {
//                    intent.putExtra(RequestConstant.KEY_CONTACT_TYPE, Constant.CONTACT_INFO_DETAIL_TYPE_MANAGER);
//                } else {
//                    intent.putExtra(RequestConstant.KEY_CONTACT_TYPE, Constant.CONTACT_INFO_DETAIL_TYPE_TECH);
//                }
//                startActivity(intent);
                UINavigation.gotoCustomerDetailActivity(getActivity(),bean.id, ConstantResources.INTENT_TYPE_TECH,true);
            }
        });
        technicianExpandList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                if (parent.isGroupExpanded(groupPosition)) {
                    parent.collapseGroup(groupPosition);
                } else {
                    parent.expandGroup(groupPosition, false);
                }
                return true;
            }
        });
        mClubEmployeeSubscription = RxBus.getInstance().toObservable(ClubEmployeeListResult.class).subscribe(
                result -> handlerClubEmployeeList(result)
        );
        onRefresh();

    }

    private void handlerClubEmployeeList(ClubEmployeeListResult result) {
        swipeRefreshWidget.setRefreshing(false);
        if (result.statusCode == 200) {
            getClubRoleList(result);
            getClubUserLists(result.respData.userList);
            clubEmployeeAdapter.setData(mCLubRoles, mClubUserLists);
            for (int i = 0; i < mCLubRoles.size(); i++) {
                technicianExpandList.expandGroup(i, false);
            }
            technicianEmptyView.setStatus(EmptyView.Status.Gone);
        } else {
            technicianEmptyView.setStatus(EmptyView.Status.Gone);
        }
    }

    private List<ClubRoleBean> getClubRoleList(ClubEmployeeListResult result) {
        mCLubRoles.clear();
        mCLubRoles.addAll(result.respData.roleList);
        //通过Id排序
        Collections.sort(mCLubRoles, (lhs, rhs) -> {
            if (lhs.id > rhs.id) {
                return 1;
            } else {
                return -1;
            }
        });
        mCLubRoles.add(new ClubRoleBean(Constant.CLUB_EMPLOYEE_DEFAULT_GROUP_ID, Constant.CLUB_EMPLOYEE_DEFAULT_GROUP, Constant.CLUB_EMPLOYEE_HAS_NONE_GROUP));
        for (ClubRoleBean roleBean : mCLubRoles) {
            roleBean.mUserListBeans = new ArrayList<>();
            mapList.put(roleBean.code, roleBean);
        }
        return mCLubRoles;
    }


    private List<List<ClubUserListBean>> getClubUserLists(List<ClubUserListBean> roles) {
        mClubUserLists.clear();
        for (ClubUserListBean roleBean : roles) {
            if (Utils.isNotEmpty(roleBean.roles)) {
                String[] rolesArr = roleBean.roles.split(",");
                for (int i = 0; i < rolesArr.length; i++) {
                    if ((mapList.keySet().toString()).contains(rolesArr[i].toString())) {
                        mapList.get(rolesArr[i]).mUserListBeans.add(roleBean);
                    }
                }
            } else {
                mapList.get(Constant.CLUB_EMPLOYEE_HAS_NONE_GROUP).mUserListBeans.add(roleBean);
            }

        }
        for (ClubRoleBean cLubRole : mCLubRoles) {
            mClubUserLists.add(cLubRole.mUserListBeans);
        }
        return mClubUserLists;
    }

    public void filterCustomer(String searchName) {
        if (Utils.isNotEmpty(searchName)) {
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
                    if ((mClubUserLists.get(i).get(j).name.indexOf(searchName.toString())) != -1 || characterParser.getSelling(mClubUserLists.get(i).get(j).name).startsWith(searchName.toString())) {
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
                    technicianExpandList.expandGroup(i, false);
                }
            } else {
                Utils.makeShortToast(getActivity(), "未查到相关联系人");

            }

        } else {
            clubEmployeeAdapter.setData(mCLubRoles, mClubUserLists);
            for (int i = 0; i < mCLubRoles.size(); i++) {
                technicianExpandList.expandGroup(i, false);
            }
        }
    }

    @Override
    public void onRefresh() {
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_CLUB_EMPLOYEE_LIST);
        technicianEmptyView.setStatus(EmptyView.Status.Loading);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unsubscribe(mClubEmployeeSubscription);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }


}
