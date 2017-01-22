package com.xmd.technician.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by heyangya on 17-1-22.
 */

public class CmdTool {
    public static List<String> runCmd(String cmd) throws IOException, InterruptedException {
        List<String> res = new ArrayList<String>();
        Process p = Runtime.getRuntime().exec(cmd);
        InputStream is1 = p.getInputStream();

        BufferedReader br1 = new BufferedReader(new InputStreamReader(is1));
        String line1;
        while ((line1 = br1.readLine()) != null) {
            res.add(line1.trim());
        }
        br1.close();
        p.waitFor();
        return res;
    }
}
