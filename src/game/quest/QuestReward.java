package game.quest;

public class QuestReward {
    private final int gold;
    private final int xp;
    private final String[] itemIds;

    public QuestReward(int gold, int xp, String... itemIds) {
        this.gold    = gold;
        this.xp      = xp;
        this.itemIds = itemIds != null ? itemIds : new String[0];
    }

    public int      getGold()    { return gold; }
    public int      getXp()      { return xp; }
    public String[] getItemIds() { return itemIds; }
}
