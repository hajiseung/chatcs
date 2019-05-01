package com.cafe24.network.chat.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

import com.cafe24.network.chat.server.ChatWindow;

public class ChatClientApp {
	private static final String SERVER_IP = "192.168.0.5";
	private static final int SERVER_PORT = 7000;

	public static void main(String[] args) {
		Scanner scn = new Scanner(System.in);
		Socket socket = new Socket();
		String nickname = null;
		try {
			socket.connect(new InetSocketAddress(SERVER_IP, SERVER_PORT));
			log("Server연결됨");

			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));
			PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "utf-8"), true);

			// join프로토콜
			while (true) {
				System.out.println("대화명을 입력하세요.");
				System.out.print(">>>");
				nickname = scn.nextLine();
				if (nickname.isEmpty() == false) {
					printWriter.println("join:" + nickname);
					printWriter.flush();
					break;
				}
				System.out.println("대화명은 한글자 이상 입력해야 합니다.\n");
			}
			String data = bufferedReader.readLine();
			System.out.println("client reader data:" + data);
			// chatClientReceiveThread시작
			new ChatWindow(nickname, socket).show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void log(String log) {
		System.out.println("[Client] " + log);
	}
}
