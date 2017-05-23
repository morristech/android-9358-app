package com.xmd.technician.http.gson;

import java.util.List;

/**
 * Created by heyangya on 16-12-22.
 */

public class UnusedTechNoListResult extends BaseResult {
    public DATA respData;

    public static class DATA {
        public String clubId;
        public String clubName;
        public List<ListItem> techNos;
    }

    public static class ListItem {
        public String id;
        public String serialNo;
    }
}
