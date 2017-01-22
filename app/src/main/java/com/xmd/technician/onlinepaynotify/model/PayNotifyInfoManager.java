package com.xmd.technician.onlinepaynotify.model;

import android.text.TextUtils;

import com.xmd.technician.Constant;
import com.xmd.technician.SharedPreferenceHelper;
import com.xmd.technician.common.Callback;
import com.xmd.technician.common.ThreadManager;
import com.xmd.technician.msgctrl.RxBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import retrofit2.Call;

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
        mArchivedMaps = new HashMap<>();
        String hideIds = SharedPreferenceHelper.getPayNotifyHideIds();
        if (!TextUtils.isEmpty(hideIds)) {
            String[] ids = hideIds.split(",");
            long limitTime = System.currentTimeMillis() - Constant.PAY_NOTIFY_MAIN_PAGE_TIME_LIMIT;
            for (int i = 0; i < ids.length / 2; i++) {
                String id = ids[2 * i];
                long time = Long.parseLong(ids[2 * i + 1]);
                if (time < limitTime) {
                    continue;
                }
                mArchivedMaps.put(id, time);
            }
        }
    }

    private List<PayNotifyInfo> mData;
    private long mCurrentStartTime = -1;
    private long mCurrentEndTime = -1;
    private Map<String, Long> mArchivedMaps;
    private Call mDataRequestCall;
    private long mNewestPayTime;

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
                    if (error == null) {
                        if (result.size() > 0) {
                            result = getDataByFilter(startTime, endTime, status, onlyNotArchived, limitCount);
                            //前台刷新时，记录最新买单时间
                            mNewestPayTime = mData.get(0).payTime;
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
        for (PayNotifyInfo info : mData) {
            if (info.payTime >= startTime && info.payTime < endTime
                    && (info.status & status) != 0
                    && (!onlyNotArchived || !info.isArchived)) {
                result.add(info);
                count++;
                if (count >= limitCount) {
                    return result;
                }
            }
        }
        return result;
    }

    private int mockCount = 10;

    //从网络加载数据
    public void loadDataFromNetwork(long startTime, long endTime, Callback<List<PayNotifyInfo>> callback) {
        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                List<PayNotifyInfo> data = new ArrayList<>();
                for (int i = 0; i < mockCount; i++) {
                    PayNotifyInfo info = new PayNotifyInfo();
                    info.id = i + 1;
                    info.userName = "顾客" + i;
                    info.amount = (long) (Math.random() * 1000);
                    info.isArchived = false;
                    long currentTime = 1485057077000L;
                    info.payTime = System.currentTimeMillis() - i * 3600 * 1000;

                    info.status = 1 + i % 2;
                    info.userAvatar = "http://img3.duitang.com/uploads/item/201608/21/20160821200538_vHxLi.thumb.700_0.jpeg";
                    info.combineTechs = new ArrayList<>();
                    info.combineTechs.add("A00" + i);
                    info.combineTechs.add("A100");
                    data.add(info);
                }

                ThreadManager.postRunnable(ThreadManager.THREAD_TYPE_MAIN, new Runnable() {
                    @Override
                    public void run() {
                        combineData(mData, data);
                        callback.onResult(null, data);
                    }
                });
                mockCount++;
            }
        }.start();
//        if (mDataRequestCall != null) {
//            mDataRequestCall.cancel();
//        }
//        String startDate = DateUtils.getSdf("yyyy-MM-dd").format(new Date(startTime));
//        String endDate = DateUtils.getSdf("yyyy-MM-dd").format(new Date(endTime));
//        mDataRequestCall = RetrofitServiceFactory.getSpaService()
//                .getPayNotifyList(SharedPreferenceHelper.getUserToken(), startDate, endDate);
//        mDataRequestCall.enqueue(new retrofit2.Callback<GetPayNotifyListResult>() {
//            @Override
//            public void onResponse(Call<GetPayNotifyListResult> call, Response<GetPayNotifyListResult> response) {
//                List<PayNotifyInfo> data = new ArrayList<>();
//                GetPayNotifyListResult result = response.body();
//                if (result != null) {
//                    SimpleDateFormat sdf = DateUtils.getSdf("yyyy-MM-dd HH:mm:ss");
//                    for (GetPayNotifyListResult.Item item : response.body().respData) {
//                        PayNotifyInfo info = new PayNotifyInfo();
//                        info.id = item.id;
//                        try {
//                            info.payTime = sdf.parse(item.createTime).getTime();
//                        } catch (ParseException e) {
//                            //时间 解析出错，设置为当前时间
//                            info.payTime = System.currentTimeMillis();
//                            Logger.e("parse time error:" + item.createTime);
//                        }
//                        info.amount = item.payAmount;
//                        info.userName = item.userName;
//                        info.userAvatar = item.userAvatarUrl;
//                        info.combineTechs = new ArrayList<>();
//                        if (!TextUtils.isEmpty(item.otherTechNames)) {
//                            String[] techs = item.otherTechNames.split(",");
//                            Collections.addAll(info.combineTechs, techs);
//                        }
//                        switch (item.status) {
//                            case "paid":
//                                info.status = PayNotifyInfo.STATUS_UNVERIFIED;
//                                break;
//                            case "pass":
//                                info.status = PayNotifyInfo.STATUS_ACCEPTED;
//                                break;
//                            case "unpass":
//                                info.status = PayNotifyInfo.STATUS_REJECTED;
//                                break;
//                            default:
//                                info.status = PayNotifyInfo.STATUS_UNVERIFIED;
//                                break;
//                        }
//                        data.add(info);
//                    }
//                    //合并数据
//                    combineData(mData, data);
//                }
//                ThreadManager.postRunnable(ThreadManager.THREAD_TYPE_MAIN, new Runnable() {
//                    @Override
//                    public void run() {
//                        callback.onResult(null, data);
//                    }
//                });
//            }
//
//            @Override
//            public void onFailure(Call<GetPayNotifyListResult> call, Throwable t) {
//                ThreadManager.postRunnable(ThreadManager.THREAD_TYPE_MAIN, new Runnable() {
//                    @Override
//                    public void run() {
//                        callback.onResult(t, null);
//                    }
//                });
//            }
//        });
    }

    //将新数据合并到缓存中
    private void combineData(List<PayNotifyInfo> originData, List<PayNotifyInfo> newData) {
        int oldSize = originData.size();
        boolean statusChanged = false;
        for (PayNotifyInfo info : newData) {
            boolean alreadyInsert = false;
            int insetPosition = -1;
            for (int i = 0; i < originData.size(); i++) {
                if (info.id == originData.get(i).id) {
                    if (info.status != originData.get(i).status) {
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
                if (insetPosition > 0) {
                    originData.add(insetPosition, info);
                } else {
                    originData.add(info);
                }
            }
        }
        for (PayNotifyInfo info : originData) {
            checkArchived(info);
        }
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
                    mArchivedMaps.put(String.valueOf(notifyInfo.id), notifyInfo.payTime);
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

    //检查是否归档
    private void checkArchived(PayNotifyInfo info) {
        long limitTime = System.currentTimeMillis() - Constant.PAY_NOTIFY_MAIN_PAGE_TIME_LIMIT;
        if (info.payTime < limitTime) {
            info.isArchived = true;
        }
        if (mArchivedMaps.containsKey(String.valueOf(info.id))) {
            info.isArchived = true;
        }
    }

    private void saveArchivedIds() {
        Iterator<String> iterator = mArchivedMaps.keySet().iterator();
        StringBuilder save = new StringBuilder();
        while (iterator.hasNext()) {
            String id = iterator.next();
            save.append(id).append(",").append(mArchivedMaps.get(id)).append(",");
        }
        if (save.length() > 0) {
            save.setLength(save.length() - 1);
        }
        SharedPreferenceHelper.setPayNotifyArchivedIds(save.toString());
    }

}
