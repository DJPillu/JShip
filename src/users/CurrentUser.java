package users;


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
	 * Returns the Current User
	 *
	 * @return The current User's Username
	 */
	public static String getCurrentUser() {
		return CurrentUser.UName;
	}

	/**
	 * Sets the Current User
	 *
	 * @param UName Username
	 */
	public static void setCurrentUser(String UName) {
		CurrentUser.UName = UName;
	}

	/**
	 * Logs the user out.
	 */
	public static void logout() {
		CurrentUser.setCurrentUser("guest");
	}

}
