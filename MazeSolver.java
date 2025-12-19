import java.util.*;

public class MazeSolver {
    private Cell[][] grid;
    private int rows, cols;

    public MazeSolver(Cell[][] grid) {
        this.grid = grid;
        this.cols = grid.length;
        this.rows = grid[0].length;
    }

    public List<Cell> solveBFS(Cell start, Cell end) {
        resetVisited();
        Queue<Cell> queue = new LinkedList<>();
        Map<Cell, Cell> parentMap = new HashMap<>();

        queue.offer(start);
        start.setVisited(true);
        parentMap.put(start, null);

        while (!queue.isEmpty()) {
            Cell current = queue.poll();

            if (current == end) return reconstructPath(parentMap, end);

            for (Cell neighbor : getValidNeighbors(current)) {
                if (!neighbor.isVisited()) {
                    neighbor.setVisited(true);
                    queue.offer(neighbor);
                    parentMap.put(neighbor, current);
                }
            }
        }
        return new ArrayList<>();
    }

    public List<Cell> solveDFS(Cell start, Cell end) {
        resetVisited();
        Stack<Cell> stack = new Stack<>();
        Map<Cell, Cell> parentMap = new HashMap<>();

        stack.push(start);
        start.setVisited(true);
        parentMap.put(start, null);

        while (!stack.isEmpty()) {
            Cell current = stack.pop();

            if (current == end) return reconstructPath(parentMap, end);

            for (Cell neighbor : getValidNeighbors(current)) {
                if (!neighbor.isVisited()) {
                    neighbor.setVisited(true);
                    stack.push(neighbor);
                    parentMap.put(neighbor, current);
                }
            }
        }
        return new ArrayList<>();
    }

    public List<Cell> solveDijkstra(Cell start, Cell end) {
        resetVisited();
        Map<Cell, Integer> costMap = new HashMap<>();
        Map<Cell, Cell> parentMap = new HashMap<>();

        PriorityQueue<Cell> pq = new PriorityQueue<>(Comparator.comparingInt(c -> costMap.getOrDefault(c, Integer.MAX_VALUE)));

        costMap.put(start, 0);
        pq.offer(start);
        parentMap.put(start, null);

        while (!pq.isEmpty()) {
            Cell current = pq.poll();
            current.setVisited(true);

            if (current == end) return reconstructPath(parentMap, end);

            for (Cell neighbor : getValidNeighbors(current)) {
                if (neighbor.isVisited()) continue;

                int newCost = costMap.get(current) + neighbor.weight;
                if (newCost < costMap.getOrDefault(neighbor, Integer.MAX_VALUE)) {
                    costMap.put(neighbor, newCost);
                    parentMap.put(neighbor, current);
                    pq.remove(neighbor);
                    pq.offer(neighbor);
                }
            }
        }
        return new ArrayList<>();
    }

    public List<Cell> solveAStar(Cell start, Cell end) {
        resetVisited();
        Map<Cell, Integer> gScore = new HashMap<>();
        Map<Cell, Cell> parentMap = new HashMap<>();

        PriorityQueue<Cell> pq = new PriorityQueue<>(Comparator.comparingInt(c -> {
            int g = gScore.getOrDefault(c, Integer.MAX_VALUE);
            int h = Math.abs(c.x - end.x) + Math.abs(c.y - end.y);
            return g + h;
        }));

        gScore.put(start, 0);
        pq.offer(start);
        parentMap.put(start, null);

        while (!pq.isEmpty()) {
            Cell current = pq.poll();
            current.setVisited(true);

            if (current == end) return reconstructPath(parentMap, end);

            for (Cell neighbor : getValidNeighbors(current)) {
                if (neighbor.isVisited()) continue;

                int newG = gScore.get(current) + neighbor.weight;
                if (newG < gScore.getOrDefault(neighbor, Integer.MAX_VALUE)) {
                    gScore.put(neighbor, newG);
                    parentMap.put(neighbor, current);
                    pq.remove(neighbor);
                    pq.offer(neighbor);
                }
            }
        }
        return new ArrayList<>();
    }

    private List<Cell> getValidNeighbors(Cell c) {
        List<Cell> neighbors = new ArrayList<>();
        int x = c.x;
        int y = c.y;

        if (!c.walls[0] && isValid(y - 1, x)) neighbors.add(grid[x][y - 1]);
        if (!c.walls[1] && isValid(y, x + 1)) neighbors.add(grid[x + 1][y]);
        if (!c.walls[2] && isValid(y + 1, x)) neighbors.add(grid[x][y + 1]);
        if (!c.walls[3] && isValid(y, x - 1)) neighbors.add(grid[x - 1][y]);

        return neighbors;
    }

    private boolean isValid(int r, int c) {
        return c >= 0 && c < cols && r >= 0 && r < rows;
    }

    private List<Cell> reconstructPath(Map<Cell, Cell> parentMap, Cell end) {
        List<Cell> path = new ArrayList<>();
        Cell current = end;
        while (current != null) {
            path.add(0, current);
            current = parentMap.get(current);
        }
        return path;
    }

    private void resetVisited() {
        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                grid[i][j].setVisited(false);
            }
        }
    }
}