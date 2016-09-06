package eu.tankernn.accounts;

import org.json.JSONException;
import org.json.JSONObject;

public class AccountEvent {
	
	private final double balanceChange;
	private final String description;
	
	public AccountEvent(double balanceChange, String description) {
		this.balanceChange = balanceChange;
		this.description = description;
	}
	
	public static AccountEvent fromJSON(JSONObject obj) throws JSONException {
		return new AccountEvent(obj.getDouble("balanceChange"), obj.getString("description"));
		
//		if (sender.isPresent() && receiver.isPresent())
//			return new AccountEvent(sender.get(), receiver.get(), obj.getDouble("amount"));
//		else
//			throw new JSONException("The account with account number " + (sender.isPresent() ? obj.getDouble("receiver") : obj.getDouble("sender") + " could not be found."));
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
