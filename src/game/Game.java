package game;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFrame;

import game.GameStateManager.GameMode;
import game.entity.ClassManager;
import game.entity.EnemyManager;
import game.entity.Entity;
import game.item.ItemManager;
import game.map.Tileset;
import game.skill.SkillManager;
import game.sprite.SpriteManager;
import game.npc.NpcManager;
import game.zone.ZoneManager;

public class Game implements Runnable {

    private JFrame window;
    private Canvas canvas;

    private InputManager input;
    private GameStateManager state;
    private Renderer renderer;

    private boolean running = true;

    @Override
    public void run() {
        initWindow();
        loadManagers();
        initState();
        gameLoop();
    }

    // --- Window ---

    private void initWindow() {
        window = new JFrame("Terminal JRPG");
        canvas = new Canvas();

        canvas.setPreferredSize(new Dimension(Config.getWINDOW_WIDTH(), Config.getWINDOW_HEIGTH()));
        canvas.setBackground(Color.BLACK);

        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.add(canvas);
        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);

        canvas.requestFocus();
        canvas.createBufferStrategy(3);
    }

    // --- Asset loading ---

    private void loadManagers() {
        try {
            Tileset.loadTileSet();
            SpriteManager.loadItems();
            SpriteManager.loadMonsters();
            SpriteManager.loadPeople();
            SkillManager.loadSkills();
            ClassManager.LoadClasses();
            ItemManager.loadItemFiles();
            EnemyManager.loadEnemies();
            NpcManager.loadNpcFiles();
            ZoneManager.loadZones();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to load game assets. Exiting.");
            System.exit(1);
        }
    }

    // --- State initialization ---

    private void initState() {
        input    = new InputManager(canvas);
        renderer = new Renderer(canvas);
        state    = new GameStateManager(input, canvas);

        // Temp test data — replace with new game screen
        ZoneManager.setCurrentZone("TEST");
        state.setCurrentZone(ZoneManager.getCurrentZone());
    }

    // --- Game loop ---

    private void gameLoop() {
        while (running) {
            state.update();
            renderer.render(state);
                        

            // Testing Heal
            if(state.getPx()==7 && state.getPy()==7) {
            	if(state.getAllies()==null) continue;
            	for(Entity e : state.getAllies()) {
            		e.setHP(e.getMAX_HP());
            		e.setMP(e.getMAX_MP());
            	}
            }
            
            try {
                Thread.sleep(16); // ~60 fps
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                running = false;
            }
        }
    }
}