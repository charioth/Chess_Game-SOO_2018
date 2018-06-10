package graphics;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class UIScrollScreen {

	/* ScrollScreen is a class that can receive a image to use as a screen that permits to scroll down and up */

	private ArrayList<UIButton> buttons;
	private Rectangle screen;
	private boolean onScreen;
	private int speed;
	private BufferedImage screenImage;

	public UIScrollScreen(BufferedImage screenImage, int x, int y, int width, int height, int speed) {
		buttons = new ArrayList<>();
		screen = new Rectangle(x, y, width, height);
		this.screenImage = screenImage;
		this.speed = speed;
	}

	public void tick() {
		/* Checks what buttons are on the screen, the ones on screen are kept active */
		for (int i = 0; i < buttons.size(); i++) {
			if (screen.contains(buttons.get(i).getX(), buttons.get(i).getY())
					&& (screen.contains(buttons.get(i).getX(), buttons.get(i).getY() + buttons.get(i).getHeight())))
				buttons.get(i).tick();
		}
	}

	public void render(Graphics graph) {
		/* Checks what buttons are on the screen, the ones on screen are kept active to render */
		graph.drawImage(screenImage, (int) screen.getX(), (int) screen.getY(), (int) screen.getWidth(),
				(int) screen.getHeight(), null);
		for (int i = 0; i < buttons.size(); i++) {
			if (screen.contains(buttons.get(i).getX(), buttons.get(i).getY())
					&& (screen.contains(buttons.get(i).getX(), buttons.get(i).getY() + buttons.get(i).getHeight())))
				buttons.get(i).render(graph);
		}
	}

	public void sMouseMoved(MouseEvent mouse) {
		/* Checks if the mouse is on the screen, if it is active buttons that are active on the screen to keep track if the mouse is on them */
		if (screen.contains(mouse.getX(), mouse.getY())) {
			onScreen = true;
			for (int i = 0; i < buttons.size(); i++) {
				if (screen.contains(buttons.get(i).getX(), buttons.get(i).getY())
						&& (screen.contains(buttons.get(i).getX(), buttons.get(i).getY() + buttons.get(i).getHeight())))
					buttons.get(i).bMouseMoved(mouse);
				;
			}
		} else {
			onScreen = false;
		}
	}

	public void sMouseRelease() {
		/* Checks what buttons are on the screen, the ones on screen are kept active to respond to mouseRelease events */
		if (onScreen) {
			onScreen = false;
			for (int i = 0; i < buttons.size(); i++) {
				if (screen.contains(buttons.get(i).getX(), buttons.get(i).getY())
						&& (screen.contains(buttons.get(i).getX(), buttons.get(i).getY() + buttons.get(i).getHeight())))
					buttons.get(i).bMouseRelease();
			}
		}
	}

	public void sMouseScroll(MouseWheelEvent mouse) {
		/* Respond when mouse scroll is used on the screen */
		int rotation = -mouse.getWheelRotation();

		if (onScreen) {
			if (buttons.size() > 0) // If there is at least one button execute
			{
				// If the dot on the left side below the button is on the screen and the rotation is down, do not let it go down further
				boolean holdScrollDown = (screen.contains(buttons.get(buttons.size() - 1).getX(),
						buttons.get(buttons.size() - 1).getY() + buttons.get(buttons.size() - 1).getHeight()))
						&& (rotation < 0);
				// If the dot on the upper right side of the button is on the screen and the rotation is up, do not let it go turther
				boolean holdScrollUp = (screen.contains(buttons.get(0).getX(), buttons.get(0).getY())
						&& (rotation > 0));
				if (holdScrollDown || holdScrollUp)
					return;
			}
			for (int i = 0; i < buttons.size(); i++) { // Actualize all buttons y position
				buttons.get(i).setY(buttons.get(i).getY() + (rotation * speed));
				buttons.get(i).getText()[0].setY(buttons.get(i).getText()[0].getY() + (rotation * speed));
				buttons.get(i).getText()[1].setY(buttons.get(i).getText()[1].getY() + (rotation * speed));
				buttons.get(i).getBound().setLocation(buttons.get(i).getX(), buttons.get(i).getY());
			}
		}
	}

	public ArrayList<UIButton> getButtons() {
		return buttons;
	}

	public void setButtons(ArrayList<UIButton> buttons) {
		this.buttons = buttons;
	}

	public Rectangle getScreen() {
		return screen;
	}

	public void setScreen(Rectangle screen) {
		this.screen = screen;
	}

	public boolean isOnScreen() {
		return onScreen;
	}

	public void setOnScreen(boolean onScreen) {
		this.onScreen = onScreen;
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public BufferedImage getScreenImage() {
		return screenImage;
	}

	public void setScreenImage(BufferedImage screenImage) {
		this.screenImage = screenImage;
	}

}
