package models.game;

import java.awt.Graphics;
import java.awt.image.BufferStrategy;

import controller.input.KeyManager;
import controller.input.MouseManager;
import controller.states.GameState;
import controller.states.MenuState;
import controller.states.State;
import view.display.Display;

public class Game implements Runnable {
	/*
	 * Class responsible for starting the game by calling all Main classes as
	 * attributes and main methods of tick (), render () and run
	 */

	// Screen Attributes
	private String name;
	private int screen_size = 1052;
	private int width, height;
	private float scale;

	private Display display;
	private BufferStrategy bs;
	private Graphics graph;

	// Possible states (states are the possible screens)
	private GameState gameState;
	private MenuState menuState;

	// FPS Lock constant
	private final int fps = 60;
	private final double timerPerTick = 1000000000 / (double) fps;

	// Input Manager
	private KeyManager keyboard;
	private MouseManager mouse;

	// Thread
	private Thread thread;

	// Stop condition
	private boolean running;

	/**
	 * Create the game object, which will run the game
	 * 
	 * @param name
	 *            Name that will be displayed in the window
	 * @param scale
	 *            Window scale
	 */
	public Game(String name, float scale) {
		// Constructor of game class, initialize width, height and name to set screen
		if (scale <= 0)
			System.exit(0);
		this.name = name;
		width = (int) (screen_size * scale);
		height = (int) (screen_size * scale);
		this.scale = scale;
	}

	private void init() {
		// Create screen
		display = new Display(name, width, height);
		// Inputs
		keyboard = new KeyManager();
		mouse = new MouseManager();
		// States
		gameState = new GameState(this);
		menuState = new MenuState(this);

		// For the input to work the screen must know that it should respond, so add
		// mouse and keyboard to the Frame and Canvas
		display.getFrame().addMouseListener(mouse);
		display.getFrame().addMouseMotionListener(mouse);
		display.getFrame().addMouseWheelListener(mouse);
		display.getFrame().addKeyListener(keyboard);
		display.getCanvas().addMouseListener(mouse);
		display.getCanvas().addMouseMotionListener(mouse);
		display.getCanvas().addMouseWheelListener(mouse);
		// Set initial state (initial screen) in this case menu state
		State.setCurrentState(menuState);
	}

	private void tick() {
		/*
		 * Tick method is called 60 times per second by the run method. It call the
		 * method of the current state selected
		 */

		// Tick to see if any key was pressed
		keyboard.tick();
		// Call current state tick method
		if (State.getCurrentState() != null) {
			State.getCurrentState().tick();
		}
	}

	private void render() {
		/*
		 * Method responsible for controlling the canvas doble buffer control It creates
		 * the buffer if it was not already created clean the screen and draw
		 */

		// if there is not a buffer to swap when drawing, then create a buffer
		if (bs == null) {
			display.getCanvas().createBufferStrategy(3);
		}
		// Get the created buffer and initialize the buffer attibute
		bs = display.getCanvas().getBufferStrategy();
		// Pass the screen to draw to the graph attribute
		graph = bs.getDrawGraphics();
		// Clean screen
		graph.clearRect(0, 0, width, height);

		// Call render method of the current state
		if (State.getCurrentState() != null) {
			State.getCurrentState().render(graph);
		}

		bs.show();
		graph.dispose();
	}

	/**
	 * Contains the main loop that keeps the game running, until asked to stop
	 */
	@Override
	public void run() {
		/*
		 * Run has the main loop of the game if executes the tick and render of the game
		 * 60 times per second
		 */

		// Delta has the somatory of the difference between the last tick and the actual
		// tick
		double delta = 0.0;
		long now; // Actual time
		long lastTime = System.nanoTime(); // Last time

		init();

		while (running) {
			now = System.nanoTime();
			delta += (double) (now - lastTime) / timerPerTick;
			lastTime = now;

			// When delta becomes bigger than one, it means that 1 frame is ready to draw
			if (delta >= 1) {
				delta--;
				this.tick();
				this.render();
			}
		}
	}

	/**
	 * Starts a thread, causing the object's run method to be called in that
	 * separately executing thread
	 */
	public synchronized void start() {
		// Method used to start the thread and initialize the running program
		if (running == true)
			return;
		running = true;
		thread = new Thread(this);
		thread.start();
	}

	/**
	 * Stop the thread and closes the game
	 */
	public synchronized void stop() {
		// Stop the program, it executes when close the program
		if (running == false)
			return;
		display.closeDisplay();
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	public GameState getGameState() {
		return gameState;
	}

	public MenuState getMenuState() {
		return menuState;
	}

	public MouseManager getMouse() {
		return mouse;
	}

	public KeyManager getKeyboard() {
		return keyboard;
	}

	public void setRunning(boolean exit) {
		running = exit;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public float getScale() {
		return scale;
	}
}
