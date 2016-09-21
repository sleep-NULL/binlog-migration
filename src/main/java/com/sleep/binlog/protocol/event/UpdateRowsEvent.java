package com.sleep.binlog.protocol.event;

import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class UpdateRowsEvent extends RowsEvent {
	// ================header===============
	private long tableId;

	private int flags;

	private String extraData;

	// =================body=================
	private long columnCount;

	private int[] bitmapBefore;

	private int[] bitmapAfter;

	// =================rows=================
	private List<Serializable[]> rowsBefore;

	private List<Serializable[]> rowsAfter;

	public UpdateRowsEvent(ByteBuffer buf, Map<Long, TableMapEvent> tableMap) throws IOException {
		super(buf);
		this.tableId = readLong(6);
		this.flags = readInt(2);
		this.extraData = readFixedLengthString(readInt(2) - 2);
		this.columnCount = readLengthEncodedInt();
		this.bitmapBefore = readBigedianBitmap((int) columnCount);
		int numOneBitmapBefore = 0;
		for (int i = 0; i < bitmapBefore.length; i++) {
			if (bitmapBefore[i] == 1) {
				numOneBitmapBefore++;
			}
		}
		this.bitmapAfter = readBigedianBitmap((int) columnCount);
		int numOneBitmapAfter = 0;
		for (int i = 0; i < bitmapAfter.length; i++) {
			if (bitmapAfter[i] == 1) {
				numOneBitmapAfter++;
			}
		}

		this.rowsBefore = new LinkedList<Serializable[]>();
		this.rowsAfter = new LinkedList<Serializable[]>();
		while (remaining() > 4) {
			rowsBefore.add(deserializeRow(tableMap.get(tableId), bitmapBefore, numOneBitmapBefore));
			rowsAfter.add(deserializeRow(tableMap.get(tableId), bitmapAfter, numOneBitmapAfter));
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

	public int[] getBitmapBefore() {
		return bitmapBefore;
	}

	public void setBitmapBefore(int[] bitmapBefore) {
		this.bitmapBefore = bitmapBefore;
	}

	public int[] getBitmapAfter() {
		return bitmapAfter;
	}

	public void setBitmapAfter(int[] bitmapAfter) {
		this.bitmapAfter = bitmapAfter;
	}

	public List<Serializable[]> getRowsBefore() {
		return rowsBefore;
	}

	public void setRowsBefore(List<Serializable[]> rowsBefore) {
		this.rowsBefore = rowsBefore;
	}

	public List<Serializable[]> getRowsAfter() {
		return rowsAfter;
	}

	public void setRowsAfter(List<Serializable[]> rowsAfter) {
		this.rowsAfter = rowsAfter;
	}

	@Override
	public String toString() {
		return "UpdateRowsEvent [tableId=" + tableId + ", flags=" + flags + ", extraData=" + extraData
				+ ", columnCount=" + columnCount + ", bitmapBefore=" + Arrays.toString(bitmapBefore) + ", bitmapAfter="
				+ Arrays.toString(bitmapAfter) + ", rowsBefore=" + rowsBefore + ", rowsAfter=" + rowsAfter + "]";
	}

}
