package com.gome.monitor.component;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class PropConfig {
    @Value("${monitor.ignore.status}")
    String ignoreStatus;
    @Value("${spring.mail.to}")
    String mailTo;
    @Value("${shell.host}")
    String shellHost;
    @Value("${shell.username}")
    String shellUser;
    @Value("${shell.password}")
    String shellPwd;
    @Value("${shell.app.sh}")
    String shellAppScript;
    @Value("${shell.logerr.sh}")
    String shellLogErrScript;

}
