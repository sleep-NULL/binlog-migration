package com.sleep.binlog.protocol;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public abstract class Protocol {
	
	private ByteBuffer buf;
	
	public Protocol(ByteBuffer buf) {
		this.buf = buf;
	}
	
	private int toInt(byte value) {
		return value & 0xff;
	}
	
	private long toLong(byte value) {
		return (long)toInt(value);
	}
	
	public int readByte() {
		return buf.get();
	}
	
	public int readInt(int length) {
		int result = 0;
		for (int i = 0; i < length; i++) {
			result |= (toInt(buf.get()) << (i << 3));
		}
		return result;
	}
	
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

}
