package game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;
import java.util.*;
import java.util.List;

public class test1 extends Canvas implements Runnable {

    // ====== Window ======
    static final int W = 1280;
    static final int H = 720;
    static final int FPS = 60;

    private JFrame frame;
    private Thread thread;
    private volatile boolean running;

    // ====== Battle model ======
    static class Battler {
        String name;
        boolean enemy;
        int hp, hpMax;
        int mp, mpMax; // PP in GS terms
        int speed;     // affects ATB fill
        double atb;    // 0..100
        boolean alive = true;

        Battler(String name, boolean enemy, int hpMax, int mpMax, int speed) {
            this.name = name;
            this.enemy = enemy;
            this.hpMax = hpMax;
            this.hp = hpMax;
            this.mpMax = mpMax;
            this.mp = mpMax;
            this.speed = speed;
        }
    }

    private final List<Battler> party = new ArrayList<>();
    private final List<Battler> foes = new ArrayList<>();
    private final Deque<Battler> readyQueue = new ArrayDeque<>();

    // ====== UI State ======
    enum UIState { IDLE, COMMAND, TARGET, MESSAGE }
    private UIState uiState = UIState.IDLE;

    // whose turn is being commanded (must be party member)
    private Battler active = null;

    // command menu (Golden Sun-ish)
    private final String[] commands = {"Attack", "Psynergy", "Djinn", "Item", "Defend"};
    private int cmdIdx = 0;

    // target selection among foes
    private int targetIdx = 0;

    // message log
    private final ArrayDeque<String> log = new ArrayDeque<>();
    private long messageFreezeUntil = 0;

    // ====== Timing ======
    private long lastNs = 0;

    // ====== Controls ======
    // Up/Down: command navigation (GS-like vertical)
    // Enter: confirm
    // Esc: back
    // Left/Right: target selection

    public static void main(String[] args) {
        new test1().start();
    }

    public test1() {
        setPreferredSize(new Dimension(W, H));
        setFocusable(true);

        // seed sample battle
        party.add(new Battler("ISAAC", false, 120, 48, 14));
        party.add(new Battler("GARET", false, 150, 22, 10));
        party.add(new Battler("MIA",   false, 90,  70, 16));

        foes.add(new Battler("SLIME A", true, 80, 0, 11));
        foes.add(new Battler("SLIME B", true, 80, 0, 12));

        pushLog("A wild SLIME pack appeared!");

        addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) { onKey(e.getKeyCode()); }
        });
    }

    public void start() {
        frame = new JFrame("Battle UI Sim — Golden Sun / FF7 feel (1 file)");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.add(this, BorderLayout.CENTER);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);

        requestFocus();

        running = true;
        thread = new Thread(this, "battle-ui-sim");
        thread.start();
    }

    @Override
    public void run() {
        createBufferStrategy(3);
        BufferStrategy bs = getBufferStrategy();
        lastNs = System.nanoTime();

        while (running) {
            long now = System.nanoTime();
            long dtNs = now - lastNs;
            lastNs = now;

            double dt = dtNs / 1_000_000_000.0; // seconds
            tick(dt);
            render(bs);

            // frame cap
            try { Thread.sleep(1000 / FPS); } catch (InterruptedException ignored) {}
        }
    }

    private void tick(double dt) {
        // during MESSAGE freeze, don't advance ATB (feels more turn-based/classic)
        if (System.currentTimeMillis() < messageFreezeUntil) return;

        // if we're in COMMAND/TARGET, we also freeze ATB (like classic selection pause)
        if (uiState == UIState.COMMAND || uiState == UIState.TARGET) return;

        // update ATB fill
        // tune factor to change pacing: higher = faster ATB
        double base = 18.0; // "global speed"
        for (Battler b : allBattlers()) {
            if (!b.alive) continue;

            // if already queued or actively being commanded, don't fill
            if (readyQueue.contains(b) || b == active) continue;

            b.atb += (base * b.speed) * dt * 0.6;
            if (b.atb >= 100.0) {
                b.atb = 100.0;
                readyQueue.addLast(b);
                // small log for readability (optional)
                // pushLog(b.name + " is ready!");
            }
        }

        // if nothing being commanded and queue has someone -> take turn
        if (active == null && !readyQueue.isEmpty()) {
            Battler next = readyQueue.pollFirst();
            if (!next.alive) return;

            if (next.enemy) {
                enemyAct(next);
            } else {
                beginPlayerTurn(next);
            }
        }
    }

    private List<Battler> allBattlers() {
        ArrayList<Battler> list = new ArrayList<>(party.size() + foes.size());
        list.addAll(party);
        list.addAll(foes);
        return list;
    }

    private void beginPlayerTurn(Battler who) {
        active = who;
        uiState = UIState.COMMAND;
        cmdIdx = 0;
        targetIdx = firstAliveFoeIndex();
        pushLog(who.name + " ready.");
    }

    private int firstAliveFoeIndex() {
        for (int i = 0; i < foes.size(); i++) if (foes.get(i).alive) return i;
        return 0;
    }

    private void enemyAct(Battler enemy) {
        // choose random alive party target
        List<Battler> aliveParty = party.stream().filter(p -> p.alive).toList();
        if (aliveParty.isEmpty()) return;

        Battler target = aliveParty.get(new Random().nextInt(aliveParty.size()));
        int dmg = 6 + new Random().nextInt(6);

        pushLog(enemy.name + " attacks!");
        applyDamage(target, dmg);
        pushLog(target.name + " takes " + dmg + " damage.");

        endTurn(enemy);
        freezeMessage(550);
    }

    private void applyDamage(Battler t, int dmg) {
        t.hp -= dmg;
        if (t.hp <= 0) {
            t.hp = 0;
            t.alive = false;
            pushLog(t.name + " was downed!");
        }
    }

    private void endTurn(Battler who) {
        who.atb = 0;
        if (who == active) active = null;
        uiState = UIState.IDLE;

        // victory check
        if (foes.stream().noneMatch(f -> f.alive)) {
            pushLog("Victory!");
            freezeMessage(2000);
        }
        // defeat check
        if (party.stream().noneMatch(p -> p.alive)) {
            pushLog("Defeat...");
            freezeMessage(2000);
        }
    }

    private void freezeMessage(long ms) {
        uiState = UIState.MESSAGE;
        messageFreezeUntil = System.currentTimeMillis() + ms;
    }

    private void pushLog(String s) {
        log.addLast(s);
        while (log.size() > 4) log.removeFirst();
    }

    private void onKey(int key) {
        // global quit
        if (key == KeyEvent.VK_F4) { running = false; frame.dispose(); return; }

        // if frozen by message, allow ESC to skip
        if (uiState == UIState.MESSAGE) {
            if (key == KeyEvent.VK_ESCAPE || key == KeyEvent.VK_ENTER) {
                messageFreezeUntil = 0;
                uiState = (active != null) ? UIState.COMMAND : UIState.IDLE;
            }
            return;
        }

        if (uiState == UIState.COMMAND) {
            if (key == KeyEvent.VK_UP) {
                cmdIdx = (cmdIdx - 1 + commands.length) % commands.length;
            } else if (key == KeyEvent.VK_DOWN) {
                cmdIdx = (cmdIdx + 1) % commands.length;
            } else if (key == KeyEvent.VK_ESCAPE) {
                // optional: allow "wait" (keep ready). Here we just stay.
                pushLog("Choose an action.");
            } else if (key == KeyEvent.VK_ENTER) {
                String cmd = commands[cmdIdx];
                if (cmd.equals("Attack")) {
                    uiState = UIState.TARGET;
                    targetIdx = firstAliveFoeIndex();
                    pushLog("Select target.");
                } else if (cmd.equals("Defend")) {
                    pushLog(active.name + " defends.");
                    endTurn(active);
                    freezeMessage(450);
                } else {
                    // placeholder for skill/item/djinn
                    pushLog(cmd.toUpperCase() + " (not implemented)");
                    // stay in command so you can keep tweaking feel
                    freezeMessage(450);
                }
            }
            return;
        }

        if (uiState == UIState.TARGET) {
            if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_UP) {
                targetIdx = prevAliveFoe(targetIdx);
            } else if (key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_DOWN) {
                targetIdx = nextAliveFoe(targetIdx);
            } else if (key == KeyEvent.VK_ESCAPE) {
                uiState = UIState.COMMAND;
                pushLog("Choose an action.");
            } else if (key == KeyEvent.VK_ENTER) {
                Battler t = foes.get(targetIdx);
                int dmg = 12 + new Random().nextInt(6);
                pushLog(active.name + " attacks " + t.name + "!");
                applyDamage(t, dmg);
                pushLog(t.name + " takes " + dmg + " damage.");
                endTurn(active);
                freezeMessage(550);
            }
        }
    }

    private int nextAliveFoe(int from) {
        if (foes.isEmpty()) return 0;
        for (int k = 1; k <= foes.size(); k++) {
            int i = (from + k) % foes.size();
            if (foes.get(i).alive) return i;
        }
        return from;
    }

    private int prevAliveFoe(int from) {
        if (foes.isEmpty()) return 0;
        for (int k = 1; k <= foes.size(); k++) {
            int i = (from - k);
            while (i < 0) i += foes.size();
            if (foes.get(i).alive) return i;
        }
        return from;
    }

    private void render(BufferStrategy bs) {
        do {
            do {
                Graphics2D g = (Graphics2D) bs.getDrawGraphics();
                try {
                    g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                    // background (battlefield)
                    g.setColor(new Color(14, 14, 20));
                    g.fillRect(0, 0, W, H);

                    // top battlefield area
                    drawPanel(g, 24, 24, W - 48, 300, 18);

                    // fake sprites positions
                    drawSprites(g);

                    // bottom left: status box (party)
                    int statusX = 24;
                    int statusY = 24 + 300 + 14;
                    int statusW = 430;
                    int statusH = H - statusY - 24;
                    drawPanel(g, statusX, statusY, statusW, statusH, 18);
                    drawPartyStatus(g, statusX, statusY, statusW, statusH);

                    // bottom right: command box
                    int cmdX = statusX + statusW + 14;
                    int cmdY = statusY;
                    int cmdW = W - cmdX - 24;
                    int cmdH = statusH;
                    drawPanel(g, cmdX, cmdY, cmdW, cmdH, 18);
                    drawCommandArea(g, cmdX, cmdY, cmdW, cmdH);

                    // battle log (overlay like classic message box)
                    drawLogBox(g, 24 + 12, H - 24 - 92, W - 48 - 24, 80);

                    // ready queue preview (FF7-ish "turn order" feel)
                    drawReadyQueue(g, 24 + 18, 24 + 18);

                } finally {
                    g.dispose();
                }
            } while (bs.contentsRestored());
            bs.show();
            Toolkit.getDefaultToolkit().sync();
        } while (bs.contentsLost());
    }

    // ====== Draw helpers (Golden Sun / FF7-ish) ======

    private void drawPanel(Graphics2D g, int x, int y, int w, int h, int arc) {
        g.setColor(new Color(0, 0, 0, 170));
        g.fillRoundRect(x, y, w, h, arc, arc);
        g.setColor(new Color(255, 255, 255, 110));
        g.drawRoundRect(x, y, w, h, arc, arc);
    }

    private void drawSprites(Graphics2D g) {
        // player side (left)
        int baseX = 90;
        int baseY = 120;

        for (int i = 0; i < party.size(); i++) {
            Battler p = party.get(i);
            int x = baseX;
            int y = baseY + i * 70;

            g.setColor(p.alive ? new Color(90, 220, 140) : new Color(80, 80, 80));
            g.fillRoundRect(x, y, 56, 56, 14, 14);
            g.setColor(new Color(255, 255, 255, 210));
            g.drawString(p.name, x + 2, y - 6);
        }

        // enemy side (right)
        int eBaseX = W - 90 - 56;
        int eBaseY = 135;

        for (int i = 0; i < foes.size(); i++) {
            Battler e = foes.get(i);
            int x = eBaseX;
            int y = eBaseY + i * 90;

            boolean selected = (uiState == UIState.TARGET && i == targetIdx && e.alive);
            g.setColor(e.alive ? new Color(240, 90, 90) : new Color(80, 80, 80));
            g.fillRoundRect(x, y, 56, 56, 14, 14);

            if (selected) {
                g.setColor(new Color(255, 240, 120, 220));
                g.drawRoundRect(x - 6, y - 6, 68, 68, 16, 16);
            }

            g.setColor(new Color(255, 255, 255, 210));
            g.drawString(e.name, x - 18, y - 6);

            // enemy HP bar
            drawBar(g, x - 90, y + 64, 140, 8, e.hp, e.hpMax, new Color(120, 220, 120, 200));
        }
    }

    private void drawPartyStatus(Graphics2D g, int x, int y, int w, int h) {
        g.setFont(g.getFont().deriveFont(Font.BOLD, 15f));
        g.setColor(new Color(240, 240, 240, 230));
        g.drawString("PARTY", x + 16, y + 26);

        int rowY = y + 44;
        for (int i = 0; i < party.size(); i++) {
            Battler p = party.get(i);

            int ry = rowY + i * 48;

            g.setFont(g.getFont().deriveFont(Font.BOLD, 14f));
            g.setColor(p == active ? new Color(255, 240, 120, 230) : new Color(240, 240, 240, 220));
            g.drawString(p.name, x + 16, ry + 18);

            // ATB bar (FF7 feel)
            drawBar(g, x + 120, ry + 8, 120, 8, (int)Math.round(p.atb), 100, new Color(255, 240, 120, 210));
            g.setFont(g.getFont().deriveFont(Font.PLAIN, 12f));
            g.setColor(new Color(220, 220, 220, 200));
            g.drawString("ATB", x + 86, ry + 16);

            // HP / PP
            g.setColor(new Color(240, 240, 240, 210));
            g.drawString("HP " + p.hp + "/" + p.hpMax, x + 260, ry + 16);
            if (p.mpMax > 0) g.drawString("PP " + p.mp + "/" + p.mpMax, x + 260, ry + 34);

            // HP bar
            drawBar(g, x + 120, ry + 24, 120, 8, p.hp, p.hpMax, new Color(120, 220, 120, 200));

            // Downed overlay
            if (!p.alive) {
                g.setColor(new Color(255, 255, 255, 120));
                g.drawString("DOWN", x + 16, ry + 34);
            }
        }
    }

    private void drawCommandArea(Graphics2D g, int x, int y, int w, int h) {
        g.setFont(g.getFont().deriveFont(Font.BOLD, 15f));
        g.setColor(new Color(240, 240, 240, 230));

        String title = switch (uiState) {
            case COMMAND -> "COMMAND";
            case TARGET  -> "TARGET";
            default      -> "WAIT";
        };
        g.drawString(title, x + 16, y + 26);

        g.setFont(g.getFont().deriveFont(Font.PLAIN, 15f));

        if (uiState == UIState.COMMAND) {
            int startY = y + 56;
            for (int i = 0; i < commands.length; i++) {
                boolean sel = (i == cmdIdx);
                g.setColor(sel ? new Color(255, 240, 120, 230) : new Color(240, 240, 240, 210));
                g.drawString((sel ? "> " : "  ") + commands[i], x + 18, startY + i * 26);
            }
            g.setColor(new Color(200, 200, 200, 140));
            g.drawString("ENTER confirm", x + 18, y + h - 18);

        } else if (uiState == UIState.TARGET) {
            g.setColor(new Color(240, 240, 240, 210));
            g.drawString("Choose an enemy", x + 18, y + 60);
            g.setColor(new Color(200, 200, 200, 140));
            g.drawString("←/→ select | ENTER confirm | ESC back", x + 18, y + h - 18);

        } else {
            g.setColor(new Color(200, 200, 200, 170));
            g.drawString("Waiting for ATB...", x + 18, y + 60);
            g.setColor(new Color(200, 200, 200, 120));
            g.drawString("When ready, COMMAND opens", x + 18, y + 84);
        }
    }

    private void drawLogBox(Graphics2D g, int x, int y, int w, int h) {
        drawPanel(g, x, y, w, h, 16);

        g.setFont(g.getFont().deriveFont(Font.PLAIN, 14f));
        g.setColor(new Color(240, 240, 240, 230));

        int ty = y + 26;
        for (String s : log) {
            g.drawString(s, x + 14, ty);
            ty += 18;
        }
    }

    private void drawReadyQueue(Graphics2D g, int x, int y) {
        // small row of "NEXT" icons like FF7 turn feel
        g.setFont(g.getFont().deriveFont(Font.BOLD, 12f));
        g.setColor(new Color(240, 240, 240, 180));
        g.drawString("READY:", x, y);

        int ix = x + 60;
        int iy = y - 10;

        int shown = 0;
        for (Battler b : readyQueue) {
            if (shown >= 6) break;
            g.setColor(b.enemy ? new Color(240, 90, 90, 200) : new Color(90, 220, 140, 200));
            g.fillRoundRect(ix, iy, 18, 18, 6, 6);
            g.setColor(new Color(255, 255, 255, 200));
            g.drawString(b.name.substring(0, 1), ix + 6, iy + 13);
            ix += 22;
            shown++;
        }
    }

    private void drawBar(Graphics2D g, int x, int y, int w, int h, int val, int max, Color fillColor) {
        int arc = 10;

        g.setColor(new Color(255, 255, 255, 50));
        g.fillRoundRect(x, y, w, h, arc, arc);

        int fill = (max <= 0) ? 0 : (int)Math.round((val / (double)max) * w);
        fill = Math.max(0, Math.min(w, fill));

        g.setColor(fillColor);
        g.fillRect(x, y, fill, h);

        g.setColor(new Color(255, 255, 255, 110));
        g.drawRoundRect(x, y, w, h, arc, arc);
    }
}
