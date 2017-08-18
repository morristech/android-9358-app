package com.xmd.manager.beans;
import java.util.List;

/**
 * Created by Lhj on 17-8-14.
 */

public class JournalTemplateBean {

    /**
     * id : 1
     * name : 通用模板
     * previewJournalId : 3
     * musicId : 161029,161030,161031
     * previewUrl : /journal/#/1/?id=3&preview=true
     * coverImageUrl : http://sdcm103.stonebean.com:8489/s/group00/M00/00/70/oIYBAFfo-6aAYCvfAABOWDdgZZk428.jpg?st=qv3UWODYaEJuYPkEDgu_fQ&e=1505291546
     * itemsConfiguration : {"08":1}
     * newsTemplateList : [{"templateUrl":"http://sdcm103.stonebean.com:8489/s/group00/M00/00/79/oIYBAFg4JXOAE1npAAHOFsAnvTY672.jpg?st=Oh-_afIUx6ZwU-uycr1mIg&e=1505290476","templateId":"01","templateArticles":["5","22"],"templateImageCount":"1","templateName":"template1"},{"templateUrl":"http://sdcm103.stonebean.com:8489/s/group00/M00/00/79/oIYBAFg4JXOAE1npAAHOFsAnvTY672.jpg?st=Oh-_afIUx6ZwU-uycr1mIg&e=1505290476","templateId":"02","templateArticles":["5","22"],"templateImageCount":"2","templateName":"template2"},{"templateUrl":"http://sdcm103.stonebean.com:8489/s/group00/M00/00/79/oIYBAFg4JXOAE1npAAHOFsAnvTY672.jpg?st=Oh-_afIUx6ZwU-uycr1mIg&e=1505290476","templateId":"03","templateArticles":["5","22"],"templateImageCount":"1","templateName":"template3"}]
     */

    public int id;
    public String name;
    public int previewJournalId;
    public String musicId;
    public String previewUrl;
    public String coverImageUrl;
    public Object itemsConfiguration;
    public List<NewsTemplateListBean> newsTemplateList;


}
