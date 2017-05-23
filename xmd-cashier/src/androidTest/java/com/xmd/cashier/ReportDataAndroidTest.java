package com.xmd.cashier;

import android.test.InstrumentationTestCase;

import com.xmd.cashier.common.Utils;
import com.xmd.cashier.dal.bean.ReportData;
import com.xmd.cashier.dal.db.DataReportTable;

import junit.framework.Assert;

import java.util.List;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ReportDataAndroidTest extends InstrumentationTestCase {
    public void testDB() throws Exception {
        Thread.sleep(1000);
        ReportData data = new ReportData();
        data.tradeNo = Utils.generateTradeNumber();


        DataReportTable.insert(data);
        DataReportTable.insert(data);
        List<ReportData> list = DataReportTable.query();
        Assert.assertTrue(list.size() == 1);


        DataReportTable.delete(data.tradeNo);
        list = DataReportTable.query();
        Assert.assertTrue(list.size() == 0);
    }
}