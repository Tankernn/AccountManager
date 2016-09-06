package eu.tankernn.accounts;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Account {
	
	private List<AccountEvent> history;
	private String firstName, lastName, accountNumber;

	public Account(String firstName, String lastName) {
		do {
			accountNumber = new BigInteger(20, new SecureRandom()).toString();
		} while (AccountManager.getAccountByNumber(accountNumber).isPresent());
		this.firstName = firstName;
		this.lastName = lastName;
		this.history = new ArrayList<AccountEvent>();
	}
	
	private Account(String accountNumber, String firstName, String lastName, List<AccountEvent> history) {
		this(firstName, lastName);
		this.accountNumber = accountNumber;
		this.history = history;
		calculateBalance();
	}

	public static Account fromJSON(JSONObject obj) throws JSONException {
		List<AccountEvent> history = new ArrayList<AccountEvent>();
		
		JSONArray arr = obj.getJSONArray("history");
		
		for (int i = 0; i < arr.length(); i++) {
			history.add(AccountEvent.fromJSON(arr.getJSONObject(i)));
		}
		
		return new Account(obj.getString("accountNumber"), obj.getString("firstName"), obj.getString("lastName"), history);
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}
	
	public List<AccountEvent> getHistory() {
		return history;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public String toString() {
		return firstName + " " + lastName;
	}

	public double calculateBalance() {
		double newBalance = 0;
		
		for (AccountEvent e : history) {
			newBalance += e.getBalanceChange();
		}
		
		return newBalance;
	}

}
