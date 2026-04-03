package game.npc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

import game.item.Item;
import game.item.ItemManager;
import game.quest.FetchQuest;
import game.quest.KillQuest;
import game.quest.Quest;
import game.quest.QuestManager;
import game.quest.QuestReward;
import game.quest.ReachQuest;

public class NpcManager {
	private static HashMap<String, Npc> Npcs = new HashMap<String, Npc>();

	private static int safeInt(String s) {
		try { return Integer.parseInt(s.trim()); } catch (Exception e) { return 0; }
	}
	
	public static void loadNpcFiles() {
		File npcsFile = new File("src/npcs/Npcs.csv");
		
		try {
			// Weapons
			BufferedReader reader = new BufferedReader(new FileReader(npcsFile));
			String line = reader.readLine();
			while((line=reader.readLine()) != null) {
				String[] parts = line.split(";");
				ArrayList<Item> shopItems = new ArrayList<Item>();
				for(String p : parts[4].split("#")) {
					String[] items = p.split("-");
					switch(items[0]) {
					case "ARMOR":
						shopItems.add(ItemManager.getArmor(items[1]));
						break;
					case "WEAPON":
						shopItems.add(ItemManager.getWeapon(items[1]));
						break;
					case "POTION":
						shopItems.add(ItemManager.getPotion(items[1]));
						break;
					//TO-DO: Add more item types
					}
				}
				ArrayList<Quest> quests = new ArrayList<Quest>();
				for (String p : parts[6].split("#")) {
					if (p.isBlank()) continue;
					String[] q = p.split("-");
					if (q.length < 4) continue;
					String type  = q[0];
					String qId   = q[1];
					String title = q[2];
					String desc  = q[3];
					int gold = q.length >= 2 ? safeInt(q[q.length - 2]) : 0;
					int xp   = q.length >= 1 ? safeInt(q[q.length - 1]) : 0;
					QuestReward reward = new QuestReward(gold, xp);
					switch (type) {
					case "KILL": {
						int amount = q.length > 4 ? safeInt(q[4]) : 1;
						ArrayList<String> targets = new ArrayList<>();
						if (q.length > 5) for (String s : q[5].split(":")) targets.add(s.trim().toUpperCase());
						KillQuest kq = new KillQuest(qId, title, desc, reward, amount, targets);
						quests.add(kq); QuestManager.register(kq); break;
					}
					case "FETCH": {
						FetchQuest fq = new FetchQuest(qId, title, desc, reward);
						if (q.length > 4) for (String s : q[4].split(":")) {
							String[] kv = s.split("=");
							if (kv.length == 2) fq.addRequirement(kv[0], safeInt(kv[1]));
						}
						quests.add(fq); QuestManager.register(fq); break;
					}
					case "REACH": {
						String zoneId = q.length > 4 ? q[4] : "";
						ReachQuest rq = new ReachQuest(qId, title, desc, reward, zoneId);
						quests.add(rq); QuestManager.register(rq); break;
					}
					}
				}
				Npc npc = new Npc(parts[0], parts[1], parts[2].split("#"), Boolean.valueOf(parts[3]), shopItems, Boolean.valueOf(parts[5]), quests);
				Npcs.put(parts[0], npc);
			}
			reader.close();
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Npc getNpc(String NpcID) {
		return Npcs.get(NpcID);
	}
}
