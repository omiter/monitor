package com.gome.monitor;

import com.gome.monitor.tasks.ScheduledTasks;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MonitorApplicationTests {

	@Autowired
	ScheduledTasks scheduledTasks;

	@Test
	public void contextLoads() {
		scheduledTasks.test();
	}

}
