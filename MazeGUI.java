import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MazeGUI extends JFrame {
    private MazePanel mazePanel;
    private Cell[][] grid;
    private MazeSolver solver;
    private Timer animationTimer;

    // Komponen Kontrol
    private JButton generateBtn, terrainBtn;
    private JButton bfsBtn, dfsBtn, dijkstraBtn, aStarBtn, compareBtn;
    private JButton resetBtn;
    private JSpinner widthSpinner, heightSpinner;

    // Komponen Tabel Statistik
    private JTable resultsTable;
    private DefaultTableModel tableModel;

    public MazeGUI() {
        // Mengatur tema jendela agar sesuai dengan sistem operasi pengguna
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}

        setTitle("Labirin Dashboard - Analisis Algoritma");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // --- Panel Atas: Pengaturan Grid dan Tombol Algoritma ---
        JPanel topContainer = new JPanel(new GridLayout(2, 1));

        // Baris Atas: Input Ukuran dan Inisialisasi
        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        row1.add(new JLabel("Lebar:"));
        widthSpinner = new JSpinner(new SpinnerNumberModel(25, 5, 60, 1));
        row1.add(widthSpinner);

        row1.add(new JLabel("Tinggi:"));
        heightSpinner = new JSpinner(new SpinnerNumberModel(25, 5, 60, 1));
        row1.add(heightSpinner);

        generateBtn = new JButton("Buat Labirin");
        generateBtn.addActionListener(e -> generateMaze());
        row1.add(generateBtn);

        terrainBtn = new JButton("Acak Medan (Bobot)");
        terrainBtn.setEnabled(false);
        terrainBtn.addActionListener(e -> randomizeTerrain());
        row1.add(terrainBtn);

        // Baris Bawah: Seleksi Algoritma Pencarian Jalur
        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        bfsBtn = new JButton("BFS"); setupAlgoButton(bfsBtn, "BFS"); row2.add(bfsBtn);
        dfsBtn = new JButton("DFS"); setupAlgoButton(dfsBtn, "DFS"); row2.add(dfsBtn);
        dijkstraBtn = new JButton("Dijkstra"); setupAlgoButton(dijkstraBtn, "Dijkstra"); row2.add(dijkstraBtn);
        aStarBtn = new JButton("A*"); setupAlgoButton(aStarBtn, "A*"); row2.add(aStarBtn);

        row2.add(Box.createHorizontalStrut(20));
        compareBtn = new JButton("BANDINGKAN SEMUA");
        compareBtn.setBackground(new Color(255, 165, 0)); // Warna oranye untuk menonjolkan fitur
        compareBtn.setFont(new Font("SansSerif", Font.BOLD, 11));
        compareBtn.setEnabled(false);
        compareBtn.addActionListener(e -> compareAllAlgorithms());
        row2.add(compareBtn);

        resetBtn = new JButton("Reset");
        resetBtn.addActionListener(e -> resetAnimation());
        row2.add(resetBtn);

        topContainer.add(row1);
        topContainer.add(row2);
        add(topContainer, BorderLayout.NORTH);

        // --- Panel Tengah: Area Visualisasi Labirin ---
        mazePanel = new MazePanel();
        add(mazePanel, BorderLayout.CENTER);

        // --- Panel Bawah: Tabel Data Statistik ---
        String[] columnNames = {"Algoritma", "Jumlah Langkah", "Total Bobot", "Waktu (ms)", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0);
        resultsTable = new JTable(tableModel);
        resultsTable.setEnabled(false); // Tabel hanya untuk melihat data, bukan input
        resultsTable.setRowHeight(20);

        // Mengatur agar teks di dalam sel tabel berada di tengah
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for(int i=0; i<resultsTable.getColumnCount(); i++){
            resultsTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(resultsTable);
        scrollPane.setPreferredSize(new Dimension(0, 130));
        add(scrollPane, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
    }

    /**
     * Membuat labirin baru dan menyiapkan objek solver berdasarkan ukuran input.
     */
    private void generateMaze() {
        int width = (Integer) widthSpinner.getValue();
        int height = (Integer) heightSpinner.getValue();
        MazeGenerator generator = new MazeGenerator(width, height);
        grid = generator.generateMaze();
        solver = new MazeSolver(grid);
        mazePanel.setMaze(grid);
        randomizeTerrain();
        enableControls(true);
        resetAnimation();
        tableModel.setRowCount(0); // Membersihkan tabel statistik sebelumnya
    }

    /**
     * Memberikan variasi bobot (medan) pada setiap sel untuk menguji algoritma Dijkstra/A*.
     */
    private void randomizeTerrain() {
        if (grid == null) return;
        resetAnimation();
        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid[0].length; x++) {
                // Pastikan titik start (0,0) dan finish (end) selalu memiliki bobot normal (1)
                if ((x == 0 && y == 0) || (x == grid[0].length-1 && y == grid.length-1)) {
                    grid[y][x].weight = 1;
                } else {
                    grid[y][x].setRandomTerrain();
                }
            }
        }
        mazePanel.repaint();
    }

    /**
     * Menjalankan algoritma tunggal dan memulai proses animasi jalur.
     */
    private void solveMaze(String algorithm) {
        if (grid == null) return;
        stopTimer();
        mazePanel.resetAnimation();

        Cell start = grid[0][0];
        Cell end = grid[grid.length - 1][grid[0].length - 1];
        List<Cell> path = null;
        long startTime = System.nanoTime();

        // Pemilihan fungsi pencarian berdasarkan nama tombol
        switch (algorithm) {
            case "BFS": path = solver.solveBFS(start, end); break;
            case "DFS": path = solver.solveDFS(start, end); break;
            case "Dijkstra": path = solver.solveDijkstra(start, end); break;
            case "A*": path = solver.solveAStar(start, end); break;
        }

        long duration = (System.nanoTime() - startTime) / 1_000_000;

        if (path != null && !path.isEmpty()) {
            int cost = calculateCost(path);
            tableModel.addRow(new Object[]{algorithm, path.size(), cost, duration, "Animating..."});
            mazePanel.setPath(path);
            runPathAnimation(path.size());
        } else {
            JOptionPane.showMessageDialog(this, "Jalur tidak ditemukan!");
        }
    }

    /**
     * Menjalankan semua algoritma secara berurutan untuk membandingkan efisiensi.
     */
    private void compareAllAlgorithms() {
        if (grid == null) return;
        stopTimer();
        mazePanel.resetAnimation();
        tableModel.setRowCount(0);
        Cell start = grid[0][0];
        Cell end = grid[grid.length - 1][grid[0].length - 1];

        runAndRecord("BFS", () -> solver.solveBFS(start, end));
        runAndRecord("DFS", () -> solver.solveDFS(start, end));
        runAndRecord("Dijkstra", () -> solver.solveDijkstra(start, end));
        runAndRecord("A*", () -> solver.solveAStar(start, end));
        JOptionPane.showMessageDialog(this, "Perbandingan Selesai! Lihat data pada tabel.");
    }

    private void runAndRecord(String name, AlgorithmRunner runner) {
        long startT = System.nanoTime();
        List<Cell> path = runner.run();
        long endT = System.nanoTime();
        double durationMs = (endT - startT) / 1_000_000.0;

        if (path != null && !path.isEmpty()) {
            int cost = calculateCost(path);
            tableModel.addRow(new Object[]{name, path.size(), cost, String.format("%.2f", durationMs), "Done"});
        } else {
            tableModel.addRow(new Object[]{name, "-", "-", "-", "Failed"});
        }
    }

    private int calculateCost(List<Cell> path) {
        int total = 0;
        for (Cell c : path) total += c.weight;
        return total;
    }

    /**
     * Mengatur jalannya visualisasi titik demi titik pada MazePanel.
     * Semakin tinggi nilai speedDelay, semakin lambat animasinya.
     */
    private void runPathAnimation(int pathSize) {
        if (animationTimer != null && animationTimer.isRunning()) animationTimer.stop();
        final int[] stepsDrawn = {0};

        int speedDelay = 50; // Milliseconds per langkah

        animationTimer = new Timer(speedDelay, e -> {
            mazePanel.incrementPathIndex();
            stepsDrawn[0]++;

            // Memberikan sedikit jeda setelah animasi selesai sebelum merubah status tabel
            if (stepsDrawn[0] >= pathSize + 5) {
                ((Timer)e.getSource()).stop();
                updateTableStatusToFinished();
            }
        });

        animationTimer.start();
    }

    private void updateTableStatusToFinished() {
        if (tableModel.getRowCount() > 0) {
            int lastRow = tableModel.getRowCount() - 1;
            if ("Animating...".equals(tableModel.getValueAt(lastRow, 4))) {
                tableModel.setValueAt("Finished", lastRow, 4);
            }
        }
    }

    private void stopTimer() {
        if (animationTimer != null) animationTimer.stop();
    }

    private void resetAnimation() {
        stopTimer();
        mazePanel.resetAnimation();
    }

    private void setupAlgoButton(JButton btn, String algo) {
        btn.setEnabled(false);
        btn.addActionListener(e -> solveMaze(algo));
    }

    private void enableControls(boolean enable) {
        terrainBtn.setEnabled(enable);
        bfsBtn.setEnabled(enable);
        dfsBtn.setEnabled(enable);
        dijkstraBtn.setEnabled(enable);
        aStarBtn.setEnabled(enable);
        compareBtn.setEnabled(enable);
    }

    interface AlgorithmRunner {
        List<Cell> run();
    }
}