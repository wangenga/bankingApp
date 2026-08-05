import java.util.Scanner;

import service.BankingService;

public class BankingApp {
    public static void main (String[] args) {
        Scanner scanner = new Scanner(System.in);
        BankingService service = new BankingService(scanner);
        
        service.start();  
    }
}
