package Inlup2;

class Account {
	// Instance variables.
	private final int id;
	private int balance;
	
	// Constructor.
	Account(int id, int balance) {
		this.id = id;
		this.balance = balance;
	}
	
	// Instance methods.
	
	int getId() {
		return id;
	}
	
	int getBalance() {
		return balance;
	}
	
	void setBalance(int balance) {
		this.balance = balance;
		// System.out.println("new balance -> id: " + id + " " + getBalance());
	}
}
