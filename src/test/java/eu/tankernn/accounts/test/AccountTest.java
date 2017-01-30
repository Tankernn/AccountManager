package eu.tankernn.accounts.test;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import eu.tankernn.accounts.Account;

public class AccountTest {
	Account a, b;
	
	@Before
	public void setup() {
		a = new Account("Test", "Tester");
		b = new Account("Example", "Exampler");
		b.deposit(500);
	}
	
	@Test
	public void testDeposit() {
		Assert.assertTrue(a.deposit(2483));
		Assert.assertFalse(a.deposit(-23));
	}
	
	@Test
	public void testWithdraw() {
		Assert.assertFalse(b.withdraw(2483));
		Assert.assertFalse(b.withdraw(-23));
		Assert.assertTrue(b.withdraw(50));
	}
	
	@Test
	public void testTransfer() {
		Assert.assertFalse(a.transfer(2, b));
		Assert.assertFalse(b.transfer(-2, a));
		Assert.assertTrue(b.transfer(200, a));
	}
}
