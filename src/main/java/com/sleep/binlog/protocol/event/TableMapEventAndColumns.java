package com.sleep.binlog.protocol.event;

import java.util.List;

public class TableMapEventAndColumns {

	private TableMapEvent tableMapEvent;

	private List<String> columns;

	public TableMapEventAndColumns(TableMapEvent tableMapEvent, List<String> columns) {
		super();
		this.tableMapEvent = tableMapEvent;
		this.columns = columns;
	}

	public TableMapEvent getTableMapEvent() {
		return tableMapEvent;
	}

	public void setTableMapEvent(TableMapEvent tableMapEvent) {
		this.tableMapEvent = tableMapEvent;
	}

	public List<String> getColumns() {
		return columns;
	}

	public void setColumns(List<String> columns) {
		this.columns = columns;
	}

	@Override
	public String toString() {
		return "TableMapEventAndColumns [tableMapEvent=" + tableMapEvent + ", columns=" + columns + "]";
	}

}
