package com.sleep.binlog.protocol;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public abstract class Response {
	
	private ByteArrayOutputStream out;
	
	public Response() {
		out = new ByteArrayOutputStream();
	}
	
	/**
	 * 小头序写 int
	 * 
	 * @param value
	 * @param length
	 */
	public void writeInt(int value, int length) {
		for (int i = 0; i < length; i++) {
			out.write((value >>> (i << 3)) & 0x000000ff);
		}
	}
	
	public void writeLong(long value, int length) {
		for (int i = 0; i < length; i++) {
			out.write((int)(value >>> (i << 3)));
		}
	}
	
	public void writeFixedLengthString(String str) throws IOException {
		out.write(str.getBytes());
	}
	
	public void writeZeroEndString(String str) throws IOException {
		out.write(str.getBytes());
		out.write(0);
	}
	
	public void fill(int value, int times) {
		for (int i = 0; i < times; i++) {
			out.write(value);
		}
	}
	
	public void write(byte[] buf) throws IOException {
		out.write(buf);
	}
	
	public byte[] toByteArray() {
		return out.toByteArray();
	}

}
