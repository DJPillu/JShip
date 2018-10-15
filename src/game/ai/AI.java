package game.ai;


/**
 * Abstract Class dealing with board generation and shot placement.
 *
 * @author blackk100
 */
abstract class AI {
	boolean[][] gridShotSelf, gridPlaceSelf;
	boolean[] initVars;

	/**
	 * Constructor for the abstract AI.
	 *
	 * @param initVars
	 * @param mode
	 */
	AI(boolean[] initVars, String mode) {
		if (initVars[0]) {
			this.gridShotSelf = new boolean[15][15];
			this.gridPlaceSelf = new boolean[15][15];
		} else {
			this.gridShotSelf = new boolean[10][10];
			this.gridPlaceSelf = new boolean[10][10];
		}

		this.place();
	}

	/**
	 * Is essentially a setter for gridPlaceSelf.
	 *
	 * @return a 2-Dimensional boolean array which indicates the positions of the ships placed
	 */
	public boolean[][] place() {
		boolean[][] out = gridPlaceSelf;
		// TODO: Do stuff
		return out;
	}

	/**
	 * Abstract Function for the AI to fire at the player's ships
	 *
	 * @return an integer array. The 1st value is the X-Coordinate. The 2nd value is the Y-Coordinate
	 */
	public abstract int[] fire();
}
