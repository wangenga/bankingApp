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

            if (!scanner.hasNextLine()){
                isRunning = false;
                continue;
            }
            

            if (currentUser == null){
                //System.out.println("Welcome to The Banking App!");
                //System.out.println("\n");
                System.out.println("Enter your name to login: ");
                String inputName = scanner.nextLine();

                currentUser = userList.stream()
                                    .filter(user -> user.getName().equalsIgnoreCase(inputName.trim()))
                                    .findAny()
                                    .orElse(null);
                if (currentUser == null){
                    System.out.println("User not found");
                }

            }
            else{
                printMenu();
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
    }

     
}