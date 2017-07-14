package com.xmd.technician.bean;

import com.xmd.technician.http.gson.BaseResult;

import java.util.List;

/**
 * Created by Administrator on 2016/8/9.
 */
public class CreditAccountResult extends BaseResult {
    /**
     * id : 763243186205257728
     * userId : 748081899301244928
     * clubId : 621280172275933185
     * amount : 100000
     * freezeAmount : 0
     * clubName : 93
     * clubImage : http://sdcm210:8489/s/group00/M00/00/2E/oIYBAFXBq1CAevp3AAAIuxFN9UA981.jpg?st=zl0gdzRKrKcc6fVeUOlKqw&e=1470827336
     */

    public List<RespDataBean> respData;

    public class RespDataBean {
        public String id;
        public String userId;
        public String clubId;
        public int amount;
        public int freezeAmount;
        public String clubName;
        public String clubImage;


    }
}
