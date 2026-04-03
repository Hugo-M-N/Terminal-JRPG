package game.quest;

import java.util.HashMap;
import java.util.Map;

public class FetchQuest extends Quest {

    // itemId -> required amount
    private final Map<String, Integer> required = new HashMap<>();
    private final Map<String, Integer> current  = new HashMap<>();

    public FetchQuest(String id, String title, String description, QuestReward reward) {
        super(id, title, description, reward);
    }

    public void addRequirement(String itemId, int amount) {
        required.put(itemId.toUpperCase(), amount);
        current.put(itemId.toUpperCase(), 0);
    }

    @Override
    public void onItemObtained(String itemId) {
        if (!isActive()) return;
        String key = itemId.toUpperCase();
        if (required.containsKey(key))
            current.put(key, Math.min(current.get(key) + 1, required.get(key)));
    }

    @Override
    public boolean checkCompletion() {
        for (Map.Entry<String, Integer> e : required.entrySet())
            if (current.getOrDefault(e.getKey(), 0) < e.getValue()) return false;
        return true;
    }

    @Override
    public String getProgressText() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Integer> e : required.entrySet())
            sb.append(current.getOrDefault(e.getKey(), 0)).append("/").append(e.getValue())
              .append(" ").append(e.getKey()).append("  ");
        return sb.toString().trim();
    }
}
