import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class HomePage {

    private JFrame homeFrame;
    private JPanel homePanel;
    private JTextField nameField;
    private JButton startButton, leaderboardButton, exitButton;
    private DatabaseHandler dbHandler; // Tambahkan DatabaseHandler
    private String playerName;

    public HomePage() {
        dbHandler = new DatabaseHandler(); // Inisialisasi DatabaseHandler

        homeFrame = new JFrame("Welcome to Memoriez Matching Card");
        homeFrame.setLayout(new BorderLayout());
        homeFrame.setSize(350, 400);
        homeFrame.setLocationRelativeTo(null);
        homeFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel nameGameLabel = new JLabel("MMC");
        nameGameLabel.setFont(new Font("poppins", Font.BOLD, 24));
        nameGameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        

        JLabel welcomeLabel = new JLabel("Masukkan Nickname Anda:");
        welcomeLabel.setFont(new Font("poppins", Font.BOLD, 18));
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        nameField = new JTextField(15); // Lebar dalam karakter
        nameField.setFont(new Font("Poppins", Font.PLAIN, 14));
        nameField.setMaximumSize(new Dimension(150, 40)); // Lebar 150px, Tinggi 25px
        nameField.setAlignmentX(Component.CENTER_ALIGNMENT); // Agar teksField sejajar di tengah
        nameField.setHorizontalAlignment(JTextField.CENTER); // Teks di tengah


        startButton = new JButton("Mulai");
        startButton.setFont(new Font("Poppins", Font.BOLD, 14));
        startButton.setBackground(new Color(0, 122, 204));  // Biru
        startButton.setForeground(Color.WHITE);
        startButton.setFocusPainted(false);
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 122, 204), 2), // Border luar (opsional)
            BorderFactory.createEmptyBorder(5, 20, 5, 20)           // Padding dalam: atas, kiri, bawah, kanan
        ));
        
        leaderboardButton = new JButton("Leaderboard");
        leaderboardButton.setFont(new Font("Poppins", Font.BOLD, 14));
        leaderboardButton.setBackground(new Color(34, 193, 195));  // Hijau Biru
        leaderboardButton.setForeground(Color.WHITE);
        leaderboardButton.setFocusPainted(false);
        leaderboardButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        leaderboardButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(34, 193, 195), 2), // Border luar (opsional)
            BorderFactory.createEmptyBorder(5, 20, 5, 20)             // Padding dalam
        ));
        
        exitButton = new JButton("Keluar");
        exitButton.setFont(new Font("Poppins", Font.BOLD, 14));
        exitButton.setBackground(new Color(255, 87, 34));  // Merah Oranye
        exitButton.setForeground(Color.WHITE);
        exitButton.setFocusPainted(false);
        exitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 87, 34), 2), // Border luar (opsional)
            BorderFactory.createEmptyBorder(5, 20, 5, 20)           // Padding dalam
        ));
        

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

        // Membuat panel untuk konten
        homePanel = new JPanel();
        homePanel.setLayout(new BoxLayout(homePanel, BoxLayout.Y_AXIS));
        homePanel.setBackground(new Color(245, 245, 245));  // Warna latar belakang yang lembut
        homePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));  // Menambahkan padding di sekeliling panel

        // Menambahkan komponen ke panel dengan jarak antar komponen
        homePanel.add(nameGameLabel);
        homePanel.add(Box.createVerticalStrut(20));  // Menambahkan jarak vertikal setelah welcomeLabel

        homePanel.add(welcomeLabel);
        homePanel.add(Box.createVerticalStrut(20));  // Menambahkan jarak vertikal setelah welcomeLabel

        homePanel.add(nameField);
        homePanel.add(Box.createVerticalStrut(20));  // Menambahkan jarak vertikal setelah nameField

        homePanel.add(startButton);
        homePanel.add(Box.createVerticalStrut(20));  // Menambahkan jarak vertikal setelah startButton

        homePanel.add(leaderboardButton);
        homePanel.add(Box.createVerticalStrut(20));  // Menambahkan jarak vertikal setelah leaderboardButton

        homePanel.add(exitButton);

        homeFrame.add(homePanel, BorderLayout.CENTER); // Menambahkan homePanel ke frame utama
        homeFrame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(HomePage::new);
    }
}