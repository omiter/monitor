package com.gome.monitor.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JobDataCount {
    private String id;
    private String job_id;
    private String exec_time;
    private String count;
    private String job_date;
    private String state;
}
