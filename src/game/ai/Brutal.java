package game.ai;

import game.Location;
import java.util.Random;


/**
 * The Brutal AI, i.e., the "Hard" AI
 * Knows the enemy's ship's locations.
 *
 * @author blackk100
 */
public final class Brutal extends AI {

	/**
	 * Constructor for the Brutal AI
	 *
	 * @param initVars Initialization Variables
	 * @param gridOpp  Enemy Grid
	 */
	public Brutal(boolean[] initVars, Location[][] gridOpp) {
		super(initVars, gridOpp);
	}

	/**
	 * Function for the AI to fire at the player's ships.
	 *
	 * The AI always "knows" where the opponents ships are located.
	 *
	 * The AI fires at random coordinates where no ships are located, until one of it's ship is hit, or no more
	 * coordinates remain.
	 * Then, the AI is guaranteed to hit a ship (however, these coordinates are selected randomly again).
	 *
	 * TODO: Make it slightly easier (later, after implementing the above version) by randomizing whether the shot
	 * lands or not by weighing in how many of it's ships aren't sunk.
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
