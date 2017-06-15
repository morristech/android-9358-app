package com.xmd.technician.http.gson;

import java.util.List;

/**
 * Created by heyangya on 17-1-20.
 */

public class GetPayNotifyListResult extends BaseResult {
    public List<Item> respData;

    public static class Item {
        public String id;
        public String createTime; //yyyy-MM-dd HH:mm:ss
        public String otherTechNames;
        public int payAmount;
        public String status; //paid-未确认;pass-确认通过;unpass-确认异常
        public String userAvatarUrl;
        public String userName;
        public String userId;
    }
}
