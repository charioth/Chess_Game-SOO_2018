package models.pieces;

import java.util.List;

import org.bson.Document;

import models.game.BoardMovements;
import models.game.ColorInfo;
import models.game.Coordinates;
import models.game.Square;

public class Piece {
	private Coordinates actualPosition;
	private int index; // Position of the piece on the pieceList
	private PieceInfo type;
	private boolean moved;
	private ColorInfo color;

	/**
	 * Empty constructor
	 */
	public Piece() {
	}

	/**
	 * Instantiate a chess piece
	 * 
	 * @param actualPosition
	 *            The piece position in the board
	 * @param type
	 *            The piece type
	 * @param moved
	 *            If the piece was moved in the game or not
	 * @param color
	 *            The piece color
	 */
	public Piece(Coordinates actualPosition, PieceInfo type, boolean moved, ColorInfo color) {
		this.actualPosition = actualPosition;
		this.type = type;
		this.moved = moved;
		this.color = color;
	}

	/**
	 * Calculates all the possible piece movements, considering the type of the
	 * piece but not all special rules
	 * 
	 * @param validMoves
	 *            All movements that the piece can make without capturing another
	 *            piece are returned here
	 * @param validAttack
	 *            All movements that the piece can make while capturing another
	 *            piece are returned here
	 * @param board
	 *            Game board
	 * @param possibleMoves
	 *            The possible moves a piece can do, basically the rules that govern
	 *            its movements
	 * @param pieceBox
	 *            The array with all the pieces
	 */
	public void move(List<Coordinates> validMoves, List<Coordinates> validAttack, final Square board[][],
			List<List<Coordinates>> possibleMoves, PieceList[] pieceBox) {
		/* Choose a movement method based on piece type */
		if (this.type == PieceInfo.PAWN) {
			this.pawnMovements(validMoves, validAttack, board, pieceBox);
		} else {
			this.pieceMovements(validMoves, validAttack, board, possibleMoves, pieceBox);
		}
	}

	/**
	 * Calculates all the possible pawn movements not considering all special rules
	 * 
	 * @param validMoves
	 *            All movements that the pawn can make without capturing another
	 *            piece are returned here
	 * @param validAttack
	 *            All movements that the pawn can make while capturing another piece
	 *            are returned here
	 * @param board
	 *            Game board
	 * @param pieceBox
	 *            The array with all the pieces
	 */
	private void pawnMovements(List<Coordinates> validMoves, List<Coordinates> validAttack, final Square board[][],
			PieceList[] pieceBox) {

		int row = this.getActualPosition().getRow(), column = this.getActualPosition().getColumn();
		int side = this.getColor() == ColorInfo.WHITE ? -1 : 1;
		int boundaries = this.getColor() == ColorInfo.WHITE ? -7 : 0;

		if (this.isMoved() == false) { // A pawn that was not moved can be moved 2 spaces forward
			if (board[row + (2 * side)][column].getPieceID() == -1) { // There is no piece in the square
				if (board[row + side][column].getPieceID() == -1) { // There is no piece between the pawn and the square
					// validMoves.add(point(row + (2 * side), column));
					BoardMovements.validateMovement(board, this, this.getColor(), pieceBox, validMoves,
							row + (2 * side), column);
				}
			}
		}

		// Inside boundaries is, depending on the pawn side, > 0 or < 7 (or > -7)
		if (side * this.getActualPosition().getRow() > boundaries) {
			// There is no piece in the square
			if (board[row + side][column].getPieceID() == -1) {
				// validMoves.add(point(row + side, column));
				BoardMovements.validateMovement(board, this, this.getColor(), pieceBox, validMoves, row + side, column);
			}
			// Pawns capture diagonally
			if (column > 0) {
				// There is a piece to capture
				if (board[row + side][column - 1].getPieceID() != -1
						&& board[row + side][column - 1].getColor() != this.getColor()) {
					// validAttack.add(point(row + side, column - 1));
					BoardMovements.validateAttack(board, this, this.getColor(), pieceBox, validAttack, row + side,
							column - 1);
				}
			}
			// Pawns capture diagonally
			if (column < 7) {
				// There is a piece to capture
				if (board[row + side][column + 1].getPieceID() != -1
						&& board[row + side][column + 1].getColor() != this.getColor()) {
					// validAttack.add(point(row + side, column + 1));
					BoardMovements.validateAttack(board, this, this.getColor(), pieceBox, validAttack, row + side,
							column + 1);
				}
			}
		}
	}

	/**
	 * Calculates all the possible piece (excluding pawns) movements not considering
	 * all special rules
	 * 
	 * @param validMoves
	 *            All movements that the piece can make without capturing another
	 *            piece are returned here
	 * @param validAttack
	 *            All movements that the piece can make while capturing another
	 *            piece are returned here
	 * @param board
	 *            Game board
	 * @param possibleMoves
	 *            The possible moves a piece can do, basically the rules that govern
	 *            its movements
	 * @param pieceBox
	 *            The array with all the pieces
	 */
	private void pieceMovements(List<Coordinates> validMoves, List<Coordinates> validAttack, final Square board[][],
			List<List<Coordinates>> possibleMoves, PieceList[] pieceBox) {
		int row, column, i;

		for (List<Coordinates> direction : possibleMoves) { // For each possible direction

			i = 0;
			row = this.actualPosition.getRow() + direction.get(i).getRow();
			column = this.actualPosition.getColumn() + direction.get(i).getColumn();

			// While there is movements in that direction and its inside the board
			while (i < direction.size() && row < 8 && column < 8 && row >= 0 && column >= 0) {

				if (board[row][column].getPieceID() == -1) { // If there is no piece, its a valid destination
					BoardMovements.validateMovement(board, this, this.getColor(), pieceBox, validMoves, row, column);
				} else {
					if (board[row][column].getColor().value != this.color.value) { // If there is an enemy piece, its a
																					// valid destination
						BoardMovements.validateAttack(board, this, this.getColor(), pieceBox, validAttack, row, column);
					}
					break; // If there is a piece, its not possible to keep going in that direction
				}
				i++;
				if (i < direction.size()) {
					row = this.actualPosition.getRow() + direction.get(i).getRow();
					column = this.actualPosition.getColumn() + direction.get(i).getColumn();
				}
			}
		}
	}

	/**
	 * Map the piece attributes the a BSON Document
	 */
	public Document pieceToDoc() {
		return new Document("coord_row", this.getActualPosition().getRow())
				.append("coord_column", this.getActualPosition().getColumn()).append("piece_type", this.getType().value)
				.append("moved", this.isMoved()).append("piece_color", this.getColor().value)
				.append("index", this.getIndex());
	}

	public Coordinates getActualPosition() {
		return actualPosition;
	}

	public void setActualPosition(Coordinates actualPosition) {
		this.actualPosition = actualPosition;
	}

	public PieceInfo getType() {
		return type;
	}

	public void setType(PieceInfo type) {
		this.type = type;
	}

	public boolean isMoved() {
		return moved;
	}

	public void setMoved(boolean moved) {
		this.moved = moved;
	}

	public ColorInfo getColor() {
		return this.color;
	}

	public void setColor(ColorInfo color) {
		this.color = color;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
}
