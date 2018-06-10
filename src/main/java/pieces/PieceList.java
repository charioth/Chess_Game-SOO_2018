package pieces;

import game.ColorInfo;
import game.Coordinates;

public class PieceList {
	/* All the pieces of a color */
	private Piece[] pieces;

	public PieceList() {
		pieces = new Piece[16];
	}

	public PieceList(ColorInfo color) {
		pieces = new Piece[16];
		initPieces(color);
	}

	private void initPieces(ColorInfo color) {
		/* Initialize pieces coordinates */
		int pos = (color == ColorInfo.WHITE ? 7 : 0);

		pieces[0] = new Piece(new Coordinates(pos, 4), PieceInfo.KING, false, color);
		pieces[1] = new Piece(new Coordinates(pos, 3), PieceInfo.QUEEN, false, color);
		pieces[2] = new Piece(new Coordinates(pos, 1), PieceInfo.KNIGHT, false, color);
		pieces[3] = new Piece(new Coordinates(pos, 6), PieceInfo.KNIGHT, false, color);
		pieces[4] = new Piece(new Coordinates(pos, 2), PieceInfo.BISHOP, false, color);
		pieces[5] = new Piece(new Coordinates(pos, 5), PieceInfo.BISHOP, false, color);
		pieces[6] = new Piece(new Coordinates(pos, 0), PieceInfo.ROOK, false, color);
		pieces[7] = new Piece(new Coordinates(pos, 7), PieceInfo.ROOK, false, color);

		if (color == ColorInfo.WHITE) {
			pos = 5;
		}
		for (int i = 0; i < 8; i++) {
			pieces[i + 8] = new Piece(new Coordinates(pos + 1, i), PieceInfo.PAWN, false, color);
		}

		for (int i = 0; i < 16; i++) {
			pieces[i].setIndex(i);
		}
	}

	public Piece[] getPieces() {
		return this.pieces;
	}
}
