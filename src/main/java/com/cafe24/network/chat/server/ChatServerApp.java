package com.cafe24.network.chat.server;

import java.io.IOException;
import java.io.Writer;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

//main thread
public class ChatServerApp {
	private static final int PORT = 7000;

	public static void main(String[] args) {
		// 서버 소켓 생성
		List<Writer> listWriter = new ArrayList<Writer>();
		ServerSocket serverSocket = null;

		try {
			serverSocket = new ServerSocket();
			// 서버 소켓 바인딩
			serverSocket.bind(new InetSocketAddress("0.0.0.0", PORT));
			log("server start...[port:" + PORT + "]");

			while (true) {
				// 클라이언트와의 연결대기
				Socket socket = serverSocket.accept();
				Thread thread = new ChatServerThread(socket, listWriter);
				thread.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (serverSocket != null && serverSocket.isClosed() == false) {
					serverSocket.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void log(String log) {
		System.out.println("[Server#" + Thread.currentThread().getId() + "] " + log);
	}
}
