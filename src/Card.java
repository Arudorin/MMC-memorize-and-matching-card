import javax.swing.ImageIcon;

class Card {
    String cardName;
    ImageIcon cardImageIcon;
    
    Card(String cardName, ImageIcon cardImageIcon) {
        this.cardName = cardName;
        this.cardImageIcon = cardImageIcon;
    }

    public String toString() {
        return cardName;
    }
}