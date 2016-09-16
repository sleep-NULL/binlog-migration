package com.sleep.binlog.protocol;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

/**
 * @author yafeng.huang
 * 
 *         <a href=
 *         "http://dev.mysql.com/doc/internals/en/packet-ERR_Packet.html">
 *
 */
public class ErrPacket extends Protocol {

	private int errorCode;

	private String sqlStateMarker;

	private String sqlState;

	private String errorMessage;

	public ErrPacket(ByteBuffer buf) throws UnsupportedEncodingException {
		super(buf);
		this.errorCode = readInt(2);
		this.sqlStateMarker = readFixedLengthString(1);
		this.sqlState = readFixedLengthString(5);
		this.errorMessage = readEOFString();
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public String getSqlStateMarker() {
		return sqlStateMarker;
	}

	public void setSqlStateMarker(String sqlStateMarker) {
		this.sqlStateMarker = sqlStateMarker;
	}

	public String getSqlState() {
		return sqlState;
	}

	public void setSqlState(String sqlState) {
		this.sqlState = sqlState;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	@Override
	public String toString() {
		return "ErrPacket [errorCode=" + errorCode + ", sqlStateMarker=" + sqlStateMarker + ", sqlState=" + sqlState
				+ ", errorMessage=" + errorMessage + "]";
	}

}
