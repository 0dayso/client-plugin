package com.cninfo.meyes.client.plugin.impl.server;

import java.io.IOException;

import com.cninfo.meyes.client.model.EventData;
import com.cninfo.meyes.client.plugin.impl.AbstractGatherPlugin;
import com.cninfo.meyes.client.util.OsUtil;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class HardWareForDellAndWin extends AbstractGatherPlugin {

	public void process(EventData event) throws Exception {
		// TODO Auto-generated method stub
		event.getDoc().put("dimm", memory());
		event.getDoc().put("battery", battery());
		event.getDoc().put("chassis", chassis());
		event.getDoc().put("pwrsupplies", pwrsupplies());
		event.getDoc().put("storage", storage());
		event.getDoc().put("fans", fans());

	}

	/**
	 * 收集DELL服务器的内存卡信息
	 * 
	 * @return
	 * @throws IOException
	 */
	private DBObject memory() throws IOException {
		String[] result = OsUtil.execAsArray(new String[]{"omreport",
				"chassis", "memory"});
		BasicDBList list = new BasicDBList();
		DBObject obj = null;
		for (int i = 0; i < result.length; i++) {
			// System.out.println("Line:"+line);
			if (result[i].startsWith("Index")
					&& getValue(result[i]).matches("^\\s*$")) {
				i = i + 5;
				continue;
			}
			if (result[i].startsWith("Index")) {
				obj = new BasicDBObject();
				list.add(obj);
				obj.put(ServerField.FIELDS_OF_DIMM[0], getValue(result[i]));
			} else if (result[i].startsWith("Status") && obj != null) {
				obj.put(ServerField.FIELDS_OF_DIMM[1], this.getValue(result[i]));
			} else if (result[i].startsWith("Connector Name") && obj != null) {
				obj.put(ServerField.FIELDS_OF_DIMM[2], this.getValue(result[i]));
			} else if (result[i].startsWith("Type") && obj != null) {
				obj.put(ServerField.FIELDS_OF_DIMM[3], this.getValue(result[i]));
			} else if (result[i].startsWith("Size") && obj != null) {
				obj.put(ServerField.FIELDS_OF_DIMM[4], this.getValue(result[i]));
			}
		}
		return list;
	}

	/**
	 * 收集DELL服务器的内存卡信息
	 * 
	 * @return
	 * @throws IOException
	 */
	private DBObject fans() throws IOException {
		String[] result = OsUtil.execAsArray(new String[]{"omreport",
				"chassis", "fans"});
		BasicDBList list = new BasicDBList();
		DBObject obj = null;
		for (int i = 0; i < result.length; i++) {
			// System.out.println("Line:"+line);

			if (result[i].startsWith("Index")) {
				obj = new BasicDBObject();
				list.add(obj);
				obj.put(ServerField.FIELDS_OF_FANS[0], getValue(result[i]));
			} else if (result[i].startsWith("Status") && obj != null) {
				obj.put(ServerField.FIELDS_OF_FANS[1], this.getValue(result[i]));
			} else if (result[i].startsWith("Probe Name") && obj != null) {
				obj.put(ServerField.FIELDS_OF_FANS[2], this.getValue(result[i]));
			} else if (result[i].startsWith("Reading") && obj != null) {
				obj.put(ServerField.FIELDS_OF_FANS[3], this.getValue(result[i]));
			} 
		}
		return list;
	}

	/**
	 * 收集DELL服务器的RAID卡电池信息
	 * 
	 * @return
	 * @throws IOException
	 */
	private DBObject battery() throws IOException {
		String[] result = OsUtil.execAsArray(new String[]{"omreport",
				"storage", "battery"});
		BasicDBList list = new BasicDBList();
		DBObject obj = null;

		for (int i = 0; i < result.length; i++) {

			if (result[i].startsWith("ID")) {
				obj = new BasicDBObject();
				list.add(obj);
				obj.put(ServerField.FIELDS_OF_STORAGE[0], getValue(result[i]));
			} else if (result[i].startsWith("Status") && obj != null) {
				obj.put(ServerField.FIELDS_OF_STORAGE[1],
						this.getValue(result[i]));
			} else if (result[i].startsWith("Name") && obj != null) {
				obj.put(ServerField.FIELDS_OF_STORAGE[2],
						this.getValue(result[i]));
			} else if (result[i].startsWith("State") && obj != null) {
				obj.put(ServerField.FIELDS_OF_STORAGE[3],
						this.getValue(result[i]));
			}
		}

		return list;
	}

	/**
	 * 收集DELL服务器的面板信息
	 * 
	 * @return
	 * @throws IOException
	 */
	private DBObject chassis() throws IOException {
		String[] result = OsUtil.execAsArray(new String[]{"omreport",
				"chassis", "info"});
		DBObject obj = new BasicDBObject();

		String infoSystem = "System";
		String infoSerial = "SerialNo";
		String infoversion = "ROMversion";

		for (String line : result) {
			if (line.startsWith("Index")) {
				obj.put(getName(line), getValue(line));
			} else if (line.startsWith("iDRAC6 Version") && obj != null) {
				obj.put(infoversion, getValue(line));
			} else if (line.startsWith("Chassis Model") && obj != null) {
				obj.put(infoSystem, getValue(line));
			} else if (line.startsWith("Chassis Service Tag") && obj != null) {
				obj.put(infoSerial, getValue(line));
			} else if (line.startsWith("Express Service Code") && obj != null) {
				obj.put(this.getName(line), getValue(line));
			} else if (line.startsWith("Flash chassis identify LED state")
					&& obj != null) {
				obj.put(this.getName(line), getValue(line));
			} else if (line
					.startsWith("Flash chassis identify LED timeout value")
					&& obj != null) {
				obj.put(this.getName(line), getValue(line));
			} else if (line.startsWith("Host Name") && obj != null) {
				obj.put(this.getName(line), getValue(line));
			}
		}
		return obj;
	}

	/**
	 * 收集DELL服务器的硬盘信息
	 * 
	 * @return
	 * @throws IOException
	 */

	private DBObject storage() throws IOException {
		String[] result = OsUtil.execAsArray(new String[]{"omreport",
				"storage", "pdisk", "controller=0"});
		BasicDBList list = new BasicDBList();
		DBObject obj = null;

		for (int i = 0; i < result.length; i++) {
			// System.out.println("Line:"+line);
			if (result[i].startsWith("ID")
					&& getValue(result[i]).matches("^\\s*$")) {
				i = i + 31;
				continue;
			}
			if (result[i].startsWith("ID")) {
				obj = new BasicDBObject();
				list.add(obj);
				obj.put(ServerField.FIELDS_OF_STORAGE[0], getValue(result[i]));
			} else if (result[i].startsWith("Status") && obj != null) {
				obj.put(ServerField.FIELDS_OF_STORAGE[1],
						this.getValue(result[i]));
			} else if (result[i].startsWith("Name") && obj != null) {
				obj.put(ServerField.FIELDS_OF_STORAGE[2],
						this.getValue(result[i]));
			} else if (result[i].startsWith("State") && obj != null) {
				obj.put(ServerField.FIELDS_OF_STORAGE[3],
						this.getValue(result[i]));
			} else if (result[i].startsWith("Bus Protocol") && obj != null) {
				obj.put(ServerField.FIELDS_OF_STORAGE[4],
						this.getValue(result[i]));
			} else if (result[i].startsWith("Capacity") && obj != null) {
				obj.put(ServerField.FIELDS_OF_STORAGE[5],
						this.getValue(result[i]));
			} else if (result[i].startsWith("Product ID") && obj != null) {
				obj.put(ServerField.FIELDS_OF_STORAGE[6],
						this.getValue(result[i]));
			} else if (result[i].startsWith("Serial No") && obj != null) {
				obj.put(ServerField.FIELDS_OF_STORAGE[7],
						this.getValue(result[i]));
			} else if (result[i].startsWith("Part Number") && obj != null) {
				obj.put(ServerField.FIELDS_OF_STORAGE[8],
						this.getValue(result[i]));
			} else if (result[i].startsWith("SAS Address") && obj != null) {
				obj.put(ServerField.FIELDS_OF_STORAGE[9],
						this.getValue(result[i]));
			}
		}

		return list;
	}

	/**
	 * 收集DELL服务器的电源信息
	 * 
	 * @return
	 * @throws IOException
	 */

	private DBObject pwrsupplies() throws IOException {
		String[] result = OsUtil.execAsArray(new String[]{"omreport",
				"chassis", "pwrsupplies"});
		BasicDBList list = new BasicDBList();
		DBObject obj = null;

		for (int i = 0; i < result.length; i++) {
			// System.out.println("Line:"+line);
			if (result[i].startsWith("Index")
					&& getValue(result[i]).matches("^\\s*$")) {
				i = i + 9;
				continue;
			}
			if (result[i].startsWith("Index")) {
				obj = new BasicDBObject();
				list.add(obj);
				obj.put(ServerField.FIELDS_OF_POWER[0], getValue(result[i]));
			} else if (result[i].startsWith("Status") && obj != null) {
				obj.put(ServerField.FIELDS_OF_POWER[1],
						this.getValue(result[i]));
			} else if (result[i].startsWith("Location") && obj != null) {
				obj.put(ServerField.FIELDS_OF_POWER[2],
						this.getValue(result[i]));
			} else if (result[i].startsWith("Type") && obj != null) {
				obj.put(ServerField.FIELDS_OF_POWER[3],
						this.getValue(result[i]));
			} else if (result[i].startsWith("Rated Input Wattage")
					&& obj != null) {
				obj.put(ServerField.FIELDS_OF_POWER[4],
						this.getValue(result[i]));
			} else if (result[i].startsWith("Maximum Output Wattage")
					&& obj != null) {
				obj.put(ServerField.FIELDS_OF_POWER[5],
						this.getValue(result[i]));
			} else if (result[i].startsWith("Firmware Version") && obj != null) {
				obj.put(ServerField.FIELDS_OF_POWER[6],
						this.getValue(result[i]));
			} else if (result[i].startsWith("Online Status") && obj != null) {
				obj.put(ServerField.FIELDS_OF_POWER[7],
						this.getValue(result[i]));
			} else if (result[i].startsWith("Power Monitoring Capable")
					&& obj != null) {
				obj.put(ServerField.FIELDS_OF_POWER[8],
						this.getValue(result[i]));
			}

		}

		return list;
	}

	// /**收集DELL服务器的硬盘控制器信息
	// * @return
	// * @throws IOException
	// */
	//
	// private DBObject controller() throws IOException{
	// String[] result = OsUtil.execAsArray(new
	// String[]{"omreport","storage","controller"});
	// BasicDBList list = new BasicDBList();
	// DBObject obj = null;
	//
	//
	// for(int i=0;i< result.length;i++){
	// //System.out.println("Line:"+line);
	//
	// if(result[i].startsWith("ID") ){
	// obj = new BasicDBObject();
	// list.add(obj);
	// obj.put(ServerField.FIELDS_OF_STORAGE_CONTROLLER[0],
	// getValue(result[i]));
	// }else if(result[i].startsWith("Status") && obj != null){
	// obj.put(ServerField.FIELDS_OF_STORAGE_CONTROLLER[1],
	// this.getValue(result[i]));
	// }else if(result[i].startsWith("Name") && obj != null){
	// obj.put(ServerField.FIELDS_OF_STORAGE_CONTROLLER[2],
	// this.getValue(result[i]));
	// }else if(result[i].startsWith("Slot ID") && obj != null){
	// obj.put(ServerField.FIELDS_OF_STORAGE_CONTROLLER[3],
	// this.getValue(result[i]));
	// }else if(result[i].startsWith("State") && obj != null){
	// obj.put(ServerField.FIELDS_OF_STORAGE_CONTROLLER[4],
	// this.getValue(result[i]));
	// }else if(result[i].startsWith("Firmware Version") && obj != null){
	// obj.put(ServerField.FIELDS_OF_STORAGE_CONTROLLER[5],
	// this.getValue(result[i]));
	// }else if(result[i].startsWith("Rebuild Rate") && obj != null){
	// obj.put(ServerField.FIELDS_OF_STORAGE_CONTROLLER[6],
	// this.getValue(result[i]));
	// }else if(result[i].startsWith("BGI Rate ") && obj != null){
	// obj.put(ServerField.FIELDS_OF_STORAGE_CONTROLLER[7],
	// this.getValue(result[i]));
	// }else if(result[i].startsWith("Check Consistency Rate") && obj != null){
	// obj.put(ServerField.FIELDS_OF_STORAGE_CONTROLLER[8],
	// this.getValue(result[i]));
	// }else if(result[i].startsWith("Reconstruct Rate") && obj != null){
	// obj.put(ServerField.FIELDS_OF_STORAGE_CONTROLLER[9],
	// this.getValue(result[i]));
	// }else if(result[i].startsWith("Number of Connectors") && obj != null){
	// obj.put(ServerField.FIELDS_OF_STORAGE_CONTROLLER[10],
	// this.getValue(result[i]));
	// }else if(result[i].startsWith("Cache Memory Size") && obj != null){
	// obj.put(ServerField.FIELDS_OF_STORAGE_CONTROLLER[11],
	// this.getValue(result[i]));
	// }
	// }
	//
	//
	// return list;
	// }
	//

	private String getName(String line) {
		return line.replaceAll(":.*$", "").replaceAll(" +", "");
	}

	private String getValue(String line) {
		return line.replaceAll("^[^:]*:", "").replaceAll("^ +", "")
				.replaceAll(" +$", "");
	}

	// private int getValueByInt(String line){
	// return Integer.parseInt(line.replaceAll("^[^:]*:", "").replaceAll(" *",
	// ""));
	// }

}
