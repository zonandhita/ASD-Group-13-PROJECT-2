import javax.swing.*;
import java.awt.*;

public class MazeGUI extends JFrame {
    private MazePanel mazePanel;
    private Cell[][] grid;
    private MazeSolver solver;
    private Timer animationTimer;
    private JButton generateBtn, bfsBtn, dfsBtn, resetBtn;
    private JSpinner widthSpinner, heightSpinner;

    public MazeGUI() {
        setTitle("Maze Generator & Solver - Prim's Algorithm");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Control Panel
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());

        controlPanel.add(new JLabel("Width:"));
        widthSpinner = new JSpinner(new SpinnerNumberModel(15, 5, 30, 1));
        controlPanel.add(widthSpinner);

        controlPanel.add(new JLabel("Height:"));
        heightSpinner = new JSpinner(new SpinnerNumberModel(15, 5, 30, 1));
        controlPanel.add(heightSpinner);

        generateBtn = new JButton("Generate Maze");
        generateBtn.addActionListener(e -> generateMaze());
        controlPanel.add(generateBtn);

        bfsBtn = new JButton("Solve BFS");
        bfsBtn.setEnabled(false);
        bfsBtn.addActionListener(e -> solveMaze("BFS"));
        controlPanel.add(bfsBtn);

        dfsBtn = new JButton("Solve DFS");
        dfsBtn.setEnabled(false);
        dfsBtn.addActionListener(e -> solveMaze("DFS"));
        controlPanel.add(dfsBtn);

        resetBtn = new JButton("Reset Animation");
        resetBtn.setEnabled(false);
        resetBtn.addActionListener(e -> resetAnimation());
        controlPanel.add(resetBtn);

        add(controlPanel, BorderLayout.NORTH);

        // Maze Panel
        mazePanel = new MazePanel();
        add(mazePanel, BorderLayout.CENTER);

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
        bfsBtn.setEnabled(true);
        dfsBtn.setEnabled(true);
        resetBtn.setEnabled(false);

        if (animationTimer != null && animationTimer.isRunning()) {
            animationTimer.stop();
        }
    }

    private void solveMaze(String algorithm) {
        if (grid == null) return;

        Cell start = grid[0][0];
        Cell end = grid[grid.length - 1][grid[0].length - 1];

        java.util.List<Cell> path;
        if (algorithm.equals("BFS")) {
            path = solver.solveBFS(start, end);
        } else {
            path = solver.solveDFS(start, end);
        }

        mazePanel.setPath(path);
        resetBtn.setEnabled(true);

        // Animate path
        if (animationTimer != null && animationTimer.isRunning()) {
            animationTimer.stop();
        }

        animationTimer = new Timer(50, e -> {
            mazePanel.animateStep();
        });
        animationTimer.start();

        // Stop animation when done
        Timer stopTimer = new Timer(50 * path.size() + 100, e -> {
            if (animationTimer != null) {
                animationTimer.stop();
            }
        });
        stopTimer.setRepeats(false);
        stopTimer.start();
    }

    private void resetAnimation() {
        mazePanel.resetAnimation();
    }
}