package com.cafe24.network.chat.server;

import java.net.Socket;

public class ChatServerReceiveThread extends Thread {
	private Socket socket;
	private String nickname;

	public ChatServerReceiveThread(Socket socket, String name) {
		this.socket = socket;
		nickname = name;
	}

	@Override
	public void run() {
	}

}
