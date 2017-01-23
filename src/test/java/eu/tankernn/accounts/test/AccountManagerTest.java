package eu.tankernn.accounts.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.tankernn.accounts.Account;
import eu.tankernn.accounts.AccountManager;

public class AccountManagerTest {
	static Account a;

	@BeforeClass
	public static void setUpClass() {
		AccountManager.init(() -> {
		}, false);
		a = new Account("Test", "McExample");
		AccountManager.addAccount(a);
	}

	@Test
	public void testSearch() {
		List<Account> aList = new ArrayList<>();
		aList.add(a);
		Assert.assertEquals(aList, AccountManager.search(a.getAccountNumber()));
		Assert.assertEquals(aList, AccountManager.search(a.getFirstName()));
		Assert.assertEquals(aList, AccountManager.search(a.getLastName()));
	}
}
