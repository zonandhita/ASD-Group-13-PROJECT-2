import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Menyesuaikan tampilan grafis (UI) agar serupa dengan tema OS yang digunakan (Windows/macOS/Linux)
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Mencatat error jika tema sistem gagal dimuat
            e.printStackTrace();
        }

        // Menjalankan pembuatan antarmuka di thread khusus (Event Dispatch Thread) agar aplikasi responsif
        SwingUtilities.invokeLater(() -> {
            MazeGUI gui = new MazeGUI();

            // Memastikan jendela muncul tepat di tengah layar monitor saat dijalankan
            gui.setLocationRelativeTo(null);

            // Menampilkan jendela aplikasi ke layar
            gui.setVisible(true);
        });
    }
}