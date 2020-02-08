// Peter Idestam-Almquist, 2019-02-04.

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class Bank {
	// Instance variables.
	private final List<Account> accounts = new ArrayList<Account>();
	// private ReentrantLock lock = new ReentrantLock();
	private ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

	// Instance methods.

	int newAccount(int balance) {
		int accountId;
		accountId = accounts.size(); // FIX ORIGINAL
		accounts.add(new Account(accountId, balance));
		return accountId;
	}

	int getAccountBalance(int accountId) {
		Account account = null;
		// if(readWriteLock.readLock().tryLock()){
		
		account = accounts.get(accountId);
	// }
	// 	readWriteLock.readLock().unlock();
	

		return account.getBalance();
	
	}

	void runOperation(Operation operation) {

		Account account = null;
		int balance;

		// readWriteLock.readLock().lock();

		account = accounts.get(operation.getAccountId());
		// readWriteLock.writeLock().lock();

		balance = account.getBalance();

		balance = balance + operation.getAmount();

		// readWriteLock.readLock().unlock();

		try {
			// System.out.println("writeLock");
			// System.out.println("account " + account.getId() +", " + account.getBalance();
			
			account.setBalance(balance);
			
			// System.out.println("account " + account.getId() +", " + account.getBalance());
			// readWriteLock.writeLock().unlock();
			// System.out.println("writeLock unlocked");

		} catch (Exception e) {
			System.out.println(e);
		}

	}

	void runTransaction(Transaction transaction) {
		List<Operation> currentOperations = transaction.getOperations();
		for (Operation operation : currentOperations) {
			runOperation(operation);
		}
	}
}
