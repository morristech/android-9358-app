package com.xmd.chat.view;

import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.shidou.commonlibrary.widget.ScreenUtils;
import com.shidou.commonlibrary.widget.XToast;
import com.xmd.app.CommonNetService;
import com.xmd.app.beans.UserCredit;
import com.xmd.app.user.User;
import com.xmd.app.user.UserInfoService;
import com.xmd.app.user.UserInfoServiceImpl;
import com.xmd.chat.MessageManager;
import com.xmd.chat.NetService;
import com.xmd.chat.R;
import com.xmd.chat.beans.DiceGameRequestResult;
import com.xmd.chat.databinding.FragmentDiceGameSettingBinding;
import com.xmd.chat.message.DiceGameChatMessage;
import com.xmd.m.network.BaseBean;
import com.xmd.m.network.NetworkSubscriber;
import com.xmd.m.network.XmdNetwork;

import java.util.List;

import rx.Observable;
import rx.Subscription;

/**
 * Created by mo on 17-7-21.
 * 骰子积分游戏设定
 */

public class DiceGameSettingFragment extends DialogFragment {
    private final static String ARG_USER_ID = "arg_user_id";
    public boolean canInvite;
    private int selectValue;
    public ObservableBoolean loading = new ObservableBoolean();
    public ObservableField<String> error = new ObservableField<>();

    private FragmentDiceGameSettingBinding binding;

    private Subscription subscription;
    private Subscription inviteSubscription;

    public ObservableInt credit = new ObservableInt();

    private UserInfoService userInfoService = UserInfoServiceImpl.getInstance();
    private User user;

    public static DiceGameSettingFragment newInstance(String userId) {
        DiceGameSettingFragment fragment = new DiceGameSettingFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_USER_ID, userId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_dice_game_setting, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        binding.setData(this);
        String userId = getArguments().getString(ARG_USER_ID);
        user = userInfoService.getUserByUserId(userId);
        if (user == null) {
            XToast.show("参数错误，无法找到用户：" + userId);
            dismiss();
            return;
        }

        loadData();

        Window window = getDialog().getWindow();
        if (window != null) {
            window.getAttributes().width = ScreenUtils.getScreenWidth();
        }
        setCancelable(false);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (subscription != null) {
            subscription.unsubscribe();
        }
        if (inviteSubscription != null) {
            inviteSubscription.unsubscribe();
        }
    }

    private void loadData() {
        loading.set(true);
        error.set(null);
        Observable<BaseBean<List<UserCredit>>> observable = XmdNetwork.getInstance()
                .getService(CommonNetService.class)
                .getUserCredit(userInfoService.getCurrentUser().getClubId(), 1, 10);
        subscription = XmdNetwork.getInstance().request(observable, new NetworkSubscriber<BaseBean<List<UserCredit>>>() {
            @Override
            public void onCallbackSuccess(BaseBean<List<UserCredit>> result) {
                loading.set(false);
                List<UserCredit> creditList = result.getRespData();
                if (creditList == null || creditList.size() == 0) {
                    credit.set(0);
                } else {
                    credit.set(creditList.get(0).amount);
                    binding.setData(DiceGameSettingFragment.this);
                }
            }

            @Override
            public void onCallbackError(Throwable e) {
                loading.set(false);
                error.set(e.getMessage() + "\n点击重新加载");
            }
        });
    }

    public void reLoad() {
        if (error.get() == null) {
            return;
        }
        loadData();
    }

    public void selectCredit(int credit) {
        if (!canSelect(credit)) {
            return;
        }
        selectValue = credit;
        canInvite = true;
        binding.setData(this);
    }

    public boolean canSelect(int credit) {
        return this.credit.get() >= credit;
    }

    public boolean isSelect(int credit) {
        return selectValue == credit;
    }

    public void onClickCancel() {
        dismiss();
    }

    public void onClickOk() {
        loading.set(true);
        Observable<BaseBean<DiceGameRequestResult>> observable = XmdNetwork.getInstance()
                .getService(NetService.class)
                .diceGameRequest(user.getChatId(), userInfoService.getCurrentUser().getClubId(), selectValue);
        inviteSubscription = XmdNetwork.getInstance().request(observable, new NetworkSubscriber<BaseBean<DiceGameRequestResult>>() {
            @Override
            public void onCallbackSuccess(BaseBean<DiceGameRequestResult> result) {
                loading.set(false);
                DiceGameChatMessage message = DiceGameChatMessage.createMessage(
                        DiceGameChatMessage.STATUS_REQUEST,
                        userInfoService.getCurrentUser().getChatId(),
                        user.getChatId(),
                        result.getRespData().gameId,
                        selectValue);
                MessageManager.getInstance().sendMessage(message);
                dismiss();
            }

            @Override
            public void onCallbackError(Throwable e) {
                loading.set(false);
                XToast.show("发送邀请失败：" + e.getMessage());
            }
        });
    }
}
