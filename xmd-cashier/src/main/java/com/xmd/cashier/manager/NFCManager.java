package com.xmd.cashier.manager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcB;
import android.nfc.tech.NfcF;
import android.nfc.tech.NfcV;
import android.os.Build;
import android.util.Log;

import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.cashier.common.HexUtils;

import java.io.IOException;

public class NFCManager {
    private static NFCManager instance;
    private NfcAdapter nfcAdapter;
    private PendingIntent mPendingIntent;
    private IntentFilter[] mFilters;
    private String[][] mTechLists;
    // 是否是NFC请求
    private boolean isNfcRequest = false;
    private NFCListener mNFCListener;
    private Activity mActivity;

    private NFCManager() {
    }

    public static NFCManager getInstance() {
        if (instance == null)
            instance = new NFCManager();
        return instance;
    }

    @SuppressLint("NewApi")
    public void init(Context context) {
        if (Build.VERSION.SDK_INT < 10) {
            return;
        }
        try {
            nfcAdapter = NfcAdapter.getDefaultAdapter(context);
        } catch (Exception e1) {
            e1.printStackTrace();
            nfcAdapter = null;
            return;
        }

        if (nfcAdapter == null) {
            // 如果手机不支持NFC，或者NFC没有打开就直接返回
            Log.d(this.getClass().getName(), "手机不支持NFC功能！");
            return;
        }

        // 三种Activity NDEF_DISCOVERED ,TECH_DISCOVERED,TAG_DISCOVERED
        // 指明的先后顺序非常重要， 当Android设备检测到有NFC Tag靠近时，会根据Action申明的顺序给对应的Activity
        // 发送含NFC消息的 Intent.
        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        IntentFilter tech = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        IntentFilter tag = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);

        try {
            ndef.addDataType("*/*");
        } catch (MalformedMimeTypeException e) {
            throw new RuntimeException("fail", e);
        }
        mFilters = new IntentFilter[]{ndef, tech, tag};
        mTechLists = new String[][]{new String[]{Ndef.class.getName(), MifareClassic.class.getName(),
                NfcA.class.getName(), NfcB.class.getName(), NfcV.class.getName(), NfcF.class.getName()}};

        if (!nfcAdapter.isEnabled()) {
            Log.d(this.getClass().getName(), "手机NFC功能没有打开！");
            return;
        }
    }

    @SuppressLint("NewApi")
    public void onPause(Activity activity) {
        if (nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(activity);
        }
        mActivity = null;
    }

    @SuppressLint("NewApi")
    public void onResume(Activity activity) {
        if (nfcAdapter != null) {
            if (mActivity == null) {
                mActivity = activity;
                mPendingIntent = PendingIntent.getActivity(mActivity, 0,
                        new Intent(mActivity, mActivity.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
                nfcAdapter.enableForegroundDispatch(activity, mPendingIntent, mFilters, mTechLists);
            } else {
                if (!mActivity.getClass().equals(activity.getClass())) {
                    mActivity = activity;
                    mPendingIntent = PendingIntent.getActivity(mActivity, 0,
                            new Intent(mActivity, mActivity.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
                    nfcAdapter.enableForegroundDispatch(activity, mPendingIntent, mFilters, mTechLists);
                }
            }
        }
    }

    public boolean isEnabled() {
        return nfcAdapter != null && nfcAdapter.isEnabled();
    }

    /**
     * 处理cup NFC卡
     *
     * @param intent
     * @throws IOException
     */
    @SuppressLint("NewApi")
    public void procNFCIntent(Intent intent) throws IOException {
        if (isEnabled()) {
            // TODO 需要解析ID为指定格式内容
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            XLogger.e(new String(tag.getId()));
            mNFCListener.onReceiveDataOffline(HexUtils.bytesToHex(tag.getId()));
        } else {
            if (mNFCListener != null) {
                mNFCListener.onError("NFC不可用");
            }
        }
    }

    public void clearNFCParams() {
        isNfcRequest = false;
    }

    public void setNFCListener(NFCListener ls) {
        mNFCListener = ls;
    }

    public interface NFCListener {
        void onReceiveDataOffline(String id);

        void onError(String error);
    }
}
