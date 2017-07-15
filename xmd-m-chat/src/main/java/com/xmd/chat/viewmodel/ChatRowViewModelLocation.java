package com.xmd.chat.viewmodel;

import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.chat.R;
import com.xmd.chat.databinding.ChatRowLocationBinding;
import com.xmd.chat.message.ChatMessage;
import com.xmd.chat.message.CustomLocationMessage;


/**
 * Created by mo on 17-7-1.
 * 位置消息
 */

public class ChatRowViewModelLocation extends ChatRowViewModel {

    public ChatRowViewModelLocation(ChatMessage chatMessage) {
        super(chatMessage);
    }

    public static View createView(ViewGroup parent) {
        ChatRowLocationBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.chat_row_location, parent, false);
        WebSettings webSettings = binding.webView.getSettings();
        webSettings.setSupportZoom(false);
        webSettings.setUseWideViewPort(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        binding.webView.setBlockTouch(true);
        return binding.getRoot();
    }

    @Override
    public ViewDataBinding onBindView(View view) {
        ChatRowLocationBinding binding = DataBindingUtil.getBinding(view);
        binding.setData(this);
        return binding;
    }

    @Override
    public void onUnbindView() {

    }

    public String getAddress() {
        return ((CustomLocationMessage) chatMessage).getAddress();
    }

    @BindingAdapter("map")
    public static void bindMap(WebView webView, ChatRowViewModelLocation data) {
        CustomLocationMessage locationMessage = (CustomLocationMessage) data.getChatMessage();
        XLogger.d("load map: " + locationMessage.getMapUrl());
        webView.loadUrl(locationMessage.getMapUrl());
    }

//    public String getAddress() {
//        return ((EMLocationMessageBody) chatMessage.getEmMessage().getBody()).getAddress();
//    }
//
//    @BindingAdapter("map")
//    public static void bindMap(WebView webView, ChatRowViewModelLocation data) {
//        EMLocationMessageBody body = (EMLocationMessageBody) data.getChatMessage().getEmMessage().getBody();
//        String url = "http://api.map.baidu.com/staticimage/v2?" +
//                "ak=WEwL0OXp5wo3YYNtxWAMUcTREgbSHhym" +
//                "&mcode=E6:35:E7:D6:3A:59:63:6E:9D:73:AA:20:E8:9C:A5:4C:72:84:D4:5A;com.baidu.navi.shelldemo" +
//                "&center=%f,%f" +
//                "&markers=%f,%f" +
//                "&width=%d&height=%d" +
//                "&zoom=16&copyright=1&dpiType=ph&coordtype=gcj02ll";
//        double latitude = body.getLatitude();
//        double longitude = body.getLongitude();
//        if (w == 0) {
//            w = ScreenUtils.dpToPx(webView.getMeasuredWidth());
//            h = ScreenUtils.dpToPx(webView.getMeasuredHeight());
//            if (w > 768 || h > 768) {
//                h = 768 * h / w;
//                w = 768;
//            }
//            if (h > 768) {
//                w = 768 * w / h;
//                h = 768;
//            }
//        }
//        String accessUrl = String.format(Locale.getDefault(), url,
//                longitude, latitude,
//                longitude, latitude,
//                w, h);
//        XLogger.d("load map: " + accessUrl);
//        webView.loadUrl(accessUrl);
//    }
}
