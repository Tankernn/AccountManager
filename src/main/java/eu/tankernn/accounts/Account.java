package eu.tankernn.accounts;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Account {

	public final String firstName, lastName, accountNumber;
	public final List<AccountEvent> history;

	public Account(String firstName, String lastName) {
		// Generate a random, unique account id
		accountNumber = Stream.generate(() -> new BigInteger(20, new SecureRandom()).toString())
				.limit(AccountManager.getAccounts().size() + 1)
				.filter(num -> !AccountManager.getAccountByNumber(num).isPresent()).findFirst()
				.orElseThrow(() -> new ArrayIndexOutOfBoundsException());
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
	
	public boolean transfer(double amount, Account receiver) {
		if (amount <= 0 || amount > this.calculateBalance())
			return false;
		
		this.history.add(new AccountEvent(-amount, "Transferred %.2f to " + receiver + "."));
		receiver.history.add(new AccountEvent(amount, "Received %.2f from " + this + "."));
		
		return true;
	}
	
	public boolean deposit(double amount) {
		if (amount <= 0)
			return false;
		
		history.add(new AccountEvent(amount, "User deposited %.2f."));
		return true;
	}
	
	public boolean withdraw(double amount) {
		if (amount <= 0 || amount > this.calculateBalance())
			return false;
		
		history.add(new AccountEvent(-amount, "User withdrew %.2f."));
		return true;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Account))
			return false;
		Account other = (Account) obj;
		return firstName.equals(other.firstName) && lastName.equals(other.lastName) && accountNumber.equals(other.accountNumber);
	}

	public String toString() {
		return firstName + " " + lastName;
	}

	public double calculateBalance() {
		return history.stream().mapToDouble(a -> a.balanceChange).sum();
	}

}
