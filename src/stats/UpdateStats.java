package stats;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import misc.DBDetails;


/**
 * Class for updating User Statistics
 *
 * @author blackk100
 */
public final class UpdateStats extends Stats {
	/**
	 * Game mode
	 */
	private final String mode;

	/**
	 * Current Player's Username
	 */
	private final String UName;

	/**
	 * Mode:<br>
	 * &emsp; 0 - Classic<br>
	 * &emsp; 1 - Salvo<br>
	 */
	private final int mdI;

	/**
	 * AIDiff:<br>
	 * &emsp; 0 - Sandbox<br>
	 * &emsp; 1 - Regular<br>
	 * &emsp; 2 - Brutal
	 */
	private final int diff;

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
	private final int[] statsList;

	/**
	 * Accuracy = (Hits Landed / Shots Fired) * 100 = (statsList[2] / statsList[1]) * 100
	 */
	private final float Acc;

	/**
	 * Constructor for the UpdateStats class. Calls the superclass's constructor.
	 *
	 * @param UName     Username
	 * @param mode      Game Mode
	 * @param AIDiff    AI Difficulty
	 * @param statsList Statistics List
	 * @param Acc       Accuracy
	 */
	public UpdateStats(String UName, String mode, int AIDiff, int[] statsList, float Acc) {
		super(UName);

		this.UName = UName;
		this.mode = mode;
		this.diff = AIDiff;
		this.mdI = (mode.equals("S") ? 3 : 0) + (AIDiff == 1 ? 2 : (AIDiff == 0 ? 1 : 0));
		this.statsList = statsList;
		this.Acc = Acc;

		this.setLocalStats();
	}

	/**
	 * Sets the Current User's statistics to the MySQL database.
	 *
	 * @return ret - An Integer indicating the result of statistics retrieval.<br>
	 * &emsp; {-1, -2, -3} imply an error.<br>
	 * &emsp; {0} implies successful update.
	 */
	public int update() {
		int ret = 0; // Return code

		try {
			Class.forName("com.mysql.jdbc.Driver");                                                                                // Creates the MySQL JDBC Driver class
			Connection con = DriverManager.getConnection(DBDetails.DB_URL + DBDetails.DB_NAME, DBDetails.DB_UNAME, DBDetails.DB_PASS); // Creates a connection to the MySQL Database

			StringBuffer update = new StringBuffer(250);
			update.append("UPDATE stats SET GP=").append(this.getStatsLists()[mdI][0]); // Base Statement with GP value
			update.append(", GW=").append(this.getStatsLists()[mdI][1]);                // GW value
			update.append(", GL=").append(this.getStatsLists()[mdI][2]);                // GL value
			update.append(", SF=").append(this.getStatsLists()[mdI][3]);                // SF value
			update.append(", Hits=").append(this.getStatsLists()[mdI][4]);              // Hits value
			update.append(", Acc=").append(this.getAcc()[mdI]);                         // Acc value
			update.append(", TH=").append(this.getStatsLists()[mdI][5]);                // TH value
			update.append(", SS=").append(this.getStatsLists()[mdI][6]);                // SS value
			update.append(", SL=").append(this.getStatsLists()[mdI][7]);                // SL value
			update.append(" WHERE UNo=(SELECT UNo FROM users WHERE UName='");           // 1st Conditional statement using Subquery
			update.append(this.UName).append("') AND Mode='").append(this.mode);        // Username for Subquery and Complete 2nd Conditional statement
			update.append("' AND AIDiff='").append(this.diff).append("';");             // Complete 3rd Conditional statement

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

		this.getStats(this.UName);
		return ret;
	}

	/**
	 * Updates Stats.statsList with this.statsList
	 */
	private void setLocalStats() {
		this.setStatsLists(this.mdI, this.statsList);
		this.setAcc(this.mdI, this.Acc);
	}

}
