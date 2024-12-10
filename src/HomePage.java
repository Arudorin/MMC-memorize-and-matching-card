import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class HomePage {

    private JFrame homeFrame;
    private JTextField nameField;
    private JButton startButton, leaderboardButton, exitButton;
    private DatabaseHandler dbHandler; // Tambahkan DatabaseHandler
    private String playerName;

    public HomePage() {
        dbHandler = new DatabaseHandler(); // Inisialisasi DatabaseHandler

        homeFrame = new JFrame("Welcome to Memoriez Matching Card");
        homeFrame.setLayout(new FlowLayout());
        homeFrame.setSize(400, 200);
        homeFrame.setLocationRelativeTo(null);
        homeFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel welcomeLabel = new JLabel("Enter Your Name:");
        nameField = new JTextField(20);

        startButton = new JButton("Start Game");
        leaderboardButton = new JButton("Leaderboard");
        exitButton = new JButton("Exit");

        // Action listeners
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                playerName = nameField.getText().trim();
                if (!playerName.isEmpty()) {
                    // Periksa apakah nama sudah ada di database
                    if (dbHandler.checkIfUserExists(playerName)) {
                        JOptionPane.showMessageDialog(homeFrame, "Welcome back, " + playerName + "! Starting the game...");
                    } else {
                        // Masukkan pemain ke database hanya jika nama tidak ada
                        dbHandler.updateScore(playerName, 0); // Set skor awal ke 0
                        JOptionPane.showMessageDialog(homeFrame, "Welcome, " + playerName + "! Starting the game...");
                    }
                    homeFrame.dispose(); // Tutup halaman utama
                    new MatchCard(playerName);  // Panggil game utama di sini
                } else {
                    JOptionPane.showMessageDialog(homeFrame, "Please enter your name!");
                }
            }
        });

        leaderboardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Tampilkan leaderboard
                new LeaderboardPage();
            }
        });

        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        homeFrame.add(welcomeLabel);
        homeFrame.add(nameField);
        homeFrame.add(startButton);
        homeFrame.add(leaderboardButton);
        homeFrame.add(exitButton);

        homeFrame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(HomePage::new);
    }
}
