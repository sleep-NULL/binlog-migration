package com.sleep.binlog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sleep.binlog.net.BinlogClient;
import com.sleep.binlog.util.ThreadUtil;

public class BinlogMigration {

	private static final Logger logger = LoggerFactory.getLogger(BinlogMigration.class);

	public static void main(String[] args) {
		logger.info("BinlogMigration start ...");
		ThreadUtil.newThread(new BinlogClient("10.0.30.152", 3306, "canal", "123456", "mysql-bin.000050", 4), "BinlogClient");
	}

}
