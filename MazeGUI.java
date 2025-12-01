import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MazeGUI extends JFrame {
    private MazePanel mazePanel;
    private Cell[][] grid;
    private MazeSolver solver;
    private Timer animationTimer;

    // UI Components
    private JButton generateBtn, terrainBtn;
    private JButton bfsBtn, dfsBtn, dijkstraBtn, aStarBtn;
    private JButton resetBtn;
    private JSpinner widthSpinner, heightSpinner;
    private JLabel statsLabel;

    public MazeGUI() {
        setTitle("Maze Generator & Solver (Weighted Graph)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // --- 1. Top Panel (Controls) ---
        JPanel topPanel = new JPanel(new GridLayout(2, 1));

        // Baris 1: Settings
        JPanel settingsPanel = new JPanel(new FlowLayout());
        settingsPanel.add(new JLabel("W:"));
        widthSpinner = new JSpinner(new SpinnerNumberModel(20, 5, 50, 1));
        settingsPanel.add(widthSpinner);

        settingsPanel.add(new JLabel("H:"));
        heightSpinner = new JSpinner(new SpinnerNumberModel(20, 5, 50, 1));
        settingsPanel.add(heightSpinner);

        generateBtn = new JButton("Generate Maze");
        generateBtn.addActionListener(e -> generateMaze());
        settingsPanel.add(generateBtn);

        terrainBtn = new JButton("Randomize Terrain");
        terrainBtn.setEnabled(false);
        terrainBtn.addActionListener(e -> randomizeTerrain());
        settingsPanel.add(terrainBtn);

        // Baris 2: Algorithm Buttons
        JPanel algoPanel = new JPanel(new FlowLayout());
        bfsBtn = new JButton("BFS");
        bfsBtn.addActionListener(e -> solveMaze("BFS"));

        dfsBtn = new JButton("DFS");
        dfsBtn.addActionListener(e -> solveMaze("DFS"));

        dijkstraBtn = new JButton("Dijkstra");
        dijkstraBtn.addActionListener(e -> solveMaze("Dijkstra"));

        aStarBtn = new JButton("A*");
        aStarBtn.addActionListener(e -> solveMaze("A*"));

        resetBtn = new JButton("Reset");
        resetBtn.addActionListener(e -> resetAnimation());

        algoPanel.add(bfsBtn);
        algoPanel.add(dfsBtn);
        algoPanel.add(dijkstraBtn);
        algoPanel.add(aStarBtn);
        algoPanel.add(resetBtn);

        topPanel.add(settingsPanel);
        topPanel.add(algoPanel);
        add(topPanel, BorderLayout.NORTH);

        // --- 2. Center (Maze Panel) ---
        mazePanel = new MazePanel();
        add(mazePanel, BorderLayout.CENTER);

        // --- 3. Bottom (Stats Label) ---
        JPanel bottomPanel = new JPanel();
        statsLabel = new JLabel("Ready. Generate a maze to start.");
        statsLabel.setFont(new Font("Arial", Font.BOLD, 14));
        bottomPanel.add(statsLabel);
        add(bottomPanel, BorderLayout.SOUTH);

        // Disable buttons initially
        enableControls(false);
        generateBtn.setEnabled(true);

        pack();
        setLocationRelativeTo(null);
    }

    private void generateMaze() {
        int width = (Integer) widthSpinner.getValue();
        int height = (Integer) heightSpinner.getValue();

        MazeGenerator generator = new MazeGenerator(width, height);
        grid = generator.generateMaze();
        solver = new MazeSolver(grid);

        mazePanel.setMaze(grid);
        statsLabel.setText("Maze Generated. Click 'Randomize Terrain' to add weights.");

        enableControls(true);
        resetAnimation();
    }

    private void randomizeTerrain() {
        if (grid == null) return;
        mazePanel.resetAnimation();

        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid[0].length; x++) {
                // Keep start/end as grass
                if ((x == 0 && y == 0) || (x == grid[0].length-1 && y == grid.length-1)) {
                    grid[y][x].weight = 1;
                } else {
                    grid[y][x].setRandomTerrain();
                }
            }
        }
        mazePanel.repaint();
        statsLabel.setText("Terrain Randomized! Green=1 (Grass), Brown=5 (Mud), Blue=10 (Water)");
    }

    private void solveMaze(String algorithm) {
        if (grid == null) return;

        // Stop timer lama jika masih jalan
        if (animationTimer != null && animationTimer.isRunning()) animationTimer.stop();
        mazePanel.resetAnimation();

        Cell start = grid[0][0];
        Cell end = grid[grid.length - 1][grid[0].length - 1];
        List<Cell> path = null;

        long startTime = System.nanoTime();

        // Solve algoritma
        switch (algorithm) {
            case "BFS": path = solver.solveBFS(start, end); break;
            case "DFS": path = solver.solveDFS(start, end); break;
            case "Dijkstra": path = solver.solveDijkstra(start, end); break;
            case "A*": path = solver.solveAStar(start, end); break;
        }

        long duration = (System.nanoTime() - startTime) / 1_000_000;

        if (path != null && !path.isEmpty()) {
            int totalCost = 0;
            for (Cell c : path) totalCost += c.weight;

            // Tampilkan text status (sedang animasi)
            statsLabel.setText(String.format("[%s] Steps: %d | Total Cost: %d | Time: %dms (Animating...)",
                    algorithm, path.size(), totalCost, duration));

            if (algorithm.equals("BFS") || algorithm.equals("DFS")) statsLabel.setForeground(Color.RED);
            else statsLabel.setForeground(new Color(0, 100, 0));

            // Set data path ke panel
            mazePanel.setPath(path);

            // JALANKAN ANIMASI STEP BY STEP
            runPathAnimation(path.size());

        } else {
            statsLabel.setText("No path found!");
        }
    }

    private void runPathAnimation(int pathSize) {
        // Delay 30ms per langkah (bisa diubah biar lebih lambat/cepat)
        animationTimer = new Timer(30, e -> {
            mazePanel.incrementPathIndex(); // Memanggil method di MazePanel
        });

        animationTimer.start();

        // Timer untuk memberhentikan animasi ketika sudah selesai
        Timer stopTimer = new Timer(30 * pathSize + 500, e -> {
            if (animationTimer != null) animationTimer.stop();
            String text = statsLabel.getText().replace("(Animating...)", "(Done)");
            statsLabel.setText(text);
        });
        stopTimer.setRepeats(false);
        stopTimer.start();
    }

    private void resetAnimation() {
        if (animationTimer != null) animationTimer.stop();
        mazePanel.resetAnimation();
        statsLabel.setText("Path cleared.");
    }

    private void enableControls(boolean enable) {
        terrainBtn.setEnabled(enable);
        bfsBtn.setEnabled(enable);
        dfsBtn.setEnabled(enable);
        dijkstraBtn.setEnabled(enable);
        aStarBtn.setEnabled(enable);
        resetBtn.setEnabled(enable);
    }
}