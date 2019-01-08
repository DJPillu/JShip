package game.ai;

import game.grid.Location;
import game.grid.Ship;
import java.util.Random;


/**
 * The Regular AI, i.e., the "Medium" AI.
 * Plays like an actual person.
 *
 * @author blackk100
 */
public final class Regular extends AI {

	/**
	 * Constant value of '0' indicating that there isn't any data on the given location.
	 */
	private final int NO_DATA = 0;

	/**
	 * Constant value of '1' indicating that the position has been shot at and was a miss, or doesn't have a ship part.
	 */
	private final int MISS = 1;

	/**
	 * Constant value of '2' indicating that the position has been shot at and was a hit.
	 */
	private final int HIT = 2;

	/**
	 * Constant value of '3' indicating that the position may have a ship part.
	 */
	private final int LIKELY = 3;

	/**
	 * A 2-Dimensional boolean array indicating the possible locations of the ship.
	 *
	 * Takes the values of <code>this.NO_DATA</code>, <code>this.MISS</code>, <code>this.HIT</code> and
	 * <code>this.LIKELY</code>.
	 */
	private int[][] probability;

	/**
	 * Constructor for the Regular AI.
	 *
	 * @param initVars Initialization Variables
	 * @param gridOpp  Enemy Grid
	 * @param shipsOpp Enemy Ships
	 */
	public Regular(boolean[] initVars, Location[][] gridOpp, Ship[] shipsOpp) {
		super(initVars, gridOpp, shipsOpp);

		this.probability = new int[this.gridSize][this.gridSize]; // Initializes probability. As default value for int is 0, all elements are equal to this.NO_DATA.
	}

	/**
	 * Function for the AI to fire at the player's ships.
	 *
	 * The coordinates are initially randomly generated until a hit occurs ('hunt' mode).
	 *
	 * Then the AI fires in the 4 adjacent positions (if available, ex.: at the corners and edges,
	 * only 2 and 3 directions are available respectively).
	 * In the previous step, if another hit occurs, the AI continues firing in that direction until
	 * no more hits occur.
	 *
	 * The process is then repeated.
	 *
	 * @return an integer array. The 1st value is the X-Coordinate. The 2nd value is the Y-Coordinate.
	 */
	@Override
	public int[] fire() {
		int[] xy = this.randomFire(); // Firing Coordinates

		huntCheck:
		for (int y = 0; y < this.gridSize; y++) {
			for (int x = 0; x < this.gridSize; x++) {
				if (this.probability[y][x] == this.LIKELY) {
					xy = new int[] {x, y};
					this.probability[y][x] = this.NO_DATA; // Prevents the AI from shooting at the same spot in Salvo.
					break huntCheck;
				}
			}
		}

		return xy;
	}

	/**
	 * A setter for gridOpp.
	 * Also updates <code>this.probability</code>.
	 *
	 * @param gridOpp Location Grid of the Player
	 */
	@Override
	public void updateGridOpp(Location[][] gridOpp) {
		this.gridOpp = gridOpp;

		this.setSunk();
		this.setProbability();
	}

	/**
	 * Support function 1 for the AI to fire at the player's ships.
	 * The coordinates are always (psuedo-)randomly generated.
	 *
	 * Is identical to <code>Sandbox.fire()</code>
	 *
	 * @return an integer array. The 1st value is the x-Coordinate, 2nd value is the Y-Coordinate.
	 */
	private int[] randomFire() {
		int[] xy = new int[2];        // Firing coordinates.
		int[] temp = new int[2];      // Temporary variable to store coordinates if xy refers to a guessed position.
		Random random = new Random(); // Random data type generator (built-in class).

		// If xy refers to a guessed coordinate, or a location that borders a ship tile that has been guessed, regenerate it.
		do { // Do initializes xy.
			for (int i = 0; i < 2; i++) {
				temp[i] = random.nextInt(this.gridSize);
			}
			xy = temp;

			if (!this.gridOpp[xy[1]][xy[0]].isUnguessed()) { // Checks if the current location has been guessed.
				continue;
			} else {                                         // Checks if the current location borders any known ship.
				if ((xy[0] > 0) && (xy[0] < this.gridSize - 1) && (xy[1] > 0) && (xy[1] < this.gridSize - 1)) { // Not at edges
					if (this.gridOpp[xy[1] - 1][xy[0]].isHit() || this.gridOpp[xy[1] + 1][xy[0]].isHit() || this.gridOpp[xy[1]][xy[0] - 1].isHit() || this.gridOpp[xy[1]][xy[0] + 1].isHit()) {
						continue;
					}
				} else {                                     // At the Edges
					if (xy[1] == 0) {                          // Top edge
						if (xy[0] == 0) {                        // Top-Left Corner
							if (this.gridOpp[xy[1]][xy[0] + 1].isHit() || this.gridOpp[xy[1] + 1][xy[0]].isHit()) {
								continue;
							}
						} else if (xy[0] == this.gridSize - 1) { // Top-Right Corner
							if (this.gridOpp[xy[1]][xy[0] - 1].isHit() || this.gridOpp[xy[1] + 1][xy[0]].isHit()) {
								continue;
							}
						} else {                                 // Top Edge (excluding Corners)
							if (this.gridOpp[xy[1]][xy[0] + 1].isHit() || this.gridOpp[xy[1]][xy[0] - 1].isHit() || this.gridOpp[xy[1] + 1][xy[0]].isHit()) {
								continue;
							}
						}
					} else if (xy[1] == this.gridSize - 1) {   // Bottom Edge
						if (xy[0] == 0) {                        // Bottom-Left Corner
							if (this.gridOpp[xy[1]][xy[0] + 1].isHit() || this.gridOpp[xy[1] - 1][xy[0]].isHit()) {
								continue;
							}
						} else if (xy[0] == this.gridSize - 1) { // Bottom-Right Corner
							if (this.gridOpp[xy[1]][xy[0] - 1].isHit() || this.gridOpp[xy[1] - 1][xy[0]].isHit()) {
								continue;
							}
						} else {                                 // Bottom Edge (excluding Corners)
							if (this.gridOpp[xy[1]][xy[0] + 1].isHit() || this.gridOpp[xy[1]][xy[0] - 1].isHit() || this.gridOpp[xy[1] - 1][xy[0]].isHit()) {
								continue;
							}
						}
					} else if (xy[0] == 0) {                   // Left Edge (excluding Corners)
						if (this.gridOpp[xy[1] + 1][xy[0]].isHit() || this.gridOpp[xy[1] - 1][xy[0]].isHit() || this.gridOpp[xy[1]][xy[0] + 1].isHit()) {
							continue;
						}
					} else {                                   // Rght Edge (ecluding Corners)
						if (this.gridOpp[xy[1] + 1][xy[0]].isHit() || this.gridOpp[xy[1] - 1][xy[0]].isHit() || this.gridOpp[xy[1]][xy[0] - 1].isHit()) {
							continue;
						}
					}
				}
			}

			break;
		} while (true);

		return xy;
	}

	/**
	 * Sets the probability values.
	 */
	private void setProbability() {
		for (int y = 0; y < this.gridSize; y++) {
			for (int x = 0; x < this.gridSize; x++) {
				if (this.gridOpp[y][x].isHit()) {         // Checks if the location is guessed and the shot hit.
					this.setHit(x, y);

					int adjacent = this.checkAdjacentHit(x, y);
					if (adjacent == 0) {   // Checks if this location contains a ship part.
						this.setAdjacentLikely(x, y);
					} else {               // Possible start of a ship or the middle of a ship.
						if (adjacent == 1) { // Checks if this location is the possible start of a ship.
							this.setAdjacentLikely(x, y, adjacent == 3);
						}
						this.setAdjacentMiss(x, y, adjacent == 3);
					}
				} else if (this.gridOpp[y][x].isMiss()) { // Checks if the location is guessed but the shot missed.
					this.setMiss(x, y);
				}
			}
		}

	}

	/**
	 * Returns whether the given coordinate has 0, 1, or 2 adjacent positions with hits.
	 *
	 * @param x X-Coordinate
	 * @param y Y-Coordinate
	 *
	 * @return an integer. 0 - No adjacent hits; 1 - 1 adjacent hit; 2 - 2 adjacent hits horizontally;
	 *         3 - 2 adjacent hits vertically.
	 */
	private int checkAdjacentHit(int x, int y) {
		int ret = 0;

		if ((x > 0) && (x < this.gridSize - 1) && (y > 0) && (y < this.gridSize - 1)) { // Not at edges
			if (this.checkHit(x, y - 1) || this.checkHit(x, y + 1) || this.checkHit(x - 1, y) || this.checkHit(x + 1, y)) {
				if (this.checkHit(x - 1, y) && this.checkHit(x + 1, y)) {
					ret = 2;
				} else if (this.checkHit(x, y - 1) && this.checkHit(x, y + 1)) {
					ret = 3;
				} else {
					ret = 1;
				}
			}
		} else {                                 // At the Edges
			if (y == 0) {                          // Top edge
				if (x == 0) {                        // Top-Left Corner
					if (this.checkHit(x, y + 1) || this.checkHit(x + 1, y)) {
						ret = 1;
					}
				} else if (x == this.gridSize - 1) { // Top-Right Corner
					if (this.checkHit(x, y + 1) || this.checkHit(x - 1, y)) {
						ret = 1;
					}
				} else {                             // Top Edge (excluding Corners)
					if (this.checkHit(x, y + 1)) {
						ret = 1;
					} else if (this.checkHit(x - 1, y) || this.checkHit(x + 1, y)) {
						if (this.checkHit(x - 1, y) && this.checkHit(x + 1, y)) {
							ret = 2;
						} else {
							ret = 1;
						}
					}
				}
			} else if (y == this.gridSize - 1) {   // Bottom Edge
				if (x == 0) {                        // Bottom-Left Corner
					if (this.checkHit(x, y - 1) || this.checkHit(x + 1, y)) {
						ret = 1;
					}
				} else if (x == this.gridSize - 1) { // Bottom-Right Corner
					if (this.checkHit(x, y - 1) || this.checkHit(x - 1, y)) {
						ret = 1;
					}
				} else {                             // Bottom Edge (excluding Corners)
					if (this.checkHit(x, y - 1)) {
						ret = 1;
					} else if (this.checkHit(x - 1, y) || this.checkHit(x + 1, y)) {
						if (this.checkHit(x - 1, y) && this.checkHit(x + 1, y)) {
							ret = 2;
						} else {
							ret = 1;
						}
					}
				}
			} else if (x == 0) {                   // Left Edge (excluding Corners)
				if (this.checkHit(x + 1, y)) {
					ret = 1;
				} else if (this.checkHit(x, y - 1) || this.checkHit(x, y + 1)) {
					if (this.checkHit(x, y - 1) && this.checkHit(x, y + 1)) {
						ret = 3;
					} else {
						ret = 1;
					}
				}
			} else {                               // Rght Edge (ecluding Corners)
				if (this.checkHit(x - 1, y)) {
					ret = 1;
				} else if (this.checkHit(x, y - 1) || this.checkHit(x, y + 1)) {
					if (this.checkHit(x, y - 1) && this.checkHit(x, y + 1)) {
						ret = 3;
					} else {
						ret = 1;
					}
				}
			}
		}

		return ret;
	}

	/**
	 * Sets adjacent locations to be <code>this.LIKELY</code>.
	 * Only to be used if there are no adjacent location having <code>this.HIT</code>.
	 *
	 * @param x X-Coordinate
	 * @param y Y-Coordinate
	 */
	private void setAdjacentLikely(int x, int y) {
		if ((x > 0) && (x < this.gridSize - 1) && (y > 0) && (y < this.gridSize - 1)) { // Not at edges
			this.setLikely(x, y - 1);
			this.setLikely(x, y + 1);
			this.setLikely(x - 1, y);
			this.setLikely(x + 1, y);
		} else {                                                                        // At the Edges
			if (y == 0) {                                                                 // Top edge
				if (x == 0) {                                                               // Top-Left Corner
					this.setLikely(x, y + 1);
					this.setLikely(x + 1, y);
				} else if (x == this.gridSize - 1) {                                        // Top-Right Corner
					this.setLikely(x, y + 1);
					this.setLikely(x - 1, y);
				} else {                                                                    // Top Edge (excluding Corners)
					this.setLikely(x, y + 1);
					this.setLikely(x - 1, y);
					this.setLikely(x + 1, y);
				}
			} else if (y == this.gridSize - 1) {                                          // Bottom Edge
				if (x == 0) {                                                               // Bottom-Left Corner
					this.setLikely(x, y - 1);
					this.setLikely(x + 1, y);
				} else if (x == this.gridSize - 1) {                                        // Bottom-Right Corner
					this.setLikely(x, y - 1);
					this.setLikely(x - 1, y);
				} else {                                                                    // Bottom Edge (excluding Corners)
					this.setLikely(x, y - 1);
					this.setLikely(x - 1, y);
					this.setLikely(x + 1, y);
				}
			} else if (x == 0) {                                                          // Left Edge (excluding Corners)
				this.setLikely(x, y - 1);
				this.setLikely(x, y + 1);
				this.setLikely(x + 1, y);
			} else {                                                                      // Rght Edge (ecluding Corners)
				this.setLikely(x, y - 1);
				this.setLikely(x, y + 1);
				this.setLikely(x - 1, y);
			}
		}
	}

	/**
	 * Sets adjacent locations to be <code>this.LIKELY</code>.
	 * Only to be used if there is a single adjacent location having <code>this.HIT</code>.
	 *
	 * @param x         X-Coordinate
	 * @param y         Y-Coordinate
	 * @param direction a boolean indicating whether the ship is placed vertically (true) or horizontally (false).
	 */
	private void setAdjacentLikely(int x, int y, boolean direction) {
		if ((x == 0) || (x == this.gridSize - 1)) {         // Checks if the location is at an horizontal edge.
			if (direction) {                                  // Checks if no ships start here.
				if (this.checkHit(x, y - 1)) {
					this.setLikely(x, y + 1);
				} else {
					this.setLikely(x, y - 1);
				}
			}
		} else if ((y == 0) || (y == this.gridSize - 1)) { // Checks if the location is at a vertical edge and if no ships start here.
			if (!direction) {                                // Checks if no ships start here.
				if (this.checkHit(x - 1, y)) {
					this.setLikely(x + 1, y);
				} else {
					this.setLikely(x - 1, y);
				}
			}
		} else {                                           // Not at an edge.
			if (direction) {                                 // Checks if the ship is placed vertically.
				if (this.checkHit(x, y - 1)) {
					this.setLikely(x, y + 1);
				} else {
					this.setLikely(x, y - 1);
				}
			} else {                                         // The ship is placed horizontally.
				if (this.checkHit(x - 1, y)) {
					this.setLikely(x + 1, y);
				} else {
					this.setLikely(x - 1, y);
				}
			}
		}
	}

	/**
	 * Sets the adjacent locations to the given coordinates as MISS.
	 *
	 * @param x         X-Coordinate
	 * @param y         Y-Coordinate
	 * @param direction a boolean indicating whether the ship is placed vertically (true) or horizontally (false).
	 */
	private void setAdjacentMiss(int x, int y, boolean direction) {
		if (direction) {                       // Checks if the ship is vertical.
			if (x == 0) {                        // Checks if the location is on the Left Edge
				this.setMiss(x + 1, y);
			} else if (x == this.gridSize - 1) { // Checks if the location is on the Right Edge
				this.setMiss(x - 1, y);
			} else {                             // Not on vertical edges.
				this.setMiss(x - 1, y);
				this.setMiss(x + 1, y);
			}
		} else {                               // Ship is horizontal.
			if (y == 0) {                        // Checks if the location is on the Top Edge
				this.setMiss(x, y + 1);
			} else if (y == this.gridSize - 1) { // Checks if the location is on the Bottom Edge
				this.setMiss(x, y - 1);
			} else {                             // Not on horizontal edges.
				this.setMiss(x, y - 1);
				this.setMiss(x, y + 1);
			}
		}
	}

	/**
	 * Sets the given probability location as a <code>this.HIT</code>.
	 *
	 * @param x X-Coordinate
	 * @param y Y-Coordinate
	 */
	private void setHit(int x, int y) {
		if ((this.probability[y][x] == this.NO_DATA) || (this.probability[y][x] == this.LIKELY)) {
			this.probability[y][x] = this.HIT;
		}
	}

	/**
	 * Returns whether the given probability location equals <code>this.HIT</code>.
	 *
	 * @param x X-Coordinate
	 * @param y Y-Coordinate
	 *
	 * @return true if hit. false if not hit.
	 */
	private boolean checkHit(int x, int y) {
		return this.probability[y][x] == this.HIT;
	}

	/**
	 * Sets the given probability location as a <code>this.MISS</code>.
	 *
	 * @param x X-Coordinate
	 * @param y Y-Coordinate
	 */
	private void setMiss(int x, int y) {
		if ((this.probability[y][x] == this.NO_DATA) || (this.probability[y][x] == this.LIKELY)) {
			this.probability[y][x] = this.MISS;
		}
	}

	/**
	 * Sets the given probability location as a <code>this.LIKELY</code>.
	 *
	 * @param x X-Coordinate
	 * @param y Y-Coordinate
	 */
	private void setLikely(int x, int y) {
		if (this.probability[y][x] == this.NO_DATA) {
			this.probability[y][x] = this.LIKELY;
		}
	}

	/**
	 * Sets <code>this.probability</code> locations to reflect sinking the given enemy ship.
	 */
	private void setSunk() {
		for (int shot = 0; shot < this.shotsSunk.length; shot++) {
			if ((this.shotsSunk[shot][0] > -1) && (this.shotsSunk[shot][1] > -1)) { // Checks if it a valid coordinate.
				for (int ship = 0; ship < this.shipNos; ship++) {
					if (this.shipsOpp[ship].getPosition(this.shotsSunk[shot]) > -1) {   // Checks if this ship was sunk with this shot.
						int shipLength = this.shipsOpp[ship].length;            // Length
						boolean direction = this.shipsOpp[ship].getDirection(); // Direction
						int[] xy = this.shipsOpp[ship].getStart();              // Start Coordinates

						for (int l = 0; l < shipLength; l++) {
							this.probability[xy[1] + (direction ? l : 0)][xy[0] + (direction ? 0 : l)] = this.HIT;

							// At the terminal positions of the ship.
							if ((l == 0) && (xy[direction ? 1 : 0] != 0)) {          // Checks if the 1st tile isn't at the edge of the Board
								this.probability[xy[1] - (direction ? 1 : 0)][xy[0] - (direction ? 0 : 1)] = this.MISS;
							} else if ((l == shipLength - 1) && (xy[direction ? 1 : 0] + shipLength - 1 != this.gridSize - 1)) { // Checks if the last tile isn't at the edge of the Board
								this.probability[xy[1] + (direction ? shipLength : 0)][xy[0] + (direction ? 0 : shipLength)] = this.MISS;
							}
							// Along the length of the ship
							if (xy[direction ? 0 : 1] == 0) {                        // At the Top/Left edge
								this.probability[xy[1] + (direction ? l : 1)][xy[0] + (direction ? 1 : l)] = this.MISS;
							} else if (xy[direction ? 0 : 1] == this.gridSize - 1) { // At the Bottom/Right edge
								this.probability[xy[1] + (direction ? l : -1)][xy[0] + (direction ? -1 : l)] = this.MISS;
							} else {                                                 // Not at the edges
								this.probability[xy[1] + (direction ? l : -1)][xy[0] + (direction ? -1 : l)] = this.MISS;
								this.probability[xy[1] + (direction ? l : 1)][xy[0] + (direction ? 1 : l)] = this.MISS;
							}
						}
					}
				}
			}
		}
	}

}
