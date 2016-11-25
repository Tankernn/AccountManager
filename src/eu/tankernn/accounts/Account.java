package eu.tankernn.accounts;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class Account {
	
	private String firstName, lastName, accountNumber;
	private List<AccountEvent> history;

	public Account(String firstName, String lastName) {
		// Generate a random, unique account id
		do {
			accountNumber = new BigInteger(20, new SecureRandom()).toString();
		} while (AccountManager.getAccountByNumber(accountNumber).isPresent());
		this.firstName = firstName;
		this.lastName = lastName;
		this.history = new ArrayList<AccountEvent>();
	}
	
	Account(String firstName, String lastName, String accountNumber, List<AccountEvent> history) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.accountNumber = accountNumber;
		this.history = history;
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
