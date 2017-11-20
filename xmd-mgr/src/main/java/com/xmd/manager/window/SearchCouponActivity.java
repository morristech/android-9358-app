package com.xmd.manager.window;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shidou.commonlibrary.widget.XToast;
import com.xmd.manager.R;
import com.xmd.manager.adapter.SearchHistoryAdapter;
import com.xmd.manager.common.ResourceUtils;
import com.xmd.manager.common.SearchHistoryManager;
import com.xmd.manager.widget.ClearableEditText;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Lhj on 17-10-31.
 */

public class SearchCouponActivity extends BaseActivity {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.search_word)
    ClearableEditText searchWord;
    @BindView(R.id.tv_search)
    TextView tvSearch;
    @BindView(R.id.recycler_search_list)
    RecyclerView recyclerSearchList;
    @BindView(R.id.ll_history_view)
    LinearLayout llHistoryView;

    private String mSearchText;
    private SearchHistoryAdapter mAdapter;
    private List<String> mHistorySearchList;
    private SearchHistoryManager mHistoryManager;

    private CouponRecordSearchListFragment mCouponRecordListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_coupon);
        ButterKnife.bind(this);
        mHistoryManager = SearchHistoryManager.getSearchHistoryManagerInstance();
        initView();
    }

    private void initView() {
        mHistorySearchList = new ArrayList<>();
        mAdapter = new SearchHistoryAdapter(mHistorySearchList);
        recyclerSearchList.setHasFixedSize(true);
        recyclerSearchList.setLayoutManager(new LinearLayoutManager(SearchCouponActivity.this));
        recyclerSearchList.setAdapter(mAdapter);
        mAdapter.setOnItemClickedListener(new SearchHistoryAdapter.ItemClickedListener() {
            @Override
            public void onViewClicked(String data) {
                searchWord.setText(data);
                showOrHideSearchHistoryList(false);

            }
        });

        searchWord.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (searchWord.getText().toString().length() > 0) {
                    if (mHistoryManager.getSearchHistoryData().size() > 0) {
                        showOrHideSearchHistoryList(true);
                    }

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        searchWord.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchCoupon();
                    return true;
                }
                return false;
                //return false;
            }
        });
        initFragmentView();
    }

    private void initFragmentView() {
        mCouponRecordListFragment = new CouponRecordSearchListFragment();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.frame_layout, mCouponRecordListFragment);
        ft.commit();
    }

    @OnClick({R.id.iv_back, R.id.tv_search, R.id.tv_clear_all})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                this.finish();
                break;
            case R.id.tv_search:
                searchCoupon();
                break;
            case R.id.tv_clear_all:
                mHistoryManager.clecarData();
                showOrHideSearchHistoryList(false);
                break;
        }
    }

    private void searchCoupon() {
        mSearchText = searchWord.getText().toString();
        if (TextUtils.isEmpty(mSearchText)) {
            XToast.show(ResourceUtils.getString(R.string.customer_search_alter));
            return;
        }
         ((InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        showOrHideSearchHistoryList(false);
        mHistoryManager.addData(mSearchText);
        mCouponRecordListFragment.searchText(mSearchText);
    }

    private void showOrHideSearchHistoryList(boolean isShow) {
        llHistoryView.setVisibility(isShow ? View.VISIBLE : View.GONE);
        if (isShow) {
            mAdapter.setData(mHistoryManager.getSearchHistoryData());
        }
    }
}
