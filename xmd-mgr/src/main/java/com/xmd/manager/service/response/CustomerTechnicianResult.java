package com.xmd.manager.service.response;

import com.xmd.manager.beans.Customer;

import java.util.List;

/**
 * Created by linms@xiaomodo.com on 16-5-18.
 */
public class CustomerTechnicianResult extends BaseResult {

    public List<Customer> respData;

    public CustomerTechnicianResult(List<Customer> respData) {
        this.respData = respData;
    }
}
