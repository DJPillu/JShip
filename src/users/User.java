package users;


/**
 * User Abstract Class
 *
 * @author blackk100
 */
abstract class User {
	private final long Hash;    // Hashed Password
	private final String UName; // Username

	/**
	 * Constructor for the User class
	 *
	 * @param name
	 * @param psswrd
	 */
	User(String name, char[] psswrd) {
		this.UName = name;
		this.Hash = hash(psswrd);
	}

	/**
	 * @return the Username
	 */
	public final String getUName() {
		return UName;
	}

	/**
	 * @return the Hash
	 */
	public final long getHash() {
		return Hash;
	}

	/**
	 * The password hashing algorithm
	 *
	 * @param psswrd
	 *
	 * @return Generates and returns the Hashed Password
	 */
	private long hash(char[] psswrd) {
		long hash = 0;                            // Hash value.
		for (int i = 0; i < psswrd.length; i++) { // Iterate over the entire password array
			if ((i + 1) % 2 == 0) {                 // If even, add hash value
				hash += Character.hashCode(psswrd[i]);
			} else {                                // If odd, subtract hash value
				hash -= Character.hashCode(psswrd[i]);
			}
		}

		hash = ~hash;                 // Binary Flip of the Hash Value

		if (hash > 8388607) {         // Max limit of MySQL MEDIUMINT is 8388607
			hash = 8388607;
		} else if (hash < -8388608) { // Max negative limit of MySQL MEDIUMINT is -8388608
			hash = -8388607;
		}

		return hash;
	}
}
