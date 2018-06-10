package graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

public class Text {
	private String message;
	private Font font;

	private Color color;
	private int x, y;

	public Text(String message, Font font, Color color, int x, int y) {
		this.message = message;
		this.font = font;
		this.color = color;
		this.x = x;
		this.y = y;
	}

	public void render(Graphics graph) {
		graph.setColor(color);
		graph.setFont(font);
		graph.drawString(message, x, y);
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Font getFont() {
		return font;
	}

	public void setFont(Font font) {
		this.font = font;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
}
