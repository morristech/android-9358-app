package com.xmd.m.comment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.crazyman.library.PermissionTool;
import com.shidou.commonlibrary.helper.XLogger;
import com.shidou.commonlibrary.util.DateUtils;
import com.shidou.commonlibrary.widget.XToast;
import com.xmd.app.BaseActivity;
import com.xmd.app.utils.ResourceUtils;
import com.xmd.app.widget.RoundImageView;
import com.xmd.app.widget.StarBar;
import com.xmd.m.R;
import com.xmd.m.R2;
import com.xmd.m.comment.adapter.CommentItemDetailAdapter;
import com.xmd.m.comment.bean.CommentBean;
import com.xmd.m.comment.bean.CommentStatusResult;
import com.xmd.m.comment.bean.UserInfoBean;
import com.xmd.m.comment.event.UserInfoEvent;
import com.xmd.m.comment.httprequest.ConstantResources;
import com.xmd.m.comment.httprequest.DataManager;
import com.xmd.m.comment.httprequest.NetService;
import com.xmd.m.comment.httprequest.RequestConstant;
import com.xmd.m.network.BaseBean;
import com.xmd.m.network.NetworkSubscriber;
import com.xmd.m.network.XmdNetwork;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import rx.Observable;
import rx.Subscription;

/**
 * Created by Lhj on 17-7-4.
 */

public class CommentDetailActivity extends BaseActivity {

    @BindView(R2.id.user_head)
    RoundImageView userHead;
    @BindView(R2.id.user_name)
    TextView userName;
    @BindView(R2.id.user_phone)
    TextView userPhone;
    @BindView(R2.id.user_comment_time)
    TextView userCommentTime;
    @BindView(R2.id.comment_tech_name)
    TextView commentTechName;
    @BindView(R2.id.comment_tech_no)
    TextView commentTechNo;
    @BindView(R2.id.starBar)
    StarBar starBar;
    @BindView(R2.id.bad_comment_detail)
    RecyclerView badCommentDetail;
    @BindView(R2.id.tv_comment_text)
    TextView tvCommentText;
    @BindView(R2.id.ll_comment_visit_btn)
    LinearLayout llCommentVisitBtn;
    @BindView(R2.id.ll_comment_complaint)
    LinearLayout llCommentComplaint;
    @BindView(R2.id.tv_comment_score_number)
    TextView tvCommentScoreNumber;
    @BindView(R2.id.tv_comment_complaint_detail)
    TextView tvCommentComplaintDetail;
    @BindView(R2.id.ll_comment_comment)
    LinearLayout llCommentComment;
    @BindView(R2.id.img_visit_mark)
    ImageView imgVisitMark;
    @BindView(R2.id.ll_comment_tech)
    LinearLayout llCommentTech;
    @BindView(R2.id.ll_comment_impression)
    LinearLayout llCommentImpression;
    @BindView(R2.id.ll_impression_detail)
    LinearLayout llImpressionDetail;


    Unbinder mUnBinder;
    private static final int REQUEST_CODE_CALL_PERMISSION = 0x1;
    private String contactPhone;
    private CommentItemDetailAdapter mCommentItemDetailAdapter;
    private CommentBean mCommentBean;
    private String commentId;
    private Subscription loadDataSubscription;
    private boolean isFromManager;
    private String mType;

    public static void startCommentDetailActivity(Activity activity, CommentBean bean, boolean isManager, String type) {
        Intent intent = new Intent(activity, CommentDetailActivity.class);
        intent.putExtra(ConstantResources.COMMENT_DETAIL, bean);
        intent.putExtra(ConstantResources.COMMENT_DETAIL_INTENT_IS_MANAGER, isManager);
        intent.putExtra(ConstantResources.BIZ_TYPE, type);
        activity.startActivity(intent);
    }

    public static void startCommentDetailActivity(Activity activity, String commentId, boolean isManager) {
        Intent intent = new Intent(activity, CommentDetailActivity.class);
        intent.putExtra(ConstantResources.COMMENT_ID, commentId);
        intent.putExtra(ConstantResources.COMMENT_DETAIL_INTENT_IS_MANAGER, isManager);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_detail);
        mUnBinder = ButterKnife.bind(this);
        init();

    }


    public void init() {
        mCommentBean = getIntent().getParcelableExtra(ConstantResources.COMMENT_DETAIL);
        mType = getIntent().getStringExtra(ConstantResources.BIZ_TYPE);
        isFromManager = getIntent().getBooleanExtra(ConstantResources.COMMENT_DETAIL_INTENT_IS_MANAGER, false);
        if (mCommentBean != null) {
            initView();
        } else {
            commentId = getIntent().getStringExtra(ConstantResources.COMMENT_ID);
            if (commentId == null) {
                XToast.show("缺少参数！");
                finish();
                return;
            }
            loadData();
        }

        if (isFromManager) {
            llCommentTech.setVisibility(View.VISIBLE);
        } else {
            llCommentTech.setVisibility(View.GONE);
        }
    }

    private void loadData() {
        Observable<BaseBean<CommentBean>> observable = XmdNetwork.getInstance()
                .getService(NetService.class)
                .getCommentDetail(commentId);
        loadDataSubscription = XmdNetwork.getInstance()
                .request(observable, new NetworkSubscriber<BaseBean<CommentBean>>() {
                    @Override
                    public void onCallbackSuccess(BaseBean<CommentBean> result) {
                        mCommentBean = result.getRespData();
                        initView();
                    }

                    @Override
                    public void onCallbackError(Throwable e) {
                        XToast.show("数据加载失败:" + e.getMessage());
                        finish();
                    }
                });
    }

    private void initView() {
        setTitle(ResourceUtils.getString(R.string.comment_detail_activity_title));
        setBackVisible(true);
        contactPhone = mCommentBean.phoneNum;
        if (isFromManager && !mCommentBean.status.equals(ConstantResources.COMMENT_STATUS_DELETE)) {
            setRightVisible(true, R.drawable.icon_comment_mark_delete);
        } else {
            setRightVisible(false, -1);
        }
        if (mCommentBean.commentType.equals(ConstantResources.COMMENT_TYPE_COMPLAINT)) {
            llCommentComplaint.setVisibility(View.VISIBLE);
            llCommentComment.setVisibility(View.GONE);
            initComplaintView();
        } else {
            llCommentComplaint.setVisibility(View.GONE);
            llCommentComment.setVisibility(View.VISIBLE);
            initCommentView();
        }

    }

    private void initComplaintView() {
        tvCommentComplaintDetail.setText(mCommentBean.comment);
        if (mCommentBean.returnStatus.equals("Y")) {
            imgVisitMark.setVisibility(View.VISIBLE);
        } else {
            imgVisitMark.setVisibility(View.GONE);
        }
        if (mCommentBean.isAnonymous.equals("N") && isFromManager) {
            llCommentVisitBtn.setVisibility(View.VISIBLE);
        } else {
            llCommentVisitBtn.setVisibility(View.GONE);
        }
        Glide.with(this).load(mCommentBean.avatarUrl).error(ResourceUtils.getDrawable(R.drawable.img_default_avatar)).into(userHead);
        userName.setText(TextUtils.isEmpty(mCommentBean.userName) ? "匿名用户" : mCommentBean.userName);
        userPhone.setText(TextUtils.isEmpty(mCommentBean.phoneNum) ? "" : mCommentBean.phoneNum);
        userCommentTime.setText(DateUtils.doLong2String(mCommentBean.createdAt, DateUtils.DF_DEFAULT));
    }

    private void initCommentView() {

        if (!isFromManager && mType.equals("1")) {//差评
            String commentUserName = TextUtils.isEmpty(mCommentBean.userName) ? "匿名用户" : mCommentBean.userName;
            userName.setText(String.format("%s**(匿名)", commentUserName.substring(0, 1)));
            userPhone.setText("");
            Glide.with(this).load(R.drawable.img_default_avatar).into(userHead);
        } else {
            Glide.with(this).load(mCommentBean.avatarUrl).error(ResourceUtils.getDrawable(R.drawable.img_default_avatar)).into(userHead);
            userName.setText(TextUtils.isEmpty(mCommentBean.userName) ? "匿名用户" : mCommentBean.userName);
            userPhone.setText(TextUtils.isEmpty(mCommentBean.phoneNum) ? "" : mCommentBean.phoneNum);
        }
        if (mCommentBean.returnStatus.equals("Y")) {
            imgVisitMark.setVisibility(View.VISIBLE);
        } else {
            imgVisitMark.setVisibility(View.GONE);
        }
        if (TextUtils.isEmpty(mCommentBean.impression)) {
            llCommentImpression.setVisibility(View.GONE);
        } else {
            llCommentImpression.setVisibility(View.VISIBLE);
            initCommentImpressionView(mCommentBean.impression);
        }


        userCommentTime.setText(DateUtils.doLong2String(mCommentBean.createdAt, DateUtils.DF_DEFAULT));
        commentTechName.setText(TextUtils.isEmpty(mCommentBean.techName) ? "会所" : mCommentBean.techName);
        commentTechNo.setText(TextUtils.isEmpty(mCommentBean.techNo) ? "" : String.format("[%s]", mCommentBean.techNo));
        fillClubStar();

        mCommentItemDetailAdapter = new CommentItemDetailAdapter(this, mCommentBean.commentRateList);
        badCommentDetail.setLayoutManager(new GridLayoutManager(this, 2));
        badCommentDetail.setAdapter(mCommentItemDetailAdapter);
        tvCommentText.setText(TextUtils.isEmpty(mCommentBean.comment) ? "-" : mCommentBean.comment);
        if (mCommentBean.isAnonymous.equals("N") && isFromManager) {
            llCommentVisitBtn.setVisibility(View.VISIBLE);
        } else {
            llCommentVisitBtn.setVisibility(View.GONE);
        }
    }

    private void initCommentImpressionView(String impression) {
        if (TextUtils.isEmpty(impression)) {
            return;
        }
        String[] impressionArray = impression.split("、");
        llImpressionDetail.removeAllViews();
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(32, 0, 0, 0);
        for (int i = 0; i < impressionArray.length; i++) {
            TextView tv = new TextView(CommentDetailActivity.this);
            tv.setText(String.format("#%s", impressionArray[i]));
            tv.setBackgroundResource(R.drawable.bg_contact_mark);
            tv.setTextSize(14);
            tv.setTextColor(Color.parseColor("#ff9a0c"));
            tv.setPadding(14, 0, 14, 0);
            tv.setGravity(Gravity.CENTER);
            tv.setLayoutParams(lp);
            llImpressionDetail.addView(tv);
        }

    }

    private void fillClubStar() {
        int totalClubScore = 0;
        float clubAverageScore = 0;
        for (int i = 0; i < mCommentBean.commentRateList.size(); i++) {
            totalClubScore += mCommentBean.commentRateList.get(i).commentRate;
        }
        clubAverageScore = totalClubScore / 20f;
        float max = clubAverageScore / mCommentBean.commentRateList.size();
        tvCommentScoreNumber.setText(String.format("%1.1f", max));
        starBar.setStarMark(max);
    }


    @Override
    public void onRightImageClickedListener() {
        super.onRightImageClickedListener();
        changeCommentStatus(mCommentBean.id, "");
    }

    @OnClick(R2.id.rl_comment_user_info)
    public void onInfoViewClicked() {
        if (!isFromManager && mType.equals("1")) {
            showToast(ResourceUtils.getString(R.string.comment_anonymous_alter_message));
            return;
        }
        if (mCommentBean != null && TextUtils.isEmpty(mCommentBean.userId)) {
            XToast.show("该用户无详情信息");
            return;
        }
        if (mCommentBean.isAnonymous.equals("N")) {
            if (isFromManager) {
                CustomerInfoDetailActivity.StartCustomerInfoDetailActivity(this, mCommentBean.userId, ConstantResources.INTENT_TYPE_MANAGER, false);
            } else {
                CustomerInfoDetailActivity.StartCustomerInfoDetailActivity(this, mCommentBean.userId, ConstantResources.INTENT_TYPE_TECH, false);
            }

        } else {
            showToast(ResourceUtils.getString(R.string.comment_anonymous_alter_message));
        }

    }

    @OnClick(R2.id.btn_visit)
    public void onViewClicked() {
        showServiceOutMenu(mCommentBean.userId, mCommentBean.phoneNum, mCommentBean.userEmchatId, mCommentBean.userName, mCommentBean.avatarUrl, mCommentBean.id, mCommentBean.returnStatus);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnBinder.unbind();
        if (loadDataSubscription != null) {
            loadDataSubscription.unsubscribe();
        }
    }

    private void showServiceOutMenu(final String userId, String phone, final String emChatId, final String userName, final String userHeadImgUrl, final String commentId, final String returnStatus) {

        BottomPopupWindow popupWindow = BottomPopupWindow.getInstance(this, phone, emChatId, commentId, returnStatus, new BottomPopupWindow.OnRootSelectedListener() {
            @Override
            public void onItemSelected(ReturnVisitMenu rootMenu) {
                switch (rootMenu.getType()) {
                    case 1:
                        PermissionTool.requestPermission(CommentDetailActivity.this, new String[]{Manifest.permission.CALL_PHONE}, new String[]{"拨打电话"}, REQUEST_CODE_CALL_PERMISSION);
                        break;
                    case 2:
                        EventBus.getDefault().post(new UserInfoEvent(0, 1, new UserInfoBean(userId, emChatId, userName, userHeadImgUrl)));
                        break;
                    case 3:
                        changeCommentStatus(commentId, returnStatus);
                        break;
                    case 4:
                        changeCommentStatus(commentId, returnStatus);
                        break;

                }
            }
        });
        popupWindow.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CALL_PERMISSION) {
            if (resultCode == RESULT_OK) {
                toCallPhone();
            } else {
                Toast.makeText(this, "获取权限失败", Toast.LENGTH_SHORT).show();
            }
            return;
        }
    }

    public void toCallPhone() {
        Intent intent = new Intent(Intent.ACTION_CALL);
        Uri data = Uri.parse("tel:" + contactPhone);
        intent.setData(data);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        startActivity(intent);
    }

    private void changeCommentStatus(String commentId, final String toStatus) {
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
                    mCommentBean.returnStatus = "Y";
                    imgVisitMark.setVisibility(View.VISIBLE);
                } else if (toStatus.equals("Y")) {
                    mCommentBean.returnStatus = "N";
                    imgVisitMark.setVisibility(View.GONE);
                } else {
                    mCommentBean.status = "delete";
                    Toast.makeText(CommentDetailActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                    CommentDetailActivity.this.finish();
                }

            }

            @Override
            public void onCallbackError(Throwable e) {
                XLogger.i(">>>", e.getLocalizedMessage());
            }
        });
    }


}
