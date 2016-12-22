package com.xmd.technician.http.gson;

import java.util.List;

/**
 * Created by heyangya on 16-12-22.
 */

public class UnusedTechNoListResult extends BaseResult {
    public List<ListItem> respData;

    public static class ListItem {
        public String id;
        public String serialNo;
    }
}
