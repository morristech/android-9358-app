package com.xmd.cashier.dal.bean;

/**
 * Created by zr on 17-7-19.
 * 处理开卡流程
 */

public class MemberCardProcess {
    private MemberInfo memberInfo;
    private TechInfo techInfo;

    public MemberCardProcess() {
        memberInfo = new MemberInfo();
        techInfo = new TechInfo();
    }

    public MemberInfo getMemberInfo() {
        return memberInfo;
    }

    public void setMemberInfo(MemberInfo memberInfo) {
        this.memberInfo = memberInfo;
    }

    public TechInfo getTechInfo() {
        return techInfo;
    }

    public void setTechInfo(TechInfo techInfo) {
        this.techInfo = techInfo;
    }
}
