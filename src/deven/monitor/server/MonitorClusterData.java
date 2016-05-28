package deven.monitor.server;

import java.util.List;

import pipe.monitor.Monitor.ClusterMonitor;

public class MonitorClusterData {
	public long clusterId;
	public int noOfNodes;
	public List<Integer> processId;
	public List<Integer> enqueuedWork;
	public List<Integer> processedWork;
	public List<Integer> stolenWork;
	public Long lastTick;
	
	public MonitorClusterData(Long l,Long m){
		this.clusterId = l;
		this.lastTick = m;
	}

}
