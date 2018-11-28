package users;


/**
 * Class for maintaining the current user
 *
 * @author blackk100
 */
public final class CurrentUser {
	private static String UName = "guest"; // Username

	/**
	 * Returns the Current User
	 *
	 * @return The current User's Username
	 */
	public static String GetCurrentUser() {
		return CurrentUser.UName;
	}

	/**
	 * Sets the Current User
	 *
	 * @param UName Username
	 */
	public static void SetCurrentUser(String UName) {
		CurrentUser.UName = UName;
	}

	/**
	 * Logs the user out.
	 */
	public static void Logout() {
		CurrentUser.SetCurrentUser("guest");
	}

}
