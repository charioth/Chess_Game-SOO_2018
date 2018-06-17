package controller.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyManager implements KeyListener {
	/* Class responsible to control what keys was pressed */
	private boolean[] keys;
	public boolean mESC;
	private boolean once = true;

	/**
	 * Initialize a class instance, mapping the keyboard keys
	 */
	public KeyManager() {
		/* Boolean buffer to map every possible key */
		keys = new boolean[256];
	}

	/**
	 * Check if the key on the ESCAPE position was pressed each tick once attribute
	 * controls that the click happens only one time (tick executes pretty fast so
	 * it needs a control to not allow multiples call
	 */
	public void tick() {
		if (keys[KeyEvent.VK_ESCAPE] & once) {
			mESC = true;
			once = false;
		}
	}

	@Override
	public void keyPressed(KeyEvent key) {
		// Sets the key pressed at the key code position
		keys[key.getKeyCode()] = true;
	}

	@Override
	public void keyReleased(KeyEvent key) {
		// Sets that the key was released at the key code position
		keys[key.getKeyCode()] = false;
		once = true;
	}

	@Override
	public void keyTyped(KeyEvent key) {
	}
}
