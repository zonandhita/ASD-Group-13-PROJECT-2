import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MazeGUI extends JFrame {
    private MazePanel mazePanel;
    private MazeSolver solver;
    private Timer animationTimer;

    private JButton generateBtn, terrainBtn, bfsBtn, dfsBtn, dijkstraBtn, aStarBtn, resetBtn;
    private JSpinner widthSpinner, heightSpinner;
    private JLabel statsLabel;

    public MazeGUI() {
        setTitle("🏰 Maze Adventure - The Lost Treasure of Eldoria");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(25, 25, 35));

        createTopPanel();
        createCenterPanel();
        createBottomPanel();

        pack();
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    private void createTopPanel() {
        JPanel topPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        topPanel.setBackground(new Color(35, 35, 50));
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // Row 1: Settings
        JPanel settingsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 5));
        settingsPanel.setOpaque(false);

        styleLabel(settingsPanel, "Width:");
        widthSpinner = new JSpinner(new SpinnerNumberModel(30, 10, 60, 1));
        settingsPanel.add(widthSpinner);

        styleLabel(settingsPanel, "Height:");
        heightSpinner = new JSpinner(new SpinnerNumberModel(25, 10, 50, 1));
        settingsPanel.add(heightSpinner);

        generateBtn = createEldoriaButton("🏰 BUAT LABIRIN", new Color(212, 175, 55));
        generateBtn.addActionListener(e -> generateMaze());
        settingsPanel.add(generateBtn);

        terrainBtn = createEldoriaButton("🌍 ACAK MEDAN", new Color(46, 139, 87));
        terrainBtn.setEnabled(false);
        terrainBtn.addActionListener(e -> randomizeTerrain());
        settingsPanel.add(terrainBtn);

        // Row 2: Algorithms
        JPanel algoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 5));
        algoPanel.setOpaque(false);

        bfsBtn = createEldoriaButton("🔵 BFS", new Color(100, 149, 237));
        dfsBtn = createEldoriaButton("🟣 DFS", new Color(138, 43, 226));
        dijkstraBtn = createEldoriaButton("🟠 DIJKSTRA", new Color(210, 105, 30));
        aStarBtn = createEldoriaButton("⭐ A*", new Color(218, 165, 32));
        resetBtn = createEldoriaButton("🔄 RESET", new Color(178, 34, 34));

        JButton[] algos = {bfsBtn, dfsBtn, dijkstraBtn, aStarBtn, resetBtn};
        for(JButton b : algos) {
            b.setEnabled(false);
            algoPanel.add(b);
        }

        bfsBtn.addActionListener(e -> solveMaze("BFS"));
        dfsBtn.addActionListener(e -> solveMaze("DFS"));
        dijkstraBtn.addActionListener(e -> solveMaze("Dijkstra"));
        aStarBtn.addActionListener(e -> solveMaze("A*"));
        resetBtn.addActionListener(e -> resetAnimation());

        topPanel.add(settingsPanel);
        topPanel.add(algoPanel);
        add(topPanel, BorderLayout.NORTH);
    }

    private JButton createEldoriaButton(String text, Color baseColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(baseColor);
        // PERBAIKAN: Font diubah menjadi Hitam agar kelihatan
        btn.setForeground(Color.BLACK);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 215, 0), 1),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) { btn.setBackground(baseColor.brighter()); }
            public void mouseExited(java.awt.event.MouseEvent evt) { btn.setBackground(baseColor); }
        });
        return btn;
    }

    private void styleLabel(JPanel p, String txt) {
        JLabel l = new JLabel(txt);
        l.setForeground(new Color(212, 175, 55));
        l.setFont(new Font("Georgia", Font.BOLD, 14));
        p.add(l);
    }

    private void createCenterPanel() {
        mazePanel = new MazePanel();
        JScrollPane scrollPane = new JScrollPane(mazePanel);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(212, 175, 55), 2));
        scrollPane.getViewport().setBackground(new Color(15, 15, 25));
        add(scrollPane, BorderLayout.CENTER);
    }

    private void createBottomPanel() {
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(20, 20, 30));
        statsLabel = new JLabel("Siap berpetualang, Alex? Buat labirin dulu!");
        statsLabel.setForeground(Color.WHITE);
        statsLabel.setFont(new Font("Monospaced", Font.BOLD, 16));
        bottomPanel.add(statsLabel);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void generateMaze() {
        int w = (int) widthSpinner.getValue();
        int h = (int) heightSpinner.getValue();
        mazePanel.setMaze(new MazeGenerator(w, h).generateMaze());
        solver = new MazeSolver(mazePanel.getGrid());
        toggleButtons(true);
        statsLabel.setText("Labirin Eldoria terwujud! (" + w + "x" + h + ")");
        if(animationTimer != null) animationTimer.stop();
    }

    private void solveMaze(String algorithm) {
        if (animationTimer != null) animationTimer.stop();
        mazePanel.resetAnimation();
        List<Cell> path;
        Cell start = mazePanel.getStartCell();
        Cell end = mazePanel.getEndCell();

        if(algorithm.equals("BFS")) path = solver.solveBFS(start, end);
        else if(algorithm.equals("DFS")) path = solver.solveDFS(start, end);
        else if(algorithm.equals("Dijkstra")) path = solver.solveDijkstra(start, end);
        else path = solver.solveAStar(start, end);

        if (path.isEmpty()) { statsLabel.setText("Tidak ada jalan!"); return; }

        int totalCost = path.stream().mapToInt(c -> c.weight).sum();
        statsLabel.setText(algorithm + " | Langkah: " + path.size() + " | Energi: " + totalCost);
        mazePanel.setPath(path);
        animationTimer = new Timer(30, e -> mazePanel.incrementPathIndex());
        animationTimer.start();
    }

    private void randomizeTerrain() {
        for (Cell[] row : mazePanel.getGrid()) {
            for (Cell c : row) c.setRandomTerrain();
        }
        mazePanel.repaint();
    }

    private void resetAnimation() {
        if (animationTimer != null) animationTimer.stop();
        mazePanel.resetAnimation();
    }

    private void toggleButtons(boolean b) {
        JButton[] btns = {terrainBtn, bfsBtn, dfsBtn, dijkstraBtn, aStarBtn, resetBtn};
        for(JButton btn : btns) btn.setEnabled(b);
    }
}