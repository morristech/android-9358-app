package com.xmd.technician.onlinepaynotify.model;


import android.text.TextUtils;

import com.xmd.technician.Constant;
import com.xmd.technician.SharedPreferenceHelper;
import com.xmd.technician.common.Callback;
import com.xmd.technician.common.DateUtils;
import com.xmd.technician.common.Logger;
import com.xmd.technician.common.ThreadManager;
import com.xmd.technician.event.EventLogout;
import com.xmd.technician.http.RetrofitServiceFactory;
import com.xmd.technician.http.gson.CheckPayNotifyResult;
import com.xmd.technician.http.gson.GetPayNotifyListResult;
import com.xmd.technician.model.LoginTechnician;
import com.xmd.technician.msgctrl.RxBus;
import com.xmd.technician.onlinepaynotify.event.PayNotifyArchiveEvent;
import com.xmd.technician.onlinepaynotify.event.PayNotifyNewDataEvent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by heyangya on 17-1-17.
 */
public class PayNotifyInfoManager extends Observable {
    private static PayNotifyInfoManager ourInstance = new PayNotifyInfoManager();

    public static PayNotifyInfoManager getInstance() {
        return ourInstance;
    }

    private PayNotifyInfoManager() {
        mData = new ArrayList<>();
        mRecentArchivedMaps = new HashMap<>();
        String hideIds = SharedPreferenceHelper.getPayNotifyHideIds();
        if (!TextUtils.isEmpty(hideIds)) {
            String[] ids = hideIds.split(",");
            long limitTime = System.currentTimeMillis() - Constant.PAY_NOTIFY_MAIN_PAGE_TIME_LIMIT;
            for (int i = 0; i < ids.length / 3; i++) {
                String id = ids[3 * i];
                long time = Long.parseLong(ids[3 * i + 1]);
                if (time < limitTime) {
                    continue;
                }
                int status = Integer.parseInt(ids[3 * i + 2]);
                mRecentArchivedMaps.put(id, new ArchiveData(id, time, status));
            }
        }

        RxBus.getInstance().toObservable(EventLogout.class).subscribe(this::handleLogoutEvent);
    }

    private List<PayNotifyInfo> mData;
    private long mCurrentStartTime = -1;
    private long mCurrentEndTime = -1;
    private Map<String, ArchiveData> mRecentArchivedMaps;
    private Call<GetPayNotifyListResult> mDataGetCall;
    private Call<CheckPayNotifyResult> mDataCheckCall;
    private final Object mDataLocker = new Object();

    private static class ArchiveData {
        public String id;
        public long payTime;
        public int status;

        public ArchiveData(String id, long payTime, int status) {
            this.id = id;
            this.payTime = payTime;
            this.status = status;
        }
    }

    /**
     * 获取数据
     *
     * @param forceNetwork    强制从网络下载
     * @param startTime       开始时间
     * @param endTime         结束时间
     * @param status          状态
     * @param onlyNotArchived 只需要非归档的数据
     * @param limitCount      限制数量
     * @param callback        通知函数
     */
    public void getNotifyInfo(boolean forceNetwork, final long startTime, final long endTime, final int status, final boolean onlyNotArchived, final int limitCount, Callback<List<PayNotifyInfo>> callback) {
        if (forceNetwork || mCurrentStartTime == -1 || startTime < mCurrentStartTime || endTime > mCurrentEndTime) {
            loadDataFromNetwork(startTime, endTime, new Callback<List<PayNotifyInfo>>() {
                @Override
                public void onResult(Throwable error, List<PayNotifyInfo> result) {
                    if (result != null) {
                        result.clear();
                    }
                    if (error == null) {
                        if (mData.size() > 0) {
                            result = getDataByFilter(startTime, endTime, status, onlyNotArchived, limitCount);
                        }
                        if (mCurrentStartTime == -1 || mCurrentStartTime > startTime) {
                            mCurrentStartTime = startTime;
                        }
                        if (mCurrentEndTime == -1 || mCurrentEndTime < endTime) {
                            mCurrentEndTime = endTime;
                        }
                    }
                    callback.onResult(error, result);
                }
            });
        } else {
            callback.onResult(null, getDataByFilter(startTime, endTime, status, onlyNotArchived, limitCount));
        }
    }

    private List<PayNotifyInfo> getDataByFilter(long startTime, long endTime, int status, boolean onlyNotArchived, int limitCount) {
        int count = 0;
        List<PayNotifyInfo> result = new ArrayList<>();
        long limitTime = System.currentTimeMillis() - Constant.PAY_NOTIFY_MAIN_PAGE_TIME_LIMIT;
        boolean needSaveArchived = false;
        for (PayNotifyInfo info : mData) {
            //检查一次数据，早于限制时间时自动归档，并且从最近归档列表中移除
            if (info.payTime < limitTime) {
                info.isArchived = true;
                mRecentArchivedMaps.remove(String.valueOf(info.id));
                needSaveArchived = true;
            }
            if (info.payTime >= startTime && info.payTime < endTime
                    && (info.status & status) != 0
                    && (!onlyNotArchived || !info.isArchived)) {
                result.add(info);
                count++;
                if (count >= limitCount) {
                    if (needSaveArchived) {
                        saveArchivedIds();
                    }
                    return result;
                }
            }
        }
        if (needSaveArchived) {
            saveArchivedIds();
        }
        return result;
    }


    //从网络加载数据
    public void loadDataFromNetwork(long startTime, long endTime, Callback<List<PayNotifyInfo>> callback) {
//        new Thread() {
//            @Override
//            public void run() {
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                List<PayNotifyInfo> data = new ArrayList<>();
//                int mockCount = 10;
//                for (int i = 0; i < mockCount; i++) {
//                    PayNotifyInfo info = new PayNotifyInfo();
//                    info.id = i + 1;
//                    info.userName = "顾客" + i;
//                    info.amount = (long) (Math.random() * 1000);
//                    info.isArchived = false;
//                    long currentTime = 1485057077000L;
//                    info.payTime = System.currentTimeMillis() - i * 10 * 3600 * 1000;
//
//                    info.status = PayNotifyInfo.STATUS_ACCEPTED;
//                    info.userAvatar = "http://img3.duitang.com/uploads/item/201608/21/20160821200538_vHxLi.thumb.700_0.jpeg";
//                    info.combineTechs = new ArrayList<>();
//                    info.combineTechs.add("A00" + i);
//                    info.combineTechs.add("A100");
//                    data.add(info);
//                }
//
//                ThreadManager.postRunnable(ThreadManager.THREAD_TYPE_MAIN, new Runnable() {
//                    @Override
//                    public void run() {
//                        processNewData(data);
//                        callback.onResult(null, data);
//                    }
//                });
//            }
//        }.start();
        if (mDataGetCall != null) {
            mDataGetCall.cancel();
        }
        String startDate = DateUtils.getSdf("yyyy-MM-dd").format(new Date(startTime));
        String endDate = DateUtils.getSdf("yyyy-MM-dd").format(new Date(endTime));
        mDataGetCall = RetrofitServiceFactory.getSpaService()
                .getPayNotifyList(SharedPreferenceHelper.getUserToken(), startDate, endDate);
        mDataGetCall.enqueue(new retrofit2.Callback<GetPayNotifyListResult>() {
            @Override
            public void onResponse(Call<GetPayNotifyListResult> call, Response<GetPayNotifyListResult> response) {
                List<PayNotifyInfo> data = new ArrayList<>();
                GetPayNotifyListResult result = response.body();
                if (result != null) {
                    SimpleDateFormat sdf = DateUtils.getSdf("yyyy-MM-dd HH:mm:ss");
                    for (GetPayNotifyListResult.Item item : response.body().respData) {
                        PayNotifyInfo info = new PayNotifyInfo();
                        info.id = item.id;
                        try {
                            info.payTime = sdf.parse(item.createTime).getTime();
                        } catch (ParseException e) {
                            //时间 解析出错，设置为当前时间
                            info.payTime = System.currentTimeMillis();
                            Logger.e("parse time error:" + item.createTime);
                        }
                        info.amount = item.payAmount;
                        info.userName = item.userName;
                        info.userAvatar = item.userAvatarUrl;
                        info.combineTechs = new ArrayList<>();
                        if (!TextUtils.isEmpty(item.otherTechNames)) {
                            String[] techs = item.otherTechNames.split(",");
                            Collections.addAll(info.combineTechs, techs);
                        }
                        switch (item.status) {
                            case "paid":
                                info.status = PayNotifyInfo.STATUS_UNVERIFIED;
                                break;
                            case "pass":
                                info.status = PayNotifyInfo.STATUS_ACCEPTED;
                                break;
                            case "unpass":
                                info.status = PayNotifyInfo.STATUS_REJECTED;
                                break;
                            default:
                                info.status = PayNotifyInfo.STATUS_UNVERIFIED;
                                break;
                        }
                        data.add(info);
                    }
                    synchronized (mDataLocker) {
                        //合并数据
                        processNewData(data);
                    }
                }
                ThreadManager.postRunnable(ThreadManager.THREAD_TYPE_MAIN, new Runnable() {
                    @Override
                    public void run() {
                        callback.onResult(null, data);
                    }
                });
            }

            @Override
            public void onFailure(Call<GetPayNotifyListResult> call, Throwable t) {
                ThreadManager.postRunnable(ThreadManager.THREAD_TYPE_MAIN, new Runnable() {
                    @Override
                    public void run() {
                        callback.onResult(t, null);
                    }
                });
            }
        });
    }

    //处理新数据，将新数据合并到缓存中
    private void processNewData(List<PayNotifyInfo> newData) {
        List<PayNotifyInfo> originData = mData;
        int oldSize = originData.size();
        boolean statusChanged = false;
        long limitTime = System.currentTimeMillis() - Constant.PAY_NOTIFY_MAIN_PAGE_TIME_LIMIT;
        //合并新数据
        for (PayNotifyInfo info : newData) {
            info.isArchived = false;

            if (info.payTime >= limitTime) {
                //如果是最近数据，ID已存在归档列表中，且状态没有变化，设置归档标志
                ArchiveData archiveData = mRecentArchivedMaps.get(String.valueOf(info.id));
                if (archiveData != null && info.status == archiveData.status) {
                    info.isArchived = true;
                }
            }

            boolean alreadyInsert = false;
            int insetPosition = -1;
            for (int i = 0; i < originData.size(); i++) {
                PayNotifyInfo originInfo = originData.get(i);
                if (info.id == originInfo.id) {
                    //原始数据中有这一项
                    if (info.status != originInfo.status) {
                        statusChanged = true;
                    }
                    originData.set(i, info);
                    alreadyInsert = true;
                    break;
                } else if (info.payTime > originData.get(i).payTime) {
                    insetPosition = i;
                    break;
                }
            }
            if (!alreadyInsert) {
                if (insetPosition >= 0 && insetPosition < originData.size()) {
                    originData.add(insetPosition, info);
                } else {
                    originData.add(info);
                }
            }
        }
        saveArchivedIds();
        //如果有新的数据，要发送事件通知
        if (oldSize < originData.size() || statusChanged) {
            RxBus.getInstance().post(new PayNotifyNewDataEvent());
        }
    }


    public void setPayNotifyInfoArchived(PayNotifyInfo info) {
        for (PayNotifyInfo notifyInfo : mData) {
            if (notifyInfo.id == info.id) {
                notifyInfo.isArchived = info.isArchived;
                if (notifyInfo.isArchived) {
                    //保存归档数据到本地
                    mRecentArchivedMaps.put(String.valueOf(notifyInfo.id),
                            new ArchiveData(String.valueOf(notifyInfo.id), notifyInfo.payTime, notifyInfo.status));
                    saveArchivedIds();
                }
            }
        }
        RxBus.getInstance().post(new PayNotifyArchiveEvent(info));
    }

    //后台获取最新数据并发出通知
    public void getRecentDataAndSendNotify(int limitTime) {
        final long startTime = System.currentTimeMillis() - limitTime;
        final long endTime = System.currentTimeMillis() + (3600 * 1000);

        loadDataFromNetwork(startTime, endTime, new Callback<List<PayNotifyInfo>>() {
            @Override
            public void onResult(Throwable error, List<PayNotifyInfo> result) {

            }
        });
    }

    //检查是否有新的支付数据
    public void checkNewPayNotify() {
        mDataCheckCall = RetrofitServiceFactory.getSpaService().checkPayNotifyData(
                LoginTechnician.getInstance().getToken(),
                "fast_pay");
        mDataCheckCall.enqueue(new retrofit2.Callback<CheckPayNotifyResult>() {
            @Override
            public void onResponse(Call<CheckPayNotifyResult> call, Response<CheckPayNotifyResult> response) {
                CheckPayNotifyResult result = response.body();
                if (result != null && result.respData != null && result.respData.equals("Y")) {
                    //有新的数据
                    getRecentDataAndSendNotify(Constant.PAY_NOTIFY_MAIN_PAGE_TIME_LIMIT);
                }
            }

            @Override
            public void onFailure(Call<CheckPayNotifyResult> call, Throwable t) {

            }
        });
    }

    //清除数据
    public void clearData() {
        Logger.d("----clear pay notify cache data -------");
        if (mDataCheckCall != null) {
            mDataCheckCall.cancel();
        }
        if (mDataGetCall != null) {
            mDataGetCall.cancel();
        }
        synchronized (mDataLocker) {
            mData.clear();
        }
    }


    private void saveArchivedIds() {
        Iterator<String> iterator = mRecentArchivedMaps.keySet().iterator();
        StringBuilder save = new StringBuilder();
        while (iterator.hasNext()) {
            String id = iterator.next();
            save.append(id).append(",")
                    .append(mRecentArchivedMaps.get(id).payTime).append(",")
                    .append(mRecentArchivedMaps.get(id).status).append(",");
        }
        if (save.length() > 0) {
            save.setLength(save.length() - 1);
        }
        SharedPreferenceHelper.setPayNotifyArchivedIds(save.toString());
    }

    private void handleLogoutEvent(EventLogout event) {
        clearData();
    }

}
