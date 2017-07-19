package com.xmd.m.comment;

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

/**
 * Created by Lhj on 17-7-5.
 */

public class CommentListFragmentTech extends BaseFragment {

    public static final String KEY_TECH_NO = "techNo";
    private ViewPagerTabIndicator commentViewPagerIndicator;
    private ViewPager commentViewPager;
    private PageFragmentAdapter mPageFragmentAdapter;
    private String techId;

    public CommentListFragmentTech() {

    }

    public static CommentListFragmentTech newInstance(String techNo) {
        CommentListFragmentTech fragmentTech = new CommentListFragmentTech();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_TECH_NO, techNo);
        fragmentTech.setArguments(bundle);
        return fragmentTech;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comment_list_tech, container, false);
        techId = getArguments().getString(KEY_TECH_NO);
        commentViewPagerIndicator = (ViewPagerTabIndicator) view.findViewById(R.id.comment_view_pager_indicator);
        commentViewPager = (ViewPager) view.findViewById(R.id.comment_view_pager);
        initViewPagerView();
        return view;
    }

    private void initViewPagerView() {
        String[] tabTexts = new String[]{ResourceUtils.getString(R.string.comment_table_good), ResourceUtils.getString(R.string.comment_table_middle), ResourceUtils.getString(R.string.comment_table_bad)};
        mPageFragmentAdapter = new PageFragmentAdapter(getFragmentManager(), getActivity());
        for (int i = 0; i < tabTexts.length; i++) {
            Bundle args = new Bundle();
            args.putSerializable(TechCommentListFragment.BIZ_TYPE, tabTexts[i]);
            args.putSerializable(TechCommentListFragment.TECH_ID, techId);
            mPageFragmentAdapter.addFragment(TechCommentListFragment.class.getName(), args);
        }
        commentViewPager.setOffscreenPageLimit(3);
        commentViewPager.setAdapter(mPageFragmentAdapter);
        commentViewPagerIndicator.setTabTexts(tabTexts);
        commentViewPagerIndicator.setWithIndicator(true);
        commentViewPagerIndicator.setIndicatorGravity(ViewPagerTabIndicator.INDICATOR_BOTTOM);
        commentViewPagerIndicator.setViewPager(commentViewPager);
        commentViewPagerIndicator.setWithDivider(false);
        commentViewPagerIndicator.setup();
    }


}
