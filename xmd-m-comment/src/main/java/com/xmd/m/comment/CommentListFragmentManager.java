package com.xmd.m.comment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xmd.app.BaseFragment;
import com.xmd.app.PageFragmentAdapter;
import com.xmd.app.utils.ResourceUtils;
import com.xmd.app.widget.ViewPagerTabIndicator;
import com.xmd.m.R;
import com.xmd.m.R2;
import com.xmd.m.comment.httprequest.ConstantResources;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Lhj on 17-7-5.
 */

public class CommentListFragmentManager extends BaseFragment {

    Unbinder unbinder;
    private ViewPagerTabIndicator commentViewPagerIndicator;
    private ViewPager commentViewPager;
    private PageFragmentAdapter mPageFragmentAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comment_list_manager, container, false);
        commentViewPagerIndicator = (ViewPagerTabIndicator) view.findViewById(R.id.comment_view_pager_indicator);
        commentViewPager = (ViewPager) view.findViewById(R.id.comment_view_pager);
        initViewPagerView();
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    private void initViewPagerView() {
        String[] tabTexts = new String[]{ResourceUtils.getString(R.string.comment_table_good), ResourceUtils.getString(R.string.comment_table_middle), ResourceUtils.getString(R.string.comment_table_complaint)};
        mPageFragmentAdapter = new PageFragmentAdapter(getFragmentManager(), getActivity());
        for (int i = 0; i < tabTexts.length; i++) {
            Bundle args = new Bundle();
            args.putSerializable(ManagerCommentListFragment.BIZ_TYPE, tabTexts[i]);
            mPageFragmentAdapter.addFragment(ManagerCommentListFragment.class.getName(), args);
        }
        commentViewPager.setOffscreenPageLimit(3);
        commentViewPager.setAdapter(mPageFragmentAdapter);
        commentViewPagerIndicator.setTabTexts(tabTexts);
        commentViewPagerIndicator.setWithIndicator(true);
        commentViewPagerIndicator.setIndicatorGravity(ViewPagerTabIndicator.INDICATOR_BOTTOM);
        commentViewPagerIndicator.setViewPager(commentViewPager);
        commentViewPagerIndicator.setWithDivider(false);
        commentViewPagerIndicator.setup();
        commentViewPager.setCurrentItem(2);
    }

    public void filterComment(String starTime, String endTime, String techList, String commentType) {

        if (commentType.equals(ConstantResources.COMMENT_TYPE_COMPLAINT)) {
            commentViewPager.setCurrentItem(2);
        }
        ManagerCommentListFragment complaintGood = (ManagerCommentListFragment) mPageFragmentAdapter.getFragments().get(0);
        complaintGood.filterComplaint(starTime, endTime, techList, commentType);
        ManagerCommentListFragment complaintMiddle = (ManagerCommentListFragment) mPageFragmentAdapter.getFragments().get(1);
        complaintMiddle.filterComplaint(starTime, endTime, techList, commentType);
        ManagerCommentListFragment complaintBad = (ManagerCommentListFragment) mPageFragmentAdapter.getFragments().get(2);
        complaintBad.filterComplaint(starTime, endTime, techList, commentType);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R2.id.ll_comment_search)
    public void onLlCommentSearchClicked() {
        CommentSearchActivity.startCommentSearchActivity(getActivity(), true, true, "", "1245645647987");
    }

    @OnClick(R2.id.img_comment_ranking_list)
    public void onImgCommentRankingListClicked() {
        Intent intent = new Intent();
        intent.setClassName(getActivity(), "com.xmd.manager.window.AllTechBadCommentActivity");
        startActivity(intent);

    }
}
