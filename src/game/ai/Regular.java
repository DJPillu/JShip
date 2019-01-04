package game.ai;

import game.grid.Location;
import game.grid.Ship;
import java.util.Random;


/**
 * The Regular AI, i.e., the "Medium" AI.
 * Plays like an actual person.
 *
 * @author blackk100
 */
public final class Regular extends AI {

	/**
	 * If ture, the AI 'hunts' for a suitable target by randomly firing at various locations.
	 * If false, the AI uses <code>this.prevShots</code> to determine the next firing location
	 * (as a suitable target was found).
	 */
	private boolean hunt = true;

	/**
	 * Stores the previous shots of the AI.
	 * If <code>this.hunt</code> is true, the 1st element stores the previous shot, rest are (-1, -1).
	 * The 1st element in the 2nd Dimension is the X-Coordinate, the 2nd is the Y-Coordinate.
	 */
	private int[][] prevShots = new int[8][2];

	/**
	 * Constructor for the Regular AI.
	 *
	 * @param initVars Initialization Variables
	 * @param gridOpp  Enemy Grid
	 * @param shipsOpp Enemy Ships
	 */
	public Regular(boolean[] initVars, Location[][] gridOpp, Ship[] shipsOpp) {
		super(initVars, gridOpp, shipsOpp);

		this.setPrevShot(new int[] {-1, -1}, true); // Initializes prevShots
	}

	/**
	 * Function for the AI to fire at the player's ships.
	 *
	 * The coordinates are initially randomly generated until a hit occurs ('hunt' mode).
	 *
	 * Then the AI fires in the 4 adjacent positions (if available, ex.: at the corners and edges,
	 * only 2 and 3 directions are available respectively).
	 * In the previous step, if another hit occurs, the AI continues firing in that direction until
	 * no more hits occur.
	 *
	 * The process is then repeated.
	 *
	 * @return an integer array. The 1st value is the X-Coordinate. The 2nd value is the Y-Coordinate.
	 */
	@Override
	public int[] fire() {
		int[] xy;

		if (this.hunt) { // Checks if the AI is in hunt mode.
			xy = this.randomFire();
		} else {         // Not in hunt mode.
			xy = this.getPrevShot();

			if (xy.equals(new int[] {-1, -1})) { // Checks if this.getPrevShot found a valid coordinate.
				System.out.println("WARNING: No Coordinate Found! Reverting to psuedorandom generation.");
				xy = this.randomFire();
			} else {                             // A valid coordinate was found.
				// TODO: Figure out where to shoot next.
				xy = this.randomFire(); // Placing this so that the game can run without errors.
			}
		}

		this.hunt = !this.gridOpp[xy[1]][xy[0]].hasShip();
		this.setPrevShot(xy, this.hunt);
		return xy;
	}

	/**
	 * Adds coordinates the <code>this.prevShots</code>.
	 * If <code>reset</code> is true, Resets <code>this.prevShots</code> to it's initial state.
	 *
	 * @param xy    the coordinates to add. 1st element is X-Coordinate, 2nd is Y-Coordinate.
	 * @param reset whether to reset <code>this.prevShots</code> or not.
	 */
	private void setPrevShot(int[] xy, boolean reset) {
		if (reset) {
			for (int shot = 1; shot < 8; shot++) {
				this.prevShots[shot] = new int[] {-1, -1};
			}
			this.prevShots[0] = xy;
		} else {
			for (int shot = 0; shot < 8; shot++) {
				if (this.prevShots[shot].equals(new int[] {-1, -1})) {
					this.prevShots[shot] = xy;
				}
			}
		}
	}

	/**
	 * @return an integer array indicating the previous shot. 1st element is the X-Coordinate, 2nd is the Y-Coordinate.
	 */
	private int[] getPrevShot() {
		for (int shot = 8; shot > -1; shot--) {
			if (!this.prevShots[shot].equals(new int[] {-1, -1})) {
				return this.prevShots[shot];
			}
		}

		return new int[] {-1, -1};
	}

	/**
	 * Support function 1 for the AI to fire at the player's ships.
	 * The coordinates are always randomly generated.
	 *
	 * Is identical to Sandbox.fire()
	 *
	 * @return an integer array. The 1st value is the X-Coordinate. The 2nd value is the Y-Coordinate
	 */
	private int[] randomFire() {
		int[] xy = new int[2];      // Firing coordinates.
		int[] temp = new int[2];    // Temporary variable to store coordinates if xy refers to a guessed position.
		Random rand = new Random(); // Random data type generator (built-in class).

		// If xy refers to a guessed coordinate, or a location that borders a ship tile that has been guessed, regenerate it.
		do { // Do initializes xy.
			for (int i = 0; i < 2; i++) {
				temp[i] = rand.nextInt(this.gridSize);
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
