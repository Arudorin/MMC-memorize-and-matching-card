import javax.swing.*;
import java.awt.*;

public class AssetLoader {
    private static final int IMAGE_WIDTH = 200; // Ukuran lebar gambar yang diinginkan
    private static final int IMAGE_HEIGHT = 300; // Ukuran tinggi gambar yang diinginkan

    public static ImageIcon[] loadImages() {
        String[] fileNames = {"assets/chery.png", "assets/lemon.png", "assets/nanas.png"};
        ImageIcon[] images = new ImageIcon[fileNames.length];

        for (int i = 0; i < fileNames.length; i++) {
            ImageIcon originalIcon = new ImageIcon(fileNames[i]);
            // Resize gambar agar lebih kecil dan lebih rapi
            Image img = originalIcon.getImage().getScaledInstance(IMAGE_WIDTH, IMAGE_HEIGHT, Image.SCALE_SMOOTH);
            images[i] = new ImageIcon(img);
        }
        return images;
    }

    public static ImageIcon getBackImage() {
        ImageIcon originalIcon = new ImageIcon("assets/tutup.png");
        // Resize gambar kartu tertutup
        Image img = originalIcon.getImage().getScaledInstance(IMAGE_WIDTH, IMAGE_HEIGHT, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }
}
