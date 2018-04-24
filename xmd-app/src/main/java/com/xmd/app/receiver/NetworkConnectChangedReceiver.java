package com.xmd.app.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.webkit.CookieManager;

import com.shidou.commonlibrary.helper.XLogger;

/**
 * Created by xmd on 2018/4/23.
 */

public class NetworkConnectChangedReceiver extends BroadcastReceiver{

    private   NetWorkStatusListener netWorkListener;

    public void initNetWorkListener(NetWorkStatusListener listener){
        this.netWorkListener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())){
            NetworkInfo info = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
            if(info != null){
                if(NetworkInfo.State.CONNECTED == info.getState() && info.isAvailable()){
                    if(info.getType() == ConnectivityManager.TYPE_WIFI || info.getType() == ConnectivityManager.TYPE_MOBILE){
                        netWorkListener.netWorkUsable(true);
                    }
                }else{
                    netWorkListener.netWorkUsable(false);
                }
            }
        }
    }

}
