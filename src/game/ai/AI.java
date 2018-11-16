package game.ai;

import java.util.Random;
import game.Location;


/**
 * Abstract Class dealing with board generation and shot placement.
 *
 * @author blackk100
 */
abstract class AI {
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
		this.gridSelf = new Location[initVars[0] ? 15 : 10][initVars[0] ? 15 : 10];
		this.gridOpp = gridOpp;
		this.place();
	}

	/**
	 * Used when a EvE round is running.
	 * Used by the 1st AI after AI2 places its ships.
	 *
	 * @param gridOpp
	 */
	private void EvE(Location[][] gridOpp) {
		this.gridOpp = gridOpp;
	}

	/**
	 * A getter for gridSelf
	 *
	 * @return gridSelf
	 */
	public Location[][] getGrid() {
		return this.gridSelf;
	}

	/**
	 * Places ships in gridSelf.
	 */
	private void place() {
		int[] shipLengths = new int[12];
		Random rand = new Random();

		for (int i = 0; i < 13; i++) {
			if (i < 2) {
				shipLengths[i] = this.initVars[1] ? 5 : 0;
			} else if (i < 4) {
				shipLengths[i] = this.initVars[2] ? 4 : 0;
			} else if (i < 7) {
				shipLengths[i] = this.initVars[3] ? 3 : 0;
			} else {
				shipLengths[i] = this.initVars[4] ? 2 : 0;
			}
		}

		for (int i = 0; i < 13; i++) {
			int shipLength = shipLengths[i];
			System.out.println(i + ": " + shipLength);

			if (shipLength > 0) {
				int[] randLoc = random(shipLength);           // 0   : Y-Coordinate ; 1: X-Coordinate
				boolean randomDirection = rand.nextBoolean(); // True: Vertical     ; False: Horizontal

				top:
				while (true) {
					if (this.gridSelf[randLoc[0]][randLoc[1]].hasShip()) { // Checks if the initial location has a ship part
						System.out.println("Ship exists at initial point");
						randLoc = random(shipLength);
						continue;
					}

					// Checks if the ship will intersect any other ship
					if (randomDirection) {
						for (int j = 0; i < shipLength; i++) {
							if (this.gridSelf[randLoc[0] + j][randLoc[1]].hasShip()) { // Y-Coordinate is incremented if vertical
								System.out.println("Ship exists at vertical length: " + (j + 1));
								continue top;
							}
						}
					} else {
						for (int j = 0; i < shipLength; i++) {
							if (this.gridSelf[randLoc[0]][randLoc[1] + j].hasShip()) { // X-Coordinate is incremented if horizontal
								System.out.println("Ship exists at horizontal length: " + (j + 1));
								continue top;
							}
						}
					}

					break;
				}

				if (randomDirection) {
					for (int j = 0; i < shipLength; i++) {
						this.gridSelf[randLoc[0] + j][randLoc[1]].shipPresent(); // Y-Coordinate is incremented if vertical
					}
				} else {
					for (int j = 0; i < shipLength; i++) {
						this.gridSelf[randLoc[0]][randLoc[1] + j].shipPresent(); // X-Coordinate is incremented if horizontal
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
	 * @return
	 */
	private int[] random(int limit) {
		int[] out = new int[2];
		Random rand = new Random();

		for (int i = 0; i < 2; i++) {
			out[i] = rand.nextInt(this.initVars[0] ? 11 - limit : 16 - limit);
		}

		return out;
	}

	/**
	 * Abstract Function for the AI to fire at the player's ships
	 *
	 * @return an integer array. The 1st value is the X-Coordinate. The 2nd value is the Y-Coordinate
	 */
	public abstract int[] fire();
}
