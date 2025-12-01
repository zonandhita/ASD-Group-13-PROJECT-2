import java.util.*;

public class MazeGenerator {
    private int width;
    private int height;
    private Cell[][] grid;
    private Random random = new Random();

    public MazeGenerator(int width, int height) {
        this.width = width;
        this.height = height;
        this.grid = new Cell[height][width];
        initializeGrid();
    }

    private void initializeGrid() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                grid[y][x] = new Cell(x, y);
            }
        }
    }

    public Cell[][] generateMaze() {
        // Prim's Algorithm
        List<Wall> walls = new ArrayList<>();
        Cell start = grid[0][0];
        start.setVisited(true);

        addWallsToList(start, walls);

        while (!walls.isEmpty()) {
            Wall wall = walls.remove(random.nextInt(walls.size()));
            Cell cell1 = wall.getCell1();
            Cell cell2 = wall.getCell2();

            if (cell1.isVisited() != cell2.isVisited()) {
                removeWallBetween(cell1, cell2);
                cell1.addNeighbor(cell2);
                cell2.addNeighbor(cell1);

                Cell unvisited = cell1.isVisited() ? cell2 : cell1;
                unvisited.setVisited(true);
                addWallsToList(unvisited, walls);
            }
        }

        // Reset visited for pathfinding
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                grid[y][x].setVisited(false);
            }
        }

        return grid;
    }

    private void addWallsToList(Cell cell, List<Wall> walls) {
        int x = cell.getX();
        int y = cell.getY();

        // Top
        if (y > 0 && !grid[y - 1][x].isVisited()) {
            walls.add(new Wall(cell, grid[y - 1][x]));
        }
        // Right
        if (x < width - 1 && !grid[y][x + 1].isVisited()) {
            walls.add(new Wall(cell, grid[y][x + 1]));
        }
        // Bottom
        if (y < height - 1 && !grid[y + 1][x].isVisited()) {
            walls.add(new Wall(cell, grid[y + 1][x]));
        }
        // Left
        if (x > 0 && !grid[y][x - 1].isVisited()) {
            walls.add(new Wall(cell, grid[y][x - 1]));
        }
    }

    private void removeWallBetween(Cell cell1, Cell cell2) {
        int dx = cell2.getX() - cell1.getX();
        int dy = cell2.getY() - cell1.getY();

        if (dx == 1) { // cell2 is to the right
            cell1.setRightWall(false);
            cell2.setLeftWall(false);
        } else if (dx == -1) { // cell2 is to the left
            cell1.setLeftWall(false);
            cell2.setRightWall(false);
        } else if (dy == 1) { // cell2 is below
            cell1.setBottomWall(false);
            cell2.setTopWall(false);
        } else if (dy == -1) { // cell2 is above
            cell1.setTopWall(false);
            cell2.setBottomWall(false);
        }
    }

    private static class Wall {
        private Cell cell1;
        private Cell cell2;

        public Wall(Cell cell1, Cell cell2) {
            this.cell1 = cell1;
            this.cell2 = cell2;
        }

        public Cell getCell1() { return cell1; }
        public Cell getCell2() { return cell2; }
    }
}