// Peter Idestam-Almquist, 2019-02-04.

import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class Bank {
	// Instance variables.
	private final List<Account> accounts = new ArrayList<Account>();
	private List<ReentrantLock> lockArray = new ArrayList<ReentrantLock>();
	private static boolean initLockList = false;

	// Instance methods.

	// synchronizerad för att man inte ska kunna ge två konton samma ID
	synchronized int newAccount(int balance) {
		int accountId;

		accountId = accounts.size(); // FIX ORIGINAL

		accounts.add(new Account(accountId, balance));
		return accountId;
	}

	// Eftersom som denna metod bara läser av variabler behövs inget lås, man skulle
	// behöva låsa flera steg om man skulle använda denna metod för att ändra
	// balansen på kontot eller liknande.
	int getAccountBalance(int accountId) {
		Account account = null;
		int balance;
		account = accounts.get(accountId);

		balance = account.getBalance();

		return balance;

	}

	void runOperation(Operation operation) {
		try {

			Account account = null;
			int balance;

			// skapar en array med ett lås för varje konto. Man kan ta ut den från denna
			// metod och göra det när man har skapat alla konton så slipper man låta alla
			// trådar vänta på att denna ska bli klar, men med testprogrammet som jag
			// använde så hade jag den metoden här för att göra det tydligare vad som hände

			// Ett annat sätt hade varit att varje konto objekt skulle ha ett lås som man
			// kommer åt via själva kontot man vill göra en operation på

			initiateLockArray();

			/*
			 * Eftersom accountID inte kommer ändras så behövs inget lås och man kan läsa
			 * den asyncront
			 */
			account = accounts.get(operation.getAccountId());

			// Jag flyttade om ordning på vad som hämtas, nu hämtas summan som ska dras
			// eller läggas in på kontot
			// först, eftersom den inte kräver något lås då den variabeln Amount i Operation
			// objektet inte kommer ändras på, det är bara instruktioner.

			balance = operation.getAmount();

			// Här behöver vi låsa för att konto balansen inte ska kunna kommas åt medans en
			// annan tråd ändrar den. Och eftersom vi har ett lås för varje konto kan vi
			// vara säkra på att det bara de trådarna som vill komma åt det kontot som får
			// vänta, och det blir inga deadLocks.

			lockArray.get(account.getId()).lock();

			balance = balance + account.getBalance();

			account.setBalance(balance);

			lockArray.get(account.getId()).unlock();

		} catch (Exception e) {
			System.out.println(e);
		}

	}

	// Denna metod är Synchronized för att vi vill vara säkra på att vi får rätt
	// mängd antal lås, innan vi säkert kan börja utföra alla operationer och
	// transaktioner.
	synchronized private void initiateLockArray() {
		if (initLockList == false) {

			initLockList = true;
			for (int i = 0; i < accounts.size(); i++) {
				ReentrantLock lock = new ReentrantLock();
				lockArray.add(lock);

			}
			System.out.println("Array of locks for each account, number of locks: " + lockArray.size());

		}
	}

	void runTransaction(Transaction transaction) {
		List<Operation> currentOperations = transaction.getOperations();
		for (Operation operation : currentOperations) {
			runOperation(operation);
		}
	}
}

// class Bank {
// // Instance variables.
// private final List<Account> accounts = new ArrayList<Account>();
// private ReentrantLock readLock = new ReentrantLock();
// private ReentrantLock writeLock = new ReentrantLock();

// private ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
// private List<ReentrantLock> lockArray = new ArrayList<ReentrantLock>();
// private static boolean initLockList = false;
// private Random rnd = new Random();

// // Instance methods.

// int newAccount(int balance) {
// int accountId;
// accountId = accounts.size(); // FIX ORIGINAL
// accounts.add(new Account(accountId, balance));
// return accountId;
// }

// int getAccountBalance(int accountId) {
// Account account = null;
// // if(readWriteLock.readLock().tryLock()){

// account = accounts.get(accountId);
// // }
// // readWriteLock.readLock().unlock();

// return account.getBalance();

// }

// // private boolean isAccountTaken(Operation operation){
// // boolean isOccupied = false;

// // for (int op : operationID) {
// // if(op == operation.getAccountId()){
// // isOccupied = true;
// // break;
// // }
// // }
// // if(isOccupied){
// // return true;
// // }
// // return false;
// // }

// // void aquireLock(Operation operation, Account account){
// // if(lockArray.get(account.getId()).tryLock()){
// // System.out.println("locking : "+ lockArray.get(account.getId()) + " -> " +
// account.getId());

// // }else{
// // try {
// // System.out.println("waiting 5 mili on -> " +
// Thread.currentThread().getName() +lockArray.get(account.getId())+
// account.getId());
// // wait(rnd.nextInt(15));

// // } catch (Exception e) {
// // //TODO: handle exception
// // }finally{
// // aquireLock(operation, account);
// // }
// // }
// // }

// void runOperation(Operation operation) {
// Account account = null;
// int balance;

// if (initLockList == false) {

// synchronized (this) {
// initLockList = true;
// for (int i = 0; i < accounts.size(); i++) {
// ReentrantLock lock = new ReentrantLock();
// lockArray.add(lock);

// }
// System.out.println(lockArray.size());
// }

// }
// // readWriteLock.readLock().lock();
// account = accounts.get(operation.getAccountId());
// balance = operation.getAmount();
// // aquireLock(operation, account);
// lockArray.get(account.getId()).lock();
// // synchronized (lockArray.get(account.getId())){

// try {
// balance = balance + account.getBalance();

// // readWriteLock.readLock().unlock();

// account.setBalance(balance);

// lockArray.get(account.getId()).unlock();
// System.out.println("unlocking : " +Thread.currentThread().getName()+ " " +
// lockArray.get(account.getId()) +" -> " + account.getId());

// } catch (Exception e) {
// System.out.println(e);
// }

// }

// // System.out.println("writeLock");
// // System.out.println("account " + account.getId() +", " +
// account.getBalance();

// // System.out.println("account " + account.getId() +", " +
// // account.getBalance());
// // System.out.println("writeLock unlocked");

// void runTransaction(Transaction transaction) {
// List<Operation> currentOperations = transaction.getOperations();
// for (Operation operation : currentOperations) {
// runOperation(operation);
// }
// }
// }
