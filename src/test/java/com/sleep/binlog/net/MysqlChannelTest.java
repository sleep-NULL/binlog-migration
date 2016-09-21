package com.sleep.binlog.net;

import java.io.ByteArrayInputStream;

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
	}

	private int toInt(byte value) {
		return value & 0xff;
	}

	private long toLong(byte value) {
		return (long) toInt(value);
	}

	public void byteOutputStreamTest() {
		ByteArrayInputStream in = new ByteArrayInputStream(null);
		in.read();
	}

	@Test
	public void fe() {
		System.out.println("char = " + (char) 0xfe);
	}

}
