package com.xmd.manager.beans;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sdcm on 15-11-23.
 * <p>
 * Only for Mock Server usage, simulate the data returned by server
 */
public class BeanFactory {

    public static final String AVATAR = "http://wx.qlogo.cn/mmopen/ChCs6YSVOGU3WS0pKpvpOox0gvE90m0bqsqVb22jUNhAogTQZemntp0mVFCqWibsJj8iaSwzTcV6Id3fz9zLZ0K4O1IdTxaaEP/0";

    public static List<Customer> getCustomers() {
        List<Customer> list = new ArrayList<Customer>() {{
            add(new Customer("王先生", "17000000000", 20, "3 天前访问过", "未分组", 3, ""));
            add(new Customer("天马", "17000000000", 7, "1 分钟前访问过", "未分组", 1, ""));
            add(new Customer("刘先生", "17000000000", 15, "2 M小时前访问过", "花生", 2, "123"));
            add(new Customer("牛", "17000000000", 0, "3 天前访问过", "席子", 3, "234"));
            add(new Customer("游客", "17000000000", 2, "1 分钟前访问", "花生", 1, "123"));
            add(new Customer("狮子总", "17000000000", 5, "3 天前访问过", "席子", 3, "234"));
        }};
        return list;
    }

    public static List<Customer> getGroupCustomer() {
        List<Customer> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add(new Customer(AVATAR, "林先生" + i));
        }
        return list;
    }

    public static List<Customer> getContactCustomer() {
        List<Customer> list = new ArrayList<Customer>() {{
            add(new Customer("1234", "王先生", "17000000001", 20, "微信", AVATAR, 3));
            add(new Customer("1235", "天马", "170000000002", 7, "粉丝", AVATAR, 3));
            add(new Customer("1236", "刘先生", "17000000003", 15, "领券", AVATAR, 3));
            add(new Customer("1237", "牛", "17000000004", 5, "微信", AVATAR, 3));
            add(new Customer("1238", "游客", "17000000005", 2, "粉丝", AVATAR, 3));
            add(new Customer("1239", "狮子总", "17000000006", 5, "领券", AVATAR, 3));
            add(new Customer("1230", "张先生", "17000000007", 20, "微信", AVATAR, 3));
            add(new Customer("2231", "王天马", "17000000008", 7, "粉丝", AVATAR, 3));
            add(new Customer("1232", "李先生", "17000000009", 15, "领券", AVATAR, 3));
            add(new Customer("1233", "赵牛", "17000000010", 0, "微信", AVATAR, 3));
            add(new Customer("1331", "孙游客", "170000000011", 2, "粉丝", AVATAR, 3));
            add(new Customer("1533", "黄狮子总", "17000000012", 5, "领券", AVATAR, 3));
        }};
        return list;
    }


    public static List<Comment> getCustomerComments() {
        List<Comment> list = new ArrayList<Comment>() {{
            add(new Comment("花生", "1", AVATAR, 100, 5, "技术不错，下次还找他"));
            add(new Comment("花生", "1", AVATAR, 70, 3, "技术不错，下次还找他"));
            add(new Comment("花生", "1", AVATAR, 100, 5, "技术不错，下次还找他"));
            add(new Comment("花生", "1", AVATAR, 80, 4, "技术不错，下次还找他"));
            add(new Comment("花生", "1", AVATAR, 50, 1, "技术不错，下次还找他"));
            add(new Comment("花生", "1", AVATAR, 40, 5, "技术不错，下次还找他"));
        }};
        return list;
    }

    public static List<Order> getCustomerOrders() {
        List<Order> list = new ArrayList<Order>() {{
            add(new Order(AVATAR, "王先生", "17000000000", "05-18 23:15", "05-17 10:00", "花生", "failure", "appoint", 0));
            add(new Order(AVATAR, "刘先生", "17000000000", "05-18 23:15", "05-17 10:00", "花生", "complete", "appoint", 0));
            add(new Order(AVATAR, "牛", "17000000000", "05-18 23:15", "05-17 10:00", "花生", "complete", "paid", 10));
            add(new Order(AVATAR, "游客", "17000000000", "05-18 23:15", "05-17 10:00", "花生", "complete", "appoint", 0));
            add(new Order(AVATAR, "狮子总", "17000000000", "05-18 23:15", "05-17 10:00", "花生", "expire", "paid", 10));
        }};

        return list;
    }

    public static List<CouponInfo> getCustomerCoupons() {
        List<CouponInfo> list = new ArrayList<CouponInfo>() {{
            add(new CouponInfo("20元优惠券", "优惠券", "", "2016-03-01 至 2016-07-01", "领取后当天生效，有效天数2天", "每天可用，不限节假日", "在线"));
            add(new CouponInfo("20元点钟券", "点钟券", "", "2016-03-01 至 2016-07-01", "客人购买后30天有效", "每天可用，不限节假日", "在线"));
            add(new CouponInfo("10元优惠券", "优惠券", "", "2016-03-01 至 2016-07-01", "领取后当天生效，有效天数2天", "每天可用，不限节假日", "在线"));
            add(new CouponInfo("20元优惠券", "优惠券", "", "2016-03-01 至 2016-07-01", "领取后当天生效，有效天数2天", "每天可用，不限节假日", "在线"));
            add(new CouponInfo("50元现金券", "现金券", "", "2016-04-01 至 2016-08-01", "领取后当天生效，至 2016-08-10 有效", "每天可用，不限节假日", "在线"));
        }};

        return list;
    }

    public static List<String> getCustomerGroups() {
        List<String> groups = new ArrayList<String>() {
            {
                add("Vip客户");
                add("超级Vip客户");
                add("白金会员");
                add("白银会员");
                add("青铜会员");
                add("钻石");
            }
        };
        return groups;
    }

    public static List<String> getAllGroups() {
        List<String> groups = new ArrayList<String>() {
            {
                add("Vip客户");
                add("超级Vip客户");
                add("老客户");
                add("至尊卡");
                add("做头");
                add("白金会员");
                add("白银会员");
                add("青铜会员");
                add("钻石");
            }
        };
        return groups;
    }

}
