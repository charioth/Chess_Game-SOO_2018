package models.game;

import java.awt.Rectangle;


public class Square {
	/* Represents each square of the board */
	private Rectangle renderSquare; // Position to be rendered in the screen
	private int pieceID; // -1 if there is no piece in the square
	private ColorInfo color; // null if there is no piece in the square
	
	/**
	  * Creates a square object with its attributes not initialized
	  */
	public Square() {
		renderSquare = null;
		pieceID = -1;
		color = null;
	}
	
	/**
	  * Creates a copy of the given square
	  * 
	  * @param copyThis	Square to be copied
	  */
	public Square(Square copyThis) {
		this.renderSquare = copyThis.renderSquare;
		this.pieceID = copyThis.pieceID;
		this.color = copyThis.color;
	}

	/**
	  * Initializes a square using a given position
	  * 
	  * @param renderPosition	Position of the square
	  */
	public Square(Rectangle renderPosition) {
		this.renderSquare = renderPosition;
		pieceID = -1;
	}
	
	/**
	  * Initializes a square using a given position and a piece
	  * 
	  * @param renderPosition	Position of the square
	  * @param ID				Id of the piece that is occupying this square
	  */
	public Square(Rectangle renderPosition, int ID) {
		this.renderSquare = renderPosition;
		this.pieceID = ID;
	}

	public Rectangle getRenderSquare() {
		return renderSquare;
	}

	public void setRenderSquare(Rectangle renderPosition) {
		this.renderSquare = renderPosition;
	}

	public int getPieceID() {
		return pieceID;
	}

	public void setPieceID(int ID) {
		this.pieceID = ID;
	}

	public ColorInfo getColor() {
		return color;
	}

	public void setColor(ColorInfo color) {
		this.color = color;
	}
}
