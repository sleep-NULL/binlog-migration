package com.sleep.binlog.util;

public class BitUtil {
	
	public static byte[] xor(byte[] a, byte[] b) {
		if (a.length != b.length) {
			throw new IllegalArgumentException("Input byte arrs length must be equal.");
		}
		byte[] result = new byte[a.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = (byte) (a[i] ^ b[i]);
		}
		return result;
	}
	
	/**
	 * 拼接两个字节数组
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static byte[] concat(byte[] a, byte[] b) {
		byte[] result = new byte[a.length + b.length];
		System.arraycopy(a, 0, result, 0, a.length);
		System.arraycopy(b, 0, result, a.length, b.length);
		return result;
	}

}
