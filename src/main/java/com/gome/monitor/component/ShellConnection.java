package com.gome.monitor.component;

import com.gome.monitor.bean.ShellBean;
import com.gome.monitor.util.ShellUtils;
import com.trilead.ssh2.Connection;
import com.trilead.ssh2.Session;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

public class ShellConnection {


    @Autowired
    PropConfig propConfig;

    private Connection conn;

    private String host;
    private String user;
    private String pwd;

    public ShellConnection(String host, String user, String pwd) {
        this.host = host;
        this.user = user;
        this.pwd = pwd;
        conn = getConnection();
    }

    public Connection getConnection() {
        try {
            if (conn != null && (conn.isAuthenticationComplete() || conn.isAuthenticationPartialSuccess())) {
                return conn;
            } else {
                conn = ShellUtils.remoteLogin(host, user, pwd);
                return conn;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return conn;
    }

    public ShellBean exec(ShellBean bean, boolean b) throws IOException, InterruptedException {
        bean.setCommand("ssh " + bean.getUser() + "@" + bean.getHost() + " <<EOF " + bean.getCommand() + ";exit \nEOF ");
        return exec(bean);
    }

    public ShellBean exec(ShellBean bean) {
        try {
            Session session = getConnection().openSession();
            bean = ShellUtils.remoteExec(bean, session);
            session.close();
            conn.close();
        }catch (Exception e){
            e.printStackTrace();
        }

        return bean;
    }
}
