package game.point21;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;

public class Point21 {

	/**21��ֽ����Ϸ
	 * @param args
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 * @author eryeer
	 * @Version 1.0
	 * @since 2017.4.2
	 */
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		System.out.println("��ӭ������˹ά��˹21���ս,ף�����Ŷ~��!");
		Player p = new Player("����",500,0,0,0); 
		//������Ϸ��ʼ����,�½����ȡ�浵
		startPage(p);
		Scanner input = new Scanner(System.in);
		while(true) {
			gameStart(p);
			System.out.println("�����ڵ��ʲ�Ϊ$" + p.getMoney());
			if (p.getMoney() == 0) {
				System.out.println("���Ѿ�һƶ��ϴ��,�Ͻ��ؼ�׬Ǯ��!");
				p.reportStatus();
				p.deleteRecord();
				System.exit(0);
			}
			System.out.println("�Ƿ�����һ��? Y or N");
			while(true) {
				String string = input.nextLine();
				if("N".equalsIgnoreCase(string)){
					System.out.println("��Ϸ����,�Զ�������ļ�¼");
					p.reportStatus();
					p.saveRecord();
					System.exit(0);
				} else if ("Y".equalsIgnoreCase(string)) {
					break;
				} else {
					System.out.println("�������,��������");
				}
			}
		}
	}

	/**
	 * ��ʼҳ��,�½������
	 * @param p
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static void startPage(Player p) throws IOException,
			ClassNotFoundException {
		System.out.println("ѡ��\"1\":����Ϸ,\"2\":����");
		Scanner input = new Scanner(System.in);
		File file = new File(Player.SAVE_NAME);
		a : while(true) {
			String line = input.nextLine();
			if ("1".equals(line)) {
				if (file.exists() && file.length() > 0) {
					System.out.println("���оɴ浵,ȷ����������? Y or N");
					while(true) {
						String abandon = input.nextLine();
						if ("y".equalsIgnoreCase(abandon)) {
							p.newRecord();
							break a;
						} else if ("n".equalsIgnoreCase(abandon)) {
							System.out.println("Ϊ���ȡ�浵");
							p.loadRecord();
							p.reportStatus();
							break a;
						} else {
							System.out.println("�������,��������");
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
				System.out.println("�������,��������");
			}
		}
	}

	/**
	 * ��ʼ��Ϸ
	 */
	public static void gameStart(Player p) {
		Scanner sc = new Scanner(System.in);
		//�����ע
		System.out.println(p.getName()+",���������ʲ�$" + p.getMoney() + ",������ע");
		int bet = 0;
		while(true) {
			String betLine = sc.nextLine();
			try {
				bet = Integer.parseInt(betLine);
				if (bet > 0 && bet <= p.getMoney()) {
					System.out.println("��ע$" + bet);
					p.setMoney(p.getMoney() - bet);
					break;
				} else if (bet > p.getMoney()){
					System.out.println("��û��ô��Ǯ,��������");
				} else {
					System.out.println("Ǯ������ΪС�ڵ���0,��������");
				}
				
			} catch (Exception e ) {
				System.out.println("����Ƿ��ַ�,����������");
			}
		}
		System.out.println("��Ϸ��ʽ��ʼ,ׯ��ϴ��...");
		// ���ÿ���
		HashMap<Integer, String> cardNameSet = new HashMap<>();
		HashMap<Integer, Integer> cardPointSet = new HashMap<>();
		ArrayList<Integer> cardIndexSet = new ArrayList<>();
		setCard(cardNameSet, cardPointSet, cardIndexSet);
		// ϴ��
		Collections.shuffle(cardIndexSet);
		// ׯ�ҿ��Ƴ�ʼ��
		ArrayList<Integer> hostCardList = new ArrayList<>();
		for (int i = 0; i < 2; i++) {
			hostCardList.add(cardIndexSet.remove(0));
		}
		System.out.println("ׯ�ҳ�ȡ������,��һ��������"
				+ cardNameSet.get(hostCardList.get(0))); 
		cardVisible(cardNameSet, hostCardList.get(0));
		// System.out.println("�ڶ��Ű�����" + cardNameSet.get(hostCardList.get(1)));
		// ���blackjack,0Ϊׯ��,1Ϊ��
		boolean check0 = checkBlackJack(0, cardPointSet.get(hostCardList.get(0)),
				cardPointSet.get(hostCardList.get(1)));
		if (check0) {
			p.addLoseRound();
			return;
		}
		System.out.println("---------------------");
		// ��ҿ��Ƴ�ʼ��
		ArrayList<Integer> myCardList = new ArrayList<>();
		for (int i = 0; i < 2; i++) {
			myCardList.add(cardIndexSet.remove(0));
		}
		System.out.println("���ȡ������,�ֱ���" + cardNameSet.get(myCardList.get(0))
				+ "��" + cardNameSet.get(myCardList.get(1)));
		cardVisible(cardNameSet, myCardList.get(0));
		cardVisible(cardNameSet, myCardList.get(1));
		//������blackjack
		boolean check1 = checkBlackJack(1, cardPointSet.get(myCardList.get(0)),
				cardPointSet.get(myCardList.get(1)));
		if (check1) {
			p.setMoney(p.getMoney() + bet * 2);
			p.addWinRound();
			return ;
		}
		
		//���ѡ���ע,���ֻ����
		System.out.println("�����ѡ��:����\"1\"����,����\"2\"�ӱ�,����\"3\"����");
		while(true) {
			String s = sc.nextLine();
			if ("1".equals(s)) {
				System.out.println("Ͷע����");
				break;
			} else if ("2".equals(s)) {
				if (bet > p.getMoney()) {
					System.out.println("Ǯ����,�޷���ע,����ԭ״");
				}else {
					p.setMoney(p.getMoney() - bet);
					bet *= 2;
					System.out.println("�����ɹ�,ͶעΪ$" + bet + ",ʣ���ʲ�$" + p.getMoney());
				}
				break;
			} else if ("3".equals(s)) {
				p.setMoney(p.getMoney() + bet / 2);
				System.out.println("���ѷ���,����Ͷע$" + (bet/2) + ",ʣ���ʲ�$" + p.getMoney());
				p.addLoseRound();
				return ;
			} else {
				System.out.println("�������,��������");
			}
		}
		System.out.println("---------------------");
		// ���ƽ׶�
		while (true) {
			System.out.println("�Ƿ�Ҫ����? Y or N");
			String s = sc.nextLine();
			if ("N".equalsIgnoreCase(s)) {
				break;
			} else if ("Y".equalsIgnoreCase(s)) {
				int addNum = cardIndexSet.remove(0);
				System.out.println("����һ��" + cardNameSet.get(addNum));
				cardVisible(cardNameSet, addNum);
				if (addNum >= 48) {
					System.out.println("��ϣ�����Ʒ�ֵΪ1����11?����\"1\"��\"11\"ȷ��");
					while (true) {
						String s1 = sc.nextLine();
						if ("1".equals(s1)) {
							System.out.println("ѡ����1��");
							addNum = 52;
							break;
						} else if ("11".equals(s1)) {
							System.out.println("ѡ����11��");
							break;
						} else {
							System.out.println("���������������");
						}
					}
				}
				myCardList.add(addNum);
				// �����Ƶ�����
				int sum = getSum(myCardList, cardPointSet);
				if (sum > 21) {
					System.out.println("��ӱ���,������!");
					p.addLoseRound();
					return ;
				} else if (sum == 21) {
					System.out.println("���Ѿ��չ�21����!!");
					break;
				}
			} else {
				System.out.println("�������,��������.");
			}
		}
		System.out.println("��Ľ׶ν���");
		System.out.println("---------------------");
		// ׯ�����ƽ׶�
		System.out.println("����ׯ�ҽ׶�");
		System.out.println("�ڶ��Ű�����" + cardNameSet.get(hostCardList.get(1)));
		cardVisible(cardNameSet, hostCardList.get(1));
		int count = 1;
		while (true) {
			// ��ׯ�����Ƶ�����
			int sum = getSum(hostCardList, cardPointSet);
			if (sum > 21) {// ׯ�ұ���
				if (count > 1 && hostCardList.get(count) >=48 && hostCardList.get(count) <= 51) {
					hostCardList.set(count, 52);
				} else {
					System.out.println("ׯ�ұ���,��Ӯ��!");
					p.setMoney(p.getMoney() + bet * 2);
					p.addWinRound();
					return ;
				}
			} else if (sum < 17) {// ׯ�Ҽ�������
				int addNum = cardIndexSet.remove(0);
				count++;
				hostCardList.add(addNum);
				System.out.println("����һ��" + cardNameSet.get(addNum));
				cardVisible(cardNameSet, addNum);
			} else {// �Ƚϵ���
				int mySum = getSum(myCardList, cardPointSet);
				if (sum > mySum) {
					System.out.println("ׯ���ܵ�����,ׯ��Ӯ��!");
					p.addLoseRound();
					return;
				} else if (sum < mySum) {
					System.out.println("���ܵ�����,��Ӯ��!");
					p.setMoney(p.getMoney() + bet * 2);
					p.addWinRound();
					return;
				} else {
					System.out.println("����һ��,ƽ��!");
					p.setMoney(p.getMoney() + bet);
					p.addFairRound();
					return;
				}
			}
		}
	}

	/**
	 * ���Ƶĵ������
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
	 * ���BlackJack
	 * 
	 */
	public static boolean checkBlackJack(int i, Integer integer, Integer integer2) {
		switch (i) {
		case 0:
			if (integer + integer2 == 21) {
				System.out.println("ׯ�һ����BlackJack!������");
				return true;
			}
		case 1:
			if (integer + integer2 == 21) {
				System.out.println("������BlackJack!ׯ������");
				return true;
			}
		default:
			return false;
		}
	}

	/**
	 * ���ÿ���
	 * 
	 * @param cardNameSet
	 * @param cardPointSet
	 * @param cardIndexSet
	 */
	public static void setCard(HashMap<Integer, String> cardNameSet,
			HashMap<Integer, Integer> cardPointSet,
			ArrayList<Integer> cardIndexSet) {
		String[] color = { "��Ƭ", "÷��", "����", "����" };
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
		// ��A��1��ֵ�����ֵ��
		cardPointSet.put(index++, 1);
	}

	/**
	 * ����DOS���ڿ��ӻ�
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
