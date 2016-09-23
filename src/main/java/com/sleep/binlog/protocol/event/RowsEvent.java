package com.sleep.binlog.protocol.event;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.util.Calendar;

import com.sleep.binlog.protocol.ColumnType;
import com.sleep.binlog.protocol.Protocol;

public abstract class RowsEvent extends Protocol {

	private static final int DIG_PER_DEC = 9;
	private static final int[] DIG_TO_BYTES = { 0, 1, 1, 2, 2, 3, 3, 4, 4, 4 };

	public RowsEvent(ByteBuffer buf) {
		super(buf);
	}

	public Serializable getFieldValue(int type, int meta, int length) {
		switch (type) {
		case ColumnType.MYSQL_TYPE_BIT:
			int bitSetLength = (meta >> 8) * 8 + (meta & 0xFF);
			return readBitmap(bitSetLength);
		case ColumnType.MYSQL_TYPE_BLOB:
			return read(readInt(meta));
		case ColumnType.MYSQL_TYPE_DATE:
			int value = readInt(3);
			int day = value % 32;
			value >>>= 5;
			int month = value % 16;
			int year = value >> 4;
			return asUnixTime(year, month, day, 0, 0, 0, 0);
		case ColumnType.MYSQL_TYPE_DATETIME:
			int[] split = split(readLong(8), 100, 6);
			return asUnixTime(split[5], split[4], split[3], split[2], split[1], split[0], 0);
		case ColumnType.MYSQL_TYPE_DATETIME2:
			long datetime = bigEndianLong(read(5), 0, 5);
			int yearMonth = extractBits(datetime, 1, 17, 40);
			return asUnixTime(yearMonth / 13, yearMonth % 13, extractBits(datetime, 18, 5, 40),
					extractBits(datetime, 23, 5, 40), extractBits(datetime, 28, 6, 40),
					extractBits(datetime, 34, 6, 40), getFractionalSeconds(meta));
		case ColumnType.MYSQL_TYPE_DOUBLE:
			return Double.longBitsToDouble(readLong(8));
		case ColumnType.MYSQL_TYPE_ENUM:
			return readInt(length);
		case ColumnType.MYSQL_TYPE_FLOAT:
			return Float.intBitsToFloat((int) readLong(4));
		case ColumnType.MYSQL_TYPE_GEOMETRY:
			int dataLength = readInt(meta);
			return read(dataLength);
		case ColumnType.MYSQL_TYPE_INT24:
			return (readInt(3) << 8) >> 8;
		case ColumnType.MYSQL_TYPE_LONG:
			return readLong(4);
		case ColumnType.MYSQL_TYPE_LONGLONG:
			return readLong(8);
		case ColumnType.MYSQL_TYPE_NEWDECIMAL:
			int precision = meta & 0xFF, scale = meta >> 8, x = precision - scale;
			int ipd = x / DIG_PER_DEC, fpd = scale / DIG_PER_DEC;
			int decimalLength = (ipd << 2) + DIG_TO_BYTES[x - ipd * DIG_PER_DEC] + (fpd << 2)
					+ DIG_TO_BYTES[scale - fpd * DIG_PER_DEC];
			return asBigDecimal(precision, scale, read(decimalLength));
		case ColumnType.MYSQL_TYPE_SET:
			return readLong(length);
		case ColumnType.MYSQL_TYPE_SHORT:
			return readInt(2);
		case ColumnType.MYSQL_TYPE_STRING:
			return read(length < 256 ? readInt(1) : readInt(2));
		case ColumnType.MYSQL_TYPE_VARCHAR:
		case ColumnType.MYSQL_TYPE_VAR_STRING:
			return readByte(meta < 256 ? readInt(1) : readInt(2));
		case ColumnType.MYSQL_TYPE_TIME:
			int v = readInt(3);
			int[] sp = split(v, 100, 3);
			return asUnixTime(1970, 1, 1, sp[2], sp[1], sp[0], 0);
		case ColumnType.MYSQL_TYPE_TIME2:
			long time = bigEndianLong(read(3), 0, 3);
			return asUnixTime(1970, 1, 1, extractBits(time, 2, 10, 24), extractBits(time, 12, 6, 24),
					extractBits(time, 18, 6, 24), getFractionalSeconds(meta));
		case ColumnType.MYSQL_TYPE_TIMESTAMP:
			return readLong(4) * 1000;
		case ColumnType.MYSQL_TYPE_TIMESTAMP2:
			return bigEndianLong(read(4), 0, 4) * 1000 + getFractionalSeconds(meta);
		case ColumnType.MYSQL_TYPE_TINY:
			return readInt(1);
		case ColumnType.MYSQL_TYPE_YEAR:
			return 1900 + readInt(1);
		}
		return null;
	}

	private int getFractionalSeconds(int meta) {
		int fractionalSecondsStorageSize = getFractionalSecondsStorageSize(meta);
		if (fractionalSecondsStorageSize > 0) {
			long fractionalSeconds = bigEndianLong(read(fractionalSecondsStorageSize), 0, fractionalSecondsStorageSize);
			if (meta % 2 == 1) {
				fractionalSeconds /= 10;
			}
			return (int) (fractionalSeconds / 1000);
		}
		return 0;
	}

	private static int getFractionalSecondsStorageSize(int fsp) {
		if (fsp == 1 || fsp == 2) {
			return 1;
		}
		if (fsp == 3 || fsp == 4) {
			return 2;
		}
		if (fsp == 5 || fsp == 6) {
			return 3;
		}
		return 0;
	}

	private int[] split(long value, int divider, int length) {
		int[] result = new int[length];
		for (int i = 0; i < length - 1; i++) {
			result[i] = (int) (value % divider);
			value /= divider;
		}
		result[length - 1] = (int) value;
		return result;
	}

	private Long asUnixTime(int year, int month, int day, int hour, int minute, int second, int millis) {
		if (year == 0 || month == 0 || day == 0) {
			return null;
		}
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, month - 1);
		c.set(Calendar.DAY_OF_MONTH, day);
		c.set(Calendar.HOUR_OF_DAY, hour);
		c.set(Calendar.MINUTE, minute);
		c.set(Calendar.SECOND, second);
		c.set(Calendar.MILLISECOND, millis);
		return c.getTimeInMillis();
	}

	private BigDecimal asBigDecimal(int precision, int scale, int[] value) {
		boolean positive = (value[0] & 0x80) == 0x80;
		value[0] ^= 0x80;
		if (!positive) {
			for (int i = 0; i < value.length; i++) {
				value[i] ^= 0xFF;
			}
		}
		int x = precision - scale;
		int ipDigits = x / DIG_PER_DEC;
		int ipDigitsX = x - ipDigits * DIG_PER_DEC;
		int ipSize = (ipDigits << 2) + DIG_TO_BYTES[ipDigitsX];
		int offset = DIG_TO_BYTES[ipDigitsX];
		BigDecimal ip = offset > 0 ? BigDecimal.valueOf(bigEndianInteger(value, 0, offset)) : BigDecimal.ZERO;
		for (; offset < ipSize; offset += 4) {
			int i = bigEndianInteger(value, offset, 4);
			ip = ip.movePointRight(DIG_PER_DEC).add(BigDecimal.valueOf(i));
		}
		int shift = 0;
		BigDecimal fp = BigDecimal.ZERO;
		for (; shift + DIG_PER_DEC <= scale; shift += DIG_PER_DEC, offset += 4) {
			int i = bigEndianInteger(value, offset, 4);
			fp = fp.add(BigDecimal.valueOf(i).movePointLeft(shift + DIG_PER_DEC));
		}
		if (shift < scale) {
			int i = bigEndianInteger(value, offset, DIG_TO_BYTES[scale - shift]);
			fp = fp.add(BigDecimal.valueOf(i).movePointLeft(scale));
		}
		BigDecimal result = ip.add(fp);
		return positive ? result : result.negate();
	}

	private static int bigEndianInteger(int[] bytes, int offset, int length) {
		int result = 0;
		for (int i = offset; i < (offset + length); i++) {
			result = (result << 8) | bytes[i];
		}
		return result;
	}

	private static long bigEndianLong(int[] bytes, int offset, int length) {
		long result = 0;
		for (int i = offset; i < (offset + length); i++) {
			result = (result << 8) | bytes[i];
		}
		return result;
	}

	private static int extractBits(long value, int bitOffset, int numberOfBits, int payloadSize) {
		long result = value >> payloadSize - (bitOffset + numberOfBits);
		return (int) (result & ((1 << numberOfBits) - 1));
	}

	protected Serializable[] deserializeRow(TableMapEvent tableMapEvent, int[] nullBitmap, int numOneBitmap)
			throws IOException {
		int[] types = tableMapEvent.getColumnTypeDef();
		int[] metadata = tableMapEvent.getColumnMetaDef();
		Serializable[] result = new Serializable[numOneBitmap];
		int[] nullColumns = readBigedianBitmap(result.length);
		for (int i = 0, numberOfSkippedColumns = 0; i < types.length; i++) {
			if (nullBitmap[i] == 0) {
				numberOfSkippedColumns++;
				continue;
			}
			int index = i - numberOfSkippedColumns;
			if (nullColumns[index] == 0) {
				int typeCode = types[i], meta = metadata[i], length = 0;
				if (typeCode == ColumnType.MYSQL_TYPE_STRING) {
					if (meta >= 256) {
						int meta0 = meta >> 8, meta1 = meta & 0xFF;
						if ((meta0 & 0x30) != 0x30) {
							typeCode = meta0 | 0x30;
							length = meta1 | (((meta0 & 0x30) ^ 0x30) << 4);
						} else {
							if (meta0 == ColumnType.MYSQL_TYPE_ENUM || meta0 == ColumnType.MYSQL_TYPE_SET) {
								typeCode = meta0;
							}
							length = meta1;
						}
					} else {
						length = meta;
					}
				}
				result[index] = getFieldValue(typeCode, meta, length);
			}
		}
		return result;
	}
}
