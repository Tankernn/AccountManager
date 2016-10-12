package eu.tankernn.accounts;

/**
 * Describes an event in an account's history.
 * @author frans
 *
 */
public class AccountEvent {
	
	private final double balanceChange;
	private final String description;
	
	public AccountEvent(double balanceChange, String description) {
		this.balanceChange = balanceChange;
		this.description = description;
	}

	public double getBalanceChange() {
		return balanceChange;
	}

	public String getDescription() {
		return description;
	}
	
	public String toString() {
		return description;
	}
	
}
