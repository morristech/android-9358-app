package com.shidou.commonlibrary.network;

import android.content.Context;
import android.content.SharedPreferences;

import com.shidou.commonlibrary.helper.XLogger;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by heyangya on 16-6-16.
 */
public class ServerAddressManager {
    private SharedPreferences mSharedPreferences;
    private static ServerAddressManager instance = new ServerAddressManager();
    private ConcurrentHashMap<String, String> mHosts = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Integer> mNeedSyncHosts = new ConcurrentHashMap<>();
    private Thread mSyncThread;

    public static ServerAddressManager getInstance() {
        return instance;
    }

    public void init(Context context) {
        mSharedPreferences = context.getSharedPreferences("server_address", Context.MODE_PRIVATE);
        Map<String, ?> map = mSharedPreferences.getAll();
        for (String key : map.keySet()) {
            String ip = mSharedPreferences.getString(key, null);
            if (ip != null) {
                mHosts.put(key, ip);
            }
        }
        if (mHosts.size() > 0) {
            for (String host : mHosts.keySet()) {
                mNeedSyncHosts.put(host, 0);
            }
        }
    }

    public String getHost(String host) {
        if (NetUtils.isIpV4Address(host)) {
            return host;
        }
        String ip = mHosts.get(host);
        if (ip == null) {
            if (!mNeedSyncHosts.contains(host)) {
                mNeedSyncHosts.put(host, 0);
            }
            return host;
        } else {
            return ip;
        }
    }

    public void sync(final SyncListener listener) {
        if (mSyncThread != null || mNeedSyncHosts.size() == 0) {
            return;
        }
        mSyncThread = new Thread() {
            @Override
            public void run() {
                try {
                    for (String host : mNeedSyncHosts.keySet()) {
                        try {
                            InetAddress inetAddress = InetAddress.getByName(host);
                            if (inetAddress != null) {
                                mHosts.put(host, inetAddress.getHostAddress());
                                mSharedPreferences.edit().putString(host, inetAddress.getHostAddress()).apply();
                                XLogger.i(host + "->" + inetAddress.getHostAddress());
                            }
                            mNeedSyncHosts.remove(host);
                        } catch (UnknownHostException e) {
                            e.printStackTrace();
                        }
                    }
                    if (listener != null) {
                        listener.onFinish();
                    }
                } finally {
                    mSyncThread = null;
                }
            }
        };
        mSyncThread.start();
    }

    public interface SyncListener {
        void onFinish();
    }
}
