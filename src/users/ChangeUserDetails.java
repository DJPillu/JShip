package users;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import misc.DBDetails;


/**
 * Class that handles changing of the current user's details.
 *
 * @author blackk100
 */
public final class ChangeUserDetails extends CurrentUser {

	/**
	 * Current user's username
	 */
	private static long Hash = 0;

	/**
	 * @return The current User's password's hash value
	 */
	public static long getCurrentHash() {
		return ChangeUserDetails.Hash;
	}

	/**
	 * Sets the current User's password's hash value.
	 *
	 * @param Hash the hash value to set.
	 */
	public static void setCurrentHash(long Hash) {
		ChangeUserDetails.Hash = Hash;
	}

	/**
	 * Used to check the entered password. Identical to <code>User.hash()</code>.
	 *
	 * @param psswrd Original Password as a character array
	 *
	 * @return The hashed value of the password.
	 */
	public long checkHash(char[] psswrd) {
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
	 * @return A boolean value indicating whether the password was changed successfully.
	 */
	public int updatePassword(char[] psswrd) {
		long hash = this.checkHash(psswrd);
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

			CurrentUser.setCurrentUser(CurrentUser.getCurrentUser());
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
	 * Used to delete the current user's details from the database.
	 *
	 * @return A boolean value indicating whether the deletion was carried out successfully.
	 */
	public int deleteUser() {
		int ret = 0; // Return code

		try {
			Class.forName("com.mysql.jdbc.Driver");                                                                                    // Creates the MySQL JDBC Driver class
			Connection con = DriverManager.getConnection(DBDetails.DB_URL + DBDetails.DB_NAME, DBDetails.DB_UNAME, DBDetails.DB_PASS); // Creates a connection to the MySQL Database

			StringBuffer[] deletes = {new StringBuffer(135), new StringBuffer(135)};
			deletes[0].append("DELETE FROM users WHERE UNo = (SELECT UNo FROM users WHERE UName = '"); // Base Statement for users
			deletes[0].append(CurrentUser.getCurrentUser()).append("');");                             // Name
			deletes[1].append("DELETE FROM stats WHERE UNo = (SELECT UNo FROM users WHERE UName = '"); // Base Statement for stats
			deletes[1].append(CurrentUser.getCurrentUser()).append("');");                             // Name

			Statement stmnt = con.createStatement();    // Creates the SQL statement object
			System.out.println(deletes[0].toString());  // 1st Statement (users)
			stmnt.executeUpdate(deletes[0].toString());
			System.out.println(deletes[0].toString());  // 2nd Statement (stats)
			stmnt.executeUpdate(deletes[0].toString());

			stmnt.close();
			con.close();

			CurrentUser.logout();
		} catch (ClassNotFoundException e) {                   // Class Not Found Exception; return -1
			System.out.println("Class Not Found Exception:\n" + e);
			ret = -1;
		} catch (SQLException e) {                             // SQL Exception;             return -2
			System.out.println("SQL Exception:\n" + e);
			ret = -2;
		} catch (Exception e) {                                // Unknown Exception;         return -3
			System.out.println("Unknown Exception occured:\n" + e);
			ret = -3;
		}

		return ret;
	}

}
