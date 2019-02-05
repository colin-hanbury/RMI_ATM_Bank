import java.io.Serializable;
import java.util.Date;
import java.util.List;

public interface BankStatementInterface extends Serializable {

public int getAccountNumber();

public Date getStartDate();

public Date getEndDate();

public String getAccountName();

public List<Transaction>  getTransations();

}
