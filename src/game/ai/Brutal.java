package game.ai;

import game.grid.Location;
import game.grid.Ship;
import java.util.Random;


/**
 * The Brutal AI, i.e., the "Hard" AI
 * Knows the enemy's ship's locations.
 *
 * @author blackk100
 */
public final class Brutal extends AI {

	/**
	 * Stores whether or not one of its ships was hit yet.
	 * If not hit (i.e., false), the AI is not suppose to hit the Player (unless no other "empty" tiles are present).
	 */
	private boolean hit = false;

	/**
	 * Constructor for the Brutal AI
	 *
	 * @param initVars Initialization Variables
	 * @param gridOpp  Enemy Grid
	 * @param shipsOpp Enemy Ships
	 */
	public Brutal(boolean[] initVars, Location[][] gridOpp, Ship[] shipsOpp) {
		super(initVars, gridOpp, shipsOpp);
	}

	/**
	 * A setter for gridSelf.
	 * Also updates <code>this.hit</code>.
	 *
	 * @param gridSelf Location Grid of the AI
	 */
	@Override
	public void updateGridSelf(Location[][] gridSelf) {
		this.gridSelf = gridSelf;

		for (int y = 0; y < gridSelf.length; y++) {
			for (int x = 0; x < gridSelf.length; x++) {
				if (gridSelf[y][x].isHit()) { // Checks if this location has a ship and was hit.
					this.hit = true;
				}
			}
		}
	}

	/**
	 * A setter for shipsOpp.
	 *
	 * @param shipsOpp Ships list of the Player
	 */
	@Override
	public void updateShipsOpp(Ship[] shipsOpp) {
		this.shipsOpp = shipsOpp;
	}

	/**
	 * Function for the AI to fire at the player's ships.
	 *
	 * The AI always "knows" where the opponents ships are located.
	 *
	 * The AI fires at random coordinates where no ships are located, until one of it's ship is hit,
	 * or no more coordinates remain.
	 * Then, the AI is guaranteed to hit a ship (however, these coordinates are selected randomly again).
	 *
	 * TODO: Make it slightly easier by randomizing whether the shot lands or not by weighing in
	 * how many of the AI's ships aren't sunk.
	 *
	 * @return an integer array. The 1st value is the X-Coordinate. The 2nd value is the Y-Coordinate
	 */
	@Override
	public int[] fire() {
		int[] xy = this.randomFire();

		int left = 0;
		int occupied = 0;
		for (int ship = 0; ship < this.shipNos; ship++) {
			occupied += this.shipsOpp[ship].length;
		}
		for (int y = 0; y < this.gridSize; y++) {
			for (int x = 0; x < this.gridSize; x++) {
				if (this.gridOpp[y][x].isUnguessed()) {
					left++;
				}
			}
		}

		if (left == occupied || this.hit) {          // Checks if there aren't anymore "black" tiles, or if any AI ship has been hit.
			for (int s = this.shipNos; s <= 0; s--) {  // Starts from the largest ships 1st to decrease difficulty in Salvo. Figure out why the order is inverted.
				Ship ship = this.shipsOpp[s];            // The current ship.
				boolean direction = ship.getDirection(); // Direction
				int length = ship.length;                // Length

				if (!ship.isSunk()) {                    // Checks if the ship wasn't sunk.
					for (int l = 0; l < length; l++) {
						if (!ship.isHit(l)) {                // Checks if the current position wasn't already hit.
							xy = new int[] {ship.getStart()[0] + (direction ? 0 : l), ship.getStart()[1] + (direction ? l : 0)};
						}
					}
				}
			}
		} else {
			while (this.gridOpp[xy[1]][xy[0]].hasShip()) { // Checks if xy isn't a ship part.
				xy = this.randomFire();
			}
		}

		return xy;
	}

	/**
	 * Support function 1 for the AI to fire at the player's ships.
	 * The coordinates are always (psuedo-)randomly generated.
	 *
	 * Is identical to <code>Sandbox.fire()</code>.
	 *
	 * @return an integer array. The 1st value is the X-Coordinate, 2nd value is the Y-Coordinate.
	 */
	private int[] randomFire() {
		int[] xy = new int[2];        // Firing coordinates.
		int[] temp = new int[2];      // Temporary variable to store coordinates if xy refers to a guessed position.
		Random random = new Random(); // Random data type generator (built-in class).

		// If xy refers to a guessed coordinate, or a location that borders a ship tile that has been guessed, regenerate it.
		do { // Do initializes xy.
			for (int i = 0; i < 2; i++) {
				temp[i] = random.nextInt(this.gridSize);
			}
			xy = temp;

			if (!this.gridOpp[xy[1]][xy[0]].isUnguessed()) { // Checks if the current location has been guessed.
				continue;
			} else {                                         // Checks if the current location borders any known ship.
				if ((xy[0] > 0) && (xy[0] < this.gridSize - 1) && (xy[1] > 0) && (xy[1] < this.gridSize - 1)) { // Not at edges
					if (this.gridOpp[xy[1] - 1][xy[0]].isHit() || this.gridOpp[xy[1] + 1][xy[0]].isHit() || this.gridOpp[xy[1]][xy[0] - 1].isHit() || this.gridOpp[xy[1]][xy[0] + 1].isHit()) {
						continue;
					}
				} else {                                     // At the Edges
					if (xy[1] == 0) {                          // Top edge
						if (xy[0] == 0) {                        // Top-Left Corner
							if (this.gridOpp[xy[1]][xy[0] + 1].isHit() || this.gridOpp[xy[1] + 1][xy[0]].isHit()) {
								continue;
							}
						} else if (xy[0] == this.gridSize - 1) { // Top-Right Corner
							if (this.gridOpp[xy[1]][xy[0] - 1].isHit() || this.gridOpp[xy[1] + 1][xy[0]].isHit()) {
								continue;
							}
						} else {                                 // Top Edge (excluding Corners)
							if (this.gridOpp[xy[1]][xy[0] + 1].isHit() || this.gridOpp[xy[1]][xy[0] - 1].isHit() || this.gridOpp[xy[1] + 1][xy[0]].isHit()) {
								continue;
							}
						}
					} else if (xy[1] == this.gridSize - 1) {   // Bottom Edge
						if (xy[0] == 0) {                        // Bottom-Left Corner
							if (this.gridOpp[xy[1]][xy[0] + 1].isHit() || this.gridOpp[xy[1] - 1][xy[0]].isHit()) {
								continue;
							}
						} else if (xy[0] == this.gridSize - 1) { // Bottom-Right Corner
							if (this.gridOpp[xy[1]][xy[0] - 1].isHit() || this.gridOpp[xy[1] - 1][xy[0]].isHit()) {
								continue;
							}
						} else {                                 // Bottom Edge (excluding Corners)
							if (this.gridOpp[xy[1]][xy[0] + 1].isHit() || this.gridOpp[xy[1]][xy[0] - 1].isHit() || this.gridOpp[xy[1] - 1][xy[0]].isHit()) {
								continue;
							}
						}
					} else if (xy[0] == 0) {                   // Left Edge (excluding Corners)
						if (this.gridOpp[xy[1] + 1][xy[0]].isHit() || this.gridOpp[xy[1] - 1][xy[0]].isHit() || this.gridOpp[xy[1]][xy[0] + 1].isHit()) {
							continue;
						}
					} else {                                   // Rght Edge (ecluding Corners)
						if (this.gridOpp[xy[1] + 1][xy[0]].isHit() || this.gridOpp[xy[1] - 1][xy[0]].isHit() || this.gridOpp[xy[1]][xy[0] - 1].isHit()) {
							continue;
						}
					}
				}
			}

			break;
		} while (true);

		return xy;
	}

}
