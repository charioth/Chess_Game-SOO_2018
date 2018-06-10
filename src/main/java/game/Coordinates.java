package game;

public class Coordinates {
	/* The coordinates of a given point */
	private int row; // row would be the Y coordinate
	private int column; // column would be the X coordinate

	public Coordinates() {
	}

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

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof Coordinates) {
			if (this.row == ((Coordinates) obj).row && this.column == ((Coordinates) obj).column)
				return true;
		}
		return false;
	}

	public boolean equals(int row, int column) {
		if (this.row == row && this.column == column)
			return true;
		return false;
	}

	@Override
	public String toString() {
		return "Row: " + row + " Column: " + column;
	}
}
