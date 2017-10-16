package com.gome.monitor;

import com.gome.monitor.component.MyEmailReceiver;
import com.gome.monitor.component.ShellConnection;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Properties;

@SpringBootApplication
public class MonitorApplication {

	public static void main(String[] args) {
		SpringApplication.run(MonitorApplication.class, args);
	}


	@Bean
	public MyEmailReceiver getMyEmailReciver(){
		MyEmailReceiver receiver = new MyEmailReceiver("pop.qq.com","gome_bigdata@qq.com","vucledwtahfbiidf");
		Properties props = new Properties();
		props.put("mail.pop3.ssl.enable", true);
		props.put("mail.pop3.host", "pop.qq.com");
		props.put("mail.pop3.port", 995);
		receiver.setJavaMailProperties(props);
		receiver.setMaxFetchSize(10);
		return receiver;
	}

	@Bean
	public ShellConnection getConnection(){
		return new ShellConnection("10.112.167.26","root","3edcXZAQ!");
	}
}
