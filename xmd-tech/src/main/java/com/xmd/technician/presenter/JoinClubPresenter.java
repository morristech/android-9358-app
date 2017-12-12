package com.xmd.technician.presenter;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;

import com.xmd.technician.common.UINavigation;
import com.xmd.technician.common.Utils;
import com.xmd.technician.contract.JoinClubContract;
import com.xmd.technician.databinding.ActivityJoinClubBinding;
import com.xmd.technician.event.EventRequestJoinClub;
import com.xmd.technician.http.gson.AuditModifyResult;
import com.xmd.technician.http.gson.JoinClubResult;
import com.xmd.technician.http.gson.RoleListResult;
import com.xmd.technician.model.LoginTechnician;
import com.xmd.technician.model.TechNo;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;
import com.xmd.technician.widget.AlertDialogBuilder;
import com.xmd.technician.window.TechNoDialogFragment;

import java.util.List;

import rx.Subscription;

/**
 * Created by heyangya on 16-12-20.
 */

public class JoinClubPresenter extends BasePresenter<JoinClubContract.View> implements JoinClubContract.Presenter {
    private ActivityJoinClubBinding mBinding;
    private LoginTechnician mTech = LoginTechnician.getInstance();
    public ObservableBoolean mCanJoin = new ObservableBoolean();
    public ObservableField<String> mTechNo = new ObservableField<>();
    public boolean mShowSkip;
    public boolean mShowBack;
    private String mInviteCode;
    private String mSelectedTechNo;
    private String mSelectedTechId;
    private TechNoDialogFragment mTechNoDialogFragment;
    private Subscription mSubscription;
    private Subscription mRoleListSubscription;
    private Subscription mModifySubscription;
    private int mOpenFrom;
    private List<RoleListResult.Item> mRoleList;
    private RoleListResult.Item mSelectRole;

    public JoinClubPresenter(Context context, JoinClubContract.View view, ActivityJoinClubBinding binding) {
        super(context, view);
        mBinding = binding;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mOpenFrom = mView.getIntent().getIntExtra(UINavigation.EXTRA_OPEN_JOIN_CLUB_FROM, UINavigation.OPEN_JOIN_CLUB_FROM_MAIN);
        if (mOpenFrom == UINavigation.OPEN_JOIN_CLUB_FROM_START) {
            mShowSkip = true;
            mShowBack = false;
        }

        mTechNo.set("选择技师编号");
        mBinding.setPresenter(this);
        mSubscription = RxBus.getInstance().toObservable(JoinClubResult.class)
                .subscribe(this::handleJoinClubResult);
        mRoleListSubscription = RxBus.getInstance().toObservable(RoleListResult.class)
                .subscribe(this::handleGetRoleList);
        mModifySubscription = RxBus.getInstance().toObservable(AuditModifyResult.class)
                .subscribe(this::handleModifyResult);
        mView.showLoading("加载角色列表");
        if (!TextUtils.isEmpty(mTech.getClubInviteCode())) {
            mInviteCode = mTech.getClubInviteCode();
            mBinding.tvTitle.setText(" 修改申请");
            mBinding.btnSure.setText("确定");
            mBinding.phoneNumber.setText(mTech.getClubInviteCode());
            mBinding.phoneNumber.setEnabled(false);
            mBinding.phoneNumber.setFocusable(false);
        } else {
            mBinding.tvTitle.setText("加入会所");
            mBinding.btnSure.setText("申请加入");
        }
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_ROLE_LIST);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unsubscribe(mSubscription, mRoleListSubscription);
    }


    //点击跳过
    @Override
    public void onClickSkip() {
        new AlertDialogBuilder(mContext)
                .setMessage("暂时不加入会所?")
                .setNegativeButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        UINavigation.gotoMainActivityFromStart(mContext);
                        mView.finishSelf();
                    }
                })
                .setPositiveButton("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                })
                .show();
    }

    @Override
    public void onClickJoin() {
        if (mSelectRole == null) {
            mView.showToast("角色加载失败，无法加入会所！");
            return;
        }
        mView.showLoading("正在提交申请...");
        if(TextUtils.isEmpty(mTech.getClubInviteCode())){
            mTech.sendJoinClubRequest(mInviteCode, mSelectedTechId, mSelectRole.code);
        }else {
            mTech.sendJoinClubAuditModify(mSelectedTechId, mSelectRole.code);
        }

    }

    private void handleGetRoleList(RoleListResult result) {
        mView.hideLoading();
        if (result.statusCode != 200) {
            mView.showToast("加载角色列表失败：" + result.statusCode + "," + result.msg);
            return;
        }
        if (result.respData == null || result.respData.size() == 0) {
            mView.showToast("角色列表为空！");
            return;
        }
        mRoleList = result.respData;
        mSelectRole = mRoleList.get(0);
        mView.showRoleList(mRoleList, 0);
    }

    private void handleJoinClubResult(JoinClubResult result) {
        mView.hideLoading();
        if (result.statusCode > 299 || (result.statusCode < 200 && result.statusCode != 0)) {
            mView.showAlertDialog(result.msg);
        } else {
            //申请加入成功，跳转到完善资料页面
            mView.showToast("申请成功，等待管理员审核");
            mTech.onSendJoinClubRequest(mInviteCode, TechNo.DEFAULT_TECH_NO.name.equals(mSelectedTechNo) ? null : mSelectedTechNo, result);
            mView.setResult(Activity.RESULT_OK, null);
            mView.finishSelf();
            if (mOpenFrom == UINavigation.OPEN_JOIN_CLUB_FROM_START) {
                UINavigation.gotoMainActivityFromStart(mContext);
            }
            RxBus.getInstance().post(new EventRequestJoinClub());
        }
    }

    private void handleModifyResult(AuditModifyResult result) {
        mView.hideLoading();
        if (result.statusCode > 299 || (result.statusCode < 200 && result.statusCode != 0)) {
            mView.showAlertDialog(result.msg);
        } else {
            //申请加入成功，跳转到完善资料页面
            mView.showToast("申请成功，等待管理员审核");
            mTech.onModifyRequest(mSelectedTechNo);
            mView.setResult(Activity.RESULT_OK, null);
            mView.finishSelf();
            RxBus.getInstance().post(new EventRequestJoinClub());
        }
    }

    @Override
    public void setInviteCode(Editable s) {
        mInviteCode = s.toString();
        checkCanJoin();
        if (!TextUtils.isEmpty(mSelectedTechNo)) {
            mSelectedTechNo = null;
            mSelectedTechId = null;
            mTechNo.set("选择技师编号");
        }
    }

    private void checkCanJoin() {
        mCanJoin.set(Utils.matchClubInviteCode(mInviteCode) && !TextUtils.isEmpty(mSelectedTechNo));
    }

    @Override
    public void onClickBack() {
        if (mShowSkip) {
            //禁止返回键
        } else {
            mView.finishSelf();
        }
    }

    @Override
    public void onClickShowTechNos() {
        if (TextUtils.isEmpty(mTech.getClubInviteCode()) && !Utils.matchClubInviteCode(mInviteCode)) {
            mView.showToast("请先填写正确的会所邀请码！");
        } else {
            FragmentManager fragmentManager = ((Activity) mContext).getFragmentManager();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            Fragment prev = fragmentManager.findFragmentByTag("tech_no_dialog");
            if (prev != null) {
                ft.remove(prev);
            }
            mTechNoDialogFragment = TechNoDialogFragment.newInstance();
            mTechNoDialogFragment.setPresenter(this);
            mTechNoDialogFragment.setParams(mInviteCode, mSelectedTechNo);
            mTechNoDialogFragment.show(ft, "tech_no_dialog");
        }
    }

    @Override
    public void onSelectTechNo(TechNo techNo) {
        mTechNoDialogFragment.dismiss();
        mTechNo.set(techNo.name);
        mSelectedTechNo = techNo.name;
        mSelectedTechId = techNo.id;
        checkCanJoin();
    }

    @Override
    public void onSelectRole(int selectPosition) {
        mSelectRole = mRoleList.get(selectPosition);
    }
}
