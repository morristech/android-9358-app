package com.xmd.chat.message;

/**
 * Created by mo on 17-7-14.
 * 用户预约后发此消息
 *
 */

public class NewOrderChatMessage<T> extends ChatMessage {
    private final static String ATTR_ORDER_ID = "orderId";
    private final static String ATTR_APPOINT_TIME = "appointTime";
    private final static String ATTR_SERVICE_ITEM_NAME = "serviceItemName";
    private final static String ATTR_ORDER_STATUS = "orderStatus";

    private String serviceItemName;
    private String appointTime;

    public NewOrderChatMessage(T message) {
        super(message);
//        String content = super.getOriginContentText().toString();
//        String msg[] = content.split("<b>|</b>");
//        if (msg.length > 1) {
//            itemName = msg[1];
//        }
//        if (msg.length > 3) {
//            arriveTime = msg[3];
//        }
    }

    public String getOrderId() {
        return getSafeStringAttribute(ATTR_ORDER_ID);
    }

    public String getItemName() {
        return getSafeStringAttribute(ATTR_SERVICE_ITEM_NAME);
    }

    public String getArriveTime() {
        return getSafeStringAttribute(ATTR_APPOINT_TIME);
    }

    public String getOrderStatus(){return getSafeStringAttribute(ATTR_ORDER_STATUS);}
}
