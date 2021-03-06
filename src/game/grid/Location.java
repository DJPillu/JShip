package game.grid;


/**
 * Class for maintaining the game board.
 *
 * @author blackk100
 */
public final class Location {

	/**
	 * Constant value of '0' indicating that this position is unguessed.
	 */
	public static final int UNGUESSED = 0;

	/**
	 * Constant value of '-1' indicating that the shot at this position missed.
	 */
	public static final int MISS = -1;

	/**
	 * Constant value of '1' indicating that the shot at this position hit a target.
	 */
	public static final int HIT = 1;

	/**
	 * Stores whether or not this location has been guessed.
	 * Takes the constants <code>this.UNGUESSED</code>, <code>this.HIT</code> and <code>this.MISS</code>.
	 */
	private int status = Location.UNGUESSED;

	/**
	 * Used to store whether or not this location has a ship part.
	 */
	private boolean hasShip = false;

	/**
	 * Used to store whether or not this location borders a ship part.
	 *
	 * This is used to prevent ships being placed beside each other.
	 */
	private boolean border = false;

	/**
	 * Getter for hasShip
	 *
	 * @return hasShip
	 */
	public boolean hasShip() {
		return this.hasShip;
	}

	/**
	 * This location has a ship part (setter for <code>this.hasShip</code> to true).
	 */
	public void shipPresent() {
		this.hasShip = true;
	}

	/**
	 * This location has a ship part (setter for <code>this.hasShip</code> to false).
	 */
	public void shipAbsent() {
		this.hasShip = false;
	}

	/**
	 * Getter for border
	 *
	 * @return border
	 */
	public boolean isBorder() {
		return this.border;
	}

	/**
	 * This location borders a ship part (setter for <code>this.border</code> to true)
	 */
	public void bordersShip() {
		this.border = true;
	}

	/**
	 * This location does not border a ship part (setter for <code>this.border</code> to false)
	 */
	public void noBordersShip() {
		this.border = false;
	}

	/**
	 * Checks if the location hasn't been guessed yet.
	 *
	 * @return true if unguessed. false if guessed.
	 */
	public boolean isUnguessed() {
		return this.status == Location.UNGUESSED;
	}

	/**
	 * Checks if the location was a hit
	 *
	 * @return true if hit. false if not hit.
	 */
	public boolean isHit() {
		return this.status == Location.HIT;
	}

	/**
	 * Checks if the location was a miss
	 *
	 * @return true if miss. false if not miss.
	 */
	public boolean isMiss() {
		return this.status == Location.MISS;
	}

	/**
	 * Marks the location as a hit or a miss depending on whether this location has a ship.
	 */
	public void markShot() {
		this.status = this.hasShip() ? Location.HIT : Location.MISS;
	}

}
