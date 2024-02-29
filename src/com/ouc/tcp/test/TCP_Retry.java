package com.ouc.tcp.test;

import com.ouc.tcp.client.Client;
import com.ouc.tcp.message.TCP_PACKET;

import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;

public class TCP_Retry extends TimerTask{
	private Client client;
    private Sender_Window window;


    public TCP_Retry(Client client, Sender_Window window) {
        this.client = client;
        this.window = window;
    }

    @Override
    public void run() {

        // 清空拥塞避免计数器
        window.setCongestionAvoidanceCount(0);

        // 超时重传
        System.out.println("***** Timeout Retransmit *****");
        if (window.getCwnd() / 2 < 2) {
            System.out.println("ssthresh: " + window.getSsthresh() + " ---> 2");
            window.setSsthresh(2);
        } else {
            System.out.println("ssthresh: " + window.getSsthresh() + " ---> " + window.getCwnd() / 2);
            window.setSsthresh(window.getCwnd() / 2);
        }
        System.out.println("cwnd: " + window.getCwnd() + " ---> 1");
        window.setCwnd(1);

        window.appendChange(window.getLastACKSequence());

    }
}
