package com.cninfo.meyes.client.plugin.impl.server;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hyperic.sigar.CpuInfo;
import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.FileSystem;
import org.hyperic.sigar.FileSystemUsage;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.NetInfo;
import org.hyperic.sigar.NetInterfaceConfig;
import org.hyperic.sigar.NetInterfaceStat;
import org.hyperic.sigar.NfsFileSystem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.SigarProxy;
import org.hyperic.sigar.cmd.Shell;

import com.cninfo.meyes.client.util.ClientUtil;
import com.cninfo.meyes.client.model.EventData;
import com.cninfo.meyes.client.plugin.impl.AbstractGatherPlugin;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

/**
 * @author lunianping
 *
 *用于采集Linux服务器的Volumn信息
 *
 *适用于RHEL及其它一般Linux操作系统
 *
 */
/**
 * @author lunianping
 * 
 */
public class ServerCommon extends AbstractGatherPlugin {

	protected Sigar sigar;
	protected SigarProxy proxy;

	public ServerCommon() {
		Shell shell = new Shell();
		this.sigar = shell.getSigar();
		this.proxy = shell.getSigarProxy();
	}

	public void process(EventData event) throws Exception {
		DBObject cpu = new BasicDBObject();
		event.getDoc().put("cpu", cpu);
		cpu.put("perc", getCpuPercTotal());
		cpu.put("info", getCpuInfo());

		DBObject mem = this.memory();
		event.getDoc().put("memory", mem);

		BasicDBList volumn = volumn();
		event.getDoc().put("volumn", volumn);
		
		BasicDBList io = io();
		event.getDoc().put("io", io);
		
		DBObject sys = sysInfo();
		event.getDoc().put("sys", sys);
		
		DBObject net = this.getNetInfo();
		event.getDoc().put("net", net);
	}
	
	private DBObject sysInfo(){
		DBObject obj = new BasicDBObject();
		ClientUtil cu = new ClientUtil();
		obj.put("CurrentUser", cu.getCurrentUser());
		obj.put("Hostname", cu.getHostName());
		obj.put("OS", cu.getOsDetail());
		obj.put("Mac", cu.getMac());
		obj.put("Ip", cu.getIpAddress());
		return obj;
	}

	/**iostat
	 * @return
	 * @throws SigarException
	 */
	private BasicDBList io() throws SigarException {
		BasicDBList list = new BasicDBList();
		FileSystem[] fslist = this.proxy.getFileSystemList();
		Map<String,DBObject> map = new HashMap<String,DBObject>();
		for (int i = 0; i < fslist.length; i++) {
			FileSystem fs = fslist[i];
			if (fs.getType() == FileSystem.TYPE_LOCAL_DISK) {
				DBObject obj = new BasicDBObject();
				FileSystemUsage usage = this.sigar.getFileSystemUsage(fs.getDirName());
				map.put(fs.getDirName(), obj);
				obj.put("MountedOn", fs.getDirName().replaceAll("\\\\", "/"));
				obj.put("Filesystem", fs.getDevName().replaceAll("\\\\", "/"));
				
				obj.put("Reads", usage.getDiskReads());
				obj.put("Writes", usage.getDiskWrites());

				obj.put("r_bytes", usage.getDiskReadBytes());
				obj.put("w_bytes", usage.getDiskWriteBytes());

				list.add(obj);
			}
		}
		/*
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(String mountOn : map.keySet()){
			DBObject obj = map.get(mountOn);
			FileSystemUsage usage = this.sigar.getFileSystemUsage(mountOn);
			obj.put("r_bytes_1", usage.getDiskReadBytes());
			obj.put("w_bytes_1", usage.getDiskWriteBytes());
		}
		*/
		return list;
	}

	/**
	 * 获取卷
	 * 
	 * @return
	 * @throws SigarException
	 */
	private BasicDBList volumn() throws SigarException {
		BasicDBList list = new BasicDBList();
		FileSystem[] fslist = this.proxy.getFileSystemList();
		for (int i = 0; i < fslist.length; i++) {
			DBObject obj = new BasicDBObject();
			FileSystem fs = fslist[i];

			if (!(fs.getType() == FileSystem.TYPE_LOCAL_DISK || fs.getType() == FileSystem.TYPE_NETWORK)) {
				continue;
			}

			if (fs instanceof NfsFileSystem) {
				NfsFileSystem nfs = (NfsFileSystem) fs;
				if (!nfs.ping()) {
					// logger.(nfs.getUnreachableMessage());
					continue;
				}
			}
			FileSystemUsage usage = this.sigar.getFileSystemUsage(fs
					.getDirName());
			long used = usage.getTotal() - usage.getFree();
			long avail = usage.getAvail();
			long total = usage.getTotal();
			//long pct = (long) (usage.getUsePercent() * 100);
			obj.put("MountedOn", fs.getDirName().replaceAll("\\\\", "/"));
			obj.put("Filesystem", fs.getDevName().replaceAll("\\\\", "/"));
			obj.put("Total", total);
			obj.put("Used", used);
			obj.put("Available", avail);
			obj.put("UsedPerc", new BigDecimal(usage.getUsePercent()*100).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
			obj.put("Type", fs.getSysTypeName() + "/" + fs.getTypeName());

			list.add(obj);
		}
		return list;
	}

	/**
	 * 内存
	 * 
	 * @return
	 * @throws SigarException
	 */
	private DBObject memory() throws SigarException {
		DBObject obj = new BasicDBObject();
		Mem mem = sigar.getMem();
		obj.put("Total", mem.getTotal()/1024);
		obj.put("Free", mem.getFree()/1024);
		obj.put("Used", mem.getUsed()/1024);
		obj.put("FreePercent", new BigDecimal(mem.getFreePercent()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
		obj.put("UsedPercent", new BigDecimal(mem.getUsedPercent()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
		obj.put("ActualFree", mem.getActualFree()/1024);
		obj.put("ActualUsed", mem.getActualUsed()/1024);
		return obj;
	}

	private DBObject getCpuInfo() throws SigarException {
		// BasicDBList list = new BasicDBList();
		CpuInfo[] infos = this.sigar.getCpuInfoList();

		CpuInfo info = infos[0];
		DBObject obj = new BasicDBObject();
		long cacheSize = info.getCacheSize();
		obj.put("Vendor", info.getVendor());
		obj.put("Model", info.getModel());
		obj.put("Mhz", info.getMhz());
		obj.put("TotalCPUs", info.getTotalCores());
		obj.put("PhysicalCPUs", info.getTotalSockets());
		obj.put("CoresPerCPU", info.getCoresPerSocket());
		obj.put("CacheSize", cacheSize);
		return obj;
	}
	
	private DBObject getNetInfo() throws SigarException {
		// BasicDBList list = new BasicDBList();
		DBObject obj = new BasicDBObject();
		BasicDBList net = new BasicDBList();
		obj.put("interface", net);
		NetInfo info = sigar.getNetInfo();
		String gateway = info.getDefaultGateway();
		String dns = info.getPrimaryDns();
		String domain = info.getDomainName();
		
		obj.put("gateway", gateway);
		obj.put("dns", dns);
		obj.put("domain", domain);
		
		ClientUtil cu = new ClientUtil();
		List<String> list = cu.getValidNetName();
		
		//Map<String,DBObject> map = new HashMap<String,DBObject>();
		for(String name : list){
			DBObject eth = new BasicDBObject();
			net.add(eth);
			//map.put(name, eth);
			NetInterfaceStat stat = sigar.getNetInterfaceStat(name);
			long speed = stat.getSpeed();
			long rx = stat.getRxBytes()/1024;
			long tx = stat.getTxBytes()/1024;
			NetInterfaceConfig config = sigar.getNetInterfaceConfig(name);
			String ipAddress = config.getAddress();
			String mac = config.getHwaddr();
			String netmask = config.getNetmask();
			String type = config.getType();
			eth.put("name", name);
			eth.put("ip", ipAddress);
			eth.put("mac", mac);
			eth.put("netmask", netmask);
			eth.put("type", type);
			eth.put("speed", speed);
			eth.put("mtu", config.getMtu());
			eth.put("rx_kb",rx);
			eth.put("tx_kb",tx);
		}
		/*
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		for(String name : map.keySet()){
			DBObject eth = map.get(name);
			NetInterfaceStat stat = sigar.getNetInterfaceStat(name);
			long rx = stat.getRxBytes()/1024;
			long tx = stat.getTxBytes()/1024;
			eth.put("rx_kb_1",rx);
			eth.put("tx_kb_1",tx);
		}
		*/
		return obj;
	}

	/**
	 * @return
	 * @throws SigarException
	 */
	private DBObject getCpuPercTotal() throws SigarException {
		return getCpuPerc(this.sigar.getCpuPerc());
	}

	/**
	 * @return
	 * @throws SigarException
	 * 
	 *             private BasicDBList getCpuPerc() throws SigarException{
	 *             BasicDBList list = new BasicDBList(); CpuPerc[] cpus =
	 *             this.sigar.getCpuPercList(); for (int i=0; i<cpus.length;
	 *             i++) { System.out.println("CPU " + i + ".........");
	 *             list.add(getCpuPerc(cpus[i])); } return list; }
	 */

	/**
	 * @param cpu
	 * @return
	 */
	private DBObject getCpuPerc(CpuPerc cpu) {
		DBObject obj = new BasicDBObject();
		
		obj.put("user", new BigDecimal(cpu.getUser()*100).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
		obj.put("sys", new BigDecimal(cpu.getSys()*100).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
		obj.put("idle", new BigDecimal(cpu.getIdle()*100).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
		obj.put("wait", new BigDecimal(cpu.getWait()*100).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
		obj.put("nice", new BigDecimal(cpu.getNice()*100).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
		obj.put("combined", new BigDecimal(cpu.getCombined()*100).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
		obj.put("irq", new BigDecimal(cpu.getIrq()*100).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
		return obj;
	}

	public static void main(String[] args) {
		ServerCommon lv = new ServerCommon();
		EventData event = new EventData();

		try {
			lv.process(event);
			System.out.println(event.getDoc());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
