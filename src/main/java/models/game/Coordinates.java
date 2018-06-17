package models.game;

public class Coordinates {
	/* The coordinates of a given point */
	private int row; // row would be the Y coordinate
	private int column; // column would be the X coordinate

	/**
	 * Empty constructor
	 */
	public Coordinates() {
	}

	/**
	 * Creates a coordinate object, using row and column
	 * 
	 * @param row
	 *            The coordinate row
	 * @param column
	 *            The coordinate column
	 */
	public Coordinates(int row, int column) {
		this.row = row;
		this.column = column;
	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getColumn() {
		return column;
	}

	public void setColumn(int column) {
		this.column = column;
	}

	/**
	 * Compare a coordinate with another one
	 * 
	 * @param obj
	 *            The piece to be verified
	 * @return True if the object is a coordinate and its rows and column are equal
	 *         to the callers
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof Coordinates) {
			if (this.row == ((Coordinates) obj).row && this.column == ((Coordinates) obj).column)
				return true;
		}
		return false;
	}

	/**
	 * Compare coordinates
	 * 
	 * @param row
	 *            The row to be compared with
	 * @param column
	 *            The column to be compared with
	 * @return True if both are equal, false otherwise
	 */
	public boolean equals(int row, int column) {
		if (this.row == row && this.column == column)
			return true;
		return false;
	}

	/**
	 * Returns a string representation of the object
	 * 
	 * @return a string representation of the object
	 */
	@Override
	public String toString() {
		return "Row: " + row + " Column: " + column;
	}
}
