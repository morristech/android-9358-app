package com.xmd.technician.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.xmd.technician.R;
import com.xmd.technician.common.XmlParserHandler;
import com.xmd.technician.model.CityInfo;
import com.xmd.technician.model.ProvinceInfo;
import com.xmd.technician.wheel.OnWheelChangedListener;
import com.xmd.technician.wheel.WheelView;
import com.xmd.technician.wheel.adapters.ArrayWheelAdapter;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Created by SD_ZR on 15-6-6.
 */
public abstract class SelectPlaceDialog extends Dialog implements OnWheelChangedListener {
    // 保存省市信息的文件名：assets
    public static final String PROVINCE_CITY_XML_FILE = "province_data.xml";
    private Context context;

    private static final int VISIBLE_ITEM_COUNT = 7;

    // 省名
    private String[] mProvinceDatas;
    // 省和省code的对应关系
    private Map<String, String> mProvinceCodesMap = new HashMap<String, String>();

    // 省名 对应 市名的数组
    private Map<String, String[]> mCityDatasMap = new HashMap<String, String[]>();
    // 市和市code的对应关系
    private Map<String, String> mCityCodesMap = new HashMap<String, String>();

    // 当前省名
    public String mCurrentProvinceName;
    public String mCurrentProvinceCode;
    private String initProvince;

    // 当前市名
    public String mCurrentCityName;
    public String mCurrentCityCode;
    private String initCity;

    private WheelView mProvinceWheel;
    private WheelView mCityWheel;
    private Button mConfirmButton;
    private Button mCancelButton;

    public SelectPlaceDialog(Context context) {
        super(context);
        this.context = context;
    }

    public SelectPlaceDialog(Context context, int theme, String province, String city) {
        super(context, theme);
        this.context = context;
        this.initProvince = province;
        this.initCity = city;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_select_place);

        initDatas();

        mProvinceWheel = (WheelView) findViewById(R.id.select_province);
        mProvinceWheel.addChangingListener(this);
        mProvinceWheel.setViewAdapter(new ArrayWheelAdapter<String>(context, mProvinceDatas));
        mProvinceWheel.setVisibleItems(VISIBLE_ITEM_COUNT);

        mCityWheel = (WheelView) findViewById(R.id.select_city);
        mCityWheel.addChangingListener(this);
        mCityWheel.setVisibleItems(VISIBLE_ITEM_COUNT);


        // 初始化
        String[] cityDatas = mCityDatasMap.get(initProvince);
        updateCities();
        if (initProvince != null && !(TextUtils.isEmpty(initProvince))) {
            mProvinceWheel.setCurrentItem(Arrays.asList(mProvinceDatas).indexOf(initProvince));
        }
        if (cityDatas != null && !(TextUtils.isEmpty(initCity))) {
            mCityWheel.setCurrentItem(Arrays.asList(cityDatas).indexOf(initCity));
        }
        mConfirmButton = (Button) findViewById(R.id.select_confirm_button);
        mConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSelectConfirmMethod();
            }
        });

        mCancelButton = (Button) findViewById(R.id.select_cancel_button);
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public abstract void onSelectConfirmMethod();

    private void initDatas() {
        List<ProvinceInfo> provinceList = null;
        AssetManager asset = context.getAssets();
        try {
            InputStream input = asset.open(PROVINCE_CITY_XML_FILE);
            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser parser = spf.newSAXParser();
            XmlParserHandler handler = new XmlParserHandler();
            parser.parse(input, handler);
            input.close();
            provinceList = handler.getDataList();
            if (provinceList != null && !provinceList.isEmpty()) {
                mCurrentProvinceName = provinceList.get(0).getName();
                List<CityInfo> cityList = provinceList.get(0).getCityInfoList();
                if (cityList != null && !cityList.isEmpty()) {
                    mCurrentCityName = cityList.get(0).getName();
                }

                mProvinceDatas = new String[provinceList.size()];
                for (int i = 0; i < provinceList.size(); i++) {
                    mProvinceDatas[i] = provinceList.get(i).getName();
                    mProvinceCodesMap.put(mProvinceDatas[i], provinceList.get(i).getProvinceCode());
                    cityList = provinceList.get(i).getCityInfoList();
                    String[] cityNames = new String[cityList.size()];
                    for (int j = 0; j < cityList.size(); j++) {
                        cityNames[j] = cityList.get(j).getName();
                        mCityCodesMap.put(cityNames[j], cityList.get(j).getCityCode());
                    }
                    mCityDatasMap.put(provinceList.get(i).getName(), cityNames);
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onChanged(WheelView wheel, int oldValue, int newValue) {
        if (wheel == mProvinceWheel) {
            updateCities();
        } else if (wheel == mCityWheel) {
            mCurrentCityName = mCityDatasMap.get(mCurrentProvinceName)[newValue];
            mCurrentCityCode = mCityCodesMap.get(mCurrentCityName);
        }
    }

    private void updateCities() {
        int provinceCurrent = mProvinceWheel.getCurrentItem();
        mCurrentProvinceName = mProvinceDatas[provinceCurrent];
        mCurrentProvinceCode = mProvinceCodesMap.get(mCurrentProvinceName);
        String[] cities = mCityDatasMap.get(mCurrentProvinceName);
        mCurrentCityName = cities[0];
        mCurrentCityCode = mCityCodesMap.get(mCurrentCityName);
        if (cities == null) {
            cities = new String[]{""};
        }
        mCityWheel.setViewAdapter(new ArrayWheelAdapter<String>(context, cities));
        mCityWheel.setCurrentItem(0);
    }
}
