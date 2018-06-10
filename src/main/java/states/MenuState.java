package states;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import database.DeleteGame;
import database.LoadGame;
import game.Game;
import graphics.ButtonAction;
import graphics.Text;
import graphics.UIButton;
import graphics.UIList;
import graphics.UIScrollScreen;
import loader.ImageLoader;

public class MenuState extends State {
	/*  Menu screen state it is the initial screen of the game it control the new button and the load button*/
	//Main Menu
	private UIList menuButtons;
	private BufferedImage menuBackground;
	
	//Load Buttons Menu (second Screen)
	private UIScrollScreen loadScreen;
	private BufferedImage subMenuBackground;
	private boolean loadScreenMenu;
	
	//Selected Game Menu (Third Screen)
	private UIList loadSubMenu;
	private BufferedImage gameSelectedBackground;
	private boolean gameSelected;
	
	public MenuState(Game game) {
		super(game);
		State.loadMenuState = true;
	}

	@Override
	public UIList getUIButtons() {
		/*Control of active buttons*/
		if (!gameSelected) { 
			return menuButtons;
		} else {
			return loadSubMenu;
		}
	}

	@Override
	public UIScrollScreen getScreen() {
		/*control if scroll buttons are active*/
		if (loadScreenMenu)
			return loadScreen;
		else
			return null;
	}

	@Override
	public void tick() {
		// If ESC is clicked on the menu screen then the game closes
		
		if(State.loadMenuState) { //loadMenuState is true then init menu screen
			initMenuScreen();
			State.loadMenuState = false;
		}
		
		if (game.getKeyboard().mESC == true) { //If esc was pressed
			
			if (loadScreenMenu) { //Release loadScreen memory
				loadScreenMenu = false;
				loadScreen.getButtons().clear();
				loadScreen = null;
				subMenuBackground = null;
			} else if(gameSelected) { //Release memory of the screen after choose a saved game
				gameSelected = false;
				loadSubMenu.getButtons().clear();
				loadSubMenu = null;
				gameSelectedBackground = null;
			} else { // If esc was clicked on menu then close game
				game.stop();
			}
			game.getKeyboard().mESC = false;
		}
		
		if(State.loadGame || State.newGame) // If load or new game true then it will change to gameState so release menu memory and changes state
		{
			menuButtons.getButtons().clear();
			menuButtons = null;
			menuBackground = null;
			State.setCurrentState(game.getGameState());
		}	
		
	}

	@Override
	public void render(Graphics graph) {
		if(State.loadMenuState) // Make sure that only render after menu was loaded
			return;
		// Draw the menu background image and render the UI buttons
		graph.drawImage(menuBackground, 0, 0, game.getWidth(), game.getHeight(), null);
		menuButtons.render(graph);

		if (loadScreenMenu) { 
			//Draw subMenu background and render buttons
			graph.drawImage(subMenuBackground, 0, 0, game.getWidth(), game.getHeight(), null);
			loadScreen.render(graph);
		} else if (gameSelected) {
			//Draw gameSelected background and render buttons
			graph.drawImage(gameSelectedBackground, 0, 0, game.getWidth(), game.getHeight(), null);
			loadSubMenu.render(graph);
		}
	}

	private void initMenuScreen()
	{
		/*Initialize the screen and buttons of the first menu screen*/
		menuBackground = ImageLoader.loadImage("/background/menu_backgroud.png");
		try {
			initMenuButtons();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void initLoadScreen()
	{
		/*Initialize the screen and buttons of the second menu screen (list of saved games)*/
		subMenuBackground = ImageLoader.loadImage("/background/submenu_background.png");
		initLoadScreenButtons();
	}
	
	private void initGameSelectedScreen()
	{
		/*Initialize the screen and  of the third menu screen (game selected)*/
		gameSelectedBackground = ImageLoader.loadImage("/background/load_submenu_background.png");
		initGameSelectedButtons();
	}
	
	private void initGameSelectedButtons()
	{
		/*Init buttons of the selected game load, delete and cancel*/
		BufferedImage loadSaveButton[] = new BufferedImage[2];
		BufferedImage deleteSaveButton[] = new BufferedImage[2];
		BufferedImage cancelButton[] = new BufferedImage[2];
		loadSubMenu = new UIList();
		
		loadSaveButton[0] = ImageLoader.loadImage("/button/load_submenu_d.png");
		loadSaveButton[1] = ImageLoader.loadImage("/button/load_submenu_s.png");
		
		int buttonWidth = (int) (loadSaveButton[0].getWidth() * game.getScale());
		int buttonHeight = (int) (loadSaveButton[0].getHeight() * game.getScale());

		//Load a saved game
		loadSubMenu.getButtons().add(new UIButton((int) (50 * game.getScale()), (int)(300 * game.getScale()), buttonWidth, buttonHeight, loadSaveButton, -1,
				new ButtonAction() {
					@Override
					public void action() {
						State.loadGame = true; // Tells gameState to load a game
						game.getKeyboard().mESC = true; // Set esc true to release memory from this screen (GameSelected screen)
					}
				}));

		deleteSaveButton[0] = ImageLoader.loadImage("/button/delete_submenu_d.png");
		deleteSaveButton[1] = ImageLoader.loadImage("/button/delete_submenu_s.png");
		
		//Delete a saved game
		loadSubMenu.getButtons().add(new UIButton((int)(50 * game.getScale()), (int)(430 * game.getScale()), buttonWidth, buttonHeight, deleteSaveButton, -1,
				new ButtonAction() {
					@Override
					public void action() {
						try {
							DeleteGame.Delete(State.savedGames.get(lastButtonIndex).split(" ")[0]); //Get the name of the button pressed and removes from database
						} catch (Exception e) {
							e.printStackTrace();
						}
						State.savedGames.clear(); //Clear database name loaded
						State.savedGames = null;
						game.getKeyboard().mESC = true; //Release memory from this screen (GameSelected screen)
					}
				}));
		
		cancelButton[0] = ImageLoader.loadImage("/button/cancel_submenu_d.png");
		cancelButton[1] = ImageLoader.loadImage("/button/cancel_submenu_s.png");
		
		//Cancel operation and goes back to the first menu screen
		loadSubMenu.getButtons().add(new UIButton((int)(50 * game.getScale()), (int)(550 * game.getScale()), buttonWidth, buttonHeight, cancelButton, -1,
				new ButtonAction() {
					@Override
					public void action() {
						State.savedGames.clear(); //Clear database name loaded
						State.savedGames = null;
						game.getKeyboard().mESC = true; //Release memory from this screen (GameSelected screen)
					}
				}));
	}
	
	private void initLoadScreenButtons() {
		/*Initialize all load screen buttons*/
		BufferedImage loadScreenImage = ImageLoader.loadImage("/background/scrollScreen.png");
		BufferedImage loadButton[] = new BufferedImage[2];
		int scrollSpeed = 10;
		
		//Init load screen
		loadScreen = new UIScrollScreen(loadScreenImage, (int)(31 * game.getScale()), (int)(132 * game.getScale()), (int)(loadScreenImage.getWidth() * game.getScale()), (int)(loadScreenImage.getHeight() * game.getScale()), scrollSpeed);
		
		loadButton[0] = ImageLoader.loadImage("/button/submenu_button_d.png");
		loadButton[1] = ImageLoader.loadImage("/button/submenu_button_s.png");

		float buttonWidth = loadButton[0].getWidth() * game.getScale();
		float buttonHeight = loadButton[0].getHeight() * game.getScale();
		
		Font font = new Font("Castellar", Font.PLAIN, (int)(25 * game.getScale()));
		for (int i = 0, accumulator = (int) loadScreen.getScreen().getY(); (int) i < savedGames.size(); i++) { //Accumulator controls the button position on the screen
			String split[] = savedGames.get(i).split(" "); //split the name that came from the database
			float buttonX = (float) (loadScreen.getScreen().getX() + 3);
			Text text[] = new Text[2];

			//Initialize both colors of the text and create the visible buttons
			text[0] = new Text("SaveGame " + (i+1) + " - " + split[split.length - 1], font, Color.black, (int) (buttonX - (25 * game.getScale()) + buttonWidth/4), accumulator + (int) (buttonHeight / 2));
			text[1] = new Text("SaveGame " + (i+1) + " - " + split[split.length - 1], font, Color.white, (int) (buttonX - (25 * game.getScale()) + buttonWidth/4), accumulator + (int) (buttonHeight / 2));
			
			loadScreen.getButtons().add(new UIButton((int) buttonX, accumulator,
										(int) (buttonWidth), (int) buttonHeight, loadButton, i, text,
										new ButtonAction() {
											public void action() {
												initGameSelectedScreen(); //Initialize gameSelect screen and buttons
												gameSelected = true;
												game.getKeyboard().mESC = true; // Select true to free memory used by the loadScreen
											}
										}));
			accumulator += (buttonHeight);
		}
	}

	private void initMenuButtons() throws Exception{
		// Resize the button depending of the scale attribute of the game class
		BufferedImage[] buttonNewGame = new BufferedImage[2];
		BufferedImage[] buttonLoadGame = new BufferedImage[2];
		
		buttonNewGame[0] = ImageLoader.loadImage("/button/new_game.png");
		buttonNewGame[1] = ImageLoader.loadImage("/button/new_game_b.png");
		buttonLoadGame[0] = ImageLoader.loadImage("/button/load_game.png");
		buttonLoadGame[1] = ImageLoader.loadImage("/button/load_game_b.png");

		menuButtons = new UIList();
		/*
		 * Creates the load button and add to the UI button list, the first two
		 * parameters has the position of the button on the screen it uses the
		 * game.width to centralize the button and the game.height to control
		 * the y position on the screen for every button a Button action is
		 * defined when passing the argument, this way is possible to program
		 * the button when creating it
		 */
		float buttonWidth = buttonLoadGame[0].getWidth() * game.getScale();
		float buttonHeight = buttonLoadGame[0].getHeight() * game.getScale();
		menuButtons.getButtons().add(new UIButton((int) 
						((game.getWidth() / 2) - (buttonWidth / 2)), (int) ((game.getHeight() - game.getHeight() / 3) + buttonHeight),
						(int) (buttonWidth), (int) buttonHeight, buttonLoadGame, -1,
						new ButtonAction() {
							public void action() {
								savedGames = new ArrayList<>();
								try {
									savedGames = LoadGame.loadNames();
								} catch (Exception e) {
									e.printStackTrace();
								}
								initLoadScreen();
								loadScreenMenu = true;
							}

						}));

		/*
		 * Creates the game button and add to the UI button list, the first two
		 * parameters has the position of the button on the screen it uses the
		 * game.width to centralize the button and the game.height to control
		 * the y position on the screen for every button a Button action is
		 * defined when passing the argument, this way is possible to program
		 * the button when creating it
		 */
		// Resize the button depending of the scale attribute of the game class
		buttonWidth = buttonNewGame[0].getWidth() * game.getScale();
		buttonHeight = buttonNewGame[0].getHeight() * game.getScale();
		menuButtons.getButtons()
				.add(new UIButton((int) ((game.getWidth() / 2) - (buttonWidth / 2)), (int) ((game.getHeight() - game.getHeight() / 3)),
						(int) (buttonWidth), (int) (buttonHeight), buttonNewGame, -1, new ButtonAction() {
							public void action() {
								State.newGame = true;
							}
						}));
	}
}
