package com.xmd.manager.service.response;

import com.xmd.manager.beans.GroupMemberBean;

import java.util.List;

/**
 * Created by Lhj on 2016/12/30.
 */

public class UserGroupDetailListResult extends BaseResult {

    /**
     * respData : {"details":[{"id":"646585419709812736","name":"匿名用户","telephone":"15986770737","avatar":null,"badCommentCount":0,"avatarUrl":null},{"id":"646634276950904832","name":"mjflx","telephone":"13588888888","avatar":null,"badCommentCount":0,"avatarUrl":null},{"id":"647320958578724864","name":"Tracy3","telephone":"15019270235","avatar":"153680","badCommentCount":0,"avatarUrl":""},{"id":"647324495631093760","name":"铅华、落尽","telephone":"18607199275","avatar":null,"badCommentCount":0,"avatarUrl":"http://wx.qlogo.cn/mmopen/NdOtLVcAWbEia1lInYUB27ibKCKJP9UAVZ57PFdTeaRn0ibXvExQGgWc04DE2ZJerqFRMHtBQysT3aruOfKaricdvjoPEnTewbUd/0"},{"id":"647338257763274752","name":"mjflx","telephone":"13333333333","avatar":null,"badCommentCount":0,"avatarUrl":"null"},{"id":"647376522008596480","name":"wuli","telephone":"13265684479","avatar":"151962","badCommentCount":0,"avatarUrl":""},{"id":"648339808136990720","name":"雪栩星枫","telephone":"17727979914","avatar":null,"badCommentCount":0,"avatarUrl":"http://wx.qlogo.cn/mmopen/HJFwEP6urjVaYibibPlsWO7AXK1k2HjVxbRyZG5r5eFxjz6TrRlGZPvoXTGUHgPPgT7FHbWcfTNU3krRP5If7qkVhqdg9qwXqic/0"},{"id":"648347840006852608","name":"建","telephone":"18665369573","avatar":"150775","badCommentCount":0,"avatarUrl":""},{"id":"648387306381643776","name":"mjf?????","telephone":"13111111111","avatar":null,"badCommentCount":0,"avatarUrl":"http://wx.qlogo.cn/mmopen/8foOTAxeicWW6XmaqbicYZhvo9IJqRSAWdbLic1MS1tlYP1eoia6r1fNdBnqQMsOIiaXBgNxn4iaxolCEpyqPsKIumeqibHzlTyXeka/0"},{"id":"648416386762149888","name":"马金凤??","telephone":"17727979915","avatar":"151038","badCommentCount":0,"avatarUrl":"http://sdcm210:8489/s/group00/M00/00/4F/oIYBAFdP5BiALWvDAADgPjErNZ0303.png?st=S24VExWqeghbA4tyftpLBA&e=1483244850"},{"id":"648417413083172864","name":"老子","telephone":"15875564851","avatar":"153243","badCommentCount":0,"avatarUrl":"http://sdcm210:8489/s/group00/M00/00/65/oIYBAFehcKCAX7MbAABfEeCuA7A727.png?st=1gvRKHLMDKIAGmxi6DpNjA&e=1483256354"},{"id":"648418788043132928","name":"匿名用户","telephone":"17727979916","avatar":null,"badCommentCount":0,"avatarUrl":null},{"id":"648421590387658752","name":"马金凤","telephone":"17727979917","avatar":null,"badCommentCount":0,"avatarUrl":"http://wx.qlogo.cn/mmopen/rMuEumaX2UNAGk1Gsq4jxQsnXicdlOQ2119mvkJD7X98yLN7n5hrc6mmPvta71uz0g3LaZ4HuX8wETjxjhWQ9PA/0"},{"id":"648424382753935360","name":"majf_2015","telephone":"18828989911","avatar":null,"badCommentCount":0,"avatarUrl":"http://wx.qlogo.cn/mmopen/rMuEumaX2UNAGk1Gsq4jxQsnXicdlOQ2119mvkJD7X98yLN7n5hrc6mmPvta71uz0g3LaZ4HuX8wETjxjhWQ9PA/0"},{"id":"648424436696879104","name":"匿名用户","telephone":"18828989912","avatar":null,"badCommentCount":0,"avatarUrl":null},{"id":"633970688134221824","name":"符号哟","telephone":"18670658890","avatar":"150877","badCommentCount":0,"avatarUrl":""}],"group":{"id":47,"clubId":"601679316694081536","name":"TEST001","description":"AAAAAAAAAAAAAAAAAA","createType":"user","totalCount":16}}
     */

    public RespDataBean respData;

    public static class RespDataBean {

        public GroupBean group;
        public List<GroupMemberBean> details;

        public static class GroupBean {
            /**
             * id : 47
             * clubId : 601679316694081536
             * name : TEST001
             * description : AAAAAAAAAAAAAAAAAA
             * createType : user
             * totalCount : 16
             */

            public int id;
            public String clubId;
            public String name;
            public String description;
            public String createType;
            public int totalCount;
        }

    }
}
