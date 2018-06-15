package view.loader;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageLoader {
	/**
	 * Used to load images
	 * 
	 * @param path	The path of the image
	 * @return		The loaded image
	 */
	public static BufferedImage loadImage(String path) {
		try {
			return ImageIO.read(ImageLoader.class.getResource(path));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
