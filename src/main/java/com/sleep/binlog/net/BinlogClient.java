package com.sleep.binlog.net;

import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sleep.binlog.protocol.HandShake;

public class BinlogClient implements Runnable {
	
	private static final Logger logger  = LoggerFactory.getLogger(BinlogClient.class);
	
	private Selector selector;
	
	private SocketChannel client;
	
	private MysqlChannel mysqlChannel;
	
	private AtomicBoolean isRunning = new AtomicBoolean(false);
	
	public BinlogClient(String hostname, int port) {
		try {
			selector = Selector.open();
			client = SocketChannel.open();
			client.configureBlocking(false);
			client.connect(new InetSocketAddress(hostname, port));
			client.register(selector, SelectionKey.OP_CONNECT);
			this.mysqlChannel = new MysqlChannel(client);
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
									System.out.println(handshake);
								} else {
									System.out.println("finishing connect");
								}
							} else {
								System.out.println("connection pending");
							}
						} else if (key.isReadable()) {
							System.out.println("readable");
						} else if (key.isWritable()) {
							System.out.println("writable");
						}
					}
				}
			} catch (Exception e) {
				logger.error("", e);
			}
		}
	}
	
}
