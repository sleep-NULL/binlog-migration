package com.sleep.binlog;

import com.sleep.binlog.net.BinlogClient;
import com.sleep.binlog.util.ThreadUtil;

public class BinlogMigration {
	
	public static void main(String[] args) {
		ThreadUtil.newThread(new BinlogClient("localhost", 3306), "BinlogClient");
	}

}
