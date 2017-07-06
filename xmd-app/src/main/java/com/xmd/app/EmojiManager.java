package com.xmd.app;

import android.content.Context;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by mo on 17-7-1.
 * 表情管理
 */

public class EmojiManager {
    private static final EmojiManager ourInstance = new EmojiManager();

    public static EmojiManager getInstance() {
        return ourInstance;
    }

    private EmojiManager() {

    }

    private Context context;
    private Map<String, Integer> emojiMap = new HashMap<>();
    private Map<Pattern, Integer> emojiPatternMap = new HashMap<>();

    public void init(Context context) {
        if (this.context != null) {
            return;
        }
        this.context = context.getApplicationContext();

        emojiMap.put("/::O", R.drawable.ee_1);
        emojiMap.put("/::Y", R.drawable.ee_2);
        emojiMap.put("/::B", R.drawable.ee_3);
        emojiMap.put("/::A", R.drawable.ee_4);
        emojiMap.put("/::C", R.drawable.ee_5);
        emojiMap.put("/::D", R.drawable.ee_6);
        emojiMap.put("/::X", R.drawable.ee_7);
        emojiMap.put("/::Z", R.drawable.ee_8);
        emojiMap.put("/::F", R.drawable.ee_9);
        emojiMap.put("/::E", R.drawable.ee_10);
        emojiMap.put("/::T", R.drawable.ee_11);
        emojiMap.put("/::P", R.drawable.ee_12);
        emojiMap.put("/::G", R.drawable.ee_13);
        emojiMap.put("/::H", R.drawable.ee_14);
        emojiMap.put("/::I", R.drawable.ee_15);
        emojiMap.put("/::J", R.drawable.ee_16);
        emojiMap.put("/::K", R.drawable.ee_17);
        emojiMap.put("/::L", R.drawable.ee_18);
        emojiMap.put("/::M", R.drawable.ee_19);
        emojiMap.put("/::N", R.drawable.ee_20);
        emojiMap.put("/::Q", R.drawable.ee_21);
        emojiMap.put("/::R", R.drawable.ee_22);
        emojiMap.put("/::S", R.drawable.ee_23);
        emojiMap.put("/::U", R.drawable.ee_24);
        emojiMap.put("/::V", R.drawable.ee_25);
        emojiMap.put("/::W", R.drawable.ee_26);
        emojiMap.put("/::a", R.drawable.ee_27);
        emojiMap.put("/::b", R.drawable.ee_28);
//        emojiMap.put(ee_29 = "[(R)]",R.drawable.ee_29);
//        emojiMap.put(ee_30 = "[({)]",R.drawable.ee_30);
//        emojiMap.put(ee_31 = "[(})]",R.drawable.ee_31);
//        emojiMap.put(ee_32 = "[(k)]",R.drawable.ee_32);
//        emojiMap.put(ee_33 = "[(F)]",R.drawable.ee_33);
//        emojiMap.put(ee_34 = "[(W)]",R.drawable.ee_34);
//        emojiMap.put(ee_35 = "[(D)]",R.drawable.ee_35);

        for (String emojiCode : emojiMap.keySet()) {
            emojiPatternMap.put(Pattern.compile(emojiCode), emojiMap.get(emojiCode));
        }
    }

    public Map<String, Integer> getEmojiMap() {
        return emojiMap;
    }



    public SpannableString format(String src) {
        SpannableString s = new SpannableString(Html.fromHtml(src));
        for (Pattern pattern : emojiPatternMap.keySet()) {
            Matcher matcher = pattern.matcher(src);
            while (matcher.find()) {
                s.setSpan(new ImageSpan(context, emojiPatternMap.get(pattern)), matcher.start(), matcher.end(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            }
        }
        return s;
    }

    public SpannableString getEmojiSpannableString(String emojiKey) {
        SpannableString s = new SpannableString(emojiKey);
        s.setSpan(new ImageSpan(context, emojiMap.get(emojiKey)), 0, emojiKey.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        return s;
    }
}
