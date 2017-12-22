package com.xmd.technician.common;

import com.xmd.technician.bean.CityInfo;
import com.xmd.technician.bean.ProvinceInfo;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * 对XML文件进行解析
 */
public class XmlParserHandler extends DefaultHandler {
    ProvinceInfo provinceInfo = new ProvinceInfo();
    CityInfo cityInfo = new CityInfo();
    private List<ProvinceInfo> provinceList = new ArrayList<ProvinceInfo>();

    public XmlParserHandler() {
    }

    public List<ProvinceInfo> getDataList() {
        return provinceList;
    }

    @Override
    public void startDocument() throws SAXException {
    }

    @Override
    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) throws SAXException {
        if (qName.equals("province")) {
            provinceInfo = new ProvinceInfo();
            provinceInfo.setName(attributes.getValue(0));
            provinceInfo.setProvinceCode(attributes.getValue(1));
            provinceInfo.setCityInfoList(new ArrayList<CityInfo>());
        } else if (qName.equals("city")) {
            cityInfo = new CityInfo();
            cityInfo.setName(attributes.getValue(0));
            cityInfo.setCityCode(attributes.getValue(1));
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        if (qName.equals("city")) {
            provinceInfo.getCityInfoList().add(cityInfo);
        } else if (qName.equals("province")) {
            provinceList.add(provinceInfo);
        }
    }
}
