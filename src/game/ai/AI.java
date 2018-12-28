package game.ai;

import game.grid.Location;
import game.grid.Ship;
import java.util.Random;


/**
 * Base AI Class.
 *
 * Only decides ship placement with a placeholder function for shot placement,
 * which is implemented in each individual AI.
 *
 * Also has a placeholder function for updating the User's Ships list, which is implemented only in the Brutal AI.
 *
 * @author blackk100
 */
public class AI {

	/**
	 * 2-Dimensional Location Grid representing the AI's grid
	 */
	Location[][] gridSelf;

	/**
	 * 2-Dimensional Location Grid representing the user's grid
	 */
	Location[][] gridOpp;

	/**
	 * An array storing the ships of the AI.
	 */
	Ship[] shipsSelf;

	/**
	 * An array storing the ships of the Player.
	 * Only used by the brutal AI.
	 */
	Ship[] shipsOpp;

	/**
	 * An integer show the length of the grid.
	 * if initVars[0] is true, gridSize is 15. Else 10.
	 */
	final int gridSize;

	/**
	 * Total number of ships available in this match.
	 */
	final int shipNos;

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
	 * I     4     I       Corvette        I
	 * I-----------I-----------------------I
	 * </pre>
	 */
	boolean[] initVars;

	/**
	 * Constructor for the AI.
	 *
	 * @param initVars Initialization VariablesW
	 * @param gridOpp  Enemy's 2-Dimensional Location Grid
	 * @param shipsOpp Enemy Ships
	 */
	AI(boolean[] initVars, Location[][] gridOpp, Ship[] shipsOpp) {
		this.initVars = initVars;
		this.gridSize = initVars[0] ? 15 : 10;
		this.gridOpp = gridOpp;
		this.shipsOpp = shipsOpp;

		this.gridSelf = new Location[this.gridSize][this.gridSize];
		for (int y = 0; y < this.gridSize; y++) {
			for (int x = 0; x < this.gridSize; x++) {
				this.gridSelf[y][x] = new Location();
			}
		}

		this.shipNos = (initVars[1] ? 1 : 0) + (initVars[2] ? 2 : 0) + (initVars[3] ? 2 : 0) + (initVars[4] ? 4 : 0);
		int shipNo = 0;    // Temporary variable storing the ship number being initialized.
		this.shipsSelf = new Ship[this.shipNos];
		if (initVars[1]) { // Battleships
			for (int i = shipNo; i < shipNo + 1; i++) {
				this.shipsSelf[i] = new Ship(5);
			}
			shipNo += 1;
		}
		if (initVars[2]) { // Cruisers
			for (int i = shipNo; i < shipNo + 2; i++) {
				this.shipsSelf[i] = new Ship(4);
			}
			shipNo += 2;
		}
		if (initVars[3]) { // Destroyers
			for (int i = shipNo; i < shipNo + 2; i++) {
				this.shipsSelf[i] = new Ship(3);
			}
			shipNo += 2;
		}
		if (initVars[4]) { // Corvettes
			for (int i = shipNo; i < shipNo + 4; i++) {
				this.shipsSelf[i] = new Ship(2);
			}
		}
		// Implicit Garbage Collecting temp (hopefully). Remove if not necessary.
		shipNo = 0;
		System.gc();

		this.place();
	}

	/**
	 * A setter for gridSelf
	 *
	 * @param gridSelf Location Grid of the AI
	 */
	public void updateGridSelf(Location[][] gridSelf) {
		this.gridSelf = gridSelf;
	}

	/**
	 * A setter for gridOpp
	 *
	 * @param gridOpp Location Grid of the Player
	 */
	public void updateGridOpp(Location[][] gridOpp) {
		this.gridOpp = gridOpp;
	}

	/**
	 * A setter for shipsSelf
	 *
	 * @param shipsSelf Ships list of the AI
	 */
	public void updateShipsSelf(Ship[] shipsSelf) {
		this.shipsSelf = shipsSelf;
	}

	/**
	 * A setter for shipsOpp.
	 * Blank for now to prevent accidental usage by Sandbox and Regular.
	 *
	 * @param shipsOpp Ships list of the Player
	 */
	public void updateShipsOpp(Ship[] shipsOpp) {
	}

	/**
	 * A getter for gridSelf
	 *
	 * @return gridSelf Self Grid
	 */
	public Location[][] getGridSelf() {
		return this.gridSelf;
	}

	/**
	 * A getter for shipsSelf
	 *
	 * @return shipsSelf Self Ships
	 */
	public Ship[] getShipsSelf() {
		return this.shipsSelf;
	}

	/**
	 * Placeholder function for the AI to fire at the player's ships.
	 *
	 * @return A 1-Dimensional array storing the X- and Y-Coordinates.
	 *         1st Integer is the Y-Coordinate. 2nd Integer is the X-Coordinate
	 */
	public int[] fire() {
		return new int[] {-1, -1};
	}

	/**
	 * Places ships in gridSelf.
	 */
	private void place() {
		int shipLength;
		boolean direction;
		Random random = new Random();
		for (int i = 0; i < this.shipNos; i++) {
			shipLength = this.shipsSelf[i].length;
			direction = random.nextBoolean();
			System.out.println("AI Ship no. " + i + ": " + shipLength + (direction ? " Vertical" : " Horizontal"));

			int[] xy = this.random(shipLength);

			intersect:
			while (true) {     // Checks for any intersections
				for (int l = 0; l < shipLength; l++) {
					// Checks if the ship will intersect any other ship
					if (this.gridSelf[xy[1] + (direction ? l : 0)][xy[0] + (direction ? 0 : l)].hasShip()) {
						System.out.println("AI - Ship exists at position: " + (l + 1));
						xy = this.random(shipLength);
						continue intersect;
					}

					// Checks if the ship will border any other ship
					if (this.gridSelf[xy[1] + (direction ? l : 0)][xy[0] + (direction ? 0 : l)].isBorder()) {
						System.out.println("AI - Bordering another ship at position: " + (l + 1));
						xy = this.random(shipLength);
						continue intersect;
					}
				}

				break intersect; // No intersections. Continue to placement.
			}

			// Sets the location values
			for (int l = 0; l < shipLength; l++) {
				this.gridSelf[xy[1] + (direction ? l : 0)][xy[0] + (direction ? 0 : l)].shipPresent();

				// Sets the borders at the terminal positions of the ship.
				if ((l == 0) && (xy[direction ? 1 : 0] != 0)) {                                                      // Checks if the 1st tile isn't at the edge of the Board
					this.gridSelf[xy[1] - (direction ? 1 : 0)][xy[0] - (direction ? 0 : 1)].bordersShip();
				} else if ((l == shipLength - 1) && (xy[direction ? 1 : 0] + shipLength - 1 != this.gridSize - 1)) { // Checks if the last tile isn't at the edge of the Board
					this.gridSelf[xy[1] + (direction ? shipLength : 0)][xy[0] + (direction ? 0 : shipLength)].bordersShip();
				}
				// Sets the borders along the length of the ship
				if (xy[direction ? 0 : 1] == 0) {                        // At the Top/Left edge
					this.gridSelf[xy[1] + (direction ? l : 1)][xy[0] + (direction ? 1 : l)].bordersShip();
				} else if (xy[direction ? 0 : 1] == this.gridSize - 1) { // At the Bottom/Right edge
					this.gridSelf[xy[1] + (direction ? l : -1)][xy[0] + (direction ? -1 : l)].bordersShip();
				} else {                                                 // Not at the edges
					this.gridSelf[xy[1] + (direction ? l : -1)][xy[0] + (direction ? -1 : l)].bordersShip();
					this.gridSelf[xy[1] + (direction ? l : 1)][xy[0] + (direction ? 1 : l)].bordersShip();
				}
			}
			this.shipsSelf[i].add(xy, direction);
			System.out.println("Ship " + i + " placed");

		}

		System.out.println("All ships placed");

	}

	/**
	 * (Psuedo-)Randomiser for the <code>place()</code> function
	 *
	 * @param length Length of the Ship
	 *
	 * @return A 1-Dimensional array storing the X- and Y-Coordinates.
	 *         1st Integer is the X-Coordinate. 2nd Integer is the Y-Coordinate
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
