import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MazeGUI gui = new MazeGUI();
            gui.setVisible(true);
        });
    }
}