package com.cafe24.network.chat.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChatServerApp {
	private static final int PORT = 7000;

	public static void main(String[] args) {
		// 서버 소켓 생성
		List<Writer> writePool = new ArrayList<Writer>();
		ServerSocket serverSocket = null;

		try {
			serverSocket = new ServerSocket();
			// 서버 소켓 바인딩
			serverSocket.bind(new InetSocketAddress("0.0.0.0", PORT));
			log("server start...[port:" + PORT + "]");

			while (true) {
				// 클라이언트와의 연결대기
				Socket socket = serverSocket.accept();

				log("전전전");
				Thread thread = new ChatServerThread(socket, writePool);
				thread.start();
				log("후후후");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void log(String log) {
		System.out.println("[Server#" + Thread.currentThread().getId() + "] " + log);
	}
}
