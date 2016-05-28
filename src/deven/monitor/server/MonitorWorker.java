package deven.monitor.server;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import pipe.monitor.Monitor.ClusterMonitor;

public class MonitorWorker extends Thread{
	HashMap<Long,MonitorClusterData> data;
	MonitorQueue queue;
	Writer writer = null;


	
	public MonitorWorker(MonitorQueue qu){
		this.queue = qu;
		data = new HashMap<Long,MonitorClusterData>();	
	    try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("log.txt"), "utf-8"));
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	@Override
	public void run() {
		while (true) {
			if(queue.inbound.size() > 0){
				ClusterMonitor msg = queue.dequeueRequest();
				long clusterId = msg.getClusterId();
				if(data.containsKey(clusterId)){
					MonitorClusterData monitorData = data.get(clusterId);
					if(msg.getTick() > monitorData.lastTick){
						monitorData.noOfNodes = msg.getNumNodes();
						monitorData.processId = msg.getProcessIdList();
						monitorData.enqueuedWork = msg.getEnqueuedList();
						monitorData.processedWork = msg.getProcessedList();
						monitorData.stolenWork = msg.getStolenList();
					}else{
						System.out.println("Rejecting message from cluster ID "+clusterId);
						System.out.println("Invalid tick time");
					}
				}else{
					MonitorClusterData monitorData = new MonitorClusterData(clusterId, msg.getTick());
					monitorData.noOfNodes = msg.getNumNodes();
					monitorData.processId = msg.getProcessIdList();
					monitorData.enqueuedWork = msg.getEnqueuedList();
					monitorData.processedWork = msg.getProcessedList();
					monitorData.stolenWork = msg.getStolenList();
					data.put(clusterId, monitorData);
				}
				try{
					printStatus();
				}catch(IOException ex){
					System.out.println(ex.toString());
				}
			}else{
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public void printStatus() throws IOException{
		Date dt = new Date();
		String date = Long.toString(dt.getTime());
		Runtime.getRuntime().exec("clear");
		System.out.println("Date\t\tClusterId\tPId\tEWork\tPWork\tSWork");
		writer.write("Date\t\tClusterId\t\tPId\tEWork\tPWork\tSWork");
		writer.write("\n");
		writer.flush();
		for (Map.Entry<Long, MonitorClusterData> entry : data.entrySet()) {
		    Long clusterId = entry.getKey();
		    MonitorClusterData d = entry.getValue();
		    for(int i=0;i<d.noOfNodes;i++){
		    	if(i < d.processId.size() && i < d.enqueuedWork.size()&& i < d.processedWork.size() &&  i < d.stolenWork.size()){
			    	String str = date+"\t\t"+clusterId+"\t\t"+d.processId.get(i)+"\t"+d.enqueuedWork.get(i)+"\t"+d.processedWork.get(i)+"\t"+d.stolenWork.get(i);
			    	System.out.println(str);
			    	writer.write(str);
			    	writer.write("\n");
			    	writer.flush();
		    	}
		    }
		}
		writer.write("\n");
    	writer.flush();
	}
	
}
