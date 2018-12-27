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
		int[] xy = new int[2], temp = new int[2];
		Random rand = new Random();

		for (int i = 0; i < 2; i++) {
			xy[i] = rand.nextInt(this.initVars[0] ? 15 : 10);
		}

		while (!this.gridSelf[xy[0]][xy[1]].isUnguessed()) {
			for (int i = 0; i < 2; i++) {
				temp[i] = rand.nextInt(this.initVars[0] ? 15 : 10);
			}

			xy = temp;
		}

		return xy;
	}
}
