package com.sleep.binlog.net;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.SocketChannel;

import com.sleep.binlog.protocol.Response;

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
		while (payload.hasRemaining()) {
			socketChannel.read(payload);
		}
		payload.flip();
		return payload;
	}

	public void sendPachet(Response res, int seq) throws IOException {
		byte[] resBytes = res.toByteArray();
		ByteBuffer resByteBuffer = ByteBuffer.allocate(4 + resBytes.length);
		writeInt(resBytes.length, 3, resByteBuffer);
		writeInt(seq, 1, resByteBuffer);
		resByteBuffer.put(resBytes);
		resByteBuffer.flip();
		while (resByteBuffer.hasRemaining()) {
			socketChannel.write(resByteBuffer);
		}
	}

	public int readPacketLength() throws IOException {
		readFull(packetLength);
		packetLength.flip();
		int result = 0;
		for (int i = 0; i < 3; i++) {
			result |= (toInt(packetLength.get()) << (i << 3));
		}
		packetLength.clear();
		readFull(seq);
		seq.rewind();
		return result;
	}

	/**
	 * 小头序写 int
	 * 
	 * @param value
	 * @param length
	 */
	private void writeInt(int value, int length, ByteBuffer buf) {
		for (int i = 0; i < length; i++) {
			buf.put((byte) ((value >>> (i << 3)) & 0x000000ff));
		}
	}

}
