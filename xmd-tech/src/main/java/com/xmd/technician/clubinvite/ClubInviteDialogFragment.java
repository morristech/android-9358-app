package com.xmd.technician.clubinvite;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.shidou.commonlibrary.widget.ScreenUtils;
import com.shidou.commonlibrary.widget.XToast;
import com.xmd.app.BaseActivity;
import com.xmd.app.Constants;
import com.xmd.app.widget.TelephoneDialogFragment;
import com.xmd.m.network.BaseBean;
import com.xmd.m.network.NetworkSubscriber;
import com.xmd.m.network.XmdNetwork;
import com.xmd.permission.event.EventRequestSyncPermission;
import com.xmd.technician.R;
import com.xmd.technician.clubinvite.beans.ClubInvite;
import com.xmd.technician.databinding.ClubInviteDialogBinding;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Arrays;

import rx.Observable;

/**
 * Created by mo on 17-8-11.
 * 邀请加入会所
 */

public class ClubInviteDialogFragment extends DialogFragment {
    private ClubInviteDialogBinding binding;

    public static ClubInviteDialogFragment newInstance(ClubInvite invite) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.EXTRA_DATA, invite);
        ClubInviteDialogFragment clubInviteDialogFragment = new ClubInviteDialogFragment();
        clubInviteDialogFragment.setArguments(bundle);
        return clubInviteDialogFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.club_invite_dialog, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        binding.setData((ClubInvite) getArguments().getSerializable(Constants.EXTRA_DATA));
        binding.setHandler(this);
    }

    public void onAccept(ClubInvite invite) {
        acceptOrRefuse(invite, "accept");
    }

    public void onRefuse(ClubInvite invite) {
        acceptOrRefuse(invite, "refuse");
    }

    private void acceptOrRefuse(ClubInvite invite, String operate) {
        Observable<BaseBean<ClubInvite>> observable = XmdNetwork.getInstance()
                .getService(NetService.class)
                .acceptOrRefuseInvite(invite.getId(), operate);
        ((BaseActivity) getActivity()).showLoading();
        XmdNetwork.getInstance().request(observable, new NetworkSubscriber<BaseBean<ClubInvite>>() {
            @Override
            public void onCallbackSuccess(BaseBean<ClubInvite> result) {
                ((BaseActivity) getActivity()).hideLoading();
                if (operate.equals("accept")) {
                    EventBus.getDefault().post(new EventRequestSyncPermission());
                }
                getDialog().dismiss();
            }

            @Override
            public void onCallbackError(Throwable e) {
                ((BaseActivity) getActivity()).hideLoading();
                ((BaseActivity) getActivity()).showToast("操作失败：" + e.getMessage());
            }
        });
    }

    public void onCall(ClubInvite invite) {
        String[] telephones = invite.getClubTelephone().split(",");
        if (telephones.length > 0) {
            ArrayList<String> phoneList = new ArrayList<>();
            phoneList.addAll(Arrays.asList(telephones));
            TelephoneDialogFragment fragment = TelephoneDialogFragment.newInstance(phoneList);
            fragment.show(getFragmentManager().beginTransaction(), TelephoneDialogFragment.class.getName());
        } else {
            XToast.show("会所没有设置电话!");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        WindowManager.LayoutParams lp = getDialog().getWindow().getAttributes();
        lp.width = ScreenUtils.getScreenWidth();
        getDialog().getWindow().setAttributes(lp);
        getDialog().getWindow().getDecorView().setBackgroundColor(0x00000000);

    }
}
