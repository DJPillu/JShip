package game.ai;

import game.Location;
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
	 */
	public Regular(boolean[] initVars, Location[][] gridOpp) {
		super(initVars, gridOpp);
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
		int[] out = {-1, -1};
		// TODO: Do something.
		return out;
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
		int[] out = new int[2], temp = new int[2];
		Random rand = new Random();

		for (int i = 0; i < 2; i++) {
			out[i] = rand.nextInt(this.initVars[0] ? 15 : 10);
		}

		while (!this.gridSelf[out[0]][out[1]].isUnguessed()) {
			for (int i = 0; i < 2; i++) {
				temp[i] = rand.nextInt(this.initVars[0] ? 15 : 10);
			}

			out = temp;
		}

		return out;
	}
}
