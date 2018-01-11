class WalletService{
  private float balance = 0;

  public boolean canAfford(float amount){
    if(this.balance >= amount){
      return true;
    }
    return false
  }

  public void withdraw(float amount){
    if(this.canAfford(amount)){
      balance -= amount;
    }
  }

}
