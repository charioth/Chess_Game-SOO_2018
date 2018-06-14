package models.pieces;

/**
 * Enum of the piece types
 */
public enum PieceInfo {
	/* Piece types */
	KING(0), QUEEN(1), ROOK(2), BISHOP(3), KNIGHT(4), PAWN(5), DEAD(6);

	public int value;

	PieceInfo(int value) {
		this.value = value;
	}
}
