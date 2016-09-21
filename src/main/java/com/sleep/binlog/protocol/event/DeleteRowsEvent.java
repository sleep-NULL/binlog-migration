package com.sleep.binlog.protocol.event;

import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DeleteRowsEvent extends RowsEvent {
	// ================header===============
	private long tableId;

	private int flags;

	private String extraData;

	// =================body=================
	private long columnCount;

	private int[] bitmap;

	// =================rows=================
	private List<Serializable[]> rows;

	public DeleteRowsEvent(ByteBuffer buf, Map<Long, TableMapEvent> tableMap) throws IOException {
		super(buf);
		this.tableId = readLong(6);
		this.flags = readInt(2);
		this.extraData = readFixedLengthString(readInt(2) - 2);
		this.columnCount = readLengthEncodedInt();
		this.bitmap = readBigedianBitmap((int) columnCount);
		int numOneBitmap = 0;
		for (int i = 0; i < bitmap.length; i++) {
			if (bitmap[i] == 1) {
				numOneBitmap++;
			}
		}
		this.rows = new LinkedList<Serializable[]>();
		while (remaining() > 4) {
			rows.add(deserializeRow(tableMap.get(tableId), bitmap, numOneBitmap));
		}
	}

	public long getTableId() {
		return tableId;
	}

	public void setTableId(long tableId) {
		this.tableId = tableId;
	}

	public int getFlags() {
		return flags;
	}

	public void setFlags(int flags) {
		this.flags = flags;
	}

	public String getExtraData() {
		return extraData;
	}

	public void setExtraData(String extraData) {
		this.extraData = extraData;
	}

	public long getColumnCount() {
		return columnCount;
	}

	public void setColumnCount(long columnCount) {
		this.columnCount = columnCount;
	}

	public int[] getBitmap() {
		return bitmap;
	}

	public void setBitmap(int[] bitmap) {
		this.bitmap = bitmap;
	}

	public List<Serializable[]> getRows() {
		return rows;
	}

	public void setRows(List<Serializable[]> rows) {
		this.rows = rows;
	}

	@Override
	public String toString() {
		return "DeleteRowsEvent [tableId=" + tableId + ", flags=" + flags + ", extraData=" + extraData
				+ ", columnCount=" + columnCount + ", bitmap=" + Arrays.toString(bitmap) + ", rows=" + rows + "]";
	}

}
