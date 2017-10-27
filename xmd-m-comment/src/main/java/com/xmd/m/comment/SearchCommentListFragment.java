package com.xmd.m.comment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.util.TimeZone;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.crazyman.library.PermissionTool;
import com.shidou.commonlibrary.helper.XLogger;
import com.shidou.commonlibrary.util.DateUtils;
import com.shidou.commonlibrary.widget.XToast;
import com.xmd.app.BaseFragment;
import com.xmd.app.XmdApp;
import com.xmd.m.R;
import com.xmd.m.R2;
import com.xmd.m.comment.adapter.ListRecycleViewAdapter;
import com.xmd.m.comment.bean.CommentBean;
import com.xmd.m.comment.bean.CommentListResult;
import com.xmd.m.comment.bean.CommentStatusResult;
import com.xmd.m.comment.bean.UserInfoBean;
import com.xmd.m.comment.event.UserInfoEvent;
import com.xmd.m.comment.httprequest.ConstantResources;
import com.xmd.m.comment.httprequest.DataManager;
import com.xmd.m.comment.httprequest.RequestConstant;
import com.xmd.m.network.NetworkSubscriber;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.internal.operators.OnSubscribeFromIterable;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Lhj on 17-6-30.
 */

public class SearchCommentListFragment extends BaseFragment implements ListRecycleViewAdapter.Callback<CommentBean>, SwipeRefreshLayout.OnRefreshListener {


    @BindView(R2.id.list_view)
    RecyclerView mListView;
    @BindView(R2.id.swipe_refresh_widget)
    SwipeRefreshLayout mSwipeRefreshLayout;
    Unbinder unbinder;

    private static final int REQUEST_CODE_CALL_PERMISSION = 0001;

    private static final int PAGE_START = 1;
    private static final int PAGE_SIZE = 20;
    private LinearLayoutManager mLayoutManager;
    private ListRecycleViewAdapter mListAdapter;
    private int mPages;
    private boolean mIsLoadingMore = false;
    private int mLastVisibleItem;
    private int mPageCount = -1;
    private List<CommentBean> mCommentList = new ArrayList<>();
    private Map<String, String> mParams;
    private String mStartDate;
    private String mEndDate;
    private String mTechId;//techId 字符串
    private String mType; //1:差评，２:中评，３:好评,4:好评,中评
    private String mUserName;
    private String mCommentType;
    private String mUserId;
    private String contactPhone;
    private boolean isFromManager;
    private String techId;
    private String searchPhone;
    private String userId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comment_list, container, false);
        unbinder = ButterKnife.bind(this, view);
        initView();
        resetData();
        requestData();
        return view;
    }

    private void initView() {
        isFromManager = getArguments().getBoolean(ConstantResources.INTENT_TYPE, false);
        techId = getArguments().getString(ConstantResources.INTENT_TECH_ID);
        searchPhone = getArguments().getString(ConstantResources.KEY_SEARCH_TELEPHONE);
        userId = getArguments().getString(ConstantResources.KEY_USER_ID);
        mCommentList = new ArrayList<>();
        initListLayout();
        mParams = new HashMap<>();

    }

    private void resetData() {
        mPages = PAGE_START;
        mStartDate = "2015-01-01";
        mEndDate = DateUtils.getCurrentDate();
        if (TextUtils.isEmpty(techId)) {
            mTechId = "";
        } else {
            mTechId = techId;
        }
        if (TextUtils.isEmpty(searchPhone)) {
            mUserName = "";
        } else {
            mUserName = searchPhone;
        }
        if (isFromManager) {
            mCommentType = "";
        } else {
            mCommentType = "comment";
        }
        if (TextUtils.isEmpty(userId)) {
            mUserId = "";
        } else {
            mUserId = userId;
        }
        if (isFromManager) {
            mType = "5";//管理者搜索,不区分好评,差评,中评
        } else {
            mType = "4";//技师搜索,只搜索好评,中评
        }
    }

    private void requestData() {
        dispatchRequest();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    public void dispatchRequest() {
        if (mParams == null) {
            mParams = new HashMap<>();
        } else {
            mParams.clear();
        }
        mParams.put(RequestConstant.KEY_PAGE, String.valueOf(mPages));
        mParams.put(RequestConstant.KEY_PAGE_SIZE, String.valueOf(PAGE_SIZE));
        mParams.put(RequestConstant.KEY_START_DATE, mStartDate);
        mParams.put(RequestConstant.KEY_END_DATE, mEndDate);
        mParams.put(RequestConstant.KEY_TECH_ID, mTechId);
        mParams.put(RequestConstant.KEY_TYPE, mType);
        mParams.put(RequestConstant.KEY_COMMENT_TYPE, mCommentType);
        mParams.put(RequestConstant.KEY_USER_NAME, mUserName);
        mParams.put(RequestConstant.KEY_USER_ID, mUserId);
        if (isFromManager) {
            mParams.put(RequestConstant.KEY_STATUS, "");
        } else {
            mParams.put(RequestConstant.KEY_STATUS, "valid");
        }


        DataManager.getInstance().loadCommentList(mParams, new NetworkSubscriber<CommentListResult>() {
                    @Override
                    public void onCallbackSuccess(CommentListResult result) {

                        onGetListSucceeded(result.getPageCount(), result.getRespData());
                    }

                    @Override
                    public void onCallbackError(Throwable e) {
                        onGetListFailed(e.getLocalizedMessage());
                    }
                }
        );
    }

    private void onGetListSucceeded(int pageCount, List<CommentBean> list) {
        mSwipeRefreshLayout.setRefreshing(false);
        mPageCount = pageCount;

        if (list != null) {
            if (!mIsLoadingMore || pageCount <= -1) {

                mCommentList.clear();
            }
            mCommentList.addAll(list);
            mListAdapter.setIsNoMore(mPages == mPageCount);
            mListAdapter.setData(mCommentList, isFromManager, "5");
        }
    }

    private void onGetListFailed(String errorMsg) {
        mSwipeRefreshLayout.setRefreshing(false);
        if (XmdApp.getInstance().getContext() != null) {
            XToast.show(errorMsg);
        }

    }

    private void loadMore() {
        if (getListSafe()) {
            //上拉刷新，加载更多数据
            mSwipeRefreshLayout.setRefreshing(true);
            mIsLoadingMore = true;
        }
    }

    private boolean getListSafe() {
        if (mPageCount < 0 || mPages + 1 <= mPageCount) {
            mPages++;
            dispatchRequest();
            return true;
        }
        return false;
    }

    private void initListLayout() {

        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mListView.setHasFixedSize(true);
        mListView.setLayoutManager(mLayoutManager);
        mListAdapter = new ListRecycleViewAdapter(getActivity(), mCommentList, this);
        mListView.setAdapter(mListAdapter);
        mListView.setItemAnimator(new DefaultItemAnimator());
        // mListView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        mListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE
                        && mLastVisibleItem + 1 == mListAdapter.getItemCount() && mCommentList.size() > 0) {
                    loadMore();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mLastVisibleItem = mLayoutManager.findLastVisibleItemPosition();
            }
        });
    }

    @Override
    public void onItemClicked(CommentBean bean, String type) {
        // CommentDetailActivity.startCommentDetailActivity(getActivity(), bean, true);
    }

    @Override
    public void onNegativeButtonClicked(CommentBean bean, int position) {
        //删除评论
        changeCommentStatus(bean.id, "", position);
    }

    @Override
    public void onPositiveButtonClicked(CommentBean bean, int position) {
        //回访
        showServiceOutMenu(bean.userId, bean.phoneNum, bean.userEmchatId, bean.userName, bean.avatarUrl, bean.id, bean.returnStatus, position);
    }

    @Override
    public void onLoadMoreButtonClicked() {

    }

    @Override
    public boolean isPaged() {
        return true;
    }

    @Override
    public void onRefresh() {
        dispatchRequest();
    }

    private void showServiceOutMenu(final String userId, String phone, final String emChatId, final String userName, final String userHeadImgUrl, final String commentId, final String returnStatus, final int positon) {

        BottomPopupWindow popupWindow = BottomPopupWindow.getInstance(getActivity(), phone, emChatId, commentId, returnStatus, new BottomPopupWindow.OnRootSelectedListener() {
            @Override
            public void onItemSelected(ReturnVisitMenu rootMenu) {
                switch (rootMenu.getType()) {
                    case 1:
                        PermissionTool.requestPermission(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, new String[]{"拨打电话"}, REQUEST_CODE_CALL_PERMISSION);
                        break;
                    case 2:
                        EventBus.getDefault().post(new UserInfoEvent(0, 1, new UserInfoBean(userId, emChatId, userName, userHeadImgUrl)));
                        break;
                    case 3:
                        changeCommentStatus(commentId, returnStatus, positon);
                        break;
                    case 4:
                        changeCommentStatus(commentId, returnStatus, positon);
                        break;

                }
            }
        });
        popupWindow.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CALL_PERMISSION) {
            if (resultCode == RESULT_OK) {
                toCallPhone();
            } else {
                XToast.show("获取权限失败");
            }
            return;
        }
    }

    public void toCallPhone() {
        Intent intent = new Intent(Intent.ACTION_CALL);
        Uri data = Uri.parse("tel:" + contactPhone);
        intent.setData(data);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        startActivity(intent);
    }

    private void changeCommentStatus(String commentId, final String toStatus, final int position) {
        String status;
        if (toStatus.equals("N")) {
            status = RequestConstant.FINISH_COMMENT;
        } else if (toStatus.equals("Y")) {
            status = RequestConstant.VALID_COMMENT;
        } else {
            status = RequestConstant.DELETE_COMMENT;
        }
        DataManager.getInstance().updateCommentStatus(commentId, status, new NetworkSubscriber<CommentStatusResult>() {
            @Override
            public void onCallbackSuccess(CommentStatusResult result) {

                if (toStatus.equals("N")) {
                    mCommentList.get(position).returnStatus = "Y";
                    mListAdapter.notifyItemChanged(position);
                } else if (toStatus.equals("Y")) {
                    mCommentList.get(position).returnStatus = "N";
                    mListAdapter.notifyItemChanged(position);
                } else {
                    mCommentList.get(position).status = "delete";
                    mListAdapter.notifyItemChanged(position);
                }

            }

            @Override
            public void onCallbackError(Throwable e) {
            }
        });
    }


    public void searchCustomer(String message) {
        resetData();
        mUserName = message;
        dispatchRequest();
    }
}
