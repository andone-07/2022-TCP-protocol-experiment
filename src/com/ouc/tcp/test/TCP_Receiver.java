/***************************2.1: ACK/NACK*****************/
/***** Feng Hong; 2015-12-09******************************/
package com.ouc.tcp.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;

import com.ouc.tcp.client.TCP_Receiver_ADT;
import com.ouc.tcp.message.*;
import com.ouc.tcp.tool.TCP_TOOL;

public class TCP_Receiver extends TCP_Receiver_ADT {
	
	private TCP_PACKET ackPack;	//回复的ACK报文段
	private int sequence=1;// 当前待接收的包序号
	private int last_sequence = -1; // 上一次收到包的序号
	private int expectedSequence = 0;  // 期望收到的seq
	private Hashtable<Integer, TCP_PACKET> storagePackets = new Hashtable<>(); // 用于缓存失序分组
	
	/*构造函数*/
	public TCP_Receiver() {
		super();	//调用超类构造函数
		super.initTCP_Receiver(this);	//初始化TCP接收端
	}

	@Override
	//接收到数据报：检查校验和，设置回复的ACK报文段
	public void rdt_recv(TCP_PACKET recvPack) {
		
		//检查校验码，生成ACK
		if(CheckSum.computeChkSum(recvPack) == recvPack.getTcpH().getTh_sum()) {
			int currentSequence = (recvPack.getTcpH().getTh_seq() - 1) / 100;  // 当前包的seq
			if (expectedSequence == currentSequence) {  // 收到的seq=expectedSequence

				// 将接收到的正确的包插入 data 队列，准备交付
				dataQueue.add(recvPack.getTcpS().getData());
				expectedSequence += 1 ;

				// 处理缓存数据
				for (int i = expectedSequence; ; i ++ ) {
					if (storagePackets.containsKey(i)) {
						dataQueue.add(storagePackets.get(i).getTcpS().getData());
						expectedSequence += 1;
						storagePackets.remove(i);
					} else {
						break;
					}
				}

				//交付数据（每20组数据交付一次）
				if(dataQueue.size() >= 20)
					deliver_data();

			} else {

				// 缓存失序分组
				if (!storagePackets.containsKey(currentSequence) && currentSequence > expectedSequence) {
					storagePackets.put(currentSequence, recvPack);
				}
			}
			/*
			//生成ACK报文段（设置确认号）
			tcpH.setTh_ack(recvPack.getTcpH().getTh_seq());
			ackPack = new TCP_PACKET(tcpH, tcpS, recvPack.getSourceAddr());
			tcpH.setTh_sum(CheckSum.computeChkSum(ackPack));
			//回复ACK报文段
			reply(ackPack);			
			
			if(recvPack.getTcpH().getTh_seq()!=sequence){
				//将接收到的正确有序的数据插入data队列，准备交付
				dataQueue.add(recvPack.getTcpS().getData());
				sequence=recvPack.getTcpH().getTh_seq();
				//sequence++;
			}
			*/
			
		}
		/*
		else{
			System.out.println("Recieve Computed: "+CheckSum.computeChkSum(recvPack));
			System.out.println("Recieved Packet"+recvPack.getTcpH().getTh_sum());
			System.out.println("Problem: Packet Number: "+recvPack.getTcpH().getTh_seq()+" + InnerSeq:  "+sequence);
			tcpH.setTh_ack(-1);
			ackPack = new TCP_PACKET(tcpH, tcpS, recvPack.getSourceAddr());
			tcpH.setTh_sum(CheckSum.computeChkSum(ackPack));
			//回复ACK报文段
			reply(ackPack);
		}
		*/
		//生成ACK报文段（设置确认号）
		tcpH.setTh_ack((expectedSequence - 1) * 100 + 1);
		ackPack = new TCP_PACKET(tcpH, tcpS, recvPack.getSourceAddr());
		tcpH.setTh_sum(CheckSum.computeChkSum(ackPack));
		reply(ackPack);  // 回复ACK报文段
		System.out.println();
	}

	@Override
	//交付数据（将数据写入文件）；不需要修改
	public void deliver_data() {
		//检查dataQueue，将数据写入文件
		File fw = new File("recvData.txt");
		BufferedWriter writer;
		
		try {
			writer = new BufferedWriter(new FileWriter(fw, true));
			
			//循环检查data队列中是否有新交付数据
			while(!dataQueue.isEmpty()) {
				int[] data = dataQueue.poll();
				
				//将数据写入文件
				for(int i = 0; i < data.length; i++) {
					writer.write(data[i] + "\n");
				}
				
				writer.flush();		//清空输出缓存
			}
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	//回复ACK报文段
	public void reply(TCP_PACKET replyPack) {
		//设置错误控制标志，eflag==7，“出错/丢包/延迟”
		tcpH.setTh_eflag((byte)7);
				
		//发送数据报
		client.send(replyPack);
	}
	
}
