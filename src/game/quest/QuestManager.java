package game.quest;

import java.util.ArrayList;
import java.util.List;

import game.entity.Entity;
import game.item.Item;
import game.item.ItemManager;

public class QuestManager {

    private static final List<Quest> quests = new ArrayList<>();

    // ── Registration ─────────────────────────────────────────────────────────

    public static void register(Quest q) {
        if (getQuest(q.getId()) == null) quests.add(q);
    }

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    public static void startQuest(String id) {
        Quest q = getQuest(id);
        if (q != null && q.getStatus() == QuestStatus.NOT_STARTED)
            q.setStatus(QuestStatus.ACTIVE);
    }

    /**
     * Tries to complete the quest and distribute rewards to the party.
     * Returns one List<String> per level-up gained (empty outer list = no level-ups / quest not completed).
     */
    public static List<List<String>> tryComplete(String id, List<Entity> party) {
        Quest q = getQuest(id);
        if (q == null || !q.isActive() || !q.checkCompletion()) return new ArrayList<>();

        q.setStatus(QuestStatus.COMPLETED);
        return distributeReward(q.getReward(), party);
    }

    // ── Notifications (called by engine systems) ──────────────────────────────

    public static void notifyKill(String enemyId) {
        for (Quest q : quests) if (q.isActive()) q.onKill(enemyId);
        autoComplete(null); // check without distributing rewards yet
    }

    public static void notifyItemObtained(String itemId) {
        for (Quest q : quests) if (q.isActive()) q.onItemObtained(itemId);
    }

    public static void notifyZoneReached(String zoneId) {
        for (Quest q : quests) if (q.isActive()) q.onZoneReached(zoneId);
    }

    /** Checks all active quests and marks completed ones (no reward distribution). */
    private static void autoComplete(List<Entity> party) {
        for (Quest q : quests)
            if (q.isActive() && q.checkCompletion() && party != null)
                tryComplete(q.getId(), party);
    }

    // ── Queries ───────────────────────────────────────────────────────────────

    public static Quest getQuest(String id) {
        for (Quest q : quests) if (q.getId().equalsIgnoreCase(id)) return q;
        return null;
    }

    public static List<Quest> getActive() {
        List<Quest> active = new ArrayList<>();
        for (Quest q : quests) if (q.isActive()) active.add(q);
        return active;
    }

    public static List<Quest> getCompleted() {
        List<Quest> done = new ArrayList<>();
        for (Quest q : quests) if (q.isCompleted()) done.add(q);
        return done;
    }

    public static List<Quest> getAll() { return quests; }

    public static void clear() { quests.clear(); }

    // ── Reward distribution ───────────────────────────────────────────────────

    private static List<List<String>> distributeReward(QuestReward reward, List<Entity> party) {
        List<List<String>> pages = new ArrayList<>();
        if (reward == null || party == null || party.isEmpty()) return pages;

        int goldEach = reward.getGold() / party.size();
        int xpEach   = reward.getXp()   / party.size();

        for (Entity member : party) {
            member.setGOLD(member.getGOLD() + goldEach);
            for (List<String> lvl : member.addXP(xpEach)) {
                List<String> page = new ArrayList<>();
                for (String s : lvl) if (s != null && !s.isBlank()) page.add(s);
                if (!page.isEmpty()) pages.add(page);
            }
        }

        // Items go to the first party member (inventory leader)
        Entity leader = party.get(0);
        for (String itemId : reward.getItemIds()) {
            Item item = ItemManager.getPotion(itemId);
            if (item == null) item = ItemManager.getWeapon(itemId);
            if (item == null) item = ItemManager.getArmor(itemId);
            if (item != null) leader.addToInventory(item.copy());
        }
        return pages;
    }
}
