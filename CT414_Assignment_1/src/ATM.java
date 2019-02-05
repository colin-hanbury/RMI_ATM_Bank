import java.rmi.Naming;
import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;


public class ATM {
    
    private int accountNumber;
    BankInterface AIB;
    Scanner scanner = new Scanner(System.in);
    static Long ID = 00000L;

    public ATM(BankInterface AIB){
        this.AIB = AIB;
        this.login();
    }

    public static void main (String args[]) throws Exception {
        if (System.getSecurityManager() == null){
            System.setSecurityManager(new SecurityManager());
        }
        
        try
        {
            String name = "AIB_Server";						
            //Perform lookup of object that is binded to registry
            BankInterface bankInterface = (BankInterface) Naming.lookup(name);	
            //Starts the ATM
            new ATM(bankInterface);														
            System.out.println("Connected to AIB's server");
        }
        catch(Exception e){
            System.err.println("Couldn't connect to AIB's server!");
            e.printStackTrace();
        }
    // get user’s input, and perform the operations
    }

    private void login(){
        System.out.println("\nEnter 'login' to begin session or enter 'exit' to exit");
        String userInput = scanner.next();
        // Loop until a recognised command is entered
        while (!userInput.equals("login") && !userInput.equals("exit")){	
			System.out.println("Invalid Command!\nPlease enter command 'login' to begin or 'quit' to exit: ");
            // get new entry
            userInput = scanner.next(); 								
        }
        // terminate the ATM (client)
        if(userInput.equals("exit")){								
            System.out.println("Session cancelled");
            System.exit(0);
        }
        else{
            // Covers case where user session has timed out, need new session
            ID = 00000L;
            // Loop until unique ID is assigned to user										
            while (ID.equals(00000L)){	
                //Get username
                System.out.println("Enter username:");
                String username = scanner.next(); 
                //Get pin
                System.out.println("Enter pin:");
                int pin = scanner.nextInt();
                //Get account number
                System.out.println("Enter account number:");
                accountNumber = scanner.nextInt(); 
                 		
                //Check entered credentials on server-side			
                try {											
                    // For successful login, user assigned unique session ID
                    ID = AIB.login(username, pin, accountNumber);	
                } 
                catch (RemoteException e) {
                    e.printStackTrace();
                } 
                catch (IncorrectUsername e){
                    System.out.println("Invalid username entered!");
                    login();
                }
                catch (IncorrectPin e){
                    System.out.println("Invalid pin entered!");
                    login();
                }
                catch(IncorrectAccountNumber e){
                    System.out.println("Invalid account number entered!");
                    login();
                }
            }
		System.out.println(ID);
		System.out.println("Login Successful");
                System.out.println("Session is valid for 5 minutes");
		optionsMenu(ID);
        }
    }

    private void optionsMenu(long ID) {
        System.out.println("\nPlease enter the respective number to the option you wish proceed with: \n");
        System.out.printf("\n%-30s %-1s \n", "Withdraw", "1");
        System.out.printf("\n%-30s %-1s \n", "Deposit", "2");
        System.out.printf("\n%-30s %-1s \n", "Balance", "3");
        System.out.printf("\n%-30s %-1s \n", "Statement", "4");
        System.out.printf("\n%-30s %-1s \n", "Log out", "5");
        System.out.println("");
        int entry = scanner.nextInt();
        //Select action based on user's input
        switch (entry){										
            case 1: withdraw(ID);
                break;
            case 2: deposit(ID);
                break;
            case 3: inquiry(ID);
                break;
            case 4: getStatement(ID);
                break;
            case 5: logout(ID);
                break;
            // User chose an invalid option
            default:System.out.println("Invalid selection!\nTry a different option"); 	
                //Return user to the options menu
                optionsMenu(ID);
                break;
        }
    }

    private void withdraw(long ID){
        System.out.println("\nEnter withdrawal amount: ");
        int withdrawalAmount = scanner.nextInt();
        
        try {
            //Call server side method to withdraw money
            AIB.withdraw(accountNumber, withdrawalAmount, ID);	
        } 
        catch (RemoteException e) {
            e.printStackTrace();
        } 
        //Exception thrown when there are insufficient funds to complete the withdrawal
        catch (InsufficientFunds e){											
            System.out.println("Insufficient funds");
            System.out.println("Please try again with a different amount");
            //Restart the withdrawal method for user
            withdraw(ID);												
        } 
        //Exception thrown when user session has timed out
        catch (ExpiredSession e) {											
            //Notify user that the session has timed out
            System.out.println("Session expired");
            System.out.println("Please log in again to continue");
            // Prompt user to sign-in again
            login();																
        }
        // Withdrawal was successful
        System.out.println("Successfully withdrew " + withdrawalAmount + " euro from account " + accountNumber + "\n");
        // Return user to main menu
        optionsMenu(ID);
    }
    
    
    private void deposit(long ID){
        System.out.println("\nPlease enter the amount you wish to deposit: ");
        int withdrawalAmount = scanner.nextInt();
        
        try {
            //Call server side method to deposit money
            AIB.deposit(accountNumber, withdrawalAmount, ID);									
        } 
        catch (RemoteException e) {
            e.printStackTrace();
        } 
        //Exception thrown when user session has timed out
        catch (ExpiredSession e) {											
            //Notify user that the session has timed out
            System.out.println("Session expired\nPlease log in again to continue");
            // Prompt user to sign-in again
            login();																
        }        
        // Deposit was successful
        System.out.println("Successfully deposited " + withdrawalAmount + " euro to account " + accountNumber + "\n");	
        optionsMenu(ID);	
    }

    private void inquiry(long ID){
        float accountBalance = 0;
        try {
            //Call server side method to check balance
            accountBalance = AIB.inquiry(accountNumber, ID);							
        } 
        catch (RemoteException e) {
            e.printStackTrace();
        } 
        //Exception thrown when user session has timed out
        catch (ExpiredSession e){												
            //Notify user that the session has timed out
            System.out.println("\nThis session has expired");
            System.out.println("Please try log in again");
            // Prompt user to sign-in again
            login();																
        }
        //Display balance
        System.out.println("\nThe current balance of account " + accountNumber + " is " + accountBalance + " euro\n");	
        // Return user to main menu
        optionsMenu(ID);																
    }

    private void getStatement(long ID){
        BankStatementInterface statement = null;
        // Start date for the statement
        System.out.println("\nPlease enter the start date (dd/mm/yyyy) for the statement: "); 
        String start = scanner.next();
        // Finish date for the statement
        System.out.println("Please enter the end date (dd/mm/yyyy) for the statement: ");	
        String end = scanner.next();
        // Date format required
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");							
        Date startDate = null, endDate = null;
        
        try {
            // Apply format to date
            startDate = formatter.parse(start);											
            endDate = formatter.parse(end);
            // Call server side method for generating bank statement
            statement = AIB.getStatement(accountNumber, startDate, endDate, ID);						
        }
        catch (RemoteException e) {
            e.printStackTrace();
            //logger.log(Level.SEVERE, e.getMessage());
            login();
        } 
        //Exception thrown when user session has timed out
        catch (ExpiredSession e) {												
            //Notify user that the session has timed out
            System.out.println("This session has expired - please log in again.");	
            // Prompt user to sign-in again
            login();																
        }
        //Exception thrown for incorrect date input
        catch(ParseException e){															
            System.out.println("Invalid Dates - Please start again");
            //Return user to start of method
            getStatement(ID);	
            //logger.log(Level.SEVERE, e.getMessage());
        }
        
        //Gets the transactions of that time period as a List
        List<Transaction> transactions = statement.getTransations();								
        System.out.println("Date\t\t\t\t\t\t Transaction Type\tAmount\t\tBalance");		
        //Loop over all transactions and print details to console
        for(Transaction t: transactions){															
            System.out.println(t.getDate().toString() + "\t\t\t " + t.getType() + "\t\t" + t.getAmount() +
                    "\t\t" + t.getBalance());
        }
        System.out.println();
        //Return user to main menu
        optionsMenu(ID);																
    }

    private void logout(long ID){
        try{
            AIB.logout(ID);
        }
        catch(RemoteException e) {
            e.printStackTrace();
        }
        login();
    }
}