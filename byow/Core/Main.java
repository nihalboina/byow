package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import edu.princeton.cs.algs4.StdDraw;

/** This is the main entry point for the program. This class simply parses
 *  the command line inputs, and lets the byow.Core.Engine class take over
 *  in either keyboard or input string mode.
 */
public class Main {
    public static void main(String[] args) {
        if (args.length > 2) {
            System.out.println("Can only have two arguments - the flag and input string");
            System.exit(0);
        } else if (args.length == 2 && args[0].equals("-s")) {
            Engine engine = new Engine();
//            engine.interactWithKeyboard();
//            engine = new Engine();
            String [] seeds = new String[] {"n1392967723524655428sddsaawwsaddw","n1392967723524655428sddsaawws:q","laddw"};
            TERenderer ter = new TERenderer();
            ter.initialize(90,45);
            for(String seed:seeds){
                System.out.println(seed);
                TETile[][] out = engine.interactWithInputString(seed);
                ter.renderFrame(out);
                StdDraw.pause(1000);
            }


        } else {
            Engine engine = new Engine();
            engine.interactWithKeyboard();
        }
    }
}


