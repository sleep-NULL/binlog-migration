package com.sleep.binlog.net;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.SocketChannel;


public class MysqlChannel implements Channel {
	
	private SocketChannel socketChannel;
	
	private ByteBuffer packetLength = ByteBuffer.allocate(3);
	
	private ByteBuffer seq = ByteBuffer.allocate(1);
	
	public MysqlChannel(SocketChannel socketChannel) {
		this.socketChannel = socketChannel;
	}
	
	@Override
	public boolean isOpen() {
		return socketChannel.isOpen();
	}

	@Override
	public void close() throws IOException {
		socketChannel.close();
	}
	
	private int toInt(byte value) {
		return value & 0xff;
	}
	
	private long toLong(byte value) {
		return (long)toInt(value);
	}
	
	public ByteBuffer readPacket() throws IOException {
		int payloadLength = readInt(3);
		socketChannel.read(seq);
		seq.rewind();
		ByteBuffer payload = ByteBuffer.allocate(payloadLength);
		socketChannel.read(payload);
		payload.flip();
		return payload;
	}
	
	/**
	 * 读取小头序的 integer
	 * 
	 * @param length
	 * @return
	 * @throws IOException
	 */
	public int readInt(int length) throws IOException {
		ByteBuffer buf = ByteBuffer.allocate(length);
		socketChannel.read(buf);
		buf.flip();
		int result = 0;
		for (int i = 0; i < length; i++) {
			result |= (toInt(buf.get()) << (i << 3));
		}
		return result;
	}

	/**
	 * 读取小头序的 long
	 * 
	 * @param length
	 * @return
	 * @throws IOException
	 */
	public long readLong(int length) throws IOException {
		ByteBuffer buf = ByteBuffer.allocate(length);
		socketChannel.read(buf);
		long result = 0;
		for (int i = 0; i < length; i++) {
			result |= (toLong(buf.get()) << (i << 3));
		}
		return result;
	}

}
