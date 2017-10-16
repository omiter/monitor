package com.gome.monitor.component;

import org.springframework.integration.mail.Pop3MailReceiver;

import javax.mail.Message;
import javax.mail.MessagingException;
import java.util.Arrays;
import java.util.Date;

public class MyEmailReceiver extends Pop3MailReceiver {

    public MyEmailReceiver(String host, String username, String password) {
        super(host, username, password);
        this.setProtocol("pop3");
    }

    @Override
    protected Message[] searchForNewMessages() throws MessagingException {
        int messageCount = this.getFolder().getMessageCount();
        if (messageCount == 0) {
            return new Message[0];
        }
        Message[] messages = this.getFolder().getMessages();
        if (messages.length >1){
            Arrays.sort(messages, (m1, m2) -> {
                Date d1 = null;
                Date d2 = null;
                try {
                    d1 = m1.getSentDate();
                    d2 = m2.getSentDate();
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
                assert d1 != null;
                assert d2 != null;
                return Long.compare(d2.getTime(), d1.getTime());
            });
        }
        return messages;
    }


}
