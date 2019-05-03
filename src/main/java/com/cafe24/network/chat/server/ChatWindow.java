package com.cafe24.network.chat.server;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.SocketException;

public class ChatWindow {
//이 클래스에서 pw쓰면 ChatServerThread로 전달이 되어진다

	private Frame frame;
	private Panel pannel;
	private Button buttonSend;
	private TextField textField;
	private TextArea textArea;
	private Socket socket;
	private PrintWriter pw;
	private Thread inThread;

	public ChatWindow(String name, Socket socket) {
		frame = new Frame(name);
		pannel = new Panel();
		buttonSend = new Button("Send");
		textField = new TextField();
		textArea = new TextArea(30, 80);
		this.socket = socket;

		try {
			pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "utf-8"), true);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		inThread = new innerThread(socket);
		inThread.start();
	}

	private void finish() {
		// Socket 정리
		try {
			pw.println("quit");
			pw.flush();
			System.exit(0);
			inThread.join();
			socket.close();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
//		pw.println("quit");
//		System.exit(0);
	}

	public void show() {
		// Button
		buttonSend.setBackground(Color.GRAY);
		buttonSend.setForeground(Color.WHITE);
		buttonSend.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				sendMessage();
			}
		});

		// Textfield
		textField.setColumns(80);

		// 엔터 누르면 자동으로 메세지 전송
		textField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				char keyCode = e.getKeyChar();
				if (keyCode == KeyEvent.VK_ENTER) {
					sendMessage();
				}
			}
		});

		// Pannel
		pannel.setBackground(Color.LIGHT_GRAY);
		pannel.add(textField);
		pannel.add(buttonSend);
		frame.add(BorderLayout.SOUTH, pannel);

		// TextArea
		textArea.setEditable(false);
		frame.add(BorderLayout.CENTER, textArea);

		// Frame
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				finish();
			}
		});

		frame.setVisible(true);
		frame.pack();

	}

	// 쓰레드에서 이놈을 불러야함
	private void updateTextArea(String message) {
		textArea.append(message);
		textArea.append("\n");
	}

	private void sendMessage() {
		String message = textField.getText();
		if ("quit".equals(message)) {
			finish();
		} else {
			pw.println("message:" + message);
		}
		textField.setText("");
		textField.requestFocus();
	}

	public class innerThread extends Thread {
		private Socket socket;

		public innerThread(Socket socket) {
			this.socket = socket;
		}

		@Override
		public void run() {
			try {
				BufferedReader bufferedReader = new BufferedReader(
						new InputStreamReader(socket.getInputStream(), "utf-8"));
				while (true) {
					String msg = bufferedReader.readLine();
					updateTextArea(msg);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
}
