import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class HistoryService {

    public static void viewHistory(
            String username,
            String accountNumber) {

        String sql = """
                SELECT t.transaction_type,
                       t.amount,
                       t.description,
                       t.transaction_date
                FROM transactions t
                JOIN accounts a ON t.account_id = a.account_id
                JOIN users u ON a.user_id = u.user_id
                WHERE a.account_number = ?
                AND u.username = ?
                ORDER BY t.transaction_date DESC
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, accountNumber);
            statement.setString(2, username);

            try (ResultSet result = statement.executeQuery()) {
                boolean found = false;

                System.out.println("\n--- TRANSACTION HISTORY ---");

                while (result.next()) {
                    found = true;

                    System.out.println("Type: "
                            + result.getString("transaction_type"));
                    System.out.println("Amount: "
                            + result.getBigDecimal("amount"));
                    System.out.println("Description: "
                            + result.getString("description"));
                    System.out.println("Date: "
                            + result.getTimestamp("transaction_date"));
                    System.out.println("---------------------------");
                }

                if (!found) {
                    System.out.println("No transactions found.");
                }
            }

        } catch (SQLException e) {
            System.out.println("Could not load transaction history: "
                    + e.getMessage());
        }
    }
}