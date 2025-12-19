import java.util.*;

public class MazeSolver {
    private Cell[][] grid;
    private int rows, cols;

    public MazeSolver(Cell[][] grid) {
        this.grid = grid;
        // Penyesuaian dimensi: grid.length biasanya adalah lebar (kolom),
        // sedangkan grid[0].length adalah tinggi (baris).
        this.cols = grid.length;
        this.rows = grid[0].length;
    }

    /**
     * BFS (Breadth-First Search): Mencari jalur terpendek pada graf tanpa bobot.
     * Menggunakan Queue (FIFO) untuk mengeksplorasi tetangga secara merata ke segala arah.
     */
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

    /**
     * DFS (Depth-First Search): Menelusuri satu jalur sedalam mungkin sebelum berbalik arah (backtrack).
     * Menggunakan Stack (LIFO). Jalur yang dihasilkan seringkali bukan yang terpendek.
     */
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

    /**
     * Dijkstra: Mencari jalur dengan total bobot (cost) terkecil.
     * Sangat efektif jika labirin memiliki medan yang berbeda (seperti air atau lumpur).
     */
    public List<Cell> solveDijkstra(Cell start, Cell end) {
        resetVisited();
        Map<Cell, Integer> costMap = new HashMap<>();
        Map<Cell, Cell> parentMap = new HashMap<>();

        // PriorityQueue memastikan kita selalu memproses sel dengan akumulasi bobot terendah dahulu
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
                    // Update posisi di PriorityQueue
                    pq.remove(neighbor);
                    pq.offer(neighbor);
                }
            }
        }
        return new ArrayList<>();
    }

    /**
     * A* (A-Star): Versi optimasi dari Dijkstra yang menggunakan Heuristik (jarak Manhattan).
     * Algoritma ini "cerdas" karena memprioritaskan sel yang posisinya secara geografis mendekati target.
     */
    public List<Cell> solveAStar(Cell start, Cell end) {
        resetVisited();
        Map<Cell, Integer> gScore = new HashMap<>(); // Jarak dari start ke sel saat ini
        Map<Cell, Cell> parentMap = new HashMap<>();

        PriorityQueue<Cell> pq = new PriorityQueue<>(Comparator.comparingInt(c -> {
            int g = gScore.getOrDefault(c, Integer.MAX_VALUE);
            // Heuristik: Estimasi jarak sisa menggunakan rumus Manhattan (x + y)
            int h = Math.abs(c.x - end.x) + Math.abs(c.y - end.y);
            return g + h; // Nilai f = g + h
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

    /**
     * Menyeleksi sel tetangga yang bisa dilewati (tidak terhalang tembok).
     */
    private List<Cell> getValidNeighbors(Cell c) {
        List<Cell> neighbors = new ArrayList<>();
        int x = c.x;
        int y = c.y;

        // Cek Tembok Atas
        if (!c.walls[0] && isValid(y - 1, x)) neighbors.add(grid[x][y - 1]);
        // Cek Tembok Kanan
        if (!c.walls[1] && isValid(y, x + 1)) neighbors.add(grid[x + 1][y]);
        // Cek Tembok Bawah
        if (!c.walls[2] && isValid(y + 1, x)) neighbors.add(grid[x][y + 1]);
        // Cek Tembok Kiri
        if (!c.walls[3] && isValid(y, x - 1)) neighbors.add(grid[x - 1][y]);

        return neighbors;
    }

    private boolean isValid(int r, int c) {
        return c >= 0 && c < cols && r >= 0 && r < rows;
    }

    /**
     * Menelusuri balik dari sel target ke sel awal melalui parentMap untuk membentuk urutan jalur.
     */
    private List<Cell> reconstructPath(Map<Cell, Cell> parentMap, Cell end) {
        List<Cell> path = new ArrayList<>();
        Cell current = end;
        while (current != null) {
            path.add(0, current); // Tambahkan ke depan agar urutan menjadi Start -> End
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