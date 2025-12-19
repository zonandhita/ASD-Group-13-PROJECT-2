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
        // Inisialisasi grid dengan objek Cell baru di setiap koordinat
        grid = new Cell[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                grid[x][y] = new Cell(x, y);
            }
        }

        // Algoritma DFS (Depth-First Search) menggunakan Stack untuk menelusuri labirin
        Stack<Cell> stack = new Stack<>();
        Cell current = grid[0][0]; // Titik awal pembuatan labirin
        current.setVisited(true);
        stack.push(current);

        while (!stack.isEmpty()) {
            current = stack.peek();
            Cell next = getUnvisitedNeighbor(current);

            if (next != null) {
                // Jika ditemukan tetangga yang belum pernah dikunjungi
                next.setVisited(true);
                removeWalls(current, next); // Hancurkan tembok pembatas
                stack.push(next);
            } else {
                // Jika buntu (tidak ada tetangga baru), kembali ke sel sebelumnya (backtrack)
                stack.pop();
            }
        }

        // Menciptakan variasi jalur agar labirin tidak hanya memiliki satu solusi tunggal
        addLoops(10);

        // Bersihkan status visited agar grid bisa digunakan ulang oleh algoritma pencari jalur
        resetVisitedStatus();
        return grid;
    }

    /**
     * Membuka beberapa tembok secara acak.
     * Tujuannya agar labirin memiliki 'siklus' (rute ganda), bukan sekadar pohon tunggal.
     */
    private void addLoops(int percentage) {
        int totalCells = width * height;
        int wallsToRemove = (totalCells * percentage) / 100;

        for (int i = 0; i < wallsToRemove; i++) {
            int x = random.nextInt(width - 1);
            int y = random.nextInt(height - 1);

            if (random.nextBoolean()) {
                // Hancurkan koneksi horizontal antara sel saat ini dan sel di kanannya
                grid[x][y].walls[1] = false;
                grid[x+1][y].walls[3] = false;
            } else {
                // Hancurkan koneksi vertikal antara sel saat ini dan sel di bawahnya
                grid[x][y].walls[2] = false;
                grid[x][y+1].walls[0] = false;
            }
        }
    }

    /**
     * Mencari tetangga (atas, kanan, bawah, kiri) yang belum diproses oleh generator.
     */
    private Cell getUnvisitedNeighbor(Cell cell) {
        List<Cell> neighbors = new ArrayList<>();
        int x = cell.x;
        int y = cell.y;

        if (y > 0 && !grid[x][y - 1].isVisited()) neighbors.add(grid[x][y - 1]);
        if (x < width - 1 && !grid[x + 1][y].isVisited()) neighbors.add(grid[x + 1][y]);
        if (y < height - 1 && !grid[x][y + 1].isVisited()) neighbors.add(grid[x][y + 1]);
        if (x > 0 && !grid[x - 1][y].isVisited()) neighbors.add(grid[x - 1][y]);

        if (neighbors.size() > 0) {
            return neighbors.get(random.nextInt(neighbors.size()));
        }
        return null;
    }

    /**
     * Logika untuk menentukan tembok mana yang harus dijatuhkan berdasarkan
     * posisi relatif antara dua sel yang bertetangga.
     */
    private void removeWalls(Cell a, Cell b) {
        int xDiff = a.x - b.x;
        int yDiff = a.y - b.y;

        if (xDiff == 1) {
            a.walls[3] = false; b.walls[1] = false; // A di sebelah kanan B
        } else if (xDiff == -1) {
            a.walls[1] = false; b.walls[3] = false; // A di sebelah kiri B
        }

        if (yDiff == 1) {
            a.walls[0] = false; b.walls[2] = false; // A di bawah B
        } else if (yDiff == -1) {
            a.walls[2] = false; b.walls[0] = false; // A di atas B
        }
    }

    private void resetVisitedStatus() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                grid[x][y].setVisited(false);
            }
        }
    }
}