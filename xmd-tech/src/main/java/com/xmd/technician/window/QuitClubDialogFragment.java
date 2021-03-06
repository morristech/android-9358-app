package com.xmd.technician.window;

import android.app.Dialog;
import android.content.Context;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.technician.Constant;
import com.xmd.technician.R;
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.common.Utils;
import com.xmd.technician.databinding.FragmentQuitClubBinding;
import com.xmd.technician.http.gson.AuditCancelResult;
import com.xmd.technician.http.gson.QuitClubResult;
import com.xmd.technician.model.LoginTechnician;
import com.xmd.technician.msgctrl.RxBus;

import rx.Subscription;

/**
 * Created by heyangya on 17-1-16.
 */

public class QuitClubDialogFragment extends DialogFragment {
    public ObservableField<String> errorString = new ObservableField<>();
    public ObservableBoolean okButtonEnable = new ObservableBoolean();
    public ObservableBoolean cancelButtonEnable = new ObservableBoolean(true);
    private String mPassword;
    private QuitClubListener mListener;
    private Subscription mQuitClubSubscription;
    private Subscription mAuditCancelSubscription;

    private FragmentQuitClubBinding mBinding;

    private LoginTechnician technician = LoginTechnician.getInstance();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentQuitClubBinding.inflate(inflater, container, false);
        mBinding.setFragment(this);
        if (technician.getStatus().equals(Constant.TECH_STATUS_UNCERT)) {
            mBinding.dialogTitle.setText(ResourceUtils.getString(R.string.cancel_join_club_title));
            mBinding.tvDialogDes.setText(ResourceUtils.getString(R.string.cancel_join_club_tip));
        } else {
            mBinding.dialogTitle.setText(ResourceUtils.getString(R.string.quit_club_title));
            mBinding.tvDialogDes.setText(ResourceUtils.getString(R.string.quit_club_tip));
        }
        mQuitClubSubscription = RxBus.getInstance().toObservable(QuitClubResult.class).subscribe(
                this::doQuitClubResult);
        mAuditCancelSubscription = RxBus.getInstance().toObservable(AuditCancelResult.class).subscribe(
                this::doCancelJoinClubResult);
        return mBinding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mQuitClubSubscription.unsubscribe();
        mAuditCancelSubscription.unsubscribe();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().setCancelable(false);
    }

    public void onClickOk() {
        errorString.set(null);
        if (TextUtils.isEmpty(mPassword)) {
            errorString.set("请先输入密码!");
            return;
        }
        if (!Utils.matchLoginPassword(mPassword)) {
            errorString.set("密码输入错误!");
            return;
        }
        errorString.set("正在向服务器发送请求...");
        okButtonEnable.set(false);
        cancelButtonEnable.set(false);
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(mBinding.editPassword.getWindowToken(), 0);
        technician.exitClub(mPassword);
    }

    public void onClickCancel() {
        getDialog().dismiss();
    }


    //退出会所成功
    private void doQuitClubResult(QuitClubResult result) {
        okButtonEnable.set(true);
        cancelButtonEnable.set(true);
        if (result.statusCode < 200 || result.statusCode > 299) {
            errorString.set("请求失败：" + result.msg);
        } else {
            errorString.set(null);
            if (technician.isActiveStatus()) {
                Toast.makeText(getActivity(), getString(R.string.quit_club_success_tips), Toast.LENGTH_LONG).show();
            } else if (technician.isVerifyStatus()) {
                Toast.makeText(getActivity(), getString(R.string.cancel_join_success_tips), Toast.LENGTH_LONG).show();
            }
            LoginTechnician.getInstance().onExitClub(result);
            if (mListener != null) {
                mListener.onQuitClubSuccess();
            }
            getDialog().dismiss();
        }
    }

    //取消加入会所申请
    private void doCancelJoinClubResult(AuditCancelResult result) {
        okButtonEnable.set(true);
        cancelButtonEnable.set(true);
        if (result.statusCode < 200 || result.statusCode > 299) {
            errorString.set("请求失败：" + result.msg);
        } else {
            errorString.set(null);
            Toast.makeText(getActivity(), getString(R.string.cancel_join_success_tips), Toast.LENGTH_LONG).show();
            LoginTechnician.getInstance().onExitClub(null);
            if (mListener != null) {
                mListener.onQuitClubSuccess();
            }
            getDialog().dismiss();
        }
    }

    public void afterPasswordTextChanged(Editable s) {
        mPassword = s.toString();
        if (!Utils.matchLoginPassword(mPassword)) {
            okButtonEnable.set(false);
        } else {
            okButtonEnable.set(true);
        }
    }

    public void setListener(QuitClubListener listener) {
        this.mListener = listener;
    }

    public interface QuitClubListener {
        void onQuitClubSuccess();
    }
}
