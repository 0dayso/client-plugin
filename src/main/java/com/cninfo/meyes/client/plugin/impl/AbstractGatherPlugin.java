package com.cninfo.meyes.client.plugin.impl;

import java.util.Map;

import com.cninfo.meyes.client.model.EventData;
import com.cninfo.meyes.client.plugin.GatherPlugin;

/**
 * @author lunianping
 *
 */
public abstract class AbstractGatherPlugin implements GatherPlugin {
	
	private Map<String,Object> params;
	
	public void before(Map<String,Object> params){
		this.params = params;
	}
	
	public void failed(EventData event,Map<String, Object> p){
//		event.getDoc().put("status", p.get("message"));
	}
	
	public void after(){
		
	}
	
	protected Object getParam(String name){
		return params.get(name);
	}
	
	protected void setParam(String name,Object value){
		params.put(name,value);
	}
	
}
