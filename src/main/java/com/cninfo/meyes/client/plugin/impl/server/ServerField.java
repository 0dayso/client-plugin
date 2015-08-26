package com.cninfo.meyes.client.plugin.impl.server;

public class ServerField {
	
	public static final String[] FIELDS_OF_STORAGE= new String[]{
		"ID",
		"Status",
		"Name",
		"State",
		"BusProtocol",
		"Capacity",
		"ProductID",
		"SerialNo",
		"PartNumber",
		"SASAddress"
		};
	
	public static final String[] FIELDS_OF_DIMM = new String[]{
		"ID",
		"Status",
		"ConnectorName",
		"Type",
		"Size"
		};
	
	public static final String[] FIELDS_OF_POWER = new String[]{
		"ID",
		"Status",
		"Location",
		"Type",
		"RatedInputWattage",
		"MaximumOutputWattage",
		"FirmwareVersion",
		"OnlineStatus",
		"PowerMonitoringCapable"
		};
	
	public static final String[] FIELDS_OF_STORAGE_BATTERY = new String[]{
		"ID",
		"Status",
		"Name",
		"State"
		};
	
	public static final String[] FIELDS_OF_STORAGE_CONTROLLER = new String[]{
		"ID",
		"Status",
		"Name",
		"SlotID",
		"State",
		"FirmwareVersion",
		"Rebuild Rate",
		"BGIRate",
		"CheckConsistencyRate",
		"ReconstructRate", 
		"NumberOfConnectors",
		"CacheMemorySize"
		};
	
	public static final String[] FIELDS_OF_FANS = new String[]{
		"ID",
		"Status",
		"Name",
		"Reading",
		"MinimumWarningThreshold",
		"MaximumWarningThreshold", 
		"MinimumFailureThreshold",
		"MaximumFailureThreshold"
		};

}
