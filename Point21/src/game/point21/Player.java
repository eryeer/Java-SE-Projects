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
 * @角色类
 * @version 1.0
 * @author eryeer
 * @since 2017.4.2
 * */
public class Player implements Serializable {
	
	//版本序列號
	private static final long serialVersionUID = 1L;
	//玩家姓名
	private String name;
	//玩家初始资金
	private int money;
	//玩家失败局数
	private int loseRound;
	//玩家胜利局数
	private int winRound;
	//玩家平局数
	private int fairRound;
	//存档文件名
	public static final String SAVE_NAME = "save";
	//空参构造
	public Player() {
		super();
		
	}
	//有参构造
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
	//计算总场数
	public int getTotalRound() {
		return this.loseRound + this.fairRound + this.winRound;
	}
	//计算胜率
	public String getWinningPercent() {
		if( winRound == 0) {
			return "0";
		} else {
			DecimalFormat df = new DecimalFormat("###.00%");
			return df.format((double)winRound / getTotalRound());
		}
	}
	//生成报告
	public void reportStatus() {
		System.out.println("当前总局数:" + getTotalRound() + ",获胜局数:" + winRound + ",胜率:" + getWinningPercent());
	}
	//角色存档
	public void saveRecord() throws IOException {
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SAVE_NAME));
		oos.writeObject(this);
		oos.close();
		System.out.println("存档成功");
		
	}
	//角色读档
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
			System.out.println("读档完毕," + this.name + ",欢迎回来."); 
		} catch(Exception e) {
			System.out.println("无法读取存档,请重新创建新纪录");
			this.newRecord();
		}
	}
	//新建角色
	public void newRecord() throws IOException {
		a : while(true) {
			System.out.println("请输入玩家姓名");
			Scanner sc = new Scanner(System.in);
			String name = sc.nextLine();
			b : while(true) {
				System.out.println("确定名字为\"" + name + "\"吗? Y or N");
				String check = sc.nextLine();
				if ("y".equalsIgnoreCase(check)) {
					this.name = name;
					break a;
				} else if ("n".equalsIgnoreCase(check)) {
					break b;
				} else {
					System.out.println("输入有误,请重新输入");
				}
			}
		}
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SAVE_NAME));
		oos.writeObject(this);
		oos.close();
		System.out.println("新纪录创建完毕!");
		System.out.println("初次游玩,你将获得$500本金,别一次都用了哦~");
		}
	//删除存档
	public void deleteRecord() {
		File file = new File("save");
		file.delete();
	}
	
}
