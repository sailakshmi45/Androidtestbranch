/**
 * Scanner Settings Device Information
 * (c) 2011 Socket Mobile, inc.
 */
package com.globalnest.scanattendee.socketmobile;

import com.socketmobile.scanapi.ISktScanDevice;
import com.socketmobile.scanapi.ISktScanSymbology;
import com.socketmobile.scanapi.SktScanDeviceType;

public class DeviceInfo {
	private ISktScanDevice _device;
	private String _name;
	private String _btaddress;
	private long _type;
	private String _version;
	private String _batteryLevel;
	private int _ndecodeval;
	private boolean _rumable;
	private String _suffix;
	private SymbologyInfo _symbologyInfo[];
	private int _symbologyindex;
	private Notification _notification=null;
	
	interface Notification
	{
		void OnNotify(DeviceInfo deviceInfo);
	}

	class SymbologyInfo{
		private String _name;
		private int _status;
		
		public String getName(){
			return _name;
		} 
		public void setName(String name){
			_name=name;
		}
		public int getStatus(){
			return _status;
		}
		public void setStatus(int status){
			_status=status;
		}
	}
	
	public DeviceInfo(String name,ISktScanDevice device,long type)
	{
		this._device=device;
		this._name=name;
		this._btaddress="Not available.";
		this._type=type;
		this._version="Unknown";
		this._batteryLevel="Unknown";
		this._ndecodeval=0;
		this._rumable=true;
		this._suffix="\n";
		this._symbologyInfo=new SymbologyInfo[ISktScanSymbology.id.kSktScanSymbologyLastSymbolID-1];
		for(int i=0;i<this._symbologyInfo.length;i++)
		{
			this._symbologyInfo[i]=new SymbologyInfo();
		}
	}
	
	public void setNotification(Notification notification)
	{
		this._notification=notification;
	}
	
	public String getName()
	{
		return _name;
	}
	
	public String getBTAddress()
	{
		return _btaddress;
	}
	
	public ISktScanDevice getSktScanDevice()
	{
		return _device;
	}
	
	public long getType()
	{
		return _type;
	}

	public String getTypeString() {
		String type;
		if(_type==SktScanDeviceType.kSktScanDeviceTypeScanner7)
			type="CHS Scanner";
		else if(_type==SktScanDeviceType.kSktScanDeviceTypeScanner7x)
			type="CHS 7X Scanner";
		else if(_type==SktScanDeviceType.kSktScanDeviceTypeScanner7xi)
			type="CHS 7Xi Scanner";
		else if(_type==SktScanDeviceType.kSktScanDeviceTypeScanner9)
			type="CRS Scanner";
		else if(_type==SktScanDeviceType.kSktScanDeviceTypeSoftScan)
			type="Soft Scanner";
		else if(_type==SktScanDeviceType.kSktScanDeviceTypeScanner8ci)
			type="CHS 8Ci Scanner";
		else
			type="Unknown scanner type!";
		return type;
	}

	public String getVersion() {
		return _version;
	}

	public String getBatteryLevel() {
		return _batteryLevel;
	}

	public int getDecodeVal() {
		return _ndecodeval;
	}

	public boolean getRumble() {
		return _rumable;
	}

	public String getSuffix() {
		return _suffix;
	}
	
	public int getSymbologyIndex() {
		return _symbologyindex;
	}
	
	public int getSymbologyStatus(int index) {
		return _symbologyInfo[index].getStatus();
	}

	public String getSymbologyName(int index) {
		return _symbologyInfo[index].getName();
	}
	
	public void setName(String name){
		this._name=name;
	}
	
	public void setBtAddress(String btaddress){
		this._btaddress=btaddress;
		if(this._notification!=null)
			this._notification.OnNotify(this);
	}
	
	public void setVersion(String version){
		this._version=version;
		if(this._notification!=null)
			this._notification.OnNotify(this);
			
	}
	
	public void setBatteryLevel(String battery){
		this._batteryLevel=battery;
		if(this._notification!=null)
			this._notification.OnNotify(this);
	}
	
	public void setDecodeVal(int decodeval){
		this._ndecodeval=decodeval;
//		if(this._notification!=null)
//			this._notification.OnNotify(this);
	}
	
	public void setRumble(boolean rumble){
		this._rumable=rumble;
//		if(this._notification!=null)
//			this._notification.OnNotify(this);
	}
	
	public void setSuffix(String suffix){
		this._suffix=suffix;
		if(this._notification!=null)
			this._notification.OnNotify(this);
	}
	
	public void setSymbologyStatus(int index,int symbologyStatus){
		this._symbologyInfo[index].setStatus(symbologyStatus);
//		if(this._notification!=null)
//			this._notification.OnNotify(this);
	}
	public void setSymbologyName(int index,String symbologyName){
		this._symbologyInfo[index].setName(symbologyName);
		if(this._notification!=null)
			this._notification.OnNotify(this);
	}
	
	public void setSymbologyIndex(int index){
		this._symbologyindex=index;
	}

	@Override
	public String toString() {
		return _name;
	}
}