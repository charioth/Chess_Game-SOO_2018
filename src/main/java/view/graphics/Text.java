package view.graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

public class Text {
	private String message;
	private Font font;

	private Color color;
	private int x, y;

	/**
	 * Constructor of the rendered message text inside window that allow to change
	 * font, color and position of the message on the window
	 * 
	 * @param message
	 *            Text that will be rendered in the window
	 * @param font
	 *            Define the text font style
	 * @param color
	 *            Define the color of the rendered text
	 * @param x
	 *            The x axis position of the text in the window
	 * @param y
	 *            The y axis position of the text in the window
	 */
	public Text(String message, Font font, Color color, int x, int y) {
		this.message = message;
		this.font = font;
		this.color = color;
		this.x = x;
		this.y = y;
	}

	/**
	 * Method that render the text in the game window
	 * 
	 * @param graph
	 *            Graphics object created a canvas to allow draw the text inside
	 */
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
