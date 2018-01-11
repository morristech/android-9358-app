package com.m.pk.bean;

/**
 * Created by Lhj on 17-6-28.
 */

public class PkFilterTeamBean {


    public String teamId;
    public String teamName;

    public PkFilterTeamBean(String id, String name) {
        this.teamId = id;
        this.teamName = name;
    }

    @Override
    public String toString() {
        return "PkFilterTeamBean{" +
                "teamId='" + teamId + '\'' +
                ", teamName='" + teamName + '\'' +
                '}';
    }

}
