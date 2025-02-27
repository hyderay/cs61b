package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.ArrayList;
import java.util.List;

public class ConnectRooms {
    /**
     * Creates a spinning tree connecting all rooms with hallways.
     *
     * @param world
     * @param rooms         A list contains all rooms.
     * */
    public static void connectRooms(TETile[][] world, List<Room> rooms) {
        if (rooms.size() <= 1) {
            return;
        }

        List<Edge> edges = new ArrayList<>();
        for (int i = 0; i < rooms.size() - 1; i++) {
            for (int j = i + 1; j < rooms.size(); j++) {
                double weight = getDistance(rooms.get(i), rooms.get(j));
                edges.add(new Edge(weight, i, j));
            }
        }

        QuickSort.quickSort(edges, 0, edges.size() - 1);

        UnionFind uf = new UnionFind(rooms.size());
        int edgeUsed = 0;
        int edgeNeeded = rooms.size() - 1;

        for (Edge edge : edges) {
            int r1 = edge.getRoom1();
            int r2 = edge.getRoom2();
            if (uf.find(r1) != uf.find(r2)) {
                uf.union(r1, r2);
                generateHallway(world, rooms.get(r1), rooms.get(r2));
                edgeUsed++;
            }
            if (edgeUsed == edgeNeeded) {
                break;
            }
        }
    }

    /**
     * Calculate the distance from the center of room1 to room2.
     * */
    private static double getDistance(Room room1, Room room2) {
        double result;
        double xDis = room1.getCenterX() - room2.getCenterX();
        double yDis = room1.getCenterY() - room2.getCenterY();
        result = Math.pow(xDis * xDis + yDis * yDis, 0.5);
        return result;
    }

    /**
     * Generate hallway.
     * */
    private static void generateHallway(TETile[][] world, Room r1, Room r2) {
        int x1 = r1.getCenterX();
        int y1 = r1.getCenterY();
        int x2 = r2.getCenterX();
        int y2 = r2.getCenterY();

        if (x1 < x2) {
            if (y1 < y2) {
                curveHallway1(world, x1, x2, y1, y2);
            } else {
                curveHallway2(world, x1, x2, y1, y2);
            }
        } else {
            if (y1 < y2) {
                curveHallway3(world, x1, x2, y1, y2);
            } else {
                curveHallway4(world, x1, x2, y1, y2);
            }
        }
    }

    private static void curveHallway1(TETile[][] world,
                                     int x1, int x2,
                                     int y1, int y2) {
        for (int i = x1; i < x2; i++) {
            world[i][y1] = Tileset.FLOOR;
            putWallsAroundHallway(world, i, y1);
        }
        for (int j = y1; j < y2; j++) {
            world[x2][j] = Tileset.FLOOR;
            putWallsAroundHallway(world, x2, j);
        }
        if (world[x2 + 1][y1 - 1].equals(Tileset.NOTHING)) {
            world[x2 + 1][y1 - 1] = Tileset.WALL;
        }
    }

    private static void curveHallway2(TETile[][] world,
                                      int x1, int x2,
                                      int y1, int y2) {
        for (int i = x1; i < x2; i++) {
            world[i][y2] = Tileset.FLOOR;
            putWallsAroundHallway(world, i, y2);
        }
        for (int j = y2; j < y1; j++) {
            world[x1][j] = Tileset.FLOOR;
            putWallsAroundHallway(world, x1, j);
        }
        if (world[x1 - 1][y2 - 1].equals(Tileset.NOTHING)) {
            world[x1 - 1][y2 - 1] = Tileset.WALL;
        }
    }

    private static void curveHallway3(TETile[][] world,
                                      int x1, int x2,
                                      int y1, int y2) {
        curveHallway2(world, x2, x1, y2, y1);
    }

    private static void curveHallway4(TETile[][] world,
                                      int x1, int x2,
                                      int y1, int y2) {
        curveHallway1(world, x2, x1, y2, y1);
    }

    private static void putWallsAroundHallway(TETile[][] world,
                                              int x, int y) {
        if (world[x - 1][y].equals(Tileset.NOTHING)) {
            world[x - 1][y] = Tileset.WALL;
        }
        if (world[x + 1][y].equals(Tileset.NOTHING)) {
            world[x + 1][y] = Tileset.WALL;
        }
        if (world[x][y - 1].equals(Tileset.NOTHING)) {
            world[x][y - 1] = Tileset.WALL;
        }
        if (world[x][y + 1].equals(Tileset.NOTHING)) {
            world[x][y + 1] = Tileset.WALL;
        }
    }

    private static boolean inBounds(TETile[][] world, int x, int y) {
        return x >= 0 && x < world.length && y >= 0 && y < world[0].length;
    }
}
