class WalletService{
    private int balance = 0;

    public WalletService(int initialBalance) {

        this.balance = initialBalance;
    }

    public boolean canAfford(int amount){

        return this.balance >= amount;
    }

    public void withdraw(int amount){
        this.balance -= amount;
    }

    public void payIn(int amount){
        this.balance += amount;
    }

    public int getBalance() {

        return balance;
    }

    public String toString(){
        return String.valueOf(this.balance);
    }
}
