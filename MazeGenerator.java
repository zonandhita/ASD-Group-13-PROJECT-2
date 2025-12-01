import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

public class MazeGenerator {
    private int width;
    private int height;
    private Cell[][] grid;
    private Random random = new Random();

    public MazeGenerator(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public Cell[][] generateMaze() {
        // ... (Kode Inisialisasi & DFS Backtracker yang lama TETAP SAMA di sini) ...
        // Copy paste bagian generateMaze() dari jawaban sebelumnya sampai loop Stack selesai

        grid = new Cell[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                grid[x][y] = new Cell(x, y);
            }
        }

        Stack<Cell> stack = new Stack<>();
        Cell current = grid[0][0];
        current.setVisited(true);
        stack.push(current);

        while (!stack.isEmpty()) {
            current = stack.peek();
            Cell next = getUnvisitedNeighbor(current);
            if (next != null) {
                next.setVisited(true);
                removeWalls(current, next);
                stack.push(next);
            } else {
                stack.pop();
            }
        }

        // --- TAMBAHAN BARU: MEMBUAT LOOP (Rute Alternatif) ---
        addLoops(10); // Hapus 10% tembok sisa agar ada banyak jalan

        // Reset visited agar siap untuk searching
        resetVisitedStatus();
        return grid;
    }

    // Method Baru: Menghancurkan tembok acak untuk membuat rute ganda
    private void addLoops(int percentage) {
        int totalCells = width * height;
        int wallsToRemove = (totalCells * percentage) / 100;

        for (int i = 0; i < wallsToRemove; i++) {
            int x = random.nextInt(width - 1); // Hindari pinggir paling kanan
            int y = random.nextInt(height - 1); // Hindari pinggir paling bawah

            // Cek tembok kanan atau bawah, lalu hancurkan
            if (random.nextBoolean()) {
                // Hancurkan tembok kanan antara (x,y) dan (x+1,y)
                grid[x][y].walls[1] = false;
                grid[x+1][y].walls[3] = false;
            } else {
                // Hancurkan tembok bawah antara (x,y) dan (x,y+1)
                grid[x][y].walls[2] = false;
                grid[x][y+1].walls[0] = false;
            }
        }
    }

    // ... (Sisa method getUnvisitedNeighbor, removeWalls, resetVisitedStatus TETAP SAMA) ...

    private Cell getUnvisitedNeighbor(Cell cell) {
        List<Cell> neighbors = new ArrayList<>();
        int x = cell.x;
        int y = cell.y;
        if (y > 0 && !grid[x][y - 1].isVisited()) neighbors.add(grid[x][y - 1]);
        if (x < width - 1 && !grid[x + 1][y].isVisited()) neighbors.add(grid[x + 1][y]);
        if (y < height - 1 && !grid[x][y + 1].isVisited()) neighbors.add(grid[x][y + 1]);
        if (x > 0 && !grid[x - 1][y].isVisited()) neighbors.add(grid[x - 1][y]);
        if (neighbors.size() > 0) return neighbors.get(random.nextInt(neighbors.size()));
        else return null;
    }

    private void removeWalls(Cell a, Cell b) {
        int x = a.x - b.x;
        int y = a.y - b.y;
        if (x == 1) { a.walls[3] = false; b.walls[1] = false; }
        else if (x == -1) { a.walls[1] = false; b.walls[3] = false; }
        if (y == 1) { a.walls[0] = false; b.walls[2] = false; }
        else if (y == -1) { a.walls[2] = false; b.walls[0] = false; }
    }

    private void resetVisitedStatus() {
        for (int x = 0; x < width; x++) for (int y = 0; y < height; y++) grid[x][y].setVisited(false);
    }
}