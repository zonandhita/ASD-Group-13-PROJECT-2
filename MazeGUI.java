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
        setLayout(new BorderLayout(10, 10)); // Memberikan jarak antar komponen

        // ========================================================
        // 1. SIDE PANEL (DASHBOARD SAMPING)
        // ========================================================
        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        sidePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        sidePanel.setPreferredSize(new Dimension(220, 0));

        // --- Grup Pengaturan ---
        JPanel configPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        configPanel.setBorder(BorderFactory.createTitledBorder("Pengaturan Grid"));
        configPanel.setMaximumSize(new Dimension(200, 100));

        configPanel.add(new JLabel("Lebar:"));
        widthSpinner = new JSpinner(new SpinnerNumberModel(25, 5, 60, 1));
        configPanel.add(widthSpinner);

        configPanel.add(new JLabel("Tinggi:"));
        heightSpinner = new JSpinner(new SpinnerNumberModel(25, 5, 60, 1));
        configPanel.add(heightSpinner);

        // --- Tombol Aksi Utama ---
        generateBtn = new JButton("Buat Labirin");
        generateBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        generateBtn.addActionListener(e -> generateMaze());

        terrainBtn = new JButton("Acak Medan");
        terrainBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        terrainBtn.setEnabled(false);
        terrainBtn.addActionListener(e -> randomizeTerrain());

        // --- Grup Algoritma ---
        JPanel algoPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        algoPanel.setBorder(BorderFactory.createTitledBorder("Algoritma"));

        bfsBtn = new JButton("BFS"); setupAlgoButton(bfsBtn, "BFS"); algoPanel.add(bfsBtn);
        dfsBtn = new JButton("DFS"); setupAlgoButton(dfsBtn, "DFS"); algoPanel.add(dfsBtn);
        dijkstraBtn = new JButton("Dijkstra"); setupAlgoButton(dijkstraBtn, "Dijkstra"); algoPanel.add(dijkstraBtn);
        aStarBtn = new JButton("A*"); setupAlgoButton(aStarBtn, "A*"); algoPanel.add(aStarBtn);

        compareBtn = new JButton("BANDINGKAN SEMUA");
        compareBtn.setBackground(new Color(255, 165, 0));
        compareBtn.setFont(new Font("SansSerif", Font.BOLD, 10));
        compareBtn.setEnabled(false);
        compareBtn.addActionListener(e -> compareAllAlgorithms());

        resetBtn = new JButton("Reset Visual");
        resetBtn.addActionListener(e -> resetAnimation());

        // Menyusun komponen ke panel samping
        sidePanel.add(configPanel);
        sidePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        sidePanel.add(generateBtn);
        sidePanel.add(Box.createRigidArea(new Dimension(0, 5)));
        sidePanel.add(terrainBtn);
        sidePanel.add(Box.createRigidArea(new Dimension(0, 15)));
        sidePanel.add(algoPanel);
        sidePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        sidePanel.add(compareBtn);
        sidePanel.add(Box.createVerticalGlue()); // Mendorong tombol reset ke bawah
        sidePanel.add(resetBtn);

        add(sidePanel, BorderLayout.WEST);

        // ========================================================
        // 2. CENTER PANEL (AREA LABIRIN)
        // ========================================================
        mazePanel = new MazePanel();
        add(mazePanel, BorderLayout.CENTER);

        // ========================================================
        // 3. BOTTOM PANEL (TABEL HASIL)
        // ========================================================
        String[] columnNames = {"Algoritma", "Langkah", "Bobot", "Waktu (ms)", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0);
        resultsTable = new JTable(tableModel);
        resultsTable.setEnabled(false);
        resultsTable.setRowHeight(20);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for(int i=0; i<resultsTable.getColumnCount(); i++){
            resultsTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(resultsTable);
        scrollPane.setPreferredSize(new Dimension(0, 120));
        add(scrollPane, BorderLayout.SOUTH);

        pack();
        setMinimumSize(new Dimension(800, 600)); // Ukuran minimal agar dashboard samping pas
        setLocationRelativeTo(null);
    }

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
        tableModel.setRowCount(0);
    }

    private void randomizeTerrain() {
        if (grid == null) return;
        resetAnimation();
        for (int y = 0; y < grid[0].length; y++) {
            for (int x = 0; x < grid.length; x++) {
                if ((x == 0 && y == 0) || (x == grid.length-1 && y == grid[0].length-1)) {
                    grid[x][y].weight = 1;
                } else {
                    grid[x][y].setRandomTerrain();
                }
            }
        }
        mazePanel.repaint();
    }

    private void solveMaze(String algorithm) {
        if (grid == null) return;
        stopTimer();
        mazePanel.resetAnimation();

        Cell start = grid[0][0];
        Cell end = grid[grid.length - 1][grid[0].length - 1];
        List<Cell> path = null;
        long startTime = System.nanoTime();

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
        JOptionPane.showMessageDialog(this, "Perbandingan Selesai!");
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

    private void runPathAnimation(int pathSize) {
        if (animationTimer != null && animationTimer.isRunning()) animationTimer.stop();
        final int[] stepsDrawn = {0};
        int speedDelay = 30; // Sedikit dipercepat karena area samping biasanya lebih ramping

        animationTimer = new Timer(speedDelay, e -> {
            mazePanel.incrementPathIndex();
            stepsDrawn[0]++;
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