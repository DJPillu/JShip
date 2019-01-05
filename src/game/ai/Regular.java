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
	 * If false, the AI 'hunts' for a suitable target by randomly firing at various locations.
	 * Else, the AI selectively targets the ship.
	 */
	private boolean hunt = true;

	/**
	 * Stores the previous shot.
	 *
	 * The 1st element in the 2nd Dimension is the X-Coordinate, the 2nd is the Y-Coordinate.
	 */
	private int[] prevShot = new int[] {-1, -1};

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
		int[] xy; // Firing Coordinates

		if (!this.hunt) { // This block checks if the previous shot sunk a ship. If yes, it updates the probability map.
			huntSunk:
			for (int ship = 0; ship < this.shipNos; ship++) {
				if ((this.shipsOpp[ship].getPosition(this.prevShot) > -1) && this.shipsOpp[ship].isSunk()) { // Checks if the previous shot was of this ship the ship was sunk.
					this.hunt = true;
					this.setProbabilitySunk(this.shipsOpp[ship]);
					break huntSunk;
				}
			}
		}
		if (!this.hunt) { // This block checks if there are any valid locations to fire at. Used as a fail-safe in case this.nextShot() doesn't update the probability map correctly.
			System.out.println("WARNING: AI unable to locate valid targetting location. Reverting to hunt mode.");
			boolean temp = true;
			huntNoLikely:
			for (int y = 0; y < this.gridSize; y++) {
				for (int x = 0; x < this.gridSize; x++) {
					if (this.probability[y][x] == this.LIKELY) {
						temp = false;
						break huntNoLikely;
					}
				}
			}
			this.hunt = temp;
		}

		if (this.hunt) {                              // Checks if the AI is in hunt mode.
			System.out.println("\nAI Hunting");
			xy = this.randomFire();

			if (this.gridOpp[xy[1]][xy[0]].hasShip()) { // Checks if a shot laanded.
				this.hunt = false; // Switch to targetting mode.
				this.setProbability(xy);
			} else {                                    // Shot missed.
				this.probability[xy[1]][xy[0]] = this.MISS;
			}
		} else {                                      // In targetting mode.
			System.out.println("\nAI Targetting");
			xy = this.nextShot();
		}

		this.prevShot = xy;
		return xy;
	}

	/**
	 * Sets the probability values depending on the given input (shot location).
	 * Only used for the 1st shot. Deviations due to Successive shots is handled by <code>this.nextShot()</code>
	 *
	 * @param xy the coordinates. 1st element is X-Coordinate, 2nd is Y-Coordinate.
	 */
	private void setProbability(int[] xy) {
		this.setProbabilityHit(xy[1], xy[0]);
		if ((xy[0] > 0) && (xy[0] < this.gridSize - 1) && (xy[1] > 0) && (xy[1] < this.gridSize - 1)) { // Not at edges
			this.setProbabilityLikely(xy[1] - 1, xy[0]);
			this.setProbabilityLikely(xy[1] + 1, xy[0]);
			this.setProbabilityLikely(xy[1], xy[0] - 1);
			this.setProbabilityLikely(xy[1], xy[0] + 1);
		} else {                                     // At the Edges
			if (xy[1] == 0) {                          // Top edge
				if (xy[0] == 0) {                        // Top-Left Corner
					this.setProbabilityLikely(xy[1], xy[0] + 1);
					this.setProbabilityLikely(xy[1] + 1, xy[0]);
				} else if (xy[0] == this.gridSize - 1) { // Top-Right Corner
					this.setProbabilityLikely(xy[1], xy[0] - 1);
					this.setProbabilityLikely(xy[1] + 1, xy[0]);
				} else {                                 // Top Edge (excluding Corners)
					this.setProbabilityLikely(xy[1], xy[0] - 1);
					this.setProbabilityLikely(xy[1] + 1, xy[0]);
					this.setProbabilityLikely(xy[1], xy[0] + 1);
				}
			} else if (xy[1] == this.gridSize - 1) {   // Bottom Edge
				if (xy[0] == 0) {                        // Bottom-Left Corner
					this.setProbabilityLikely(xy[1] - 1, xy[0]);
					this.setProbabilityLikely(xy[1], xy[0] + 1);
				} else if (xy[0] == this.gridSize - 1) { // Bottom-Right Corner
					this.setProbabilityLikely(xy[1] - 1, xy[0]);
					this.setProbabilityLikely(xy[1], xy[0] - 1);
				} else {                                 // Bottom Edge (excluding Corners)
					this.setProbabilityLikely(xy[1], xy[0] - 1);
					this.setProbabilityLikely(xy[1] - 1, xy[0]);
					this.setProbabilityLikely(xy[1], xy[0] + 1);
				}
			} else if (xy[0] == 0) {                   // Left Edge (excluding Corners)
				this.setProbabilityLikely(xy[1] - 1, xy[0]);
				this.setProbabilityLikely(xy[1], xy[0] + 1);
				this.setProbabilityLikely(xy[1] + 1, xy[0]);
			} else {                                   // Rght Edge (ecluding Corners)
				this.setProbabilityLikely(xy[1] - 1, xy[0]);
				this.setProbabilityLikely(xy[1], xy[0] - 1);
				this.setProbabilityLikely(xy[1] + 1, xy[0]);
			}
		}
	}

	/**
	 * Sets <code>this.probability</code> locations to reflect sinking the given enemy ship.
	 *
	 * @param ship the ship that was sunk
	 */
	private void setProbabilitySunk(Ship ship) {
		int shipLength = ship.length;
		boolean direction = ship.getDirection();
		int[] xy = ship.getStart();

		for (int l = 0; l < shipLength; l++) {
			this.probability[xy[1] + (direction ? l : 0)][xy[0] + (direction ? 0 : l)] = this.HIT;

			// At the terminal positions of the ship.
			if ((l == 0) && (xy[direction ? 1 : 0] != 0)) { // Checks if the 1st tile isn't at the edge of the Board
				this.probability[xy[1] - (direction ? 1 : 0)][xy[0] - (direction ? 0 : 1)] = this.MISS;
			} else if ((l == shipLength - 1) && (xy[direction ? 1 : 0] + shipLength - 1 != this.gridSize - 1)) { // Checks if the last tile isn't at the edge of the Board
				this.probability[xy[1] + (direction ? shipLength : 0)][xy[0] + (direction ? 0 : shipLength)] = this.MISS;
			}
			// Along the length of the ship
			if (xy[direction ? 0 : 1] == 0) { // At the Top/Left edge
				this.probability[xy[1] + (direction ? l : 1)][xy[0] + (direction ? 1 : l)] = this.MISS;
			} else if (xy[direction ? 0 : 1] == this.gridSize - 1) { // At the Bottom/Right edge
				this.probability[xy[1] + (direction ? l : -1)][xy[0] + (direction ? -1 : l)] = this.MISS;
			} else { // Not at the edges
				this.probability[xy[1] + (direction ? l : -1)][xy[0] + (direction ? -1 : l)] = this.MISS;
				this.probability[xy[1] + (direction ? l : 1)][xy[0] + (direction ? 1 : l)] = this.MISS;
			}
		}
	}

	/**
	 * Sets the given <code>this.probability</code> location to <code>this.LIKELY</code>, subject to constraints.
	 *
	 * @param y Y-Coordinate
	 * @param x X-Coordinate
	 */
	private void setProbabilityLikely(int y, int x) {
		if (this.probability[y][x] == this.NO_DATA) {
			this.probability[y][x] = this.LIKELY;
		}
	}

	/**
	 * Sets the given <code>this.probability</code> location to <code>this.HIT</code>, subject to constraints.
	 *
	 * @param y Y-Coordinate
	 * @param x X-Coordinate
	 */
	private void setProbabilityHit(int y, int x) {
		if (this.probability[y][x] == this.NO_DATA || this.probability[y][x] == this.LIKELY) {
			this.probability[y][x] = this.HIT;
		}
	}

	/**
	 * Sets the given <code>this.probability</code> location to <code>this.MISS</code>, subject to constraints.
	 *
	 * @param y Y-Coordinate
	 * @param x X-Coordinate
	 */
	private void setProbabilityMiss(int y, int x) {
		if (this.probability[y][x] == this.NO_DATA || this.probability[y][x] == this.LIKELY) {
			this.probability[y][x] = this.MISS;
		}
	}

	/**
	 * Decides where to fire next if a suitable target has been located.
	 *
	 * @return an integer array. The 1st value is the X-Coordinate. The 2nd value is the Y-Coordinate
	 */
	private int[] nextShot() {
		int[] xy = this.randomFire();        // Firing Coordinates. Initialized to random values to prevent compiler errors.

		nextLikelyPostion:
		for (int y = 0; y < this.gridSize; y++) {
			for (int x = 0; x < this.gridSize; x++) {
				if (this.probability[y][x] == this.LIKELY) {
					xy = new int[] {x, y};
					break nextLikelyPostion;
				}
			}
		}

		if (this.gridOpp[xy[1]][xy[0]].hasShip()) {      // Checks if a shot hit
			boolean direction = xy[0] == this.prevShot[0]; // If X-Coordinates align, the ship is Vertical (true); else Horiontal (false).
			boolean side = direction ? (xy[1] > this.prevShot[1]) : (xy[0] > this.prevShot[0]); // False indicates a trailing location. True indicates a leading location.
			this.setProbabilityHit(xy[1], xy[0]);

			if ((xy[0] > 0) && (xy[0] < this.gridSize - 1) && (xy[1] > 0) && (xy[1] < this.gridSize - 1)) { // Not at edges
				if (direction) {
					this.setProbabilityMiss(xy[1], xy[0] - 1);
					this.setProbabilityMiss(xy[1], xy[0] + 1);
					if (side) {
						this.setProbabilityLikely(xy[1] + 1, xy[0]);
					} else {
						this.setProbabilityLikely(xy[1] - 1, xy[0]);
					}
				} else {
					this.setProbabilityMiss(xy[1] - 1, xy[0]);
					this.setProbabilityMiss(xy[1] + 1, xy[0]);
					if (side) {
						this.setProbabilityLikely(xy[1], xy[0] + 1);
					} else {
						this.setProbabilityLikely(xy[1], xy[0] - 1);
					}
				}
			} else {                                     // At the Edges
				if (xy[1] == 0) {                          // Top edge
					if (xy[0] == 0) {                        // Top-Left Corner
						if (direction) {
							this.setProbabilityMiss(xy[1], xy[0] + 1);
							this.setProbabilityLikely(xy[1] + 1, xy[0]);
						} else {
							this.setProbabilityMiss(xy[1] + 1, xy[0]);
							this.setProbabilityLikely(xy[1], xy[0] + 1);
						}
					} else if (xy[0] == this.gridSize - 1) { // Top-Right Corner
						if (direction) {
							this.setProbabilityMiss(xy[1], xy[0] - 1);
							this.setProbabilityLikely(xy[1] + 1, xy[0]);
						} else {
							this.setProbabilityMiss(xy[1] + 1, xy[0]);
							this.setProbabilityLikely(xy[1], xy[0] - 1);
						}
					} else {                                 // Top Edge (excluding Corners)
						if (direction) {
							this.setProbabilityMiss(xy[1], xy[0] - 1);
							this.setProbabilityMiss(xy[1], xy[0] + 1);
							if (side) {
								this.setProbabilityLikely(xy[1] + 1, xy[0]);
							}
						} else {
							this.setProbabilityMiss(xy[1] + 1, xy[0]);
							if (side) {
								this.setProbabilityLikely(xy[1], xy[0] - 1);
							} else {
								this.setProbabilityLikely(xy[1], xy[0] + 1);
							}
						}
					}
				} else if (xy[1] == this.gridSize - 1) {   // Bottom Edge
					if (xy[0] == 0) {                        // Bottom-Left Corner
						if (direction) {
							this.setProbabilityMiss(xy[1], xy[0] + 1);
							this.setProbabilityLikely(xy[1] - 1, xy[0]);
						} else {
							this.setProbabilityMiss(xy[1] - 1, xy[0]);
							this.setProbabilityLikely(xy[1], xy[0] + 1);
						}
					} else if (xy[0] == this.gridSize - 1) { // Bottom-Right Corner
						if (direction) {
							this.setProbabilityMiss(xy[1], xy[0] - 1);
							this.setProbabilityLikely(xy[1] - 1, xy[0]);
						} else {
							this.setProbabilityMiss(xy[1] - 1, xy[0]);
							this.setProbabilityLikely(xy[1], xy[0] - 1);
						}
					} else {                                 // Bottom Edge (excluding Corners)
						if (direction) {
							this.setProbabilityMiss(xy[1], xy[0] - 1);
							this.setProbabilityMiss(xy[1], xy[0] + 1);
							if (!side) {
								this.setProbabilityLikely(xy[1] - 1, xy[0]);
							}
						} else {
							this.setProbabilityMiss(xy[1] - 1, xy[0]);
							if (side) {
								this.setProbabilityLikely(xy[1], xy[0] + 1);
							} else {
								this.setProbabilityLikely(xy[1], xy[0] - 1);
							}
						}
					}
				} else if (xy[0] == 0) {                   // Left Edge (excluding Corners)
					if (direction) {
						this.setProbabilityMiss(xy[1], xy[0] + 1);
						if (side) {
							this.setProbabilityLikely(xy[1] + 1, xy[0]);
						} else {
							this.setProbabilityLikely(xy[1] - 1, xy[0]);
						}
					} else {
						this.setProbabilityMiss(xy[1] - 1, xy[0]);
						this.setProbabilityMiss(xy[1] + 1, xy[0]);
						if (side) {
							this.setProbabilityLikely(xy[1], xy[0] + 1);
						}
					}
				} else {                                   // Rght Edge (ecluding Corners)
					if (direction) {
						this.setProbabilityMiss(xy[1], xy[0] - 1);
						if (side) {
							this.setProbabilityLikely(xy[1] + 1, xy[0]);
						} else {
							this.setProbabilityLikely(xy[1] - 1, xy[0]);
						}
					} else {
						this.setProbabilityMiss(xy[1] - 1, xy[0]);
						this.setProbabilityMiss(xy[1] + 1, xy[0]);
						if (!side) {
							this.setProbabilityLikely(xy[1], xy[0] - 1);
						}
					}
				}
			}
		} else {                                    // Miss
			this.setProbabilityMiss(xy[1], xy[0]);
		}

		return xy;
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

}
