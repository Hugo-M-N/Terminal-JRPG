package game.sprite;

import java.awt.image.BufferedImage;

public class Sprite {
	BufferedImage image;
	int height, width;
	
	public BufferedImage getImage() {
		return image;
	}
	public void setImage(BufferedImage image) {
		this.image = image;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	
	public Sprite(BufferedImage image, int height, int width) {
		this.image = image;
		this.height = height;
		this.width = width;
	}
	
}
