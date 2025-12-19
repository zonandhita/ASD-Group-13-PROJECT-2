import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class MazeGUI extends JFrame {
    private MazePanel mazePanel;
    private MazeSolver solver;
    private Timer animationTimer;
    private SoundManager soundManager = new SoundManager(); // Inisialisasi SoundManager

    private JButton generateBtn, terrainBtn, bfsBtn, dfsBtn, dijkstraBtn, aStarBtn, resetBtn, compareAllBtn;
    private JSpinner widthSpinner, heightSpinner;
    private JLabel statsLabel;
    private JTable resultsTable;
    private DefaultTableModel tableModel;

    // Palet Warna Kontras
    private final Color COLOR_BG = new Color(18, 18, 24);
    private final Color COLOR_SIDE = new Color(30, 30, 40);
    private final Color COLOR_ACCENT = new Color(212, 175, 55); // Emas
    private final Color COLOR_BTN_LIGHT = new Color(230, 230, 230); // Abu-abu Terang
    private final Color COLOR_TEXT_GOLD = new Color(255, 215, 0); // Emas Terang untuk Label

    public MazeGUI() {
        setTitle("🏰 Maze Adventure - Eldoria Statistics");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(COLOR_BG);

        createSideDashboard();
        createCenterPanel();
        createBottomStatusBar();

        // MULAI MEMUTAR BACKSOUND SAAT APLIKASI DIBUKA
        // Pastikan file backsound2.wav ada di folder resources
        soundManager.playBacksound("resources/backsound2.wav");

        setSize(1200, 800);
        setLocationRelativeTo(null);
    }

    // Menghentikan musik saat jendela ditutup
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
        sidePanel.setPreferredSize(new Dimension(300, 0));

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

        terrainBtn = createStyledButton("🌍 ACAK MEDAN", COLOR_BTN_LIGHT, Color.BLACK);
        terrainBtn.addActionListener(e -> randomizeTerrain());
        sidePanel.add(terrainBtn);

        sidePanel.add(Box.createRigidArea(new Dimension(0, 30)));

        JLabel algoHeader = new JLabel("ALGORITMA PENCARIAN");
        algoHeader.setFont(new Font("SansSerif", Font.BOLD, 13));
        algoHeader.setForeground(COLOR_TEXT_GOLD);
        algoHeader.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidePanel.add(algoHeader);
        sidePanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JPanel algoGrid = new JPanel(new GridLayout(2, 2, 8, 8));
        algoGrid.setOpaque(false);
        algoGrid.setMaximumSize(new Dimension(270, 90));

        bfsBtn = createStyledButton("BFS", COLOR_BTN_LIGHT, Color.BLACK);
        dfsBtn = createStyledButton("DFS", COLOR_BTN_LIGHT, Color.BLACK);
        dijkstraBtn = createStyledButton("DIJKSTRA", COLOR_BTN_LIGHT, Color.BLACK);
        aStarBtn = createStyledButton("A*", COLOR_BTN_LIGHT, Color.BLACK);

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

    private JButton createStyledButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        btn.setMaximumSize(new Dimension(270, 40));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JSpinner createCustomSpinner(int val) {
        JSpinner spinner = new JSpinner(new SpinnerNumberModel(val, 5, 100, 1));
        JComponent editor = spinner.getEditor();
        JFormattedTextField tf = ((JSpinner.DefaultEditor) editor).getTextField();
        tf.setForeground(Color.BLACK);
        tf.setBackground(Color.WHITE);
        tf.setFont(new Font("SansSerif", Font.BOLD, 12));
        return spinner;
    }

    private void createStyledTable() {
        String[] columns = {"Algo", "Langkah", "Cost", "Waktu"};
        tableModel = new DefaultTableModel(columns, 0);
        resultsTable = new JTable(tableModel);
        resultsTable.setBackground(new Color(40, 40, 50));
        resultsTable.setForeground(Color.WHITE);
        resultsTable.setRowHeight(25);

        resultsTable.getTableHeader().setBackground(COLOR_ACCENT);
        resultsTable.getTableHeader().setForeground(Color.BLACK);
        resultsTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < 4; i++) resultsTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
    }

    private void styleFieldLabel(JPanel p, String txt) {
        JLabel l = new JLabel(txt);
        l.setForeground(COLOR_TEXT_GOLD);
        l.setFont(new Font("SansSerif", Font.BOLD, 12));
        p.add(l);
    }

    private void createBottomStatusBar() {
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 8));
        statusPanel.setBackground(new Color(15, 15, 20));
        statsLabel = new JLabel("Siapkan petualanganmu...");
        statsLabel.setForeground(Color.WHITE);
        statsLabel.setFont(new Font("Monospaced", Font.BOLD, 13));
        statusPanel.add(statsLabel);
        add(statusPanel, BorderLayout.SOUTH);
    }

    private void generateMaze() {
        int w = (int) widthSpinner.getValue();
        int h = (int) heightSpinner.getValue();
        mazePanel.setMaze(new MazeGenerator(w, h).generateMaze());
        solver = new MazeSolver(mazePanel.getGrid());
        toggleButtons(true);
        tableModel.setRowCount(0);
        statsLabel.setText("Labirin Eldoria terwujud! (" + w + "x" + h + ")");
        if(animationTimer != null) animationTimer.stop();
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
        animationTimer = new Timer(20, e -> mazePanel.incrementPathIndex());
        animationTimer.start();
    }

    private void runComparison() {
        if (mazePanel.getGrid() == null) return;
        tableModel.setRowCount(0);
        resetAnimation();
        String[] algos = {"BFS", "DFS", "Dijkstra", "A*"};
        Cell start = mazePanel.getStartCell();
        Cell end = mazePanel.getEndCell();
        for (String algo : algos) {
            long startTime = System.nanoTime();
            List<Cell> path;
            if (algo.equals("BFS")) path = solver.solveBFS(start, end);
            else if (algo.equals("DFS")) path = solver.solveDFS(start, end);
            else if (algo.equals("Dijkstra")) path = solver.solveDijkstra(start, end);
            else path = solver.solveAStar(start, end);
            long endTime = System.nanoTime();
            double duration = (endTime - startTime) / 1_000_000.0;
            if (!path.isEmpty()) {
                int cost = path.stream().mapToInt(c -> c.weight).sum();
                tableModel.addRow(new Object[]{algo, path.size(), cost, String.format("%.2f", duration)});
            }
        }
    }

    private void randomizeTerrain() {
        if (mazePanel.getGrid() == null) return;
        for (Cell[] row : mazePanel.getGrid()) {
            for (Cell c : row) c.setRandomTerrain();
        }
        mazePanel.repaint();
        tableModel.setRowCount(0);
    }

    private void resetAnimation() {
        if (animationTimer != null) animationTimer.stop();
        mazePanel.resetAnimation();
    }

    private void updateTableRow(String algo, int steps, int cost, double time) {
        boolean found = false;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            if (tableModel.getValueAt(i, 0).equals(algo)) {
                tableModel.setValueAt(steps, i, 1);
                tableModel.setValueAt(cost, i, 2);
                tableModel.setValueAt(String.format("%.2f", time), i, 3);
                found = true; break;
            }
        }
        if (!found) tableModel.addRow(new Object[]{algo, steps, cost, String.format("%.2f", time)});
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