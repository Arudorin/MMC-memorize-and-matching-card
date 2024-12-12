    import java.awt.*;
    import java.awt.event.*;
    import java.io.File;
    import java.util.ArrayList;
    import javax.swing.*;

    public class MatchCard extends JPanel {

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

        JLabel playerLabel = new JLabel();
        JLabel textLabel = new JLabel();
        JPanel textPanel = new JPanel();
        JPanel mainPanel;
        JPanel boardPanel = new JPanel();
        JPanel restartGamePanel = new JPanel();  
        JButton restartButton = new JButton(); 

        int errorCount = 0;
        
        boolean gameReady = false; 
        ArrayList<JButton> board;
        JButton card1Selected; 
        JButton card2Selected; 
        int matchedPairs = 0;   
        
        
        Timer reviewCardsTimer; 
        JLabel timerLabel = new JLabel(); 
        Timer hideCardTimer; 
        Timer gameTimer; 
        int initialTime = 25; 
        int timeLeft = initialTime;  


        private DatabaseHandler dbHandler;

        private CardLayout cardLayout;
        private JPanel mainPanelContainer;
        String globalPlayerName;
        
        MatchCard(String playerName, CardLayout cardLayout, JPanel mainPanelContainer) {
            this.cardLayout = cardLayout;
            this.mainPanelContainer = mainPanelContainer;

            AudioPlayer audioPlayer = new AudioPlayer();
            audioPlayer.playBackgroundMusic("src/sound/bg_music.wav");
            
            setupCards();
            shuffleCards();

            dbHandler = new DatabaseHandler();

            globalPlayerName = playerName;

            setLayout(new BorderLayout());
            setPreferredSize(new Dimension(boardWidth, boardHeight));

            textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
            
            playerLabel.setFont(new Font("Arial", Font.BOLD, 20));
            playerLabel.setHorizontalAlignment(JLabel.CENTER);
            playerLabel.setText(playerName);
            playerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            // Menambahkan timerLabel di atas dan textLabel di bawah
            timerLabel.setFont(new Font("poppins", Font.BOLD, 20));
            timerLabel.setHorizontalAlignment(JLabel.CENTER);
            int minutes = timeLeft / 60;  // Menghitung menit
            int seconds = timeLeft % 60;  // Menghitung detik
            // Update waktu di label dalam format menit:detik
            timerLabel.setText(String.format("Waktu Tersisa: %02d:%02d", minutes, seconds));  
            timerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);  // Menjaga agar label terpusat
            
            textLabel.setFont(new Font("Arial", Font.BOLD, 20));
            textLabel.setHorizontalAlignment(JLabel.CENTER);
            textLabel.setText("Salah: " + Integer.toString(errorCount));
            textLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            textPanel.add(playerLabel);
            textPanel.add(Box.createVerticalStrut(10));
            textPanel.add(timerLabel);
            textPanel.add(Box.createVerticalStrut(10));
            textPanel.add(textLabel);
            
            mainPanel = new JPanel(){
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
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

            for (int i = 0; i < cardSet.size(); i++) {
                JButton tile = new JButton();
                tile.setPreferredSize(new Dimension(cardWidth, cardHeight));
                tile.setOpaque(false);
                tile.setContentAreaFilled(false);
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
                        if (tile.getIcon() == cardBackImageIcon) {
                            if (card1Selected == null) {  
                                card1Selected = tile;
                                int index = board.indexOf(card1Selected);
                                card1Selected.setIcon(cardSet.get(index).cardImageIcon);
                            } else if (card2Selected == null) {  
                                card2Selected = tile;
                                int index = board.indexOf(card2Selected);
                                card2Selected.setIcon(cardSet.get(index).cardImageIcon);

                                if (card1Selected.getIcon() != card2Selected.getIcon()) {
                                    errorCount += 1;  
                                    textLabel.setText("Salah: " + Integer.toString(errorCount));
                                    hideCardTimer.start();  
                                } else {
                                    matchedPairs += 1;  
                                    card1Selected = null;
                                    card2Selected = null;

                                    if (matchedPairs == (cardSet.size() / 2)) {
                                        gameReady = false;  
                                        gameTimer.stop();
                                        int reply = JOptionPane.showConfirmDialog(null, "Semua kartu sudah dicocokkan! Ingin bermain lagi?", 
                                                                                "Permainan Selesai", JOptionPane.YES_NO_OPTION);
                                        if (reply == JOptionPane.YES_OPTION) {
                                            restartGame();  
                                        } else {
                                            audioPlayer.stopBackgroundMusic();
                                            updateScore();
                                            cardLayout.show(mainPanelContainer, "HomePage");
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

                    restartGame();  
                    hideCardTimer.start();
                }
            });

            restartGamePanel.add(restartButton);
            restartGamePanel.setOpaque(false);
            mainPanel.add(restartGamePanel, BorderLayout.SOUTH);

            add(mainPanel, BorderLayout.CENTER);

            hideCardTimer = new Timer(1050, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    hideCards();
                }
            });
            hideCardTimer.setRepeats(false);
            hideCardTimer.start();

            gameTimer = new Timer(1000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (timeLeft > 0) {
                        timeLeft--;
                        // Update waktu di label
                            int minutes = timeLeft / 60;  // Menghitung menit
                            int seconds = timeLeft % 60;  // Menghitung detik
                            // Update waktu di label dalam format menit:detik
                            timerLabel.setText(String.format("Waktu Tersisa: %02d:%02d", minutes, seconds)); 
                    } else {
                        gameTimer.stop();  
                        gameReady = false;  
                        int reply = JOptionPane.showConfirmDialog(null, "Waktu Telah Habis, Ingin bermain lagi?", 
                                                                    "Permainan Selesai", JOptionPane.YES_NO_OPTION);
                        if (reply == JOptionPane.YES_OPTION) {
                            restartGame();  
                        } else {
                            audioPlayer.stopBackgroundMusic();
                            updateScore();
                            cardLayout.show(mainPanelContainer, "HomePage");
                        }
                    }
                }
            });
            gameTimer.start();  
        }

        void setupCards() {
            cardSet = new ArrayList<Card>();
            for (String cardName : cardList) {
                Image cardImg = new ImageIcon(new File("src/img/" + cardName + ".jpg").getAbsolutePath()).getImage();
                ImageIcon cardImageIcon = new ImageIcon(cardImg.getScaledInstance(cardWidth, cardHeight, java.awt.Image.SCALE_SMOOTH));
                Card card = new Card(cardName, cardImageIcon);
                cardSet.add(card);
            }
            cardSet.addAll(cardSet);
            Image cardBackImg = new ImageIcon(getClass().getResource("./img/back.jpg")).getImage();
            cardBackImageIcon = new ImageIcon(cardBackImg.getScaledInstance(cardWidth, cardHeight, java.awt.Image.SCALE_SMOOTH));
        }

        void shuffleCards() {
            for (int i = 0; i < cardSet.size(); i++) {
                int j = (int) (Math.random() * cardSet.size());
                Card temp = cardSet.get(i);
                cardSet.set(i, cardSet.get(j));
                cardSet.set(j, temp);
            }
        }

        void hideCards() {
            if (gameReady && card1Selected != null && card2Selected != null) {
                card1Selected.setIcon(cardBackImageIcon);
                card1Selected = null;  
                card2Selected.setIcon(cardBackImageIcon);  
                card2Selected = null;  
            } else {
                for (int i = 0; i < board.size(); i++) {
                    board.get(i).setIcon(cardBackImageIcon);  
                }
                gameReady = true;  
                restartButton.setEnabled(true);  
            }
        }

        void restartGame() {
            matchedPairs = 0;  
            errorCount = 0;    
            textLabel.setText("Errors: " + Integer.toString(errorCount));  
            setupCards();
            shuffleCards();  
            gameReady = false;  
        
            timeLeft = initialTime;  
            timerLabel.setText("Time Left: " + timeLeft);  
            gameTimer.restart();  
        
            for (int i = 0; i < board.size(); i++) {
                board.get(i).setIcon(cardSet.get(i).cardImageIcon);
            }
        
            reviewCardsTimer = new Timer(2000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    hideCardTimer.start();  
                }
            });

            reviewCardsTimer.setRepeats(false);
            reviewCardsTimer.start();
        }
        
        void updateScore() {
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