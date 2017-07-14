package com.xmd.m.comment;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.shidou.commonlibrary.util.DateUtils;
import com.xmd.app.utils.Utils;
import com.xmd.app.widget.DateTimePickDialog;
import com.xmd.m.R;
import com.xmd.m.comment.adapter.TechNumberAdapter;
import com.xmd.m.comment.bean.TechBean;
import com.xmd.m.comment.httprequest.ConstantResources;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lhj on 17-7-3.
 */

public class CommentFilterPopupWindow extends PopupWindow implements View.OnClickListener {
    TextView startTime;
    TextView endTime;
    RecyclerView techRecyclerView;
    TextView commentLimitNone;
    TextView tvCommentOnly;
    TextView tvCommentComplaintOnly;
    TextView commentFilterCancel;
    TextView commentFilterSubmit;

    private Context mContext;
    private View mRootView;
    private List<View> mViewList;
    private String mCurrentCommentFilter;
    private TechNumberAdapter mAdapter;
    private List<String> mSelectedTechNumber;
    private List<TechBean> mTechBeanList;
    private CommentFilterInterface mCommentInterface;

    public static CommentFilterPopupWindow getInstance(Context context, List<TechBean> beans) {
        View contentView = LayoutInflater.from(context).inflate(R.layout.fragment_comment_filter_dialog, null);
        return new CommentFilterPopupWindow(context, contentView, beans);
    }

    private CommentFilterPopupWindow(Context context, View view, List<TechBean> beans) {
        super(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        this.mContext = context;
        this.mRootView = view;
        this.setBackgroundDrawable(new BitmapDrawable());
        this.setAnimationStyle(R.style.popup_window_style);
        this.setOutsideTouchable(true);
        this.mTechBeanList = beans;
        initViews();
    }

    public interface CommentFilterInterface {
        void filterComment(String starTime, String endTime, String techList, String commentType);
    }

    public void setCommentFilterListener(CommentFilterInterface commentInterface) {
        this.mCommentInterface = commentInterface;
    }

    private void initViews() {
        startTime = (TextView) mRootView.findViewById(R.id.startTime);
        endTime = (TextView) mRootView.findViewById(R.id.endTime);
        techRecyclerView = (RecyclerView) mRootView.findViewById(R.id.tech_recycler_view);
        commentLimitNone = (TextView) mRootView.findViewById(R.id.comment_limit_none);
        tvCommentOnly = (TextView) mRootView.findViewById(R.id.tv_comment_only);
        tvCommentComplaintOnly = (TextView) mRootView.findViewById(R.id.tv_comment_complaint_only);
        commentFilterCancel = (TextView) mRootView.findViewById(R.id.comment_filter_reset);
        commentFilterSubmit = (TextView) mRootView.findViewById(R.id.comment_filter_submit);
        startTime.setText(DateUtils.getCurrentDate());
        endTime.setText(DateUtils.getCurrentDate());
        mViewList = new ArrayList<>();

        startTime.setOnClickListener(this);
        endTime.setOnClickListener(this);
        commentLimitNone.setOnClickListener(this);
        tvCommentOnly.setOnClickListener(this);
        tvCommentComplaintOnly.setOnClickListener(this);
        commentFilterCancel.setOnClickListener(this);
        commentFilterSubmit.setOnClickListener(this);
        mViewList.add(commentLimitNone);
        mViewList.add(tvCommentOnly);
        mViewList.add(tvCommentComplaintOnly);
        setViewSate(commentLimitNone);
        mCurrentCommentFilter = "";
        mSelectedTechNumber = new ArrayList<>();
        mTechBeanList.add(0, new TechBean("不限", true));
        mAdapter = new TechNumberAdapter(mTechBeanList, new TechNumberAdapter.TechNoClickedListener() {


            @Override
            public void techNoItem(TechBean bean, int position) {
                if (position == 0) {//不限
                    mSelectedTechNumber.clear();
                    for (TechBean techBean : mTechBeanList) {
                        techBean.isSelected = false;
                    }
                    mTechBeanList.get(0).isSelected = true;
                    mAdapter.setData(mTechBeanList);
                } else {
                    if (mTechBeanList.get(position).isSelected) {
                        mTechBeanList.get(position).isSelected = false;
                        mSelectedTechNumber.remove(mTechBeanList.get(position).techId);
                    } else {
                        mSelectedTechNumber.add(mTechBeanList.get(position).techId);
                        mTechBeanList.get(position).isSelected = true;
                    }
                    if (mSelectedTechNumber.size() > 0) {
                        mTechBeanList.get(0).isSelected = false;
                    } else {
                        mTechBeanList.get(0).isSelected = true;
                    }
                    mAdapter.notifyItemChanged(0);
                    mAdapter.notifyItemChanged(position);
                }

            }
        });

        RecyclerView.LayoutManager manager = new GridLayoutManager(mContext, 5);
        techRecyclerView.setLayoutManager(manager);
        techRecyclerView.setAdapter(mAdapter);
    }

    public void setViewSate(TextView viewSate) {
        for (int i = 0; i < mViewList.size(); i++) {
            mViewList.get(i).setSelected(false);
        }
        viewSate.setSelected(true);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.startTime) {
            DateTimePickDialog dataPickDialogStr = new DateTimePickDialog((CommentListActivity) mContext, startTime.getText().toString());
            dataPickDialogStr.dateTimePicKDialog(startTime);
            return;
        }
        if (i == R.id.endTime) {
            DateTimePickDialog dataPickDialogEnd = new DateTimePickDialog((CommentListActivity) mContext, endTime.getText().toString());
            dataPickDialogEnd.dateTimePicKDialog(endTime);
            return;
        }
        if (i == R.id.comment_limit_none) {
            setViewSate(commentLimitNone);
            mCurrentCommentFilter = "";
            return;
        }
        if (i == R.id.tv_comment_only) {
            setViewSate(tvCommentOnly);
            mCurrentCommentFilter = ConstantResources.COMMENT_TYPE_COMMENT;
            return;
        }
        if (i == R.id.tv_comment_complaint_only) {
            setViewSate(tvCommentComplaintOnly);
            mCurrentCommentFilter = ConstantResources.COMMENT_TYPE_COMPLAINT;
            return;
        }

        if (i == R.id.comment_filter_reset) {
            setViewSate(commentLimitNone);
            mCurrentCommentFilter = "";
            mSelectedTechNumber.clear();
            for (TechBean techBean : mTechBeanList) {
                techBean.isSelected = false;
            }
            mTechBeanList.get(0).isSelected = true;
            mAdapter.setData(mTechBeanList);
            return;
        }

        if (i == R.id.comment_filter_submit) {
            String start = startTime.getText().toString();
            String end = endTime.getText().toString();
            String techLest = Utils.listToString(mSelectedTechNumber, ",");
            String currentCommentFilter = mCurrentCommentFilter;
            if (mCommentInterface != null) {
                mCommentInterface.filterComment(start, end, techLest, currentCommentFilter);
            }
            this.dismiss();
            return;
        }

    }

    public void showAsViewDown(View view, int x, int y) {
        showAsDropDown(view, x, y);
        Utils.maskScreen((Activity) mContext, true);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        Utils.maskScreen((Activity) mContext, false);

    }
}
