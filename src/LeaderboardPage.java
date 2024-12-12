import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class LeaderboardPage {
    private JFrame leaderboardFrame;
    private JPanel leaderboardPanel;
    private DatabaseHandler dbHandler;

    private CardLayout cardLayout;
    private JPanel mainPanel;

    // Pass in the CardLayout and mainPanel from your main application (e.g., MatchCard or MainFrame)
    public LeaderboardPage(CardLayout cardLayout, JPanel mainPanel) {
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;
        dbHandler = new DatabaseHandler(); // Initialize DatabaseHandler

        leaderboardFrame = new JFrame("Leaderboard");
        leaderboardFrame.setLayout(new BorderLayout());
        leaderboardFrame.setSize(350, 400);
        leaderboardFrame.setLocationRelativeTo(null);
        leaderboardFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Create panel for content
        leaderboardPanel = new JPanel();
        leaderboardPanel.setLayout(new BoxLayout(leaderboardPanel, BoxLayout.Y_AXIS));
        leaderboardPanel.setBackground(new Color(245, 245, 245)); // Light background color
        leaderboardPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Panel padding

        // Leaderboard title
        JLabel leaderboardLabel = new JLabel("Leaderboard");
        leaderboardLabel.setFont(new Font("Poppins", Font.BOLD, 24));
        leaderboardLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        leaderboardPanel.add(leaderboardLabel);

        // Add vertical space
        leaderboardPanel.add(Box.createVerticalStrut(20));

        // Create a panel to display player names and scores
        JPanel playerPanel = new JPanel();
        playerPanel.setLayout(new BoxLayout(playerPanel, BoxLayout.Y_AXIS)); // Vertical layout
        playerPanel.setBackground(new Color(245, 245, 245));

        // Add player data to the panel
        loadLeaderboardData(playerPanel);

        // Add the playerPanel to the leaderboardPanel
        JScrollPane scrollPane = new JScrollPane(playerPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); // Disable horizontal scroll
        leaderboardPanel.add(scrollPane);

        // Add vertical space
        leaderboardPanel.add(Box.createVerticalStrut(20));

        // Back to Home Page button
        JButton backButton = new JButton("Kembali");
        backButton.setFont(new Font("Poppins", Font.BOLD, 14));
        backButton.setBackground(new Color(255, 87, 34)); // Orange Red
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 87, 34), 2), // Border
            BorderFactory.createEmptyBorder(5, 20, 5, 20) // Padding
        ));
        backButton.addActionListener(e -> {
            leaderboardFrame.dispose(); // Close LeaderboardPage
            cardLayout.show(mainPanel, "HomePage"); // Show the HomePage using CardLayout
        });
        leaderboardPanel.add(backButton);

        // Add leaderboardPanel to the frame
        leaderboardFrame.add(leaderboardPanel, BorderLayout.CENTER);

        leaderboardFrame.setVisible(true);
    }

    // Method to load leaderboard data from the database
    private void loadLeaderboardData(JPanel playerPanel) {
        ArrayList<User> users = dbHandler.getUsers();
        for (User user : users) {
            // Create a label for each player and center it
            String playerData = user.getName() + " - " + user.getScore();
            JLabel playerLabel = new JLabel(playerData);
            playerLabel.setFont(new Font("Poppins", Font.PLAIN, 16));
            playerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            playerPanel.add(playerLabel);
        }
    }

    // public static void main(String[] args) {
    //     SwingUtilities.invokeLater(() -> {
    //         // Assuming you have a CardLayout in your main panel and a HomePage JPanel
    //         CardLayout cardLayout = new CardLayout();
    //         JPanel mainPanel = new JPanel(cardLayout);

    //         // Add your home page and leaderboard page to the CardLayout
    //         HomePage homePage = new HomePage();  // HomePage class should be implemented
    //         mainPanel.add(homePage, "HomePage");

    //         // Show the LeaderboardPage when needed (this is just an example)
    //         new LeaderboardPage(cardLayout, mainPanel);
    //     });
    // }
}
