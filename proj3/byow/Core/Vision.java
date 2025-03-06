package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.LinkedList;
import java.util.Queue;

public class Vision {
    private static boolean visionEnable = false;
    private static final int VISION_RADIUS = 6;

    public static void switchVision() {
        visionEnable = !visionEnable;
    }

    public static boolean switchStatus() {
        return visionEnable;
    }

    /**
     * Create a world with vision enabled.
     *
     * @param world     Original world
     * @param x         Player's x coordinate
     * @param y         Player's y coordinate
     * @return          A world with vision applied
     */
    public static TETile[][] applyVision(TETile[][] world, int x, int y) {
        int width = world.length;;
        int height = world[0].length;

        boolean[][] isVisited = new boolean[width][height];
        Queue<int[]> queue = new LinkedList<>();
        queue.offer(new int[]{x, y, 0});
        isVisited[x][y] = true;

        while (!queue.isEmpty()) {
            int[] curr = queue.poll();
            int cx = curr[0];
            int cy = curr[1];
            int dis = curr[2];

            for (int[] dir : new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}}) {
                int nx = cx + dir[0];
                int ny = cy + dir[1];
                int ndis = dis++;

                if (!isVisited[nx][ny]) {
                    isVisited[nx][ny] = true;

                    if (ndis < VISION_RADIUS && !world[nx][ny].equals(Tileset.WALL)) {
                        queue.offer(new int[]{nx, ny, ndis});
                    }
                }
            }
        }

        TETile[][] maskedWorld = new TETile[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (isVisited[i][j]) {
                    maskedWorld[i][j] = world[i][j];
                } else {
                    maskedWorld[i][j] = Tileset.NOTHING;
                }
            }
        }
        return maskedWorld;
    }
}
