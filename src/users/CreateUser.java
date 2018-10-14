package users;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import misc.DBDetails;


/**
 * Class for creating a new user.
 *
 * @author blackk100
 */
public final class CreateUser extends User {

	/**
	 * Constructor for the CreateUser class. Calls the superclass's constructor.
	 *
	 * @param name   Username
	 * @param psswrd Original password as a character array
	 */
	public CreateUser(String name, char[] psswrd) {
		super(name, psswrd);
	}

	/**
	 * Creates the user in the MySQL database.
	 *
	 * @return ret - An integer that refers to a specific return code.<br>
	 * &emsp; {-1, -2, -3} imply an error.<br>
	 * &emsp; {0} implies successful creation.
	 */
	public int create() {
		int ret = 0; // Return code

		try {
			Class.forName("com.mysql.jdbc.Driver");                                                                                // Creates the MySQL JDBC Driver class
			Connection con = DriverManager.getConnection(DBDetails.DBURL + DBDetails.DBName, DBDetails.DBUName, DBDetails.DBPass); // Creates a connection to the MySQL Database

			/**
			 * StringBuffers for holding the insert statements
			 * <pre>
			 * I-----------I--------------------I
			 * I Index No. I  Statement Stored  I
			 * I-----------I--------------------I
			 * I     0     I        User        I
			 * I     1     I     Stats  C-S     I
			 * I     2     I     Stats  C-R     I
			 * I     3     I     Stats  C-B     I
			 * I     4     I     Stats  S-S     I
			 * I     5     I     Stats  S-R     I
			 * I     6     I     Stats  S-B     I
			 * I-----------I--------------------I
			 * </pre>
			 */
			StringBuffer[] inserts = {
				new StringBuffer(121),
				new StringBuffer(162), new StringBuffer(162), new StringBuffer(162),
				new StringBuffer(162), new StringBuffer(162), new StringBuffer(162)
			};

			inserts[0].append("INSERT INTO users (UName, PassWrd) VALUES ('"); // Base Statement
			inserts[0].append(this.getUName()).append("', ");                  // Name
			inserts[0].append(this.getHash()).append(");");                    // Password Hash Value

			for (int i = 1; i < inserts.length; i++) {
				inserts[i].append("INSERT INTO stats (UNo, Mode, AIDiff) VALUES ((SELECT UNo FROM users WHERE UName = '"); // Base Statement
				inserts[i].append(this.getUName()).append("'), '");                                                        // Name

				if (i < 4) {                 // 1st 3 StringBuffers are for Classic Mode
					inserts[i].append("C', '");
				} else {                     // Last 3 StringBuffers are for Salvo Mode
					inserts[i].append("S', '");
				}

				if (i - 1 % 3 == 0) {        // 1st StringBuffer of either Mode is for Sandbox Difficulty
					inserts[i].append("S');");
				} else if (i + 1 % 3 == 0) { // 2nd StringBuffer of either Mode is for Regular Difficulty
					inserts[i].append("R');");
				} else if (i % 3 == 0) {     // 3rd StringBuffer of either Mode is for Brutal Difficulty
					inserts[i].append("B');");
				}
			}

			Statement stmnt = con.createStatement(); // Creates the SQL statement object
			/**
			 * Runs the insert statements
			 *
			 * Equivalent to the following code:
			 * <code>
			 *	for (int i = 0; i < inserts.length; i++) {
			 *		stmnt.executeUpdate(inserts[i].toString());
			 *	}
			 * </code>
			 */
			for (StringBuffer i : inserts) {
				stmnt.executeUpdate(i.toString());
			}

			stmnt.close();
			con.close();
		} catch (MySQLIntegrityConstraintViolationException e) { // User already registered;   return -4
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
}
