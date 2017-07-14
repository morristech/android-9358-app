package com.example.xmd_m_comment.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lhj on 17-7-1.
 */

public class CommentBean implements Parcelable{


    /**
     * id : 850303117290373120
     * comment :
     * techScore : 65
     * status : valid
     * returnStatus : N
     * createdAt : 1491563036000
     * techId : null
     * techName : null
     * techNo : null
     * orderId : 850302817435385856
     * commentType : order
     * phoneNum : 13049344009
     * userId : 700881129107886080
     * userName : Lin2
     * avatar : 161293
     * userEmchatId : 0657f1cc39ade84da3c4df8c97924458
     * commentRateList : [{"id":26,"userId":"700881129107886080","userName":"Lin2","techId":"768000433410019328","techName":"nn","clubId":"601679316694081537","clubName":null,"commentId":"850303117290373120","commentRate":80,"commentTagId":12,"commentTagName":"技师态度","commentTagType":1,"createTime":1491563036000},{"id":27,"userId":"700881129107886080","userName":"Lin2","techId":"768000433410019328","techName":"nn","clubId":"601679316694081537","clubName":null,"commentId":"850303117290373120","commentRate":80,"commentTagId":13,"commentTagName":"技师仪容","commentTagType":1,"createTime":1491563036000},{"id":28,"userId":"700881129107886080","userName":"Lin2","techId":"768000433410019328","techName":"nn","clubId":"601679316694081537","clubName":null,"commentId":"850303117290373120","commentRate":60,"commentTagId":14,"commentTagName":"技师手法","commentTagType":1,"createTime":1491563036000},{"id":29,"userId":"700881129107886080","userName":"Lin2","techId":"768000433410019328","techName":"nn","clubId":"601679316694081537","clubName":null,"commentId":"850303117290373120","commentRate":80,"commentTagId":15,"commentTagName":"技师足钟","commentTagType":1,"createTime":1491563036000},{"id":30,"userId":"700881129107886080","userName":"Lin2","techId":"768000433410019328","techName":"nn","clubId":"601679316694081537","clubName":null,"commentId":"850303117290373120","commentRate":80,"commentTagId":22,"commentTagName":"接待服务","commentTagType":1,"createTime":1491563036000},{"id":31,"userId":"700881129107886080","userName":"Lin2","techId":"768000433410019328","techName":"nn","clubId":"601679316694081537","clubName":null,"commentId":"850303117290373120","commentRate":0,"commentTagId":23,"commentTagName":"配套设施","commentTagType":1,"createTime":1491563036000},{"id":32,"userId":"700881129107886080","userName":"Lin2","techId":"768000433410019328","techName":"nn","clubId":"601679316694081537","clubName":null,"commentId":"850303117290373120","commentRate":80,"commentTagId":24,"commentTagName":"卫生环境","commentTagType":1,"createTime":1491563036000}]
     * impression : 善解人意、认穴精准、甜美
     * isAnonymous : N
     * avatarUrl : http://sdcm103.stonebean.com:8489/s/group00/M00/01/2E/oIYBAFk4tz-AaIPBAAC5NuCFPpU261.png?st=Lb0A0e9YBdj49HssHvSTzA&e=1499566133
     */

    public String id;
    public String comment;
    public int techScore;
    public String status;
    public String returnStatus;
    public long createdAt;
    public String techId;
    public String techName;
    public String techNo;
    public String orderId;
    public String commentType;
    public String phoneNum;
    public String userId;
    public String userName;
    public String avatar;
    public String userEmchatId;
    public String impression;
    public String isAnonymous;
    public String avatarUrl;
    public List<CommentRateBean> commentRateList;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.comment);
        dest.writeInt(this.techScore);
        dest.writeString(this.status);
        dest.writeString(this.returnStatus);
        dest.writeLong(this.createdAt);
        dest.writeString(this.techId);
        dest.writeString(this.techName);
        dest.writeString(this.techNo);
        dest.writeString(this.orderId);
        dest.writeString(this.commentType);
        dest.writeString(this.phoneNum);
        dest.writeString(this.userId);
        dest.writeString(this.userName);
        dest.writeString(this.avatar);
        dest.writeString(this.userEmchatId);
        dest.writeString(this.impression);
        dest.writeString(this.isAnonymous);
        dest.writeString(this.avatarUrl);
        dest.writeList(this.commentRateList);
    }

    public CommentBean() {
    }

    protected CommentBean(Parcel in) {
        this.id = in.readString();
        this.comment = in.readString();
        this.techScore = in.readInt();
        this.status = in.readString();
        this.returnStatus = in.readString();
        this.createdAt = in.readLong();
        this.techId = in.readString();
        this.techName = in.readString();
        this.techNo = in.readString();
        this.orderId = in.readString();
        this.commentType = in.readString();
        this.phoneNum = in.readString();
        this.userId = in.readString();
        this.userName = in.readString();
        this.avatar = in.readString();
        this.userEmchatId = in.readString();
        this.impression = in.readString();
        this.isAnonymous = in.readString();
        this.avatarUrl = in.readString();
        this.commentRateList = new ArrayList<CommentRateBean>();
        in.readList(this.commentRateList, CommentRateBean.class.getClassLoader());
    }

    public static final Creator<CommentBean> CREATOR = new Creator<CommentBean>() {
        @Override
        public CommentBean createFromParcel(Parcel source) {
            return new CommentBean(source);
        }

        @Override
        public CommentBean[] newArray(int size) {
            return new CommentBean[size];
        }
    };
}
