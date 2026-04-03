package game.npc;

import java.util.ArrayList;
import java.util.List;

import game.TextMenus;
import game.entity.Entity;
import game.item.Item;
import game.quest.Quest;

public class Npc {
	String id;
	String name;
	String[] dialogs;
	boolean hasShop;
	List<Item> shopItems;
	boolean hasQuest;
	List<Quest> quests;
	
	public void shop(ArrayList<Entity> Allies) {
		TextMenus.shopMenu(shopItems, Allies);
	}
	
	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String[] getDialogs() {
		return dialogs;
	}

	public boolean HasShop() {
		return hasShop;
	}

	public List<Item> getShopItems() {
		return shopItems;
	}

	public boolean HasQuest() {
		return hasQuest;
	}

	public List<Quest> getQuests() {
		return quests;
	}

	public Npc(String id, String name, String[] dialogs, boolean hasShop, List<Item> shopItems, boolean hasQuest,
			List<Quest> quests) {
		super();
		this.id = id;
		this.name = name;
		this.dialogs = dialogs;
		this.hasShop = hasShop;
		this.shopItems = shopItems;
		this.hasQuest = hasQuest;
		this.quests = quests;
	}
	
	
	
}
