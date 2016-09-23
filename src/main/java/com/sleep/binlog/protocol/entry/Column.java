package com.sleep.binlog.protocol.entry;

public class Column {

	private String name;

	private String value;

	private String beforeValue;

	public Column() {
		super();
	}

	public Column(String name, String value, String beforeValue) {
		super();
		this.name = name;
		this.value = value;
		this.beforeValue = beforeValue;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getBeforeValue() {
		return beforeValue;
	}

	public void setBeforeValue(String beforeValue) {
		this.beforeValue = beforeValue;
	}

	@Override
	public String toString() {
		return "Column [name=" + name + ", value=" + value + ", beforeValue=" + beforeValue + "]";
	}

}
