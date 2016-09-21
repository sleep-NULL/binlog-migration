package com.sleep.binlog.net;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sleep.binlog.protocol.ComBinlogDump;
import com.sleep.binlog.protocol.ComQuery;
import com.sleep.binlog.protocol.ErrPacket;
import com.sleep.binlog.protocol.HandShake;
import com.sleep.binlog.protocol.HandShakeResponse;
import com.sleep.binlog.protocol.OkPacket;
import com.sleep.binlog.protocol.Packet;
import com.sleep.binlog.protocol.event.BinlogEventHeader;
import com.sleep.binlog.protocol.event.DeleteRowsEvent;
import com.sleep.binlog.protocol.event.EVENT_TYPE;
import com.sleep.binlog.protocol.event.RotateEvent;
import com.sleep.binlog.protocol.event.TableMapEvent;
import com.sleep.binlog.protocol.event.UpdateRowsEvent;
import com.sleep.binlog.protocol.event.WriteRowsEvent;

public class BinlogClient implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(BinlogClient.class);

	private SocketChannel client;

	private MysqlChannel mysqlChannel;

	private AtomicBoolean isRunning = new AtomicBoolean(false);

	private String username;

	private String password;
	
	private String binlogFilename;
	
	private int binlogPos;

	private Map<Long, TableMapEvent> tableMap;

	public BinlogClient(String hostname, int port, String username, String password, String binlogFilename, int binlogPos) {
		try {
			client = SocketChannel.open();
			client.socket().setKeepAlive(true);
			client.socket().setSoTimeout(1000 * 10);
			client.connect(new InetSocketAddress(hostname, port));
			this.mysqlChannel = new MysqlChannel(client);
			this.username = username;
			this.password = password;
			this.binlogFilename = binlogFilename;
			this.binlogPos = binlogPos;
			this.tableMap = new HashMap<Long, TableMapEvent>();
		} catch (Exception e) {
			logger.error("Init BinlogClient occured error.");
			throw new NetworkException("Init BinlogClient occured error.", e);
		}
	}

	@Override
	public void run() {
		isRunning.set(true);
		try {
			authorize();
			mysqlChannel.sendPachet(new ComQuery("set @master_binlog_checksum='NONE'"), 0);
			readGenericPacket();
			mysqlChannel.sendPachet(new ComBinlogDump(binlogPos, 0, 2, binlogFilename), 0);
			int i = 0;
			while (isRunning.get()) {
				System.out.println(i++);
				readBinlogEvent();
			}
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	private void authorize() throws IOException, UnsupportedEncodingException {
		HandShake handshake = new HandShake(mysqlChannel.readPacket());
		logger.info(handshake.toString());
		HandShakeResponse res = new HandShakeResponse(handshake.getCharacterSet(), username, password,
				handshake.getAuthPluginDataPart1() + handshake.getAuthPluginDataPart2());
		mysqlChannel.sendPachet(res, 1);
		readGenericPacket();
	}

	private void readGenericPacket() throws IOException, UnsupportedEncodingException {
		ByteBuffer packet = mysqlChannel.readPacket();
		switch (packet.get() & 0xff) {
		case Packet.OK_HEADER:
			logger.info(new OkPacket(packet).toString());
			break;
		case Packet.ERR_HEADER:
			ErrPacket errPacket = new ErrPacket(packet);
			logger.error(errPacket.toString());
			throw new NetworkException("Connect to mysql server failed.");
		}
	}

	private void readBinlogEvent() throws IOException {
		ByteBuffer packet = mysqlChannel.readPacket();
		switch (packet.get() & 0xff) {
		case Packet.OK_HEADER:
			BinlogEventHeader header = new BinlogEventHeader(packet);
			logger.info(header.toString());
			switch (EVENT_TYPE.valueOf(header.getEventType())) {
			case ROTATE_EVENT:
				RotateEvent rotateEvent = new RotateEvent(packet);
				logger.info(rotateEvent.toString());
				break;
			case TABLE_MAP_EVENT:
				TableMapEvent tableMapEvent = new TableMapEvent(packet);
				tableMap.put(tableMapEvent.getTableId(), tableMapEvent);
				logger.info(tableMapEvent.toString());
				break;
			case WRITE_ROWS_EVENTv2:
				WriteRowsEvent writeRowsEvent = new WriteRowsEvent(packet, tableMap);
				logger.info(writeRowsEvent.toString());
				break;
			case DELETE_ROWS_EVENTv2:
				DeleteRowsEvent deleteRowsEvent = new DeleteRowsEvent(packet, tableMap);
				logger.info(deleteRowsEvent.toString());
				break;
			case UPDATE_ROWS_EVENTv2:
				UpdateRowsEvent updateRowsEvent = new UpdateRowsEvent(packet, tableMap);
				logger.info(updateRowsEvent.toString());
				break;
			default:
				logger.info("Ignore event.");
			}
			break;
		case Packet.ERR_HEADER:
			ErrPacket errPacket = new ErrPacket(packet);
			logger.error(errPacket.toString());
			break;
		}
	}

}
