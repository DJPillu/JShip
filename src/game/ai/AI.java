package game.ai;

import game.Location;
import java.util.Random;


/**
 * Base AI Class.
 * Only decides ship placement.
 *
 * @author blackk100
 */
public class AI {

	/**
	 * 2-Dimensional Location Grid representing the AI's grid
	 */
	Location[][] gridSelf;

	/**
	 * An integer show the length of the grid.
	 * if initVars[0] is true, boardSize is 15. Else 10.
	 */
	final int boardSize;

	/**
	 * Round Initialization Variables:
	 *
	 * <pre>
	 * I-----------I-----------I-----------I
	 * I Index No. I   false   I    true   I
	 * I-----------I-----------I-----------I
	 * I     0     I  10 x 10  I  15 x 15  I
	 * I-----------I-----------------------I
	 * I     1     I       Battleship      I
	 * I     2     I        Cruiser        I
	 * I     3     I       Destroyer       I
	 * I     4     I      Patrol Boat      I
	 * I-----------I-----------------------I
	 * </pre>
	 */
	boolean[] initVars;

	/**
	 * Constructor for the AI.
	 *
	 * @param initVars Initialization VariablesW
	 */
	AI(boolean[] initVars) {
		this.initVars = initVars;
		this.boardSize = initVars[0] ? 15 : 10;

		this.gridSelf = new Location[this.boardSize][this.boardSize];
		for (int y = 0; y < this.boardSize; y++) {
			for (int x = 0; x < this.boardSize; x++) {
				this.gridSelf[y][x] = new Location();
			}
		}

		this.place();
	}

	/**
	 * A getter for gridSelf
	 *
	 * @return gridSelf Self Grid
	 */
	public Location[][] getGrid() {
		return this.gridSelf;
	}

	/**
	 * Places ships in gridSelf.
	 */
	private void place() {
		int[] shipLengths = new int[38];
		Random rand = new Random();

		for (int i = 0; i < 38; i++) {
			if (i < 5) {         // Battleships
				shipLengths[i] = this.initVars[1] ? 1 : 0;
			} else if (i < 13) { // Cruisers
				shipLengths[i] = this.initVars[2] ? 1 : 0;
			} else if (i < 23) { // Destroyers
				shipLengths[i] = this.initVars[3] ? 1 : 0;
			} else {             // Patrol Boats
				shipLengths[i] = this.initVars[4] ? 1 : 0;
			}
		}

		for (int i = 0; i < 38; i++) {
			int shipLength = shipLengths[i];
			System.out.println(i + ": " + shipLength);

			if (shipLength > 0) {                           // Checks if the current ship is being used
				int[] randLoc = random(shipLength);           // 0   : Y-Coordinate ; 1: X-Coordinate
				boolean randomDirection = rand.nextBoolean(); // True: Vertical     ; False: Horizontal

				top: // outer loop label
				while (true) {
					if (this.gridSelf[randLoc[0]][randLoc[1]].hasShip()) { // Checks if the initial location has a ship part
						System.out.println("Ship exists at initial point");
						randLoc = random(shipLength);
						continue;
					}

					// Checks if the ship will intersect any other ship
					if (randomDirection) {                                         // Vertical Orientation
						for (int l = 0; l < shipLength; l++) {
							if (this.gridSelf[randLoc[0] + l][randLoc[1]].hasShip()) { // Y-Coordinate is incremented if vertical
								System.out.println("Ship exists at vertical length: " + (l + 1));
								continue top;
							}
						}
					} else {                                                       // Horizontal Orientation
						for (int l = 0; l < shipLength; l++) {
							if (this.gridSelf[randLoc[0]][randLoc[1] + l].hasShip()) { // X-Coordinate is incremented if horizontal
								System.out.println("Ship exists at horizontal length: " + (l + 1));
								continue top;
							}
						}
					}

					break; // No intersections. Continue to placement.
				}

				// Sets the location values
				if (randomDirection) {                                       // Verical Orientation
					for (int l = 0; l < shipLength; l++) {
						this.gridSelf[randLoc[0] + l][randLoc[1]].shipPresent(); // Y-Coordinate is incremented if vertical
					}
				} else {                                                     // Horizontal Orientation
					for (int l = 0; l < shipLength; l++) {
						this.gridSelf[randLoc[0]][randLoc[1] + l].shipPresent(); // X-Coordinate is incremented if horizontal
					}
				}

				System.out.println("Ship " + i + " placed");
			} else {                                        // Ship is not being used
				System.out.println("Ship " + i + " is not being used");
			}
		}

		System.out.println("All ships placed");
	}

	/**
	 * (Psuedo-)Randomiser for the place() function
	 *
	 * @param length Length of the Ship
	 *
	 * @return A 1-Dimensional array storing the X- and Y-Coordinates. 1st Integer is the Y-Coordinate. 2nd Integer is the
	 *         X-Coordinate
	 */
	private int[] random(int length) {
		int[] out = new int[2];
		Random rand = new Random();

		for (int i = 0; i < 2; i++) {
			out[i] = rand.nextInt(this.initVars[0] ? 16 - length : 11 - length);
		}

		return out;
	}
}
