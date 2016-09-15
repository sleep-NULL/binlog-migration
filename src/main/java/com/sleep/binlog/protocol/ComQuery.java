package com.sleep.binlog.protocol;

import java.io.IOException;

public class ComQuery extends Response {
	
	private int commandId = 0x03;
	
	public ComQuery(String query) throws IOException {
		super();
		writeInt(commandId, 1);
		writeString(query);
	}

}
