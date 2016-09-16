package com.sleep.binlog.protocol;

import java.io.IOException;

/**
 * @author huangyafeng
 * 
 *         <a href="http://dev.mysql.com/doc/internals/en/com-binlog-dump.html">
 *
 */
public class ComBinlogDump extends Response {

	private static final int commandId = 0x12;

	public ComBinlogDump(int binlogPos, int flags, int serverId, String binlogFilename) throws IOException {
		super();
		writeInt(commandId, 1);
		writeLong(binlogPos, 4);
		writeInt(flags, 2);
		writeLong(serverId, 4);
		writeString(binlogFilename);
	}

}
