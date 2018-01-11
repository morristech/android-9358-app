package com.m.pk.bean;

/**
 * Created by Lhj on 18-1-4.
 */

public class PKDetailListBean {

    /**
     * teamId : 67
     * teamName : 队伍1
     * leader : 枫【A0088】
     * teamMember : GG【A0050】,技师【】,小摩豆技师【】,足浴技师小小豆01【A6816】,小摩豆技师【A5236】,小摩豆技师【】
     * customerStat : 0
     * saleStat : 0
     * commentStat : 0
     * couponStat : 0
     * avatar : 158852
     * memberCount : 7
     * teamRanking : 0
     * serialNo : null
     * avatarUrl : http://sdcm103.stonebean.com:8489/s/group00/M00/01/1C/oIYBAFh0R8GAD7jzAADXQ4YKfHI426.png?st=6I5hqygHTw-oUtno2h3CMQ&e=1492574479
     */
    private String teamId;
    private String teamName;
    private String leader;
    private String teamMember;
    private int customerStat;
    private int saleStat;
    private int commentStat;
    private int couponStat;
    private String avatar;
    private int memberCount;
    private int teamRanking;
    private String serialNo;
    private String avatarUrl;
    private boolean isTeam;
    private float paidServiceItemStat;
    private int paidServiceItemCount;
    private int position;
    private int teamSize;
    private String sortType;

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getLeader() {
        return leader;
    }

    public void setLeader(String leader) {
        this.leader = leader;
    }

    public String getTeamMember() {
        return teamMember;
    }

    public void setTeamMember(String teamMember) {
        this.teamMember = teamMember;
    }

    public int getCustomerStat() {
        return customerStat;
    }

    public void setCustomerStat(int customerStat) {
        this.customerStat = customerStat;
    }

    public int getSaleStat() {
        return saleStat;
    }

    public void setSaleStat(int saleStat) {
        this.saleStat = saleStat;
    }

    public int getCommentStat() {
        return commentStat;
    }

    public void setCommentStat(int commentStat) {
        this.commentStat = commentStat;
    }

    public int getCouponStat() {
        return couponStat;
    }

    public void setCouponStat(int couponStat) {
        this.couponStat = couponStat;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }

    public int getTeamRanking() {
        return teamRanking;
    }

    public void setTeamRanking(int teamRanking) {
        this.teamRanking = teamRanking;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public boolean isTeam() {
        return isTeam;
    }

    public void setTeam(boolean team) {
        isTeam = team;
    }

    public float getPaidServiceItemStat() {
        return paidServiceItemStat;
    }

    public void setPaidServiceItemStat(int paidServiceItemStat) {
        this.paidServiceItemStat = paidServiceItemStat;
    }

    public int getPaidServiceItemCount() {
        return paidServiceItemCount;
    }

    public void setPaidServiceItemCount(int paidServiceItemCount) {
        this.paidServiceItemCount = paidServiceItemCount;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }


    public int getTeamSize() {
        return teamSize;
    }

    public void setTeamSize(int teamSize) {
        this.teamSize = teamSize;
    }

    public String getSortType() {
        return sortType;
    }

    public void setSortType(String sortType) {
        this.sortType = sortType;
    }
}
