package com.xmd.chat.message;

import com.hyphenate.chat.EMMessage;
import com.tencent.imsdk.TIMMessage;
import com.xmd.app.user.User;
import com.xmd.chat.xmdchat.constant.XmdMessageType;
import com.xmd.chat.xmdchat.messagebean.ClubLocationMessageBean;
import com.xmd.chat.xmdchat.model.XmdChatModel;
import com.xmd.chat.xmdchat.present.ImChatMessageManagerPresent;

/**
 * Created by mo on 17-7-7.
 * <p>
 * 因为历史原因，定位使用了自定义消息 !!!
 * <p>
 * 会所位置信息
 * <p>
 * ext : { //扩展字段
 * clubId : clubId, //技师的clubId
 * clubName : clubName, //技师的clubName
 * techId : techId, //技师的id
 * name : techName, //技师昵称
 * header : techHeader, //技师的头像
 * no : techSerialNo, //技师编号
 * avatar : techAvatar,//技师的头像id
 * time : Date.now(), //当前的时间
 * msgType : ‘clubLocation’,//标识为会所定位消息
 * lat：lat， //会所维度
 * lng：lng，//会所经度
 * address：address， //会所地址
 * staticMap：staticMap //会所地图定位静态图url
 * }
 */

public class CustomLocationMessage<T> extends ChatMessage {

    private static final String ATTR_LAT = "lat";
    private static final String ATTR_LNG = "lng";
    private static final String ATTR_ADDRESS = "address";
    private static final String ATTR_STATIC_MAP = "staticMap";
    private static final String ATTR_STATIC_MAP_W = "staticMapWidth";
    private static final String ATTR_STATIC_MAP_H = "staticMapHeight";

    public CustomLocationMessage(T message) {
       super(message);
    }

    public static CustomLocationMessage create(User remoteUser, double latitude, double longitude, String address, String map) {
        if(XmdChatModel.getInstance().chatModelIsEm()){
            EMMessage emMessage = EMMessage.createTxtSendMessage(address, remoteUser.getChatId());
            CustomLocationMessage message = new CustomLocationMessage(emMessage);
            message.setMsgType(MSG_TYPE_CLUB_LOCATION);
            message.setAttr(ATTR_LAT, String.valueOf(latitude));
            message.setAttr(ATTR_LNG, String.valueOf(longitude));
            message.setAttr(ATTR_ADDRESS, address);
            message.setAttr(ATTR_STATIC_MAP, map);
            return message;
        }else {
            ClubLocationMessageBean bean = new ClubLocationMessageBean();
            bean.setAddress(address);
            bean.setLat(String.valueOf(latitude));
            bean.setLng(String.valueOf(longitude));
            bean.setStaticMap(String.valueOf(map));
            TIMMessage message = ImChatMessageManagerPresent.wrapMessage(bean, XmdMessageType.CLUB_LOCATION_TYPE,null,null);
            CustomLocationMessage locationMessage = new CustomLocationMessage(message);
            return locationMessage;
        }

    }

    public String getAddress() {
        return getSafeStringAttribute(ATTR_ADDRESS);
    }

    public String getMapUrl() {
        return getSafeStringAttribute(ATTR_STATIC_MAP);
    }
}
