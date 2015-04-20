package image;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import javax.imageio.ImageIO;

import org.imgscalr.Scalr;

public class ScalePane implements Serializable{

	private static final long serialVersionUID = 1L;
	private BufferedImage original;
    private BufferedImage image;

    public ScalePane(File file) {
        try {
            original = ImageIO.read(file.getAbsoluteFile());
            image = Scalr.resize(original, Scalr.Method.BALANCED, Scalr.Mode.FIT_TO_WIDTH, 150, 100, Scalr.OP_ANTIALIAS);
            
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public BufferedImage getImage() {
		return image;
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}
}
