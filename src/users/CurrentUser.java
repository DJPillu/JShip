package users;


/**
 * Class for maintaining the current user
 *
 * @author blackk100
 */
public final class CurrentUser {
	private static String UName = "guest"; // Username

	/**
	 * @return The current User's Username
	 */
	public static String GetCurrentUser() {
		return CurrentUser.UName;
	}

	/**
	 * @param UName
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
