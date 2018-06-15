package controller.states;

import java.awt.Graphics;
import java.util.ArrayList;

import models.game.Game;
import view.graphics.UIList;
import view.graphics.UIScrollScreen;

public abstract class State {
	/*
	 * Super class that every state of the game extends it controls the current state using a static variable and control when execute the newGame and loadGame
	 */
	private static State currentState = null;
	public static int lastButtonIndex;
	// MenuState to gameState signals
	protected static boolean newGame;
	protected static boolean loadGame;
	protected static ArrayList<String> savedGames;

	// GameState to MenuState signals
	protected static boolean loadMenuState;

	protected Game game;
	
	/**
	 * Initializes the game states
	 * 
	 * @param game The game object to be initialized
	 */
	State(Game game) {
		this.game = game;
		savedGames = new ArrayList<>();
		lastButtonIndex = 0;
	}

	public static void setCurrentState(State state) {
		/* Change current state */
		currentState = state;
	}

	public static State getCurrentState() {
		/* return current state */
		return currentState;
	}

	// List of buttons that the state has to call
	public abstract UIList getUIButtons();

	public abstract UIScrollScreen getScreen();

	public abstract void tick();

	public abstract void render(Graphics graph);
}
