package com.gome.monitor.util;

import com.trilead.ssh2.*;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

/**
 * Created by hutao on 2017/8/3.
 */
@Slf4j
public class ShellUtils {

//    public static Connection conn = null;
    private static final long TIME_OUT = 1000 * 60 * 60 * 2;

    public static boolean isWin() {
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            return true;
        }
        return false;
    }

    public static String[] exec(String cmd) {
        String[] cmds = null;
        String encoding = "UTF-8";
        int i = -1;
        if (isWin()) {
            cmds = new String[]{"cmd", "exe", "/c", cmd};
            encoding = "GBK";
        } else {
            cmds = new String[]{"/bin/bash", "-c", cmd};
        }
        try {
            Process process = Runtime.getRuntime().exec(cmds);
            process.waitFor(TIME_OUT, TimeUnit.MILLISECONDS);
            String out = processStream(process.getInputStream(), encoding);
            String err = processStream(process.getErrorStream(), encoding);
            i = process.exitValue();
            if (i == 0) {
                return new String[]{i + "", out};
            } else {
                return new String[]{i + "", err};
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new String[]{i + "", e.getMessage()};
        }
    }

    public static Connection remoteLogin(String ip, String user, String pwd) throws IOException {
        Connection conn = new Connection(ip);
        conn.connect();
        if (conn.isAuthenticationComplete()||
            conn.isAuthenticationPartialSuccess()||
            conn.authenticateWithPassword(user, pwd)){
            return conn;
        }
        return null;
    }

    public static String[] remoteExec(String cmd,Connection conn) throws IOException, InterruptedException {
        String encoding = "UTF-8";
        if (isWin()) encoding = "GBK";
        Session session = conn.openSession();
        session.execCommand(cmd);
        @Cleanup InputStream stdout = new StreamGobbler(session.getStdout());
        @Cleanup InputStream stderr = new StreamGobbler(session.getStderr());
        String out = processStream(stdout, encoding);
        String err = processStream(stderr, encoding);
        session.waitForCondition(ChannelCondition.EXIT_STATUS, TIME_OUT);
        Integer status = session.getExitStatus();
        if (status == 0) {
            return new String[]{status + "", out};
        } else {
            return new String[]{status + "", err};
        }
    }

    private static String processStream(InputStream in, String charset) throws IOException {
        @Cleanup BufferedReader reader = new BufferedReader(new InputStreamReader(in, charset));
        String line="";
        StringBuilder sb = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        return sb.toString();
    }

//    public static void main(String[] args) throws Exception {
//        boolean b = ShellUtils.remoteLogin("10.112.167.26", "root", "3edcXZAQ!");
//        System.out.println(b);
//        String[] pwd = ShellUtils.remoteExec("/data12/monitor/bin/monitor.sh /data12/monitor/bin/list.conf");
//        System.out.println(pwd[0]);
////        System.out.println(pwd[1]);
//        List<String> list = Arrays.asList(pwd[1].split("\\n"));
//
//        for (String i:list){
//            System.out.println(i);
//        }
//
//    }

}
