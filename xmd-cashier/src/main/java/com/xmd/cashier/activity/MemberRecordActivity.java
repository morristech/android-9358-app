package com.xmd.cashier.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.xmd.cashier.R;
import com.xmd.cashier.adapter.MemberRecordAdapter;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.contract.MemberRecordContract;
import com.xmd.cashier.dal.bean.MemberRecordInfo;
import com.xmd.cashier.presenter.MemberRecordPresenter;
import com.xmd.cashier.widget.ArrayPopupWindow;
import com.xmd.cashier.widget.CustomLoadingLayout;
import com.xmd.cashier.widget.CustomRecycleViewDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zr on 17-7-11.
 * 会员账户记录
 */

public class MemberRecordActivity extends BaseActivity implements MemberRecordContract.View {
    private MemberRecordContract.Presenter mPresenter;
    private MemberRecordAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private ArrayPopupWindow<String> mPopFilter;

    private EditText mSearchInput;
    private TextView mFilterText;
    private SwipeRefreshLayout mRefreshLayout;
    private CustomLoadingLayout mLoadingLayout;
    private RecyclerView mRecyclerList;

    private int mLastVisibleItem;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_record);
        mPresenter = new MemberRecordPresenter(this, this);
        initView();
        mPresenter.onCreate();
        onInit();
    }

    private void onInit() {
        mRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mRefreshLayout.setRefreshing(true);
                mPresenter.load(true);
            }
        });
    }

    private void initView() {
        showToolbar(R.id.toolbar, "账户记录");
        mSearchInput = (EditText) findViewById(R.id.et_member_record_search);  //搜索
        mFilterText = (TextView) findViewById(R.id.tv_type_filter); //筛选
        mFilterText.setText(AppConstants.STATUS_ALL_TEXT);
        mPopFilter = new ArrayPopupWindow<>(this, mFilterText, null,
                getWindowManager().getDefaultDisplay().getWidth() / 3,
                R.style.anim_top_to_bottom_style,
                getResources().getDrawable(R.drawable.bg_ex_popup_window), 0);
        mPopFilter.setDataSet(new ArrayList<>(AppConstants.MEMBER_RECORD_STATUS_FILTER.keySet()));   //下拉列表

        mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.ly_member_record_refresh);   //刷新
        mLoadingLayout = (CustomLoadingLayout) findViewById(R.id.ly_member_load_status);   //加载层
        mRecyclerList = (RecyclerView) findViewById(R.id.rv_member_record_list); //列表
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new MemberRecordAdapter(this);

        mRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        mRecyclerList.setHasFixedSize(true);
        mRecyclerList.setLayoutManager(mLayoutManager);
        mRecyclerList.setItemAnimator(new DefaultItemAnimator());
        mRecyclerList.addItemDecoration(new CustomRecycleViewDecoration(15));
        mRecyclerList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && mLastVisibleItem + 1 == mAdapter.getItemCount()) {
                    mPresenter.loadMore();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mLastVisibleItem = mLayoutManager.findLastVisibleItemPosition();
            }
        });

        mAdapter.setCallBack(new MemberRecordAdapter.CallBack() {
            @Override
            public void onLoadMore() {
                mPresenter.loadMore();
            }

            @Override
            public void onPrint(MemberRecordInfo info) {
                mPresenter.print(info, true);
            }
        });
        mRecyclerList.setAdapter(mAdapter);

        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSearchInput.setText(null);
                mPresenter.setSearch(null);
                mFilterText.setText(AppConstants.STATUS_ALL_TEXT);
                mPresenter.setFilter(AppConstants.MEMBER_RECORD_STATUS_FILTER.get(AppConstants.STATUS_ALL_TEXT));
                mPresenter.load(false);
            }
        });

        mLoadingLayout.setOnReloadListener(new CustomLoadingLayout.OnReloadListener() {
            @Override
            public void onReload(View view) {
                mPresenter.load(false);
            }
        });

        mSearchInput.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                boolean flag;
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_UP:
                        Drawable drawableRight = mSearchInput.getCompoundDrawables()[2];
                        if (drawableRight != null && event.getX() > (mSearchInput.getWidth() - drawableRight.getBounds().width() * 2)) {
                            String temp = mSearchInput.getText().toString().trim();
                            doSearch(temp);
                            flag = true;
                        } else {
                            flag = false;
                        }
                        break;
                    default:
                        flag = false;
                        break;
                }
                return flag;
            }
        });
        mSearchInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String search = mSearchInput.getText().toString().trim();
                    doSearch(search);
                    return true;
                }
                return false;
            }
        });

        mPopFilter.setItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mPresenter.setSearch(null);
                mSearchInput.setText(null);
                String temp = ((TextView) view).getText().toString();
                mFilterText.setText(temp);
                mPresenter.setFilter(AppConstants.MEMBER_RECORD_STATUS_FILTER.get(temp));
                mPresenter.load(false);
            }
        });
        mPopFilter.setDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                mFilterText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_pop_up, 0);
            }
        });

        mFilterText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopFilter.showAsDownCenter(true);
                mFilterText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_pop_down, 0);
            }
        });
    }

    private void doSearch(String search) {
        if (TextUtils.isEmpty(search)) {
            showToast("请输入搜索条件");
        } else {
            mPresenter.setSearch(search);
            mPresenter.load(false);
        }
    }

    @Override
    public void clearData() {
        mAdapter.clearData();
        mRecyclerList.removeAllViews();
    }

    @Override
    public void showData(List<MemberRecordInfo> list) {
        mAdapter.setData(list);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void showRefreshIng() {
        mLoadingLayout.setStatus(CustomLoadingLayout.STATUS_LOADING);
        mRefreshLayout.setRefreshing(true);
    }

    @Override
    public void showRefreshError() {
        mLoadingLayout.setStatus(CustomLoadingLayout.STATUS_ERROR);
        mRefreshLayout.setRefreshing(false);
    }

    @Override
    public void showRefreshEmpty() {
        mLoadingLayout.setStatus(CustomLoadingLayout.STATUS_EMPTY);
        mRefreshLayout.setRefreshing(false);
    }

    @Override
    public void showRefreshNoNetwork() {
        mLoadingLayout.setStatus(CustomLoadingLayout.STATUS_NONETWORK);
        mRefreshLayout.setRefreshing(false);
    }

    @Override
    public void showRefreshSuccess() {
        mLoadingLayout.setStatus(CustomLoadingLayout.STATUS_SUCCESS);
        mRefreshLayout.setRefreshing(false);
    }

    @Override
    public void showMoreLoading() {
        mAdapter.setStatus(AppConstants.FOOTER_STATUS_LOADING);
        mAdapter.notifyItemChanged(mAdapter.getItemCount() - 1);
    }

    @Override
    public void showMoreError() {
        mAdapter.setStatus(AppConstants.FOOTER_STATUS_ERROR);
        mAdapter.notifyItemChanged(mAdapter.getItemCount() - 1);
    }

    @Override
    public void showMoreNoNetwork() {
        mAdapter.setStatus(AppConstants.FOOTER_STATUS_NO_NETWORK);
        mAdapter.notifyItemChanged(mAdapter.getItemCount() - 1);
    }

    @Override
    public void showMoreNone() {
        mAdapter.setStatus(AppConstants.FOOTER_STATUS_NONE);
    }

    @Override
    public void showMoreSuccess() {
        mAdapter.setStatus(AppConstants.FOOTER_STATUS_SUCCESS);
    }

    @Override
    public void setPresenter(MemberRecordContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void finishSelf() {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.onDestroy();
        }
    }
}
