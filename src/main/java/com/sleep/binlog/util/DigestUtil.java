package com.sleep.binlog.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DigestUtil {
	
	private static final Logger logger = LoggerFactory.getLogger(DigestUtil.class);
	
	private static MessageDigest sha1;
	
	static {
		try {
			sha1 = MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException e) {
			logger.error("Digest algorithm sha1 not found.", e);
		}
	}
	
	public static byte[] sha1(byte[] message) {
		return sha1.digest(message);
	}
	
	public static void main(String[] args) {
		System.out.println(sha1("cadsfa".getBytes()));
	}

}
