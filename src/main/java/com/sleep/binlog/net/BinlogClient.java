package com.sleep.binlog.net;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	
	private HandShake handshake;
	
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
									handshake = new HandShake(mysqlChannel.readPacket());
									System.out.println(handshake);
									key.interestOps(SelectionKey.OP_WRITE);
								} else {
									System.out.println("finishing connect");
								}
							} else {
								System.out.println("connection pending");
							}
						} else if (key.isReadable()) {
							ByteBuffer packet = mysqlChannel.readPacket();
							switch (packet.get() & 0xff) {
							case Packet.OK_HEADER:
								System.out.println(new OkPacket(packet));
								break;
							case Packet.ERR_HEADER:
								System.out.println(new ErrPacket(packet));
								break;
							}
						} else if (key.isWritable()) {
							HandShakeResponse res = new HandShakeResponse(handshake.getCharacterSet(), username, password, handshake.getAuthPluginDataPart1() + handshake.getAuthPluginDataPart2());
							mysqlChannel.sendPachet(res, 1);
							key.interestOps(SelectionKey.OP_READ);
						}
					}
				}
			} catch (Exception e) {
				logger.error("", e);
				return;
			}
		}
	}
	
}
