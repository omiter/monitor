package com.gome.monitor.component;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class EmailConfig {
    @Value("${spring.mail.properties.mail.from}")
    private String emailFrom;
}
