import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            // Tampilkan story dialog dulu sebelum masuk ke game
            StoryDialog storyDialog = new StoryDialog();
            storyDialog.setVisible(true);
        });
    }
}