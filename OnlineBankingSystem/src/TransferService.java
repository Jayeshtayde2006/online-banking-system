import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TransferService {

    public static boolean transfer(
            String username,
            String fromAccount,
            String toAccount,
            BigDecimal amount) {

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("Transfer amount must be greater than zero.");
            return false;
        }

        if (fromAccount.equals(toAccount)) {
            System.out.println("Sender and receiver accounts cannot be the same.");
            return false;
        }

        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);

            try {
                Integer senderId = getUserAccountId(
                        connection, username, fromAccount
                );

                Integer receiverId = getAccountId(
                        connection, toAccount
                );

                if (senderId == null) {
                    System.out.println("Sender account not found.");
                    connection.rollback();
                    return false;
                }

                if (receiverId == null) {
                    System.out.println("Receiver account not found.");
                    connection.rollback();
                    return false;
                }

                BigDecimal balance = getBalance(connection, senderId);

                if (balance.compareTo(amount) < 0) {
                    System.out.println("Insufficient balance.");
                    connection.rollback();
                    return false;
                }

                updateBalance(connection, senderId, amount, false);
                updateBalance(connection, receiverId, amount, true);

                saveTransaction(
                        connection, senderId, "TRANSFER_OUT",
                        amount, toAccount, "Money transferred"
                );

                saveTransaction(
                        connection, receiverId, "TRANSFER_IN",
                        amount, fromAccount, "Money received"
                );

                connection.commit();
                System.out.println("Transfer successful!");
                return true;

            } catch (SQLException e) {
                connection.rollback();
                System.out.println("Transfer failed: " + e.getMessage());
                return false;

            } finally {
                connection.setAutoCommit(true);
            }

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
            return false;
        }
    }

    private static Integer getUserAccountId(
            Connection connection,
            String username,
            String accountNumber) throws SQLException {

        String sql = """
                SELECT account_id FROM accounts
                WHERE account_number = ?
                AND user_id = (
                    SELECT user_id FROM users WHERE username = ?
                )
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, accountNumber);
            statement.setString(2, username);

            try (ResultSet result = statement.executeQuery()) {
                return result.next()
                        ? result.getInt("account_id")
                        : null;
            }
        }
    }

    private static Integer getAccountId(
            Connection connection,
            String accountNumber) throws SQLException {

        String sql = "SELECT account_id FROM accounts WHERE account_number = ?";

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, accountNumber);

            try (ResultSet result = statement.executeQuery()) {
                return result.next()
                        ? result.getInt("account_id")
                        : null;
            }
        }
    }

    private static BigDecimal getBalance(
            Connection connection,
            int accountId) throws SQLException {

        String sql = "SELECT balance FROM accounts WHERE account_id = ?";

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, accountId);

            try (ResultSet result = statement.executeQuery()) {
                return result.next()
                        ? result.getBigDecimal("balance")
                        : BigDecimal.ZERO;
            }
        }
    }

    private static void updateBalance(
            Connection connection,
            int accountId,
            BigDecimal amount,
            boolean add) throws SQLException {

        String sql = add
                ? "UPDATE accounts SET balance = balance + ? WHERE account_id = ?"
                : "UPDATE accounts SET balance = balance - ? WHERE account_id = ?";

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setBigDecimal(1, amount);
            statement.setInt(2, accountId);
            statement.executeUpdate();
        }
    }

    private static void saveTransaction(
            Connection connection,
            int accountId,
            String type,
            BigDecimal amount,
            String relatedAccount,
            String description) throws SQLException {

        String sql = """
                INSERT INTO transactions
                (account_id, transaction_type, amount,
                 related_account_number, description)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, accountId);
            statement.setString(2, type);
            statement.setBigDecimal(3, amount);
            statement.setString(4, relatedAccount);
            statement.setString(5, description);
            statement.executeUpdate();
        }
    }
}