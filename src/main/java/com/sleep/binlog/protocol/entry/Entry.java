package com.sleep.binlog.protocol.entry;

import java.util.List;

public class Entry {

	private String schema;

	private String table;

	private long timestamp;

	private String binlog;

	private long offset;

	private char type;

	private List<Column> columns;

	public Entry() {
		super();
	}

	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getBinlog() {
		return binlog;
	}

	public void setBinlog(String binlog) {
		this.binlog = binlog;
	}

	public long getOffset() {
		return offset;
	}

	public void setOffset(long offset) {
		this.offset = offset;
	}

	public char getType() {
		return type;
	}

	public void setType(char type) {
		this.type = type;
	}

	public List<Column> getColumns() {
		return columns;
	}

	public void setColumns(List<Column> columns) {
		this.columns = columns;
	}

	@Override
	public String toString() {
		return "Entry [schema=" + schema + ", table=" + table + ", timestamp=" + timestamp + ", binlog=" + binlog
				+ ", offset=" + offset + ", type=" + type + ", columns=" + columns + "]";
	}

}
