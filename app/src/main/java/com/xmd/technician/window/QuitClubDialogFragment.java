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

import com.xmd.technician.R;
import com.xmd.technician.common.Utils;
import com.xmd.technician.databinding.FragmentQuitClubBinding;
import com.xmd.technician.http.gson.QuitClubResult;
import com.xmd.technician.model.LoginTechnician;
import com.xmd.technician.msgctrl.RxBus;

import rx.Subscription;

/**
 * Created by heyangya on 17-1-16.
 */

public class QuitClubDialogFragment extends DialogFragment {
    private String mPassword;
    private QuitClubListener mListener;
    public ObservableField<String> errorString = new ObservableField<>();
    public ObservableBoolean okButtonEnable = new ObservableBoolean();
    public ObservableBoolean cancelButtonEnable = new ObservableBoolean(true);

    private Subscription mQuitClubSubscription;

    private FragmentQuitClubBinding mBinding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentQuitClubBinding.inflate(inflater, container, false);
        mBinding.setFragment(this);
        mQuitClubSubscription = RxBus.getInstance().toObservable(QuitClubResult.class).subscribe(
                this::doQuitClubResult);
        return mBinding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mQuitClubSubscription.unsubscribe();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setCancelable(false);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
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
        LoginTechnician.getInstance().exitClub(mPassword);
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
            Toast.makeText(getActivity(), getString(R.string.quit_club_success_tips), Toast.LENGTH_LONG).show();
            LoginTechnician.getInstance().onExitClub(result);
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


    public interface QuitClubListener {
        void onQuitClubSuccess();
    }

    public void setListener(QuitClubListener listener) {
        this.mListener = listener;
    }
}
