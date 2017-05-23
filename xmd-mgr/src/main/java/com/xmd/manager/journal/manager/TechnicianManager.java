package com.xmd.manager.journal.manager;

import android.text.TextUtils;

import com.xmd.manager.ClubData;
import com.xmd.manager.SharedPreferenceHelper;
import com.xmd.manager.common.DateUtil;
import com.xmd.manager.journal.Callback;
import com.xmd.manager.journal.model.Technician;
import com.xmd.manager.journal.model.TechnicianRanking;
import com.xmd.manager.journal.net.NetworkSubscriber;
import com.xmd.manager.service.RequestConstant;
import com.xmd.manager.service.RetrofitServiceFactory;
import com.xmd.manager.service.response.TechnicianListResult;
import com.xmd.manager.service.response.TechnicianRankingListResult;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by heyangya on 16-11-3.
 */

public class TechnicianManager {
    private static TechnicianManager instance = new TechnicianManager();
    private List<Technician> mTechnicians;
    private AtomicBoolean mTechnicianLoaded = new AtomicBoolean(false);
    private Subscription mTechnicianSubscription;

    private List<TechnicianRanking> mRankings;
    private Map<String, Subscription> mRankingSubscriptions;


    private TechnicianManager() {
        mTechnicians = new ArrayList<>();
        mRankings = new ArrayList<>();
        mRankingSubscriptions = new HashMap<>();
    }

    public static TechnicianManager getInstance() {
        return instance;
    }

    private String getUserToken() {
        return SharedPreferenceHelper.getUserToken();
    }

    //获取会所技师列表
    public Subscription loadTechnicians(Callback<List<Technician>> callback) {
        if (mTechnicianLoaded.get()) {
            if (callback != null) {
                callback.onResult(null, mTechnicians);
            }
            return null;
        }
        if (mTechnicianSubscription != null) {
            mTechnicianSubscription.unsubscribe();
        }
        mTechnicianSubscription = RetrofitServiceFactory.getSpaService().getTechnicianList(getUserToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetworkSubscriber<TechnicianListResult>(new Callback<TechnicianListResult>() {
                    @Override
                    public void onResult(Throwable error, TechnicianListResult result) {
                        mTechnicianSubscription = null;
                        if (error == null) {
                            mTechnicians.clear();
                            for (TechnicianListResult.Item item : result.respData) {
                                Technician technician = new Technician(item.techId, item.techNo, item.techName, item.avatarUrl);
                                if (TextUtils.isEmpty(item.avatarUrl)) {
                                    //默认为会所LOGO
                                    technician.setIconUrl(ClubData.getInstance().getClubImageLocalPath());
                                }
                                mTechnicians.add(technician);
                            }
                            mTechnicianLoaded.set(true);
                        }
                        if (callback != null) {
                            callback.onResult(error, mTechnicians);
                        }
                    }
                }));
        return mTechnicianSubscription;
    }

    public TechnicianRanking getCommentRanking() {
        for (TechnicianRanking ranking : mRankings) {
            if (ranking.getType().equals(RequestConstant.TECHNICIAN_RANKING_TYPE_COMMENT)) {
                return ranking;
            }
        }
        return null;
    }

    //获取会所技师排行榜
    public synchronized Subscription loadTechnicianRanking(String type, Callback<TechnicianRanking> callback) {
        for (TechnicianRanking ranking : mRankings) {
            if (ranking.getType().equals(type)) {
                if (callback != null) {
                    callback.onResult(null, ranking);
                    return null;
                }
            }
        }
        Subscription subscription = mRankingSubscriptions.get(type);
        if (subscription != null) {
            subscription.unsubscribe();
        }
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        String startDate = DateUtil.getSdf("yyyy-MM-dd").format(calendar.getTime());
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, 0);
        String endDate = DateUtil.getSdf("yyyy-MM-dd").format(calendar.getTime());
        subscription = RetrofitServiceFactory.getSpaService().getTechnicianRankingList(
                getUserToken(), type, 1, 10, startDate, endDate)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetworkSubscriber<>(new Callback<TechnicianRankingListResult>() {
                    @Override
                    public void onResult(Throwable error, TechnicianRankingListResult result) {
                        mRankingSubscriptions.remove(type);
                        TechnicianRanking ranking = null;
                        if (error == null) {
                            ranking = new TechnicianRanking();
                            ranking.setType(type);
                            List<Technician> data = new ArrayList<>();
                            ranking.setData(data);
                            for (TechnicianRankingListResult.Item item : result.respData) {
                                Technician tri = new Technician(item.id, item.serialNo, item.name, item.avatarUrl);
                                if (TextUtils.isEmpty(item.avatarUrl)) {
                                    //默认为会所LOGO
                                    tri.setIconUrl(ClubData.getInstance().getClubImageLocalPath());
                                }
                                data.add(tri);
                            }
                            mRankings.add(ranking);
                        }
                        if (callback != null) {
                            callback.onResult(error, ranking);
                        }
                    }
                }));
        mRankingSubscriptions.put(type, subscription);
        return subscription;
    }

    public void clearData() {
        mTechnicianLoaded.set(false);
        mTechnicians.clear();
        mRankings.clear();
    }

    public Technician getTechnician(String id) {
        for (Technician technician : mTechnicians) {
            if (technician.getId().equals(id)) {
                return technician;
            }
        }
        return null;
    }

    public Technician getTechnicianFromRecommendRanking(String id) {
        for (TechnicianRanking ranking : mRankings) {
            if (ranking.getType().equals(RequestConstant.TECHNICIAN_RANKING_TYPE_COMMENT)) {
                for (Technician technician : ranking.getData()) {
                    if (technician.getId().equals(id)) {
                        return technician;
                    }
                }
            }
        }
        return null;
    }

    //TODO 临时处理
    //如果数据变动找不到技师显示的话，返回第一个技师
    public Technician getTechnician() {
        return mTechnicians.get(0);
    }
}
