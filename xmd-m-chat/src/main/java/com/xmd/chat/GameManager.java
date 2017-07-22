package com.xmd.chat;

import com.xmd.chat.beans.DiceGameSetting;
import com.xmd.m.network.BaseBean;
import com.xmd.m.network.NetworkSubscriber;
import com.xmd.m.network.XmdNetwork;

import rx.Observable;

/**
 * Created by mo on 17-7-22.
 */

public class GameManager {
    private static final GameManager ourInstance = new GameManager();

    public static GameManager getInstance() {
        return ourInstance;
    }

    private long diceExpireTime;

    private GameManager() {
        diceExpireTime = XmdChat.getInstance().getSp().getLong(ChatConstants.SP_GAME_DICE_EXPIRE_TIME, 3600 * 1000);
        diceExpireTime = 20 * 1000;//FIXME for test
    }

    public long getDiceExpireTime() {
        return diceExpireTime;
    }

    public void loadDiceExpireTime() {
        Observable<BaseBean<DiceGameSetting>> observable = XmdNetwork.getInstance()
                .getService(NetService.class)
                .getGameSetting();
        XmdNetwork.getInstance().request(observable, new NetworkSubscriber<BaseBean<DiceGameSetting>>() {
            @Override
            public void onCallbackSuccess(BaseBean<DiceGameSetting> result) {
                //FIXME
//                diceExpireTime =result.getRespData().diceGameExpireTime;
                XmdChat.getInstance().getSp().edit().putLong(ChatConstants.SP_GAME_DICE_EXPIRE_TIME, diceExpireTime).apply();
            }

            @Override
            public void onCallbackError(Throwable e) {

            }
        });
    }
}
