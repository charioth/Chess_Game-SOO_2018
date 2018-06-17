package view.graphics;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import controller.states.State;

public class UIButton {
	/*
	 * Button class it has a position on the screen, a size (width and height) a
	 * onButton attribute to control when the mouse is on the button a bufferedImage
	 * vector to hold the 2 images of the button a Rectangle to test if the mouse
	 * position is on bound with the button area a vector Text if the button uses
	 * dynamic messages and last the buttonAction that tell what the button should
	 * execute when clicked
	 */
	private int index;
	private int x, y, width, height;
	private boolean onButton;
	private BufferedImage button[];
	private Rectangle bound;
	private ButtonAction click;
	private Text text[];

	/**
	 * Constructor of a button render that allow to configure his position, size,
	 * images (that alters when mouse over) and action when clicked
	 * 
	 * @param x
	 *            x axis coordinate of the button in the screen
	 * @param y
	 *            y axis coordinate of the button in the screen
	 * @param width
	 *            Button width in pixel value
	 * @param height
	 *            Button height in pixel value
	 * @param button
	 *            Define two images to define the button
	 * 
	 * @param index
	 *            Allow to define a intern index value to the button
	 * 
	 * @param click
	 *            Allow to define the action to execute when the button is clicked
	 */

	public UIButton(int x, int y, int width, int height, BufferedImage button[], int index, ButtonAction click) {
		// Constructor that does not use Text
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.button = button;
		onButton = false;
		bound = new Rectangle(x, y, width, height);
		this.click = click;
		this.text = null;
		this.index = index;
	}

	/**
	 * Constructor of a button render that allow to configure his position, size,
	 * images (that alters when mouse over) and action when clicked
	 * 
	 * @param x
	 *            x axis coordinate of the button in the screen
	 * @param y
	 *            y axis coordinate of the button in the screen
	 * @param width
	 *            Button width in pixel value
	 * @param height
	 *            Button height in pixel value
	 * @param button
	 *            Define two images to define the button
	 * 
	 * @param index
	 *            Allow to define a intern index value to the button
	 * 
	 * @param text
	 *            Define a text to be rendered inside the button box
	 * 
	 * @param click
	 *            Allow to define the action to execute when the button is clicked
	 */
	// Constructor that use one Text
	public UIButton(int x, int y, int width, int height, BufferedImage button[], int index, Text text,
			ButtonAction click) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.button = button;
		onButton = false;
		bound = new Rectangle(x, y, width, height);
		this.click = click;
		this.text = new Text[2];
		this.text[0] = text;
		this.text[1] = text;
		this.index = index;
	}

	/**
	 * Constructor of a button render that allow to configure his position, size,
	 * images (that alters when mouse over) and action when clicked
	 * 
	 * @param x
	 *            x axis coordinate of the button in the screen
	 * @param y
	 *            y axis coordinate of the button in the screen
	 * @param width
	 *            Button width in pixel value
	 * @param height
	 *            Button height in pixel value
	 * @param button
	 *            Define two images to define the button
	 * 
	 * @param index
	 *            Allow to define a intern index value to the button
	 * 
	 * @param text
	 *            Define a array text to be rendered inside the button box to allow
	 *            the text change when mouse over
	 * 
	 * @param click
	 *            Allow to define the action to execute when the button is clicked
	 */
	// Constructor that use two Texts
	public UIButton(int x, int y, int width, int height, BufferedImage button[], int index, Text text[],
			ButtonAction click) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.button = button;
		onButton = false;
		bound = new Rectangle(x, y, width, height);
		this.click = click;
		this.text = text;
		this.index = index;
	}

	/**
	 * Render the button on the screen and change the button render image and allow
	 * the action click happens while the mouse is over the button
	 * 
	 * @param graphic
	 *            Object that allow to draw and render the button in a canvas object
	 * 
	 */
	public void render(Graphics graph) {
		/* Method responsible to render the button on the screen */

		/*
		 * If onButton false it means that the mouse is not on the button so use the
		 * first image if is true than render the second image
		 */
		if (onButton == false) {
			graph.drawImage(button[0], x, y, width, height, null);
			if (text != null)
				text[0].render(graph);
		} else {
			graph.drawImage(button[1], x, y, width, height, null);
			if (text != null)
				text[1].render(graph);
		}

	}

	/**
	 * Method responsible for checking if the mouse is in over the button area, if
	 * it is, than change the render image and allow to click action to activate if
	 * clicked image if not then on button is false
	 * 
	 * @param mouse
	 *            It`s a MouseEvent object used to track the current coordinates of
	 *            the cursor on the screen
	 */
	public void bMouseMoved(MouseEvent mouse) {
		/*
		 * Method responsible for checking if the mouse coordinates is in bounds with
		 * the button area, if true, than onButton should be true to render the second
		 * image if not then on button is false
		 */
		if (bound.contains(mouse.getX(), mouse.getY())) {
			onButton = true;
		} else {
			onButton = false;
		}
	}

	/**
	 * activates the click action when called and mouse cursor is over the button
	 */
	public void bMouseRelease() {
		// If the mouse is on button than call the action method that execute the action
		if (onButton) {
			if (index >= 0) {
				State.lastButtonIndex = index;
			}
			onButton = false;
			click.action();
		}
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

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public Text[] getText() {
		return text;
	}

	public void setText(Text text[]) {
		this.text = text;
	}

	// If there was a action that the button should execute it would be necessary to
	// program here
	public void tick() {

	}

	public Rectangle getBound() {
		return bound;
	}

	public void setBound(Rectangle bound) {
		this.bound = bound;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
}
