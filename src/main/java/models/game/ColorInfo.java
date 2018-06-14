package models.game;

/**
 * Enum of the colors
 */
public enum ColorInfo {
	/* Defines the colors of each side */
	WHITE(0), BLACK(1);

	public int value;

	ColorInfo(int value) {
		this.value = value;
	}
}
