package com.xmd.manager.beans;

/**
 * Created by linms@xiaomodo.com on 16-5-25.
 */
public class Comment {
    /**
     * id : 854357920760987648
     * techName : 10
     * techId : 849573973539688448
     * techAvatar : 301178
     * techSerialNo : 10
     * techEmchatId : null
     * rate : 100
     * rewardAmount : 0
     * comment : 喜欢喜欢超喜欢
     * createdAt : 1492529776000
     * attitudeRate : 100
     * appearanceRate : 100
     * skillRate : 100
     * clockRate : 100
     * impression : 服务周到、态度好、专业负责
     */

    public String id;
    public String techName;
    public String techId;
    public String techAvatar;
    public String techSerialNo;
    public String techEmchatId;
    public int rate;
    public int rewardAmount;
    public String comment;
    public long createdAt;
    public int attitudeRate;
    public int appearanceRate;
    public int skillRate;
    public int clockRate;
    public String impression;


    public Comment(String techName, String techId, String techAvatarUrl, int rate, int reward, String comment) {
        this.comment = comment;
        this.rate = rate;
        this.rewardAmount = reward;
        this.techAvatar = techAvatarUrl;
        this.techId = techId;
        this.techName = techName;
    }
}
