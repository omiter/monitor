package com.gome.monitor.bean;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ShellBean {
    private String user;
    private String host;
    private String command;
    private int status;
    private String out;
    private String error;
}
