package service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import exception.InvalidAmountException;
import model.Fund;
import model.User;


public class BankingService{
    private Scanner scanner;
    private List<User> userList;
    //Immutable list


    public BankingService(Scanner scanner){
        BigDecimal initialCash = new BigDecimal("1000.00");
        List<String> names = List.of("Alice", "Bob", "Charlie", "Diana");
        
        this.scanner = scanner;
        this.userList = names.stream()
                             .map(name -> new User(name, initialCash))
                             .collect(Collectors.toList());
    }

    public void start() {
        boolean isRunning = true;

        User currentUser = null;

        while (isRunning){
        
            if (currentUser == null){
                //System.out.println("Welcome to The Banking App!");
                //System.out.println("\n");
                System.out.print("Enter your name to login: ");
            } else {
                
                printMenu();
                
            }
            if (!scanner.hasNextLine()){
                isRunning = false;
                continue;
            }

            String input = scanner.nextLine().trim();

            if (currentUser == null){
                final String searchName = input;
                currentUser = userList.stream()
                                    .filter(user -> user.getName().equalsIgnoreCase(searchName.trim()))
                                    .findAny()
                                    .orElse(null);

                if (currentUser == null){
                    System.out.println("User not found");
                } else {
                    System.out.println("Welcome, " + currentUser.getName() + " !");
                }
            } else {

                switch (input) {
                    case "1":
                        System.out.println("Savings: $" + currentUser.getSavingsAccount().getBalance());
                        System.out.println("Investment Total: $" + currentUser.getInvestmentAccount().getBalance());
    
                        // 2. Show the uninvested cash
                        System.out.println("  - Uninvested: $" + currentUser.getInvestmentAccount().getNotInvestedBalance());
                        
                        // 3. Loop through the Map to show active funds
                        Map<Fund, BigDecimal> userInvestments = currentUser.getInvestmentAccount().getInvestments();
                        break;

                    case "2":
                        System.out.print("Enter Amount to deposit to saving Account: $");
                        String amountInput = scanner.nextLine().trim();

                        try{
                            BigDecimal depositAmount = new BigDecimal(amountInput);
                            currentUser.deductCash(depositAmount);
                            currentUser.getSavingsAccount().deposit(depositAmount);
                            System.out.println("Deposit Successful.");
                        } catch (NumberFormatException e){
                            System.out.println("Invalid Amount. Please enter valid Amount.");
                        } catch (InvalidAmountException e){
                            System.out.println(e.getMessage());
                        }
                        break;
                    case "3":
                        System.out.println("Enter Amount to withdraw from saving Account");
                        String withdrawInput = scanner.nextLine().trim();

                        try{

                            BigDecimal withdrawAmount = new BigDecimal(withdrawInput);

                            currentUser.getSavingsAccount().withdraw(withdrawAmount);

                            currentUser.addCash(withdrawAmount);
                            System.out.println("Withdraw Successful.");
                        } catch (NumberFormatException e){
                            System.out.println("Invalid Amount. Please enter valid Amount.");
                        } catch (InvalidAmountException e){
                            System.out.println(e.getMessage());
                        }
                        
                        break;
                    case "4":
                        System.out.println("Enter Name of the person");
                        String recipientInput = scanner.nextLine().trim();

                        User recipient = userList.stream()
                                                .filter(user -> user.getName().equalsIgnoreCase(recipientInput))
                                                .findAny()
                                                .orElse(null);
                        if (recipient == null){
                            System.out.println("User not found");
                            return;
                        }

                        if (recipient.getName().equalsIgnoreCase(currentUser.getName())){
                            System.out.println("You cannot send money to yourself");
                            break;
                        }

                        System.out.println("Enter amount: ");
                        String sendInput = scanner.nextLine().trim();

                        try{

                            BigDecimal sendAmount = new BigDecimal(sendInput);

                            currentUser.getSavingsAccount().withdraw(sendAmount);

                            recipient.getSavingsAccount().deposit(sendAmount);
                            System.out.println("Money sent successfully");
                        } catch (NumberFormatException e){
                            System.out.println("Invalid Amount. Please enter valid Amount.");
                        } catch (InvalidAmountException e){
                            System.out.println(e.getMessage());
                        }

                        break;
                    case "5":
                        System.out.println("Available funds: ");
                        Fund[] allFunds = Fund.values();
                        for (int i = 0; i < allFunds.length; i++){
                            System.out.println((i + 1) + ". " + allFunds[i]);
                        }

                        System.out.println("Select a fund to invest in: ");
                        String fundChoice = scanner.nextLine().trim();

                        try{
                            int index = Integer.parseInt(fundChoice);
                            if (index < 1 || index > allFunds.length){
                                System.out.println("Invalid fund choice");
                                break;
                            }

                            Fund selectedFund = allFunds[index - 1];
                            System.out.println("Enter Amount to Invest");
                            String investInput = scanner.nextLine().trim();

                            BigDecimal investAmount = new BigDecimal(investInput);

                            currentUser.getInvestmentAccount().invest(selectedFund, investAmount);

                            BigDecimal currentValue =
                                    currentUser.getInvestmentAccount()
                                            .getInvestmentBalance(selectedFund);

                            System.out.println(
                                    "Successfully invested $" + investAmount +
                                            " into " + selectedFund +
                                            ". Current investment value: $" + currentValue
                            );

                            System.out.println("Successfully invested $" + investAmount + " into " + selectedFund + ".");
                        } catch (NumberFormatException e){
                            System.out.println("Invalid Amount. Please enter valid Amount.");
                        } catch (InvalidAmountException e){
                            System.out.println(e.getMessage());
 
                        }

                        
                        break;
                    case "6":
                        System.out.println("1. Tranfer from Savings to Investment ");
                        System.out.println("2. Tranfer from Investment to Savings ");
                        System.out.println("Select direction");

                        String direction = scanner.nextLine().trim();

                        if (!direction.equals("1") && !direction.equals("2")){
                            System.out.println("Invalid direction");
                            break;
                        }

                        System.out.println("Enter amount to tranfer");
                        String tranferInput = scanner.nextLine().trim();

                        try{

                            BigDecimal tranferAmount = new BigDecimal(tranferInput);

                            if (direction.equals("1")){
                                currentUser.getSavingsAccount().withdraw(tranferAmount);
                                currentUser.getInvestmentAccount().deposit(tranferAmount);
                                System.out.println("Tranfer to Investment Successful");

                            } else if (direction.equals("2")){
                                currentUser.getInvestmentAccount().withdraw(tranferAmount);
                                currentUser.getSavingsAccount().deposit(tranferAmount);
                                System.out.println("Tranfer to Investment Successful");
                            }
                        } catch (NumberFormatException e){
                            System.out.println("Invalid Amount. Please enter valid Amount.");
                        } catch (InvalidAmountException e){
                            System.out.println(e.getMessage());
                        }

                        
                        break;
                    case "7":
                        currentUser.getInvestmentAccount().withdrawAllInvestments();
                        System.out.println("All your investments have been withdrawn to your uninvested balance ");
                        break;
                    case "8":
                        currentUser = null;
                        break;
                    case "9":
                        isRunning = false;
                        break;
                
                    default:
                        System.out.println("Invalid option. Please try again. ");
                        break;
                }
            }
        }
    }

    private void printMenu(){
        System.out.println("\n--- Banking App Menu ---");
        System.out.println("1. Show balance");
        System.out.println("2. Deposit money");
        System.out.println("3. Withdraw money");
        System.out.println("4. Send money to a person");
        System.out.println("5. Invest in funds");
        System.out.println("6. Transfer between accounts");
        System.out.println("7. Withdraw all investments");
        System.out.println("8. Logout");
        System.out.println("9. Exit");
        System.out.print("Enter your choice: ");
    }
}