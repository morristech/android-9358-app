package com.xmd.technician.common;

import android.os.Process;

import java.io.IOException;
import java.util.List;

/**
 * Created by heyangya on 17-1-22.
 */

public class AppUtils {
    public static boolean isBackground() {
        try {
            List<String> result = CmdTool.runCmd("cat /proc/" + Process.myPid() + "/oom_adj");
            if (result.size() > 0 && result.get(0).equals("0")) {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }
}
