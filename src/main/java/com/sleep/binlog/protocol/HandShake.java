package com.sleep.binlog.protocol;

import java.nio.ByteBuffer;

public class HandShake extends Protocol {
	
	/**
	 * 协议版本
	 */
	private int version;
	
	/**
	 * mysql server 版本
	 */
	private String serverVersion;
	
	public HandShake(ByteBuffer buf) {
		super(buf);
		this.version = readInt(1);
		System.out.println(version);
		this.serverVersion = readZeroEndString();
		System.out.println(serverVersion);
	}
	
	
	
	

}
