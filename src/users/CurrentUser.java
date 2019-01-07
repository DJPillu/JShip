package users;


/**
 * Class for maintaining the current user
 *
 * @author blackk100
 */
public class CurrentUser {

	/**
	 * The default user.
	 */
	private static final String DEFAULT = "guest";

	/**
	 * Current user's username
	 */
	private static String UName = CurrentUser.DEFAULT;

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
	 */
	public static void setCurrentUser(String UName) {
		CurrentUser.UName = UName;
	}

	/**
	 * Logs the user out.
	 */
	public static void logout() {
		CurrentUser.setCurrentUser(CurrentUser.DEFAULT);
	}

}
