package game.trigger;

public class Trigger {

    public enum Action {
        COMBAT, ZONE_TRANSITION, DIALOGUE, GIVE_ITEM, QUEST_START, QUEST_END, EVENT
    }

    private boolean active;
    private final int x, y;
    private final Action action;
    private final String[] params;

    public Trigger(boolean active, int x, int y, Action action, String[] params) {
        this.active = active;
        this.x      = x;
        this.y      = y;
        this.action = action;
        this.params = params;
    }

    public boolean isActive()            { return active; }
    public void    setActive(boolean v)  { this.active = v; }
    public int     getX()                { return x; }
    public int     getY()                { return y; }
    public Action  getAction()           { return action; }
    public String[] getParams()          { return params; }

    /** Convenience: returns params[i] or null if out of bounds. */
    public String getParam(int i) {
        return (params != null && i < params.length) ? params[i] : null;
    }
}
