package com.xmd.technician.http.gson;

import java.util.List;

/**
 * Created by heyangya on 17-3-27.
 */

public class RoleListResult extends BaseResult {
    public List<Item> respData;

    public static class Item {
        public String code; //tech-技师;floor_staff-楼面
        public long id;
        public String name;
    }
}
