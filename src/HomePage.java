import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class HomePage extends JPanel {

    private JTextField nameField;
    private JButton startButton, leaderboardButton, exitButton;
    private DatabaseHandler dbHandler;
    private String playerName;
    private CardLayout cardLayout;
    private JPanel mainPanel;

    public HomePage(CardLayout cardLayout, JPanel mainPanel) {
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;

        dbHandler = new DatabaseHandler();

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(new Color(245, 245, 245));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel nameGameLabel = new JLabel("MMC");
        nameGameLabel.setFont(new Font("poppins", Font.BOLD, 24));
        nameGameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel welcomeLabel = new JLabel("Masukkan Nickname Anda:");
        welcomeLabel.setFont(new Font("poppins", Font.BOLD, 18));
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        nameField = new JTextField(15);
        nameField.setFont(new Font("Poppins", Font.PLAIN, 14));
        nameField.setMaximumSize(new Dimension(150, 40));
        nameField.setAlignmentX(Component.CENTER_ALIGNMENT);
        nameField.setHorizontalAlignment(JTextField.CENTER);

        startButton = new JButton("Mulai");
        startButton.setFont(new Font("Poppins", Font.BOLD, 14));
        startButton.setBackground(new Color(0, 122, 204));
        startButton.setForeground(Color.WHITE);
        startButton.setFocusPainted(false);
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 122, 204), 2),
            BorderFactory.createEmptyBorder(5, 20, 5, 20)
        ));

        leaderboardButton = new JButton("Leaderboard");
        leaderboardButton.setFont(new Font("Poppins", Font.BOLD, 14));
        leaderboardButton.setBackground(new Color(34, 193, 195));
        leaderboardButton.setForeground(Color.WHITE);
        leaderboardButton.setFocusPainted(false);
        leaderboardButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        leaderboardButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(34, 193, 195), 2),
            BorderFactory.createEmptyBorder(5, 20, 5, 20)
        ));

        exitButton = new JButton("Keluar");
        exitButton.setFont(new Font("Poppins", Font.BOLD, 14));
        exitButton.setBackground(new Color(255, 87, 34));
        exitButton.setForeground(Color.WHITE);
        exitButton.setFocusPainted(false);
        exitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 87, 34), 2),
            BorderFactory.createEmptyBorder(5, 20, 5, 20)
        ));

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                playerName = nameField.getText().trim();
                if (!playerName.isEmpty()) {
                    if (dbHandler.checkIfUserExists(playerName)) {
                        JOptionPane.showMessageDialog(null, "Welcome back, " + playerName + "! Starting the game...");
                    } else {
                        dbHandler.updateScore(playerName, 0);
                        JOptionPane.showMessageDialog(null, "Welcome, " + playerName + "! Starting the game...");
                    }
                    MatchCard matchCard = new MatchCard(playerName, cardLayout, mainPanel);
                    mainPanel.add(matchCard, "MatchCard");
                    cardLayout.show(mainPanel, "MatchCard");
                } else {
                    JOptionPane.showMessageDialog(null, "Please enter your name!");
                }
            }
        });

        leaderboardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new LeaderboardPage();
            }
        });

        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        add(nameGameLabel);
        add(Box.createVerticalStrut(20));
        add(welcomeLabel);
        add(Box.createVerticalStrut(20));
        add(nameField);
        add(Box.createVerticalStrut(20));
        add(startButton);
        add(Box.createVerticalStrut(20));
        add(leaderboardButton);
        add(Box.createVerticalStrut(20));
        add(exitButton);
    }
}