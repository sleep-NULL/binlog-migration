package com.sleep.binlog.protocol.event;

import java.nio.ByteBuffer;

import com.sleep.binlog.protocol.Protocol;

public class WriteRowsEvent extends Protocol {
	
	private long tableId;
	
	private int flags;
	
	private long columnCount;
	
	private int[] bitmap1;
	
	private int[] bitmap2;
	
	private int[] nullBitmap;
	
	private int[] field;

	public WriteRowsEvent(ByteBuffer buf) {
		super(buf);
		
	}

}
