package com.cafe24.network.chat.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

public class ChatServerThread extends Thread {
	private Socket socket;
	private String nickname;
	private List<Writer> writePool;

	public ChatServerThread(Socket socket, List<Writer> writePool) {
		this.socket = socket;
		this.writePool = writePool;
	}

	@Override
	public void run() {
		InetSocketAddress inetRemoteSocketAddress = (InetSocketAddress) socket.getRemoteSocketAddress();
		String remoteHostAddress = inetRemoteSocketAddress.getAddress().getHostAddress();
		int remotePort = inetRemoteSocketAddress.getPort();
		log("Connected by client[" + remoteHostAddress + ":" + remotePort + "]," + socket);

		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "utf-8"), true);
			while (true) {
				String request = br.readLine();
				log("request=" + request);
				String[] tokens = request.split(":");
				log("tokens=" + Arrays.toString(tokens));
//				if (request == null) {
//					log("클라이언트로 부터 연결 끊김");
//					break;
//				}

				if ("join".equals(tokens[0])) {
					doJoin(tokens[1], pw);
				} else if ("msg".equals(tokens[0])) {
					doMsg(tokens[1], pw);
				} else if ("quit".equals(tokens[0])) {
					doQuit(tokens[1], pw);
				} else {
					ChatServerApp.log("Error : 알 수 없는 요청입니다...(" + tokens[0] + ")");
				}
				// 프로토콜 분석
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void doQuit(String quit, Writer pw) {
	}

	private void doMsg(String msg, Writer pw) {
	}

	private void doJoin(String nickname, Writer pw) {
		this.nickname = nickname;
		String data = nickname + "님이 참여하였습니다.";
		System.out.println(data);
		addWriter(pw);
		brocast(data);
		pw.println("join:ok");
		pw.flush();
	}

	private void brocast(String data) {
		synchronized (writePool) {
			for (PrintWriter tmp : writePool) {
				PrintWriter printWriter = tmp;
				printWriter.println(data);
				printWriter.flush();
			}
		}
	}

	private void addWriter(Writer pw) {
		synchronized (writePool) {
			writePool.add(pw);
		}
	}

	public static void log(String log) {
		System.out.println("[Client] " + log);
	}
}
