package models.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import controller.input.MouseManager;
import models.pieces.Piece;
import models.pieces.PieceInfo;
import models.pieces.PieceList;

/**
 * Class with static methods used to select pieces and assess special movements
 */
public class BoardMovements {
	public static List<Coordinates> validMoves;
	public static List<Coordinates> specialMoves;
	public static List<Coordinates> validAttack;
	public static Map<PieceInfo, List<List<Coordinates>>> possiblePiecesMovements;
	public static Piece selectedPiece = null;
	public static boolean elpassant;
	public static Coordinates lastPawn;

	/**
	  * Select a piece using the mouse
	  * 
	  * @param mouse		Mouse manager used the keep track of the mouse position and left clicks
	  * @param pieceBox		Array with the pieces
	  * @param board		Game board
	  * @param turn			The color of the active player
	  * @param row			The row where the mouse clicked
	  * @param column		The column where the mouse clicked
	  */
	public static void selectPiece(final MouseManager mouse, final PieceList pieceBox[], final Square board[][],
			ColorInfo turn, int row, int column) {
		/* Select a piece using the mouse input */

		if (mouse.isLeftButtonPressed()) {
			if (row >= 8 || column >= 8) // See if mouse is out of bounds
				return;

			if (board[row][column].getRenderSquare().contains(mouse.getMouseX(), mouse.getMouseY())) {
				mouse.setLeftButtonPressed(false);
				int pieceID = board[row][column].getPieceID();

				if ((pieceID >= 0) && (board[row][column].getColor() == turn)) {
					/* Check if there is a piece in the square and if it belongs to the current turn player */

					// Select the piece
					selectedPiece = pieceBox[turn.value].getPieces()[pieceID];

					// Generate the possible movements of the piece
					selectedPiece.move(validMoves, validAttack, board,
							possiblePiecesMovements.get(selectedPiece.getType()), pieceBox);

					elPassantValidMove(selectedPiece, pieceBox, board, validMoves, specialMoves);
					// if the piece cannot be moved, ignore the selection
					if (validAttack.isEmpty() && validMoves.isEmpty()) {
						selectedPiece = null;
					} else {
						castlingValidMove(selectedPiece, pieceBox, board, validMoves, specialMoves);
					}
				}
			}
		}
	}

	/**
	 * When a king is selected check if castling is available
	 * 
	 * @param piece	 		Selected piece used to check if it`s a king
	 * @param pieceBox 		Array that has all pieces info from the game
	 * @param board Game 	Board that hold piece positions info
	 * @param validMoves 	List with all pieces valid movies
	 * @param specialMoves 	List with all special moves valid positions
	 */
	private static void castlingValidMove(Piece piece, PieceList[] pieceBox, Square[][] board,
			List<Coordinates> validMoves, List<Coordinates> specialMoves) {
		/* Check if the special movement can be activate to the selected king */
		if ((piece.getType() == PieceInfo.KING) && !piece.isMoved()) // if selectedPiece is a king and was not moved
		{

			for (int sideStep = 1; sideStep != 0; sideStep = ((sideStep > 0) ? -1 : 0)) // Look both sides
			{
				int row = piece.getActualPosition().getRow();
				int boundaries = (sideStep > 0) ? 7 : 0; // Get the rook column
				int rookID = board[row][boundaries].getPieceID(); // get ID of the rook at the boundaries
				List<Coordinates> validPath = new ArrayList<Coordinates>();

				if (rookID != -1) {
					if (!pieceBox[piece.getColor().value].getPieces()[rookID].isMoved()) // If rook was not moved then try see if Castling is valid with it
					{
						for (int counter = 0, column = piece.getActualPosition().getColumn()
								+ sideStep; counter < 2; counter++, column += sideStep) // Start walking validating the path
						{
							if (board[row][column].getPieceID() == -1) // if there is not a piece at row and column
							{
								validateMovement(board, piece, piece.getColor(), pieceBox, validPath, row, column); // and king does not walk into check add position to validPath
							}
						}

						if (validPath.size() == 2) // If size 2 it means that castling is possible
						{
							validMoves.add(validPath.get(1)); // So take the last position and add as validMoves
							specialMoves.add(validPath.get(1)); // Set this position as special to render in a different color
						}
						validPath.clear();
					}
				}
			}
		}
	}

	/**
	 * If the castling movement is valid than execute the logic to change the king position with a tower
	 * 
	 * @param piece			 Selected piece used to check if it`s a king
	 * @param pieceBox		 Array that has all pieces info from the game
	 * @param board Game	 Board that hold piece positions info
	 * @param position		 Position get from a mouse click used to check if it`s a valid square
	 * @param specialMoves	 List with all special moves valid positions
	 */
	private static void castlingMovement(Piece piece, PieceList[] pieceBox, Square[][] board, Coordinates position,
			List<Coordinates> specialMoves) {
		/*  */
		if ((piece.getType() == PieceInfo.KING) && (specialMoves.size() > 0)) // Check if the piece is a king and there is a valid specialMoves
		{
			if (specialMoves.contains(position)) // See if the position choose as destination is a special move square
			{
				int column = (position.getColumn() > 3) ? (position.getColumn() - 1) : (position.getColumn() + 1); // Get the tower position using the side of the special move
				int row = piece.getActualPosition().getRow(); // get row of the piece (if it's black 0, if it's white 7)
				int boundaries = (position.getColumn() > 3) ? 7 : 0; // Using the same idea as before get the column of the tower to be moved
				int rookID = board[row][boundaries].getPieceID(); // Get rook id

				// Change rook position at the board
				board[row][column].setPieceID(rookID);
				board[row][column].setColor(board[row][boundaries].getColor());

				// Change rook actual position at the piece
				pieceBox[piece.getColor().value].getPieces()[rookID].getActualPosition().setColumn(column);

				// Mark the place where the rook was as empty
				board[row][boundaries].setPieceID(-1);
			}
			specialMoves.clear();
		}
	}
	
	/**
	  * Used to validate the piece move
	  * 
	  * @param mouse		Mouse manager used the keep track of the mouse position and left clicks
	  * @param row			The row where the mouse clicked
	  * @param column		The column where the mouse clicked
	  * @return				True if the move is valid, false if not
	  */
	public static boolean isValidMove(MouseManager mouse, int row, int column) {
		/* Checks if the chosen movement is valid for the piece */
		if (mouse.isLeftButtonPressed()) {

			mouse.setLeftButtonPressed(false);

			// Search the validAttack and validMoves list for the chosen movement
			if (validAttack.contains(point(row, column)) || validMoves.contains(point(row, column))) {
				elPassantActive(row, column);
				validMoves.clear();
				validAttack.clear();
				return true;
			}
		}
		return false;
	}

	/**
	 * After a pawn clicked on a valid space and is moving two squares in front elPassant become true
	 * 
	 * @param row 		The row where the mouse clicked
	 * @param column 	The column where the mouse clicked
	 */
	private static void elPassantActive(int row, int column) {
		if (selectedPiece.getType() == PieceInfo.PAWN && !selectedPiece.isMoved() && !validMoves.isEmpty()) {
			elpassant = validMoves.get(0).equals(row, column);
			lastPawn = validMoves.get(0);
		}
	}

	/**
	 * Check if the actual selected pawn has any opponent pawn that jumped to his side
	 * making a elPassant move valid
	 * 
	 * @param piece  		Selected piece used to check pawn info
	 * @param pieceBox 		Array that has all pieces info from the game
	 * @param board 		Board that hold piece positions info
	 * @param validMoves	List with all pieces valid movies
	 * @param specialMoves 	List with all special moves valid positions
	 */
	private static void elPassantValidMove(Piece piece, PieceList[] pieceBox, Square[][] board,
			List<Coordinates> validMoves, List<Coordinates> specialMoves) {
		int side = piece.getColor() == ColorInfo.WHITE ? -1 : 1;

		if (piece.getType() == PieceInfo.PAWN && elpassant) // If is a pawn and el passant is possible
		{
			if (board[lastPawn.getRow()][lastPawn.getColumn()].getColor() != piece.getColor()) // Only if the lastPawn is a opponent
			{
				for (int i = 1; i != 0; i = (i > 0) ? -i : 0) // Condition to check both sides -1 left and +1 right
				{
					Coordinates position = new Coordinates(piece.getActualPosition().getRow(),
							piece.getActualPosition().getColumn() - i); // Left side of selected Pawn
					if (position.getColumn() >= 0) // Look if left side is in board
					{
						if (lastPawn.equals(position)
								&& board[lastPawn.getRow() + side][lastPawn.getColumn()].getPieceID() == -1) // See if last pawn moved is by his side
						{
							validateMovement(board, piece, piece.getColor(), pieceBox, specialMoves,
									lastPawn.getRow() + side, lastPawn.getColumn()); // Check if the king will be put at check if use el passant
							if (specialMoves.size() > 0) // If there is a special move, then add to valid move too
								validMoves.add(specialMoves.get(0));
						}
					}
				}
			}
		}
	}

	/**
	 * Update pieces position info if elPassant logic is valid and selected
	 * 
	 * @param piece 	 		Selected piece used to check pawn info
	 * @param pieceBox 			Array that has all pieces info from the game
	 * @param board 			Board that hold piece positions info
	 * @param adversaryColor	Hold the info of the opponent piece color
	 * @param point 			Mouse click coordinate
	 * @param specialMoves		List with all special moves valid positions
	 */
	private static void elPassantAttack(Piece piece, PieceList[] pieceBox, Square[][] board, int adversaryColor,
			Coordinates point, List<Coordinates> specialMoves) {
		if (elpassant && piece.getType() == PieceInfo.PAWN) // if el passant is true and piece is a pawn
		{
			if (specialMoves.contains(point)) // see if the chosen movement is the special movement
			{
				int behind = piece.getColor() == ColorInfo.WHITE ? 1 : -1;
				Coordinates deadPiece = point(point.getRow() + behind, point.getColumn()); // get the position of the piece that will die

				pieceBox[adversaryColor].getPieces()[board[deadPiece.getRow()][deadPiece.getColumn()].getPieceID()]
						.setType(PieceInfo.DEAD); // Mark piece as dead
				board[deadPiece.getRow()][deadPiece.getColumn()].setPieceID(-1); // remove it from the board
			}
			specialMoves.clear();
		}
	}
	
	/**
	  * Move the piece to the selected location
	  * 
	  * @param piece			The piece that was selected and will be moved
	  * @param board			Game board
	  * @param pieceBox			The array with all the pieces
	  * @param turn				The color of the active player
	  * @param row				Row where the piece is going
	  * @param column			Column where the piece is going
	  */
	public static void movePiece(Piece piece, Square board[][], PieceList[] pieceBox, ColorInfo turn, int row,
			int column) {
		/* Move the selected piece */
		int adversaryColor = (turn.value == ColorInfo.WHITE.value ? ColorInfo.BLACK.value : ColorInfo.WHITE.value);
		ColorInfo pieceColor = board[piece.getActualPosition().getRow()][piece.getActualPosition().getColumn()]
				.getColor(); // Get color of selectedPiece
		int pieceID = board[piece.getActualPosition().getRow()][piece.getActualPosition().getColumn()].getPieceID();

		// Update the board parameters of old position
		board[piece.getActualPosition().getRow()][piece.getActualPosition().getColumn()].setPieceID(-1);
		board[piece.getActualPosition().getRow()][piece.getActualPosition().getColumn()].setColor(null);

		// Execute pawn el passant attack if chosen
		elPassantAttack(piece, pieceBox, board, adversaryColor, point(row, column), specialMoves);

		// Execute castling movement if chosen
		castlingMovement(piece, pieceBox, board, point(row, column), specialMoves);

		// Capture enemy piece
		if ((board[row][column].getPieceID() >= 0)) {
			// Erase from pieceBox
			pieceBox[adversaryColor].getPieces()[board[row][column].getPieceID()].setType(PieceInfo.DEAD);
		}
		piece.setMoved(true);
		piece.setActualPosition(point(row, column));

		// Update the board parameters of new position
		board[row][column].setPieceID(pieceID);
		board[row][column].setColor(pieceColor);
	}

	/**
	 * Fake a movement in a copy of the board to assess later
	 * 
	 * @param piece 		Selected piece
	 * @param copy 			Copy piece array
	 * @param pieceColor 	Actual piece color info
	 * @param row 			Row the piece can move
	 * @param column 		Column the piece can move
	 */
	private static void fakeMove(Piece piece, Square copy[][], ColorInfo pieceColor, int row, int column) {
		copy[piece.getActualPosition().getRow()][piece.getActualPosition().getColumn()].setPieceID(-1);
		copy[piece.getActualPosition().getRow()][piece.getActualPosition().getColumn()].setColor(null);

		copy[row][column].setPieceID(piece.getIndex());
		copy[row][column].setColor(pieceColor);
	}

	/**
	 * See if a piece can reach the enemy king, if yes returns true
	 * 
	 * @param piece 		Selected piece
	 * @param board 		Game board
	 * @param possibleMoves All valid coordinates to move
	 * @return 				True if the enemy king is in check
	 */
	private static boolean checkKing(Piece piece, final Square board[][], List<List<Coordinates>> possibleMoves) {
		int row, column;

		if (piece.getType() == PieceInfo.PAWN) {
			row = piece.getActualPosition().getRow();
			column = piece.getActualPosition().getColumn();
			int side = piece.getColor() == ColorInfo.WHITE ? -1 : 1;
			int boundaries = piece.getColor() == ColorInfo.WHITE ? -7 : 0;

			// Inside boundaries is, depending on the pawn side, > 0 or < 7 (or > -7)
			if (side * piece.getActualPosition().getRow() > boundaries) {
				// Pawns capture diagonally
				if (column > 0) {
					// There is a piece to capture
					if (board[row + side][column - 1].getPieceID() == PieceInfo.KING.value
							&& board[row + side][column - 1].getColor() != piece.getColor()) {
						return true;
					}
				}
				// Pawns capture diagonally
				if (column < 7) {
					// There is a piece to capture
					if (board[row + side][column + 1].getPieceID() == PieceInfo.KING.value
							&& board[row + side][column + 1].getColor() != piece.getColor()) {
						return true;
					}
				}
			}
		} else {
			int i;
			for (List<Coordinates> direction : possibleMoves) { // For each possible direction

				i = 0;
				row = piece.getActualPosition().getRow() + direction.get(i).getRow();
				column = piece.getActualPosition().getColumn() + direction.get(i).getColumn();

				// While there is movements in that direction and its inside the board
				while (i < direction.size() && row < 8 && column < 8 && row >= 0 && column >= 0) {
					if (board[row][column].getPieceID() == PieceInfo.KING.value
							&& board[row][column].getColor().value != piece.getColor().value) { // Found a king of opposite color
						return true;
					} else if (board[row][column].getPieceID() != -1) // Cannot move further in that direction if a piece was found
						break;

					i++;
					if (i < direction.size()) {
						row = piece.getActualPosition().getRow() + direction.get(i).getRow();
						column = piece.getActualPosition().getColumn() + direction.get(i).getColumn();
					}
				}
			}
		}

		return false;
	}

	/**
	 * For each piece of opposite color see if it can reach the king
	 * 
	 * @param board 		Game board
	 * @param turn			Actual player turn
	 * @param pieceBox 		The array with all the pieces
	 * @return Return 		true king will be in check
	 */
	private static boolean isChecked(final Square board[][], ColorInfo turn, PieceList[] pieceBox) {
		int enemyTurn = (turn.value == ColorInfo.WHITE.value ? ColorInfo.BLACK.value : ColorInfo.WHITE.value);

		for (Piece testCheck : pieceBox[enemyTurn].getPieces()) {
			if (testCheck.getType() == PieceInfo.DEAD)
				continue; // Next piece if this one is dead
			if (checkKing(testCheck, board, possiblePiecesMovements.get(testCheck.getType()))) {
				return true; // A piece threaten the king
			}
		}
		return false; // No piece threaten the king
	}
	
	/**
	  * Check if the piece can capture another piece
	  * 
	  * @param board			Game board
	  * @param piece			The piece that was selected
	  * @param pieceColor		The color of the piece
	  * @param pieceBox			The array with all the pieces
	  * @param validList		If the attack is valid, it's appended to this list
	  * @param row				Row that the piece may be attacking
	  * @param column			Column that the piece may be attacking
	  */
	public static void validateAttack(final Square board[][], Piece piece, ColorInfo pieceColor, PieceList[] pieceBox,
			List<Coordinates> validList, int row, int column) {
		/* Mark the piece as dead to validate the movement */
		PieceInfo temp = pieceBox[board[row][column].getColor().value].getPieces()[board[row][column].getPieceID()]
				.getType();
		pieceBox[board[row][column].getColor().value].getPieces()[board[row][column].getPieceID()]
				.setType(PieceInfo.DEAD);

		validateMovement(board, piece, pieceColor, pieceBox, validList, row, column);

		/* Revive the piece */
		pieceBox[board[row][column].getColor().value].getPieces()[board[row][column].getPieceID()].setType(temp);
	}
	
	/**
	  * Check if the piece can move to a square
	  * 
	  * @param board		Game board
	  * @param piece		The piece that was selected
	  * @param pieceColor	The color of the piece
	  * @param pieceBox		The array with all the pieces
	  * @param validList	If the move is valid, it's appended to this list
	  * @param row			Row that the piece may move to
	  * @param column		Column that the piece may move to
	  */
	public static void validateMovement(final Square board[][], Piece piece, ColorInfo pieceColor, PieceList[] pieceBox,
			List<Coordinates> validList, int row, int column) {
		/* Copy the board and validates the movement, seeing if the king is left checked */
		Square copy[][];
		copy = copyBoard(board);
		fakeMove(piece, copy, pieceColor, row, column);
		if (!isChecked(copy, pieceColor, pieceBox)) { // If the king is not left checked the movement is valid
			validList.add(point(row, column));
		}
	}

	/**
	  * Check if the game is in a stalemate
	  * 
	  * @param board		Game board
	  * @param pieceBox		The array with all the pieces
	  * @param turn			The active player color
	  * @return				True if the game is in a stalemate, false otherwise
	  */
	public static boolean isStaleMate(final Square board[][], PieceList[] pieceBox, ColorInfo turn) {
		/* Stalemate happens when a player cannot move any piece and his king is not in check */
		List<Coordinates> testStalemate = new ArrayList<Coordinates>();

		for (Piece piece : pieceBox[turn.value].getPieces()) {
			if (piece.getType() == PieceInfo.DEAD) { // Ignore dead pieces
				continue;
			}
			piece.move(testStalemate, testStalemate, board, possiblePiecesMovements.get(piece.getType()), pieceBox);
			if (!testStalemate.isEmpty()) { // If the list is not empty, the piece can be moved
				return false;
			}
		}

		return true; // Impossible to move a piece
	}

	/**
	  * Used to see if a checkmate happened 
	  * 
	  * @param board		Game board
	  * @param turn			The active player color
	  * @param pieceBox		The array with all the pieces
	  * @return				True if the checkmate happened, false otherwise
	  */
	public static boolean isCheckmate(final Square board[][], ColorInfo turn, PieceList[] pieceBox) {
		Piece king = pieceBox[turn.value].getPieces()[0];
		List<Coordinates> testCheckmate = new ArrayList<Coordinates>();

		king.move(testCheckmate, testCheckmate, board, possiblePiecesMovements.get(king.getType()), pieceBox);
		if (testCheckmate.isEmpty() && isStaleMate(board, pieceBox, turn)) {
			if (isChecked(board, turn, pieceBox)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	  * Used to see if the piece is a pawn and can be promoted
	  * 
	  * @param movedPawn	The piece to be verified
	  * @return				True if the piece is a pawn AND can be promoted, false otherwise
	  */
	public static boolean promotePawn(Piece movedPawn) {
		/* When a pawn reaches the other side of the board it can be promoted to another piece */
		boolean blackPiecePromote = ((movedPawn.getType() == PieceInfo.PAWN)
				&& (movedPawn.getColor() == ColorInfo.BLACK) && (movedPawn.getActualPosition().getRow() == 7));
		boolean whitePiecePromote = ((movedPawn.getType() == PieceInfo.PAWN)
				&& (movedPawn.getColor() == ColorInfo.WHITE) && (movedPawn.getActualPosition().getRow() == 0));
		return (blackPiecePromote || whitePiecePromote);
	}
	
	/**
	  * Initialize the piece movements accordingly to the game rules
	  */
	public static void initializePieceMovements() {
		/* Determines the rules of piece movements, each list of a piece is a direction */

		validMoves = new ArrayList<Coordinates>();
		validAttack = new ArrayList<Coordinates>();
		specialMoves = new ArrayList<Coordinates>();
		possiblePiecesMovements = new HashMap<PieceInfo, List<List<Coordinates>>>();

		List<List<Coordinates>> queenMovements = new ArrayList<List<Coordinates>>();
		List<List<Coordinates>> bishopMovements = new ArrayList<List<Coordinates>>();
		List<List<Coordinates>> rookMovements = new ArrayList<List<Coordinates>>();
		List<List<Coordinates>> kingMovements = new ArrayList<List<Coordinates>>();
		List<List<Coordinates>> knightMovements = new ArrayList<List<Coordinates>>();

		for (int i = 0; i < 8; i++)
			queenMovements.add(new ArrayList<>());

		// All possible queen movements
		for (int i = 1; i <= 8; i++) {
			queenMovements.get(0).add(point(i, 0)); // up
			queenMovements.get(1).add(point(-i, 0));// down
			queenMovements.get(2).add(point(0, i)); // right
			queenMovements.get(3).add(point(0, -i));// left
			queenMovements.get(4).add(point(i, i)); // diagonal up right
			queenMovements.get(5).add(point(i, -i));// diagonal down right
			queenMovements.get(6).add(point(-i, i));// diagonal up left
			queenMovements.get(7).add(point(-i, -i));// diagonal down left
		}

		// Rook and Bishop use parts of queen movements
		for (int i = 0; i < 4; i++) {
			rookMovements.add(queenMovements.get(i));
			bishopMovements.add(queenMovements.get(i + 4));
		}

		// King movements
		kingMovements.add(Arrays.asList(point(1, 0)));
		kingMovements.add(Arrays.asList(point(1, 1)));
		kingMovements.add(Arrays.asList(point(0, 1)));
		kingMovements.add(Arrays.asList(point(1, -1)));
		kingMovements.add(Arrays.asList(point(-1, 0)));
		kingMovements.add(Arrays.asList(point(-1, 1)));
		kingMovements.add(Arrays.asList(point(0, -1)));
		kingMovements.add(Arrays.asList(point(-1, -1)));

		// Knight movements
		knightMovements.add(Arrays.asList(point(1, 2)));
		knightMovements.add(Arrays.asList(point(2, 1)));
		knightMovements.add(Arrays.asList(point(-1, 2)));
		knightMovements.add(Arrays.asList(point(-2, 1)));
		knightMovements.add(Arrays.asList(point(1, -2)));
		knightMovements.add(Arrays.asList(point(2, -1)));
		knightMovements.add(Arrays.asList(point(-1, -2)));
		knightMovements.add(Arrays.asList(point(-2, -1)));

		possiblePiecesMovements = new HashMap<>();
		possiblePiecesMovements.put(PieceInfo.KING, kingMovements);
		possiblePiecesMovements.put(PieceInfo.QUEEN, queenMovements);
		possiblePiecesMovements.put(PieceInfo.ROOK, rookMovements);
		possiblePiecesMovements.put(PieceInfo.BISHOP, bishopMovements);
		possiblePiecesMovements.put(PieceInfo.KNIGHT, knightMovements);
	}
	
	/**
	  * Copies the given board
	  * 
	  * @param board	The board to be copied
	  * @return			The copy
	  */
	public static Square[][] copyBoard(final Square[][] board) {
		Square copy[][] = new Square[8][8];
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				copy[i][j] = new Square(board[i][j]);
			}
		}
		return copy;
	}

	/**
	 * Creates a new Coordinate point using a row and column values
	 * 
	 * @param row 		Contains a row line value
	 * @param column 	Contains a column line value
	 * @return 			Return a coordinate point object with the row and column value
	 */
	private static Coordinates point(int row, int column) {
		return new Coordinates(row, column);
	}

}
