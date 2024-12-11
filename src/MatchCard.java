import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import javax.swing.*;

public class MatchCard {

    String[] cardList = {
        "a", "b", "c", "d", "e", "f", "g", "h", "i", "j"
    }; 

    int rows = 4;  
    int cols = 6; 
    int cardWidth = 90; 
    int cardHeight = 128; 

    ArrayList<Card> cardSet;  
    ImageIcon cardBackImageIcon;

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
    Timer hideCardTimer; 
    boolean gameReady = false; 
    JButton card1Selected; 
    JButton card2Selected; 

    // Variabel untuk jumlah pasangan yang sudah dicocokkan
    int matchedPairs = 0;   
    Timer reviewCardsTimer; 

    JLabel timerLabel = new JLabel(); 
    JLabel playerLabel = new JLabel();

    Timer gameTimer; 

    // Set Waktu Awal Permainan
    int initialTime = 25; 
    // Waktu yang tersisa 
    int timeLeft = initialTime;  

    private DatabaseHandler dbHandler;

    String globalPlayerName;
    
    MatchCard(String playerName) {
        // AudioPlayer audioPlayer = new AudioPlayer();
        // audioPlayer.playBackgroundMusic("src/sound/bg_music.wav");
        setupCards();
        shuffleCards();

        dbHandler = new DatabaseHandler();

        globalPlayerName = playerName;
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
        
                
        // namePanel.add(new JLabel("Score: " + Integer.toString(matchedPairs)));
        playerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        playerLabel.setHorizontalAlignment(JLabel.CENTER);
        playerLabel.setText(playerName);
        playerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);  // Menjaga agar label terpusat
        
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
        textPanel.add(playerLabel);
        textPanel.add(Box.createVerticalStrut(10));  // Memberikan jarak antara timer dan error
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
        // mainPanel.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
        // mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        // mainPanel.setBackground(Color.cyan);

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
                                        updateScore();
                                        new HomePage();
                                        frame.dispose();
                                        // System.exit(0);  
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
                        updateScore();
                        new HomePage();
                        frame.dispose();
                        // System.exit(0);  
                    }
                    // Panggil fungsi restart jika waktu habis
                    // restartGame();  
                }
            }
        });
        // Mulai timer
        gameTimer.start();  

    }
    void setupCards() {
        cardSet = new ArrayList<Card>();
        for (String cardName : cardList) {
            System.out.println(cardName);
            //Memuat Card
            Image cardImg = new ImageIcon(new File("src/img/" + cardName + ".jpg").getAbsolutePath()).getImage();

            ImageIcon cardImageIcon = new ImageIcon(cardImg.getScaledInstance(cardWidth, cardHeight, java.awt.Image.SCALE_SMOOTH));
            //Membuat objek kartu
            Card card = new Card(cardName, cardImageIcon);
            cardSet.add(card);
        }
        cardSet.addAll(cardSet);
        //load the back card image
        Image cardBackImg = new ImageIcon(getClass().getResource("./img/back.jpg")).getImage();
        cardBackImageIcon = new ImageIcon(cardBackImg.getScaledInstance(cardWidth, cardHeight, java.awt.Image.SCALE_SMOOTH));
    }

    void shuffleCards() {
        System.out.println(cardSet);
        //Mengacak Kartu
        for (int i = 0; i < cardSet.size(); i++) {
            int j = (int) (Math.random() * cardSet.size());
            //Menukar kartu
            Card temp = cardSet.get(i);
            cardSet.set(i, cardSet.get(j));
            cardSet.set(j, temp);
        }
    }

    void hideCards() {
        if (gameReady && card1Selected != null && card2Selected != null) {
            // Menyembunyikan kartu pertama
            card1Selected.setIcon(cardBackImageIcon);
            // Reset kartu pertama ke null  
            card1Selected = null;  
            // Menyembunyikan kartu kedua
            card2Selected.setIcon(cardBackImageIcon);  
            card2Selected = null;  // Reset kartu kedua ke null
        } else {
            for (int i = 0; i < board.size(); i++) {
                board.get(i).setIcon(cardBackImageIcon);  // Menyembunyikan semua kartu pada papan
            }
            gameReady = true;  // Menandakan permainan siap untuk dimulai
            restartButton.setEnabled(true);  // Mengaktifkan tombol restart
        }
    }

    void restartGame() {
        matchedPairs = 0;  // Reset jumlah pasangan yang cocok
        errorCount = 0;    // Reset jumlah kesalahan
        textLabel.setText("Errors: " + Integer.toString(errorCount));  // Reset label error
        setupCards();
        shuffleCards();  // Acak ulang kartu
        gameReady = false;  // Set gameReady ke false saat memulai ulang permainan
    
        // Setel ulang waktu dan mulai timer lagi
        timeLeft = initialTime;  // Setel ulang timer ke waktu awal (60 detik)
        timerLabel.setText("Time Left: " + timeLeft);  // Update label timer dengan waktu awal
        gameTimer.restart();  // Mulai ulang timer (gameTimer sudah diinisialisasi sebelumnya)
        
        // Menampilkan semua kartu untuk beberapa detik
        for (int i = 0; i < board.size(); i++) {
            board.get(i).setIcon(cardSet.get(i).cardImageIcon);
        }
    
        // Timer untuk menyembunyikan kartu setelah beberapa detik
        reviewCardsTimer = new Timer(2000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Mulai timer untuk menyembunyikan kartu
                hideCardTimer.start();  
            }
        });

        reviewCardsTimer.setRepeats(false);
        reviewCardsTimer.start();

    }
    
    void updateScore() {
        // Score = (100 - eror) + sisa waktu
        int score = 100 - errorCount + timeLeft;
        dbHandler.getUsers();
        dbHandler.updateScore(globalPlayerName, score);

        if (dbHandler != null) {
            dbHandler.updateScore(globalPlayerName, score);
        } else {
            System.out.println("Error: DatabaseHandler is not initialized.");
        }
         
    }
}
