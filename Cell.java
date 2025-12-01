import java.awt.*;

public class Cell implements Comparable<Cell> {
    // Koordinat & Data Tembok
    public int x, y;
    public boolean[] walls = {true, true, true, true}; // Top, Right, Bottom, Left

    // Algoritma Data (Bobot & Pathfinding)
    public int weight = 1; // 1=Grass, 5=Mud, 10=Water
    private boolean visited = false;
    public Cell parent;
    public int g, h, f;

    public Cell(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // ==========================================
    // BAGIAN INI UNTUK MEMPERBAIKI ERROR DI MAZEPANEL
    // ==========================================

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    // Konversi array walls[] ke method has...Wall()
    public boolean hasTopWall() { return walls[0]; }
    public boolean hasRightWall() { return walls[1]; }
    public boolean hasBottomWall() { return walls[2]; }
    public boolean hasLeftWall() { return walls[3]; }

    // Jika MazeGenerator butuh menghapus tembok
    public void removeTopWall() { walls[0] = false; }
    public void removeRightWall() { walls[1] = false; }
    public void removeBottomWall() { walls[2] = false; }
    public void removeLeftWall() { walls[3] = false; }

    // ==========================================
    // DATA ALGORITMA
    // ==========================================

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public void setRandomTerrain() {
        double r = Math.random();
        if (r < 0.6) weight = 1;
        else if (r < 0.9) weight = 5;
        else weight = 10;
    }

    // Untuk PriorityQueue (Dijkstra/A*)
    @Override
    public int compareTo(Cell other) {
        return Integer.compare(this.f, other.f);
    }

    // Method Draw (Opsional, jika dipanggil manual)
    public void draw(Graphics g2d, int size) {
        int xPos = x * size;
        int yPos = y * size;

        // Warna Terrain
        if (weight == 1) g2d.setColor(new Color(220, 255, 220));
        else if (weight == 5) g2d.setColor(new Color(139, 69, 19));
        else if (weight == 10) g2d.setColor(new Color(135, 206, 235));

        g2d.fillRect(xPos, yPos, size, size);

        // Gambar Tembok
        g2d.setColor(Color.BLACK);
        if (walls[0]) g2d.drawLine(xPos, yPos, xPos + size, yPos);
        if (walls[1]) g2d.drawLine(xPos + size, yPos, xPos + size, yPos + size);
        if (walls[2]) g2d.drawLine(xPos + size, yPos + size, xPos, yPos + size);
        if (walls[3]) g2d.drawLine(xPos, yPos + size, xPos, yPos);
    }
}