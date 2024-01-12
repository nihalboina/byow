package byow.Core;
/**
 * Created by hug.
 */

import byow.InputDemo.InputSource;
import edu.princeton.cs.algs4.StdDraw;

import java.awt.*;

public class KeyboardInputSource2 implements InputSource {
    private static final boolean PRINT_TYPED_KEYS = false;


    public KeyboardInputSource2(String pName) {
        StdDraw.setCanvasSize(90 * 16, 45 * 16);
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(0.4, 0.9, "Welcome ");
        StdDraw.setPenColor(Color.RED);
        StdDraw.text(0.5, 0.9, pName);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(0.65, 0.9, " to the 61B Game!");
        StdDraw.text(0.5, 0.7, "Change Character Name (C)");
        StdDraw.text(0.5, 0.5, "New Game (N)");
        StdDraw.text(0.5, 0.3, "Load Game (L)");
        StdDraw.text(0.5, 0.1, "Quit Game (Q)");
    }


    public char getNextKey() {
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char c = Character.toUpperCase(StdDraw.nextKeyTyped());
                if (PRINT_TYPED_KEYS) {
                    System.out.print(c);
                }
                return c;
            }

        }
    }

    public boolean possibleNextInput() {
        return true;
    }
}
