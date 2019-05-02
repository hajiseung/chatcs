package com.cafe24.network.chat.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.Socket;
import java.util.List;

//여기서 printWriter 사용하면 ChatClientApp로 전달되어진다.
public class ChatServerThread extends Thread {
	private Socket socket;
	private List<Writer> listWriter;
	private String nickname;
	private BufferedReader bufferedReader;
	private PrintWriter printWriter;

	public ChatServerThread(Socket socket, List<Writer> listWriter) {
		this.socket = socket;
		this.listWriter = listWriter;
	}

	@Override
	public void run() {
		try {
			// 스트림 얻기
			bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));
			printWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

			// 요청 처리
			while (true) {
				String request = bufferedReader.readLine();
				if (request == null) {
					doQUit(printWriter);
					break;
				}

				// 프로토콜 분석
				System.out.println(request);
				String[] tokens = request.split(":");
				if ("join".equals(tokens[0])) {
					doJoin(tokens[1], printWriter);
				} else if ("message".equals(tokens[0])) {
					doMessage(tokens[1]);
				} else if ("quit".equals(tokens[0])) {
					doQUit(printWriter);
					break;
				} else {
					ChatServerApp.log("에러:알 수 없는 요청(" + tokens[0] + ")");
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void doQUit(Writer writer) {
		removeWriter(writer);
		String data = this.nickname + "님이 퇴장 하였습니다.";
		broadcast(data);
	}

	private void removeWriter(Writer writer) {
		synchronized (writer) {
			listWriter.remove(writer);
		}
	}

	private void doMessage(String string) {
		broadcast("[" + this.nickname + "]:" + string);
	}

	private void doJoin(String nickname, Writer writer) {
		this.nickname = nickname;
		String data = nickname + "님이 참여하셨습니다.";
		broadcast(data);
		// WriterPool에 저장
		addWriter(writer);

		// ack(클라이언트에게 전달)
		printWriter.println("join:ok");
		printWriter.flush();
	}

	private void broadcast(String data) {
		synchronized (listWriter) {
			for (Writer tmp : listWriter) {
				PrintWriter printWriter = (PrintWriter) tmp;
				printWriter.println(data);
				printWriter.flush();
			}
		}
	}

	private void addWriter(Writer writer) {
		synchronized (listWriter) {
			listWriter.add(writer);
		}
	}

}
