package com.xmd.m.comment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.xmd.app.BaseActivity;
import com.xmd.app.widget.ClearableEditText;
import com.xmd.m.R;
import com.xmd.m.R2;
import com.xmd.m.comment.httprequest.ConstantResources;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Lhj on 17-7-5.
 */

public class CommentSearchActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R2.id.edit_search_contact)
    ClearableEditText editSearchContact;
    @BindView(R2.id.img_btn_search)
    ImageView imgBtnSearch;
    @BindView(R2.id.ll_search_view)
    LinearLayout llSearchView;

    private boolean isFromManager;
    private String techNo;
    private boolean isSearch;
    private String searchTelephone;
    private SearchCommentListFragment mCommentListFragment;

    public static void startCommentSearchActivity(Activity activity, boolean isFromManager, boolean isSearch, String techNo, String telephone) {
        Intent intent = new Intent(activity, CommentSearchActivity.class);
        intent.putExtra(ConstantResources.INTENT_TYPE, isFromManager);
        intent.putExtra(ConstantResources.KEY_IS_SEARCH, isSearch);
        intent.putExtra(ConstantResources.KEY_SEARCH_TELEPHONE, telephone);
        intent.putExtra(ConstantResources.INTENT_TECH_NO, techNo);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_search);
        ButterKnife.bind(this);
        getIntentData();
        initView();
        initFragmentView();

    }

    public void getIntentData() {
        isFromManager = getIntent().getBooleanExtra(ConstantResources.INTENT_TYPE, false);
        isSearch = getIntent().getBooleanExtra(ConstantResources.KEY_IS_SEARCH, false);
        searchTelephone = getIntent().getStringExtra(ConstantResources.KEY_SEARCH_TELEPHONE);
        techNo = getIntent().getStringExtra(ConstantResources.INTENT_TECH_NO);
    }

    private void initView() {
        if (isFromManager && !isSearch) {
            setTitle("投诉评论");
            llSearchView.setVisibility(View.GONE);
        } else if (!isFromManager && !isSearch) {
            llSearchView.setVisibility(View.GONE);
            setTitle("全部评论");
        } else {
            llSearchView.setVisibility(View.VISIBLE);
            setTitle("搜索");
        }
        imgBtnSearch.setOnClickListener(this);
    }


    private void initFragmentView() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        mCommentListFragment = new SearchCommentListFragment();
        ft.replace(R.id.search_fragment, mCommentListFragment);
        Bundle args = new Bundle();
        args.putBoolean(ConstantResources.INTENT_TYPE, isFromManager);
        args.putString(ConstantResources.INTENT_TECH_NO, techNo);
        args.putBoolean(ConstantResources.KEY_IS_SEARCH, isSearch);
        args.putString(ConstantResources.KEY_SEARCH_TELEPHONE, searchTelephone);
        mCommentListFragment.setArguments(args);
        ft.commit();
    }


    @Override
    public void onClick(View v) {
        String search = editSearchContact.getText().toString();
        if (TextUtils.isEmpty(search)) {
            showToast("请输入搜索内容");
        } else {
            mCommentListFragment.searchCustomer(search);
        }

    }
}
