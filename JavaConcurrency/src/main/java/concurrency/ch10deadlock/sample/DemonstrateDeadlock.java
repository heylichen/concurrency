package concurrency.ch10deadlock.sample;

import java.util.Random;

public class DemonstrateDeadlock {
	private static final int NUM_THREADS = 20;
	private static final int NUM_ACCOUNTS = 5;
	private static final int NUM_ITERATIONS = 1000000;

	public static void main(String[] args) {
		final Random rnd = new Random();
		final Account[] accounts = new Account[NUM_ACCOUNTS];
		for (int i = 0; i < accounts.length; i++)
			accounts[i] = new Account(new DollarAmount(20000));
		class TransferThread extends Thread {
			public void run() {
				for (int i = 0; i < NUM_ITERATIONS; i++) {
					int fromAcct = rnd.nextInt(NUM_ACCOUNTS);
					int toAcct = rnd.nextInt(NUM_ACCOUNTS);
					DollarAmount amount = new DollarAmount(rnd.nextInt(1000));
					transferMoney(accounts[fromAcct], accounts[toAcct], amount);
				}
			}

			// Warning: deadlock-prone!
			public void transferMoney(Account fromAccount, Account toAccount, DollarAmount amount)
					throws InsufficientFundsException {
				synchronized (fromAccount) {
					synchronized (toAccount) {
						if (fromAccount.getBalance().getAmount().compareTo(amount.getAmount()) < 0)
							throw new InsufficientFundsException();
						else {
							fromAccount.debit(amount);
							toAccount.credit(amount);
						}
					}
				}
			}
		}
		for (int i = 0; i < NUM_THREADS; i++)
			new TransferThread().start();
	}
}