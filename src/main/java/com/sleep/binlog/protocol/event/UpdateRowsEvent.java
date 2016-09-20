package com.sleep.binlog.protocol.event;

import java.nio.ByteBuffer;

public class UpdateRowsEvent extends RowsEvent {

	public UpdateRowsEvent(ByteBuffer buf) {
		super(buf);
	}

}
