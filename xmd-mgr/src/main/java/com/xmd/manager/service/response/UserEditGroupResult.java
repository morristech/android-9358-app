package com.xmd.manager.service.response;


import com.xmd.m.comment.bean.AllGroupListBean;
import com.xmd.m.comment.bean.UserGroupListBean;

import java.util.List;

/**
 * Created by Lhj on 2016/12/30.
 */

public class UserEditGroupResult extends BaseResult {

    /**
     * respData : {"userGroupList":[{"id":50,"name":"分组1","createTime":1483523358000,"createUserId":null,"createType":"user","description":"分组备注1","totalCount":1,"userNames":null}],"allGroupList":[{"id":47,"name":"TEST001","createTime":1483001317000,"createUserId":null,"createType":"user","description":"AAAAAAAAAAAAAAAAAA","totalCount":15,"userNames":null},{"id":49,"name":"TEST002","createTime":1483417062000,"createUserId":null,"createType":"user","description":"CCCCCC","totalCount":10,"userNames":null},{"id":50,"name":"分组1","createTime":1483523358000,"createUserId":null,"createType":"user","description":"分组备注1","totalCount":1,"userNames":null},{"id":52,"name":"测试分组3","createTime":1483523572000,"createUserId":null,"createType":"user","description":"测试分组3备注","totalCount":7,"userNames":null},{"id":53,"name":"测试分组3","createTime":1483523572000,"createUserId":null,"createType":"user","description":"测试分组3备注","totalCount":13,"userNames":null},{"id":54,"name":"新建分组5","createTime":1483524884000,"createUserId":null,"createType":"user","description":"","totalCount":4,"userNames":null},{"id":55,"name":"啦啦","createTime":1483525083000,"createUserId":null,"createType":"user","description":"","totalCount":6,"userNames":null}]}
     */

    public RespDataBean respData;

    public static class RespDataBean {
        public List<UserGroupListBean> userGroupList;
        public List<AllGroupListBean> allGroupList;
    }
}
