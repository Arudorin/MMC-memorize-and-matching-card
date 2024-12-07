import javax.swing.*;

public class MainFrame extends JFrame {
    public MainFrame() {
        setTitle("Memory and Matching Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setResizable(false);

        MatchingCard gamePanel = new MatchingCard();
        add(gamePanel);

        setVisible(true);
    }

    public static void main(String[] args) {
        new MainFrame();
    }
}
