import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public class StoryDialog extends JDialog {
    private int storyIndex = 0;
    private StoryPanel storyPanel;
    private JButton nextButton;

    private String[][] storyData = {
            {
                    "THE LOST TREASURE OF ELDORIA",
                    "You are Alex, a brave adventurer who has discovered\n" +
                            "an ancient map leading to the legendary Lost Treasure\n" +
                            "of Eldoria.\n\n" +
                            "The treasure is hidden deep within a mystical maze\n" +
                            "that changes its paths based on different terrains..."
            },
            {
                    "THE CHALLENGE",
                    "The maze contains three types of terrain:\n\n" +
                            "🟢 GRASS - Easy to walk through (Cost: 1)\n" +
                            "🟤 MUD - Slows you down significantly (Cost: 5)\n" +
                            "🔵 WATER - Very difficult to cross (Cost: 10)\n\n" +
                            "Each step costs energy based on the terrain type!"
            },
            {
                    "YOUR MISSION",
                    "Navigate from the GREEN START to the RED TREASURE!\n\n" +
                            "Choose your pathfinding algorithm wisely:\n\n" +
                            "• BFS - Finds shortest path by number of steps\n" +
                            "• DFS - Explores deeply before backtracking\n" +
                            "• DIJKSTRA - Finds cheapest terrain path\n" +
                            "• A* - Intelligent pathfinding with heuristics\n\n" +
                            "Good luck, adventurer!"
            }
    };

    public StoryDialog() {
        setTitle("The Lost Treasure of Eldoria");
        setModal(true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(850, 600);
        setLocationRelativeTo(null);
        setResizable(false);

        storyPanel = new StoryPanel();
        add(storyPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(20, 20, 30));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 20, 0));

        nextButton = new JButton("NEXT");
        nextButton.setFont(new Font("Arial", Font.BOLD, 22));
        nextButton.setPreferredSize(new Dimension(220, 55));
        nextButton.setBackground(new Color(255, 215, 0));
        nextButton.setForeground(Color.BLACK);
        nextButton.setFocusPainted(false);
        nextButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 180, 0), 3),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        nextButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        nextButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                nextButton.setBackground(new Color(255, 235, 100));
                nextButton.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(255, 200, 50), 4),
                        BorderFactory.createEmptyBorder(5, 15, 5, 15)
                ));
            }
            public void mouseExited(MouseEvent e) {
                nextButton.setBackground(new Color(255, 215, 0));
                nextButton.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(255, 180, 0), 3),
                        BorderFactory.createEmptyBorder(5, 15, 5, 15)
                ));
            }
        });

        nextButton.addActionListener(e -> nextStory());
        buttonPanel.add(nextButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void nextStory() {
        storyIndex++;
        if (storyIndex < storyData.length) {
            storyPanel.setStoryIndex(storyIndex);
            if (storyIndex == storyData.length - 1) {
                nextButton.setText("START ADVENTURE!");
                nextButton.setFont(new Font("Arial", Font.BOLD, 20));
            }
        } else {
            dispose();
            SwingUtilities.invokeLater(() -> {
                MazeGUI gui = new MazeGUI();
                gui.setLocationRelativeTo(null);
                gui.setVisible(true);
            });
        }
    }

    class StoryPanel extends JPanel {
        private int currentStory = 0;

        public StoryPanel() {
            setBackground(new Color(20, 20, 30));
        }

        public void setStoryIndex(int index) {
            currentStory = index;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            // Draw animated background
            drawAnimatedBackground(g2d, w, h);

            // Draw icon based on story
            drawStoryIcon(g2d, w, h);

            // Draw title with shadow
            g2d.setColor(new Color(0, 0, 0, 100));
            g2d.setFont(new Font("Arial", Font.BOLD, 38));
            String title = storyData[currentStory][0];
            FontMetrics fm = g2d.getFontMetrics();
            int titleY = 130;
            g2d.drawString(title, (w - fm.stringWidth(title)) / 2 + 3, titleY + 3);

            g2d.setColor(new Color(255, 215, 0));
            g2d.drawString(title, (w - fm.stringWidth(title)) / 2, titleY);

            // Draw decorative line
            g2d.setStroke(new BasicStroke(3));
            int lineWidth = 450;
            GradientPaint gradient = new GradientPaint(
                    (w - lineWidth) / 2, titleY + 15,
                    new Color(255, 215, 0, 0),
                    w / 2, titleY + 15,
                    new Color(255, 215, 0, 255)
            );
            g2d.setPaint(gradient);
            g2d.drawLine((w - lineWidth) / 2, titleY + 15, w / 2, titleY + 15);

            gradient = new GradientPaint(
                    w / 2, titleY + 15,
                    new Color(255, 215, 0, 255),
                    (w + lineWidth) / 2, titleY + 15,
                    new Color(255, 215, 0, 0)
            );
            g2d.setPaint(gradient);
            g2d.drawLine(w / 2, titleY + 15, (w + lineWidth) / 2, titleY + 15);

            // Draw content
            String content = storyData[currentStory][1];
            String[] lines = content.split("\n");

            g2d.setFont(new Font("Arial", Font.PLAIN, 19));
            g2d.setColor(new Color(230, 230, 255));

            int startY = 210;
            int lineHeight = 32;

            for (int i = 0; i < lines.length; i++) {
                String line = lines[i];
                if (line.trim().isEmpty()) continue;

                // Highlight keywords
                if (line.contains("GRASS") || line.contains("MUD") || line.contains("WATER") || line.contains("🟢") || line.contains("🟤") || line.contains("🔵")) {
                    drawHighlightedLine(g2d, line, w, startY + i * lineHeight);
                } else if (line.contains("BFS") || line.contains("DFS") || line.contains("DIJKSTRA") || line.contains("A*")) {
                    drawAlgorithmLine(g2d, line, w, startY + i * lineHeight);
                } else {
                    fm = g2d.getFontMetrics();
                    g2d.drawString(line, (w - fm.stringWidth(line)) / 2, startY + i * lineHeight);
                }
            }
        }

        private void drawAnimatedBackground(Graphics2D g2d, int w, int h) {
            // Draw gradient background
            GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(30, 30, 50),
                    0, h, new Color(20, 20, 30)
            );
            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, w, h);

            // Draw stars
            g2d.setColor(new Color(255, 255, 200, 120));
            for (int i = 0; i < 60; i++) {
                int x = (i * 137) % w;
                int y = (i * 211) % h;
                int size = 1 + (i % 3);
                g2d.fillOval(x, y, size, size);
            }

            // Draw glow circles
            for (int i = 0; i < 3; i++) {
                int x = ((i + 1) * 250) % w;
                int y = ((i + 1) * 180) % h;
                RadialGradientPaint radial = new RadialGradientPaint(
                        x, y, 100,
                        new float[]{0f, 1f},
                        new Color[]{new Color(100, 100, 200, 30), new Color(100, 100, 200, 0)}
                );
                g2d.setPaint(radial);
                g2d.fillOval(x - 100, y - 100, 200, 200);
            }
        }

        private void drawStoryIcon(Graphics2D g2d, int w, int h) {
            int iconSize = 70;
            int iconX = w / 2 - iconSize / 2;
            int iconY = 35;

            if (currentStory == 0) {
                // Castle icon with shadow
                g2d.setColor(new Color(0, 0, 0, 80));
                g2d.fillRect(iconX + 12, iconY + 22, 17, 33);
                g2d.fillRect(iconX + 37, iconY + 22, 17, 33);

                g2d.setColor(new Color(255, 215, 0));
                g2d.fillRect(iconX + 10, iconY + 20, 17, 33);
                g2d.fillRect(iconX + 35, iconY + 20, 17, 33);
                g2d.fillRect(iconX + 5, iconY + 30, 55, 23);
                g2d.fillRect(iconX + 20, iconY + 10, 23, 17);

                // Tower tops
                int[] xp1 = {iconX+10, iconX+18, iconX+27};
                int[] yp1 = {iconY+20, iconY+3, iconY+20};
                g2d.fillPolygon(xp1, yp1, 3);

                int[] xp2 = {iconX+35, iconX+43, iconX+52};
                int[] yp2 = {iconY+20, iconY+3, iconY+20};
                g2d.fillPolygon(xp2, yp2, 3);

            } else if (currentStory == 1) {
                // Warning triangle with glow
                RadialGradientPaint glow = new RadialGradientPaint(
                        iconX + 30, iconY + 30, 40,
                        new float[]{0f, 1f},
                        new Color[]{new Color(255, 100, 100, 100), new Color(255, 100, 100, 0)}
                );
                g2d.setPaint(glow);
                g2d.fillOval(iconX - 10, iconY - 10, 90, 90);

                g2d.setColor(new Color(255, 100, 100));
                int[] xPoints = {iconX + 32, iconX + 5, iconX + 59};
                int[] yPoints = {iconY + 5, iconY + 60, iconY + 60};
                g2d.fillPolygon(xPoints, yPoints, 3);

                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.BOLD, 35));
                g2d.drawString("!", iconX + 26, iconY + 50);

            } else {
                // Target/treasure icon
                g2d.setColor(new Color(255, 0, 0));
                g2d.fillOval(iconX, iconY, iconSize, iconSize);
                g2d.setColor(new Color(255, 215, 0));
                g2d.fillOval(iconX + 10, iconY + 10, iconSize - 20, iconSize - 20);
                g2d.setColor(new Color(255, 0, 0));
                g2d.fillOval(iconX + 20, iconY + 20, iconSize - 40, iconSize - 40);
                g2d.setColor(new Color(255, 215, 0));
                g2d.fillOval(iconX + 27, iconY + 27, iconSize - 54, iconSize - 54);
            }
        }

        private void drawHighlightedLine(Graphics2D g2d, String line, int w, int y) {
            FontMetrics fm = g2d.getFontMetrics();
            int x = (w - fm.stringWidth(line)) / 2;

            if (line.contains("GRASS") || line.contains("🟢")) {
                String keyword = line.contains("GRASS") ? "GRASS" : "🟢";
                drawColoredKeyword(g2d, line, keyword, new Color(100, 255, 100), x, y);
            } else if (line.contains("MUD") || line.contains("🟤")) {
                String keyword = line.contains("MUD") ? "MUD" : "🟤";
                drawColoredKeyword(g2d, line, keyword, new Color(160, 120, 80), x, y);
            } else if (line.contains("WATER") || line.contains("🔵")) {
                String keyword = line.contains("WATER") ? "WATER" : "🔵";
                drawColoredKeyword(g2d, line, keyword, new Color(100, 200, 255), x, y);
            } else {
                g2d.drawString(line, x, y);
            }
        }

        private void drawAlgorithmLine(Graphics2D g2d, String line, int w, int y) {
            FontMetrics fm = g2d.getFontMetrics();
            int x = (w - fm.stringWidth(line)) / 2;

            if (line.contains("BFS")) {
                drawColoredKeyword(g2d, line, "BFS", new Color(100, 149, 237), x, y);
            } else if (line.contains("DFS")) {
                drawColoredKeyword(g2d, line, "DFS", new Color(138, 43, 226), x, y);
            } else if (line.contains("DIJKSTRA")) {
                drawColoredKeyword(g2d, line, "DIJKSTRA", new Color(255, 140, 0), x, y);
            } else if (line.contains("A*")) {
                drawColoredKeyword(g2d, line, "A*", new Color(255, 215, 0), x, y);
            } else {
                g2d.drawString(line, x, y);
            }
        }

        private void drawColoredKeyword(Graphics2D g2d, String line, String keyword, Color color, int x, int y) {
            int keywordIndex = line.indexOf(keyword);
            if (keywordIndex >= 0) {
                String before = line.substring(0, keywordIndex);
                String after = line.substring(keywordIndex + keyword.length());

                FontMetrics fm = g2d.getFontMetrics();
                g2d.setColor(new Color(230, 230, 255));
                g2d.drawString(before, x, y);

                int keywordX = x + fm.stringWidth(before);
                g2d.setColor(color);
                Font boldFont = new Font("Arial", Font.BOLD, 19);
                g2d.setFont(boldFont);
                g2d.drawString(keyword, keywordX, y);

                int afterX = keywordX + g2d.getFontMetrics().stringWidth(keyword);
                g2d.setColor(new Color(230, 230, 255));
                g2d.setFont(new Font("Arial", Font.PLAIN, 19));
                g2d.drawString(after, afterX, y);
            } else {
                g2d.drawString(line, x, y);
            }
        }
    }
}