import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountService {

    public static boolean createAccount(
            String username,
            String holderName,
            String accountType) {

        if (!accountType.equalsIgnoreCase("Savings")
                && !accountType.equalsIgnoreCase("Current")) {
            System.out.println("Account type must be Savings or Current.");
            return false;
        }

        String accountNumber = "AC"
                + (1000000000L
                + new SecureRandom().nextInt(900000000));

        String sql = """
                INSERT INTO accounts
                (account_number, user_id, holder_name, account_type, balance)
                SELECT ?, user_id, ?, ?, 0.00
                FROM users
                WHERE username = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, accountNumber);
            statement.setString(2, holderName);
            statement.setString(3, accountType);
            statement.setString(4, username);

            int rows = statement.executeUpdate();

            if (rows > 0) {
                System.out.println("Account created successfully!");
                System.out.println("Account number: " + accountNumber);
                return true;
            }

            System.out.println("Could not find the user.");
            return false;

        } catch (SQLException e) {
            System.out.println("Could not create account: " + e.getMessage());
            return false;
        }
    }

    public static void viewAccounts(String username) {
        String sql = """
                SELECT account_number, holder_name, account_type, balance
                FROM accounts
                WHERE user_id = (
                    SELECT user_id FROM users WHERE username = ?
                )
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, username);

            try (ResultSet result = statement.executeQuery()) {
                boolean found = false;

                System.out.println("\n--- YOUR ACCOUNTS ---");

                while (result.next()) {
                    found = true;
                    System.out.println("Account number: "
                            + result.getString("account_number"));
                    System.out.println("Holder name: "
                            + result.getString("holder_name"));
                    System.out.println("Account type: "
                            + result.getString("account_type"));
                    System.out.println("Balance: ₹"
                            + result.getBigDecimal("balance"));
                    System.out.println("----------------------");
                }

                if (!found) {
                    System.out.println("No accounts found.");
                }
            }

        } catch (SQLException e) {
            System.out.println("Could not load accounts: " + e.getMessage());
        }
    }
}