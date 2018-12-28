package stats;

import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import misc.DBDetails;
import users.CurrentUser;


/**
 * Class for dealing with User Statistics.
 *
 * @author blackk100
 */
public class Stats {

	/**
	 * <pre>
	 * statsList 1st Dimensional Array:
	 * I-----------I----------------------I
	 * I Index No. I     Stats Stored     I
	 * I-----------I----------------------I
	 * I     0     I Classic - Sandbox    I
	 * I     1     I Classic - Regular    I
	 * I     2     I Classic - Brutal     I
	 * I     3     I Salvo   - Sandbox    I
	 * I     4     I Salvo   - Regular    I
	 * I     5     I Salvo   - Brutal     I
	 * I-----------I----------------------I
	 *
	 * statsList 2nd Dimensional Arrays:
	 * I-----------I----------------------I
	 * I Index No. I     Value Stored     I
	 * I-----------I----------------------I
	 * I     0     I Games Played  (GP)   I
	 * I     1     I Games Won     (GW)   I
	 * I     2     I Games Lost    (GL)   I
	 * I     3     I Shots Fired   (SF)   I
	 * I     4     I Hits landed   (Hits) I
	 * I     5     I Times Hit     (TH)   I
	 * I     6     I Ships Sunk    (SS)   I
	 * I     7     I Ships Lost    (SL)   I
	 * I-----------I----------------------I
	 *
	 * Index value = 0 + x, where x +=
	 *     Mode:
	 *          0 - Classic
	 *          3 - Salvo
	 *     AIDiff:
	 *          0 - Sandbox
	 *          1 - Regular
	 *          2 - Brutal
	 * </pre>
	 */
	private int[][] statsLists = new int[6][8];

	/**
	 * <pre>
	 * Acc 1st Dimensional Array:
	 * I-----------I----------------------I
	 * I Index No. I     Stats Stored     I
	 * I-----------I----------------------I
	 * I     0     I Classic - Sandbox    I
	 * I     1     I Classic - Regular    I
	 * I     2     I Classic - Brutal     I
	 * I     3     I Salvo   - Sandbox    I
	 * I     4     I Salvo   - Regular    I
	 * I     5     I Salvo   - Brutal     I
	 * I-----------I----------------------I
	 *
	 * Mode:
	 *      0 - Classic
	 *      3 - Salvo
	 *
	 * AIDiff:
	 *      0 - Sandbox<br>
	 *      1 - Regular<br>
	 *      2 - Brutal
	 * </pre>
	 */
	private float[] acc = new float[6];

	/**
	 * Constructor for the Stats class.
	 */
	public Stats() {
		this.getStats();
	}

	/**
	 * @return the statsLst
	 */
	public final int[][] getStatsLists() {
		return statsLists;
	}

	/**
	 * @return the acc
	 */
	public final float[] getAcc() {
		return acc;
	}

	/**
	 * Gets the Current User's statistics from the MySQL database.
	 *
	 * @return ret - An Integer indicating the result of statistics retrieval.<br>
	 * &emsp; {-1, -2, -3} imply an error.<br>
	 * &emsp; {0} implies a successful retrieval.
	 */
	final int getStats() {
		int ret = 0; // Return code

		try {
			Class.forName("com.mysql.jdbc.Driver");                                                                                    // Creates the MySQL JDBC Driver class
			Connection con = DriverManager.getConnection(DBDetails.DB_URL + DBDetails.DB_NAME, DBDetails.DB_UNAME, DBDetails.DB_PASS); // Creates a connection to the MySQL Database

			StringBuffer query = new StringBuffer(137);                                             // StringBuffer for holding the updates
			query.append("SELECT * FROM stats WHERE UNo = (SELECT UNo FROM users WHERE UName = '"); // Base Query with Base Subquery
			query.append(CurrentUser.getCurrentUser()).append("');");                                                      // Username for Subquery

			Statement stmnt = con.createStatement();             // Creates the SQL statement object
			ResultSet rs = stmnt.executeQuery(query.toString()); // Runs the querys

			while (rs.next()) {
				String mode = rs.getString(2), diff = rs.getString(3);
				int index = (mode.equals("S") ? 3 : 0) + (diff.equals("B") ? 2 : (diff.equals("R") ? 1 : 0));

				this.statsLists[index][0] = rs.getInt(4);  // Games Played
				this.statsLists[index][1] = rs.getInt(5);  // Games Won
				this.statsLists[index][2] = rs.getInt(6);  // Games Lost
				this.statsLists[index][3] = rs.getInt(7);  // Shots Fired
				this.statsLists[index][4] = rs.getInt(8);  // Hits
				this.acc[index] = rs.getFloat(9);          // Accuracy
				this.statsLists[index][5] = rs.getInt(10); // Times Hit
				this.statsLists[index][6] = rs.getInt(11); // Ships Sunk
				this.statsLists[index][7] = rs.getInt(12); // Ships Lost
			}

			rs.close();
			stmnt.close();
			con.close();
		} catch (ClassNotFoundException e) { // Class Not Found Exception; return -1
			System.out.println("Class Not Found Exception:\n" + e);
			ret = -1;
		} catch (SQLException e) {           // SQL Exception;             return -2
			System.out.println("SQL Exception:\n" + e);
			e.printStackTrace(new PrintStream(System.out));
			ret = -2;
		} catch (Exception e) {              // Unknown Exception;         return -3
			System.out.println("Unknown Exception occured:\n" + e);
			ret = -3;
		}

		return ret;
	}

	/**
	 * Setter for <code>this.statsLists</code>.
	 *
	 * @param index     Index number of the statistics record
	 * @param statsList the statsLists to set
	 */
	final void setStatsLists(int index, int[] statsList) {
		this.statsLists[index][0] += 1;                                                          // Games PLayed += 1
		if (statsList[0] == 1) {                                                                 // Checks if the match was won
			this.statsLists[index][1] += 1;
		} else {                                                                                 // The match was lost
			this.statsLists[index][2] += 1;
		}
		this.acc[index] = (float) (Math.round((statsList[2] * 10000.0) / statsList[1]) / 100.0); // Accuracy
		for (int stat = 1; stat < statsList.length; stat++) {                                    // All other stats
			this.statsLists[index][stat + 2] = statsList[stat];
		}
	}

	/**
	 * Setter for <code>this.statsLists</code> to "blanks".
	 * Used only for resetting statistics.
	 *
	 * @param index Index number of the statistics record
	 */
	final void resetStatsLists(int index) {
		for (int stat = 0; stat < this.statsLists[index].length; stat++) {
			this.statsLists[index][stat] = 0;
		}

		this.acc[index] = 0;
	}

}
