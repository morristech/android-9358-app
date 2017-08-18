package com.xmd.black;

import com.shidou.commonlibrary.widget.XToast;
import com.xmd.black.bean.AddToBlacklistResult;
import com.xmd.black.bean.InBlacklistResult;
import com.xmd.black.bean.InUserBlacklistResult;
import com.xmd.black.bean.RemoveFromBlacklistResult;
import com.xmd.black.event.AddOrRemoveBlackEvent;
import com.xmd.black.event.InCustomerBlackListEvent;
import com.xmd.black.event.InUserBlackListEvent;
import com.xmd.black.httprequest.DataManager;
import com.xmd.m.network.NetworkSubscriber;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Lhj on 17-7-24.
 */

public class BlackListManager {

    private static final BlackListManager ourInstance = new BlackListManager();

    public static BlackListManager getInstance() {
        return ourInstance;
    }

    private BlackListManager() {
    }

    public void addUserToBlack(String friendId) {
        DataManager.getInstance().addToBlackList(friendId, new NetworkSubscriber<AddToBlacklistResult>() {
            @Override
            public void onCallbackSuccess(AddToBlacklistResult result) {
                EventBus.getDefault().post(new AddOrRemoveBlackEvent(true, true, result.getMsg()));
            }

            @Override
            public void onCallbackError(Throwable e) {
                EventBus.getDefault().post(new AddOrRemoveBlackEvent(false, true, e.getLocalizedMessage()));
            }
        });
    }

    public void removeUserFromBlackList(final String friendId) {
        DataManager.getInstance().removeFromBlackList(friendId, new NetworkSubscriber<RemoveFromBlacklistResult>() {
            @Override
            public void onCallbackSuccess(RemoveFromBlacklistResult result) {
                EventBus.getDefault().post(new AddOrRemoveBlackEvent(true, false, result.getMsg()));
            }

            @Override
            public void onCallbackError(Throwable e) {
                EventBus.getDefault().post(new AddOrRemoveBlackEvent(false, false, e.getLocalizedMessage()));
            }
        });
    }

    public void isInBlackList(String friendId) {
        DataManager.getInstance().loadBooleanInBlackList(friendId, new NetworkSubscriber<InBlacklistResult>() {
            @Override
            public void onCallbackSuccess(InBlacklistResult result) {
                EventBus.getDefault().post(new InUserBlackListEvent((Boolean) result.getRespData(), result.getMsg()));
            }

            @Override
            public void onCallbackError(Throwable e) {
                //        EventBus.getDefault().post(new InUserBlackListEvent(false, e.getLocalizedMessage()));
                XToast.show(e.getLocalizedMessage());
            }
        });
    }

    public void judgeInCustomerBlackList(String chatId) {
        DataManager.getInstance().judgeInCustomerBlackList(chatId, new NetworkSubscriber<InUserBlacklistResult>() {
            @Override
            public void onCallbackSuccess(InUserBlacklistResult result) {
                EventBus.getDefault().post(new InCustomerBlackListEvent((Boolean) result.getRespData(), result.getMsg()));
            }

            @Override
            public void onCallbackError(Throwable e) {
                //        EventBus.getDefault().post(new InCustomerBlackListEvent(false, e.getLocalizedMessage()));
                XToast.show(e.getLocalizedMessage());
            }
        });
    }
}
