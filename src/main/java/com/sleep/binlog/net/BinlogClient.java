package com.sleep.binlog.net;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sleep.binlog.protocol.ComQuery;
import com.sleep.binlog.protocol.ErrPacket;
import com.sleep.binlog.protocol.HandShake;
import com.sleep.binlog.protocol.HandShakeResponse;
import com.sleep.binlog.protocol.OkPacket;
import com.sleep.binlog.protocol.Packet;

public class BinlogClient implements Runnable {
	
	private static final Logger logger  = LoggerFactory.getLogger(BinlogClient.class);
	
	private Selector selector;
	
	private SocketChannel client;
	
	private MysqlChannel mysqlChannel;
	
	private AtomicBoolean isRunning = new AtomicBoolean(false);
	
	private String username;
	
	private String password;
	
	public BinlogClient(String hostname, int port, String username, String password) {
		try {
			selector = Selector.open();
			client = SocketChannel.open();
			client.configureBlocking(false);
			client.socket().setKeepAlive(true);
			client.register(selector, SelectionKey.OP_CONNECT);
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
		while (isRunning.get()) {
			try {
				int readyNum = selector.select(300L);
				if (readyNum != 0) {
					Iterator<SelectionKey> it = selector.selectedKeys().iterator();
					while (it.hasNext()) {
						SelectionKey key = it.next();
						it.remove();
						if (key.isConnectable()) {
							if (client.isConnectionPending()) {
								if (client.finishConnect()) {
									HandShake handshake = new HandShake(mysqlChannel.readPacket());
									logger.info(handshake.toString());
									HandShakeResponse res = new HandShakeResponse(handshake.getCharacterSet(), username, password, handshake.getAuthPluginDataPart1() + handshake.getAuthPluginDataPart2());
									mysqlChannel.sendPachet(res, 1);
									key.interestOps(SelectionKey.OP_READ);
								}
							}
						} else if (key.isReadable()) {
							readGenericPacket();
							mysqlChannel.sendPachet(new ComQuery("set @master_binlog_checksum='NONE'"), 0);
						} else if (key.isWritable()) {
						}
					}
				}
			} catch (Exception e) {
				logger.error("", e);
				return;
			}
		}
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
	
}
