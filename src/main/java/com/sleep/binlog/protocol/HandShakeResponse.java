package com.sleep.binlog.protocol;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import com.sleep.binlog.util.BitUtil;
import com.sleep.binlog.util.DigestUtil;

public class HandShakeResponse extends Response {

	public HandShakeResponse(int charset, String username, String password, String randomData)
			throws UnsupportedEncodingException, IOException {
		super();
		writeInt(CapabilityFlag.CLIENT_PROTOCOL_41 | CapabilityFlag.CLIENT_LONG_FLAG
				| CapabilityFlag.CLIENT_SECURE_CONNECTION, 4);
		writeInt(0, 4);
		writeInt(charset, 1);
		fill(0, 23);
		writeZeroEndString(username);
		byte[] pwd = calculatedPassword(password, randomData);
		writeInt(pwd.length, 1);
		write(pwd);
	}

	/**
	 * <a href=
	 * "http://dev.mysql.com/doc/internals/en/secure-password-authentication.html">
	 * 
	 * @param password
	 * @param randomData
	 * @return
	 */
	private byte[] calculatedPassword(String password, String randomData) {
		byte[] passwordBytes = password.getBytes();
		return BitUtil.xor(DigestUtil.sha1(passwordBytes), DigestUtil
				.sha1(BitUtil.concat(randomData.getBytes(), DigestUtil.sha1(DigestUtil.sha1(passwordBytes)))));
	}
	
	public static void main(String[] args) throws UnsupportedEncodingException, IOException {
		HandShakeResponse res = new HandShakeResponse(33, "root", "", "");
		byte[] byteArr = res.calculatedPassword("test", "password");
		for (int i = 0; i < byteArr.length; i++) {
			System.out.print(byteArr[i]);
		}
	}

}
