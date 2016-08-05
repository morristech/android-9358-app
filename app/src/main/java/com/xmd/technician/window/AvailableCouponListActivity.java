package com.xmd.technician.window;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import com.xmd.technician.Adapter.ChatCouponListViewAdapter;
import com.xmd.technician.R;
import com.xmd.technician.bean.CheckedCoupon;
import com.xmd.technician.bean.CouponInfo;
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.common.ThreadManager;
import com.xmd.technician.msgctrl.RxBus;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Administrator on 2016/8/3.
 */
public class AvailableCouponListActivity extends BaseActivity implements View.OnClickListener {
    private static List<CouponInfo> couponInfoList;
    private ChatCouponListViewAdapter adapter;
    private ListView listView;
    private CheckedCoupon coupon;
    private List<CheckedCoupon> checkedCouponList = new ArrayList<>();
    private TextView sent;
    private Map<String,String> map = new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_availabel_coupon_deatil);
        sent = (TextView) findViewById(R.id.toolbar_right);
        sent.setOnClickListener(this);
        initView();
    }
    protected void initView() {
        setTitle(ResourceUtils.getString(R.string.check_coupon));
        setRightVisible(true,R.string.confirm);
        setBackVisible(true);
        listView = (ListView) findViewById(R.id.list);
        adapter = new ChatCouponListViewAdapter(AvailableCouponListActivity.this,couponInfoList);
        listView.setDivider(null);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView text = (TextView) view.findViewById(R.id.coupon_select);
                coupon = new CheckedCoupon(couponInfoList.get(position).useTypeName,couponInfoList.get(position).actValue,couponInfoList.get(position).couponPeriod,
                        couponInfoList.get(position).actId,couponInfoList.get(position).couponType ,position );
                if(map.containsKey(String.valueOf(position))){
                    text.setEnabled(false);
                    map.remove(String.valueOf(position));
                    for (int i = 0; i <checkedCouponList.size() ; i++) {
                        if(checkedCouponList.get(i).position ==position){
                            checkedCouponList.remove(i);
                            return;
                        }
                    }
                }else{
                    checkedCouponList.add(coupon);
                    map.put(String.valueOf(position),"");
                    text.setEnabled(true);
                }
            }

        });
    }
    public static  void setData(List<CouponInfo> couponInfo){
        couponInfoList = couponInfo;
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.toolbar_right){
            for (int i = 0; i <checkedCouponList.size() ; i++) {
                RxBus.getInstance().post(checkedCouponList.get(i));
            }
            ThreadManager.postDelayed(ThreadManager.THREAD_TYPE_MAIN, new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, 800);
        }
    }
}
