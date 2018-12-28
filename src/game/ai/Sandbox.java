package game.ai;

import game.grid.Location;
import game.grid.Ship;
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
	 * @param gridOpp  Enemy Grid
	 * @param shipsOpp Enemy Ships
	 */
	public Sandbox(boolean[] initVars, Location[][] gridOpp, Ship[] shipsOpp) {
		super(initVars, gridOpp, shipsOpp);
	}

	/**
	 * Function for the AI to fire at the player's ships.
	 * The coordinates are always (psuedo-)randomly generated.
	 *
	 * @return an integer array. The 1st value is the x-Coordinate. The 2nd value is the Y-Coordinate.
	 */
	@Override
	public int[] fire() {
		int[] xy = new int[2];      // Firing coordinates.
		int[] temp = new int[2];    // Temporary variable to store coordinates if xy refers to a guessed position.
		Random rand = new Random(); // Random data type generator (built-in class).

		for (int i = 0; i < 2; i++) {
			xy[i] = rand.nextInt(this.gridSize);
		}

		while (!this.gridOpp[xy[1]][xy[0]].isUnguessed()) { // If xy refers to a guessed coordinate, regenerate it.
			for (int i = 0; i < 2; i++) {
				temp[i] = rand.nextInt(this.gridSize);
			}

			xy = temp;
		}

		return xy;
	}

}
