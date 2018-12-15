package game.ai;

import java.util.Random;


/**
 * The Sandbox AI, i.e., the "Easy" AI.
 * Fires at random positions.
 *
 * @author blackk100
 */
public final class Sandbox extends AI {

	/**
	 * Constructor for the Sandbox AI
	 *
	 * @param initVars Initialization Variables
	 */
	public Sandbox(boolean[] initVars) {
		super(initVars);
	}

	/**
	 * Function for the AI to fire at the player's ships.
	 * The coordinates are always (psuedo-)randomly generated.
	 *
	 * @return an integer array. The 1st value is the X-Coordinate. The 2nd value is the Y-Coordinate.
	 */
	public int[] fire() {
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
