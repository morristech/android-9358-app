package com.xmd.chat.beans;

/**
 * Created by mo on 17-7-21.
 * 骰子游戏结果
 */

public class DiceGameResult {
    private String clubId;

    private String clubName;

    /**
     * 游戏的标的
     */
    private int belongingsId;

    /**
     * 游戏的标的数量
     */
    private int belongingsAmount;

    /**
     * 发出邀请用户Id
     */
    private String srcId;

    /**
     * 发出邀请用户名称
     */
    private String srcName;

    /**
     * 发出邀请用户类型（user、tech、manager）
     */
    private String srcType;

    /**
     * 发出邀请用户手机号码
     */
    private String srcTelephone;

    /**
     * 接受邀请用户Id
     */
    private String dstId;

    /**
     * 接受邀请用户名称
     */
    private String dstName;

    /**
     * 接受邀请用户类型（user、tech、manager）
     */
    private String dstType;

    /**
     * 接受邀请用户手机号码
     */
    private String dstTelephone;

    /**
     * 游戏状态，submit:提交，finish:结束
     */
    private String status;

    /**
     * 发出邀请用户色子点数
     */
    private int srcPoint;

    /**
     * 接受邀请用户色子点数
     */
    private int dstPoint;

    public String getClubId() {
        return clubId;
    }

    public void setClubId(String clubId) {
        this.clubId = clubId;
    }

    public String getClubName() {
        return clubName;
    }

    public void setClubName(String clubName) {
        this.clubName = clubName;
    }

    public int getBelongingsId() {
        return belongingsId;
    }

    public void setBelongingsId(int belongingsId) {
        this.belongingsId = belongingsId;
    }

    public int getBelongingsAmount() {
        return belongingsAmount;
    }

    public void setBelongingsAmount(int belongingsAmount) {
        this.belongingsAmount = belongingsAmount;
    }

    public String getSrcId() {
        return srcId;
    }

    public void setSrcId(String srcId) {
        this.srcId = srcId;
    }

    public String getSrcName() {
        return srcName;
    }

    public void setSrcName(String srcName) {
        this.srcName = srcName;
    }

    public String getSrcType() {
        return srcType;
    }

    public void setSrcType(String srcType) {
        this.srcType = srcType;
    }

    public String getSrcTelephone() {
        return srcTelephone;
    }

    public void setSrcTelephone(String srcTelephone) {
        this.srcTelephone = srcTelephone;
    }

    public String getDstId() {
        return dstId;
    }

    public void setDstId(String dstId) {
        this.dstId = dstId;
    }

    public String getDstName() {
        return dstName;
    }

    public void setDstName(String dstName) {
        this.dstName = dstName;
    }

    public String getDstType() {
        return dstType;
    }

    public void setDstType(String dstType) {
        this.dstType = dstType;
    }

    public String getDstTelephone() {
        return dstTelephone;
    }

    public void setDstTelephone(String dstTelephone) {
        this.dstTelephone = dstTelephone;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getSrcPoint() {
        return srcPoint;
    }

    public void setSrcPoint(int srcPoint) {
        this.srcPoint = srcPoint;
    }

    public int getDstPoint() {
        return dstPoint;
    }

    public void setDstPoint(int dstPoint) {
        this.dstPoint = dstPoint;
    }

    @Override
    public String toString() {
        return "DiceGameResult{" +
                "clubId='" + clubId + '\'' +
                ", clubName='" + clubName + '\'' +
                ", belongingsId=" + belongingsId +
                ", belongingsAmount=" + belongingsAmount +
                ", srcId='" + srcId + '\'' +
                ", srcName='" + srcName + '\'' +
                ", srcType='" + srcType + '\'' +
                ", srcTelephone='" + srcTelephone + '\'' +
                ", dstId='" + dstId + '\'' +
                ", dstName='" + dstName + '\'' +
                ", dstType='" + dstType + '\'' +
                ", dstTelephone='" + dstTelephone + '\'' +
                ", status='" + status + '\'' +
                ", srcPoint=" + srcPoint +
                ", dstPoint=" + dstPoint +
                '}';
    }
}
