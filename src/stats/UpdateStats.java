package stats;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


/**
 * Class for updating User Statistics
 *
 * @author blackk100
 */
public final class UpdateStats extends Stats {
	private final String mode, diff, UName;
	private final int mdI; // See Stats.statsLists and Stats.Acc 1-Dimensional Index Documentation

	/**
	 * Constructor for the UpdateStats class. Calls the superclass's constructor.
	 *
	 * @param UName
	 * @param mode
	 * @param diff
	 */
	public UpdateStats(String UName, String mode, String diff) {
		super(UName);
		this.UName = UName;
		this.mode = mode;
		this.diff = diff;
		this.mdI = (mode.equals("S") ? 3 : 0) + (diff.equals("B") ? 2 : (diff.equals("R") ? 1 : 0));
	}

	/**
	 * Sets the Current User's statistics to the MySQL database.
	 *
	 * @return ret - An Integer indicating the result of statistics retrieval.<br>
	 * &emsp; {-1, -2, -3} imply an error.<br>
	 * &emsp; {0} implies successful update.
	 */
	public int setStats() {
		int ret = 0; // Return code

		try {
			String dbNames = "jship?useSSL=false"; // Database Name
			String dbUName = "root";               // DBMS Username
			String dbPsswd = "root";               // DBMS Password

			Class.forName("com.mysql.jdbc.Driver");                                    // Creates the MySQL JDBC Driver class
			Connection con = DriverManager.getConnection(
							"jdbc:mysql://localhost:3306/".concat(dbNames), dbUName, dbPsswd); // Creates a connection to the MySQL Database

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
}
