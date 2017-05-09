package game.point21;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Scanner;
/**
 * @��ɫ��
 * @version 1.0
 * @author eryeer
 * @since 2017.4.2
 * */
public class Player implements Serializable {
	
	//�汾����̖
	private static final long serialVersionUID = 1L;
	//�������
	private String name;
	//��ҳ�ʼ�ʽ�
	private int money;
	//���ʧ�ܾ���
	private int loseRound;
	//���ʤ������
	private int winRound;
	//���ƽ����
	private int fairRound;
	//�浵�ļ���
	public static final String SAVE_NAME = "save";
	//�ղι���
	public Player() {
		super();
		
	}
	//�вι���
	public Player(String name, int money, int failRound, int winRound,
			int fairRound) {
		super();
		this.name = name;
		this.money = money;
		this.loseRound = failRound;
		this.winRound = winRound;
		this.fairRound = fairRound;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getMoney() {
		return money;
	}

	public void setMoney(int money) {
		this.money = money;
	}
	
	
	public int getLoseRound() {
		return loseRound;
	}

	public void addLoseRound() {
		this.loseRound = this.loseRound + 1;
	}

	public int getWinRound() {
		return winRound;
	}

	public void addWinRound() {
		this.winRound = this.winRound + 1;
	}

	public int getFairRound() {
		return fairRound;
	}

	public void addFairRound() {
		this.fairRound = this.fairRound + 1;
	}
	//�����ܳ���
	public int getTotalRound() {
		return this.loseRound + this.fairRound + this.winRound;
	}
	//����ʤ��
	public String getWinningPercent() {
		if( winRound == 0) {
			return "0";
		} else {
			DecimalFormat df = new DecimalFormat("###.00%");
			return df.format((double)winRound / getTotalRound());
		}
	}
	//���ɱ���
	public void reportStatus() {
		System.out.println("��ǰ�ܾ���:" + getTotalRound() + ",��ʤ����:" + winRound + ",ʤ��:" + getWinningPercent());
	}
	//��ɫ�浵
	public void saveRecord() throws IOException {
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SAVE_NAME));
		oos.writeObject(this);
		oos.close();
		System.out.println("�浵�ɹ�");
		
	}
	//��ɫ����
	public void loadRecord() throws IOException, ClassNotFoundException {
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(SAVE_NAME));
			Player p = (Player)ois.readObject();
			this.name = p.name;
			this.money = p.money;
			this.fairRound = p.fairRound;
			this.loseRound = p.loseRound;
			this.winRound = p.winRound;
			ois.close();
			System.out.println("�������," + this.name + ",��ӭ����."); 
		} catch(Exception e) {
			System.out.println("�޷���ȡ�浵,�����´����¼�¼");
			this.newRecord();
		}
	}
	//�½���ɫ
	public void newRecord() throws IOException {
		a : while(true) {
			System.out.println("�������������");
			Scanner sc = new Scanner(System.in);
			String name = sc.nextLine();
			b : while(true) {
				System.out.println("ȷ������Ϊ\"" + name + "\"��? Y or N");
				String check = sc.nextLine();
				if ("y".equalsIgnoreCase(check)) {
					this.name = name;
					break a;
				} else if ("n".equalsIgnoreCase(check)) {
					break b;
				} else {
					System.out.println("��������,����������");
				}
			}
		}
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SAVE_NAME));
		oos.writeObject(this);
		oos.close();
		System.out.println("�¼�¼�������!");
		System.out.println("��������,�㽫���$500����,��һ�ζ�����Ŷ~");
		}
	//ɾ���浵
	public void deleteRecord() {
		File file = new File("save");
		file.delete();
	}
	
}
