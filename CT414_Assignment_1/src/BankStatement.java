import java.util.Date;
import java.util.List;

public class BankStatement implements BankStatementInterface {

    List<Transaction> transactions;
    Date startDate;
    Date endDate;
    int accountNumber;

    public BankStatement(List<Transaction> transactions, Date startDate, Date endDate, int accountNumber){
        this.transactions = transactions;				
        this.startDate = startDate;
        this.endDate = endDate;
        this.accountNumber = accountNumber;
    }
    @Override
    public int getAccountNumber() {
            return accountNumber;
    }
    @Override
    public Date getStartDate() {
            return startDate;
    }
    @Override
    public Date getEndDate() {
            return endDate;
    }
    @Override
    public String getAccountName() {
            return "" + accountNumber;
    }
    @Override
    public List<Transaction> getTransations() {
            return transactions;
    }
}