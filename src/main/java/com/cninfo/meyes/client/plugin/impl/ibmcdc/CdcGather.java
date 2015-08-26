package com.cninfo.meyes.client.plugin.impl.ibmcdc;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cninfo.meyes.client.model.EventData;
import com.cninfo.meyes.client.plugin.impl.AbstractGatherPlugin;
import com.datamirror.ea.api.ApiException;
import com.datamirror.ea.api.DataSource;
import com.datamirror.ea.api.DefaultContext;
import com.datamirror.ea.api.Toolkit;
import com.datamirror.ea.api.publisher.Publisher;
import com.datamirror.ea.api.publisher.Subscription;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class CdcGather extends AbstractGatherPlugin {
	private static final Log logger = LogFactory.getLog(CdcGather.class);
	
	private DataSource ds;
	
	private BasicDBList publishers;
	private BasicDBList subscriptions;
	
	public CdcGather(){
		this.publishers = new BasicDBList();
		this.subscriptions = new BasicDBList();
	}

	public void process(EventData event) throws Exception {
		// TODO Auto-generated method stub
		
		String user = (String)getParam("user");
		String password = (String)getParam("password");
		String id = event.getId();
		String[] strs = id.split(",");
		String host = strs[1];
		int port = Integer.parseInt(strs[2]);
		
		ds = this.connectToAccessServer(user, password, host, port);
		this.getSubscription();
		close();
		event.getDoc().put("Subscriptions", this.subscriptions);
		event.getDoc().put("Publishers", this.publishers);
	}

	/**
	 * 连接AccessServer
	 * @param user
	 * @param password
	 * @param host
	 * @param port
	 * @return
	 * @throws ApiException
	 */
	private DataSource connectToAccessServer(String user, String password,
			String host, int port) throws ApiException {
		DefaultContext c = new DefaultContext();
		c.setString(DataSource.User, user);
		c.setString(DataSource.Password, password);
		c.setString(DataSource.Hostname, host);
		c.setInt(DataSource.Port, port);
		DataSource dataSource = Toolkit.getDefaultToolkit().createDataSource();
		dataSource.connect(c);
		return dataSource;
	}
	
	private void close(){
		try{
			if(ds != null){
				ds.close();
			}
		}catch(Exception e){}
	}
			
	/**
	 * 获取所有订单
	 * @return
	 */
	private void getSubscription(){
		//BasicDBList list = new BasicDBList();
		Publisher[] ps = ds.getPublishers();
		for (Publisher publisher : ps) {
			String name = publisher.getName();
			String pstatus = "failed";
			boolean retry = true;
			for(int i = 0 ;i<2 && retry;i++ ){
				try {
					publisher.connect();
					pstatus = "Ok";
					retry = false;
					String[] ns = publisher.getSubscriptionNames();
					for (String n : ns) {
						DBObject obj = new BasicDBObject();
						Subscription sub = publisher.getSubscription(n);
						
						obj.put("Subscription", sub.getName());
						byte[] status = sub.getLiveActivityStatus();
						obj.put("activity", this.getActivity(status[0]));
						obj.put("status", this.getStatus(status[1]));
						obj.put("publisher", publisher.getName());
						this.subscriptions.add(obj);
						
						/*
						PerformanceStatisticData psd = new PerformanceStatisticData();
						sub.getPerformanceStatistics(psd);
						
						DBPath[] paths = sub.getSubscribedTableDBPaths();
						for(DBPath path : paths){
							System.out.println(path.getName()+" / "+path.getFullName());
							SubscribedTable[] tbs = sub.getSubscribedTables(path);
							for(SubscribedTable tb : tbs){
								System.out.println(tb.getName());
							}
						}
						*/
					}
					publisher.disconnect();
				} catch (ApiException e) {
					//e.printStackTrace();
					retry = true;
					logger.warn("Connect to publisher '"+name+"' failed,sleep 100ms and retry "+i);
					try {
						Thread.sleep(100);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				}
			}
			DBObject obj = new BasicDBObject();
			obj.put("name", name);
			obj.put("status", pstatus);
			this.publishers.add(obj);
		}
	}
	
	/**订单状态
	 * @param activity
	 * @return
	 */
	private String getActivity(byte activity){
		String re = null;
		switch(activity){
			case Subscription.LIVE_ACTIVITY_DESCRIBE : re = "DESCRIBE";break;
			case Subscription.LIVE_ACTIVITY_IDLE : re = "IDLE";break;
			case Subscription.LIVE_ACTIVITY_MIRROR : re = "MIRROR";break;
			case Subscription.LIVE_ACTIVITY_NET_CHANGE : re = "NET_CHANGE";break;
			case Subscription.LIVE_ACTIVITY_REFRESH : re = "REFRESH";break;
		}
		return re;
	}
	
	/**
	 * 订单状态
	 * @param status
	 * @return
	 */
	private String getStatus(byte status){
		String re = null;
		switch(status){
			case Subscription.LIVE_STATUS_ACTIVE : re = "ACTIVE";break;
			case Subscription.LIVE_STATUS_BLOCKED : re = "BLOCKED";break;
			case Subscription.LIVE_STATUS_DS_CONNECTING_WITH_TARGET : re = "DS_CONNECTING_WITH_TARGET";break;
			case Subscription.LIVE_STATUS_DS_JOB_ENDING : re = "DS_JOB_ENDING";break;
			case Subscription.LIVE_STATUS_DS_STARTING_JOB : re = "DS_STARTING_JOB";break;
			case Subscription.LIVE_STATUS_DS_WAITING_FOR_JOB_TO_START : re = "DS_WAITING_FOR_JOB_TO_START";break;
			case Subscription.LIVE_STATUS_IDLE : re = "IDLE";break;
			case Subscription.LIVE_STATUS_RECOVERY : re = "RECOVERY";break;
			case Subscription.LIVE_STATUS_START : re = "START";break;
			case Subscription.LIVE_STATUS_WAIT : re = "WAIT";break;
		}
		return re;
	}

	public static void main(String[] args) {
		CdcGather lv = new CdcGather();

		EventData event = new EventData();
		event.setCategory("IBMCDC");
		event.setId("IBMCDC,172.30.3.214,10101");
		//event.setId("IBMCDC,172.26.2.92,10101");
		
		/*
		System.out.println(Subscription.LIVE_ACTIVITY_DESCRIBE+"/"+Subscription.LIVE_ACTIVITY_IDLE+"/"+Subscription.LIVE_ACTIVITY_MIRROR+"/"+Subscription.LIVE_ACTIVITY_NET_CHANGE+"/"+Subscription.LIVE_ACTIVITY_REFRESH);
		System.out.println(Subscription.LIVE_STATUS_ACTIVE+"/"+Subscription.LIVE_STATUS_BLOCKED+"/"+
		Subscription.LIVE_STATUS_DS_CONNECTING_WITH_TARGET+"/"+Subscription.LIVE_STATUS_DS_JOB_ENDING+"/"+Subscription.LIVE_STATUS_DS_STARTING_JOB+"/"+Subscription.LIVE_STATUS_DS_WAITING_FOR_JOB_TO_START+"/"+
				Subscription.LIVE_STATUS_IDLE+"/"+Subscription.LIVE_STATUS_RECOVERY+"/"+Subscription.LIVE_STATUS_START+"/"+Subscription.LIVE_STATUS_WAIT);
		*/
		Map<String,Object> params = new HashMap<String,Object>();
		String user = "cdcadmin";
		//String password = "cdcadmin123";
		String password = "xayw6da9dayd";
		
		params.put("user", user);
		params.put("password", password);
		
		lv.before(params);
		try {
			lv.process(event);
			System.out.println(event.getDoc());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
