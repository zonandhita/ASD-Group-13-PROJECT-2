import java.util.*;

public class MazeSolver {
    private Cell[][] grid;
    private int width;
    private int height;

    public MazeSolver(Cell[][] grid) {
        this.grid = grid;
        this.height = grid.length;
        this.width = grid[0].length;
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

            if (current == end) {
                return reconstructPath(parentMap, end);
            }

            for (Cell neighbor : current.getNeighbors()) {
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

            if (current == end) {
                return reconstructPath(parentMap, end);
            }

            for (Cell neighbor : current.getNeighbors()) {
                if (!neighbor.isVisited()) {
                    neighbor.setVisited(true);
                    stack.push(neighbor);
                    parentMap.put(neighbor, current);
                }
            }
        }

        return new ArrayList<>();
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
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                grid[y][x].setVisited(false);
            }
        }
    }
}