import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class MazeGUI extends JFrame {
    private MazePanel mazePanel;
    private MazeSolver solver;
    private Timer animationTimer;
    private SoundManager soundManager = new SoundManager();

    private JButton generateBtn, terrainBtn, bfsBtn, dfsBtn, dijkstraBtn, aStarBtn, resetBtn, compareAllBtn;
    private JSpinner widthSpinner, heightSpinner;
    private JLabel statsLabel;
    private JTable resultsTable;
    private DefaultTableModel tableModel;

    private final Color COLOR_BG = new Color(18, 18, 24);
    private final Color COLOR_SIDE = new Color(30, 30, 40);
    private final Color COLOR_ACCENT = new Color(212, 175, 55);

    public MazeGUI() {
        setTitle("🏰 Maze Adventure - Eldoria Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(COLOR_BG);

        createSideDashboard();
        createCenterPanel();
        createBottomStatusBar();

        soundManager.playBacksound("resources/backsound2.wav");

        setSize(1200, 800);
        setLocationRelativeTo(null);
    }

    @Override
    public void dispose() {
        soundManager.stopBacksound();
        super.dispose();
    }

    private void createSideDashboard() {
        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        sidePanel.setBackground(COLOR_SIDE);
        sidePanel.setBorder(new EmptyBorder(20, 15, 20, 15));
        sidePanel.setPreferredSize(new Dimension(320, 0));

        JLabel titleLabel = new JLabel("ELDORIA DASHBOARD");
        titleLabel.setFont(new Font("Georgia", Font.BOLD, 18));
        titleLabel.setForeground(COLOR_ACCENT);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidePanel.add(titleLabel);
        sidePanel.add(Box.createRigidArea(new Dimension(0, 25)));

        JPanel configContainer = new JPanel(new GridLayout(2, 2, 10, 10));
        configContainer.setOpaque(false);
        configContainer.setMaximumSize(new Dimension(270, 80));

        styleFieldLabel(configContainer, "Lebar:");
        widthSpinner = createCustomSpinner(30);
        configContainer.add(widthSpinner);

        styleFieldLabel(configContainer, "Tinggi:");
        heightSpinner = createCustomSpinner(25);
        configContainer.add(heightSpinner);

        sidePanel.add(configContainer);
        sidePanel.add(Box.createRigidArea(new Dimension(0, 20)));

        generateBtn = createStyledButton("🏰 BUAT LABIRIN", COLOR_ACCENT, Color.BLACK);
        generateBtn.addActionListener(e -> generateMaze());
        sidePanel.add(generateBtn);
        sidePanel.add(Box.createRigidArea(new Dimension(0, 8)));

        terrainBtn = createStyledButton("🌍 ACAK MEDAN", new Color(230, 230, 230), Color.BLACK);
        terrainBtn.addActionListener(e -> randomizeTerrain());
        sidePanel.add(terrainBtn);

        sidePanel.add(Box.createRigidArea(new Dimension(0, 30)));

        JPanel algoGrid = new JPanel(new GridLayout(2, 2, 8, 8));
        algoGrid.setOpaque(false);
        algoGrid.setMaximumSize(new Dimension(270, 90));

        bfsBtn = createStyledButton("BFS", Color.WHITE, Color.BLACK);
        dfsBtn = createStyledButton("DFS", Color.WHITE, Color.BLACK);
        dijkstraBtn = createStyledButton("DIJKSTRA", Color.WHITE, Color.BLACK);
        aStarBtn = createStyledButton("A*", Color.WHITE, Color.BLACK);

        bfsBtn.addActionListener(e -> solveMaze("BFS"));
        dfsBtn.addActionListener(e -> solveMaze("DFS"));
        dijkstraBtn.addActionListener(e -> solveMaze("Dijkstra"));
        aStarBtn.addActionListener(e -> solveMaze("A*"));

        algoGrid.add(bfsBtn); algoGrid.add(dfsBtn);
        algoGrid.add(dijkstraBtn); algoGrid.add(aStarBtn);
        sidePanel.add(algoGrid);

        sidePanel.add(Box.createRigidArea(new Dimension(0, 15)));

        compareAllBtn = createStyledButton("📊 BANDINGKAN SEMUA", new Color(180, 40, 40), Color.WHITE);
        compareAllBtn.addActionListener(e -> runComparison());
        sidePanel.add(compareAllBtn);

        sidePanel.add(Box.createRigidArea(new Dimension(0, 25)));

        createStyledTable();
        JScrollPane scrollPane = new JScrollPane(resultsTable);
        scrollPane.getViewport().setBackground(COLOR_SIDE);
        scrollPane.setBorder(new LineBorder(new Color(60, 60, 70)));
        scrollPane.setMaximumSize(new Dimension(270, 180));
        sidePanel.add(scrollPane);

        sidePanel.add(Box.createVerticalGlue());

        resetBtn = createStyledButton("🔄 RESET VISUAL", new Color(70, 70, 80), Color.WHITE);
        resetBtn.addActionListener(e -> resetAnimation());
        sidePanel.add(resetBtn);

        toggleButtons(false);
        add(sidePanel, BorderLayout.WEST);
    }

    private void solveMaze(String algorithm) {
        if (animationTimer != null) animationTimer.stop();
        mazePanel.resetAnimation();

        long startTime = System.nanoTime();
        List<Cell> path;
        Cell start = mazePanel.getStartCell();
        Cell end = mazePanel.getEndCell();

        if(algorithm.equals("BFS")) path = solver.solveBFS(start, end);
        else if(algorithm.equals("DFS")) path = solver.solveDFS(start, end);
        else if(algorithm.equals("Dijkstra")) path = solver.solveDijkstra(start, end);
        else path = solver.solveAStar(start, end);

        long endTime = System.nanoTime();
        double duration = (endTime - startTime) / 1_000_000.0;
        if (path.isEmpty()) { statsLabel.setText("Tidak ada jalan!"); return; }

        int totalCost = path.stream().mapToInt(c -> c.weight).sum();
        statsLabel.setText(algorithm + " | Waktu: " + String.format("%.2f", duration) + "ms | Energi: " + totalCost);
        updateTableRow(algorithm, path.size(), totalCost, duration);

        mazePanel.setPath(path);

        animationTimer = new Timer(20, e -> {
            mazePanel.incrementPathIndex();
            if (mazePanel.getPathDrawLimit() >= path.size()) {
                animationTimer.stop();
                soundManager.playSFX("resources/victory.wav"); // Pastikan file tersedia
                showVictoryDialog(algorithm, path.size(), totalCost);
            }
        });
        animationTimer.start();
    }

    private void showVictoryDialog(String algo, int steps, int cost) {
        LeaderboardManager.saveScore(algo, steps, cost);

        JDialog winDialog = new JDialog(this, "Mission Complete!", true);
        winDialog.setSize(500, 500);
        winDialog.setLocationRelativeTo(this);
        winDialog.setLayout(new BorderLayout());

        JPanel p = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setPaint(new GradientPaint(0, 0, new Color(25, 25, 45), 0, getHeight(), new Color(10, 10, 20)));
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(new EmptyBorder(25, 25, 25, 25));

        JLabel title = new JLabel("HARTA KARUN DITEMUKAN!");
        title.setForeground(COLOR_ACCENT);
        title.setFont(new Font("Georgia", Font.BOLD, 22));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextArea story = new JTextArea("\nAlex berhasil! Dengan bantuan algoritma " + algo +
                ", Alex melewati labirin berbahaya dan menemukan harta karun Eldoria. " +
                "Total energi yang Alex habiskan adalah " + cost + " unit.");
        story.setLineWrap(true); story.setWrapStyleWord(true);
        story.setOpaque(false); story.setEditable(false);
        story.setForeground(Color.WHITE);
        story.setFont(new Font("SansSerif", Font.ITALIC, 15));
        story.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lbTitle = new JLabel("\n--- TOP 3 ELDORIA LEADERS ---");
        lbTitle.setForeground(Color.YELLOW);
        lbTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        StringBuilder sb = new StringBuilder();
        List<String[]> scores = LeaderboardManager.getTopScores();
        for (int i = 0; i < Math.min(3, scores.size()); i++) {
            String[] s = scores.get(i);
            sb.append((i+1) + ". " + s[0] + " - Cost: " + s[2] + "\n");
        }
        JTextArea lbText = new JTextArea(sb.toString());
        lbText.setOpaque(false); lbText.setForeground(Color.CYAN);
        lbText.setFont(new Font("Monospaced", Font.BOLD, 14));
        lbText.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btn = new JButton("KEMBALI KE PETUALANGAN");
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.addActionListener(e -> winDialog.dispose());

        p.add(title); p.add(story); p.add(lbTitle); p.add(lbText);
        p.add(Box.createRigidArea(new Dimension(0, 20))); p.add(btn);
        winDialog.add(p);
        winDialog.setVisible(true);
    }

    private JButton createStyledButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setBackground(bg); btn.setForeground(fg);
        btn.setFocusPainted(false); btn.setOpaque(true);
        btn.setMaximumSize(new Dimension(270, 40));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        return btn;
    }

    private JSpinner createCustomSpinner(int val) {
        JSpinner s = new JSpinner(new SpinnerNumberModel(val, 5, 100, 1));
        return s;
    }

    private void createStyledTable() {
        String[] columns = {"Algo", "Langkah", "Cost", "Waktu"};
        tableModel = new DefaultTableModel(columns, 0);
        resultsTable = new JTable(tableModel);
        resultsTable.setBackground(new Color(40, 40, 50));
        resultsTable.setForeground(Color.WHITE);
    }

    private void styleFieldLabel(JPanel p, String txt) {
        JLabel l = new JLabel(txt);
        l.setForeground(new Color(255, 215, 0));
        p.add(l);
    }

    private void createBottomStatusBar() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 8));
        p.setBackground(new Color(15, 15, 20));
        statsLabel = new JLabel("Siapkan petualanganmu...");
        statsLabel.setForeground(Color.WHITE);
        p.add(statsLabel);
        add(p, BorderLayout.SOUTH);
    }

    private void generateMaze() {
        int w = (int) widthSpinner.getValue();
        int h = (int) heightSpinner.getValue();
        mazePanel.setMaze(new MazeGenerator(w, h).generateMaze());
        solver = new MazeSolver(mazePanel.getGrid());
        toggleButtons(true);
        tableModel.setRowCount(0);
    }

    private void runComparison() {
        if (mazePanel.getGrid() == null) return;
        tableModel.setRowCount(0);
        String[] algos = {"BFS", "DFS", "Dijkstra", "A*"};
        Cell start = mazePanel.getStartCell();
        Cell end = mazePanel.getEndCell();
        for (String algo : algos) {
            long st = System.nanoTime();
            List<Cell> path = algo.equals("BFS") ? solver.solveBFS(start, end) :
                    algo.equals("DFS") ? solver.solveDFS(start, end) :
                            algo.equals("Dijkstra") ? solver.solveDijkstra(start, end) :
                                    solver.solveAStar(start, end);
            double d = (System.nanoTime() - st) / 1_000_000.0;
            if (!path.isEmpty()) {
                int c = path.stream().mapToInt(cl -> cl.weight).sum();
                tableModel.addRow(new Object[]{algo, path.size(), c, String.format("%.2f", d)});
            }
        }
    }

    private void randomizeTerrain() {
        if (mazePanel.getGrid() == null) return;
        for (Cell[] row : mazePanel.getGrid()) for (Cell c : row) c.setRandomTerrain();
        mazePanel.repaint(); tableModel.setRowCount(0);
    }

    private void resetAnimation() {
        if (animationTimer != null) animationTimer.stop();
        mazePanel.resetAnimation();
    }

    private void updateTableRow(String a, int s, int c, double t) {
        tableModel.addRow(new Object[]{a, s, c, String.format("%.2f", t)});
    }

    private void toggleButtons(boolean b) {
        JButton[] btns = {terrainBtn, bfsBtn, dfsBtn, dijkstraBtn, aStarBtn, resetBtn, compareAllBtn};
        for(JButton btn : btns) btn.setEnabled(b);
    }

    private void createCenterPanel() {
        mazePanel = new MazePanel();
        add(mazePanel, BorderLayout.CENTER);
    }
}