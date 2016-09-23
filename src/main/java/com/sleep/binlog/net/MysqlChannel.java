package com.sleep.binlog.net;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.SocketChannel;

import com.sleep.binlog.protocol.Response;

/**
 * SocketChannel 的简单包装
 * 
 * @author huangyafeng
 *
 */
public class MysqlChannel implements Channel {

	private SocketChannel socketChannel;

	/**
	 * 前 3 个字节为 packet 长度, 第 4 字节为 packet sequence 号
	 */
	private ByteBuffer packetLengthAndSeq = ByteBuffer.allocate(4);

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

	private int readFull(ByteBuffer buf) throws IOException {
		int i = 0;
		do {
			i += socketChannel.read(buf);
		} while (buf.hasRemaining());
		return i;
	}

	public ByteBuffer readPacket() throws IOException {
		int payloadLength = readPacketLength();
		ByteBuffer payload = ByteBuffer.allocate(payloadLength);
		readFull(payload);
		payload.flip();
		return payload;
	}

	public void sendPachet(Response res, int seq) throws IOException {
		byte[] resBytes = res.toByteArray();
		ByteBuffer buf = ByteBuffer.allocate(4 + resBytes.length);
		writeInt(resBytes.length, 3, buf);
		writeInt(seq, 1, buf);
		buf.put(resBytes);
		buf.flip();
		do {
			socketChannel.write(buf);
		} while (buf.hasRemaining());
	}

	public int readPacketLength() throws IOException {
		readFull(packetLengthAndSeq);
		packetLengthAndSeq.flip();
		int result = 0;
		for (int i = 0; i < 3; i++) {
			result |= (toInt(packetLengthAndSeq.get()) << (i << 3));
		}
		packetLengthAndSeq.clear();
		return result;
	}

	private void writeInt(int value, int length, ByteBuffer buf) {
		for (int i = 0; i < length; i++) {
			buf.put((byte) ((value >>> (i << 3)) & 0xff));
		}
	}

}
