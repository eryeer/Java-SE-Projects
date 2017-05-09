package com.chat;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;

public class ChatTool extends JFrame {

	private TextArea sendText;
	private TextArea viewText;
	private JButton send;
	private JButton clear;
	private JButton record;
	private JButton vib;
	private TextField tf;
	private DatagramSocket socket;
	private BufferedWriter bw;
	private String ip;

	public static void main(String[] args) {
		ChatTool dp = new ChatTool("QQ");
	}
	/**
	 * @param args
	 */
	public ChatTool(String title) {
		super(title);
		initial();
		southPanel();
		centralPanel();
		new Receive().start();
		try {
			socket = new DatagramSocket();
			bw = new BufferedWriter(new FileWriter("log.txt",true));
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		event();

	}

	public void event() {
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				try {
					bw.close();
				} catch (IOException e1) {
					
					e1.printStackTrace();
				}
				System.exit(0);
			}
		});
		send.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					send();
				} catch (Exception e1) {
					
					e1.printStackTrace();
				}
			}
		});
		clear.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				viewText.setText("");
			}
		});
		record.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					showLog();
				} catch (Exception e1) {
					
					e1.printStackTrace();
				}
			}

		});
		vib.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					shake();
				} catch (Exception e1) {
					
					e1.printStackTrace();
				}
			}

		});
		sendText.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				
				if (e.getKeyCode() == KeyEvent.VK_S && e.isAltDown()) {
					try {
						send();
					} catch (Exception e1) {
						
						e1.printStackTrace();
					}
				}
			}
		});
		
	}
	public void shake() throws Exception, IOException {
		send(ip, new byte[]{-1});
		
	}
	public void vibration() {
		int x = this.getLocation().x;
		int y = this.getLocation().y;
		try {
			for (int i = 0; i < 10; i++) {
				this.setLocation(x + 20, y + 20);
				Thread.sleep(30);
				this.setLocation(x - 20, y + 20);
				Thread.sleep(30);
				this.setLocation(x + 20, y - 20);
				Thread.sleep(30);
				this.setLocation(x , y );
			}
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}
	}
	public void showLog() throws Exception {
		bw.flush();
		BufferedInputStream bis = new BufferedInputStream(new FileInputStream("log.txt"));
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int b;
		while((b = bis.read()) != -1) {
			baos.write(b);
		}
		bis.close();
		viewText.setText(baos.toString());
		
	}
	/**
	 * @throws Exception
	 * 
	 */
	public void send() throws Exception {
		String message = sendText.getText();
		ip = "".equals(tf.getText().trim()) ? "255.255.255.255" : tf
				.getText().trim();
		byte[] arr = message.getBytes();
		send(ip, arr);
		String str = getTime() +" 你对" + (ip.equals("255.255.255.255")? "所有人" : ip) + "说:\r\n" + message + "\r\n\r\n";
		viewText.append(str);
		bw.write(str);
		sendText.setText("");
		

	}
	/**
	 * @param ip
	 * @param arr
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public void send(String ip, byte[] arr) throws UnknownHostException,
			IOException {
		DatagramPacket packet = new DatagramPacket(arr,
				arr.length, InetAddress.getByName(ip), 33333);
		socket.send(packet);
	}
	public String getTime() {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(date);
		
	}
	private void centralPanel() {
		Panel center = new Panel();
		sendText = new TextArea(5, 1);
		viewText = new TextArea();
		center.setLayout(new BorderLayout());
		center.add(sendText, BorderLayout.SOUTH);
		center.add(viewText, BorderLayout.CENTER);
		viewText.setEditable(false);
		viewText.setBackground(Color.WHITE);
		viewText.setFont(new Font("xxx", Font.PLAIN, 15));
		sendText.setFont(new Font("xxx", Font.PLAIN, 15));
		
		this.add(center, BorderLayout.CENTER);

	}

	private void southPanel() {
		Panel south = new Panel();
		tf = new TextField(12);
		tf.setText("127.0.0.1");
		send = new JButton("发 送");
		clear = new JButton("清 屏");
		record = new JButton("记 录");
		vib = new JButton("震 动");
		south.add(tf);
		south.add(send);
		south.add(clear);
		south.add(record);
		south.add(vib);
		this.add(south, BorderLayout.SOUTH);
	}

	public void initial() {

		this.setLocation(300, 50);
		this.setSize(420, 600);
		//this.setIconImage(Toolkit.getDefaultToolkit().createImage("Icon.png"));
		this.setIconImage(new ImageIcon("Icon.png").getImage());
		this.setVisible(true);

	}

	class Receive extends Thread {
		@Override
		public void run() {
			
			try {
				DatagramSocket socket = new DatagramSocket(33333);
				DatagramPacket packet = new DatagramPacket(new byte[1024 * 12], 1024 * 12);
				while(true) {
					socket.receive(packet);
					int length = packet.getLength();
					byte[] arr = packet.getData();
					if (length == 1 && arr[0] == -1) {
						vibration();
						continue;
					}
					String ip = packet.getAddress().getHostAddress();
					String message = new String(arr,0,length);
					String str = getTime() + ip + "对你说:\r\n" + message + "\r\n\r\n";
					viewText.append(str);
					bw.write(str);
					
				}
			} catch (Exception e) {
				
				e.printStackTrace();
			}
		}

		
	}
}
