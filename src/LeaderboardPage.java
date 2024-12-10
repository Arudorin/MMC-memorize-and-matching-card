// import model.User;
import javax.swing.*;

import java.awt.*;
import java.util.ArrayList;

public class LeaderboardPage {

    private JFrame leaderboardFrame;
    private JTextArea leaderboardArea;
    private DatabaseHandler dbHandler;

    public LeaderboardPage() {
        dbHandler = new DatabaseHandler(); // Inisialisasi DatabaseHandler

        leaderboardFrame = new JFrame("Leaderboard");
        leaderboardFrame.setSize(400, 300);
        // leaderboardFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Pastikan aplikasi berhenti saat frame ditutup
        leaderboardFrame.setLocationRelativeTo(null);

        leaderboardArea = new JTextArea(10, 30);
        leaderboardArea.setEditable(false);
        leaderboardArea.setFont(new Font("Arial", Font.PLAIN, 16));

        JScrollPane scrollPane = new JScrollPane(leaderboardArea);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadLeaderboardData());

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(refreshButton);

        leaderboardFrame.add(scrollPane, BorderLayout.CENTER);
        leaderboardFrame.add(bottomPanel, BorderLayout.SOUTH);

        loadLeaderboardData();

        leaderboardFrame.setVisible(true);
    }

    // Method to load leaderboard data from database
    private void loadLeaderboardData() {
        ArrayList<User> users = dbHandler.getUsers();
        StringBuilder leaderboardText = new StringBuilder("Leaderboard\n\n");
        for (User user : users) {
            leaderboardText.append(user.getName()).append(" - ").append(user.getScore()).append("\n");
        }
        leaderboardArea.setText(leaderboardText.toString());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LeaderboardPage::new);
    }
}
