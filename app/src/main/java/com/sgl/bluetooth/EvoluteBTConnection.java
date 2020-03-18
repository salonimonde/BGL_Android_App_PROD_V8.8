package com.sgl.bluetooth;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Build;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

@SuppressLint("NewApi")
public class EvoluteBTConnection {
	/**Service UUID*//*
	public final static String UUID_STR = "00001101-0000-1000-8000-00805F9B34FB";
	*//**Bluetooth address code*//*
	private String msMAC;
	*//**Bluetooth connection status*//*
	private static boolean mbConectOk = false;

	*//* Get Default Adapter *//*
	private BluetoothAdapter mBT = BluetoothAdapter.getDefaultAdapter();
	*//**Bluetooth serial port connection object*//*
	public static BluetoothSocket mbsSocket = null;
	*//** Input stream object *//*
	public static InputStream misIn = null;
	*//** Output stream object *//*
	public static OutputStream mosOut = null;
	*//**Constant: The current Adnroid SDK version number*//*
	private static final int SDK_VER;
	static{
		SDK_VER = Build.VERSION.SDK_INT;
	};

	*//**
	 * Constructor 
	 * @param sMAC Bluetooth device MAC address required to connect
	 * *//*
	public EvoluteBTConnection(String sMAC){
		this.msMAC = sMAC;
	}
	
	*//**
	 * Disconnect the Bluetooth device connection
	 * @return void
	 * *//*
	public static void closeConn(){
		if (mbConectOk ){
			try{
				if (null != misIn)
					misIn.close();
				if (null != mosOut)
					mosOut.close();
				if (null != mbsSocket)
					mbsSocket.close();
				mbConectOk = false;//Mark the connection has been closed
			}catch (IOException e){
				//Any part of the error, will be forced to close socket connection
				misIn = null;
				mosOut = null;
				mbsSocket = null;
				mbConectOk = false;//Mark the connection has been closed
			}
		}
	}
	
	final public boolean createConn(){
		if (! mBT.isEnabled())
			return false;
		//If a connection already exists, disconnect
		if (mbConectOk)
			this.closeConn();
		*//*Start Connecting a Bluetooth device*//*
    	final BluetoothDevice device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(this.msMAC);
    	final UUID uuidComm = UUID.fromString(UUID_STR);
		try{
			//this.mbsSocket = device.createInsecureRfcommSocketToServiceRecord(uuidComm);
			this.mbsSocket = device.createRfcommSocketToServiceRecord(uuidComm);
			Thread.sleep(2000);
			this.mbsSocket.connect();
			Thread.sleep(2000);
			this.mosOut = this.mbsSocket.getOutputStream();//Get global output stream object
			this.misIn = this.mbsSocket.getInputStream(); //Get global streaming input object
			this.mbConectOk = true; //Device is connected successfully
		}catch (Exception e){
			try {
				Thread.sleep(2000);
				//this.mbsSocket = device.createRfcommSocketToServiceRecord(uuidComm);
				this.mbsSocket = device.createInsecureRfcommSocketToServiceRecord(uuidComm);
				Thread.sleep(2000);
				this.mbsSocket.connect();
				Thread.sleep(2000);
				this.mosOut = this.mbsSocket.getOutputStream();//Get global output stream object
				this.misIn = this.mbsSocket.getInputStream(); //Get global streaming input object
				this.mbConectOk = true;
			} catch (IOException e1) {
				e1.printStackTrace();
				this.closeConn();//Disconnect
				return false;
			} catch (Exception ee){
				ee.printStackTrace();
				this.closeConn();//Disconnect
				return false;
			
			}
			return true;
		}
		return true;
	}
	
	*//**
	 * If the communication device has been established 
	 * @return Boolean true: communication has been established / false: communication lost
	 * *//*
	public boolean isConnect()	{
		return this.mbConectOk;
	}
*/
	
}
