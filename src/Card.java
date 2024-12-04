import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Card extends JButton {
    private ImageIcon frontImage;
    private ImageIcon backImage;
    private boolean isFlipped = false;
    private GameLogic gameLogic;

    public Card(ImageIcon frontImage, GameLogic gameLogic) {
        this.frontImage = frontImage;
        this.backImage = AssetLoader.getBackImage();
        this.gameLogic = gameLogic;

        setIcon(backImage);
        addActionListener(new CardClickListener());
    }

    public void flip() {
        if (isFlipped) {
            setIcon(backImage);
        } else {
            setIcon(frontImage);
        }
        isFlipped = !isFlipped;
    }

    public ImageIcon getFrontImage() {
        return frontImage;
    }

    private class CardClickListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            gameLogic.cardClicked(Card.this);
        }
    }
}
