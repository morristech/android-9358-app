package com.xmd.chat.message;

/**
 * Created by mo on 17-7-14.
 * 用户预约后发此消息
 * <p>
 * msg :  "<span>发起预约</span><br>到店时间：<b>"+arriveTime+"</b><br>"+"预约项目：<b>"+(orderItem || "到店选择" )+"</b>"; //消息数据
 */

public class NewOrderChatMessage<T> extends ChatMessage {
    private final static String ATTR_ORDER_ID = "orderId";
    private final static String ATTR_APPOINT_TIME = "appointTime";
    private final static String ATTR_SERVICE_ITEM_NAME = "serviceItemName";

    private String serviceItemName;
    private String appointTime;

    public NewOrderChatMessage(T emMessage) {
        super(emMessage);
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
}
