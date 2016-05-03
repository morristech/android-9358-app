package com.xmd.technician.http.gson;

import com.xmd.technician.bean.AlbumInfo;
import com.xmd.technician.bean.NativePlaceInfo;
import com.xmd.technician.bean.TechDetailInfo;

import java.util.List;

/**
 * Created by sdcm on 16-3-14.
 */
public class TechEditResult extends BaseResult {

    public Content respData;
    public class Content{
        public String phoneNum;
        public NativePlaceInfo nativePlace;
        public List<AlbumInfo> albums;
        public TechDetailInfo info;
    }
}
