package com.xmd.technician.http.gson;

import com.xmd.technician.bean.DayInfo;
import com.xmd.technician.bean.TimeInfo;

import java.util.List;

/**
 * Created by sdcm on 16-3-16.
 */
public class WorkTimeResult extends BaseResult {

    public Content respData;

    public class Content {
        public String id;
        public String techStatus;
        public String workTimeRange;
        public String workDayRange;
        public List<DayInfo> days;
        public TimeInfo times;
    }
}
