package game.point21;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;

public class Point21 {

	/**21点纸牌游戏
	 * @param args
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 * @author eryeer
	 * @Version 1.0
	 * @since 2017.4.2
	 */
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		System.out.println("欢迎来到拉斯维加斯21点大战,祝你好运哦~亲!");
		Player p = new Player("匿名",500,0,0,0); 
		//进入游戏开始界面,新建或读取存档
		startPage(p);
		Scanner input = new Scanner(System.in);
		while(true) {
			gameStart(p);
			System.out.println("你现在的资产为$" + p.getMoney());
			if (p.getMoney() == 0) {
				System.out.println("你已经一贫如洗了,赶紧回家赚钱吧!");
				p.reportStatus();
				p.deleteRecord();
				System.exit(0);
			}
			System.out.println("是否再来一局? Y or N");
			while(true) {
				String string = input.nextLine();
				if("N".equalsIgnoreCase(string)){
					System.out.println("游戏结束,自动保存你的记录");
					p.reportStatus();
					p.saveRecord();
					System.exit(0);
				} else if ("Y".equalsIgnoreCase(string)) {
					break;
				} else {
					System.out.println("输入错误,重新输入");
				}
			}
		}
	}

	/**
	 * 开始页面,新建或读档
	 * @param p
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static void startPage(Player p) throws IOException,
			ClassNotFoundException {
		System.out.println("选择\"1\":新游戏,\"2\":读档");
		Scanner input = new Scanner(System.in);
		File file = new File(Player.SAVE_NAME);
		a : while(true) {
			String line = input.nextLine();
			if ("1".equals(line)) {
				if (file.exists() && file.length() > 0) {
					System.out.println("你有旧存档,确定放弃它吗? Y or N");
					while(true) {
						String abandon = input.nextLine();
						if ("y".equalsIgnoreCase(abandon)) {
							p.newRecord();
							break a;
						} else if ("n".equalsIgnoreCase(abandon)) {
							System.out.println("为你读取存档");
							p.loadRecord();
							p.reportStatus();
							break a;
						} else {
							System.out.println("输入错误,重新输入");
						}
					}
					
				} else {
					p.newRecord();
					break a;
				}
			} else if ("2".equals(line)) {
				p.loadRecord();
				p.reportStatus();
				break a;
			} else {
				System.out.println("输入错误,重新输入");
			}
		}
	}

	/**
	 * 开始游戏
	 */
	public static void gameStart(Player p) {
		Scanner sc = new Scanner(System.in);
		//玩家下注
		System.out.println(p.getName()+",你现在总资产$" + p.getMoney() + ",请先下注");
		int bet = 0;
		while(true) {
			String betLine = sc.nextLine();
			try {
				bet = Integer.parseInt(betLine);
				if (bet > 0 && bet <= p.getMoney()) {
					System.out.println("下注$" + bet);
					p.setMoney(p.getMoney() - bet);
					break;
				} else if (bet > p.getMoney()){
					System.out.println("你没那么多钱,重新输入");
				} else {
					System.out.println("钱数不能为小于等于0,重新输入");
				}
				
			} catch (Exception e ) {
				System.out.println("输入非法字符,请重新输入");
			}
		}
		System.out.println("游戏正式开始,庄家洗牌...");
		// 配置卡牌
		HashMap<Integer, String> cardNameSet = new HashMap<>();
		HashMap<Integer, Integer> cardPointSet = new HashMap<>();
		ArrayList<Integer> cardIndexSet = new ArrayList<>();
		setCard(cardNameSet, cardPointSet, cardIndexSet);
		// 洗牌
		Collections.shuffle(cardIndexSet);
		// 庄家卡牌初始化
		ArrayList<Integer> hostCardList = new ArrayList<>();
		for (int i = 0; i < 2; i++) {
			hostCardList.add(cardIndexSet.remove(0));
		}
		System.out.println("庄家抽取两张牌,第一张明牌是"
				+ cardNameSet.get(hostCardList.get(0))); 
		cardVisible(cardNameSet, hostCardList.get(0));
		// System.out.println("第二张暗牌是" + cardNameSet.get(hostCardList.get(1)));
		// 检查blackjack,0为庄家,1为我
		boolean check0 = checkBlackJack(0, cardPointSet.get(hostCardList.get(0)),
				cardPointSet.get(hostCardList.get(1)));
		if (check0) {
			p.addLoseRound();
			return;
		}
		System.out.println("---------------------");
		// 玩家卡牌初始化
		ArrayList<Integer> myCardList = new ArrayList<>();
		for (int i = 0; i < 2; i++) {
			myCardList.add(cardIndexSet.remove(0));
		}
		System.out.println("你抽取两张牌,分别是" + cardNameSet.get(myCardList.get(0))
				+ "和" + cardNameSet.get(myCardList.get(1)));
		cardVisible(cardNameSet, myCardList.get(0));
		cardVisible(cardNameSet, myCardList.get(1));
		//检查玩家blackjack
		boolean check1 = checkBlackJack(1, cardPointSet.get(myCardList.get(0)),
				cardPointSet.get(myCardList.get(1)));
		if (check1) {
			p.setMoney(p.getMoney() + bet * 2);
			p.addWinRound();
			return ;
		}
		
		//玩家选择加注,保持或放弃
		System.out.println("你可以选择:输入\"1\"保持,输入\"2\"加倍,输入\"3\"放弃");
		while(true) {
			String s = sc.nextLine();
			if ("1".equals(s)) {
				System.out.println("投注不变");
				break;
			} else if ("2".equals(s)) {
				if (bet > p.getMoney()) {
					System.out.println("钱不够,无法加注,保持原状");
				}else {
					p.setMoney(p.getMoney() - bet);
					bet *= 2;
					System.out.println("翻倍成功,投注为$" + bet + ",剩余资产$" + p.getMoney());
				}
				break;
			} else if ("3".equals(s)) {
				p.setMoney(p.getMoney() + bet / 2);
				System.out.println("你已放弃,返回投注$" + (bet/2) + ",剩余资产$" + p.getMoney());
				p.addLoseRound();
				return ;
			} else {
				System.out.println("输入错误,重新输入");
			}
		}
		System.out.println("---------------------");
		// 摸牌阶段
		while (true) {
			System.out.println("是否要摸牌? Y or N");
			String s = sc.nextLine();
			if ("N".equalsIgnoreCase(s)) {
				break;
			} else if ("Y".equalsIgnoreCase(s)) {
				int addNum = cardIndexSet.remove(0);
				System.out.println("摸了一张" + cardNameSet.get(addNum));
				cardVisible(cardNameSet, addNum);
				if (addNum >= 48) {
					System.out.println("你希望该牌分值为1还是11?输入\"1\"或\"11\"确认");
					while (true) {
						String s1 = sc.nextLine();
						if ("1".equals(s1)) {
							System.out.println("选择了1分");
							addNum = 52;
							break;
						} else if ("11".equals(s1)) {
							System.out.println("选择了11分");
							break;
						} else {
							System.out.println("输入错误重新输入");
						}
					}
				}
				myCardList.add(addNum);
				// 求手牌点数和
				int sum = getSum(myCardList, cardPointSet);
				if (sum > 21) {
					System.out.println("你加爆了,你输了!");
					p.addLoseRound();
					return ;
				} else if (sum == 21) {
					System.out.println("你已经凑够21点了!!");
					break;
				}
			} else {
				System.out.println("输入错误,重新输入.");
			}
		}
		System.out.println("你的阶段结束");
		System.out.println("---------------------");
		// 庄家摸牌阶段
		System.out.println("进入庄家阶段");
		System.out.println("第二张暗牌是" + cardNameSet.get(hostCardList.get(1)));
		cardVisible(cardNameSet, hostCardList.get(1));
		int count = 1;
		while (true) {
			// 求庄家手牌点数和
			int sum = getSum(hostCardList, cardPointSet);
			if (sum > 21) {// 庄家爆牌
				if (count > 1 && hostCardList.get(count) >=48 && hostCardList.get(count) <= 51) {
					hostCardList.set(count, 52);
				} else {
					System.out.println("庄家爆了,你赢了!");
					p.setMoney(p.getMoney() + bet * 2);
					p.addWinRound();
					return ;
				}
			} else if (sum < 17) {// 庄家继续摸牌
				int addNum = cardIndexSet.remove(0);
				count++;
				hostCardList.add(addNum);
				System.out.println("摸了一张" + cardNameSet.get(addNum));
				cardVisible(cardNameSet, addNum);
			} else {// 比较点数
				int mySum = getSum(myCardList, cardPointSet);
				if (sum > mySum) {
					System.out.println("庄家总点数大,庄家赢了!");
					p.addLoseRound();
					return;
				} else if (sum < mySum) {
					System.out.println("你总点数大,你赢了!");
					p.setMoney(p.getMoney() + bet * 2);
					p.addWinRound();
					return;
				} else {
					System.out.println("点数一样,平局!");
					p.setMoney(p.getMoney() + bet);
					p.addFairRound();
					return;
				}
			}
		}
	}

	/**
	 * 卡牌的点数求和
	 * */
	public static int getSum(ArrayList<Integer> CardList,
			HashMap<Integer, Integer> cardPointSet) {
		int sum = 0;
		for (Integer integer : CardList) {
			sum += cardPointSet.get(integer);
		}
		return sum;
	}

	/**
	 * 检查BlackJack
	 * 
	 */
	public static boolean checkBlackJack(int i, Integer integer, Integer integer2) {
		switch (i) {
		case 0:
			if (integer + integer2 == 21) {
				System.out.println("庄家获得了BlackJack!你输了");
				return true;
			}
		case 1:
			if (integer + integer2 == 21) {
				System.out.println("你获得了BlackJack!庄家输了");
				return true;
			}
		default:
			return false;
		}
	}

	/**
	 * 配置卡牌
	 * 
	 * @param cardNameSet
	 * @param cardPointSet
	 * @param cardIndexSet
	 */
	public static void setCard(HashMap<Integer, String> cardNameSet,
			HashMap<Integer, Integer> cardPointSet,
			ArrayList<Integer> cardIndexSet) {
		String[] color = { "方片", "梅花", "黑桃", "红桃" };
		String[] number = { "2", "3", "4", "5", "6", "7", "8", "9", "10", "J",
				"Q", "K", "A" };
		int index = 0;
		for (int i = 0; i < number.length; i++) {
			for (int j = 0; j < color.length; j++) {
				cardNameSet.put(index, color[j].concat(number[i]));
				cardIndexSet.add(index);
				if (index < 32) {
					cardPointSet.put(index, Integer.parseInt(number[i]));
				} else if (index >= 32 && index < 48) {
					cardPointSet.put(index, 10);
				} else {
					cardPointSet.put(index, 11);
				}
				index++;
			}
		}
		// 将A的1分值加入分值库
		cardPointSet.put(index++, 1);
	}

	/**
	 * 卡牌DOS窗口可视化
	 * */
	public static void cardVisible(HashMap<Integer, String> cardNameSet,
			Integer integer) {
		String string = cardNameSet.get(integer);
		char[] c = string.toCharArray();
		ArrayList<Character> list = new ArrayList<>();
		for (Character character : c) {
			list.add(character);
		}
		if (list.size() == 3) {
			list.add(' ');
		}
		System.out.println(" - - - - -");
		System.out.println("|" + list.get(2) + list.get(3) + "       |");
		System.out.println("|" + "         |");
		System.out.println("|    " + list.get(0) + list.get(1) + "         |");
		System.out.println("|" + "         |");
		System.out.println("|" + "         |");
		System.out.println("|" + "         |");
		System.out.println(" - - - - -");
	}
}
