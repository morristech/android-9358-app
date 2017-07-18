package com.xmd.m.comment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.xmd.app.BaseActivity;
import com.xmd.m.R;
import com.xmd.m.comment.bean.TechBean;
import com.xmd.m.comment.bean.TechListResult;
import com.xmd.m.comment.httprequest.ConstantResources;
import com.xmd.m.comment.httprequest.DataManager;
import com.xmd.m.network.NetworkSubscriber;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Lhj on 17-6-30.
 */

public class CommentListActivity extends BaseActivity implements CommentFilterPopupWindow.CommentFilterInterface {

    private ImageView toolbarRight;
    private boolean isFromManager;//来自管理者
    private CommentListFragmentManager mFragmentManager;
    private CommentListFragmentTech mFragmentTech;
    private List<TechBean> mTechBeanList;
    private CommentFilterPopupWindow popupWindow;
    private String techNo;

    public static void startCommentListActivity(Activity activity, boolean isManager, String techNo) {
        Intent intent = new Intent(activity, CommentListActivity.class);
        intent.putExtra(ConstantResources.INTENT_TYPE, isManager);
        intent.putExtra(ConstantResources.INTENT_TECH_NO, techNo);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getIntentData();
        setContentView(R.layout.activity_comment_list);
        initView();
    }

    public void getIntentData() {
        Intent intentData = getIntent();
        isFromManager = intentData.getBooleanExtra(ConstantResources.INTENT_TYPE, false);
        techNo = intentData.getStringExtra(ConstantResources.INTENT_TECH_NO);
    }

    private void initView() {
        setTitle(R.string.comment_activity_title);
        setBackVisible(true);
        toolbarRight = (ImageView) findViewById(R.id.img_toolbar_right);
        if (isFromManager) {
            setRightVisible(true, R.drawable.icon_comment_filter);
        } else {
            setRightVisible(true, R.drawable.ic_search_normal);
        }
        initFragmentView();
    }


    private void initFragmentView() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        if (isFromManager) {
            getTechList();
            mFragmentManager = new CommentListFragmentManager();
            ft.replace(R.id.fragment_comment_list, mFragmentManager);
        } else {
            if (TextUtils.isEmpty(techNo)) {
                Toast.makeText(this, "技师id为空", Toast.LENGTH_SHORT).show();
            } else {
                mFragmentTech = CommentListFragmentTech.newInstance(techNo);
                ft.replace(R.id.fragment_comment_list, mFragmentTech);
            }

        }
        ft.commit();
    }

    private void getTechList() {
        DataManager.getInstance().loadTechList(new NetworkSubscriber<TechListResult>() {
            @Override
            public void onCallbackSuccess(TechListResult result) {
                if (mTechBeanList == null) {
                    mTechBeanList = new ArrayList<>();
                } else {
                    mTechBeanList.clear();
                }
                for (int i = 0; i < result.getRespData().size(); i++) {
                    if (!TextUtils.isEmpty(result.getRespData().get(i).techNo)) {
                        mTechBeanList.add(result.getRespData().get(i));
                    }
                }

            }

            @Override
            public void onCallbackError(Throwable e) {
                showToast(e.getLocalizedMessage());
            }
        });
    }


    @Override
    public void onRightImageClickedListener() {
        if (isFromManager && null != mTechBeanList) {
            if (popupWindow == null) {
                popupWindow = CommentFilterPopupWindow.getInstance(this, mTechBeanList);
                popupWindow.setCommentFilterListener(this);
            }
            popupWindow.showAsViewDown(toolbarRight, 0, 40);
        } else {
            CommentSearchActivity.startCommentSearchActivity(CommentListActivity.this, isFromManager, true, techNo, "11111111111");
        }

    }

    @Override
    public void filterComment(String starTime, String endTime, String techList, String commentType) {
        FragmentManager fm = getSupportFragmentManager();
        CommentListFragmentManager managerFragment = (CommentListFragmentManager) fm.findFragmentById(R.id.fragment_comment_list);
        managerFragment.filterComment(starTime, endTime, techList, commentType);
    }

}
