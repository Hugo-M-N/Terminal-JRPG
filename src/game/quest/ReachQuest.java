package game.quest;

public class ReachQuest extends Quest {

    private final String targetZoneId;
    private boolean reached = false;

    public ReachQuest(String id, String title, String description,
                      QuestReward reward, String targetZoneId) {
        super(id, title, description, reward);
        this.targetZoneId = targetZoneId.toUpperCase();
    }

    @Override
    public void onZoneReached(String zoneId) {
        if (!isActive()) return;
        if (targetZoneId.equals(zoneId.toUpperCase())) reached = true;
    }

    @Override
    public boolean checkCompletion() { return reached; }

    @Override
    public String getProgressText() {
        return reached ? "Reached " + targetZoneId : "Find " + targetZoneId;
    }
}
