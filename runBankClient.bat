@echo off
cd C:\Users\colin\Documents\College\Semester_2\distributed_systems_and_co_operative_computing\CT414_Assignment_1\src
javac ATM.java Bank.java BankAccount.java BankInterface.java BankStatement.java BankStatementInterface.java ExpiredSession.java IncorrectPin.java IncorrectUsername.java IncorrectAccountNumber.java InsufficientFunds.java Transaction.java -Xlint
copy *.class C:\Users\colin\Documents\College\Semester_2\distributed_systems_and_co_operative_computing\CT414_Assignment_1\bin
copy *.class C:\Users\colin\Documents\College\Semester_2\distributed_systems_and_co_operative_computing\client
cd ..\bin
start rmiregistry
start java -Djava.security.policy=server.policy Bank
cd ..\..\client
start java -Djava.security.policy=client.policy ATM
pause
