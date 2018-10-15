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
	 * @param initVars
	 * @param mode
	 */
	public Sandbox(boolean[] initVars, String mode) {
		super(initVars, mode);
	}

	/**
	 * Function for the AI to fire at the player's ships.
	 * The coordinates are always randomly generated.
	 *
	 * @return an integer array. The 1st value is the X-Coordinate. The 2nd value is the Y-Coordinate
	 */
	@Override
	public int[] fire() {
		int[] out = new int[2], temp = new int[2];
		Random rand = new Random();

		for (int i = 0; i < out.length; i++) {
			out[i] = rand.nextInt(this.initVars[0] ? 15 : 10);
		}

		while (this.gridShotSelf[out[0]][out[1]]) {
			for (int i = 0; i < temp.length; i++) {
				temp[i] = rand.nextInt(this.initVars[0] ? 15 : 10);
			}

			out = temp;
		}

		return out;
	}

}
