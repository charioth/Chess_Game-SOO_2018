package graphics;

import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class UIList {
	/*
	 * Class that contains a list of the buttons initialized it makes easy to call all render methods and create new buttons
	 */
	private ArrayList<UIButton> buttons;

	public UIList() {
		buttons = new ArrayList<UIButton>();
	}

	public ArrayList<UIButton> getButtons() {
		return buttons;
	}

	public void tick() {
		// Execute every tick button method
		for (UIButton b : buttons) {
			b.tick();
		}
	}

	public void render(Graphics graph) {
		// Execute every render method of the button list
		for (UIButton b : buttons) {
			b.render(graph);
		}
	}

	public void bMouseMoved(MouseEvent mouse) {
		// Execute every button mouse moved to see if the mouse is on a button
		for (UIButton b : buttons) {
			b.bMouseMoved(mouse);
		}
	}

	public void bMouseRelease() {
		// If the mouse was released execute the button action that the mouse is on
		for (UIButton b : buttons) {
			b.bMouseRelease();
		}
	}

}
