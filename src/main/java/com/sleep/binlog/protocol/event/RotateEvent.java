package com.sleep.binlog.protocol.event;

import java.nio.ByteBuffer;

import com.sleep.binlog.protocol.Protocol;

/**
 * @author huangyafeng
 *
 *         <a href="http://dev.mysql.com/doc/internals/en/rotate-event.html">
 *
 */
public class RotateEvent extends Protocol {

	private long position;

	private String nextBinlog;
	
	public RotateEvent(ByteBuffer buf) {
		super(buf);
		this.position = readLong(8);
		this.nextBinlog = readEOFString();
	}

	public long getPosition() {
		return position;
	}

	public void setPosition(long position) {
		this.position = position;
	}

	public String getNextBinlog() {
		return nextBinlog;
	}

	public void setNextBinlog(String nextBinlog) {
		this.nextBinlog = nextBinlog;
	}

	@Override
	public String toString() {
		return "RotateEvent [position=" + position + ", nextBinlog=" + nextBinlog + "]";
	}

}
