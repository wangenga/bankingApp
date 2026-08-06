package service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

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

            String inputName = scanner.nextLine();

            if (currentUser == null){
                final String searchName = inputName;
                currentUser = userList.stream()
                                    .filter(user -> user.getName().equalsIgnoreCase(searchName.trim()))
                                    .findAny()
                                    .orElse(null);

                if (currentUser == null){
                    System.out.println("User not found");
                } else {
                    System.out.println("Welcome " + currentUser.getName() + " !");
                }
            } else {
                int choice = scanner.nextInt();

                switch (choice) {
                    case 1:
                        System.out.println("Savings: $" + currentUser.getSavingsAccount().getBalance());
                        break;

                    case 2:
                        
                        break;
                    case 3:
                        
                        break;
                    case 4:
                        
                        break;
                    case 5:
                        
                        break;
                    case 6:
                        
                        break;
                    case 7:
                        
                        break;
                    case 8:
                        currentUser = null;
                        break;
                    case 9:
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