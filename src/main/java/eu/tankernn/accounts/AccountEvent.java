package eu.tankernn.accounts;

/**
 * Describes an event in an account's history.
 * 
 * @author frans
 *
 */
public class AccountEvent {

	public final double balanceChange;
	public final String description;

	/**
	 * Creates a new account event.
	 * 
	 * @param balanceChange The change in account balance.
	 * @param descriptionFormat
	 *            A string that will be used for a <code>String.format()</code>
	 *            call, along with the absolute balance change value.
	 */
	public AccountEvent(double balanceChange, String descriptionFormat) {
		this.balanceChange = balanceChange;
		this.description = String.format(descriptionFormat, Math.abs(balanceChange));
	}

	public String toString() {
		return description;
	}

}
