package com.xmd.manager.common;

import android.app.Activity;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xmd.manager.Constant;
import com.xmd.manager.R;
import com.xmd.manager.beans.Order;
import com.xmd.manager.msgctrl.MsgDef;
import com.xmd.manager.msgctrl.MsgDispatcher;
import com.xmd.manager.service.RequestConstant;
import com.xmd.manager.widget.AlertDialogBuilder;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by linms@xiaomodo.com on 16-6-3.
 */
public class OrderManagementHelper {

    public static void handleOrderNegative(Activity activity, Order order) {
        if (Constant.ORDER_STATUS_SUBMIT.equals(order.status)) {
            doNegativeOrder(activity, ResourceUtils.getString(R.string.order_detail_reject_order_confirm), Constant.ORDER_STATUS_REJECTED, order, "");
        } else if (Constant.ORDER_STATUS_ACCEPT.equals(order.status)) {
            if (Constant.ORDER_TYPE_PAID.equals(order.orderType)) {
                //Paid Order to expire
                usePaidOrder(activity, ResourceUtils.getString(R.string.order_detail_expire_order_confirm), createDialogContentView(activity, order), Constant.PAID_ORDER_OP_EXPIRE, order, "");
            } else {
                // Appointed Order to failure
                doNegativeOrder(activity, ResourceUtils.getString(R.string.order_detail_failure_order_confirm), Constant.ORDER_STATUS_FAILURE, order, "");
            }
        }
    }

    /**
     * Positive button clicked in order
     *
     * @param activity
     * @param order
     */
    public static void handleOrderPositive(Activity activity, Order order) {
        if (Constant.ORDER_STATUS_SUBMIT.equals(order.status)) {
            doManageOrder(Constant.ORDER_STATUS_ACCEPT, order, "");
        } else if (Constant.ORDER_STATUS_ACCEPT.equals(order.status)) {
            // Paid Order to verify
            if (Constant.ORDER_TYPE_PAID.equals(order.orderType)) {
                usePaidOrder(activity, ResourceUtils.getString(R.string.order_detail_verified_order_confirm), createDialogContentView(activity, order), Constant.PAID_ORDER_OP_VERIFIED, order, "");
            } else {
                // Appointed Order to complete
                doManageOrder(Constant.ORDER_STATUS_COMPLETE, order, "");
            }
        } else {
            doNegativeOrder(activity, ResourceUtils.getString(R.string.order_detail_delete_order_confirm), Constant.ORDER_STATUS_DELETE, order, "");
        }
    }

    public static void verifyPaidOrder(Activity activity, Order order) {
        if (Constant.ORDER_STATUS_ACCEPT.equals(order.status)) {
            usePaidOrder(activity, ResourceUtils.getString(R.string.order_detail_verified_order_confirm), createDialogContentView(activity, order), Constant.PAID_ORDER_OP_VERIFIED, order, "");
        }
    }

    private static void doNegativeOrder(Activity activity, String description, String type, Order order, String reason) {
        new AlertDialogBuilder(activity)
                .setMessage(description)
                .setPositiveButton(ResourceUtils.getString(R.string.confirm), v -> doManageOrder(type, order, reason))
                .setNegativeButton(ResourceUtils.getString(R.string.cancel), null)
                .show();
    }

    private static void doManageOrder(String type, Order order, String reason) {
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_PROCESS_TYPE, type);
        params.put(RequestConstant.KEY_ID, order.id);
        params.put(RequestConstant.KEY_REASON, reason);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_MANAGE_ORDER, params);
    }

    private static void usePaidOrder(Activity activity, String title, View customView, String type, Order order, String reason) {
        new AlertDialogBuilder(activity)
                .setTitle(title)
                .setCustomView(customView)
                .setPositiveButton(ResourceUtils.getString(R.string.confirm), v -> doUsePaidOrder(type, order, reason))
                .setNegativeButton(ResourceUtils.getString(R.string.cancel), null)
                .setCancelable(true)
                .show();
    }

    private static void doUsePaidOrder(String type, Order order, String reason) {
        if (Constant.ORDER_STATUS_SUBMIT.equals(order.status)) {
            Map<String, String> params = new HashMap<>();
            params.put(RequestConstant.KEY_ORDER_NO, order.orderNo);
            params.put(RequestConstant.KEY_PROCESS_TYPE, type);
            params.put(RequestConstant.KEY_ID, order.id);
            params.put(RequestConstant.KEY_REASON, reason);
            MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_PAID_ORDER_USE, params);
        } else {
            Map<String, String> params = new HashMap<>();
            params.put(RequestConstant.KEY_ORDER_NO, order.orderNo);
            params.put(RequestConstant.KEY_PAY_ORDER_PROCESS_TYPE, type);
            MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_CHECK_INFO_ORDER_SAVE, params);
        }

     /*

      */
    }

    private static View createDialogContentView(Activity activity, Order order) {

        int padding = ResourceUtils.getDimenInt(R.dimen.inter_space);
        LinearLayout linearLayout = new LinearLayout(activity);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(padding, padding, padding, padding);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ResourceUtils.getDimenInt(R.dimen.dialog_content_item_height));
        TextView customerView = new TextView(activity);
        customerView.setGravity(Gravity.CENTER_VERTICAL);
        customerView.setText(order.customerName + "(" + order.phoneNum + ")");
        customerView.setTextColor(ResourceUtils.getColor(R.color.dialog_content_text_color));
        customerView.setTextSize(TypedValue.COMPLEX_UNIT_PX, ResourceUtils.getDimenFloat(R.dimen.dialog_content_text_size));
        linearLayout.addView(customerView, lp);

        TextView appointTimeView = new TextView(activity);
        appointTimeView.setGravity(Gravity.CENTER_VERTICAL);
        appointTimeView.setText(ResourceUtils.getString(R.string.order_list_item_time_desc) + ":" + order.appointTime);
        appointTimeView.setTextSize(TypedValue.COMPLEX_UNIT_PX, ResourceUtils.getDimenFloat(R.dimen.dialog_content_text_size));
        customerView.setTextColor(ResourceUtils.getColor(R.color.dialog_content_text_color));
        linearLayout.addView(appointTimeView, lp);

        return linearLayout;
    }

    public static void showResultDialog(Activity activity, String msg) {
        new AlertDialogBuilder(activity)
                .setTitle(ResourceUtils.getString(R.string.alert_tips_title))
                .setMessage(msg)
                .setPositiveButton(ResourceUtils.getString(R.string.confirm), null)
                .show();
    }
}
