package com.xmd.technician.bean;

import com.xmd.technician.http.gson.BaseResult;

/**
 * Created by Administrator on 2016/8/22.
 */
public class GameResult extends BaseResult {

    /**
     * id : 768372297537974272
     * clubId : 601679316694081536
     * clubName : 刘哥会所2
     * belongingsId : 1
     * belongingsAmount : 10
     * srcId : 743718740591382528
     * srcName : 算法的技术
     * srcType : user
     * srcTelephone : 13137558109
     * dstId : 768364493666258944
     * dstName : 冰与火之歌
     * dstType : tech
     * dstTelephone : 13166666666
     * status : accept
     * createDatetime : 1472029206000
     * modifyDatetime : 1472030167614
     * srcPoint : 2
     * dstPoint : 1
     * bizType : dice
     * srcWin : true
     * gameDescription : 骰子游戏
     */

    public RespDataBean respData;



    public static class RespDataBean {
        public String id;
        public String clubId;
        public String clubName;
        public int belongingsId;
        public String belongingsAmount;
        public String srcId;
        public String srcName;
        public String srcType;
        public String srcTelephone;
        public String dstId;
        public String dstName;
        public String dstType;
        public String dstTelephone;
        public String status;
        public long createDatetime;
        public long modifyDatetime;
        public int srcPoint;
        public int dstPoint;
        public String bizType;
        public boolean srcWin;
        public String gameDescription;

    }
}
