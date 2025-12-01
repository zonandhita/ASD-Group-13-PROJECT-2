import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MazePanel extends JPanel {
    private Cell[][] grid;
    private int cellSize = 30;
    private List<Cell> currentPath;
    private int currentStep = 0;

    public MazePanel() {
        setPreferredSize(new Dimension(600, 600));
        setBackground(Color.WHITE);
    }

    public void setMaze(Cell[][] grid) {
        this.grid = grid;
        if (grid != null && grid.length > 0) {
            int height = grid.length;
            int width = grid[0].length;
            cellSize = Math.min(550 / width, 550 / height);
        }
        currentPath = null;
        currentStep = 0;
        repaint();
    }

    public void setPath(List<Cell> path) {
        this.currentPath = path;
        this.currentStep = 0;
        repaint();
    }

    public void animateStep() {
        if (currentPath != null && currentStep < currentPath.size()) {
            currentStep++;
            repaint();
        }
    }

    public void showFullPath() {
        if (currentPath != null) {
            currentStep = currentPath.size();
            repaint();
        }
    }

    public void resetAnimation() {
        currentStep = 0;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (grid == null) return;

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int offsetX = 25;
        int offsetY = 25;

        // Draw cells
        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid[0].length; x++) {
                Cell cell = grid[y][x];
                int px = offsetX + x * cellSize;
                int py = offsetY + y * cellSize;

                // Draw walls
                g2d.setColor(Color.BLACK);
                g2d.setStroke(new BasicStroke(2));

                if (cell.hasTopWall()) {
                    g2d.drawLine(px, py, px + cellSize, py);
                }
                if (cell.hasRightWall()) {
                    g2d.drawLine(px + cellSize, py, px + cellSize, py + cellSize);
                }
                if (cell.hasBottomWall()) {
                    g2d.drawLine(px, py + cellSize, px + cellSize, py + cellSize);
                }
                if (cell.hasLeftWall()) {
                    g2d.drawLine(px, py, px, py + cellSize);
                }
            }
        }

        // Draw start (green)
        g2d.setColor(new Color(0, 200, 0));
        g2d.fillRect(offsetX + 2, offsetY + 2, cellSize - 4, cellSize - 4);

        // Draw end (red)
        int endX = (grid[0].length - 1) * cellSize;
        int endY = (grid.length - 1) * cellSize;
        g2d.setColor(new Color(200, 0, 0));
        g2d.fillRect(offsetX + endX + 2, offsetY + endY + 2, cellSize - 4, cellSize - 4);

        // Draw path animation
        if (currentPath != null && currentStep > 0) {
            g2d.setColor(new Color(100, 150, 255));
            for (int i = 0; i < Math.min(currentStep, currentPath.size()); i++) {
                Cell cell = currentPath.get(i);
                int px = offsetX + cell.getX() * cellSize;
                int py = offsetY + cell.getY() * cellSize;
                g2d.fillOval(px + cellSize/4, py + cellSize/4, cellSize/2, cellSize/2);
            }
        }
    }
}
