package com.sleep.binlog;

import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sleep.binlog.net.BinlogClient;
import com.sleep.binlog.util.ThreadUtil;

public class BinlogMigration {
	
	private static final Logger logger = LoggerFactory.getLogger(BinlogMigration.class);
	
	public static void main(String[] args) throws UnknownHostException {
		logger.info("BinlogMigration start ...");
		ThreadUtil.newThread(new BinlogClient("localhost", 3306, "canal", "canal"), "BinlogClient");
	}

}
