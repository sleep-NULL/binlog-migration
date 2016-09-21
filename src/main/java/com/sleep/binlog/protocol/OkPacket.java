package com.sleep.binlog.protocol;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @author yafeng.huang
 * 
 *         <a href=
 *         "http://dev.mysql.com/doc/internals/en/packet-OK_Packet.html">
 *
 */
public class OkPacket extends Protocol {

	private long affectedRows;

	private long lastInsertId;

	private int statusFlags;

	private int warnings;

	private String info;

	public OkPacket(ByteBuffer buf) throws IOException {
		super(buf);
		this.affectedRows = readLengthEncodedInt();
		this.lastInsertId = readLengthEncodedInt();
		this.statusFlags = readInt(2);
		if (buf.hasRemaining()) {
			this.warnings = readInt(2);
			this.info = readEOFString();
		}
	}

	public long getAffectedRows() {
		return affectedRows;
	}

	public void setAffectedRows(long affectedRows) {
		this.affectedRows = affectedRows;
	}

	public long getLastInsertId() {
		return lastInsertId;
	}

	public void setLastInsertId(long lastInsertId) {
		this.lastInsertId = lastInsertId;
	}

	public int getStatusFlags() {
		return statusFlags;
	}

	public void setStatusFlags(int statusFlags) {
		this.statusFlags = statusFlags;
	}

	public int getWarnings() {
		return warnings;
	}

	public void setWarnings(int warnings) {
		this.warnings = warnings;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	@Override
	public String toString() {
		return "OkPacket [affectedRows=" + affectedRows + ", lastInsertId=" + lastInsertId + ", statusFlags="
				+ statusFlags + ", warnings=" + warnings + ", info=" + info + "]";
	}

}
