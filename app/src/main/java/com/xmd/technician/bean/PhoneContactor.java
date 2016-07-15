package com.xmd.technician.bean;



/**
 * Created by Administrator on 2016/7/4.
 */
public class PhoneContactor  {
    public String name;
    public String telephone;
    public String sortLetters;
    public    PhoneContactor(String name,String telephone){
        this.name = name;
        this.telephone = telephone;
    }

    @Override
    public String toString() {
        return "PhoneContactor{" +
                "name='" + name + '\'' +
                ", telephone='" + telephone + '\'' +
                ", sortLetters='" + sortLetters + '\'' +
                '}';
    }
}
