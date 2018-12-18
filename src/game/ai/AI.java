package game.ai;

import game.Grid.Location;
import game.Grid.Ship;
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
		this.boardSize = initVars[0] ? 15 : 10;
		this.gridOpp = gridOpp;
		this.shipsOpp = shipsOpp;

		this.gridSelf = new Location[this.boardSize][this.boardSize];
		for (int y = 0; y < this.boardSize; y++) {
			for (int x = 0; x < this.boardSize; x++) {
				this.gridSelf[y][x] = new Location();
			}
		}

		int shipNos = (initVars[1] ? 1 : 0) + (initVars[2] ? 3 : 0) + (initVars[3] ? 4 : 0) + (initVars[4] ? 5 : 0);
		int shipNo = 0;    // Temporary variable storing the ship number being initialized.
		this.shipsSelf = new Ship[shipNos];
		Random random = new Random();
		if (initVars[1]) { // Battleships
			for (int i = shipNo; i < shipNo + 1; i++) {
				this.shipsSelf[i] = new Ship(5, random.nextBoolean());
			}
			shipNo += 1;
		}
		if (initVars[2]) { // Cruisers
			for (int i = shipNo; i < shipNo + 3; i++) {
				this.shipsSelf[i] = new Ship(4, random.nextBoolean());
			}
			shipNo += 3;
		}
		if (initVars[3]) { // Destroyers
			for (int i = shipNo; i < shipNo + 4; i++) {
				this.shipsSelf[i] = new Ship(3, random.nextBoolean());
			}
			shipNo += 4;
		}
		if (initVars[4]) { // Corvettes
			for (int i = shipNo; i < shipNo + 5; i++) {
				this.shipsSelf[i] = new Ship(2, random.nextBoolean());
			}
		}
		shipNo = 0;
		random = null;
		System.gc();

		this.place(shipNos);
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
	 *
	 * @param shipNos the number of ships present
	 */
	private void place(int shipNos) {
		for (int i = 0; i < shipNos; i++) {
			int shipLength = this.shipsSelf[i].length;
			boolean direction = this.shipsSelf[i].getDirection();
			System.out.println(i + ": " + shipLength + (direction ? " Vertical" : " Horizontal"));

			int[] xy = this.random(shipLength); // 0: Y-Coordinate ; 1: X-Coordinate

			// Checks for any intersections
			top:                                           // outer loop label
			while (true) {
				if (this.gridSelf[xy[1]][xy[0]].hasShip()) { // Checks if the initial location has a ship part
					System.out.println("Ship exists at initial point");
					xy = this.random(shipLength);
					continue;
				}

				for (int l = 0; l < shipLength; l++) {       // Checks if the ship will intersect any other ship
					if (this.gridSelf[xy[1] + (direction ? l : 0)][xy[0] + (direction ? 0 : l)].hasShip()) { // X- is incremented if horizontal, else Y-
						System.out.println("Ship exists at position: " + (l + 1));
						xy = this.random(shipLength);
						continue top;
					}
				}

				break;                                       // No intersections. Continue to placement.
			}

			// Sets the location values
			for (int l = 0; l < shipLength; l++) {
				this.gridSelf[xy[1] + (direction ? l : 0)][xy[0] + (direction ? 0 : l)].shipPresent(); // X- is incremented if horizontal, else Y-
			}
			this.shipsSelf[i].setStart(xy);
			System.out.println("Ship " + i + " placed");

		}

		System.out.println("All ships placed");

	}

	/**
	 * (Psuedo-)Randomiser for the place() function
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
