import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;

public interface BankInterface extends Remote {

    public long login(String username, int pin, int accountNumber) throws RemoteException, IncorrectUsername, IncorrectPin, IncorrectAccountNumber;

    public void deposit(int accountNumber, int amount, long ID) throws RemoteException, ExpiredSession;

    public void withdraw(int accountNumber, int amount, long ID) throws RemoteException, ExpiredSession, InsufficientFunds;

    public int inquiry(int accountNumber, long ID) throws RemoteException, ExpiredSession;

    public BankStatementInterface getStatement(int accountNumber, Date from, Date to, long ID) throws RemoteException, ExpiredSession;

    public void logout(long ID) throws RemoteException;

}