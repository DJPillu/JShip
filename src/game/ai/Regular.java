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
	 * Constructor for the Regular AI
	 *
	 * @param initVars Initialization Variables
	 * @param gridOpp  Enemy Grid
	 * @param shipsOpp Enemy Ships
	 */
	public Regular(boolean[] initVars, Location[][] gridOpp, Ship[] shipsOpp) {
		super(initVars, gridOpp, shipsOpp);
	}

	/**
	 * Function for the AI to fire at the player's ships.
	 *
	 * The coordinates are initially randomly generated until a "hit" occurs.
	 * Then the AI fires in the 4 adjacent positions (if available, ex.: at the corners and edges, only 2 and 3 directions
	 * are available).
	 * If in the previous step, another "hit" occurs, the AI continues firing in that direction until no more "hits"
	 * occur.
	 *
	 * The process is then repeated.
	 *
	 * @return an integer array. The 1st value is the X-Coordinate. The 2nd value is the Y-Coordinate
	 */
	@Override
	public int[] fire() {
		int[] xy = this.randomFire();
		// TODO: Do something.
		return xy;
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
		int[] xy = new int[2];     // Firing coordinates.
		int[] temp = new int[2];    // Temporary variable to store coordinates if xy refers to a guessed position.
		Random rand = new Random(); // Random data type generator (built-in class).

		for (int i = 0; i < 2; i++) {
			xy[i] = rand.nextInt(this.initVars[0] ? 15 : 10);
		}

		while (!this.gridSelf[xy[0]][xy[1]].isUnguessed()) { // If xy refers to a guessed coordinate, regenerate it.
			for (int i = 0; i < 2; i++) {
				temp[i] = rand.nextInt(this.initVars[0] ? 15 : 10);
			}

			xy = temp;
		}

		return xy;
	}
}
