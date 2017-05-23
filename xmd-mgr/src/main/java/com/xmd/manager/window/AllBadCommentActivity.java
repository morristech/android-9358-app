package com.xmd.manager.window;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;

import com.xmd.manager.R;
import com.xmd.manager.adapter.PageFragmentPagerAdapter;
import com.xmd.manager.common.ResourceUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by lhj on 2016/11/18.
 */
public class AllBadCommentActivity extends BaseActivity {

    @Bind(R.id.btn_wait_return_visit)
    Button btnWaitReturnVisit;
    @Bind(R.id.btn_had_return_visited)
    Button btnHadReturnVisited;
    @Bind(R.id.vp_bad_comment_contact)
    ViewPager mVpBadCommentContact;

    private PageFragmentPagerAdapter mPageFragmentPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_bad_comment);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        setRightVisible(true, ResourceUtils.getString(R.string.bad_comment_rank), view -> {
            startActivity(new Intent(AllBadCommentActivity.this, AllTechBadCommentActivity.class));
        });

        initViewPager();
    }

    private void initViewPager() {
        mPageFragmentPagerAdapter = new PageFragmentPagerAdapter(getSupportFragmentManager(), AllBadCommentActivity.this);
        mPageFragmentPagerAdapter.addFragment(new WaitingReturnVisitFragment());
        mPageFragmentPagerAdapter.addFragment(new HadReturnFragment());
        mVpBadCommentContact.setAdapter(mPageFragmentPagerAdapter);
        mVpBadCommentContact.setCurrentItem(0);
        btnWaitReturnVisit.setSelected(true);
        mVpBadCommentContact.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        resetBtnView(0);
                        break;
                    case 1:
                        resetBtnView(1);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    @OnClick({R.id.btn_wait_return_visit, R.id.btn_had_return_visited})
    public void onBtnClickedListener(View v) {
        switch (v.getId()) {
            case R.id.btn_wait_return_visit:
                mVpBadCommentContact.setCurrentItem(0);
                resetBtnView(0);
                break;
            case R.id.btn_had_return_visited:
                mVpBadCommentContact.setCurrentItem(1);
                resetBtnView(1);
                break;
        }

    }

    public void resetBtnView(int position) {
        if (position == 0) {
            btnWaitReturnVisit.setSelected(true);
            btnHadReturnVisited.setSelected(false);
        } else {
            btnWaitReturnVisit.setSelected(false);
            btnHadReturnVisited.setSelected(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}
