import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MazePanel extends JPanel {
    private Cell[][] grid;
    private int cellSize = 25;
    private List<Cell> currentPath = new ArrayList<>();

    // Mengontrol jumlah langkah jalur yang ditampilkan ke layar untuk efek animasi
    private int pathDrawLimit = 0;

    // Definisi palet warna untuk tipe medan (Terrain)
    private final Color COLOR_GRASS = new Color(225, 255, 225);
    private final Color COLOR_MUD = new Color(210, 180, 140);
    private final Color COLOR_WATER = new Color(173, 216, 230);

    public MazePanel() {
        setPreferredSize(new Dimension(600, 600));
        setBackground(Color.WHITE);
    }

    /**
     * Memperbarui data grid labirin dan mereset status visual.
     */
    public void setMaze(Cell[][] grid) {
        this.grid = grid;
        this.currentPath.clear();
        this.pathDrawLimit = 0;
        repaint();
    }

    /**
     * Menyiapkan jalur hasil pencarian algoritma untuk dianimasikan.
     */
    public void setPath(List<Cell> path) {
        this.currentPath = path;
        this.pathDrawLimit = 0;
    }

    /**
     * Menambah jumlah sel yang digambar satu per satu.
     * Fungsi ini dipanggil secara berkala oleh Timer di MazeGUI.
     */
    public void incrementPathIndex() {
        if (currentPath != null && pathDrawLimit < currentPath.size()) {
            pathDrawLimit++;
            repaint(); // Memicu penggambaran ulang setiap ada penambahan langkah
        }
    }

    public void resetAnimation() {
        this.currentPath.clear();
        this.pathDrawLimit = 0;
        repaint();
    }

    public Cell[][] getGrid() { return grid; }
    public Cell getStartCell() { return grid[0][0]; }
    public Cell getEndCell() { return grid[grid.length-1][grid[0].length-1]; }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (grid == null) return;

        Graphics2D g2d = (Graphics2D) g;
        // Mengaktifkan Antialiasing agar garis tembok dan lingkaran jalur terlihat halus
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Menghitung ukuran sel secara dinamis berdasarkan ukuran jendela yang tersedia
        int width = getWidth();
        int height = getHeight();
        int cols = grid.length;
        int rows = grid[0].length;
        cellSize = Math.min(width / cols, height / rows);
        if (cellSize < 5) cellSize = 5;

        // --- TAHAP 1: Menggambar Lantai (Medan) dan Tembok ---
        for (int x = 0; x < cols; x++) {
            for (int y = 0; y < rows; y++) {
                Cell cell = grid[x][y];
                int px = x * cellSize;
                int py = y * cellSize;

                // Menentukan warna berdasarkan bobot sel
                if (cell.weight == 1) g2d.setColor(COLOR_GRASS);
                else if (cell.weight == 5) g2d.setColor(COLOR_MUD);
                else if (cell.weight == 10) g2d.setColor(COLOR_WATER);

                // Menandai titik Start (Hijau) dan Finish (Merah)
                if (x == 0 && y == 0) g2d.setColor(Color.GREEN);
                else if (x == cols-1 && y == rows-1) g2d.setColor(Color.RED);

                g2d.fillRect(px, py, cellSize, cellSize);

                // Menggambar garis tembok jika status dinding sel bernilai true
                g2d.setColor(Color.BLACK);
                g2d.setStroke(new BasicStroke(2));
                if (cell.walls[0]) g2d.drawLine(px, py, px + cellSize, py); // Atas
                if (cell.walls[1]) g2d.drawLine(px + cellSize, py, px + cellSize, py + cellSize); // Kanan
                if (cell.walls[2]) g2d.drawLine(px + cellSize, py + cellSize, px, py + cellSize); // Bawah
                if (cell.walls[3]) g2d.drawLine(px, py + cellSize, px, py); // Kiri
            }
        }

        // --- TAHAP 2: Menggambar Jalur Solusi (Animasi) ---
        if (currentPath != null && !currentPath.isEmpty()) {
            g2d.setColor(new Color(0, 102, 204)); // Biru tua untuk jalur
            for (int i = 0; i < pathDrawLimit && i < currentPath.size(); i++) {
                Cell c = currentPath.get(i);
                int px = c.x * cellSize;
                int py = c.y * cellSize;

                // Menggambar bulatan di tengah sel
                int dotSize = cellSize / 2;
                int offset = (cellSize - dotSize) / 2;
                g2d.fillOval(px + offset, py + offset, dotSize, dotSize);
            }
        }
    }
}