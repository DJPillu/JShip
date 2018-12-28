package stats;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import misc.DBDetails;
import users.CurrentUser;


/**
 * Class for updating User Statistics
 *
 * @author blackk100
 */
public final class UpdateStats extends Stats {

	/**
	 * Game Mode:
	 *
	 * <pre>
	 *      0 - Classic
	 *      1 - Salvo
	 * </pre>
	 */
	private final String mode;

	/**
	 * AI Difficulty:
	 *
	 * <pre>
	 *      0 - Sandbox
	 *      1 - Regular
	 *      2 - Brutal
	 * </pre>
	 */
	private final int AIDiff;

	/**
	 * Statistics list index
	 *
	 * <pre>
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
	private final int index;

	/**
	 * Round Statistics:
	 *
	 * <pre>
	 * I-----------I-----------------------I
	 * I Index No. I    Value Stored       I
	 * I-----------I-----------------------I
	 * I     0     I Round Status (status) I -- 1: Win ; 0: Lose
	 * I     1     I Shots Fired  (SF)     I
	 * I     2     I Hits landed  (Hits)   I
	 * I     3     I Times Hit    (TH)     I
	 * I     4     I Ships Sunk   (SS)     I
	 * I     5     I Ships Lost   (SL)     I
	 * I-----------I-----------------------I
	 * </pre>
	 */
	private int[] statsList;

	/**
	 * Constructor for the UpdateStats class. Calls the superclass's constructor.
	 *
	 * @param mode      Game Mode
	 * @param AIDiff    AI Difficulty
	 * @param statsList Statistics List
	 */
	public UpdateStats(String mode, int AIDiff, int[] statsList) {
		super();

		this.mode = mode;
		this.AIDiff = AIDiff;
		this.index = mode.equals("S") ? 3 : 0 + AIDiff;
		this.statsList = statsList;

		this.setStatsLists(this.index, this.statsList);
	}

	/**
	 * Updates the Current User's statistics in the MySQL database.
	 *
	 * @return ret - An Integer indicating the result of statistics retrieval.
	 *         {-1, -2, -3} imply an error.
	 *         {0} implies successful update.
	 */
	public int update() {
		int ret = 0; // Return code

		try {
			Class.forName("com.mysql.jdbc.Driver");                                                                                // Creates the MySQL JDBC Driver class
			Connection con = DriverManager.getConnection(DBDetails.DB_URL + DBDetails.DB_NAME, DBDetails.DB_UNAME, DBDetails.DB_PASS); // Creates a connection to the MySQL Database

			int[] statsList = this.getStatsLists()[this.index];
			float acc = this.getAcc()[this.index];

			StringBuffer update = new StringBuffer(258);
			update.append("UPDATE stats SET GP=").append(statsList[0]);           // Base Statement with Games Played
			update.append(", GW=").append(statsList[1]);                          // Games Won
			update.append(", GL=").append(statsList[2]);                          // Games Lost
			update.append(", SF=").append(statsList[3]);                          // Shots Fired
			update.append(", Hits=").append(statsList[4]);                        // Hits
			update.append(", Acc=").append(acc);                                  // Accuracy
			update.append(", TH=").append(statsList[5]);                          // Times Hit
			update.append(", SS=").append(statsList[6]);                          // Ships Sunk
			update.append(", SL=").append(statsList[7]);                          // Ships Lost
			update.append(" WHERE UNo = (SELECT UNo FROM users WHERE UName = '"); // 1st Conditional statement using Subquery
			update.append(CurrentUser.getCurrentUser()).append("') AND Mode = '").append(this.mode); // Username for Subquery and Complete 2nd Conditional statement
			update.append("' AND AIDiff = '").append(this.AIDiff == 2 ? "B" : (this.AIDiff == 1 ? "R" : "S")).append("';"); // Complete 3rd Conditional statement

			Statement stmnt = con.createStatement(); // Creates the SQL statement object
			stmnt.executeUpdate(update.toString());  // Runs the update statement

			stmnt.close();
			con.close();
		} catch (ClassNotFoundException e) { // Class Not Found Exception; return -1
			System.out.println("Class Not Found Exception:\n" + e);
			ret = -1;
		} catch (SQLException e) {           // SQL Exception;             return -2
			System.out.println("SQL Exception:\n" + e);
			ret = -2;
		} catch (Exception e) {              // Unknown Exception;         return -3
			System.out.println("Unknown Exception occured:\n" + e);
			ret = -3;
		}

		this.getStats();
		return ret;
	}

	/**
	 * Resets the statistics of the given category to all zeroes.
	 *
	 * @return ret - An Integer indicating the result of statistics retrieval.
	 *         {-1, -2, -3} imply an error.
	 *         {0} implies successful update.
	 */
	public int reset() {
		this.resetStatsLists(this.index);
		return this.update();
	}

}
