package com.xmd.manager.window;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.xmd.manager.R;
import com.xmd.manager.common.ResourceUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zr on 17-11-22.
 * 展示报表说明
 */

public class ReportExplainDialogActivity extends BaseActivity {
    public static final String EXTRA_REPORT_TYPE = "report_type";
    public static final String REPORT_TYPE_SALARY = "salary";   //技师工资报表
    public static final String REPORT_TYPE_CASHIER = "cashier"; //买单收银报表

    private String mReportType;

    @BindView(R.id.tv_explain_head)
    TextView headText;
    @BindView(R.id.tv_explain_one)
    TextView oneText;
    @BindView(R.id.tv_explain_two)
    TextView twoText;
    @BindView(R.id.tv_explain_three)
    TextView threeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_explain_dialog);
        ButterKnife.bind(this);
        mReportType = getIntent().getStringExtra(EXTRA_REPORT_TYPE);
        switch (mReportType) {
            case REPORT_TYPE_CASHIER:
                headText.setText(ResourceUtils.getString(R.string.report_cashier_explain_head));
                oneText.setVisibility(View.VISIBLE);
                oneText.setText(ResourceUtils.getString(R.string.report_cashier_explain_one));
                twoText.setVisibility(View.VISIBLE);
                twoText.setText(ResourceUtils.getString(R.string.report_cashier_explain_two));
                threeText.setVisibility(View.VISIBLE);
                threeText.setText(ResourceUtils.getString(R.string.report_cashier_explain_three));
                break;
            case REPORT_TYPE_SALARY:
                headText.setText(ResourceUtils.getString(R.string.report_salary_explain_head));
                oneText.setVisibility(View.VISIBLE);
                oneText.setText(ResourceUtils.getString(R.string.report_salary_explain_one));
                twoText.setVisibility(View.VISIBLE);
                twoText.setText(ResourceUtils.getString(R.string.report_salary_explain_two));
                threeText.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @OnClick({R.id.img_dialog_close, R.id.btn_dialog_confirm})
    public void onDialogClose() {
        finish();
    }
}
