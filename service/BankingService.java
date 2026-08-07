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
                System.out.flush();
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
                    System.out.println("User not found. Please try again.");
                    System.out.println("User not found. Please try again.");
                } else {
                    System.out.println("Welcome, " + currentUser.getName() + "!");
                }
            } else {

                switch (input) {
                    case "1":
                        System.out.println("Savings account balance: $" + currentUser.getSavingsAccount().getBalance());
                        System.out.println("Investment account balance:");

                        BigDecimal amount = currentUser.getInvestmentAccount()
                                .getNotInvestedBalance()
                                .setScale(2);
                        // 2. Show the uninvested cash
                        System.out.println("* Not Invested: $" + amount);
    

                        // 3. Loop through the Map to show active funds
                        Map<Fund, BigDecimal> userInvestments = currentUser.getInvestmentAccount().getInvestments();
                        break;

                    case "2":
                        System.out.print("Enter amount to deposit to savings account: $");
                        String amountInput = scanner.nextLine().trim();

                        try{
                            BigDecimal depositAmount = new BigDecimal(amountInput);
                            currentUser.deductCash(depositAmount);
                            currentUser.getSavingsAccount().deposit(depositAmount);
                            System.out.println("Deposit successful.");
                        } catch (NumberFormatException e){
                            System.out.println("Invalid Amount. Please enter valid Amount.");
                        } catch (InvalidAmountException e){
                            System.out.println(e.getMessage());
                        }
                        break;
                    case "3":
                        System.out.print("Enter amount to withdraw from savings account: $");
                        String withdrawInput = scanner.nextLine().trim();

                        try{

                            BigDecimal withdrawAmount = new BigDecimal(withdrawInput);

                            currentUser.getSavingsAccount().withdraw(withdrawAmount);

                            currentUser.addCash(withdrawAmount);
                            System.out.println("Withdraw successful.");
                        } catch (NumberFormatException e){
                            System.out.println("Withdrawal failed: amount must be positive");
                        } catch (InvalidAmountException e){
                            System.out.println(e.getMessage());
                        }
                        
                        break;
                    case "4":
                        System.out.println("Available recipients:");

                        for (User u : userList) {
                            if (!u.getName().equalsIgnoreCase(currentUser.getName())) {
                                System.out.println(u.getName());
                            }
                        }
                        System.out.print("Enter recipient's name: ");

                        String recipientInput = scanner.nextLine().trim();

                        User recipient = userList.stream()
                                                .filter(user -> user.getName().equalsIgnoreCase(recipientInput))
                                                .findAny()
                                                .orElse(null);
                        if (recipient == null){
                            System.out.println("Invalid recipient.");
                            break;
                        }

                        if (recipient.getName().equalsIgnoreCase(currentUser.getName())){
                            System.out.println("You cannot send money to yourself");
                            break;
                        }

                        System.out.print("Enter amount to send: $");
                        String sendInput = scanner.nextLine().trim();

                        try{

                            BigDecimal sendAmount = new BigDecimal(sendInput);

                            currentUser.getSavingsAccount().withdraw(sendAmount);

                            recipient.getSavingsAccount().deposit(sendAmount);
                            System.out.println("Sent $"+ sendAmount + " to " + recipientInput);
                        } catch (NumberFormatException e){
                            System.out.println("Failed to send money: amount must be positive");
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

                        System.out.println("Enter fund to invest in: ");
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

                            

                            System.out.println("Successfully invested $" + investAmount + " into " + selectedFund + ".");
                        } catch (NumberFormatException e){
                            System.out.println("Invalid Amount. Please enter valid Amount.");
                        } catch (InvalidAmountException e){
                            System.out.println(e.getMessage());
 
                        }

                        
                        break;
                    case "6":
                        System.out.println("1. Transfer from savings to investment ");
                        System.out.println("2. Transfer from investment to savings ");
                        System.out.print("Enter your choice: ");

                        String direction = scanner.nextLine().trim();

                        if (!direction.equals("1") && !direction.equals("2")){
                            System.out.println("Invalid choice");
                            break;
                        }

                        System.out.print("Enter amount to transfer: $");
                        String tranferInput = scanner.nextLine().trim();

                        try{

                            BigDecimal tranferAmount = new BigDecimal(tranferInput);

                            if (direction.equals("1")){
                                currentUser.getSavingsAccount().withdraw(tranferAmount);
                                currentUser.getInvestmentAccount().deposit(tranferAmount);
                                System.out.println("Successfully transferred $" + tranferAmount + " in fund ");

                            } else if (direction.equals("2")){
                                currentUser.getInvestmentAccount().withdraw(tranferAmount);
                                currentUser.getSavingsAccount().deposit(tranferAmount);
                                System.out.println("Transfer to Investment Successful");
                            }
                        } catch (NumberFormatException e){
                            System.out.println("Invalid funds");
                        } catch (InvalidAmountException e){
                            System.out.println(e.getMessage());
                        }

                        
                        break;
                    case "7":
                        currentUser.getInvestmentAccount().withdrawAllInvestments();
                        System.out.println("All investments have been withdrawn and added to your investment account balance.");
                        break;
                    case "8":
                        System.out.println("You have been logged out.");
                        currentUser = null;
                        break;
                    case "9":
                        System.out.println("Thank you for using our banking app. Goodbye!");
                        isRunning = false;
                        break;
                
                    default:
                        System.out.println("Invalid choice. Please try again.");
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
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
        System.out.print("Enter your choice:");
=======
        System.out.print("Enter your choice: ");
        System.out.flush();
>>>>>>> 9978fe7 (Enhance user feedback in BankingService by adding flush calls for immediate output and improving error message clarity for user login attempts.)
=======
        System.out.print("Enter your choice: ");
        System.out.flush();
=======
        System.out.print("Enter your choice:");
>>>>>>> 83fa1f4 (Test cases corrections)
>>>>>>> 4d3be89 (Test cases corrections)
=======
        System.out.print("Enter your choice:");
>>>>>>> 7edc6e5 (Enhance user feedback in BankingService by adding flush calls for immediate output and improving error message clarity for user login attempts.)
    }
}