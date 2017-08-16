package com.xmd.contact.bean;

import java.util.List;

/**
 * Created by Lhj on 17-7-29.
 */

public class ContactAllList {

    public int blackListCount;
    public int totalCount;
    public String serviceStatus; //"Y":是客服,"N":不是客服
    public String today;
    public List<ContactAllBean> userList;
}
