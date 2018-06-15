package controller.input;

import java.awt.event.MouseEvent;

import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import controller.states.State;

public class MouseManager implements MouseListener, MouseMotionListener, MouseWheelListener {
	/*
	 * Mouse manager class it implement the actions on the click of the left mouse
	 * and keep track of the actual position of the mouse on the screen
	 */
	private boolean leftButton;
	private int x = 0, y = 0;

	/**
	 * Checks if the left mouse button was pressed
	 * 
	 * @return True if the left mouse button was pressed, false otherwise
	 */
	public boolean isLeftButtonPressed() {
		return leftButton;
	}

	/**
	 * In case the left mouse button is hold down this method is called to save the
	 * state and stop the constant input
	 * 
	 * @param leftButton
	 *            The button state in case its hold down
	 * @return True if the state is true, false otherwise
	 */
	public boolean setLeftButtonPressed(boolean leftButton) {
		return this.leftButton = leftButton;
	}

	public int getMouseX() {
		return x;
	}

	public int getMouseY() {
		return y;
	}

	@Override
	public void mousePressed(MouseEvent mouse) {
		/* Method called every time the mouse is pressed */
		// Check if the BUTTON1 was pressed button1 represent the left mouse button
		if ((mouse.getButton() == MouseEvent.BUTTON1)) {
			// if it was pressed get the mouse position
			x = mouse.getX();
			y = mouse.getY();
			leftButton = true;
		}
	}

	@Override
	public void mouseReleased(MouseEvent mouse) {
		/* Method called when the mouse button is released */
		// Check if the mouse released was the left mouse button
		if (mouse.getButton() == MouseEvent.BUTTON1) {
			leftButton = false;
		}

		if (State.getCurrentState().getScreen() != null) {
			State.getCurrentState().getScreen().sMouseRelease();
		}
		// If there is a button list on the current state, call it and execute the
		// MouseRelease of each button
		else if (State.getCurrentState().getUIButtons() != null) {
			State.getCurrentState().getUIButtons().bMouseRelease();
		}

	}

	@Override
	public void mouseClicked(MouseEvent mouse) {
	}

	@Override
	public void mouseEntered(MouseEvent mouse) {
	}

	@Override
	public void mouseExited(MouseEvent mouse) {
	}

	@Override
	public void mouseDragged(MouseEvent mouse) {
	}

	@Override
	public void mouseMoved(MouseEvent mouse) {
		/*
		 * Every mouse movement this method is called and it checks if the current state
		 * has a button list, if it is true then call the mouseMoved method of each
		 * button on the list
		 */
		if (State.getCurrentState().getScreen() != null) {
			State.getCurrentState().getScreen().sMouseMoved(mouse);
		} else if (State.getCurrentState().getUIButtons() != null) {
			State.getCurrentState().getUIButtons().bMouseMoved(mouse);
		}
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent mouse) {
		if ((State.getCurrentState().getScreen() != null)) {
			State.getCurrentState().getScreen().sMouseScroll(mouse);
		}
	}
}