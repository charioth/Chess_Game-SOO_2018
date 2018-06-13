package view.display;

import java.awt.Canvas;
import java.awt.Dimension;
import javax.swing.JFrame;

public class Display {
	/* Class used to create the window and the canvas that allow to draw */
	private JFrame frame;
	private Canvas canvas; // Allow us to draw things on the screen

	private String title;
	private int width, height;

	public Display(String title, int width, int height) {
		this.title = title;
		this.width = width;
		this.height = height;

		createDisplay();
	}

	private void createDisplay() {
		// Set the screen title
		frame = new JFrame(title);
		// Create a canvas
		canvas = new Canvas();

		// Set the info of the screen
		frame.setSize(width, height);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // To close and not stay running behind
		frame.setResizable(false);
		frame.setLocale(null); // Center of the screen
		frame.setVisible(true); // Make it visible

		// Set the info of the canvas
		canvas.setPreferredSize(new Dimension(width, height));
		canvas.setMaximumSize(new Dimension(width, height));
		canvas.setMinimumSize(new Dimension(width, height));
		canvas.setFocusable(false);

		// Add the canvas to the screen
		frame.add(canvas);
		frame.pack();
	}

	public void closeDisplay() {
		// Dispose of the frame window
		frame.dispose();
	}

	public Canvas getCanvas() {
		return canvas;
	}

	public JFrame getFrame() {
		return frame;
	}
}
