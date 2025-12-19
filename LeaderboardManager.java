import java.io.*;
import java.util.*;

public class LeaderboardManager {
    private static final String FILE_NAME = "leaderboard.txt";

    public static void saveScore(String algo, int steps, int cost) {
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(FILE_NAME, true)))) {
            out.println(algo + "," + steps + "," + cost);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String[]> getTopScores() {
        List<String[]> scores = new ArrayList<>();
        File file = new File(FILE_NAME);
        if (!file.exists()) return scores;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                scores.add(line.split(","));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Urutkan berdasarkan cost terkecil
        scores.sort(Comparator.comparingInt(a -> Integer.parseInt(a[2])));
        return scores;
    }
}