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
	private final String mode, UName;
	private final int mdI, diff;   // See Stats.statsLists and Stats.Acc 1-Dimensional Index Documentation
	/**
	 * Round Statistics:
	 *
	 * <pre>
	 * I-----------I-----------------------I
	 * I Index No. I    Value Stored       I
	 * I-----------I-----------------------I
	 * I     0     I Round Status (status) I -> 1: Win ; 0: Lose
	 * I     1     I Shots Fired  (SF)     I
	 * I     2     I Hits landed  (Hits)   I
	 * I     3     I Times Hit    (TH)     I
	 * I     4     I Ships Sunk   (SS)     I
	 * I     5     I Ships Lost   (SL)     I
	 * I-----------I-----------------------I
	 * </pre>
	 */
	private final int[] statsList;
	private final float Acc;

	/**
	 * Constructor for the UpdateStats class. Calls the superclass's constructor.
	 *
	 * @param UName
	 * @param mode
	 * @param diff
	 * @param statsList
	 * @param Acc
	 */
	public UpdateStats(String UName, String mode, int diff, int[] statsList, float Acc) {
		super(UName);

		this.UName = UName;
		this.mode = mode;
		this.diff = diff;
		this.mdI = (mode.equals("S") ? 3 : 0) + (diff == 1 ? 2 : (diff == 0 ? 1 : 0));
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
			Connection con = DriverManager.getConnection(DBDetails.DBURL + DBDetails.DBName, DBDetails.DBUName, DBDetails.DBPass); // Creates a connection to the MySQL Database

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
			stmnt.executeQuery(update.toString());   // Runs the update

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
