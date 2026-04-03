package game.quest;

public abstract class Quest {

    protected String      id;
    protected String      title;
    protected String      description;
    protected QuestStatus status = QuestStatus.NOT_STARTED;
    protected QuestReward reward;

    public Quest(String id, String title, String description, QuestReward reward) {
        this.id          = id;
        this.title       = title;
        this.description = description;
        this.reward      = reward;
    }

    // ── Abstract ──────────────────────────────────────────────────────────────

    /** Short text shown in the quest list, e.g. "3/10 Goblins killed". */
    public abstract String getProgressText();

    /** Returns true if the quest conditions are met. */
    public abstract boolean checkCompletion();

    // ── Notification hooks (subclasses override what they need) ───────────────

    public void onKill(String enemyId)          {}
    public void onItemObtained(String itemId)   {}
    public void onZoneReached(String zoneId)    {}

    // ── Getters / Setters ─────────────────────────────────────────────────────

    public String      getId()          { return id; }
    public String      getTitle()       { return title; }
    public String      getDescription() { return description; }
    public QuestStatus getStatus()      { return status; }
    public QuestReward getReward()      { return reward; }

    public boolean isActive()    { return status == QuestStatus.ACTIVE; }
    public boolean isCompleted() { return status == QuestStatus.COMPLETED; }

    public void setStatus(QuestStatus status) { this.status = status; }
}
