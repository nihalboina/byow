package byow.Core;

import byow.InputDemo.InputSource;
import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.algs4.StdDraw;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Engine {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    private static final int WIDTH = 90;
    private static final int HEIGHT = 45;
    TETile[][] tiles;
    private static long SEED = 0;
    private static Random RANDOM = new Random(SEED);

    private int[][] allLengths;
    private int[][] allLocsnMinDist;

    private String playerName = "default_player_name";

    private int[] curLoc;
    private List<Integer[]> locBadGuys;

    public Engine() {
        tiles = new TETile[WIDTH][HEIGHT];
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                tiles[x][y] = Tileset.GRAY_LOCKED_DOOR;
            }
        }
    }

    private double getDist(int[] loc1, int[] loc2) {
        int x1 = loc1[0];
        int y1 = loc1[1];
        int x2 = loc2[0];
        int y2 = loc2[1];
        return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }

    /**
     * Make direct route from loc1 to loc2
     */

    private void paintNotRoom(int x, int y) {
        if (!tiles[x][y].equals(Tileset.RoomTile)) {
            tiles[x][y] = Tileset.NOTHING;
        }
    }

    private void makeRoute(int[] loc1, int[] loc2) {
        int width = RANDOM.nextInt(0, 2) + 1;
        for (int x = loc1[0]; x <= loc2[0] + width - 1 && x < WIDTH; x++) {
            paintNotRoom(Math.min(x, WIDTH), loc1[1]);
            if (width > 1) {
                if (loc1[1] + 1 < HEIGHT) {
                    paintNotRoom(Math.min(x, WIDTH), loc1[1] + 1);
                } else {
                    paintNotRoom(Math.min(x, WIDTH), loc1[1] - 1);
                }
            }
        }

        for (int x = loc2[0]; x <= loc1[0] + width - 1 && x < WIDTH; x++) {
            paintNotRoom(Math.min(x, WIDTH), loc1[1]);
            if (width > 1) {
                if (loc1[1] + 1 < HEIGHT) {
                    paintNotRoom(Math.min(x, WIDTH), loc1[1] + 1);
                } else {
                    paintNotRoom(Math.min(x, WIDTH), loc1[1] - 1);
                }
            }
        }

        for (int y = loc1[1]; y < loc2[1] && y < HEIGHT; y++) {
            paintNotRoom(loc2[0], Math.min(y, HEIGHT));
            if (width > 1) {
                if (loc2[0] + 1 < WIDTH) {
                    paintNotRoom(loc2[0] + 1, Math.min(y, HEIGHT));
                } else {
                    paintNotRoom(loc2[0] - 1, Math.min(y, HEIGHT));
                }
            }
        }

        for (int y = loc2[1]; y < loc1[1] && y < HEIGHT; y++) {
            paintNotRoom(loc2[0], Math.min(y, HEIGHT));
            if (width > 1) {
                if (loc2[0] + 1 < WIDTH) {
                    paintNotRoom(loc2[0] + 1, Math.min(y, HEIGHT));
                } else {
                    paintNotRoom(loc2[0] - 1, Math.min(y, HEIGHT));
                }
            }
        }
    }

    /**
     * x = x param of center of room
     * y = y param of center of room
     * dist = minimum distance separating two rooms
     * <p>
     * Given everything above, should paint a Room with total random width of 2*dist/3 and height of 2*dist/3
     */
    private boolean checkSurrounding(int x, int y) {
        if (x + 1 < WIDTH) {
            if (tiles[x + 1][y] == Tileset.RoomTile || tiles[x + 1][y] == Tileset.NOTHING) {
                return true;
            }
        }
        if (x - 1 >= 0) {
            if (tiles[x - 1][y] == Tileset.RoomTile || tiles[x - 1][y] == Tileset.NOTHING) {
                return true;
            }
        }
        if (y + 1 < HEIGHT) {
            if (tiles[x][y + 1] == Tileset.RoomTile || tiles[x][y + 1] == Tileset.NOTHING) {
                return true;
            }
        }
        if (y - 1 >= 0) {
            return tiles[x][y - 1] == Tileset.RoomTile || tiles[x][y - 1] == Tileset.NOTHING;
        }
        return false;

    }

    private void waller() {
        boolean in = false;
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                if (tiles[x][y] == Tileset.GRAY_LOCKED_DOOR && (checkSurrounding(x, y))) {
                    tiles[x][y] = Tileset.WALL;
                }
                if (!in && tiles[x][y] == Tileset.RoomTile) {
                    tiles[x][y] = Tileset.person;
                    curLoc = new int[]{x, y};
                    in = true;
                }
            }
        }
    }

    private int[] paintRoom(int[] loc) {
        int x = loc[0];
        int y = loc[1];
        int dist = loc[2];
        int width1 = RANDOM.nextInt(2, dist / 2);
        int width2 = RANDOM.nextInt(2, dist / 2);
        int height1 = RANDOM.nextInt(0, 1) + 1 + 1 + 1;
        int height2 = RANDOM.nextInt(0, 1) + 1 + 1 + 1;
        for (int xPlus = 0; xPlus < width1 && xPlus + x < WIDTH; xPlus++) {
            for (int yPlus = 0; yPlus < height1 && yPlus + y < HEIGHT; yPlus++) {
                if (xPlus == width1 - 1 || xPlus + x == WIDTH - 1 || yPlus == height1 - 1 || yPlus + y == HEIGHT - 1) {
                    tiles[x + xPlus][y + yPlus] = Tileset.RoomTile;
                } else {
                    tiles[x + xPlus][y + yPlus] = Tileset.RoomTile;
                }
            }
            for (int yMinus = height2; yMinus >= 0 && y - yMinus >= 0; yMinus--) {
                if (xPlus == width1 - 1 || xPlus + x == WIDTH - 1 || yMinus == height2) {
                    tiles[x + xPlus][y - yMinus] = Tileset.RoomTile;
                } else {
                    tiles[x + xPlus][y - yMinus] = Tileset.RoomTile;
                }
            }
        }
        for (int xMinus = width2; xMinus >= 0 && x - xMinus >= 0; xMinus--) {
            for (int yPlus = 0; yPlus < height1 && yPlus + y < HEIGHT; yPlus++) {
                if (xMinus == width2 || yPlus == height1 - 1 || yPlus + y == HEIGHT - 1 || y == 0) {
                    tiles[x - xMinus][y + yPlus] = Tileset.RoomTile;
                } else {
                    tiles[x - xMinus][y + yPlus] = Tileset.RoomTile;
                }
            }
            for (int yMinus = height2; yMinus >= 0 && y - yMinus >= 0; yMinus--) {
                if (xMinus == width2 || yMinus == height2) {
                    tiles[x - xMinus][y - yMinus] = Tileset.RoomTile;
                } else {
                    tiles[x - xMinus][y - yMinus] = Tileset.RoomTile;
                }
            }
        }

        return new int[]{width1, width2, height1, height2};
    }

    public TETile[][] hide() {
        TETile[][] newTiles = new TETile[WIDTH][HEIGHT];
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                if (getDist(new int[]{x, y}, curLoc) < 5) {
                    newTiles[x][y] = tiles[x][y];
                } else {
                    newTiles[x][y] = Tileset.GRAY_LOCKED_DOOR;
                }
            }
        }
        return newTiles;
    }

    public int[][] allRoomLocs() {
        int numberRooms = RANDOM.nextInt((int) Math.sqrt(WIDTH)) + 1;
        while (numberRooms < 2 + 1) {
            numberRooms = RANDOM.nextInt((int) Math.sqrt(WIDTH));
        }
        int[][] allLocs = new int[numberRooms][1 + 1 + 1];
        int i = 0;

        while (i < numberRooms) {
            //check if not alr in, and difference of at least 4 awa
            // y
            int[] newLoc = {RANDOM.nextInt(WIDTH), RANDOM.nextInt(HEIGHT)};
            boolean dif = true;

            double minDist = WIDTH * HEIGHT;
            int minLoc = -1;
            for (int ii = 0; ii < i; ii++) {
                double dist = getDist(allLocs[ii], newLoc);
                if (dist < (int) Math.sqrt(WIDTH)) {
                    dif = false;
                } else {
                    if (dist < minDist) {
                        minDist = dist;
                        minLoc = ii;
                    }
                }
            }
            if (dif) {
                allLocs[i] = new int[]{newLoc[0], newLoc[1], (int) (minDist + 0.5)};
                if (!(minLoc == -1) && minDist < allLocs[minLoc][2]) {
                    allLocs[minLoc] = new int[]{allLocs[minLoc][0], allLocs[minLoc][1], (int) (minDist + 0.5)};
                }
                tiles[newLoc[0]][newLoc[1]] = Tileset.NOTHING;

                i += 1;
            }
        }
        for (i = 0; i < numberRooms; i++) {
            int[] loc = allLocs[i];
            paintRoom(loc);
            if (i < numberRooms - 1) {
                makeRoute(allLocs[i], allLocs[i + 1]);
            }
        }
        this.allLocsnMinDist = allLocs;

        return allLocs;
    }

    public Integer[] randomMove(Integer[] loc) {
        boolean[] dirs = new boolean[]{false, false, false, false}; //posX, negX, posY, negY
        int count = 0;
        if (loc[0] < (WIDTH - 1)) {
            if ((tiles[loc[0] + 1][loc[1]] == Tileset.RoomTile || tiles[loc[0] + 1][loc[1]] == Tileset.NOTHING)) {
                dirs[0] = true;
                count += 1;
            }
        }
        if (loc[0] > 0) {
            if (tiles[loc[0] - 1][loc[1]] == Tileset.RoomTile || tiles[loc[0] - 1][loc[1]] == Tileset.NOTHING) {
                dirs[1] = true;
                count += 1;
            }
        }

        if (loc[1] < (HEIGHT - 1)) {
            if (tiles[loc[0]][loc[1] + 1] == Tileset.RoomTile || tiles[loc[0]][loc[1] + 1] == Tileset.NOTHING) {
                dirs[2] = true;
                count += 1;
            }
        }
        if (loc[1] > 0) {
            if (tiles[loc[0]][loc[1] - 1] == Tileset.RoomTile || tiles[loc[0]][loc[1] - 1] == Tileset.NOTHING) {
                dirs[3] = true;
                count += 1;
            }
        }
        if (count > 0) {
            int randomDir = RANDOM.nextInt(1, count + 1);
            int chosenInd = 0;
            int seen = 0;
            int i = 0;
            while (i < 4) {
                if (dirs[i]) {
                    seen += 1;
                }
                if (seen == randomDir) {
                    chosenInd = i;
                    i = 5;
                }
                i += 1;
            }
            tiles[loc[0]][loc[1]] = Tileset.RoomTile;
            if (chosenInd == 0) {
                tiles[loc[0] + 1][loc[1]] = Tileset.badGuys;
                return (new Integer[]{loc[0] + 1, loc[1]});
            }
            if (chosenInd == 1) {
                tiles[loc[0] - 1][loc[1]] = Tileset.badGuys;
                return (new Integer[]{loc[0] - 1, loc[1]});
            }
            if (chosenInd == 2) {
                tiles[loc[0]][loc[1] + 1] = Tileset.badGuys;
                return (new Integer[]{loc[0], loc[1] + 1});
            }
            if (chosenInd == 3) {
                tiles[loc[0]][loc[1] - 1] = Tileset.badGuys;
                return (new Integer[]{loc[0], loc[1] - 1});
            }
        } else {
            System.out.println("WTf");
        }
        return new Integer[0];
    }

    public void moveBadGuys() {
        for (int i = 0; i < locBadGuys.size(); i++) {
            Integer[] loc = locBadGuys.get(i);
            locBadGuys.set(i, randomMove(loc));
        }

    }

    public void move(char dir) {
        List<TETile> inThis = Arrays.asList(Tileset.RoomTile, Tileset.NOTHING);
        if (dir == 'W' && curLoc[1] < HEIGHT - 1) {
            if (inThis.contains(tiles[curLoc[0]][curLoc[1] + 1])) {
                tiles[curLoc[0]][curLoc[1] + 1] = Tileset.person;
                tiles[curLoc[0]][curLoc[1]] = Tileset.RoomTile;
                curLoc = new int[]{curLoc[0], curLoc[1] + 1};
            }
        }
        if (dir == 'S' && curLoc[1] > 0) {
            if (inThis.contains(tiles[curLoc[0]][curLoc[1] - 1])) {
                tiles[curLoc[0]][curLoc[1] - 1] = Tileset.person;
                tiles[curLoc[0]][curLoc[1]] = Tileset.RoomTile;
                curLoc = new int[]{curLoc[0], curLoc[1] - 1};
            }
        }
        if (dir == 'A' && curLoc[0] > 0) {
            if (inThis.contains(tiles[curLoc[0] - 1][curLoc[1]])) {
                tiles[curLoc[0] - 1][curLoc[1]] = Tileset.person;
                tiles[curLoc[0]][curLoc[1]] = Tileset.RoomTile;
                curLoc = new int[]{curLoc[0] - 1, curLoc[1]};
            }
        }
        if (dir == 'D' && curLoc[0] < WIDTH - 1) {
            if (inThis.contains(tiles[curLoc[0] + 1][curLoc[1]])) {
                tiles[curLoc[0] + 1][curLoc[1]] = Tileset.person;
                tiles[curLoc[0]][curLoc[1]] = Tileset.RoomTile;
                curLoc = new int[]{curLoc[0] + 1, curLoc[1]};
            }
        }
        int times = RANDOM.nextInt(1, 5);
        for (int i = 0; i < times; i++) {
            moveBadGuys();
        }
    }

    public void placeBadGuys() {
        List<Integer[]> badlocs = new ArrayList<>();
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                int place = RANDOM.nextInt(1, 9 * 9);
                if (place == 1 && (tiles[x][y] == Tileset.RoomTile || tiles[x][y] == Tileset.NOTHING)) {
                    tiles[x][y] = Tileset.badGuys;
                    badlocs.add(new Integer[]{x, y});
                }
            }
        }
        this.locBadGuys = badlocs;
    }

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        InputSource inputSource;
        inputSource = new KeyboardInputSource2(playerName);
        String everything = "";
        boolean newGame = false;
        TERenderer ter = null;
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                everything += inputSource.getNextKey();
                if (everything.charAt(everything.length() - 1) == 'L') {
                    interactWithInputString(everything);
                    newGame = true;
                    TETile[][] tiles = interactWithInputString(everything);
                    ter = new TERenderer();
                    ter.initialize(WIDTH, HEIGHT);
                    ter.renderFrame(tiles);
                    StdDraw.setPenColor(Color.GRAY);
                    StdDraw.filledRectangle(0, HEIGHT, WIDTH / 10, HEIGHT / 10);
                    StdDraw.show();
                }
                if (everything.charAt(everything.length() - 1) == 'N') {
                    StdDraw.clear(Color.BLACK);
                    StdDraw.text(0.5, 0.9, "Enter Seed");
                    while (everything.charAt(everything.length() - 1) != 'S') {
                        everything += inputSource.getNextKey();
                        StdDraw.clear(Color.BLACK);
                        StdDraw.text(0.5, 0.9, "Enter Seed");
                        StdDraw.text(0.5, 0.7, everything.substring(1));
                    }
                }
                if (everything.charAt(everything.length() - 1) == 'C' && !newGame) {
                    StdDraw.clear(Color.BLACK);
                    StdDraw.text(0.5, 0.9, "Enter New Name, click '-' when done");
                    String name = "";
                    char nxt = inputSource.getNextKey();
                    while (nxt != '-') {
                        name += nxt;
                        StdDraw.clear(Color.BLACK);
                        StdDraw.text(0.5, 0.9, "Enter New Name, click '-' when done");
                        StdDraw.text(0.5, 0.7, name);
                        nxt = inputSource.getNextKey();
                    }
                    everything = "";
                    playerName = name;
                    inputSource = new KeyboardInputSource2(playerName);
                } else if (everything.charAt(0) == 'N' && everything.charAt(everything.length() - 1) == 'S' && !newGame) {
                    newGame = true;
                    TETile[][] tiles2 = interactWithInputString(everything);
                    ter = new TERenderer();
                    ter.initialize(WIDTH, HEIGHT);
                    ter.renderFrame(tiles2);
                    StdDraw.setPenColor(Color.GRAY);
                    StdDraw.filledRectangle(0, HEIGHT, WIDTH / 10, HEIGHT / 10);
                    StdDraw.show();
                } else if (newGame) {
                    if (everything.charAt(everything.length() - 1) == 'I') {
                        everything = everything.substring(0, everything.length() - 1);
                        ter.renderFrame(tiles);
                        System.out.println("Toggle on!");
                        StdDraw.setPenColor(Color.GRAY);
                        StdDraw.filledRectangle(0, HEIGHT, WIDTH / 4, HEIGHT / 9);
                        StdDraw.setPenColor(Color.CYAN);
                        StdDraw.text(9, HEIGHT - 0.6, "   Hiding: off (left click for toggle)");
                        StdDraw.show();
                    } else {
                        if (everything.charAt(everything.length() - 1) == 'Y') {
                            everything = everything.substring(0, everything.length() - 1);
                        }
                        if (everything.charAt(everything.length() - 1) == 'Q') {
                            interactWithInputString(everything);
                            System.exit(0);
                        } else {
                            StdDraw.clear(Color.BLACK);
                            tiles = interactWithInputString(everything);
                            TETile[][] hiddentiles = hide();
                            ter.renderFrame(hiddentiles);
                            System.out.println(StdDraw.mouseX() + ", " + StdDraw.mouseY());
                            StdDraw.setPenColor(Color.GRAY);
                            StdDraw.filledRectangle(0, HEIGHT, WIDTH / 4, HEIGHT / 9);
                            StdDraw.setPenColor(Color.CYAN);
                            StdDraw.text(9, HEIGHT - 0.6, "   Hiding: on (left click for toggle");
                            StdDraw.show();
                        }

                    }
                }
            } else if (newGame) {
                int[] loc = new int[]{(int) (StdDraw.mouseX()), (int) (StdDraw.mouseY())};
                StdDraw.setPenColor(Color.GRAY);
                StdDraw.filledRectangle(0, HEIGHT, WIDTH / 4, HEIGHT / 9);
                StdDraw.setPenColor(Color.CYAN);
                StdDraw.text(9, HEIGHT - 0.6, "    Hiding: on (left click for toggle)");
                StdDraw.text(4, HEIGHT - 1.8, tiles[loc[0]][loc[1]].description());
                StdDraw.setPenColor(Color.RED);
                StdDraw.text(9, HEIGHT - 3.0, "   " + playerName);
                StdDraw.show();
                while (StdDraw.isMousePressed()) {
                    System.out.println("Toggle on!");
                    StdDraw.clear(Color.BLACK);
                    tiles = interactWithInputString(everything);
                    ter.renderFrame(tiles);
                    StdDraw.setPenColor(Color.GRAY);
                    StdDraw.filledRectangle(0, HEIGHT, WIDTH / 4, HEIGHT / 9);
                    StdDraw.setPenColor(Color.CYAN);
                    StdDraw.text(9, HEIGHT - 0.6, "    Hiding: off (left click for toggle)");
                    StdDraw.text(4, HEIGHT - 1.8, tiles[loc[0]][loc[1]].description());
                    StdDraw.show();
                }
                StdDraw.clear(Color.BLACK);
                TETile[][] hiddentiles = hide();
                ter.renderFrame(hiddentiles);
                StdDraw.setPenColor(Color.GRAY);
                StdDraw.filledRectangle(0, HEIGHT, WIDTH / 4, HEIGHT / 9);
                StdDraw.setPenColor(Color.CYAN);
                StdDraw.text(9, HEIGHT - 0.6, "    Hiding: on (left click for toggle)");
                StdDraw.text(4, HEIGHT - 1.8, tiles[loc[0]][loc[1]].description());
                StdDraw.setPenColor(Color.RED);
                StdDraw.text(9, HEIGHT - 3.0, "   " + playerName);
                StdDraw.show();
            }
            StdDraw.pause(10);
        }
    }


    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     * <p>
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     * <p>
     * In other words, running both of these:
     * - interactWithInputString("n123sss:q")
     * - interactWithInputString("lww")
     * <p>
     * should yield the exact same world state as:
     * - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        input = input.toUpperCase();
        if (input.contains(":Q")) {
            System.out.println("1");
            try {
                System.out.println("2");
                File file = new File("mem.txt");
                System.out.println("3");
                FileWriter myWriter = new FileWriter(file.getAbsolutePath());
                System.out.println("4");
                myWriter.write(input.replace(":Q", ""));
                System.out.println("5");
                myWriter.close();
            } catch (IOException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }
            interactWithInputString(input.replace(":Q", ""));
            return tiles;
        }

        if (input.length() == 0) {
            return tiles;
        }
        if (Character.toUpperCase(input.charAt(0)) == 'L') {
            try {
                File file = new File("mem.txt");
                BufferedReader br = new BufferedReader(new FileReader(file.getAbsolutePath()));
                String inp = br.readLine();
                interactWithInputString(inp + input.substring(1));
            } catch (IOException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }
        }
        if (Character.toUpperCase(input.charAt(0)) == 'N') {
            boolean in = false;
            int end = 0;
            while (!in) {
                end += 1;
                if (Character.toUpperCase(input.charAt(end)) == 'S') {
                    in = true;
                }
            }
            long seed = Long.valueOf(input.substring(1, end));
            SEED = seed;
            RANDOM = new Random(SEED);
            tiles = new TETile[WIDTH][HEIGHT];
            for (int y = 0; y < HEIGHT; y++) {
                for (int x = 0; x < WIDTH; x++) {
                    tiles[x][y] = Tileset.GRAY_LOCKED_DOOR;
                }
            }
            allRoomLocs();
            waller();
            placeBadGuys();
            interactWithInputString(input.substring(end + 1));
        }
        List<Character> allDirs = Arrays.asList('A', 'S', 'D', 'W');
        if (allDirs.contains(input.charAt(0))) {
            move(input.charAt(0));
            interactWithInputString(input.substring(1));
        }
        return tiles;
    }
}
