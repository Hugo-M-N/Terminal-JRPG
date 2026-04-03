package game.trigger;

import java.util.ArrayList;
import java.util.List;

public class TriggerManager {

    private static final List<Trigger> triggers = new ArrayList<>();

    // ── Loading ───────────────────────────────────────────────────────────────

    /**
     * Parses trigger entries from a map file line.
     * Format per entry: active;x;y;ACTION;param1;param2;...
     * Entries are separated by '#'.
     */
    public static void load(String[] entries) {
        triggers.clear();
        if (entries == null) return;
        for (String entry : entries) {
            if (entry == null || entry.isBlank()) continue;
            String[] parts = entry.trim().split(";");
            if (parts.length < 4) continue;
            try {
                boolean active       = Boolean.parseBoolean(parts[0]);
                int x                = Integer.parseInt(parts[1]);
                int y                = Integer.parseInt(parts[2]);
                Trigger.Action action = Trigger.Action.valueOf(parts[3].toUpperCase());
                String[] params      = new String[parts.length - 4];
                System.arraycopy(parts, 4, params, 0, params.length);
                triggers.add(new Trigger(active, x, y, action, params));
            } catch (Exception e) {
                System.err.println("TriggerManager: failed to parse entry: " + entry);
            }
        }
    }

    // ── Queries ───────────────────────────────────────────────────────────────

    /** Returns the first active trigger at (x, y), or null. */
    public static Trigger getActiveAt(int x, int y) {
        for (Trigger t : triggers)
            if (t.isActive() && t.getX() == x && t.getY() == y) return t;
        return null;
    }

    // ── Progression ──────────────────────────────────────────────────────────

    /**
     * Deactivates the current active trigger at (x, y) and activates the next
     * one at the same position (if any).
     */
    public static void advance(int x, int y) {
        boolean deactivated = false;
        for (Trigger t : triggers) {
            if (t.getX() != x || t.getY() != y) continue;
            if (!deactivated && t.isActive()) {
                t.setActive(false);
                deactivated = true;
            } else if (deactivated) {
                t.setActive(true);
                break;
            }
        }
    }

    // ── Save / Load state ────────────────────────────────────────────────────

    /** Returns all triggers (for serialisation in SaveManager). */
    public static List<Trigger> getAll() { return triggers; }
}
