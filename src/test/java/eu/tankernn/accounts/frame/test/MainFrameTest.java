package eu.tankernn.accounts.frame.test;

import org.junit.Test;

import eu.tankernn.accounts.Account;
import eu.tankernn.accounts.AccountManager;
import eu.tankernn.accounts.frame.MainFrame;

public class MainFrameTest {
	@Test
	public void frameShouldInit() {
		AccountManager.init(() -> {
		}, false);
		MainFrame f = new MainFrame();
		f.accountPanel.updatePanel(new Account("Test", "Exampleman"));
	}
}
