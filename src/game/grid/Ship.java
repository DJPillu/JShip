package game.grid;


/**
 * Class for maintaining the details of each ship.
 * Required for having multi-tile ships.
 *
 * @author blackk100
 */
public final class Ship {

	/**
	 * A positive integer indicating length of the ship (distance between start and end points).
	 * A value of 0 implies the ship is not being used.
	 */
	public final int length;

	/**
	 * A boolean indicating the orientation of the ship.
	 * <pre>
	 * true  - Vertical
	 * false - Horizontal
	 * </pre>
	 */
	private boolean direction;

	/**
	 * 2 Values indicating the start coordinates.
	 * 1st value is X-Coordinate. 2nd is Y-Coordinate.
	 * It's coordinate values are always less than/equal to end's.
	 */
	private int[] start;

	/**
	 * 2 Values indicating the end coordinates.
	 * 1st value is X-Coordinate. 2nd is Y-Coordinate.
	 * It's coordinate values are always greater than/equal to start's.
	 */
	private int[] end;

	/**
	 * A boolean array indicating whether or not each individual tile the ship is located on was hitor not.
	 * <pre>
	 * true  - Hit
	 * false - Not Hit
	 * </pre>
	 */
	private boolean[] hit;

	/**
	 * A boolean indicating whether or not the ship has been sunk.
	 * Is true if all values in <code>sections</code> are <code>true</code>.
	 */
	private boolean sunk = false;

	/**
	 * Constructor for the Ship class.
	 *
	 * @param length The length of the ship.
	 */
	public Ship(int length) {
		this.length = length;

		this.remove();

		if (length != 0) { // Ship not being used.
			this.hit = new boolean[length];
			for (int i = 0; i < length; i++) {
				this.hit[i] = false;
			}
		}
	}

	/**
	 * Sets the starting and the ending coordinates of the ship.
	 *
	 * @param coordinates The starting coordinates (1st value is X-Coordinate, 2nd is Y-Coordinate).
	 * @param direction   The orientation of the ship.
	 */
	public void add(int[] coordinates, boolean direction) {
		if (length != 0) { // Checks if the ship is being used.
			this.start = coordinates;
			this.end = new int[] {coordinates[0] + (direction ? 0 : length - 1), coordinates[1] + (direction ? length - 1 : 0)};

			this.direction = direction;
		}
	}

	/**
	 * "Removes" the ship from the game board
	 */
	public void remove() {
		this.start = new int[] {-1, -1};
		this.end = new int[] {-1, -1};
		this.direction = false;
	}

	/**
	 * @return whether the ship was placed or not.
	 */
	public boolean isPlaced() {
		return (this.start[0] != -1) && (this.start[1] != -1) && (this.end[0] != -1) && (this.end[1] != -1);
	}

	/**
	 * Returns the Start coordinates.
	 *
	 * @return start 1st value is X-Coordinate. 2nd is Y-Coordinate.
	 */
	public int[] getStart() {
		return this.start;
	}

	/**
	 * Returns the End coordinates.
	 *
	 * @return end 1st value is X-Coordinate. 2nd is Y-Coordinate.
	 */
	public int[] getEnd() {
		return this.end;
	}

	/**
	 * Returns the orientation of the ship.
	 *
	 * @return the direction
	 */
	public boolean getDirection() {
		return this.direction;
	}

	/**
	 * Checks if the ship has been sunk.
	 *
	 * @return true if sunk, else false.
	 */
	public boolean isSunk() {
		if (length != 0) { // Ship not being used.
			return this.sunk;
		} else {
			return false;
		}
	}

	/**
	 * Marks the given position as hit.
	 * Also checks if the ship was sunk.
	 *
	 * @param coords The coordinates at which the ship was hit. 1st value is X-Coordinate, 2nd is Y-Coordinate
	 */
	public void sectionHit(int[] coords) {
		if (length != 0) { // Checks if the ship is being used.
			int position = this.getPosition(coords);

			if (position > -1) {
				this.hit[position] = true;

				// Checking if the ship was sunk.
				boolean allHit = true;
				for (int i = 0; i < this.length; i++) {
					if (!this.hit[i]) {
						allHit = false;
						break;
					}
				}

				if (allHit) {
					this.sunk = true;
				}
			}
		} // Ship isn't being used. Do nothing.
	}

	/**
	 * Checks if the given position was hit,
	 *
	 * @param coords The coordinates to check. 1st value is X-Coordinate, 2nd is Y-Coordinate
	 *
	 * @return true if hit, else false.
	 */
	public boolean isHit(int[] coords) {
		if (length != 0) { // Checks if the ship is being used.
			return this.hit[this.getPosition(coords)];
		} else {
			return false;
		}
	}

	/**
	 * Gives the given coordinates position relative to the start coordinate of the ship.
	 * Returns -1 if not part of the ship.
	 *
	 * @param coords The coordinates to resolve. 1st value is X-Coordinate, 2nd is Y-Coordinate
	 *
	 * @return an integer giving the relative position of the coordinates. If -1, the coordinates aren't part of the ship.
	 */
	public int getPosition(int[] coords) {
		for (int position = 0; position < this.length; position++) {
			if ((this.start[0] + (this.direction ? 0 : position) == coords[0]) && (this.start[1] + (this.direction ? position : 0) == coords[1])) {
				return position; // Matched
			}
		}
		return -1;    // Not wihtin bounds
	}

}
