package com.xmd.technician;

import android.os.SystemClock;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.app.user.User;
import com.xmd.app.user.UserInfoServiceImpl;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

/**
 * Created by mo on 17-8-15.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class UserServiceTest {
    @Test
    public void test() {
        UserInfoServiceImpl.getInstance().init(TechApplication.getAppContext());
        for (int i = 0; i < 10000; i++) {
            UserInfoServiceImpl.getInstance().saveUser(new User(String.valueOf(i)));
        }
        long t = SystemClock.elapsedRealtime();
        List<User> users = UserInfoServiceImpl.getInstance().getAllUsers();
        XLogger.d("---read " + users.size() + " cost: " + (SystemClock.elapsedRealtime() - t));
    }
}
