package users;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import misc.DBDetails;


/**
 * Class for creating a logging in a user.
 *
 * @author blackk100
 */
public final class LoginUser extends User {

	/**
	 * Constructor for the LoginUser class
	 *
	 * @param UName  Username
	 * @param psswrd Password
	 */
	public LoginUser(String UName, char[] psswrd) {
		super(UName, psswrd);
	}

	/**
	 * Compares the provided user data to that present in the MySQL database
	 *
	 * @return ret - an Integer indicating the result of logging in.<br>
	 * &emsp; {-1, -2, -3} imply an error.<br>
	 * &emsp; {1} value implies successful login.<br>
	 * &emsp; {0} implies unsuccessful login
	 */
	public int login() {
		boolean match = false; // A boolean value indicating whether or not the username has matched
		int ret = 0;           // Return code.

		try {
			Class.forName("com.mysql.jdbc.Driver");                                                                                    // Creates the MySQL JDBC Driver class
			Connection con = DriverManager.getConnection(DBDetails.DB_URL + DBDetails.DB_NAME, DBDetails.DB_UNAME, DBDetails.DB_PASS); // Creates a connection to the MySQL Database
			Statement stmnt = con.createStatement();                                                                                   // Creates the SQL statement object
			ResultSet rs = stmnt.executeQuery("SELECT UName, PassWrd FROM users;");                                                    // Runs the query

			// Verification of Username and Password
			System.out.println("verification started!");

			String curUser = this.getUName(); // Current User's username
			long curHash = this.getHash();    // Current User's password hash
			while (!match && rs.next()) {
				// Loops over the result set to see whether the entered username matches any of the registered users
				System.out.println("match (within loop): ".concat(match ? "true" : "false"));
				/**
				 * Whether or not the given details match.
				 *
				 * Equivalent to the following code:
				 * <code>
				 *	if (rs.getString(1).equals(curUser) && (rs.hetLong(2) == curHash)) {
				 *		match = true;
				 *	} else {
				 *		match = false;
				 *	}
				 * </code>
				 */
				match = rs.getString(1).equals(curUser) && (rs.getLong(2) == curHash);
			}
			System.out.println("match (final value): ".concat(match ? "true" : "false"));
			System.out.println("verification finished!");

			rs.close();
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

		System.out.println("return code check started!");
		if (ret >= 0) { // Return value isn't an error code
			System.out.println("return code wasn't an error!");

			if (match) {  // Logged in successfuly. Changing current user.
				System.out.println("return code corresponds to a match!");
				CurrentUser.setCurrentUser(this.getUName());
				ret = 1;
			} else {      // Login unsuccessful.
				System.out.println("return code corresponds to a non-match!");
				ret = 0;
			}
		} else {        // Return value is an error code
			System.out.println("return code was an error!");
			System.out.println("return code: ".concat(Integer.toString(ret)));
		}

		return ret;
	}

}
