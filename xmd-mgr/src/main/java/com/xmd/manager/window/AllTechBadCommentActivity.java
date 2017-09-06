package com.xmd.manager.window;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.xmd.manager.R;
import com.xmd.manager.common.DateUtil;
import com.xmd.manager.common.ResourceUtils;
import com.xmd.manager.msgctrl.MsgDef;
import com.xmd.manager.msgctrl.MsgDispatcher;
import com.xmd.manager.service.RequestConstant;
import com.xmd.manager.widget.DropDownMenuDialog;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016/11/21.
 */
public class AllTechBadCommentActivity extends BaseActivity {

    @BindView(R.id.bad_comment_num)
    TextView badCommentNum;
    @BindView(R.id.bad_comment_ratio)
    TextView badCommentRatio;
    @BindView(R.id.toolbar_right_text)
    TextView tvToolbarRight;


    private String startTime;
    private String endTime;
    private String currentSortType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_tech_bad_comment);
        ButterKnife.bind(this);
        badCommentNum.setSelected(true);
        initView();
    }

    private void initView() {
        startTime = DateUtil.getMondayOfWeek();
        endTime = DateUtil.getCurrentDate();
        currentSortType = RequestConstant.COMMENT_SORT_COUNT;

        setRightVisible(true, "本周", ResourceUtils.getDrawable(R.drawable.allow_down_icon), view -> {
            final String[] items = new String[]{"今天", "本周", "本月", "本季", "本年", "累计"};
            DropDownMenuDialog.getDropDownMenuDialog(AllTechBadCommentActivity.this, items, (index -> {
                switch (index) {
                    case 0:
                        tvToolbarRight.setText("日");
                        startTime = DateUtil.getCurrentDate();
                        getData();
                        break;
                    case 1:
                        tvToolbarRight.setText("周");
                        startTime = DateUtil.getMondayOfWeek();
                        getData();
                        break;
                    case 2:
                        tvToolbarRight.setText("月");
                        startTime = DateUtil.getFirstDayOfMonth();
                        getData();
                        break;
                    case 3:
                        tvToolbarRight.setText("季");
                        startTime = DateUtil.getFirstDayOfQuarterString(new Date());
                        getData();
                        break;
                    case 4:
                        tvToolbarRight.setText("年");
                        startTime = DateUtil.getFirstDayOfYear();
                        getData();
                        break;
                    case 5:
                        tvToolbarRight.setText("累计");
                        startTime = "2015-01-01";
                        getData();
                        break;
                }
            })).show(tvToolbarRight);
        }, true);
    }

    @OnClick({R.id.bad_comment_num, R.id.bad_comment_ratio})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bad_comment_num:
                badCommentNum.setSelected(true);
                badCommentRatio.setSelected(false);
                currentSortType = RequestConstant.COMMENT_SORT_COUNT;
                getData();
                break;
            case R.id.bad_comment_ratio:
                currentSortType = RequestConstant.COMMENT_SORT_RATE;
                badCommentNum.setSelected(false);
                badCommentRatio.setSelected(true);
                getData();
                break;
        }
    }

    public void getData() {
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_SORT_TYPE, currentSortType);
        params.put(RequestConstant.KEY_START_DATE, startTime);
        params.put(RequestConstant.KEY_END_DATE, endTime);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_TECH_BAD_COMMENT, params);
    }
}
