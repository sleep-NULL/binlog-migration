package com.sleep.binlog.protocol.event;

import java.nio.ByteBuffer;

import com.sleep.binlog.protocol.Protocol;

public class QueryEvent extends Protocol {

	private long slaveProxyId;

	private long executionTime;

	private int errorCode;

	private String statusVars;

	private String schema;

	private String query;

	public QueryEvent(ByteBuffer buf) {
		super(buf);
		this.slaveProxyId = readLong(4);
		this.executionTime = readLong(4);
		ignore(1);
		this.errorCode = readInt(2);
		this.statusVars = readFixedLengthString(readInt(2));
		this.schema = readZeroEndString();
		this.query = readEOFString();
	}

	public long getSlaveProxyId() {
		return slaveProxyId;
	}

	public void setSlaveProxyId(long slaveProxyId) {
		this.slaveProxyId = slaveProxyId;
	}

	public long getExecutionTime() {
		return executionTime;
	}

	public void setExecutionTime(long executionTime) {
		this.executionTime = executionTime;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public String getStatusVars() {
		return statusVars;
	}

	public void setStatusVars(String statusVars) {
		this.statusVars = statusVars;
	}

	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	@Override
	public String toString() {
		return "QueryEvent [slaveProxyId=" + slaveProxyId + ", executionTime=" + executionTime + ", errorCode="
				+ errorCode + ", statusVars=" + statusVars + ", schema=" + schema + ", query=" + query + "]";
	}

}
