package com.xmd.technician.model;

/**
 * Created by heyangya on 16-12-20.
 */
public class LoginTechnician {
    private static LoginTechnician ourInstance = new LoginTechnician();

    public static LoginTechnician getInstance() {
        return ourInstance;
    }

    private LoginTechnician() {

    }

    public String techNo;
    public String inviteCode;
    public String phoneNumber;
    public String techId;
}
