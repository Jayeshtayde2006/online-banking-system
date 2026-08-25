import java.math.BigDecimal;
import java.util.Scanner;

public class App {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n--- ONLINE BANKING SYSTEM ---");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine();

            if (choice.equals("1")) {
                System.out.print("Enter username: ");
                String username = scanner.nextLine().trim();

                System.out.print("Enter password: ");
                String password = scanner.nextLine();

                if (username.isEmpty()) {
                    System.out.println("Username cannot be empty.");
                } else if (password.length() < 8) {
                    System.out.println("Password must have at least 8 characters.");
                } else {
                    UserService.registerUser(username, password);
                }

            } else if (choice.equals("2")) {
                System.out.print("Enter username: ");
                String username = scanner.nextLine().trim();

                System.out.print("Enter password: ");
                String password = scanner.nextLine();

                if (UserService.loginUser(username, password)) {
                    showUserMenu(scanner, username);
                }

            } else if (choice.equals("3")) {
                System.out.println("Goodbye!");
                scanner.close();
                return;

            } else {
                System.out.println("Invalid option. Try again.");
            }
        }
    }

  private static void showUserMenu(Scanner scanner, String username) {
    while (true) {
        System.out.println("\n--- ACCOUNT MENU ---");
        System.out.println("1. Create bank account");
        System.out.println("2. View account details / balance");
        System.out.println("3. Deposit money");
        System.out.println("4. Withdraw money");
        System.out.println("5. Transaction history");
        System.out.println("6. Transfer money");
        System.out.println("7. Logout");
        System.out.print("Choose an option: ");

        String choice = scanner.nextLine();

        if (choice.equals("1")) {
            System.out.print("Enter account holder name: ");
            String holderName = scanner.nextLine().trim();

            System.out.print("Enter account type (Savings/Current): ");
            String accountType = scanner.nextLine().trim();

            if (holderName.isEmpty()) {
                System.out.println("Account holder name cannot be empty.");
            } else {
                AccountService.createAccount(
                        username,
                        holderName,
                        accountType
                );
            }

        } else if (choice.equals("2")) {
            AccountService.viewAccounts(username);

        } else if (choice.equals("3")) {
            System.out.print("Enter account number: ");
            String accountNumber = scanner.nextLine().trim();

            System.out.print("Enter deposit amount: ");
            String amountText = scanner.nextLine().trim();

            try {
                BigDecimal amount = new BigDecimal(amountText);
                TransactionService.deposit(username, accountNumber, amount);
            } catch (NumberFormatException e) {
                System.out.println("Enter a valid amount, for example: 500.00");
            }

        } else if (choice.equals("4")) {
            System.out.print("Enter account number: ");
            String accountNumber = scanner.nextLine().trim();

            System.out.print("Enter withdrawal amount: ");
            String amountText = scanner.nextLine().trim();

            try {
                BigDecimal amount = new BigDecimal(amountText);
                TransactionService.withdraw(username, accountNumber, amount);
            } catch (NumberFormatException e) {
                System.out.println("Enter a valid amount, for example: 200.00");
            }

        } else if (choice.equals("5")) {
            System.out.print("Enter account number: ");
            String accountNumber = scanner.nextLine().trim();

            HistoryService.viewHistory(username, accountNumber);

        } else if (choice.equals("6")) {
            System.out.print("Enter sender account number: ");
            String fromAccount = scanner.nextLine().trim();

            System.out.print("Enter receiver account number: ");
            String toAccount = scanner.nextLine().trim();

            System.out.print("Enter transfer amount: ");
            String amountText = scanner.nextLine().trim();

            try {
                BigDecimal amount = new BigDecimal(amountText);

                TransferService.transfer(
                        username,
                        fromAccount,
                        toAccount,
                        amount
                );

            } catch (NumberFormatException e) {
                System.out.println("Enter a valid amount, for example: 100.00");
            }

        } else if (choice.equals("7")) {
            System.out.println("Logged out successfully.");
            return;

        } else {
            System.out.println("Invalid option. Try again.");
        }
    }
  }
}
