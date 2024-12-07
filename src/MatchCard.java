import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import javax.swing.*;

public class MatchCard {

    int rows = 4;  
    int cols = 6; 
    int cardWidth = 90; 
    int cardHeight = 128; 

    int boardWidth = cols * cardWidth; 
    int boardHeight = rows * cardHeight; 

    JFrame frame = new JFrame("Memoriez Matching Card"); 
    JLabel textLabel = new JLabel(); 
    JPanel textPanel = new JPanel(); 
    JPanel mainPanel; 
    JPanel boardPanel = new JPanel(); 
    JPanel restartGamePanel = new JPanel();  
    JButton restartButton = new JButton();

    int errorCount = 0; 
    ArrayList<JButton> board; 


    Timer reviewCardsTimer; 

    JLabel timerLabel = new JLabel();
    Timer gameTimer; 

    // Set Waktu Awal Permainan
    int initialTime = 120;
    // Waktu yang tersisa 
    int timeLeft = initialTime; 
    
    MatchCard() {
        AudioPlayer audioPlayer = new AudioPlayer();
        audioPlayer.playBackgroundMusic("src/sound/bg_music.wav");
        setupCards();
        shuffleCards();

        // Membuat panel latar belakang dan menambahkannya ke frame
        BackgroundPanel backgroundPanel = new BackgroundPanel("src/img/background.jpg");
        backgroundPanel.setLayout(new BorderLayout());

        frame.setLayout(new BorderLayout());
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);

        frame.setResizable(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Mengubah layout textPanel menjadi BoxLayout vertikal
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        
        // Menambahkan timerLabel di atas dan textLabel di bawah
        timerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        timerLabel.setHorizontalAlignment(JLabel.CENTER);
        timerLabel.setText("Waktu Tersisa: " + timeLeft);
        timerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);  // Menjaga agar label terpusat
        
        textLabel.setFont(new Font("Arial", Font.BOLD, 20));
        textLabel.setHorizontalAlignment(JLabel.CENTER);
        textLabel.setText("Salah: " + Integer.toString(errorCount));
        textLabel.setAlignmentX(Component.CENTER_ALIGNMENT);  // Menjaga agar label terpusat
        
        // Menambahkan kedua label ke textPanel
        textPanel.add(timerLabel);
        textPanel.add(Box.createVerticalStrut(10));  // Memberikan jarak antara timer dan error
        textPanel.add(textLabel);
        
        // Menambahkan textPanel ke frame
        mainPanel = new JPanel(){
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Menggambar gambar latar 
                ImageIcon backgroundImage = new ImageIcon("src/img/background.jpg");
                backgroundImage = new ImageIcon(backgroundImage.getImage().getScaledInstance(getWidth(), getHeight(), Image.SCALE_SMOOTH));
                Image image = backgroundImage.getImage();
                g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
            }  
        };
        
        textPanel.setOpaque(false);
        mainPanel.add(textPanel, BorderLayout.NORTH);
                
        board = new ArrayList<JButton>();

        boardPanel.setLayout(new GridLayout(rows, cols, 0, 0));  
        boardPanel.setOpaque(false);

        mainPanel.setBorder(BorderFactory.createEmptyBorder(100,500,70,500));
        mainPanel.setBackground(Color.cyan);
        mainPanel.add(boardPanel, BorderLayout.CENTER);

        for (int i = 0; i < cardSet.size(); i++) {
            JButton tile = new JButton();
            tile.setPreferredSize(new Dimension(cardWidth, cardHeight));
            // tile.setOpaque(true);
            tile.setOpaque(false);  // Set opacity ke false untuk membuat tombol transparan
            tile.setContentAreaFilled(false);  // Menghilangkan warna latar belakang tombol
            tile.setBorder(null);
            tile.setIcon(cardSet.get(i).cardImageIcon);
            tile.setFocusable(false);
            tile.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (!gameReady) {
                        return;
                    }

                    JButton tile = (JButton) e.getSource();
                    // Hanya jika kartu yang dipilih adalah gambar belakang
                    if (tile.getIcon() == cardBackImageIcon) {
                        // Kartu pertama dipilih  
                        if (card1Selected == null) {  
                            card1Selected = tile;
                            int index = board.indexOf(card1Selected);
                            card1Selected.setIcon(cardSet.get(index).cardImageIcon);
                        } else if (card2Selected == null) {  // Kartu kedua dipilih
                            card2Selected = tile;
                            int index = board.indexOf(card2Selected);
                            card2Selected.setIcon(cardSet.get(index).cardImageIcon);

                            // Cek apakah kedua kartu cocok
                            if (card1Selected.getIcon() != card2Selected.getIcon()) {
                                // Jika tidak cocok, increment errorCount
                                errorCount += 1;  
                                textLabel.setText("Salah: " + Integer.toString(errorCount));
                                // Mulai timer untuk menyembunyikan kartu kembali
                                hideCardTimer.start();  
                            } else {
                                // Menambah jumlah pasangan yang cocok
                                matchedPairs += 1;  
                                card1Selected = null;
                                card2Selected = null;

                                // Cek apakah semua kartu sudah cocok
                                if (matchedPairs == (cardSet.size() / 2)) {
                                    gameReady = false;  // Menandakan permainan selesai
                                    gameTimer.stop();
                                    int reply = JOptionPane.showConfirmDialog(frame, "Semua kartu sudah dicocokkan! Ingin bermain lagi?", 
                                                                              "Permainan Selesai", JOptionPane.YES_NO_OPTION);
                                    if (reply == JOptionPane.YES_OPTION) {
                                        // Panggil fungsi restart untuk memulai ulang permainan
                                        restartGame();  
                                    } else {
                                        // Keluar dari aplikasi jika pemain memilih NO
                                        System.exit(0);  
                                    }
                                }
                            }
                        }
                    }
                }
            });
            board.add(tile);
            boardPanel.add(tile);
        }
        mainPanel.add(boardPanel);


        restartButton.setFont(new Font("Arial", Font.BOLD, 20));
        restartButton.setText("Restart Game");
        restartButton.setPreferredSize(new Dimension(boardWidth, 30));
        restartButton.setFocusable(false);
        restartButton.setEnabled(false);

        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!gameReady) {
                    return;
                }

                // Panggil fungsi restartGame untuk memulai ulang permainan
                restartGame();  

                // Menyembunyikan kartu setelah restart
                hideCardTimer.start();
            }
        });

        restartGamePanel.add(restartButton);
        restartGamePanel.setOpaque(false);
        mainPanel.add(restartGamePanel, BorderLayout.SOUTH);
        // frame.add(restartGamePanel, BorderLayout.SOUTH);

        frame.add(mainPanel, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);

        //start game
        hideCardTimer = new Timer(1050, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                hideCards();
            }
        });
        hideCardTimer.setRepeats(false);
        hideCardTimer.start();

        // Inisialisasi Timer untuk menghitung mundur
        gameTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (timeLeft > 0) {
                    timeLeft--;
                    // Update waktu di label
                    timerLabel.setText("Waktu Tersisa: " + timeLeft);  
                } else {
                    // Hentikan timer
                    gameTimer.stop();  
                    // Menandakan permainan selesai
                    gameReady = false;  
                    int reply = JOptionPane.showConfirmDialog(frame, "Waktu Telah Habis, Ingin bermain lagi?", 
                                                                "Permainan Selesai", JOptionPane.YES_NO_OPTION);
                    if (reply == JOptionPane.YES_OPTION) {
                        // Panggil fungsi restart untuk memulai ulang permainan
                        restartGame();  
                    } else {
                        // Keluar dari aplikasi jika pemain memilih NO
                        System.exit(0);  
                    }
                    // Panggil fungsi restart jika waktu habis
                    restartGame();  
                }
            }
        });
        // Mulai timer
        gameTimer.start();  

    }

}
