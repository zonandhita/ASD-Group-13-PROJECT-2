import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MazePanel extends JPanel {
    private Cell[][] grid;
    private int cellSize = 25;
    private List<Cell> currentPath = new ArrayList<>();
    private int pathDrawLimit = 0;

    private final Color COLOR_PATH = new Color(255, 215, 0); // Emas Eldoria
    private final Color COLOR_START = new Color(0, 255, 0);
    private final Color COLOR_END = new Color(255, 0, 0);

    public MazePanel() {
        setBackground(new Color(15, 15, 25)); // Background gelap elegan
    }

    public void setMaze(Cell[][] grid) {
        this.grid = grid;
        this.currentPath.clear();
        this.pathDrawLimit = 0;
        repaint();
    }

    public void setPath(List<Cell> path) {
        this.currentPath = path;
        this.pathDrawLimit = 0;
    }

    public void incrementPathIndex() {
        if (currentPath != null && pathDrawLimit < currentPath.size()) {
            pathDrawLimit++;
            repaint();
        }
    }

    public void resetAnimation() {
        this.currentPath.clear();
        this.pathDrawLimit = 0;
        repaint();
    }

    public Cell[][] getGrid() { return grid; }
    public Cell getStartCell() { return grid != null ? grid[0][0] : null; }
    public Cell getEndCell() {
        return grid != null ? grid[grid.length - 1][grid[0].length - 1] : null;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (grid == null) {
            drawWelcomeMessage(g);
            return;
        }

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int cols = grid.length;
        int rows = grid[0].length;

        // PERBAIKAN: Margin dikurangi (dari 80 ke 20) agar ukuran maze lebih besar
        cellSize = Math.min((getWidth() - 20) / cols, (getHeight() - 20) / rows);
        if (cellSize < 5) cellSize = 5;

        // LOGIKA PEMUSATAN: Hitung sisa ruang (offset)
        int mazeWidth = cols * cellSize;
        int mazeHeight = rows * cellSize;
        int offsetX = (getWidth() - mazeWidth) / 2;
        int offsetY = (getHeight() - mazeHeight) / 2;

        // Gambar Grid dan Terrain
        for (int x = 0; x < cols; x++) {
            for (int y = 0; y < rows; y++) {
                Cell cell = grid[x][y];
                int px = offsetX + (x * cellSize);
                int py = offsetY + (y * cellSize);

                drawStyledCell(g2d, cell, px, py, cellSize);
            }
        }

        // Gambar Start & End Marker
        drawMarkers(g2d, offsetX, offsetY, cols, rows);

        // Gambar Path Animasi
        if (currentPath != null && !currentPath.isEmpty()) {
            drawAnimatedPath(g2d, offsetX, offsetY);
        }
    }

    private void drawStyledCell(Graphics2D g2d, Cell cell, int px, int py, int size) {
        // Warna Terrain
        if (cell.weight == 1) g2d.setColor(new Color(144, 238, 144, 200)); // Grass
        else if (cell.weight == 5) g2d.setColor(new Color(139, 69, 19, 200)); // Mud
        else g2d.setColor(new Color(30, 144, 255, 200)); // Water

        g2d.fillRect(px, py, size, size);

        // Walls
        g2d.setColor(new Color(0, 0, 0));
        g2d.setStroke(new BasicStroke(2));
        if (cell.walls[0]) g2d.drawLine(px, py, px + size, py);
        if (cell.walls[1]) g2d.drawLine(px + size, py, px + size, py + size);
        if (cell.walls[2]) g2d.drawLine(px + size, py + size, px, py + size);
        if (cell.walls[3]) g2d.drawLine(px, py + size, px, py);
    }

    private void drawMarkers(Graphics2D g2d, int ox, int oy, int cols, int rows) {
        int r = (int)(cellSize * 0.7);
        // Start
        g2d.setColor(COLOR_START);
        g2d.fillOval(ox + (cellSize - r) / 2, oy + (cellSize - r) / 2, r, r);
        // End
        g2d.setColor(COLOR_END);
        int ex = ox + (cols - 1) * cellSize;
        int ey = oy + (rows - 1) * cellSize;
        g2d.fillOval(ex + (cellSize - r) / 2, ey + (cellSize - r) / 2, r, r);
    }

    private void drawAnimatedPath(Graphics2D g2d, int ox, int oy) {
        g2d.setStroke(new BasicStroke(Math.max(3, cellSize / 5), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.setColor(COLOR_PATH);
        for (int i = 0; i < pathDrawLimit - 1; i++) {
            Cell c1 = currentPath.get(i);
            Cell c2 = currentPath.get(i + 1);
            g2d.drawLine(ox + c1.x * cellSize + cellSize / 2, oy + c1.y * cellSize + cellSize / 2,
                    ox + c2.x * cellSize + cellSize / 2, oy + c2.y * cellSize + cellSize / 2);
        }
    }

    private void drawWelcomeMessage(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(new Color(212, 175, 55));
        g2d.setFont(new Font("Georgia", Font.BOLD, 40));
        String txt = "MAZE ADVENTURE";
        int w = g2d.getFontMetrics().stringWidth(txt);
        g2d.drawString(txt, (getWidth() - w) / 2, getHeight() / 2);
    }
}