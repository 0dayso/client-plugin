package com.cninfo.meyes.client.plugin.impl.server;

import java.io.IOException;

import com.cninfo.meyes.client.model.EventData;
import com.cninfo.meyes.client.plugin.impl.AbstractGatherPlugin;
import com.cninfo.meyes.client.util.OsUtil;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class HardWareForHPAndWin extends AbstractGatherPlugin {

	public void process(EventData event) throws Exception {
		// TODO Auto-generated method stub
		event.getDoc().put("dimm", memory());
		event.getDoc().put("battery", batteries());
		event.getDoc().put("chassis", chassis());
		event.getDoc().put("pwrsupplies", pwrsupplies());
		event.getDoc().put("storage", storage());
		event.getDoc().put("fans", fans());

	}

	/**
	 * 收集hp服务器的内存卡卡电池
	 * 
	 * @return
	 * @throws IOException
	 */
	private DBObject memory() throws IOException {

		String[] result = OsUtil.execAsArray(new String[]{"/bin/bash", "-c",
				"hpasmcli -s 'show DIMM'"});

		BasicDBList list = new BasicDBList();
		DBObject obj = null;
		String processvalue = "";

		for (int i = 0; i < result.length; i++) {
			if (result[i].startsWith("Processor #:")
					&& getValue(result[i]).matches("^\\s*$")) {
				i = i + 10;
				continue;
			}
			if (result[i].startsWith("Processor #:")
					|| result[i].startsWith("Cartridge #:")) {
				obj = new BasicDBObject();
				list.add(obj);
				processvalue = getValue(result[i]);
			} else if (result[i].startsWith("Module #:")) {
				obj.put(ServerField.FIELDS_OF_DIMM[0], processvalue + ":"
						+ this.getValue(result[i]));
			} else if (result[i].startsWith("Status") && obj != null) {
				obj.put(ServerField.FIELDS_OF_DIMM[1], this.getValue(result[i]));
			} else if (result[i].startsWith("Size") && obj != null) {
				obj.put(ServerField.FIELDS_OF_DIMM[4], this.getValue(result[i]));
			} else if (result[i].startsWith("Memory Type") && obj != null) {
				obj.put(ServerField.FIELDS_OF_DIMM[2], "");

				obj.put(ServerField.FIELDS_OF_DIMM[3], this.getValue(result[i]));
			}
		}
		return list;
	}

	/**
	 * 收集hp服务器的RAID卡电池
	 * 
	 * @return
	 * @throws IOException
	 */
	private DBObject batteries() throws IOException {
		String[] result = OsUtil.execAsArray(new String[]{"hpacucli", "ctrl",
				"all", "show", "status"});
		BasicDBList list = new BasicDBList();
		DBObject obj = null;

		for (int i = 0; i < result.length; i++) {
			// System.out.println("Line:"+line);

			if (result[i].contains("Battery")
					&& result[i].contains("Capacitor")) {
				obj = new BasicDBObject();
				list.add(obj);
				obj.put(ServerField.FIELDS_OF_STORAGE_BATTERY[0], 0);
				obj.put(ServerField.FIELDS_OF_STORAGE_BATTERY[1],
						getValue(result[i]));
				obj.put(ServerField.FIELDS_OF_STORAGE_BATTERY[2], "");
				obj.put(ServerField.FIELDS_OF_STORAGE_BATTERY[3], "");

			}
		}

		return list;
	}

	/**
	 * 收集HP服务器的风扇信息
	 * 
	 * @return
	 * @throws IOException
	 */
	private DBObject fans() throws IOException {
		String[] result = OsUtil.execAsArray(new String[]{"/bin/bash", "-c",
				"hpasmcli  -s 'SHOW FANS'"});
		BasicDBList list = new BasicDBList();
		DBObject obj = null;
		for (int i = 0; i < result.length; i++) {
			// System.out.println("Line:"+line);

			if (result[i].startsWith("#")) {
				obj = new BasicDBObject();
				list.add(obj);
				String[] values = result[i].split("\\s+");

				for (int j = 0; j < values.length; j++) {
					obj.put(ServerField.FIELDS_OF_FANS[0], values[0]);
					obj.put(ServerField.FIELDS_OF_FANS[2], values[1]);
					obj.put(ServerField.FIELDS_OF_FANS[1], values[2]);
					obj.put(ServerField.FIELDS_OF_FANS[3], values[3]);
				}

			}
		}
		return list;
	}

	/**
	 * 收集HP服务器的面板信息
	 * 
	 * @return
	 * @throws IOException
	 */
	private DBObject chassis() throws IOException {
		String[] result = OsUtil.execAsArray(new String[]{"/bin/bash", "-c",
				"hpasmcli -s 'show server'"});
		// BasicDBList list = new BasicDBList();
		DBObject obj = null;

		String infoIndex = "Index";
		String infoSystem = "System";
		String infoSerial = "SerialNo";
		String infoversion = "ROMversion";

		for (int i = 0; i < result.length; i++) {
			if (result[i].startsWith("System")) {
				obj = new BasicDBObject();
				// list.add(obj);
				obj.put(infoIndex, 0);
				obj.put("HostName", "");
				obj.put(infoSystem, getValue(result[i]));
			} else if (result[i].startsWith("Serial No.") && obj != null) {
				obj.put(infoSerial, getValue(result[i]));
			} else if (result[i].startsWith("ROM version") && obj != null) {
				obj.put(infoversion, getValue(result[i]));
				obj.put("ExpressServiceCode", "");
				obj.put("FlashchassisidentifyLEDstate", "");
				obj.put("FlashchassisidentifyLEDtimeoutvalue", "");
			}
		}
		return obj;
	}

	/**
	 * 收集HP服务器的硬盘信息
	 * 
	 * @return
	 * @throws IOException
	 */
	private DBObject storage() throws IOException {

		String[] slots = OsUtil.execAsArray(new String[]{"hpacucli", "ctrl",
				"all", "show", "status"});

		int soltNumber = 0;

		for (int i = 0; i < slots.length; i++) {
			if (slots[i].startsWith("Smart Array")) {
				soltNumber = Integer.parseInt(slots[i].replaceAll(".*Slot", "")
						.replaceAll("\\(Embedded\\)", "").trim());
			}

		}

		String[] result = OsUtil.execAsArray(new String[]{"/bin/bash", "-c",
				"hpacucli ctrl slot=" + soltNumber + " pd all show status"});
		BasicDBList list = new BasicDBList();
		DBObject obj = null;

		for (int i = 0; i < result.length; i++) {
			// System.out.println("Line:"+line);

			if (result[i].contains("physicaldrive")) {
				obj = new BasicDBObject();
				list.add(obj);
				obj.put(ServerField.FIELDS_OF_STORAGE[0],
						getStorageID(result[i]));
				obj.put(ServerField.FIELDS_OF_STORAGE[1],
						getStorageStatus(result[i]));
				obj.put(ServerField.FIELDS_OF_STORAGE[2], "");
				obj.put(ServerField.FIELDS_OF_STORAGE[3], "");
				obj.put(ServerField.FIELDS_OF_STORAGE[4], "");
				obj.put(ServerField.FIELDS_OF_STORAGE[5],
						getCapacity(result[i]));
				obj.put(ServerField.FIELDS_OF_STORAGE[6], "");
				obj.put(ServerField.FIELDS_OF_STORAGE[7], "");
				obj.put(ServerField.FIELDS_OF_STORAGE[8], "");
				obj.put(ServerField.FIELDS_OF_STORAGE[9], "");

			}
		}

		return list;
	}

	/**
	 * 收集HP服务器的电源信息
	 * 
	 * @return
	 * @throws IOException
	 */
	private DBObject pwrsupplies() throws IOException {
		String[] result = OsUtil.execAsArray(new String[]{"/bin/bash", "-c",
				"hpasmcli -s 'show powersupply'"});
		BasicDBList list = new BasicDBList();
		DBObject obj = null;
		for (int i = 0; i < result.length; i++) {
			// System.out.println("Line:"+line);

			if (result[i].startsWith("Power supply")) {
				obj = new BasicDBObject();
				list.add(obj);
				obj.put(ServerField.FIELDS_OF_POWER[0],
						getValuebySymbol(result[i]));
			} else if (result[i].contains("Present") && obj != null) {
				obj.put(ServerField.FIELDS_OF_POWER[1],
						this.getValue(result[i]));
				obj.put(ServerField.FIELDS_OF_POWER[2], "");
				obj.put(ServerField.FIELDS_OF_POWER[3], "");

				obj.put(ServerField.FIELDS_OF_POWER[4], "");
				obj.put(ServerField.FIELDS_OF_POWER[5], "");
				obj.put(ServerField.FIELDS_OF_POWER[6], "");
				obj.put(ServerField.FIELDS_OF_POWER[7], "");
				obj.put(ServerField.FIELDS_OF_POWER[8], "");

			}
		}

		return list;
	}

	// private String getName(String line) {
	// return line.replaceAll(":.*$", "").replaceAll(" +", "")
	// .replaceAll("\t", "");
	// }

	private String getValue(String line) {
		return line.replaceAll("^[^:]*:", "").replaceAll("^ +", "")
				.replaceAll(" +$", "");
	}

	@SuppressWarnings("unused")
	private int getValueByInt(String line) {
		return Integer.parseInt(line.replaceAll("^[^:]*:", "").replaceAll(" *",
				""));
	}

	// private String getNamebySymbol(String line) {
	// return line.replaceAll("#.*$", "").replaceAll(" +", "")
	// .replaceAll("\t", "");
	// }

	private String getValuebySymbol(String line) {
		return line.replaceAll("^[^#]*#", "").replaceAll("^ +", "")
				.replaceAll(" +$", "");
	}

	// private String getNameByParentheses(String line) {
	// return line.replaceAll("\\(.*$", "").replaceAll(" +", "");
	// }
	//
	// private String getValueByParenthesesString(String line) {
	// return line.replaceAll("^[^(]*", "").replaceAll("^ +", "")
	// .replaceAll(" +$", "");
	// }

	private String getStorageStatus(String line) {
		return line.replaceAll(".+:", "").replaceAll("^ +", "")
				.replaceAll(" +$", "");
	}

	private String getCapacity(String line) {
		return line.replaceAll("^[^,]*,", "").replaceAll("\\).*$", "")
				.replaceAll("\t", "").replaceAll("^[a-z]+", "")
				.replaceAll(" +", "");
	}

	private String getStorageID(String line) {
		return line.replaceAll("\\(.*$", "").replaceAll("\t", "")
				.replaceAll("^ +", "").replaceAll("^[a-z]+", "")
				.replaceAll(" +", "");

	}

}
