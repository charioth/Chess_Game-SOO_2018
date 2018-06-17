package controller.states;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import database.LoadGame;
import database.SaveGame;
import models.game.BoardMovements;
import models.game.ColorInfo;
import models.game.Coordinates;
import models.game.Game;
import models.game.Square;
import models.pieces.PieceInfo;
import models.pieces.PieceList;
import view.graphics.ButtonAction;
import view.graphics.UIButton;
import view.graphics.UIList;
import view.graphics.UIScrollScreen;
import view.loader.ImageLoader;

public class GameState extends State {
	/*
	 * Game screen state it is the game screen where all the movements and action
	 * occurs
	 */

	// Game logic
	private ColorInfo actualTurn; // Turn of the actual player, since there is only two color than a color is what
									// is need to control the turn
	private Square[][] board;
	private PieceList pieceBox[];

	// Screen control
	private UIList subMenuButtons; // SubMenu buttons Quit,Save,Draw and Continue
	private UIList drawButtons; // Yes or No
	private boolean subMenu; // if true it show on the screen the exit menu (open with ESC key)
	private boolean drawOption; // if the person select the draw option active to change the buttons
	private boolean releaseMenu;

	private boolean endGame; // Used to render the image
	private boolean checkmated; // Tell that the game to set winner message

	private boolean promoteMenu; // When a pawn get to the end of the table

	private BufferedImage winnerMessage;// Depending of the ending this image change (possible screens white victory,
										// black victory and draw messages)
	private BufferedImage background; // Board background
	private BufferedImage renderPieceBox[][]; // Matrix 2x6 used to render the pieces

	// Promotion render info
	private List<Rectangle> promotionChoices;
	private BufferedImage promoteBackground;

	public BufferedImage gameLogo; // SubMenu logo
	private BufferedImage acceptDraw;
	private int logoWidth;
	private int logoHeight;

	private BufferedImage moveSquare;
	private BufferedImage attackSquare;
	private BufferedImage selectSquare;
	private BufferedImage specialSquare;
	private int squareSize;
	private int moveDistance; // Used to init board rectangle x,y
	private int edge; // Used to init board rectangle x,y

	/**
	 * Initializes the game state
	 * 
	 * @param game
	 *            The game object to be initialized
	 */
	public GameState(Game game) {
		super(game);
	}

	// ("/button/draw_request_w.png"));
	@Override
	public UIScrollScreen getScreen() {
		return null;
	}

	/**
	 * Contains the game logic, used to initialize the game and run it, also
	 * controlling its submenus and database saves and loads
	 */
	@Override
	public void tick() {
		/* tick method that runs 60 times per second */
		if (State.newGame) { // If newGame boolean attribute is set true than it means that on the menu
								// state, the new game button was pressed
			newGame(); // initialize a new game table (all piece position at the start condition)
		} else if (State.loadGame) { // if the loadGame was set than on the menu state the load button was pressed,
										// so load the game that was been played
			loadGame(State.savedGames.get(lastButtonIndex).split(" ")[0]); // load the game
		}

		if (promoteMenu) { // If promote occured
			if (game.getMouse().isLeftButtonPressed()) { // Wait left mouse button click
				ColorInfo enemyTurn = (actualTurn == ColorInfo.WHITE) ? ColorInfo.BLACK : ColorInfo.WHITE;
				int counter = 1;
				for (Rectangle position : promotionChoices) {
					if (position.contains(game.getMouse().getMouseX(), game.getMouse().getMouseY())) {
						BoardMovements.selectedPiece.setType(PieceInfo.values()[counter]);
						BoardMovements.selectedPiece = null;
						if (BoardMovements.isCheckmate(board, enemyTurn, pieceBox)) {
							endGame = true;
							checkmated = true;
						} else if (BoardMovements.isStaleMate(board, pieceBox, enemyTurn)) {
							endGame = true;
						} else {
							actualTurn = enemyTurn; // Change turn
						}
						promotionChoices.clear(); // Releases memory used to render the promotion
						promotionChoices = null;
						promoteBackground = null;
						specialSquare = null;
						promoteMenu = false;
						break;
					}
					counter++;
				}
			}
			return;
		}

		if (endGame) { // If endGame is true than it waits util left mouse button is clicked to change
						// back to menu state
			if (checkmated) {
				checkmated = false;
				if (actualTurn == ColorInfo.WHITE)
					winnerMessage = ImageLoader.loadImage("/background/congratulations_white.png"); // white piece
																									// victory image
				else
					winnerMessage = ImageLoader.loadImage("/background/congratulations_black.png"); // black piece
																									// victory image
			}
			if (game.getMouse().isLeftButtonPressed()) { // Wait left mouse button click
				State.loadMenuState = true;
				exitGameState();
				State.loadMenuState = true;
				State.setCurrentState(game.getMenuState()); // Change state
			}
		} else if (!subMenu) { // If submenu is false then the game is been played
			if (subMenu = game.getKeyboard().mESC) {// If the ESC key was pressed then active the subMenu
				initSubMenuScreen(); // Load SubMenu image and buttons (Quit, Save, Draw and Continue
			}
			if (BoardMovements.selectedPiece == null) { // If there is not a piece selected
				BoardMovements.selectPiece(game.getMouse(), pieceBox, board, actualTurn,
						(game.getMouse().getMouseY() - edge) / moveDistance,
						(game.getMouse().getMouseX() - edge) / moveDistance); // executes the function that select a
																				// piece
			} else if (BoardMovements.isValidMove(game.getMouse(), (game.getMouse().getMouseY() - edge) / moveDistance,
					(game.getMouse().getMouseX() - edge) / moveDistance)) { // if there is a piece selected then wait
																			// until the player click on a valid
																			// position
				BoardMovements.movePiece(BoardMovements.selectedPiece, board, pieceBox, actualTurn,
						(game.getMouse().getMouseY() - edge) / moveDistance,
						(game.getMouse().getMouseX() - edge) / moveDistance);
				if (!(promoteMenu = BoardMovements.promotePawn(BoardMovements.selectedPiece))) { // if promote not true
																									// than chance turn
					ColorInfo enemyTurn = (actualTurn == ColorInfo.WHITE) ? ColorInfo.BLACK : ColorInfo.WHITE;
					if (BoardMovements.isCheckmate(board, enemyTurn, pieceBox)) {
						endGame = true;
						checkmated = true;
					} else if (BoardMovements.isStaleMate(board, pieceBox, enemyTurn)) {
						winnerMessage = ImageLoader.loadImage("/background/draw_game_background.png"); // Set ending
																										// message to a
																										// draw
						endGame = true;
					} else {
						BoardMovements.selectedPiece = null; // Deselect piece
						actualTurn = enemyTurn; // Change turn
					}
				} else {
					initPromotion(); // Initialize promotion rectangles to get the choose piece
				}
			}
		} else if (releaseMenu) { // Fre memory if the submenu was closed (because endgame = false and subMenu =
									// true and release = true, it means close submenu screen)
			releaseMenu = false;
			subMenu = false;
			releaseUIButtons();
		}
	}

	/**
	 * Render the game in the screen
	 * 
	 * @param graph
	 *            Used to render the game in the canvas
	 */
	@Override
	public void render(Graphics graph) {
		if (State.loadGame || State.newGame) {
			return;
		}
		/* Method responsible to render the game */
		graph.drawImage(background, 0, 0, game.getWidth(), game.getHeight(), null); // Draw the background

		if (BoardMovements.selectedPiece != null) { // if there is a piece selected then draw the highlight positions
			renderHighlightPath(graph);
		}
		renderPieces(graph); // Draw all the pieces

		if (promoteMenu) {
			renderPromoteChoices(graph);
		}

		if (subMenu) {
			renderSubMenu(graph); // Draw the subMenu if esc key was pressed
		}

		if ((endGame && winnerMessage != null)) { // Draw the winner msg
			graph.drawImage(winnerMessage, 0, 0, (int) (winnerMessage.getWidth() * game.getScale()),
					(int) (winnerMessage.getHeight() * game.getScale()), null);
		}
	}

	/**
	 * Render the screen of options when promote a pawn
	 * 
	 * @param graph
	 *            Used to render the game in the canvas
	 */
	private void renderPromoteChoices(Graphics graph) {
		/* Show on the screen the promote pieces */
		int i = 1;
		graph.drawImage(promoteBackground, 0, 0, game.getWidth(), game.getHeight(), null); // Draw the background
		for (Rectangle position : promotionChoices) {
			graph.drawImage(renderPieceBox[actualTurn.value][i], (int) position.getX(), (int) position.getY(),
					(int) position.getWidth(), (int) position.getHeight(), null);
			i++;
		}
	}

	/**
	 * When a piece is selected, call this method to render the highlight squares of
	 * valid, attack and selected positions
	 * 
	 * @param graph
	 *            Used to render the game in the canvas
	 */
	private void renderHighlightPath(Graphics graph) {
		/* Method that highlight valid, attack and selected positions */

		// Get selected piece position to hightlight the purple square on the same spot
		int row = BoardMovements.selectedPiece.getActualPosition().getRow();
		int column = BoardMovements.selectedPiece.getActualPosition().getColumn();

		// Draw purple square
		graph.drawImage(selectSquare, board[row][column].getRenderSquare().x, board[row][column].getRenderSquare().y,
				squareSize, squareSize, null);

		// For every valid position draw a blue square on the position
		for (Coordinates valid : BoardMovements.validMoves) {
			graph.drawImage(moveSquare, board[valid.getRow()][valid.getColumn()].getRenderSquare().x,
					board[valid.getRow()][valid.getColumn()].getRenderSquare().y, squareSize, squareSize, null);
		}

		// For every valid attack position draw a red square on the position
		for (Coordinates valid : BoardMovements.validAttack) {
			graph.drawImage(attackSquare, board[valid.getRow()][valid.getColumn()].getRenderSquare().x,
					board[valid.getRow()][valid.getColumn()].getRenderSquare().y, squareSize, squareSize, null);
		}

		for (Coordinates valid : BoardMovements.specialMoves) {
			graph.drawImage(specialSquare, board[valid.getRow()][valid.getColumn()].getRenderSquare().x,
					board[valid.getRow()][valid.getColumn()].getRenderSquare().y, squareSize, squareSize, null);
		}
	}

	/**
	 * Draw all pieces on the screen using the piece position info and images
	 * 
	 * @param graph
	 *            Used to render the game in the canvas
	 */
	private void renderPieces(Graphics graph) {
		/* Method that draw all pieces on the screen */

		for (int j = 0; j < 2; j++) { // j = 0 Draw white pieces, j = 1 white pieces

			for (int i = 0; i < 16; i++) { // Draw all pieces on the pieceBox
				// Get pieces info to draw
				PieceInfo type = pieceBox[j].getPieces()[i].getType();
				Coordinates piecePoint = pieceBox[j].getPieces()[i].getActualPosition();

				// If the color is null than this piece is death so continue
				if (type == PieceInfo.DEAD)
					continue;

				graph.drawImage(renderPieceBox[j][type.value],
						board[piecePoint.getRow()][piecePoint.getColumn()].getRenderSquare().x,
						board[piecePoint.getRow()][piecePoint.getColumn()].getRenderSquare().y, squareSize, squareSize,
						null);
			}
		}
	}

	/**
	 * Render the submenu when esc button is clicked, to show options of save, exit
	 * and propose draw
	 * 
	 * @param graph
	 *            Used to render the game in the canvas
	 */
	private void renderSubMenu(Graphics graph) {
		/* Draw the subMenu */
		graph.drawImage(gameLogo, 0, (int) (100 * game.getScale()), logoWidth, logoHeight, null);

		if (drawOption) { // if draw was selected then show the message "Accept draw request"
			graph.drawImage(acceptDraw, (int) (160 * game.getScale()), (int) ((530 * game.getScale())),
					(int) (acceptDraw.getWidth() * game.getScale()), (int) (acceptDraw.getHeight() * game.getScale()),
					null);
		}
		getUIButtons().render(graph); // render buttons on the screen
	}

	/**
	 * Control what buttons should be rendered, depending on the submenu
	 * 
	 * @return The submenu to be rendered, if there is any
	 */
	@Override
	public UIList getUIButtons() {
		if (drawOption) {
			return drawButtons;
		} else if (subMenu) {
			return subMenuButtons;
		}
		return null;
	}

	/**
	 * Internal method used to map the squares of the table in screen coordinates
	 * 
	 * @param line
	 *            Array index of the piece inside the matrix board used to map to
	 *            coordinates
	 * @return coordinate mapped value of the index in pixels
	 */
	private int move(int line) {
		return edge + (line * moveDistance);
	}

	/**
	 * Render the promotion background and possible pieces when a pawn get to the
	 * and of the board to select
	 * 
	 */
	private void initPromotion() {
		int step = (int) (145 * game.getScale());
		promotionChoices = new ArrayList<>();
		promoteBackground = ImageLoader.loadImage("/background/promote.png");
		for (int i = 0, initialPosition = squareSize*2; i < 4; i++, initialPosition += step) {
			promotionChoices.add(new Rectangle(initialPosition, (int) ((game.getHeight() / 2) + (game.getScale() * 10)),
					squareSize, squareSize));
		}
	}

	/**
	 * Load in memory all images info of the draw buttons, like positions, images to
	 * render, size, width and height
	 * 
	 */
	private void initDrawMenuButtons() {
		BufferedImage[] buttonYes = new BufferedImage[2];
		BufferedImage[] buttonNo = new BufferedImage[2];

		buttonYes[0] = ImageLoader.loadImage("/button/yes_w.png");
		buttonYes[1] = ImageLoader.loadImage("/button/yes_b.png");
		buttonNo[0] = ImageLoader.loadImage("/button/no_w.png");
		buttonNo[1] = ImageLoader.loadImage("/button/no_b.png");

		drawButtons = new UIList();

		float buttonWidth = buttonYes[0].getWidth() * game.getScale();
		float buttonHeight = buttonYes[0].getHeight() * game.getScale();

		/*
		 * Add to the draw button list the yes button, this button is used to accept the
		 * draw proposal when draw option was selected if active the end game, close the
		 * subMenu and set the winnerMessage to a draw message
		 */
		drawButtons.getButtons()
				.add(new UIButton((int) ((game.getWidth() / 2) - (250 * game.getScale())),
						(int) ((670 * game.getScale())), (int) (buttonWidth), (int) (buttonHeight), buttonYes, -1,
						new ButtonAction() {
							public void action() { // If this button was selected
								endGame = true; // Set the game to end
								winnerMessage = ImageLoader.loadImage("/background/draw_game_background.png"); // Set
																												// ending
																												// message
																												// to a
																												// draw
								subMenu = false;
								drawOption = false; // Set draw option to close
								game.getKeyboard().mESC = false; // Set ESC key to not pressed
							}
						}));

		buttonWidth = buttonNo[0].getWidth() * game.getScale();
		buttonHeight = buttonNo[0].getHeight() * game.getScale();

		/*
		 * Add to the draw button list the no button, this button is used negate the
		 * draw proposal when draw option was selected if clicked the no button closes
		 * the draw message and the menu and go back to the game
		 */
		drawButtons.getButtons()
				.add(new UIButton((int) ((game.getWidth() / 2) + (120 * game.getScale())),
						(int) ((670 * game.getScale())), (int) (buttonWidth), (int) (buttonHeight), buttonNo, -1,
						new ButtonAction() {
							public void action() { // If this button was selected
								drawOption = false; // Set draw option to close
								releaseMenu = true; // Let release menu memory be free and close the menu
								game.getKeyboard().mESC = false; // Set ESC key to not pressed
							}
						}));

	}

	/**
	 * Load in memory all images info of the submenu buttons/banner, like positions,
	 * images to render, size, width and height
	 */
	private void initSubMenuButtons() {
		/* Method that initialize all buttons used on the game state */
		BufferedImage[] buttonSave = new BufferedImage[2];
		;
		BufferedImage[] buttonQuit = new BufferedImage[2];
		BufferedImage[] buttonDraw = new BufferedImage[2];
		;
		BufferedImage[] buttonContinue = new BufferedImage[2];

		buttonSave[0] = ImageLoader.loadImage("/button/save_w.png");
		buttonSave[1] = ImageLoader.loadImage("/button/save_b.png");
		buttonQuit[0] = ImageLoader.loadImage("/button/quit_w.png");
		buttonQuit[1] = ImageLoader.loadImage("/button/quit_b.png");
		buttonDraw[0] = ImageLoader.loadImage("/button/draw_w.png");
		buttonDraw[1] = ImageLoader.loadImage("/button/draw_b.png");
		buttonContinue[0] = ImageLoader.loadImage("/button/continue_w.png");
		buttonContinue[1] = ImageLoader.loadImage("/button/continue_b.png");

		float buttonWidth = buttonQuit[0].getWidth() * game.getScale();
		float buttonHeight = buttonQuit[0].getHeight() * game.getScale();

		subMenuButtons = new UIList();
		/*
		 * Add to the subMenu list the Quit button, this button is used to give up and
		 * quit the game screen for every button a Button action is defined when passing
		 * the argument, this way is possible to program the button when creating it
		 */
		subMenuButtons.getButtons().add(new UIButton((int) (150 * game.getScale()), (int) ((550 * game.getScale())),
				(int) (buttonWidth), (int) (buttonHeight), buttonQuit, -1, new ButtonAction() {
					public void action() { // If this button was selected
						/* Quit button action implements */
						// Active the end game, if the turn is white and it clicked on the quit it means
						// white piece loses, else
						// black pieces clicked the white piece wins
						endGame = true;
						checkmated = true;
						actualTurn = actualTurn == ColorInfo.WHITE ? ColorInfo.BLACK : ColorInfo.WHITE; // Change the
																										// turn to the
																										// winner turn
						game.getKeyboard().mESC = false;// Set ESC key to not pressed
					}
				}));

		buttonWidth = buttonSave[0].getWidth() * game.getScale();
		buttonHeight = buttonSave[0].getHeight() * game.getScale();

		/*
		 * Add to the subMenu list the Save button, this button is used to save the game
		 * and go back to the menu state
		 */
		subMenuButtons.getButtons()
				.add(new UIButton((int) ((game.getWidth() / 2) - (buttonWidth / 2)), (int) ((550 * game.getScale())),
						(int) (buttonWidth), (int) (buttonHeight), buttonSave, -1, new ButtonAction() {
							public void action() { // If this button was selected
								/* Save button action implements */
								// System.out.println("Save a game name");
								try {
									SaveGame.save(actualGameName ,actualTurn.value, pieceBox);
								} catch (Exception e) {
									e.printStackTrace();
								}
								releaseMenu = true; // Let release menu memory be free and close the menu
								game.getKeyboard().mESC = false; // Set ESC key to not pressed

							}

						}));

		buttonWidth = buttonDraw[0].getWidth() * game.getScale();
		buttonHeight = buttonDraw[0].getHeight() * game.getScale();

		/*
		 * Add to the subMenu list the Draw button, this button is used to propose a
		 * draw game. When selected it change the buttons on the screen
		 */
		subMenuButtons.getButtons().add(new UIButton((int) (740 * game.getScale()), (int) ((550 * game.getScale())),
				(int) (buttonWidth), (int) (buttonHeight), buttonDraw, -1, new ButtonAction() {
					public void action() { // If this button was selected
						initDrawMenuScreen(); // Active draw buttons
						drawOption = true; // Active draw msg and change what buttons will be active (Yes and No will
											// active)
						game.getKeyboard().mESC = false; // Set ESC key to not pressed
					}

				}));

		buttonWidth = buttonContinue[0].getWidth() * game.getScale();
		buttonHeight = buttonContinue[0].getHeight() * game.getScale();

		/*
		 * Add to the subMenu list the continue button, this button is used to close the
		 * subMenu and go back to the game
		 */
		subMenuButtons.getButtons()
				.add(new UIButton((int) ((game.getWidth() / 2) - (buttonWidth / 2)), (int) ((670 * game.getScale())),
						(int) (buttonWidth), (int) (buttonHeight), buttonContinue, -1, new ButtonAction() {
							public void action() { // If this button was selected
								releaseMenu = true; // Let release menu memory be free and close the menu
								game.getKeyboard().mESC = false; // Set ESC key to not pressed
							}

						}));
	}

	/**
	 * When occurs a draw game, call this method to load the draw background
	 */
	private void initDrawMenuScreen() {
		acceptDraw = ImageLoader.loadImage("/button/draw_request_w.png");
		initDrawMenuButtons();
	}

	/**
	 * Load in memory the banner of the submenu
	 */
	private void initSubMenuScreen() {
		gameLogo = ImageLoader.loadImage("/background/exit_logo.png");
		logoWidth = (int) (((float) gameLogo.getWidth()) * game.getScale());
		logoHeight = (int) (((float) gameLogo.getHeight()) * game.getScale());
		initSubMenuButtons();
	}

	/**
	 * Load in memory all images used in game, like board, squares and pieces
	 * 
	 */
	private void initGameAssets() {
		/* Load All utility Info for the game like board, square and pieces */
		// Render part
		int edgeSize = 52;
		int blueLineSize = 5;
		background = ImageLoader.loadImage("/background/board.png"); // Game Background

		renderPieceBox = new BufferedImage[2][6];
		for (int i = 0; i < 6; i++) { // 6 is the number of different pieces
			renderPieceBox[0][i] = ImageLoader.loadImage("/pieces/w_" + i + ".png");
			renderPieceBox[1][i] = ImageLoader.loadImage("/pieces/b_" + i + ".png");
		}

		moveSquare = ImageLoader.loadImage("/background/blue_square.png");
		attackSquare = ImageLoader.loadImage("/background/red_square.png");
		selectSquare = ImageLoader.loadImage("/background/purple_square.png");
		specialSquare = ImageLoader.loadImage("/background/yellow_square.png");
		squareSize = (int) (((float) selectSquare.getWidth() - 2) * game.getScale());
		moveDistance = (int) ((float) (selectSquare.getWidth() - 2 + blueLineSize) * game.getScale());
		edge = (int) Math.round((((float) edgeSize * game.getScale())));
		// Logic part
		BoardMovements.initializePieceMovements(); // Pieces movement rules
	}

	/**
	 * Release all submenu images from memory
	 */
	private void releaseUIButtons() {
		gameLogo = null;
		if (subMenuButtons != null) {
			subMenuButtons.getButtons().clear();
			subMenuButtons = null;
			if (drawButtons != null) {
				drawButtons.getButtons().clear();
				drawButtons = null;
				acceptDraw = null;
			}
		}
	}

	/**
	 * Method that initialize all pieces at default position
	 */
	private void newGame() {
		board = new Square[8][8]; // Table of the game
		pieceBox = new PieceList[2]; // PieceBox has the a array list with all the white pieces and other with the
										// black pieces
		initGameAssets();
		actualGameName = null;
		State.newGame = false;
		actualTurn = ColorInfo.WHITE;
		pieceBox[0] = new PieceList(ColorInfo.WHITE);
		pieceBox[1] = new PieceList(ColorInfo.BLACK);
		initBoard();
		if (State.savedGames != null) {
			State.savedGames.clear();
			State.savedGames = null;
		}
	}

	/**
	 * Method that initialize all pieces positions using a saved game from database
	 * 
	 * @param gameName
	 *            Name of the game selected to load from database
	 */
	private void loadGame(String gameName) {
		/* Load the game using a saved game name */
		actualGameName = gameName;
		board = new Square[8][8]; // Table of the game
		pieceBox = new PieceList[2];
		pieceBox[0] = new PieceList();
		pieceBox[1] = new PieceList();
		State.loadGame = false;
		initGameAssets();
		try {
			actualTurn = LoadGame.loadGame(gameName, pieceBox);
		} catch (Exception e) {
			e.printStackTrace();
		}

		State.savedGames.clear();
		State.savedGames = null;
		initBoard();
	}

	/**
	 * Release all variables memory used to store the game logic
	 */
	private void exitGameState() {
		/* Release all memory used by the game State */
		game.getMouse().setLeftButtonPressed(false);
		game.getKeyboard().mESC = false;

		/* Completes release all boardMoviments */
		BoardMovements.selectedPiece = null;
		BoardMovements.validAttack.clear();
		BoardMovements.validMoves.clear();
		BoardMovements.possiblePiecesMovements.clear();

		board = null;
		pieceBox = null;
		actualTurn = null;

		releaseUIButtons();

		subMenu = false;
		drawOption = false;
		releaseMenu = false;

		winnerMessage = null;
		background = null;
		endGame = false;
	}

	/**
	 * Initialize all square board coordinates in pixels
	 * 
	 */
	private void initBoard() {
		// Init all squares and pieces positions
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				board[i][j] = new Square();
				board[i][j].setRenderSquare(new Rectangle(move(j), move(i), squareSize, squareSize));
			}
		}

		// Get the piece position and set the square ID and color to the piece that is
		// there
		// (Obs: ID of a piece is the position on the array, example king is 0, queen is
		// 1 etc..)
		for (int i = 0; i < 2; i++) {
			ColorInfo color = pieceBox[i].getPieces()[0].getColor();
			for (int j = 0, row, column; j < 16; j++) {
				row = pieceBox[i].getPieces()[j].getActualPosition().getRow();
				column = pieceBox[i].getPieces()[j].getActualPosition().getColumn();
				board[row][column].setPieceID(j);
				board[row][column].setColor(color);
			}
		}
	}
}
