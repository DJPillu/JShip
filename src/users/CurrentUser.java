package users;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.Arrays;
import misc.DBDetails;


/**
 * Class for maintaining the current user
 *
 * @author blackk100
 */
public final class CurrentUser {

	/**
	 * Current user's username
	 */
	private static String UName = "guest";

	/**
	 * Current user's username
	 */
	private static long Hash = 0;

	/**
	 * @return The current User's Username
	 */
	public static String getCurrentUser() {
		return CurrentUser.UName;
	}

	/**
	 * Updates the Current User
	 *
	 * @param UName Username
	 * @param Hash
	 */
	public static void setCurrentUser(String UName, long Hash) {
		CurrentUser.UName = UName;
		CurrentUser.Hash = Hash;
	}

	/**
	 * @return The current User's password's hash value
	 */
	public static long getCurrentHash() {
		return CurrentUser.Hash;
	}

	/**
	 * Used to check the entered password. Identical to <code>User.hash()</code>.
	 *
	 * @param psswrd Original Password as a character array
	 *
	 * @return The hashed value of the password.
	 */
	public static long checkHash(char[] psswrd) {
		long hash = 0;                            // Hash value.
		for (int i = 0; i < psswrd.length; i++) { // Iterate over the entire password array
			if ((i + 1) % 2 == 0) {                 // If even, add hash value
				hash += Character.hashCode(psswrd[i]);
			} else {                                // If odd, subtract hash value
				hash -= Character.hashCode(psswrd[i]);
			}

			Arrays.fill(psswrd, '0');               // Security measure
		}

		hash = ~hash;                 // Binary Flip of the Hash Value

		if (hash > 8388607) {         // Max positive limit of MySQL MEDIUMINT is +8388607
			hash = 8388607;
		} else if (hash < -8388608) { // Max negative limit of MySQL MEDIUMINT is -8388608
			hash = -8388607;
		}

		return hash;
	}

	/**
	 * Used to check the entered password. Identical to <code>User.hash()</code>.
	 *
	 * @param psswrd Original Password as a character array
	 *
	 * @return A boolean value indicating whether the given password is correct.
	 */
	public static int updatePassword(char[] psswrd) {
		long hash = CurrentUser.checkHash(psswrd);
		int ret = 0; // Return code

		try {
			Class.forName("com.mysql.jdbc.Driver");                                                                                    // Creates the MySQL JDBC Driver class
			Connection con = DriverManager.getConnection(DBDetails.DB_URL + DBDetails.DB_NAME, DBDetails.DB_UNAME, DBDetails.DB_PASS); // Creates a connection to the MySQL Database

			StringBuffer update = new StringBuffer(121);

			update.append("UPDATE users SET ");     // Base Statement
			update.append("Passwrd=").append(hash); // Password Hash Value with Conditional statement.
			update.append(" WHERE UName='").append(CurrentUser.getCurrentUser()).append("';"); // Conditional Statement for Username

			Statement stmnt = con.createStatement(); // Creates the SQL statement object
			System.out.println(update.toString());
			stmnt.executeUpdate(update.toString());

			stmnt.close();
			con.close();

			CurrentUser.setCurrentUser(CurrentUser.getCurrentUser(), hash);
		} catch (SQLIntegrityConstraintViolationException e) { // User already registered;   return -4
			// Is above the rest since it is a subclass of SQLException.
			System.out.println("User Already Registered Exception:\n" + e);
			ret = -4;
		} catch (ClassNotFoundException e) {                     // Class Not Found Exception; return -1
			System.out.println("Class Not Found Exception:\n" + e);
			ret = -1;
		} catch (SQLException e) {                               // SQL Exception;             return -2
			System.out.println("SQL Exception:\n" + e);
			ret = -2;
		} catch (Exception e) {                                  // Unknown Exception;         return -3
			System.out.println("Unknown Exception occured:\n" + e);
			ret = -3;
		}

		return ret;
	}

	/**
	 * Logs the user out.
	 */
	public static void logout() {
		CurrentUser.setCurrentUser("guest", 0);
	}
}
