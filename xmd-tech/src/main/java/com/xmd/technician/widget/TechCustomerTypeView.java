package com.xmd.technician.widget;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xmd.technician.Constant;
import com.xmd.technician.R;
import com.xmd.technician.common.Utils;

/**
 * Created by Lhj on 17-6-2.
 */

public class TechCustomerTypeView extends LinearLayout {

    private View mView;
    private TextView techAddCustomer, newAddCustomer, bigCustomer, normalCustomer, activationCustomer;

    public TechCustomerTypeView(Context context) {
        this(context, null);
    }

    public TechCustomerTypeView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public TechCustomerTypeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public TechCustomerTypeView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, AttributeSet attrs) {
        mView = LayoutInflater.from(context).inflate(R.layout.layout_tech_customer_type, this);
        techAddCustomer = (TextView) mView.findViewById(R.id.tv_customer_tech_add);
        newAddCustomer = (TextView) mView.findViewById(R.id.tv_customer_tech_new_add);
        bigCustomer = (TextView) mView.findViewById(R.id.tv_customer_tech_big);
        normalCustomer = (TextView) mView.findViewById(R.id.tv_customer_tech_normal);
        activationCustomer = (TextView) mView.findViewById(R.id.tv_customer_tech_activation);
    }

    public void setTechCustomerType(String mark) {

        if (Utils.isEmpty(mark)) {
            normalCustomer.setVisibility(View.GONE);
            techAddCustomer.setVisibility(View.GONE);
            newAddCustomer.setVisibility(View.GONE);
            bigCustomer.setVisibility(View.GONE);
            activationCustomer.setVisibility(View.GONE);
            return;
        }

        if (mark.contains(Constant.USER_MARK_TECH_ADD)) {
            techAddCustomer.setVisibility(View.VISIBLE);
            normalCustomer.setVisibility(View.GONE);
        } else {
            techAddCustomer.setVisibility(View.GONE);
        }
        if (mark.contains(Constant.USER_MARK_NEW_ADD)) {
            normalCustomer.setVisibility(View.GONE);
            newAddCustomer.setVisibility(View.VISIBLE);
        } else {
            newAddCustomer.setVisibility(View.GONE);
        }
        if (mark.contains(Constant.USER_MARK_BIG)) {
            normalCustomer.setVisibility(View.GONE);
            bigCustomer.setVisibility(View.VISIBLE);
        } else {
            bigCustomer.setVisibility(View.GONE);
        }
        if (mark.contains(Constant.USER_MARK_NORMAL)) {
            normalCustomer.setVisibility(View.GONE);
            normalCustomer.setVisibility(View.VISIBLE);
        } else {
            normalCustomer.setVisibility(View.GONE);
        }
        if (mark.contains(Constant.USER_MARK_ACTIVATION)) {
            normalCustomer.setVisibility(View.GONE);
            activationCustomer.setVisibility(View.VISIBLE);
        } else {
            activationCustomer.setVisibility(View.GONE);
        }


    }

}
