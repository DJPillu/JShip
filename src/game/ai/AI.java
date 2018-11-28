package game.ai;

import java.util.Random;
import game.Location;


/**
 * Abstract Class dealing with board generation and shot placement.
 *
 * @author blackk100
 */
public class AI {
	Location[][] gridSelf, gridOpp;
	boolean[] initVars;

	/**
	 * Constructor for the abstract AI.
	 *
	 * @param initVars
	 * @param mode
	 * @param gridOpp
	 */
	AI(boolean[] initVars, String mode, Location[][] gridOpp) {
		this.initVars = initVars;

		this.gridSelf = new Location[initVars[0] ? 15 : 10][initVars[0] ? 15 : 10];
		this.gridOpp = gridOpp;

		this.place();
	}

	/**
	 * Used when a EvE round is running.
	 * Used by the 1st AI after AI2 places its ships.
	 *
	 * @param gridOpp Enemy Grid
	 */
	private void EvE(Location[][] gridOpp) {
		this.gridOpp = gridOpp;
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

		for (int i = 0; i < 37; i++) {
			if (i < 5) {
				shipLengths[i] = this.initVars[1] ? 1 : 0;
			} else if (i < 13) {
				shipLengths[i] = this.initVars[2] ? 1 : 0;
			} else if (i < 23) {
				shipLengths[i] = this.initVars[3] ? 1 : 0;
			} else {
				shipLengths[i] = this.initVars[4] ? 1 : 0;
			}
		}

		for (int i = 0; i < 37; i++) {
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
			} else {
				System.out.println("Ship " + i + " is not being used");
			}
		}

		System.out.println("All ships placed");
	}

	/**
	 * (Psuedo-)Randomiser for the place() function
	 *
	 * @return A 1-Dimensional array storing the X- and Y-Coordinates. 1st Integer is the Y-Coordinate. 2nd Integer is the X-Coordinate
	 */
	private int[] random(int length) {
		int[] out = new int[2];
		Random rand = new Random();

		for (int i = 0; i < 2; i++) {
			out[i] = rand.nextInt(this.initVars[0] ? 11 - length : 16 - length);
		}

		return out;
	}
}
