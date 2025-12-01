import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Mengatur agar tampilan mengikuti Sistem Operasi (Windows/Mac)
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            MazeGUI gui = new MazeGUI();
            // Mengatur agar window muncul di tengah layar
            gui.setLocationRelativeTo(null);
            gui.setVisible(true);
        });
    }
}