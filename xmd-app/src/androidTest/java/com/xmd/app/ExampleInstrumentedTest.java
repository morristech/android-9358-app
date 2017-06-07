package com.xmd.app;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.xmd.app.user.User;
import com.xmd.app.user.UserInfoService;
import com.xmd.app.user.UserInfoServiceImpl;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.xmd.app.test", appContext.getPackageName());
    }

    @Test
    public void testUserInfoService() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();
        UserInfoService userService = UserInfoServiceImpl.getInstance();
        userService.init(appContext);

        Assert.assertNotNull(userService.getUserByChatId("chat_1"));

        User user1 = new User("1");
        user1.setId("1");
        user1.setName("用户1");
        user1.setChatId("chat_1");
        userService.saveUser(user1);
        Log.i("test", "user1:" + user1);

        User user11 = userService.getUserByUserId("1");
        Assert.assertNotNull(user11);
        Assert.assertEquals(user11, user1);
        Log.i("test", "user11:" + user11);

        User user2 = userService.getUserByUserId("2");
        Assert.assertNull(user2);

        user1.setId("2");
        user1.setName("用户2");
        userService.saveUser(user1);
        User user22 = userService.getUserByUserId("2");
        Assert.assertNotNull(user22);
    }
}
