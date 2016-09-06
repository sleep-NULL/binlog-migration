package com.sleep.binlog.protocol;

import java.nio.ByteBuffer;

/**
 * 
 * <a href=
 * "http://dev.mysql.com/doc/internals/en/connection-phase-packets.html#packet-Protocol::Handshake">
 * 
 * @author yafeng.huang
 *
 */
public class HandShake extends Protocol {

	/**
	 * 协议版本
	 */
	private int version;

	/**
	 * mysql server 版本
	 */
	private String serverVersion;

	private int connectId;

	private String authPluginDataPart1;

	private int capabilityFlags;

	private int characterSet;

	private int statusFlags;

	private String reserved;

	private String authPluginDataPart2;

	private String authPluginName;

	public HandShake(ByteBuffer buf) {
		super(buf);
		this.version = readInt(1);
		this.serverVersion = readZeroEndString();
		this.connectId = readInt(4);
		this.authPluginDataPart1 = readFixedLengthString(8);
		ignore(1);
		this.capabilityFlags = readInt(2);
		if (buf.hasRemaining()) {
			this.characterSet = readInt(1);
			this.statusFlags = readInt(2);
			this.capabilityFlags |= readInt(2) << 16;
			int authPluginDataLength = 0;
			if ((capabilityFlags & CapabilityFlag.CLIENT_PLUGIN_AUTH) == CapabilityFlag.CLIENT_PLUGIN_AUTH) {
				authPluginDataLength = readInt(1);
			} else {
				ignore(1);
			}
			reserved = readFixedLengthString(10);
			if ((capabilityFlags
					& CapabilityFlag.CLIENT_SECURE_CONNECTION) == CapabilityFlag.CLIENT_SECURE_CONNECTION) {
				authPluginDataPart2 = readFixedLengthString(Math.max(13, authPluginDataLength - 8));
			}
			if ((capabilityFlags & CapabilityFlag.CLIENT_PLUGIN_AUTH) == CapabilityFlag.CLIENT_PLUGIN_AUTH) {
				authPluginName = readZeroEndString();
			}
		}
	}

	@Override
	public String toString() {
		return "HandShake [version=" + version + ", serverVersion=" + serverVersion + ", connectId=" + connectId
				+ ", authPluginDataPart1=" + authPluginDataPart1 + ", capabilityFlags=" + capabilityFlags
				+ ", characterSet=" + characterSet + ", statusFlags=" + statusFlags + ", reserved=" + reserved
				+ ", authPluginDataPart2=" + authPluginDataPart2 + ", authPluginName=" + authPluginName + "]";
	}

}
