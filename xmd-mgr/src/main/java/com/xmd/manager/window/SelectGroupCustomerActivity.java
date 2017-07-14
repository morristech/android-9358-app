package com.xmd.manager.window;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.xmd.manager.R;
import com.xmd.manager.adapter.SelectedCustomerAdapter;
import com.xmd.manager.adapter.SelectedMemberAdapter;
import com.xmd.manager.beans.GroupMemberBean;
import com.xmd.manager.common.ResourceUtils;
import com.xmd.manager.common.Utils;
import com.xmd.manager.msgctrl.MsgDef;
import com.xmd.manager.msgctrl.MsgDispatcher;
import com.xmd.manager.msgctrl.RxBus;
import com.xmd.manager.service.RequestConstant;
import com.xmd.manager.service.response.GroupUserListResult;
import com.xmd.manager.widget.AlertDialogBuilder;
import com.xmd.manager.widget.ClearableEditText;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

/**
 * Created by lhj on 2016/12/5.
 */

public class SelectGroupCustomerActivity extends BaseActivity {


    @BindView(R.id.search_customer)
    ClearableEditText mSearchCustomer;
    @BindView(R.id.group_customer_selected)
    RecyclerView groupCustomerSelected;
    @BindView(R.id.selected_customer)
    LinearLayout mSelectedCustomer;
    @BindView(R.id.list_customer)
    RecyclerView mListCustomer;
    @BindView(R.id.title_layout_no_friends)
    TextView mTitleLayoutNoFriends;
    @BindView(R.id.status_progressbar)
    ProgressBar mStatusProgressbar;
    @BindView(R.id.btn_search)
    ImageView mBtnSearch;


    public static final String SELECT_CUSTOMER_RESULT = "select_result";
    public static final String HAD_SELECTED_CUSTOMER = "had_select";

    private static final int PAGE_START = 1;
    private static final String PAGE_SIZE = "20";

    protected boolean mIsLoadingMore = false;
    protected int mLastVisibleItem;
    protected int mPageCount = -1;

    private Subscription mGetMemberListSubscription;
    private SelectedMemberAdapter selectedMemberAdapter;
    private SelectedCustomerAdapter selectedAdapter;
    private List<GroupMemberBean> mSelectedMemberList;
    private List<GroupMemberBean> mMemberList;
    private LinearLayoutManager mLayoutManager;
    private int mPages = PAGE_START;
    private String selectedIds = "";
    private String searchText;
    private Boolean isClearBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selet_group_customer);
        ButterKnife.bind(this);
        searchText = "";
        initView();
    }

    private void initView() {
        setTitle(ResourceUtils.getString(R.string.activity_select_member));
        if (Utils.isNotEmpty(getIntent().getStringExtra(HAD_SELECTED_CUSTOMER))) {
            selectedIds = getIntent().getStringExtra(HAD_SELECTED_CUSTOMER);
        }
        mSearchCustomer.setClearEditTextListener(new ClearableEditText.ClearEditText() {
            @Override
            public void clearListener() {
                isClearBtn = true;
                mMemberList.clear();
                mPages = 1;
                searchText = "";
                getData();
            }
        });
        mSearchCustomer.addTextChangedListener(searchCustomerWatch);
        setLeftVisible(true, -1, leftClick);

        mGetMemberListSubscription = RxBus.getInstance().toObservable(GroupUserListResult.class).subscribe(
                userList -> handlerUserList(userList)
        );
        mMemberList = new ArrayList<>();
        mSelectedMemberList = new ArrayList<>();
        getData();
        mStatusProgressbar.setVisibility(View.GONE);
        mListCustomer.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mListCustomer.setLayoutManager(mLayoutManager);
        mListCustomer.setItemAnimator(new DefaultItemAnimator());
        selectedAdapter = new SelectedCustomerAdapter(this);
        groupCustomerSelected.setItemAnimator(new DefaultItemAnimator());
        groupCustomerSelected.setHasFixedSize(true);
        groupCustomerSelected.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        groupCustomerSelected.setAdapter(selectedAdapter);
        selectedMemberAdapter = new SelectedMemberAdapter(this, mMemberList, new SelectedMemberAdapter.ItemClickedInterface() {
            @Override
            public void onItemClicked(GroupMemberBean bean, Integer position, boolean isChecked) {
                if (isChecked) {
                    for (int i = 0; i < mSelectedMemberList.size(); i++) {
                        if (mSelectedMemberList.get(i).id.equals(bean.id)) {
                            mSelectedMemberList.remove(mSelectedMemberList.get(i));
                            break;
                        }
                    }
                    mMemberList.set(position, new GroupMemberBean(bean.id, bean.name, bean.telephone, bean.avatar, bean.badCommentCount, bean.userType,
                            bean.avatarUrl, 3));
                } else {
                    mMemberList.set(position, new GroupMemberBean(bean.id, bean.name, bean.telephone, bean.avatar, bean.badCommentCount, bean.userType,
                            bean.avatarUrl, 2));
                    mSelectedMemberList.add(mMemberList.get(position));
                }
                selectedMemberAdapter.notifyItemChanged(position);
                if (mSelectedMemberList.size() > 0) {
                    selectedAdapter.setCustomersData(mSelectedMemberList);
                    setRightVisible(true, String.format("确定(%s)", mSelectedMemberList.size()), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            saveDataAndBack();
                        }
                    });
                    groupCustomerSelected.scrollToPosition(mSelectedMemberList.size() - 1);
                } else {
                    mSelectedMemberList.clear();
                    selectedAdapter.setCustomersData(mSelectedMemberList);
                    setRightVisible(false, "", null);

                }
            }
        });

        mListCustomer.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && mLastVisibleItem + 1 == selectedMemberAdapter.getItemCount()) {
                    loadMore();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mLastVisibleItem = mLayoutManager.findLastVisibleItemPosition();
            }
        });
        mListCustomer.setAdapter(selectedMemberAdapter);
        Utils.hideKeyboard(SelectGroupCustomerActivity.this);
    }

    @OnClick(R.id.btn_search)
    public void searchCustomer() {
        if (Utils.isNotEmpty(mSearchCustomer.getText().toString().trim())) {
            mMemberList.clear();
            mPages = 1;
            searchText = mSearchCustomer.getText().toString().trim();
            getData();
        } else {
            makeShortToast(ResourceUtils.getString(R.string.customer_search_alter));
        }

    }

    private void handlerUserList(GroupUserListResult userList) {
        if (userList.statusCode == 200) {
            if (Utils.isNotEmpty(selectedIds)) {
                for (int i = 0; i < userList.respData.size(); i++) {
                    if (selectedIds.contains(userList.respData.get(i).id)) {
                        userList.respData.set(i, new GroupMemberBean(userList.respData.get(i).id, userList.respData.get(i).name, userList.respData.get(i).telephone, userList.respData.get(i).avatar
                                , userList.respData.get(i).badCommentCount, userList.respData.get(i).userType, userList.respData.get(i).avatarUrl, 1));
                    }
                }
            }
            for (int i = 0; i < userList.respData.size(); i++) {
                for (int j = 0; j < mSelectedMemberList.size(); j++) {
                    if (mSelectedMemberList.get(j).id.equals(userList.respData.get(i).id)) {
                        userList.respData.set(i, new GroupMemberBean(userList.respData.get(i).id, userList.respData.get(i).name, userList.respData.get(i).telephone, userList.respData.get(i).avatar
                                , userList.respData.get(i).badCommentCount, userList.respData.get(i).userType, userList.respData.get(i).avatarUrl, 2));
                    }
                }
            }

            mPageCount = userList.pageCount;
            if (userList != null) {
                if (!mIsLoadingMore) {
                    mMemberList.clear();
                }
                mMemberList.addAll(userList.respData);
                selectedMemberAdapter.setIsNoMore(mPages == mPageCount);
                selectedMemberAdapter.setData(mMemberList);
            }
        } else {
            makeShortToast(userList.msg);
        }


    }

    private void getData() {
        isClearBtn = false;
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_GROUP_USER_NAME, searchText);
        params.put(RequestConstant.KEY_PAGE, String.valueOf(mPages));
        params.put(RequestConstant.KEY_PAGE_SIZE, PAGE_SIZE);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_GROUP_USER_LIST, params);
    }

    private void loadMore() {
        if (getListSafe()) {
            mIsLoadingMore = true;
        }
    }

    private boolean getListSafe() {
        if (mPageCount < 0 || mPages + 1 <= mPageCount) {
            mPages++;
            getData();
            return true;
        }
        return false;
    }

    private void saveDataAndBack() {
        Intent intent = new Intent();
        intent.putExtra(SELECT_CUSTOMER_RESULT, (Serializable) mSelectedMemberList);
        setResult(RESULT_OK, intent);
        this.finish();
    }

    View.OnClickListener leftClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mSelectedMemberList.size() > 0) {
                new AlertDialogBuilder(SelectGroupCustomerActivity.this).setTitle("温馨提示").setMessage("您添加了顾客尚未进行保存").setCancelable(true).setNegativeButton("离开", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                }).setPositiveButton("保存", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        saveDataAndBack();
                    }
                }).show();
            } else {
                finish();
            }
        }
    };
    TextWatcher searchCustomerWatch = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (mSearchCustomer.getText().toString().length() == 0) {
                if (!isClearBtn) {
                    mMemberList.clear();
                    mPages = 1;
                    searchText = "";
                    getData();
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unsubscribe(mGetMemberListSubscription);
    }
}
