package com.xmd.manager.window;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.crazyman.library.PermissionTool;
import com.xmd.manager.R;
import com.xmd.manager.adapter.CommentDetailItemAdapter;
import com.xmd.manager.beans.BadCommentDetailBean;
import com.xmd.manager.beans.CommentRateListBean;
import com.xmd.manager.common.DateUtil;
import com.xmd.manager.common.ResourceUtils;
import com.xmd.manager.common.ReturnVisitMenu;
import com.xmd.manager.common.Utils;
import com.xmd.manager.msgctrl.MsgDef;
import com.xmd.manager.msgctrl.MsgDispatcher;
import com.xmd.manager.msgctrl.RxBus;
import com.xmd.manager.service.RequestConstant;
import com.xmd.manager.service.response.BadCommentResult;
import com.xmd.manager.service.response.ChangeStatusResult;
import com.xmd.manager.widget.BottomPopupWindow;
import com.xmd.manager.widget.CircleImageView;
import com.xmd.manager.widget.StarBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

/**
 * Created by lhj on 2016/11/18.
 */
public class BadCommentDetailActivity extends BaseActivity {


    @BindView(R.id.comment_avatar)
    CircleImageView commentAvatar;
    @BindView(R.id.comment_customer_name)
    TextView commentCustomerName;
    @BindView(R.id.comment_time)
    TextView commentTime;
    @BindView(R.id.comment_customer_phone)
    TextView commentCustomerPhone;
    @BindView(R.id.customer_info)
    RelativeLayout customerInfo;
    @BindView(R.id.comment_type)
    TextView commentType;
    @BindView(R.id.comment_tech_name)
    TextView commentTechName;
    @BindView(R.id.comment_tech_code)
    TextView commentTechCode;
    @BindView(R.id.customer_comment_tech)
    LinearLayout customerCommentTech;
    @BindView(R.id.starBar)
    StarBar starBar;
    @BindView(R.id.comment_tv_tech_grade)
    TextView commentTvTechGrade;
    @BindView(R.id.ll_comment_tech)
    LinearLayout llCommentTech;
    @BindView(R.id.bad_comment_detail)
    RecyclerView badCommentDetail;
    @BindView(R.id.comment_tech_remark)
    TextView commentTechRemark;
    @BindView(R.id.comment_tech_grade)
    LinearLayout commentTechGrade;
    @BindView(R.id.comment_detail)
    TextView commentDetail;
    @BindView(R.id.button_return_visit)
    Button buttonReturnVisit;
    @BindView(R.id.menu_activity_logout)
    RelativeLayout menuActivityLogout;
    @BindView(R.id.total_score)
    TextView totalScore;
    private String mCommentId;
    private Boolean mIsDeleted;
    private Subscription getCommentDetailSubscription;
    private String mUserId;

    private String callPhone;
    private static final int REQUEST_CODE_PHONE = 0x0001;
    private BadCommentDetailBean badComment;
    private Subscription mBadCommentStatusSubscription;
    private CommentDetailItemAdapter mDetailItemAdapter;
    private List<CommentRateListBean> mCommentRateList;
    private boolean isAnonymous;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bad_comment_detail);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        mCommentId = getIntent().getStringExtra(RequestConstant.KEY_COMMENT_ID);
        mIsDeleted = getIntent().getBooleanExtra(RequestConstant.KEY_IS_DELETED, false);
        if (!mIsDeleted) {
            setRightVisible(true, ResourceUtils.getDrawable(R.drawable.icon_tabble_right_selected), view -> {
                        doRemarkComment(mCommentId, RequestConstant.DELETE_COMMENT);
                    }
            );
        } else {
            setRightVisible(false, "", null);
        }
        mCommentRateList = new ArrayList<>();
        mDetailItemAdapter = new CommentDetailItemAdapter(BadCommentDetailActivity.this, mCommentRateList);
        badCommentDetail.setLayoutManager(new GridLayoutManager(BadCommentDetailActivity.this, 2));
        badCommentDetail.setAdapter(mDetailItemAdapter);

        getCommentDetailSubscription = RxBus.getInstance().toObservable(BadCommentResult.class).subscribe(
                badCommentResult -> handleBadCommentResult(badCommentResult)
        );
        mBadCommentStatusSubscription = RxBus.getInstance().toObservable(ChangeStatusResult.class).subscribe(
                changeStatusResult -> {
                    if (changeStatusResult.statusCode == 200) {
                        makeShortToast(changeStatusResult.msg);
                        setRightVisible(false, "", null);
                    } else {
                        makeShortToast(changeStatusResult.msg);
                    }
                }
        );
        getBadCommentDetail();

    }

    private void handleBadCommentResult(BadCommentResult badCommentResult) {
        if (badCommentResult.statusCode == 200) {
            badComment = badCommentResult.respData;
            if (badComment == null) {
                return;
            }
            if (badComment.isAnonymous.equals("Y")) {
                isAnonymous = true;
                buttonReturnVisit.setVisibility(View.GONE);
            } else {
                isAnonymous = false;
                buttonReturnVisit.setVisibility(View.VISIBLE);
            }
            mUserId = badComment.userId;
            Glide.with(BadCommentDetailActivity.this).load(badComment.avatarUrl).into(commentAvatar);
            commentCustomerName.setText(badComment.userName);
            if (Utils.isNotEmpty(badComment.phoneNum)) {
                commentCustomerPhone.setText(badComment.phoneNum);
            }
            commentTime.setText(DateUtil.longToDate(badComment.createdAt));
            mDetailItemAdapter.setData(badCommentResult.respData.commentRateList);

            if (badComment.commentType.equals(RequestConstant.COMMENT_TYPE_ORDER) || badComment.commentType.equals(RequestConstant.COMMENT_TYPE_TECH)) {
                commentType.setText(ResourceUtils.getString(R.string.comment_text));

                initCommentTech(badComment);
            } else if (badComment.commentType.equals(RequestConstant.COMMENT_TYPE_CLUB)) {
                commentType.setText(ResourceUtils.getString(R.string.comment_text));
                initCommentClub(badComment);
            } else {
                if (Utils.isNotEmpty(badComment.techName)) {
                    commentTechName.setText(badComment.techName);
                }
                commentType.setText(ResourceUtils.getString(R.string.comment_qr_code));
                initCommentTech(badComment);
            }

        }

    }

    private void initCommentTech(BadCommentDetailBean badComment) {
        if (Utils.isNotEmpty(badComment.techName)) {
            commentTechName.setText(badComment.techName);
        } else {
            commentTechName.setText("");
        }

        String impression = badComment.impression;
        if (Utils.isNotEmpty(impression)) {
            commentTechRemark.setText("#" + impression.replaceAll("、", " #"));
        } else {
            commentTechRemark.setVisibility(View.GONE);
        }
        if (Utils.isNotEmpty(badComment.techNo)) {
            int startIndex = 1;
            int endIndex = startIndex + badComment.techNo.length();
            Spannable spanString = Utils.changeColor("[" + badComment.techNo + "]",
                    ResourceUtils.getColor(R.color.number_color), startIndex, endIndex);
            commentTechCode.setText(spanString);
            commentTechCode.setVisibility(View.VISIBLE);
        }
        if (Utils.isNotEmpty(badComment.comment)) {
            commentDetail.setText(badComment.comment);
        } else {
            commentDetail.setText("暂无");
        }
        fillTechStar(badComment);
    }

    private void initCommentClub(BadCommentDetailBean comment) {
        commentTechName.setText("会所");
        commentTechCode.setVisibility(View.GONE);

        if (Utils.isNotEmpty(comment.comment)) {
            commentDetail.setText(comment.comment);
        } else {
            commentDetail.setText("暂无");
        }
        fillClubStar(badComment);
    }

    private void fillClubStar(BadCommentDetailBean badComment) {
        int totalClubScore = 0;
        float clubAverageScore = 0;
        for (int i = 0; i < badComment.commentRateList.size(); i++) {
            totalClubScore += badComment.commentRateList.get(i).commentRate;
        }
        clubAverageScore = totalClubScore / 20f;
        float max = clubAverageScore / badComment.commentRateList.size();
        commentTvTechGrade.setText(String.format("%1.2f", max));
        starBar.setStarMark(max);
    }


    private void fillTechStar(BadCommentDetailBean comment) {
        float max = comment.techScore / 20f;
        commentTvTechGrade.setText(String.format("%1.2f", max));
        starBar.setStarMark(max);
    }

    @OnClick({R.id.button_return_visit, R.id.customer_info})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.customer_info:
                if (isAnonymous) {
                    makeShortToast("匿名用户无法查看详情");
                } else {
                    Intent intent = new Intent(BadCommentDetailActivity.this, CustomerActivity.class);
                    intent.putExtra(RequestConstant.COMMENT_USER_ID, mUserId);
                    startActivity(intent);
                }

                break;
            case R.id.button_return_visit:
                showServiceOutMenu(badComment.phoneNum, badComment.userEmchatId, badComment.userName, badComment.avatarUrl, badComment.id, badComment.returnStatus);
                break;
        }
    }

    private void showServiceOutMenu(String phone, String emChatId, String userName, String userHeadImgUrl, String commentId, String returnStatus) {

        BottomPopupWindow popupWindow = BottomPopupWindow.getInstance(this, phone, emChatId, commentId, returnStatus, new BottomPopupWindow.OnRootSelectedListener() {
            @Override
            public void onItemSelected(ReturnVisitMenu rootMenu) {
                switch (rootMenu.getType()) {
                    case 1:
                        PermissionTool.requestPermission(BadCommentDetailActivity.this, new String[]{Manifest.permission.CALL_PHONE}, new String[]{"拨打电话"}, REQUEST_CODE_PHONE);
                        break;
                    case 2:
//                        EmchatUserHelper.startToChat(emChatId, userName, userHeadImgUrl);
                        break;
                    case 3:
                        doRemarkComment(commentId, RequestConstant.FINISH_COMMENT);
                        break;
                    case 4:
                        doRemarkComment(commentId, RequestConstant.VALID_COMMENT);
                        break;

                }
            }
        });
        popupWindow.show();
    }

    private void doRemarkComment(String commentId, String status) {
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_BAD_COMMENT_ID, commentId);
        params.put(RequestConstant.KEY_COMMENT_STATUS, status);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_CHANGE_COMMENT_STATUS, params);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unsubscribe(getCommentDetailSubscription, mBadCommentStatusSubscription);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PHONE) {
            if (resultCode == RESULT_OK) {
                callPhone = ResourceUtils.getString(R.string.service_phone);
                if (Utils.isNotEmpty(callPhone)) {
                    toCallPhone(callPhone);
                } else {
                    Utils.makeShortToast(this, ResourceUtils.getString(R.string.phone_is_null));
                }
            } else {
                Toast.makeText(this, "获取权限失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void toCallPhone(String servicePhone) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        Uri data = Uri.parse("tel:" + servicePhone);
        intent.setData(data);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        startActivity(intent);
    }

    public void getBadCommentDetail() {
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_BAD_COMMENT_ID, mCommentId);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_BAD_COMMENT_DETAIL, params);
    }
}
