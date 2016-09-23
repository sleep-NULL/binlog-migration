package com.sleep.binlog.net;

import org.junit.Test;

public class MysqlChannelTest {

	@Test
	public void test() {
		byte a = -2;
		System.out.println(toInt(a));
		System.out.println(toLong(a));
		int b = 255;
		System.out.println(toInt(a) << 3);
		System.out.println(b << 3);
		System.out.println(toLong(a));
		System.out.println(0xfff);
		
		System.out.println(0xff);
		System.out.println(0xfff & 0xff);
	}

	private int toInt(byte value) {
		return value & 0xff;
	}

	private long toLong(byte value) {
		return (long) toInt(value);
	}

}
