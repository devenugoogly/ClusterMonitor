package deven.monitor.server;

import java.util.concurrent.LinkedBlockingDeque;

import com.google.protobuf.GeneratedMessage;

import io.netty.channel.Channel;
import pipe.monitor.Monitor.ClusterMonitor;

public class MonitorQueue {
	LinkedBlockingDeque<com.google.protobuf.GeneratedMessage> inbound;
	
	public MonitorQueue(){
		init();
	}
	
	protected void init() {
		inbound = new LinkedBlockingDeque<com.google.protobuf.GeneratedMessage>();
	}
	
	public void enqueueRequest(ClusterMonitor req, Channel notused) {
		try {
			System.out.println("Request received");
			inbound.put(req);
		} catch (InterruptedException e) {
			System.out.println("message not enqueued for processing "+e.toString());
		}
	}
	
	public ClusterMonitor dequeueRequest(){
		try{
			GeneratedMessage msg = inbound.take();
			if(msg instanceof ClusterMonitor){
				return (ClusterMonitor)msg;
			}
		}catch(InterruptedException ex){
			System.out.println(ex.toString());
		}
		return null;
		
	}
}
