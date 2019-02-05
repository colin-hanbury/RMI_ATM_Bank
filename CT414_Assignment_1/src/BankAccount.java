import java.util.ArrayList;
import java.util.Date;
import java.util.List;

class BankAccount {
    
    private int accountNumber;
    private int balance;
    private List<Transaction> transactions = new ArrayList<>();

    public BankAccount(int accountNumber, int amount){
            this.accountNumber = accountNumber;
            this.balance = amount;
    }

    //Deposit money in user bank account
    public void deposit(int amount){
        //Deposit must be greater than 0
        if (amount > 0){		
            this.balance += amount;
            //Add transaction to list
            addTransaction(new Transaction("Deposit", amount, new Date(), this.balance, this.accountNumber));
        }
    }

    public boolean withdraw(int amount){
        //new account balance after withdrawal
        int newbalance = this.balance - amount;
        //new balance must be greater than 0 (no overdrafts)
        if (newbalance >= 0){						
                this.balance = newbalance;
                //Add transaction to list
                addTransaction(new Transaction("Withdrawal", amount, new Date(), this.balance, this.accountNumber));
                //Transaction successful
                return true;
        }
        else{
            //Transaction unsuccessful
            return false;
        }
        
    }

    //Check balance of user bank account
    public int inquiry(){
            return this.balance;
    }

    //Return statement to user recording transactions from defined time period
    public BankStatementInterface getStatement(Date startDate, Date endDate){
        //Stores transactions from set period
        List<Transaction> statementTransactions = new ArrayList<>();
        //check each transaction's date and add relevant transactions to list
        for (Transaction transaction: transactions){
            Date date = transaction.getDate();				
            if(date.after(startDate) && date.before(endDate)){
                statementTransactions.add(transaction);
            }
        }
        //Generate statement with relevant info
        BankStatement statement = new BankStatement(statementTransactions, startDate, endDate, this.accountNumber);
        return statement;
    }
    
    //Adds transaction to list of total history transactions
    public void addTransaction(Transaction t){	
            transactions.add(t);
    }

    //Getter and setter methods
    public int getAmount() {
            return this.balance;
    }
    public int getAccountNumber() {
            return accountNumber;
    }
    public void setAmount(int amount) {
            this.balance = amount;
    }
    public void setAccountNumber(int accountNumber) {
            this.accountNumber = accountNumber;
    }
}