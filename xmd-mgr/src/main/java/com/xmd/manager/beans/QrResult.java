package com.xmd.manager.beans;

/**
 * Created by linms@xiaomodo.com on 16-4-27.
 */
public class QrResult {
    /**
     * period : 60
     * time : 1469698044968
     * type : consume
     * rid : 219e8c67e654393c1e7a159bbf1f1903
     * qrNo : 12708217675410575360
     */
    public String period;
    public String time;
    public String type;
    public String rid;
    public String qrNo;

    public QrResult(String qrNo) {
        this.qrNo = qrNo;
    }

    @Override
    public String toString() {
        return "QrResult{" +
                "period='" + period + '\'' +
                ", time='" + time + '\'' +
                ", type='" + type + '\'' +
                ", rid='" + rid + '\'' +
                ", qrNo='" + qrNo + '\'' +
                '}';
    }
}
