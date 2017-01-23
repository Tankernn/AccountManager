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

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Account))
			return false;
		Account other = (Account) obj;
		return firstName.equals(other.firstName) && lastName.equals(other.lastName)
				&& accountNumber.equals(other.accountNumber);
	}

	public String toString() {
		return firstName + " " + lastName;
	}

	public double calculateBalance() {
		return history.stream().mapToDouble(a -> a.getBalanceChange()).sum();
	}

}
