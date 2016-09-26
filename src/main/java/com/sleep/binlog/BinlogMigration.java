package com.sleep.binlog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sleep.binlog.listener.EventListener;
import com.sleep.binlog.protocol.entry.Entry;

public class BinlogMigration {

	private static final Logger logger = LoggerFactory.getLogger(BinlogMigration.class);

	public static void main(String[] args) {
		logger.info("BinlogMigration start ...");
		BinlogClient binlogClient = new BinlogClient("localhost", 3306, "canal", "canal", 2, "mysql-bin.000001", 4);
		binlogClient.setListener(new EventListener() {
			@Override
			public void onEentry(Entry entry) {
				System.out.println(entry);
			}
		});
		binlogClient.start();
		logger.info("BinlogMigration start ok.");
	}

}
