package com.xmd.cashier.widget;

import android.app.Activity;
import android.inputmethodservice.KeyboardView;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;

/**
 * Created by heyangya on 16-8-26.
 */

public class OnMyKeyboardCallback implements KeyboardView.OnKeyboardActionListener {
    private Activity mActivity;
    private Callback mCallback;
    private static final int KEYCODE_CLEAR = 10001;
    private static final int KEYCODE_DELETE = 10002;
    private static final int KEYCODE_ENTER = 10003;

    public OnMyKeyboardCallback(Activity activity, Callback callback) {
        mActivity = activity;
        mCallback = callback;
    }

    @Override
    public void onPress(int primaryCode) {

    }

    @Override
    public void onRelease(int primaryCode) {

    }

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        if (mCallback.onKey(primaryCode)) {
            return;
        }
        if (primaryCode == KEYCODE_ENTER && mCallback.onKeyEnter()) {
            return;
        }
        View focusView = mActivity.getWindow().getCurrentFocus();
        if (focusView == null || !(focusView instanceof CustomEditText)) {
            return;
        }
        EditText editText = (EditText) focusView;
        Editable editable = editText.getText();
        int start = editText.getSelectionStart();
        switch (primaryCode) {
            case KEYCODE_CLEAR:
                editable.clear();
                break;
            case KEYCODE_DELETE:
                if (start > 0)
                    editable.delete(start - 1, start);
                break;
            default:
                editable.insert(start, Character.toString((char) primaryCode));
                break;
        }
    }

    @Override
    public void onText(CharSequence text) {

    }

    @Override
    public void swipeLeft() {

    }

    @Override
    public void swipeRight() {

    }

    @Override
    public void swipeDown() {

    }

    @Override
    public void swipeUp() {

    }

    public static abstract class Callback {
        public boolean onKeyEnter() {
            return false;
        }

        public boolean onKey(int primaryCode) {
            return false;
        }
    }
}
