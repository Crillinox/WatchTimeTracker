import java.io.*;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;

public class WatchCounter {

    public static void main(String[] args) throws URISyntaxException {
        String jarDir = new File(WatchCounter.class.getProtectionDomain()
                .getCodeSource().getLocation().toURI()).getParent();

        String ytPath    = jarDir + File.separator + "watchHistory.txt";
        String animePath = jarDir + File.separator + "animeHistory.txt";

        long totalSeconds = 0;
        int ytCount = 0;
        int animeEpisodes = 0;
        StringBuilder output = new StringBuilder();

        // --- Parse YouTube history ---
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(ytPath), "UTF-8"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                long secs = parseDuration(line);
                if (secs >= 0) {
                    totalSeconds += secs;
                    ytCount++;
                }
            }
        } catch (IOException e) {
            output.append("Error reading watchHistory.txt: ").append(e.getMessage()).append("\n");
        }

        // --- Parse anime history ---
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(animePath), "UTF-8"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split("\\s+", 2);
                if (parts.length < 2) continue;
                try {
                    int epLengthMins = Integer.parseInt(parts[0].trim());
                    int epCount      = Integer.parseInt(parts[1].trim());
                    totalSeconds += (long) epLengthMins * epCount * 60;
                    animeEpisodes += epCount;
                } catch (NumberFormatException ignored) {}
            }
        } catch (IOException e) {
            output.append("Error reading animeHistory.txt: ").append(e.getMessage()).append("\n");
        }

        // --- Format total time ---
        long hours   = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

        output.append("===========================================\n");
        output.append("           WATCH TIME COUNTER             \n");
        output.append("===========================================\n");
        output.append("  YouTube Videos  : ").append(ytCount).append(" videos\n");
        output.append("  Anime Episodes  : ").append(animeEpisodes).append(" episodes\n");
        output.append("-------------------------------------------\n");
        output.append(String.format("  Total Time : %d hrs  %d mins  %d secs%n", hours, minutes, seconds));
        output.append(String.format("  That's     : %.1f days of your life%n", totalSeconds / 86400.0));
        output.append("===========================================\n");

        JFrame frame = new JFrame("Watch Counter");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 300);

        JTextArea textArea = new JTextArea(output.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Courier New", Font.PLAIN, 14));
        textArea.setBackground(new Color(30, 30, 30));
        textArea.setForeground(new Color(212, 212, 212));
        textArea.setCaretColor(Color.WHITE);
        textArea.setMargin(new Insets(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(null);

        frame.add(scrollPane);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // Parses H:MM:SS or M:SS into total seconds
    private static long parseDuration(String s) {
        try {
            String[] parts = s.split(":");
            if (parts.length == 3) {
                return Long.parseLong(parts[0]) * 3600
                     + Long.parseLong(parts[1]) * 60
                     + Long.parseLong(parts[2]);
            } else if (parts.length == 2) {
                return Long.parseLong(parts[0]) * 60
                     + Long.parseLong(parts[1]);
            }
        } catch (NumberFormatException ignored) {}
        return -1;
    }
}