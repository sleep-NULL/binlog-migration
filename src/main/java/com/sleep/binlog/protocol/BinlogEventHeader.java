package com.sleep.binlog.protocol;

import java.nio.ByteBuffer;

/**
 * @author huangyafeng
 * 
 *         <a href=
 *         "http://dev.mysql.com/doc/internals/en/binlog-event-header.html">
 *
 */
public class BinlogEventHeader extends Protocol {

	private long timestamp;

	private int eventType;

	private long serverId;

	private long eventSize;

	private long logPos;

	private int flags;

	public BinlogEventHeader(ByteBuffer buf) {
		super(buf);
		this.timestamp = readLong(4);
		this.eventType = readInt(1);
		this.serverId = readLong(4);
		this.eventSize = readLong(4);
		this.logPos = readLong(4);
		this.flags = readInt(2);
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public int getEventType() {
		return eventType;
	}

	public void setEventType(int eventType) {
		this.eventType = eventType;
	}

	public long getServerId() {
		return serverId;
	}

	public void setServerId(long serverId) {
		this.serverId = serverId;
	}

	public long getEventSize() {
		return eventSize;
	}

	public void setEventSize(long eventSize) {
		this.eventSize = eventSize;
	}

	public long getLogPos() {
		return logPos;
	}

	public void setLogPos(long logPos) {
		this.logPos = logPos;
	}

	public int getFlags() {
		return flags;
	}

	public void setFlags(int flags) {
		this.flags = flags;
	}

	@Override
	public String toString() {
		return "BinlogEventHeader [timestamp=" + timestamp + ", eventType=" + eventType + ", serverId=" + serverId
				+ ", eventSize=" + eventSize + ", logPos=" + logPos + ", flags=" + flags + "]";
	}
	
}

