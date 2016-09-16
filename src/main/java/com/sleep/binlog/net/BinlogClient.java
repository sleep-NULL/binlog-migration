package com.sleep.binlog.net;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
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

public class BinlogClient implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(BinlogClient.class);

	// private Selector selector;

	private SocketChannel client;

	private MysqlChannel mysqlChannel;

	private AtomicBoolean isRunning = new AtomicBoolean(false);

	private String username;

	private String password;

	public BinlogClient(String hostname, int port, String username, String password) {
		try {
			// selector = Selector.open();
			client = SocketChannel.open();
			// client.configureBlocking(false);
			client.socket().setKeepAlive(true);
			client.socket().setSoTimeout(1000 * 60);
			// client.register(selector, SelectionKey.OP_CONNECT);
			client.connect(new InetSocketAddress(hostname, port));
			this.mysqlChannel = new MysqlChannel(client);
			this.username = username;
			this.password = password;
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
			mysqlChannel.sendPachet(new ComBinlogDump(865, 0, 2, "mysql-bin.000005"), 0);
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
			System.out.println("binlog event");
			break;
		case Packet.ERR_HEADER:
			ErrPacket errPacket = new ErrPacket(packet);
			logger.error(errPacket.toString());
		}
	}

}
