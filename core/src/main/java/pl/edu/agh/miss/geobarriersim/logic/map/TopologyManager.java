package pl.edu.agh.miss.geobarriersim.logic.map;

import pl.edu.agh.miss.geobarriersim.logic.map.element.Vector2d;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class TopologyManager {

    static final int[][] DIRECTIONS = {
        {-1, -1}, {-1, 0}, {-1, 1},
        {0, -1},          {0, 1},
        {1, -1},  {1, 0},  {1, 1}
    };


    private TopologyManager() {}

    public static List<List<Vector2d>> getAreas(WorldMap worldMap) {
        int[][] map = worldMap.getTopology();
        int rows = map.length;
        int cols = map[0].length;
        boolean[][] visited = new boolean[rows][cols];
        List<List<Vector2d>> result = new ArrayList<>();

        for (int x = 0; x < rows; x++) {
            for (int y = 0; y < cols; y++) {
                if (map[x][y] == 0 && !visited[x][y]) {
                    result.add(bfs(map, visited, x, y));
                }
            }
        }
        return result;
    }


    private static List<Vector2d> bfs(int[][] map, boolean[][] visited, int startX, int startY) {
        int rows = map.length;
        int cols = map[0].length;
        List<Vector2d> area = new ArrayList<>();
        Queue<Vector2d> queue = new LinkedList<>();

        queue.offer(new Vector2d(startX, startY));
        visited[startX][startY] = true;

        while (!queue.isEmpty()) {
            Vector2d point = queue.poll();
            area.add(point);

            for (int[] dir : DIRECTIONS) {
                int newX = point.x();
                int newY = point.y();
                newX += dir[0];
                newY += dir[1];

                if (newX >= 0 && newX < rows && newY >= 0 && newY < cols &&
                    map[newX][newY] == 0 && !visited[newX][newY]) {
                    visited[newX][newY] = true;
                    queue.offer(new Vector2d(newX, newY));
                }
            }
        }

        return area;
    }
}
