import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Bank extends UnicastRemoteObject implements BankInterface {

    private static final long serialVersionUID = 1L;
    //List of all user accounts
    private List<BankAccount> accounts;
    //Maps users and their respective login details
    private HashMap<String, Integer> usernamesPins;
    
    private HashMap<String, Integer> usernamesAccounts;
    // Stores info on all active sessions
    private HashMap<Long, Date> activeUsers;

    public Bank() throws RemoteException{
        super();
        //Retrieve accounts
        accounts = getBankAccounts();
        //Retrieve pins
        usernamesPins = getPins();
        //Retrieve account numbers
        usernamesAccounts = getAccountNumbers();
        //Map active users to their respective IDs
        activeUsers = new HashMap<>();
    }

    public static void main(String args[]) throws Exception {
        if (System.getSecurityManager() == null){
            System.setSecurityManager(new SecurityManager());
        }
        try {
            String name = "AIB_Server";
            BankInterface engine = new Bank();
            //Bind object to registry
            Naming.rebind(name, engine);						
            System.out.println("AIB Server Accessed");		
        }
        catch(Exception e){
            System.err.println("Bank Server Exception!");
            e.printStackTrace();
        }
    }

    @Override
    public long login(String username, int pin, int accountNumber) throws RemoteException, IncorrectUsername, IncorrectPin, IncorrectAccountNumber {
        //Verify username
        if(usernamesPins.containsKey(username)){			
            System.out.println("True");
            int retrievedPin = usernamesPins.get(username);
            int retrievedAccountNumber = usernamesAccounts.get(username);
            //Verify password
                if (pin == retrievedPin){	
                    //Verify account number
                    if(accountNumber == retrievedAccountNumber){
                        //Assign unige identifier for current session
                        long ID = generateNewID();		
                        return ID;
                    }
                    else{
                        //Incorrect account number
                        throw new IncorrectAccountNumber(); 
                    }
                }
                else{ 
                    //Incorrect password
                    throw new IncorrectPin(); 
                }
        }
        else{
            //Incorrect username
            throw new IncorrectUsername();
        }
    }
    
    
    @Override
    public void withdraw(int accountNumber, int amount, long ID) throws RemoteException, ExpiredSession, InsufficientFunds {
        //Session expired?
        boolean expired = sessionExpired(ID);
        //Throw exception if session expired
        if (expired == true){									
            throw new ExpiredSession("Session Timed Out");
        }
        //Find user's bank account
        BankAccount bankAccount = findAccount(accounts, accountNumber);				
        boolean successfulTransaction;
        //Withdraw amount into user's bank account
        successfulTransaction = bankAccount.withdraw(amount);
        //Throw exception if not enough funds are available
        if (!successfulTransaction){						
            throw new InsufficientFunds("Insufficient Funds");
        }			
    }

    @Override
    public void deposit(int accountNumber, int amount, long ID) throws RemoteException, ExpiredSession  {
        //Session expired?
        boolean expired = sessionExpired(ID);
        //Throw exception if session expired
        if (expired == true){								
            throw new ExpiredSession("Session Timed Out");
        }
        //Find user's bank account
        BankAccount bankAccount = findAccount(accounts, accountNumber);
        //Deposits amount into user's bank account
        bankAccount.deposit(amount);	
    }

    @Override
    public int inquiry(int accountNumber, long ID) throws RemoteException, ExpiredSession {
        //Session expired?
        boolean expired = sessionExpired(ID);
        //Throw exception if session expired
        if (expired == true){
            throw new ExpiredSession("Session Timed Out");
        }
        //Find user's bank account
        BankAccount bankAccount = findAccount(accounts, accountNumber);
        //Gets user's bank account balance
        return bankAccount.inquiry();		
    }

    @Override
    public BankStatementInterface getStatement(int accountNumber, Date startDate, Date endDate, long ID) throws RemoteException, ExpiredSession {
        //Session expired?
	boolean expired = sessionExpired(ID);
        //Throw exception if session expired
        if (expired == true){				
            throw new ExpiredSession("Session Timed Out");
        }
        //Find user's bank account
	BankAccount bankAccount = findAccount(accounts, accountNumber);
	BankStatementInterface statement = null;
        //Gets user's bank account statement for given dates
        statement = bankAccount.getStatement(startDate, endDate);
        return statement;			
    }

    @Override
    public void logout(long ID) throws RemoteException {
            activeUsers.remove(ID);
    }
   
    
    //Sample accounts
    private List<BankAccount> getBankAccounts() {
        //                                         account    account
	//                                         number     balance
	BankAccount bankAccount1 = new BankAccount(123456789, 10000);
	BankAccount bankAccount2 = new BankAccount(234567891, 300);
	BankAccount bankAccount3 = new BankAccount(345678912, 7500);
	BankAccount bankAccount4 = new BankAccount(456789123, 9000);
	BankAccount bankAccount5 = new BankAccount(567891234, 2100);
        BankAccount bankAccount6 = new BankAccount(678912345, 115000);
	BankAccount bankAccount7 = new BankAccount(789123456, 30000);
	BankAccount bankAccount8 = new BankAccount(891234567, 8000);
	BankAccount bankAccount9 = new BankAccount(912345678, 2900);
	BankAccount bankAccount10 = new BankAccount(112345678, 64000);
	List<BankAccount> bankAccounts = new ArrayList<>();
	bankAccounts.add(bankAccount1);
	bankAccounts.add(bankAccount2);
	bankAccounts.add(bankAccount3);
	bankAccounts.add(bankAccount4);
	bankAccounts.add(bankAccount5);
        bankAccounts.add(bankAccount6);
	bankAccounts.add(bankAccount7);
	bankAccounts.add(bankAccount8);
	bankAccounts.add(bankAccount9);
	bankAccounts.add(bankAccount10);
	return bankAccounts;
    }

    //Verify bank account number given
    private BankAccount findAccount(List<BankAccount> accounts, int accountNumber) {
	for (BankAccount account : accounts){
		if (account.getAccountNumber() == accountNumber){
			return account;
		}
	}
	return null;
    }

    //Sample usernames and pins
    private HashMap<String, Integer> getPins() {
        
	HashMap<String, Integer> detailsPins = new HashMap<>();
        //                   username       pin
	detailsPins.put("john_smith", new Integer(9999));
	detailsPins.put("james_burr", new Integer(2312));
	detailsPins.put("mike_black", new Integer(5647));
        detailsPins.put("pat_simon", new Integer(7640));
	detailsPins.put("james_burr", new Integer(8260));
	detailsPins.put("paul_leary", new Integer(6532));
        detailsPins.put("jack_white", new Integer(5646));
	detailsPins.put("fred_reilly", new Integer(8624));
	detailsPins.put("matt_down", new Integer(9135));
        detailsPins.put("colm_healy", new Integer(3792));
        
	return detailsPins;
    }
    //Sample usernames and pins
    private HashMap<String, Integer> getAccountNumbers() {
        HashMap<String, Integer> detailsAccounts = new HashMap<>();
        //                   username       account
        detailsAccounts.put("john_smith", 123456789);
	detailsAccounts.put("james_burr", 234567891);
	detailsAccounts.put("mike_black", 345678912);
        detailsAccounts.put("pat_simon", 456789123);
	detailsAccounts.put("james_burr", 567891234);
	detailsAccounts.put("paul_leary", 678912345);
        detailsAccounts.put("jack_white", 789123456);
	detailsAccounts.put("fred_reilly", 891234567);
	detailsAccounts.put("matt_down", 912345678);
        detailsAccounts.put("colm_healy", 112345678);
        return detailsAccounts;
    }

    //Generate a unique ID with an associated time
    private long generateNewID() {
	Random random = new Random();
	boolean notUnique = true;
	long id = 00000;
        //Loop until unique key found
	while(notUnique){								
            id = random.nextLong();
            //Want a positive ID number
            if (id < 0){							
                    id *= -1;
            }
            //Check if ID already exists
            if (!activeUsers.containsKey(id)){	
                //Store ID and current time
                activeUsers.put(id, new Date());
                //exit loop
                notUnique = false;						
            }
	}
	System.out.println(id);
	return id;
    }

    //Has ID expired?
    private boolean sessionExpired(long ID) {
	//ID creation time
	Date startUp = activeUsers.get(ID);
        //Current time
	Date now = new Date();
        //Is difference < 5 minutes
	if (now.getTime() - startUp.getTime() >= 5*60*1000){
            //ID expired
            //Remove ID
            activeUsers.remove(ID);						
            return true;										
	}
        //ID valid
	return false;
    }
}