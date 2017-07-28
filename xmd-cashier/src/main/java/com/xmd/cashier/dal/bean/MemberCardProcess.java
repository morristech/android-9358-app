package com.xmd.cashier.dal.bean;

/**
 * Created by zr on 17-7-19.
 * 处理开卡流程
 */

public class MemberCardProcess {
    private MemberInfo memberInfo;

    public MemberCardProcess() {
        memberInfo = new MemberInfo();
    }

    public MemberInfo getMemberInfo() {
        return memberInfo;
    }

    public void setMemberInfo(MemberInfo memberInfo) {
        this.memberInfo = memberInfo;
    }
}
