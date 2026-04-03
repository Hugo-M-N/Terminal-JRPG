package game.quest;

import java.util.List;

public class KillQuest extends Quest {

    private final List<String> targetIds; // enemy IDs that count
    private final int          required;
    private int                current = 0;

    public KillQuest(String id, String title, String description,
                     QuestReward reward, int required, List<String> targetIds) {
        super(id, title, description, reward);
        this.required  = required;
        this.targetIds = targetIds;
    }

    @Override
    public void onKill(String enemyId) {
        if (!isActive()) return;
        if (targetIds.isEmpty() || targetIds.contains(enemyId.toUpperCase()))
            current = Math.min(current + 1, required);
    }

    @Override
    public boolean checkCompletion() { return current >= required; }

    @Override
    public String getProgressText() {
        return current + "/" + required + " killed";
    }

    public int getCurrent()      { return current; }
    public int getRequired()     { return required; }
    public void setCurrent(int n){ this.current = n; }
}
