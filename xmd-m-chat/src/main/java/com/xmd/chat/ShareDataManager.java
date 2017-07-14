package com.xmd.chat;

import com.xmd.chat.beans.Journal;
import com.xmd.chat.beans.Marketing;
import com.xmd.chat.beans.MarketingCategory;
import com.xmd.chat.beans.OnceCard;
import com.xmd.chat.beans.ResultOnceCard;
import com.xmd.chat.message.ShareChatMessage;
import com.xmd.m.network.BaseBean;
import com.xmd.m.network.NetworkSubscriber;
import com.xmd.m.network.XmdNetwork;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.Subscription;

/**
 * Created by mo on 17-7-11.
 * 分享一些东西，比如电子期刊，次卡，优惠券等，提供数据访问
 */

public class ShareDataManager {
    public static final String DATA_TYPE_JOURNAL = "电子期刊";
    public static final String DATA_TYPE_ONCE_CARD_SINGLE = "单项次卡";
    public static final String DATA_TYPE_ONCE_CARD_MIX = "混合套餐";
    public static final String DATA_TYPE_ONCE_CARD_CREDIT = "积分礼包";

    private static final ShareDataManager ourInstance = new ShareDataManager();

    public static ShareDataManager getInstance() {
        return ourInstance;
    }

    private ShareDataManager() {
    }

    private Map<String, List> dataSource = new HashMap<>();
    private List<String> marketingDataTypeList = new ArrayList<>();

    public List getDataList(String type) {
        return dataSource.get(type);
    }

    public List<String> getMarketingDataTypeList() {
        return marketingDataTypeList;
    }

    public int getDataTypeLayoutId(String type) {
        switch (type) {
            case DATA_TYPE_JOURNAL:
                return R.layout.chat_share_list_item_journal;
            case DATA_TYPE_ONCE_CARD_SINGLE:
            case DATA_TYPE_ONCE_CARD_MIX:
            case DATA_TYPE_ONCE_CARD_CREDIT:
                return R.layout.chat_share_list_item_once_card;
            default:
                if (marketingDataTypeList.contains(type)) {
                    return R.layout.chat_share_list_item_marketing;
                }
                throw new RuntimeException("不支持的类型");
        }
    }

    //分享数据
    public void onShare(String chatId, Map<String, List> shareData) {
        for (String type : shareData.keySet()) {
            switch (type) {
                case DATA_TYPE_JOURNAL:
                    shareJournalList(chatId, shareData.get(type));
                    break;
                case DATA_TYPE_ONCE_CARD_SINGLE:
                case DATA_TYPE_ONCE_CARD_MIX:
                case DATA_TYPE_ONCE_CARD_CREDIT:
                    shareOnceCardList(chatId, shareData.get(type));
                    break;
            }
            if (marketingDataTypeList.contains(type)) {
                shareMarketingList(chatId, shareData.get(type));
            }
        }
    }

    //分享电子期刊
    private void shareJournalList(String chatId, List<Journal> shareData) {
        for (Journal journal : shareData) {
            ShareChatMessage message = ShareChatMessage.createJournalMessage(
                    chatId,
                    journal.journalId,
                    String.valueOf(journal.templateId),
                    journal.title);
            MessageManager.getInstance().sendMessage(message);
        }
    }

    //分享次卡
    private void shareOnceCardList(String chatId, List<OnceCard> shareData) {
        for (OnceCard data : shareData) {
            ShareChatMessage message = ShareChatMessage.createOnceCardMessage(chatId, data);
            MessageManager.getInstance().sendMessage(message);
        }
    }

    //分享营销活动
    private void shareMarketingList(String chatId, List<Marketing> shareData) {
        ShareChatMessage message = null;
        for (Marketing data : shareData) {
            switch (data.getCategory()) {
                case MarketingCategory.TIME_LIMIT:
                    message = ShareChatMessage.createMarketingTimeLimitMessage(chatId, data);
                    break;
                case MarketingCategory.ONE_YUAN:
                    message = ShareChatMessage.createMarketingOneYuanMessage(chatId, data);
                    break;
                case MarketingCategory.LUCKY_WHEEL:
                    message = ShareChatMessage.createMarketingLuckWheelMessage(chatId, data);
                    break;
            }
            if (message != null) {
                MessageManager.getInstance().sendMessage(message);
            }
        }
    }

    //加载电子期刊列表
    public Subscription loadJournalList(final NetworkSubscriber<Void> networkSubscriber) {
        Observable<BaseBean<List<Journal>>> observable = XmdNetwork.getInstance()
                .getService(NetService.class)
                .listShareJournal(AccountManager.getInstance().getUser().getClubId(), "0", "1000");
        return XmdNetwork.getInstance().request(observable, new NetworkSubscriber<BaseBean<List<Journal>>>() {
            @Override
            public void onCallbackSuccess(BaseBean<List<Journal>> result) {
                List<Journal> dataList = new ArrayList<>();
                for (Journal journal : result.getRespData()) {
                    dataList.add(journal);
                }
                dataSource.put(DATA_TYPE_JOURNAL, dataList);
                networkSubscriber.onCallbackSuccess(null);
            }

            @Override
            public void onCallbackError(Throwable e) {
                networkSubscriber.onCallbackError(e);
            }
        });
    }

    //加载优惠商城数据
    public Subscription loadOnceCardList(final NetworkSubscriber<Void> networkSubscriber) {
        Observable<BaseBean<ResultOnceCard>> observable = XmdNetwork.getInstance()
                .getService(NetService.class)
                .listOnceCards(AccountManager.getInstance().getUser().getClubId(), "true", "0", String.valueOf(Integer.MAX_VALUE));
        return XmdNetwork.getInstance().request(observable, new NetworkSubscriber<BaseBean<ResultOnceCard>>() {
            @Override
            public void onCallbackSuccess(BaseBean<ResultOnceCard> result) {
                List<OnceCard> singleOnceCardList = new ArrayList<>();
                List<OnceCard> mixOnceCardList = new ArrayList<>();
                List<OnceCard> creditOnceCardList = new ArrayList<>();
                for (OnceCard onceCard : result.getRespData().activityList) {
                    switch (onceCard.cardType) {
                        case OnceCard.CARD_TYPE_SINGLE:
                            singleOnceCardList.add(onceCard);
                            break;
                        case OnceCard.CARD_TYPE_MIX:
                            mixOnceCardList.add(onceCard);
                            break;
                        case OnceCard.CARD_TYPE_CREDIT:
                            creditOnceCardList.add(onceCard);
                            break;
                    }
                }
                dataSource.put(DATA_TYPE_ONCE_CARD_SINGLE, singleOnceCardList);
                dataSource.put(DATA_TYPE_ONCE_CARD_MIX, mixOnceCardList);
                dataSource.put(DATA_TYPE_ONCE_CARD_CREDIT, creditOnceCardList);
                networkSubscriber.onCallbackSuccess(null);
            }

            @Override
            public void onCallbackError(Throwable e) {
                networkSubscriber.onCallbackError(e);
            }
        });
    }

    //加载营销活动数据
    public Subscription loadMarketingList(final NetworkSubscriber<Void> networkSubscriber) {
        Observable<BaseBean<List<MarketingCategory>>> observable = XmdNetwork.getInstance()
                .getService(NetService.class)
                .listMarketing();
        return XmdNetwork.getInstance().request(observable, new NetworkSubscriber<BaseBean<List<MarketingCategory>>>() {
            @Override
            public void onCallbackSuccess(BaseBean<List<MarketingCategory>> result) {
                marketingDataTypeList.clear();
                for (MarketingCategory marketing : result.getRespData()) {
                    marketingDataTypeList.add(marketing.categoryName);
                    for (Marketing sub : marketing.list) {
                        sub.setCategory(marketing.category);
                    }
                    dataSource.put(marketing.categoryName, marketing.list);
                }
                networkSubscriber.onCallbackSuccess(null);
            }

            @Override
            public void onCallbackError(Throwable e) {
                networkSubscriber.onCallbackError(e);
            }
        });
    }
}
