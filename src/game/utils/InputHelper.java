package game.utils;

import java.io.IOException;

import org.jline.keymap.BindingReader;
import org.jline.keymap.KeyMap;
import org.jline.terminal.Attributes;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.InfoCmp.Capability;

public class InputHelper {
    public static Terminal TERMINAL;
    public static Attributes ORIGINAL_ATTRS;
    private static boolean menuMode= false;
    
    static {
    	try {
    		TERMINAL = TerminalBuilder.builder()
    				.system(true)
    		        .jna(true)
    		        .build();
    	} catch (IOException e) {
			System.err.println(e);
		}
    }
        

    public static final BindingReader READER = new BindingReader(TERMINAL.reader());
    public static final KeyMap<String> KEYMAP = createKeyMap();

    private static KeyMap<String> createKeyMap() {
        KeyMap<String> km = new KeyMap<>();
        km.setAmbiguousTimeout(50);
        km.bind("UP",    KeyMap.key(TERMINAL, Capability.key_up));
        km.bind("DOWN",  KeyMap.key(TERMINAL, Capability.key_down));
        km.bind("LEFT",  KeyMap.key(TERMINAL, Capability.key_left));
        km.bind("RIGHT", KeyMap.key(TERMINAL, Capability.key_right));
        km.bind("ENTER", "\r");
        km.bind("ESCAPE", "\033");
        return km;
    }
    
    public static boolean checkMenuMode() {
    	return menuMode;
    }
    
    public static void enableMenuMode() {
        ORIGINAL_ATTRS = TERMINAL.enterRawMode();
        TERMINAL.puts(Capability.cursor_invisible);
        TERMINAL.flush();
        menuMode = true;
    }

    public static void enableTextMode() {
        TERMINAL.setAttributes(ORIGINAL_ATTRS);
        TERMINAL.puts(Capability.cursor_visible);
        TERMINAL.flush();
        menuMode = false;
    }
    
    public static void clearScreen() {
    	InputHelper.TERMINAL.puts(Capability.clear_screen);
    	InputHelper.TERMINAL.flush();
    }
}

