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

		// If xy refers to a guessed coordinate, or a location that borders a ship tile that has been guessed, regenerate it.
		do { // Do initializes xy.
			for (int i = 0; i < 2; i++) {
				temp[i] = rand.nextInt(this.gridSize);
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
