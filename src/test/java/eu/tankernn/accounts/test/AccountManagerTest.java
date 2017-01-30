package eu.tankernn.accounts.test;

import java.io.File;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.tankernn.accounts.Account;
import eu.tankernn.accounts.AccountManager;

public class AccountManagerTest {
	static Account a;
	static File f;

	@BeforeClass
	public static void setUpClass() {
		AccountManager.init(() -> {
		}, false);
		AccountManager.setSaveWithEncryption(false);
		f = new File("test.acc");
		a = new Account("Test", "McExample");
		AccountManager.addAccount(a);
	}

	@Test
	public void testSearch() {
		Assert.assertTrue(AccountManager.search(a.accountNumber).contains(a));
		Assert.assertTrue(AccountManager.search(a.firstName).contains(a));
		Assert.assertTrue(AccountManager.search(a.lastName).contains(a));
		Assert.assertEquals(a, AccountManager.getAccountByNumber(a.accountNumber).get());
	}
	
	@AfterClass
	public static void cleanUp() {
		f.delete();
	}
}
