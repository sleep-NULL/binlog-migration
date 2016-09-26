package com.sleep.binlog.protocol;

import java.io.IOException;

/**
 * @author huangyafeng
 * 
 *         <a href="http://dev.mysql.com/doc/internals/en/com-query.html">
 *
 */
public class ComQuery extends Output {

	private int commandId = 0x03;

	public ComQuery(String query) throws IOException {
		super();
		writeInt(commandId, 1);
		writeString(query);
	}

}
