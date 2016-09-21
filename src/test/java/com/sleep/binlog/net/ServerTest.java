package com.sleep.binlog.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerTest {

	public static void main(String[] args) throws IOException {
		ServerSocket server = new ServerSocket(4000);

		Socket client = server.accept();

		System.out.println(client.getInetAddress().getHostAddress());
	}

}
