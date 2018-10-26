package game;


/**
 * Class for maintaining the location of each ship.
 *
 * @author blackk100
 */
public final class Location {

	/**
	 * Takes the constants UNGUESSED, HIT & MISS
	 */
	private int status;
	public static final int UNGUESSED = 0;
	public static final int MISS = -1;
	public static final int HIT = 1;

	private boolean hasShip;

	/**
	 * Constructor for the Location class.
	 */
	public Location() {
		this.status = Location.UNGUESSED;
		this.shipAbsent();
	}

	/**
	 * Getter for hasShip
	 *
	 * @return hasShip
	 */
	public boolean hasShip() {
		return this.hasShip;
	}

	/**
	 * This location has a ship
	 */
	public void shipPresent() {
		this.hasShip = true;
	}

	/**
	 * This location doesn't have a ship.
	 */
	public void shipAbsent() {
		this.hasShip = false;
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
	public boolean checkHit() {
		return this.status == Location.HIT;
	}

	/**
	 * Marks the location as a hit.
	 */
	public void markHit() {
		this.status = Location.HIT;
	}

	/**
	 * Marks the location as a miss.
	 */
	public void markMiss() {
		this.status = Location.MISS;
	}
}
