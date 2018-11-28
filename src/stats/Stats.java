package stats;

import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import misc.DBDetails;


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
	 * </pre>
	 *
	 * Mode:<br>
	 * &emsp; 0 - Classic<br>
	 * &emsp; 1 - Salvo<br>
	 * <br>
	 * AIDiff:<br>
	 * &emsp; 0 - Sandbox<br>
	 * &emsp; 1 - Regular<br>
	 * &emsp; 2 - Brutal
	 */
	private int[][] statsLists = new int[6][10];

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
	 * </pre>
	 *
	 * Mode:<br>
	 * &emsp; 0 - Classic<br>
	 * &emsp; 1 - Salvo<br>
	 * <br>
	 * AIDiff:<br>
	 * &emsp; 0 - Sandbox<br>
	 * &emsp; 1 - Regular<br>
	 * &emsp; 2 - Brutal
	 */
	private float[] Acc = new float[6];

	/**
	 * Constructor for the Stats class.
	 *
	 * @param UName
	 */
	public Stats(String UName) {
		getStats(UName);
	}

	/**
	 * @return the statsLst
	 */
	public final int[][] getStatsLists() {
		return statsLists;
	}

	/**
	 * @return the Acc
	 */
	public final float[] getAcc() {
		return Acc;
	}

	/**
	 * Gets the Current User's statistics from the MySQL database.
	 *
	 * @param UName - The Username
	 *
	 * @return ret - An Integer indicating the result of statistics retrieval.<br>
	 * &emsp; {-1, -2, -3} imply an error.<br>
	 * &emsp; {0} implies a successful retrieval.
	 */
	public int getStats(String UName) {
		int ret = 0; // Return code

		try {
			Class.forName("com.mysql.jdbc.Driver");                                                                                // Creates the MySQL JDBC Driver class
			Connection con = DriverManager.getConnection(DBDetails.DB_URL + DBDetails.DB_NAME, DBDetails.DB_UNAME, DBDetails.DB_PASS); // Creates a connection to the MySQL Database

			StringBuffer query = new StringBuffer(137);                                             // StringBuffer for holding the updates
			query.append("SELECT * FROM stats WHERE UNo = (SELECT UNo FROM users WHERE UName = '"); // Base Query with Base Subquery
			query.append(UName).append("');");                                                      // Username for Subquery

			Statement stmnt = con.createStatement();             // Creates the SQL statement object
			ResultSet rs = stmnt.executeQuery(query.toString()); // Runs the querys

			while (rs.next()) {
				String mode = rs.getString(2), diff = rs.getString(3);
				int i = mode.equals("S") ? 3 : 0;
				i += diff.equals("B") ? 2 : (diff.equals("R") ? 1 : 0);

				this.statsLists[i][0] = rs.getInt(4);  // GP value
				this.statsLists[i][1] = rs.getInt(5);  // GW value
				this.statsLists[i][2] = rs.getInt(6);  // GL value
				this.statsLists[i][3] = rs.getInt(7);  // SF value
				this.statsLists[i][4] = rs.getInt(8);  // Hits value
				this.Acc[i] = rs.getFloat(9);          // Acc value
				this.statsLists[i][5] = rs.getInt(10); // TH value
				this.statsLists[i][6] = rs.getInt(11); // SS value
				this.statsLists[i][7] = rs.getInt(12); // SL value
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
	 * <pre>
	 * public static void arraycopy(Object src, int srcPos, Object dest, int destPos, int length)
	 *
	 * src     − This is the source array.
	 * srcPos  − This is the starting position in the source array.
	 * dest    − This is the destination array.
	 * destPos − This is the starting position in the destination data.
	 * length  − This is the number of array elements to be copied.
	 * </pre>
	 *
	 * @param statsList the statsLists to set
	 */
	final void setStatsLists(int index, int[] statsList) {
		System.arraycopy(statsList, 1, this.statsLists[index], 3, statsList.length - 1);

		this.statsLists[index][0] += 1;                         // Games PLayed += 1
		this.statsLists[index][1] += statsList[0] == 1 ? 1 : 0; // Games Won += 1 if Round Status is true
		this.statsLists[index][2] += statsList[0] == 0 ? 1 : 0; // Games Lost += 1 if Round Status is false
	}

	/**
	 * @param Acc the Acc to set
	 */
	final void setAcc(int index, float Acc) {
		this.Acc[index] = Acc;
	}
}
