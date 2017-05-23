package com.xmd.cashier.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.shidou.commonlibrary.util.DateUtils;
import com.xmd.cashier.R;
import com.xmd.cashier.adapter.VerifyRecordAdapter;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.contract.VerifyRecordContract;
import com.xmd.cashier.dal.bean.VerifyRecordInfo;
import com.xmd.cashier.presenter.VerifyRecordPresenter;
import com.xmd.cashier.widget.ArrayPopupWindow;
import com.xmd.cashier.widget.CustomLoadingLayout;
import com.xmd.cashier.widget.stickyview.StickyHeaderInterface;
import com.xmd.cashier.widget.stickyview.StickyRecyclerHeadersDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zr on 17-5-2.
 * 核销记录
 */

public class VerifyRecordActivity extends BaseActivity implements VerifyRecordContract.View {
    private CustomLoadingLayout mLoadingLayout;
    private SwipeRefreshLayout mRefreshLayout;
    private VerifyRecordAdapter mAdapter;
    private StickyRecyclerHeadersDecoration mDecoration;
    private RecyclerView mRecyclerList;

    private VerifyRecordContract.Presenter mPresenter;

    private LinearLayoutManager mLayoutManager;
    private ArrayPopupWindow<String> mPopFilter;

    private EditText mSearchInput;
    private TextView mFilterText;

    private int mLastVisibleItem;

    private List<VerifyRecordInfo> mDecorateData = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_record);
        mPresenter = new VerifyRecordPresenter(this, this);
        initView();
        mPresenter.onStart();
        mPresenter.loadTypeMap();
        onInit();
    }

    private void onInit() {
        mRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mRefreshLayout.setRefreshing(true);
                mPresenter.load(true, Utils.getMonthStart(), Utils.getMonthEnd());
            }
        });
    }

    private void initView() {
        showToolbar(R.id.toolbar, "核销记录");
        mFilterText = (TextView) findViewById(R.id.tv_type_filter);
        mSearchInput = (EditText) findViewById(R.id.et_list_search);
        mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.ly_list_refresh);
        mLoadingLayout = (CustomLoadingLayout) findViewById(R.id.ly_load_status);
        mRecyclerList = (RecyclerView) findViewById(R.id.rv_data_list);
        mAdapter = new VerifyRecordAdapter(this);
        mLayoutManager = new LinearLayoutManager(this);

        // 图标
        mSearchInput.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                boolean flag;
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_UP:
                        Drawable drawableRight = mSearchInput.getCompoundDrawables()[2];
                        if (drawableRight != null && event.getX() > (mSearchInput.getWidth() - drawableRight.getBounds().width())) {
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
        // 输入法
        mSearchInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String temp = mSearchInput.getText().toString().trim();
                    doSearch(temp);
                    return true;
                }
                return false;
            }
        });

        mAdapter.setVerifyRecordCallBack(new VerifyRecordAdapter.VerifyRecordCallBack() {
            @Override
            public void onLoadMore() {
                mPresenter.loadMore();
            }

            @Override
            public void onRecordClick(VerifyRecordInfo info, int position) {
                mPresenter.onRecordClick(String.valueOf(info.id));
            }
        });

        mDecoration = new StickyRecyclerHeadersDecoration(new StickyHeaderInterface() {
            @Override
            protected String getHeaderId(int position) {
                try {
                    if (TextUtils.isEmpty(mDecorateData.get(position).verifyTime)) {
                        return "未知";
                    } else {
                        return DateUtils.doString2String(mDecorateData.get(position).verifyTime, DateUtils.DF_DEFAULT, DateUtils.DF_JUST_YEAR_MONTH_ZH);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
                return new HeadItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_verify_header, parent, false));
            }

            @Override
            protected void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
                try {
                    HeadItemHolder headItemHolder = (HeadItemHolder) holder;
                    headItemHolder.mHeadTime.setText(getHeaderId(position));
                    headItemHolder.mHeadCount.setText("核销数：" + mDecorateData.get(position).currentCount);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected int getItemCount() {
                return mDecorateData.size();
            }

            @Override
            protected boolean showItemHeader() {
                return true;
            }
        });

        mRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        mRecyclerList.setHasFixedSize(true);
        mRecyclerList.setLayoutManager(mLayoutManager);
        mRecyclerList.addItemDecoration(mDecoration);
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
        mRecyclerList.setAdapter(mAdapter);

        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.load(false, Utils.getMonthStart(), Utils.getMonthEnd());
            }
        });

        mLoadingLayout.setOnReloadListener(new CustomLoadingLayout.OnReloadListener() {
            @Override
            public void onReload(View view) {
                mPresenter.load(false, Utils.getMonthStart(), Utils.getMonthEnd());
            }
        });
    }

    private void doSearch(String search) {
        mPresenter.setSearch(search);
        mPresenter.load(false, Utils.getMonthStart(), Utils.getMonthEnd());
    }

    @Override
    public void initFilter(List<String> list) {
        mFilterText.setText(AppConstants.STATUS_ALL);
        mPopFilter = new ArrayPopupWindow<>(this, mFilterText, null, getWindowManager().getDefaultDisplay().getWidth() / 3, R.style.anim_top_to_bottom_style,
                getResources().getDrawable(R.drawable.bg_ex_popup_window), 0);
        mPopFilter.setDataSet(list);
        mPopFilter.setItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String text = ((TextView) view).getText().toString();
                mFilterText.setText(text);
                mPresenter.setTypeFilter(text);
                mPresenter.load(false, Utils.getMonthStart(), Utils.getMonthEnd());
            }
        });
        mPopFilter.setDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {

            }
        });
        mFilterText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopFilter.showAsDownCenter(true);
            }
        });
    }

    @Override
    public void clearData() {
        mDecorateData.clear();
        mDecoration.invalidateHeaders();
        mAdapter.clearData();
        mRecyclerList.removeAllViews();
    }

    @Override
    public void showData(List<VerifyRecordInfo> list) {
        mDecorateData.addAll(list);
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
        mAdapter.notifyItemChanged(mAdapter.getItemCount() - 1);
    }

    @Override
    public void showMoreSuccess() {
        mAdapter.setStatus(AppConstants.FOOTER_STATUS_SUCCESS);
        mAdapter.notifyItemChanged(mAdapter.getItemCount() - 1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.onDestroy();
        }
    }

    @Override
    public void setPresenter(VerifyRecordContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void finishSelf() {
        finish();
    }

    public class HeadItemHolder extends RecyclerView.ViewHolder {
        public TextView mHeadTime;
        public TextView mHeadCount;

        public HeadItemHolder(View itemView) {
            super(itemView);
            mHeadTime = (TextView) itemView.findViewById(R.id.item_header_time);
            mHeadCount = (TextView) itemView.findViewById(R.id.item_header_count);
        }
    }
}
