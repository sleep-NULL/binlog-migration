package com.sleep.binlog.protocol;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public abstract class Protocol {
	
	private ByteBuffer buf;
	
	public Protocol(ByteBuffer buf) {
		this.buf = buf;
	}
	
	/**
	 * java byte 去符号位转 int
	 * 
	 * @param value
	 * @return
	 */
	private int toInt(byte value) {
		return value & 0xff;
	}
	
	/**
	 * java byte 去符号位转 long
	 * 
	 * @param value
	 * @return
	 */
	private long toLong(byte value) {
		return (long)toInt(value);
	}
	
	/**
	 * 读取一个字节
	 * 
	 * @return
	 */
	public int readByte() {
		return buf.get();
	}
	
	/**
	 * 以小头序读取若干字节的 int
	 * 
	 * @param length
	 * @return
	 */
	public int readInt(int length) {
		int result = 0;
		for (int i = 0; i < length; i++) {
			result |= (toInt(buf.get()) << (i << 3));
		}
		return result;
	}
	
	/**
	 * 读取以 0 为结尾字节的字符串
	 * 
	 * @return
	 */
	public String readZeroEndString() {
		List<Byte> bytes = new ArrayList<Byte>();
		byte temp = 0;
		while ((temp = buf.get()) != 0) {
			bytes.add(temp);
		}
		byte[] result = new byte[bytes.size()];
		for (int i = 0; i < bytes.size(); i++) {
			result[i] = bytes.get(i);
		}
		return new String(result);
	}
	
	/**
	 * 读取固定长度的字符串
	 * 
	 * @param length
	 * @return
	 */
	public String readFixedLengthString(int length) {
		byte[] arr = new byte[length];
		for (int i = 0; i < length; i++) {
			arr[i] = buf.get();
		}
		return new String(arr);
	}
	
	/**
	 * 忽略若干字节
	 * 
	 * @param length
	 */
	public void ignore(int length) {
		for (int i = 0; i < length; i++) {
			buf.get();
		}
	}

}
