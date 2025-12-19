import java.awt.*;

public class Cell implements Comparable<Cell> {
    // Menyimpan posisi sel dalam grid (indeks baris dan kolom)
    public int x, y;

    // Status tembok pada empat sisi: [0]=Atas, [1]=Kanan, [2]=Bawah, [3]=Kiri
    public boolean[] walls = {true, true, true, true};

    // Penentuan bobot jalur: 1 (Normal), 5 (Sedang/Lumpur), 10 (Sulit/Air)
    public int weight = 1;
    private boolean visited = false;

    // Variabel pendukung untuk algoritma pencarian jalur (A* atau Dijkstra)
    public Cell parent;
    public int g, h, f;

    public Cell(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // --- Akses Posisi ---

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    // --- Manajemen Status Tembok ---

    public boolean hasTopWall() { return walls[0]; }
    public boolean hasRightWall() { return walls[1]; }
    public boolean hasBottomWall() { return walls[2]; }
    public boolean hasLeftWall() { return walls[3]; }

    public void removeTopWall() { walls[0] = false; }
    public void removeRightWall() { walls[1] = false; }
    public void removeBottomWall() { walls[2] = false; }
    public void removeLeftWall() { walls[3] = false; }

    // --- Logika Algoritma & Terrain ---

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    /**
     * Mengatur tipe medan secara acak berdasarkan probabilitas:
     * 60% Rumput, 30% Lumpur, 10% Air.
     */
    public void setRandomTerrain() {
        double r = Math.random();
        if (r < 0.6) weight = 1;
        else if (r < 0.9) weight = 5;
        else weight = 10;
    }

    /**
     * Digunakan oleh PriorityQueue untuk menentukan urutan prioritas sel
     * berdasarkan nilai total cost (f) terkecil.
     */
    @Override
    public int compareTo(Cell other) {
        return Integer.compare(this.f, other.f);
    }

    /**
     * Render visual sel dan temboknya ke dalam komponen grafis.
     */
    public void draw(Graphics g2d, int size) {
        int xPos = x * size;
        int yPos = y * size;

        // Visualisasi warna berdasarkan bobot terrain
        if (weight == 1) g2d.setColor(new Color(220, 255, 220));      // Hijau muda (Grass)
        else if (weight == 5) g2d.setColor(new Color(139, 69, 19));   // Cokelat (Mud)
        else if (weight == 10) g2d.setColor(new Color(135, 206, 235)); // Biru (Water)

        g2d.fillRect(xPos, yPos, size, size);

        // Menggambar garis tembok jika statusnya true
        g2d.setColor(Color.BLACK);
        if (walls[0]) g2d.drawLine(xPos, yPos, xPos + size, yPos);
        if (walls[1]) g2d.drawLine(xPos + size, yPos, xPos + size, yPos + size);
        if (walls[2]) g2d.drawLine(xPos + size, yPos + size, xPos, yPos + size);
        if (walls[3]) g2d.drawLine(xPos, yPos + size, xPos, yPos);
    }
}